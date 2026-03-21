package com.agency.dashboard.ui;

import com.agency.dashboard.domain.AppNotification;
import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.Lead;
import com.agency.dashboard.domain.TrafficAd;
import com.agency.dashboard.repo.AppNotificationRepository;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.LeadRepository;
import com.agency.dashboard.repo.TrafficAdRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;

@Route(value = "dashboard-old", layout = MainLayout.class)
@PageTitle("Dashboard Geral | Creative Ops")
public class DashboardView extends VerticalLayout {

    private final ClientRepository clientRepository;
    private final LeadRepository leadRepository;
    private final TrafficAdRepository trafficAdRepository;
    private final AppNotificationRepository appNotificationRepository;

    private final Span totalClients = new Span();
    private final Span totalLeads = new Span();
    private final Span totalAds = new Span();
    private final Span unreadNotifications = new Span();

    private final Grid<Lead> recentLeadsGrid = new Grid<>(Lead.class, false);
    private final Grid<TrafficAd> recentAdsGrid = new Grid<>(TrafficAd.class, false);
    private final Grid<AppNotification> recentNotificationsGrid = new Grid<>(AppNotification.class, false);

    public DashboardView(
            ClientRepository clientRepository,
            LeadRepository leadRepository,
            TrafficAdRepository trafficAdRepository,
            AppNotificationRepository appNotificationRepository
    ) {
        this.clientRepository = clientRepository;
        this.leadRepository = leadRepository;
        this.trafficAdRepository = trafficAdRepository;
        this.appNotificationRepository = appNotificationRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(buildHeader());
        add(buildKpiRow());
        add(buildContentRow());

        configureRecentLeadsGrid();
        configureRecentAdsGrid();
        configureRecentNotificationsGrid();

        refresh();
    }

    private Component buildHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setPadding(false);
        header.setSpacing(false);

        H2 title = new H2("Dashboard Geral");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "2rem");

        Span subtitle = new Span("Visão rápida da operação da agência");
        subtitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "0.95rem");

        header.add(title, subtitle);
        return header;
    }

    private Component buildKpiRow() {
        HorizontalLayout row = new HorizontalLayout(
                createKpiCard("Clientes", totalClients),
                createKpiCard("Leads", totalLeads),
                createKpiCard("Anúncios", totalAds),
                createKpiCard("Notificações não lidas", unreadNotifications)
        );
        row.setWidthFull();
        row.setSpacing(true);
        return row;
    }

    private Component createKpiCard(String label, Span value) {
        value.getStyle()
                .set("font-size", "2rem")
                .set("font-weight", "800");

        Span caption = new Span(label);
        caption.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "0.9rem");

        VerticalLayout card = new VerticalLayout(value, caption);
        card.setPadding(true);
        card.setSpacing(false);
        card.setWidthFull();
        card.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "16px")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("background", "var(--lumo-base-color)");

        return card;
    }

    private Component buildContentRow() {
        recentLeadsGrid.setHeight("320px");
        recentAdsGrid.setHeight("320px");
        recentNotificationsGrid.setHeight("320px");

        VerticalLayout left = new VerticalLayout(new H3("Leads recentes"), recentLeadsGrid);
        left.setPadding(true);
        left.setSpacing(true);
        left.setSizeFull();
        left.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "16px");

        VerticalLayout center = new VerticalLayout(new H3("Anúncios recentes"), recentAdsGrid);
        center.setPadding(true);
        center.setSpacing(true);
        center.setSizeFull();
        center.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "16px");

        VerticalLayout right = new VerticalLayout(new H3("Notificações recentes"), recentNotificationsGrid);
        right.setPadding(true);
        right.setSpacing(true);
        right.setSizeFull();
        right.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "16px");

        HorizontalLayout row = new HorizontalLayout(left, center, right);
        row.setSizeFull();
        row.setSpacing(true);
        row.setFlexGrow(1, left, center, right);

        return row;
    }

    private void configureRecentLeadsGrid() {
        recentLeadsGrid.addColumn(Lead::getName)
                .setHeader("Lead")
                .setAutoWidth(true);

        recentLeadsGrid.addColumn(lead -> valueOrDash(lead.getCompany()))
                .setHeader("Empresa")
                .setAutoWidth(true);

        recentLeadsGrid.addColumn(lead -> lead.getStatus() != null ? lead.getStatus().name() : "—")
                .setHeader("Status")
                .setAutoWidth(true);
    }

    private void configureRecentAdsGrid() {
        recentAdsGrid.addColumn(TrafficAd::getName)
                .setHeader("Anúncio")
                .setAutoWidth(true);

        recentAdsGrid.addColumn(ad -> ad.getClient() != null ? ad.getClient().getName() : "—")
                .setHeader("Cliente")
                .setAutoWidth(true);

        recentAdsGrid.addColumn(ad -> valueOrDash(ad.getStatus()))
                .setHeader("Status")
                .setAutoWidth(true);
    }

    private void configureRecentNotificationsGrid() {
        recentNotificationsGrid.addColumn(AppNotification::getTitle)
                .setHeader("Título")
                .setAutoWidth(true);

        recentNotificationsGrid.addColumn(notification -> valueOrDash(notification.getTargetSector()))
                .setHeader("Setor")
                .setAutoWidth(true);

        recentNotificationsGrid.addColumn(notification -> notification.isRead() ? "Lida" : "Nova")
                .setHeader("Status")
                .setAutoWidth(true);
    }

    private void refresh() {
        List<Lead> leads = leadRepository.findAll().stream()
                .sorted(Comparator.comparing(Lead::getCreatedAt).reversed())
                .toList();

        List<TrafficAd> ads = trafficAdRepository.findAll().stream()
                .sorted(Comparator.comparing(TrafficAd::getId).reversed())
                .toList();

        List<AppNotification> notifications = appNotificationRepository.findAllByOrderByCreatedAtDesc();

        totalClients.setText(String.valueOf(clientRepository.count()));
        totalLeads.setText(String.valueOf(leads.size()));
        totalAds.setText(String.valueOf(ads.size()));
        unreadNotifications.setText(String.valueOf(
                notifications.stream().filter(n -> !n.isRead()).count()
        ));

        recentLeadsGrid.setItems(leads.stream().limit(8).toList());
        recentAdsGrid.setItems(ads.stream().limit(8).toList());
        recentNotificationsGrid.setItems(notifications.stream().limit(8).toList());
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}