package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Lead;
import com.agency.dashboard.domain.LeadStatus;
import com.agency.dashboard.domain.User;
import com.agency.dashboard.repo.LeadRepository;
import com.agency.dashboard.security.AccessControl;
import com.agency.dashboard.security.SecureView;
import com.agency.dashboard.service.CurrentUserService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route(value = "commercial-dashboard", layout = MainLayout.class)
@PageTitle("Dashboard Comercial | Creative Ops")
public class CommercialDashboardView extends SecureView {

    private final LeadRepository leadRepository;

    private final Span totalLeads = new Span();
    private final Span diagnosticLeads = new Span();
    private final Span proposalLeads = new Span();
    private final Span closedLeads = new Span();
    private final Span lostLeads = new Span();

    private final Grid<Map.Entry<String, Long>> sourceGrid = new Grid<>();
    private final Grid<Lead> recentLeadsGrid = new Grid<>(Lead.class, false);

    public CommercialDashboardView(
            LeadRepository leadRepository,
            CurrentUserService currentUserService
    ) {
        super(currentUserService);
        this.leadRepository = leadRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Dashboard Comercial"));

        add(buildKpiRow());
        add(buildContentRow());

        configureSourceGrid();
        configureRecentGrid();
        refresh();
    }

    @Override
    protected boolean hasAccess(User user) {
        return AccessControl.canAccessCommercial(user);
    }

    private Component buildKpiRow() {
        HorizontalLayout row = new HorizontalLayout(
                createKpiCard("Total de leads", totalLeads),
                createKpiCard("Em diagnóstico", diagnosticLeads),
                createKpiCard("Propostas", proposalLeads),
                createKpiCard("Fechados", closedLeads),
                createKpiCard("Perdidos", lostLeads)
        );
        row.setWidthFull();
        row.setSpacing(true);
        return row;
    }

    private Component createKpiCard(String label, Span value) {
        value.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "700");

        Span caption = new Span(label);
        caption.getStyle().set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout card = new VerticalLayout(value, caption);
        card.setSpacing(false);
        card.setPadding(true);
        card.setWidth("220px");
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "14px")
                .set("box-shadow", "var(--lumo-box-shadow-s)");

        return card;
    }

    private Component buildContentRow() {
        sourceGrid.setHeight("420px");
        recentLeadsGrid.setHeight("420px");

        VerticalLayout left = new VerticalLayout(new H2("Origem dos leads"), sourceGrid);
        left.setPadding(false);
        left.setSpacing(false);
        left.setSizeFull();

        VerticalLayout right = new VerticalLayout(new H2("Leads recentes"), recentLeadsGrid);
        right.setPadding(false);
        right.setSpacing(false);
        right.setSizeFull();

        HorizontalLayout row = new HorizontalLayout(left, right);
        row.setSizeFull();
        row.setFlexGrow(1, left, right);
        row.setSpacing(true);

        return row;
    }

    private void configureSourceGrid() {
        sourceGrid.addColumn(Map.Entry::getKey)
                .setHeader("Origem")
                .setAutoWidth(true)
                .setFlexGrow(1);

        sourceGrid.addColumn(Map.Entry::getValue)
                .setHeader("Qtd. leads")
                .setAutoWidth(true);
    }

    private void configureRecentGrid() {
        recentLeadsGrid.addColumn(Lead::getName)
                .setHeader("Nome")
                .setAutoWidth(true);

        recentLeadsGrid.addColumn(lead -> valueOrDash(lead.getCompany()))
                .setHeader("Empresa")
                .setAutoWidth(true);

        recentLeadsGrid.addColumn(lead -> lead.getStatus() != null ? lead.getStatus().name() : "—")
                .setHeader("Status")
                .setAutoWidth(true);

        recentLeadsGrid.addColumn(lead -> valueOrDash(lead.getResponsible()))
                .setHeader("Responsável")
                .setAutoWidth(true);

        recentLeadsGrid.addColumn(lead -> valueOrDash(lead.getSource()))
                .setHeader("Origem")
                .setAutoWidth(true);
    }

    private void refresh() {
        List<Lead> leads = leadRepository.findAll().stream()
                .sorted(Comparator.comparing(Lead::getCreatedAt).reversed())
                .toList();

        totalLeads.setText(String.valueOf(leads.size()));
        diagnosticLeads.setText(String.valueOf(
                leads.stream().filter(lead -> lead.getStatus() == LeadStatus.DIAGNOSTICO).count()
        ));
        proposalLeads.setText(String.valueOf(
                leads.stream().filter(lead -> lead.getStatus() == LeadStatus.PROPOSTA).count()
        ));
        closedLeads.setText(String.valueOf(
                leads.stream().filter(lead -> lead.getStatus() == LeadStatus.FECHADO).count()
        ));
        lostLeads.setText(String.valueOf(
                leads.stream().filter(lead -> lead.getStatus() == LeadStatus.PERDIDO).count()
        ));

        List<Map.Entry<String, Long>> sources = leads.stream()
                .collect(Collectors.groupingBy(
                        lead -> {
                            String source = lead.getSource();
                            return (source == null || source.isBlank()) ? "Não informado" : source;
                        },
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .toList();

        sourceGrid.setItems(sources);
        recentLeadsGrid.setItems(leads.stream().limit(15).toList());
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}