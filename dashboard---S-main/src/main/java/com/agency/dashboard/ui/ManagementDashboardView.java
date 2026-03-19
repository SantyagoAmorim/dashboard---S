package com.agency.dashboard.ui;

import com.agency.dashboard.domain.AppNotification;
import com.agency.dashboard.domain.Lead;
import com.agency.dashboard.domain.LeadStatus;
import com.agency.dashboard.domain.OnboardingTask;
import com.agency.dashboard.domain.OnboardingTaskStatus;
import com.agency.dashboard.domain.TrafficAdStatus;
import com.agency.dashboard.domain.TrafficAdTask;
import com.agency.dashboard.repo.AppNotificationRepository;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.LeadRepository;
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

@Route(value = "management-dashboard", layout = MainLayout.class)
@PageTitle("Dashboard da Gestão | Creative Ops")
public class ManagementDashboardView extends VerticalLayout {

    private final ClientRepository clientRepository;
    private final LeadRepository leadRepository;
    private final OnboardingTaskRepository onboardingTaskRepository;
    private final TrafficAdTaskRepository trafficAdTaskRepository;
    private final AppNotificationRepository appNotificationRepository;

    private final Span totalClients = new Span();
    private final Span totalLeads = new Span();
    private final Span totalOnboardings = new Span();
    private final Span totalAds = new Span();
    private final Span closedLeads = new Span();
    private final Span pendingOnboardings = new Span();
    private final Span adsInProgress = new Span();
    private final Span unreadNotifications = new Span();

    private final Grid<Lead> recentLeadsGrid = new Grid<>(Lead.class, false);
    private final Grid<TrafficAdTask> recentAdsGrid = new Grid<>(TrafficAdTask.class, false);
    private final Grid<AppNotification> recentNotificationsGrid = new Grid<>(AppNotification.class, false);

    public ManagementDashboardView(
            ClientRepository clientRepository,
            LeadRepository leadRepository,
            OnboardingTaskRepository onboardingTaskRepository,
            TrafficAdTaskRepository trafficAdTaskRepository,
            AppNotificationRepository appNotificationRepository
    ) {
        this.clientRepository = clientRepository;
        this.leadRepository = leadRepository;
        this.onboardingTaskRepository = onboardingTaskRepository;
        this.trafficAdTaskRepository = trafficAdTaskRepository;
        this.appNotificationRepository = appNotificationRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Dashboard da Gestão"));

        add(buildKpiRow1());
        add(buildKpiRow2());
        add(buildContentRow());

        configureRecentLeadsGrid();
        configureRecentAdsGrid();
        configureRecentNotificationsGrid();

        refresh();
    }

    private Component buildKpiRow1() {
        HorizontalLayout row = new HorizontalLayout(
                createKpiCard("Clientes", totalClients),
                createKpiCard("Leads", totalLeads),
                createKpiCard("Onboardings", totalOnboardings),
                createKpiCard("Cards de anúncios", totalAds)
        );
        row.setWidthFull();
        row.setSpacing(true);
        return row;
    }

    private Component buildKpiRow2() {
        HorizontalLayout row = new HorizontalLayout(
                createKpiCard("Leads fechados", closedLeads),
                createKpiCard("Onboardings pendentes", pendingOnboardings),
                createKpiCard("Anúncios em andamento", adsInProgress),
                createKpiCard("Notificações não lidas", unreadNotifications)
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
        recentLeadsGrid.setHeight("320px");
        recentAdsGrid.setHeight("320px");
        recentNotificationsGrid.setHeight("320px");

        VerticalLayout col1 = new VerticalLayout(new H2("Leads recentes"), recentLeadsGrid);
        col1.setPadding(false);
        col1.setSpacing(false);
        col1.setSizeFull();

        VerticalLayout col2 = new VerticalLayout(new H2("Anúncios recentes"), recentAdsGrid);
        col2.setPadding(false);
        col2.setSpacing(false);
        col2.setSizeFull();

        VerticalLayout col3 = new VerticalLayout(new H2("Notificações recentes"), recentNotificationsGrid);
        col3.setPadding(false);
        col3.setSpacing(false);
        col3.setSizeFull();

        HorizontalLayout row = new HorizontalLayout(col1, col2, col3);
        row.setSizeFull();
        row.setFlexGrow(1, col1, col2, col3);
        row.setSpacing(true);

        return row;
    }

    private void configureRecentLeadsGrid() {
        recentLeadsGrid.addColumn(Lead::getName).setHeader("Nome").setAutoWidth(true);
        recentLeadsGrid.addColumn(lead -> valueOrDash(lead.getCompany())).setHeader("Empresa").setAutoWidth(true);
        recentLeadsGrid.addColumn(lead -> lead.getStatus() != null ? lead.getStatus().getLabel() : "—")
                .setHeader("Status").setAutoWidth(true);
    }

    private void configureRecentAdsGrid() {
        recentAdsGrid.addColumn(task -> task.getClient() != null ? task.getClient().getName() : "—")
                .setHeader("Cliente").setAutoWidth(true);
        recentAdsGrid.addColumn(TrafficAdTask::getTitle).setHeader("Título").setFlexGrow(1);
        recentAdsGrid.addColumn(task -> task.getStatus() != null ? task.getStatus().getLabel() : "—")
                .setHeader("Status").setAutoWidth(true);
    }

    private void configureRecentNotificationsGrid() {
        recentNotificationsGrid.addColumn(AppNotification::getTitle).setHeader("Título").setAutoWidth(true);
        recentNotificationsGrid.addColumn(notification -> valueOrDash(notification.getTargetSector()))
                .setHeader("Setor").setAutoWidth(true);
        recentNotificationsGrid.addColumn(notification -> notification.isRead() ? "Lida" : "Não lida")
                .setHeader("Status").setAutoWidth(true);
    }

    private void refresh() {
        List<Lead> leads = leadRepository.findAll().stream()
                .sorted(Comparator.comparing(Lead::getCreatedAt).reversed())
                .toList();

        List<OnboardingTask> onboardingTasks = onboardingTaskRepository.findAllByOrderByCreatedAtDesc();
        List<TrafficAdTask> adTasks = trafficAdTaskRepository.findAllByOrderByCreatedAtDesc();
        List<AppNotification> notifications = appNotificationRepository.findAllByOrderByCreatedAtDesc();

        totalClients.setText(String.valueOf(clientRepository.count()));
        totalLeads.setText(String.valueOf(leads.size()));
        totalOnboardings.setText(String.valueOf(onboardingTasks.size()));
        totalAds.setText(String.valueOf(adTasks.size()));

        closedLeads.setText(String.valueOf(
                leads.stream().filter(lead -> lead.getStatus() == LeadStatus.FECHADO).count()
        ));

        pendingOnboardings.setText(String.valueOf(
                onboardingTasks.stream().filter(task -> task.getStatus() == OnboardingTaskStatus.PENDING).count()
        ));

        adsInProgress.setText(String.valueOf(
                adTasks.stream().filter(task -> task.getStatus() == TrafficAdStatus.EM_ANDAMENTO).count()
        ));

        unreadNotifications.setText(String.valueOf(
                notifications.stream().filter(notification -> !notification.isRead()).count()
        ));

        recentLeadsGrid.setItems(leads.stream().limit(10).toList());
        recentAdsGrid.setItems(adTasks.stream().limit(10).toList());
        recentNotificationsGrid.setItems(notifications.stream().limit(10).toList());
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}