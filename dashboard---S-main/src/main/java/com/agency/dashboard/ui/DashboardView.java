package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.CreativeRequest;
import com.agency.dashboard.domain.RequestStatus;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.service.CurrentUserService;
import com.agency.dashboard.service.MetricsService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;
import java.util.Map;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Creative Ops")
public class DashboardView extends VerticalLayout implements BeforeEnterObserver {

    private final MetricsService metrics;
    private final ClientRepository clients;
    private final CurrentUserService currentUserService;

    private final ComboBox<Client> clientFilter = new ComboBox<>("Cliente");
    private final ComboBox<RequestStatus> statusFilter = new ComboBox<>("Status");
    private final DatePicker monthPicker = new DatePicker("Mês");

    private final Grid<Map.Entry<String, Long>> byClientGrid = new Grid<>();
    private final Grid<CreativeRequest> recentGrid = new Grid<>();

    private final Span kpiRequested = new Span();
    private final Span kpiDelivered = new Span();
    private final Span kpiOpen = new Span();
    private final Span kpiLead = new Span();

    public DashboardView(MetricsService metrics,
                         ClientRepository clients,
                         CurrentUserService currentUserService) {
        this.metrics = metrics;
        this.clients = clients;
        this.currentUserService = currentUserService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Visão geral"));

        monthPicker.setValue(LocalDate.now().withDayOfMonth(1));
        monthPicker.setHelperText("Selecione qualquer dia do mês (usamos o mês inteiro).");

        clientFilter.setItems(clients.findAll());
        clientFilter.setItemLabelGenerator(Client::getName);
        clientFilter.setClearButtonVisible(true);

        statusFilter.setItems(RequestStatus.values());
        statusFilter.setClearButtonVisible(true);

        HorizontalLayout filters = new HorizontalLayout(monthPicker, clientFilter, statusFilter);
        filters.setWidthFull();
        filters.getStyle().set("gap", "12px");
        add(filters);

        add(kpiRow());
        add(gridsRow());

        monthPicker.addValueChangeListener(e -> refresh());
        clientFilter.addValueChangeListener(e -> refresh());
        statusFilter.addValueChangeListener(e -> refresh());

        configureGrids();
        refresh();
    }

    private Component kpiRow() {
        HorizontalLayout row = new HorizontalLayout(
                kpiCard("Requisições no mês", kpiRequested),
                kpiCard("Entregues no mês", kpiDelivered),
                kpiCard("Pendentes (criadas no mês)", kpiOpen),
                kpiCard("Lead time médio (h)", kpiLead)
        );
        row.setWidthFull();
        row.getStyle().set("gap", "12px");
        return row;
    }

    private Component kpiCard(String label, Span value) {
        value.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "700");

        Span caption = new Span(label);
        caption.getStyle().set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout card = new VerticalLayout(value, caption);
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidth("260px");
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "14px")
                .set("box-shadow", "var(--lumo-box-shadow-s)");
        return card;
    }

    private Component gridsRow() {
        byClientGrid.setHeight("360px");
        recentGrid.setHeight("360px");

        VerticalLayout left = new VerticalLayout(new H2("Requisições por cliente (mês)"), byClientGrid);
        left.setPadding(false);
        left.setSpacing(false);
        left.setSizeFull();

        VerticalLayout right = new VerticalLayout(new H2("Pedidos recentes (filtráveis)"), recentGrid);
        right.setPadding(false);
        right.setSpacing(false);
        right.setSizeFull();

        HorizontalLayout row = new HorizontalLayout(left, right);
        row.setSizeFull();
        row.setFlexGrow(1, left, right);
        row.getStyle().set("gap", "12px");
        return row;
    }

    private void configureGrids() {
        byClientGrid.addColumn(Map.Entry::getKey)
                .setHeader("Cliente")
                .setAutoWidth(true)
                .setFlexGrow(1);

        byClientGrid.addColumn(Map.Entry::getValue)
                .setHeader("Qtd. no mês")
                .setAutoWidth(true);

        recentGrid.addColumn(r -> r.getClient().getName()).setHeader("Cliente").setAutoWidth(true);
        recentGrid.addColumn(r -> r.getType().name()).setHeader("Tipo").setAutoWidth(true);
        recentGrid.addColumn(r -> r.getStatus().name()).setHeader("Status").setAutoWidth(true);
        recentGrid.addColumn(r -> r.getTitle() == null ? "-" : r.getTitle()).setHeader("Título").setFlexGrow(1);
        recentGrid.addColumn(r -> r.getCreatedAt().toLocalDate()).setHeader("Criado em").setAutoWidth(true);
    }

    private void refresh() {
        LocalDate base = monthPicker.getValue() == null ? LocalDate.now() : monthPicker.getValue();
        LocalDate month = base.withDayOfMonth(1);

        long requested = metrics.totalRequested(month);
        long delivered = metrics.totalDelivered(month);
        long open = metrics.totalOpen(month);
        double lead = metrics.avgLeadTimeHours(month);

        kpiRequested.setText(String.valueOf(requested));
        kpiDelivered.setText(String.valueOf(delivered));
        kpiOpen.setText(String.valueOf(open));
        kpiLead.setText(String.valueOf(lead));

        var byClient = metrics.requestsByClient(month).entrySet().stream().toList();
        byClientGrid.setItems(byClient);

        recentGrid.setItems(metrics.filter(month, clientFilter.getValue(), statusFilter.getValue()));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!currentUserService.isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }
    }
}