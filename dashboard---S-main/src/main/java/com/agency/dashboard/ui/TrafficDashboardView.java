package com.agency.dashboard.ui;

import com.agency.dashboard.domain.OnboardingTask;
import com.agency.dashboard.domain.OnboardingTaskStatus;
import com.agency.dashboard.domain.TrafficAdStatus;
import com.agency.dashboard.domain.TrafficAdTask;
import com.agency.dashboard.repo.OnboardingTaskRepository;
import com.agency.dashboard.repo.TrafficAdTaskRepository;
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

@Route(value = "traffic-dashboard", layout = MainLayout.class)
@PageTitle("Dashboard do Tráfego | Creative Ops")
public class TrafficDashboardView extends VerticalLayout {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final TrafficAdTaskRepository trafficAdTaskRepository;

    private final Span totalOnboardingTasks = new Span();
    private final Span pendingOnboardingTasks = new Span();
    private final Span adsInProgress = new Span();
    private final Span adsDone = new Span();

    private final Grid<Map.Entry<String, Long>> rankingGrid = new Grid<>();
    private final Grid<TrafficAdTask> recentAdsGrid = new Grid<>(TrafficAdTask.class, false);

    public TrafficDashboardView(
            OnboardingTaskRepository onboardingTaskRepository,
            TrafficAdTaskRepository trafficAdTaskRepository
    ) {
        this.onboardingTaskRepository = onboardingTaskRepository;
        this.trafficAdTaskRepository = trafficAdTaskRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Dashboard do Tráfego"));

        add(buildKpiRow());
        add(buildContentRow());

        configureRankingGrid();
        configureRecentAdsGrid();
        refresh();
    }

    private Component buildKpiRow() {
        HorizontalLayout row = new HorizontalLayout(
                createKpiCard("Tarefas de onboarding", totalOnboardingTasks),
                createKpiCard("Onboardings pendentes", pendingOnboardingTasks),
                createKpiCard("Anúncios em andamento", adsInProgress),
                createKpiCard("Anúncios concluídos", adsDone)
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
        recentAdsGrid.setHeight("420px");

        VerticalLayout left = new VerticalLayout(new H2("Clientes com mais demandas"), rankingGrid);
        left.setPadding(false);
        left.setSpacing(false);
        left.setSizeFull();

        VerticalLayout right = new VerticalLayout(new H2("Anúncios recentes"), recentAdsGrid);
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

    private void configureRecentAdsGrid() {
        recentAdsGrid.addColumn(task -> task.getClient() != null ? task.getClient().getName() : "—")
                .setHeader("Cliente")
                .setAutoWidth(true);

        recentAdsGrid.addColumn(TrafficAdTask::getTitle)
                .setHeader("Título")
                .setFlexGrow(1);

        recentAdsGrid.addColumn(task -> task.getStatus() != null ? task.getStatus().getLabel() : "—")
                .setHeader("Status")
                .setAutoWidth(true);

        recentAdsGrid.addColumn(task -> valueOrDash(task.getResponsible()))
                .setHeader("Responsável")
                .setAutoWidth(true);

        recentAdsGrid.addColumn(task -> task.getDueDate() != null ? task.getDueDate().toString() : "—")
                .setHeader("Prazo")
                .setAutoWidth(true);
    }

    private void refresh() {
        List<OnboardingTask> onboardingTasks = onboardingTaskRepository.findAllByOrderByCreatedAtDesc();
        List<TrafficAdTask> adTasks = trafficAdTaskRepository.findAllByOrderByCreatedAtDesc();

        long totalOnboarding = onboardingTasks.size();
        long pendingOnboarding = onboardingTasks.stream()
                .filter(task -> task.getStatus() == OnboardingTaskStatus.PENDING)
                .count();

        long inProgressAds = adTasks.stream()
                .filter(task -> task.getStatus() == TrafficAdStatus.EM_ANDAMENTO)
                .count();

        long doneAdsCount = adTasks.stream()
                .filter(task -> task.getStatus() == TrafficAdStatus.CONCLUIDO)
                .count();

        totalOnboardingTasks.setText(String.valueOf(totalOnboarding));
        pendingOnboardingTasks.setText(String.valueOf(pendingOnboarding));
        adsInProgress.setText(String.valueOf(inProgressAds));
        adsDone.setText(String.valueOf(doneAdsCount));

        List<Map.Entry<String, Long>> ranking = adTasks.stream()
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
        recentAdsGrid.setItems(adTasks.stream().limit(15).toList());
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}