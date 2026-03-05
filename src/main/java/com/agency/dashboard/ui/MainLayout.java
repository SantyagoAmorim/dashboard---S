package com.agency.dashboard.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;

public class MainLayout extends AppLayout {

    public MainLayout() {

        H1 title = new H1("Creative Ops Dashboard");
        title.getStyle()
                .set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");

        HorizontalLayout header = new HorizontalLayout(title);
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(HorizontalLayout.Alignment.CENTER);

        addToNavbar(header);

        HorizontalLayout menu = new HorizontalLayout(
                new RouterLink("Dashboard", DashboardView.class),
                new RouterLink("Pedidos", RequestsView.class)
        );

        menu.setPadding(true);

        addToDrawer(menu);
    }
}