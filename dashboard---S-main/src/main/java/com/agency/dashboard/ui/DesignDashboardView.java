package com.agency.dashboard.ui;

import com.agency.dashboard.domain.TrafficAdStatus;
import com.agency.dashboard.domain.TrafficAdTask;
import com.agency.dashboard.domain.User;
import com.agency.dashboard.repo.TrafficAdTaskRepository;
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

@Route(value = "design-dashboard", layout = MainLayout.class)
@PageTitle("Dashboard do Design | Creative Ops")
public class DesignDashboardView extends SecureView {

    private final TrafficAdTaskRepository trafficAdTaskRepository;

    private final Span totalCards = new Span();
    private final Span pendingCards = new Span();
    private final Span approvalCards = new Span();
    private final Span doneCards = new Span();

    private final Grid<Map.Entry<String, Long>> rankingGrid = new Grid<>();
    private final Grid<TrafficAdTask> recentGrid = new Grid<>(TrafficAdTask.class, false);

    public DesignDashboardView(
            TrafficAdTaskRepository trafficAdTaskRepository,
            CurrentUserService currentUserService
    ) {
        super(currentUserService);
        this.trafficAdTaskRepository = trafficAdTaskRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Dashboard do Design"));

        add(buildKpiRow());
        add(buildContentRow());

        configureRankingGrid();
        configureRecentGrid();
        refresh();
    }

    @Override
    protected boolean hasAccess(User user) {
        return AccessControl.canAccessDesign(user);
    }

    private Component buildKpiRow() {
        HorizontalLayout row = new HorizontalLayout(
                createKpiCard("Demandas totais", totalCards),
                createKpiCard("Pendentes", pendingCards),
                createKpiCard("Aguardando aprovação", approvalCards),
                createKpiCard("Concluídas", doneCards)
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
        card.setWidth("260px");
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "14px")
                .set("box-shadow", "var(--lumo-box-shadow-s)");

        return card;
    }

    private Component buildContentRow() {
        rankingGrid.setHeight("420px");
        recentGrid.setHeight("420px");

        VerticalLayout left = new VerticalLayout(new H2("Clientes com mais demandas"), rankingGrid);
        left.setPadding(false);
        left.setSpacing(false);
        left.setSizeFull();

        VerticalLayout right = new VerticalLayout(new H2("Cards recentes do Design"), recentGrid);
        right.setPadding(false);
        right.setSpacing(false);
        right.setSizeFull();

        HorizontalLayout row = new HorizontalLayout(left, right);
        row.setSizeFull();
        row.setFlexGrow(1, left, right);
        row.setSpacing(true);

        return row;
    }

    private void configureRankingGrid() {
        rankingGrid.addColumn(Map.Entry::getKey)
                .setHeader("Cliente")
                .setAutoWidth(true)
                .setFlexGrow(1);

        rankingGrid.addColumn(Map.Entry::getValue)
                .setHeader("Qtd. demandas")
                .setAutoWidth(true);
    }

    private void configureRecentGrid() {
        recentGrid.addColumn(task -> task.getClient() != null ? task.getClient().getName() : "—")
                .setHeader("Cliente")
                .setAutoWidth(true);

        recentGrid.addColumn(TrafficAdTask::getTitle)
                .setHeader("Título")
                .setFlexGrow(1);

        recentGrid.addColumn(task -> task.getStatus() != null ? task.getStatus().getLabel() : "—")
                .setHeader("Status")
                .setAutoWidth(true);

        recentGrid.addColumn(task -> valueOrDash(task.getResponsible()))
                .setHeader("Responsável")
                .setAutoWidth(true);

        recentGrid.addColumn(task -> task.getDueDate() != null ? task.getDueDate().toString() : "—")
                .setHeader("Prazo")
                .setAutoWidth(true);
    }

    private void refresh() {
        List<TrafficAdTask> allTasks = trafficAdTaskRepository.findAllByOrderByCreatedAtDesc();

        long total = allTasks.size();
        long pending = allTasks.stream()
                .filter(task -> task.getStatus() == TrafficAdStatus.PENDENTE)
                .count();
        long approval = allTasks.stream()
                .filter(task -> task.getStatus() == TrafficAdStatus.ESPERANDO_APROVACAO_CRIATIVO)
                .count();
        long done = allTasks.stream()
                .filter(task -> task.getStatus() == TrafficAdStatus.CONCLUIDO)
                .count();

        totalCards.setText(String.valueOf(total));
        pendingCards.setText(String.valueOf(pending));
        approvalCards.setText(String.valueOf(approval));
        doneCards.setText(String.valueOf(done));

        List<Map.Entry<String, Long>> ranking = allTasks.stream()
                .filter(task -> task.getClient() != null)
                .collect(Collectors.groupingBy(
                        task -> task.getClient().getName(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .toList();

        rankingGrid.setItems(ranking);
        recentGrid.setItems(allTasks.stream().limit(15).toList());
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}