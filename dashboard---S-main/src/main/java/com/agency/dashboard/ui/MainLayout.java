package com.agency.dashboard.ui;

import com.agency.dashboard.domain.User;
import com.agency.dashboard.domain.UserRole;
import com.agency.dashboard.service.CurrentUserService;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout(CurrentUserService currentUserService) {
        H1 title = new H1("Creative Ops Dashboard");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

        Span userName = new Span();
        User currentUser = currentUserService.getCurrentUser();
        if (currentUser != null) {
            userName.setText("Olá, " + currentUser.getName());
        }

        Button logoutButton = new Button("Sair", event -> {
            currentUserService.logout();
            getUI().ifPresent(ui -> ui.navigate("login"));
        });

        HorizontalLayout right = new HorizontalLayout(userName, logoutButton);
        right.setAlignItems(HorizontalLayout.Alignment.CENTER);

        HorizontalLayout header = new HorizontalLayout(title, right);
        header.setWidthFull();
        header.setJustifyContentMode(HorizontalLayout.JustifyContentMode.BETWEEN);
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);
        header.setPadding(true);

        addToNavbar(header);

        HorizontalLayout nav = new HorizontalLayout();
        nav.setPadding(true);
        nav.setSpacing(true);

        nav.add(
                new RouterLink("Dashboard", DashboardView.class),
                new RouterLink("Pedidos", RequestsView.class),
                new RouterLink("Clientes", ClientsView.class),
                new RouterLink("Tarefas", TasksView.class),
                new RouterLink("Onboarding Tráfego", TrafficOnboardingView.class),
                new RouterLink("Anúncios Tráfego", TrafficAdsView.class)
        );

        if (currentUser != null && (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.MANAGEMENT)) {
            nav.add(new RouterLink("Usuários", UsersView.class));
        }

        addToDrawer(nav);
    }
}