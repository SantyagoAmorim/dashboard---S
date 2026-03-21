package com.agency.dashboard.ui;

import com.agency.dashboard.domain.User;
import com.agency.dashboard.security.AccessControl;
import com.agency.dashboard.service.CurrentUserService;
import com.agency.dashboard.service.NotificationService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout(CurrentUserService currentUserService, NotificationService notificationService) {
        User currentUser = currentUserService.getCurrentUser();
        long unreadCount = notificationService.unreadCount();

        setPrimarySection(Section.DRAWER);

        addToNavbar(buildHeader(currentUser, unreadCount, currentUserService));
        addToDrawer(buildDrawer(currentUser));
    }

    private Div buildHeader(User currentUser, long unreadCount, CurrentUserService currentUserService) {
        DrawerToggle toggle = new DrawerToggle();

        H1 title = new H1("Creative Ops");
        title.getStyle()
                .set("font-size", "1.2rem")
                .set("margin", "0")
                .set("font-weight", "700");

        Span subtitle = new Span("Start Digital Company");
        subtitle.getStyle()
                .set("font-size", "0.8rem")
                .set("color", "var(--lumo-secondary-text-color)");

        VerticalLayout titleBlock = new VerticalLayout(title, subtitle);
        titleBlock.setPadding(false);
        titleBlock.setSpacing(false);
        titleBlock.getStyle().set("line-height", "1.1");

        Div left = new Div(toggle, titleBlock);
        left.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px");

        Span notifications = new Span("Notificações: " + unreadCount);
        notifications.getStyle()
                .set("font-weight", "600")
                .set("padding", "8px 12px")
                .set("border-radius", "999px")
                .set("background", "var(--lumo-contrast-5pct)")
                .set("color", unreadCount > 0
                        ? "var(--lumo-error-text-color)"
                        : "var(--lumo-secondary-text-color)");

        String userText = currentUser != null
                ? currentUser.getName() + " (" + currentUser.getRole().name() + ")"
                : "Usuário";

        Span userInfo = new Span(userText);
        userInfo.getStyle()
                .set("font-weight", "500");

        Button logoutButton = new Button("Sair", event -> {
            currentUserService.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });
        logoutButton.setIcon(new Icon(VaadinIcon.SIGN_OUT));

        Div right = new Div(notifications, userInfo, logoutButton);
        right.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "12px")
                .set("margin-left", "auto");

        Div header = new Div(left, right);
        header.getStyle()
                .set("width", "100%")
                .set("display", "flex")
                .set("align-items", "center")
                .set("padding", "12px 20px")
                .set("box-sizing", "border-box")
                .set("border-bottom", "1px solid var(--lumo-contrast-10pct)")
                .set("background", "var(--lumo-base-color)");

        return header;
    }

    private Scroller buildDrawer(User currentUser) {
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setPadding(true);
        drawerContent.setSpacing(false);
        drawerContent.setSizeFull();
        drawerContent.getStyle()
                .set("gap", "8px")
                .set("background", "var(--lumo-base-color)");

        drawerContent.add(buildBrandBlock());

        if (AccessControl.canAccessDesign(currentUser)) {
            drawerContent.add(buildSectionTitle("Design"));
            drawerContent.add(createNavLink("Dashboard Design", DesignDashboardView.class, VaadinIcon.DASHBOARD));
            drawerContent.add(createNavLink("Pedidos", RequestsView.class, VaadinIcon.CLIPBOARD_TEXT));
        }

        if (AccessControl.canAccessTraffic(currentUser)) {
            drawerContent.add(new Hr());
            drawerContent.add(buildSectionTitle("Tráfego"));
            drawerContent.add(createNavLink("Dashboard Tráfego", TrafficDashboardView.class, VaadinIcon.CHART));
            drawerContent.add(createNavLink("Clientes", ClientsView.class, VaadinIcon.USERS));
            drawerContent.add(createNavLink("Tarefas", TasksView.class, VaadinIcon.TASKS));
            drawerContent.add(createNavLink("Onboarding", TrafficOnboardingView.class, VaadinIcon.ROAD_BRANCH));
            drawerContent.add(createNavLink("Anúncios", TrafficAdsView.class, VaadinIcon.MEGAPHONE));
            drawerContent.add(createNavLink("Board Anúncios", TrafficAdsKanbanView.class, VaadinIcon.SPLIT));
        }

        if (AccessControl.canAccessCommercial(currentUser)) {
            drawerContent.add(new Hr());
            drawerContent.add(buildSectionTitle("Comercial"));
            drawerContent.add(createNavLink("Dashboard Comercial", CommercialDashboardView.class, VaadinIcon.LINE_BAR_CHART));
            drawerContent.add(createNavLink("Pipeline", SalesPipelineView.class, VaadinIcon.MONEY));
        }

        if (AccessControl.canAccessManagement(currentUser)) {
            drawerContent.add(new Hr());
            drawerContent.add(buildSectionTitle("Gestão"));
            drawerContent.add(createNavLink("Dashboard Gestão", ManagementDashboardView.class, VaadinIcon.OFFICE));
        }

        drawerContent.add(new Hr());
        drawerContent.add(buildSectionTitle("Geral"));
        drawerContent.add(createNavLink("Notificações", NotificationsView.class, VaadinIcon.BELL));

        if (AccessControl.canAccessUsers(currentUser)) {
            drawerContent.add(createNavLink("Usuários", UsersView.class, VaadinIcon.USER_CARD));
        }

        Scroller scroller = new Scroller(drawerContent);
        scroller.setSizeFull();
        return scroller;
    }

    private VerticalLayout buildBrandBlock() {
        H1 brand = new H1("START");
        brand.getStyle()
                .set("font-size", "1.4rem")
                .set("margin", "0")
                .set("font-weight", "800");

        Span subtitle = new Span("Creative Ops Dashboard");
        subtitle.getStyle()
                .set("color", "var(--lumo-secondary-text-color)")
                .set("font-size", "0.85rem");

        VerticalLayout brandBlock = new VerticalLayout(brand, subtitle);
        brandBlock.setPadding(false);
        brandBlock.setSpacing(false);
        brandBlock.getStyle()
                .set("padding", "8px 4px 16px 4px");

        return brandBlock;
    }

    private Span buildSectionTitle(String text) {
        Span section = new Span(text);
        section.getStyle()
                .set("font-size", "0.75rem")
                .set("font-weight", "700")
                .set("letter-spacing", "0.08em")
                .set("text-transform", "uppercase")
                .set("color", "var(--lumo-secondary-text-color)")
                .set("padding", "10px 6px 6px 6px");
        return section;
    }

    private RouterLink createNavLink(String text, Class<? extends com.vaadin.flow.component.Component> navigationTarget, VaadinIcon icon) {
        Icon navIcon = icon.create();
        navIcon.getStyle().set("margin-right", "10px");

        RouterLink link = new RouterLink();
        link.add(navIcon, new Span(text));
        link.setRoute(navigationTarget);

        link.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("padding", "10px 12px")
                .set("border-radius", "10px")
                .set("text-decoration", "none")
                .set("color", "var(--lumo-body-text-color)")
                .set("font-weight", "500");

        return link;
    }
}