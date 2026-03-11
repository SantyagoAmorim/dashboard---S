package com.agency.dashboard.ui;

import com.agency.dashboard.domain.AppNotification;
import com.agency.dashboard.service.NotificationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "notifications", layout = MainLayout.class)
@PageTitle("Notificações | Creative Ops")
public class NotificationsView extends VerticalLayout {

    private final NotificationService notificationService;
    private final Grid<AppNotification> grid = new Grid<>(AppNotification.class, false);

    public NotificationsView(NotificationService notificationService) {
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Notificações");

        Button refreshButton = new Button("Atualizar", e -> refreshGrid());
        Button markAllReadButton = new Button("Marcar todas como lidas", e -> {
            notificationService.markAllAsRead();
            Notification.show("Todas as notificações foram marcadas como lidas.");
            refreshGrid();
        });

        HorizontalLayout top = new HorizontalLayout(title, refreshButton, markAllReadButton);
        top.setWidthFull();
        top.setAlignItems(Alignment.CENTER);
        top.expand(title);

        configureGrid();

        add(top, grid);
        expand(grid);

        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(AppNotification::getTitle)
                .setHeader("Título")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(AppNotification::getMessage)
                .setHeader("Mensagem")
                .setAutoWidth(true)
                .setFlexGrow(2);

        grid.addColumn(notification -> valueOrDash(notification.getTargetSector()))
                .setHeader("Setor")
                .setAutoWidth(true);

        grid.addColumn(notification -> valueOrDash(notification.getTargetUser()))
                .setHeader("Usuário")
                .setAutoWidth(true);

        grid.addColumn(notification -> notification.isRead() ? "Lida" : "Não lida")
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addColumn(notification -> notification.getCreatedAt() != null ? notification.getCreatedAt().toString() : "—")
                .setHeader("Criada em")
                .setAutoWidth(true);

        grid.addComponentColumn(notification -> {
            Button button = new Button("Marcar como lida", e -> {
                notificationService.markAsRead(notification.getId());
                Notification.show("Notificação marcada como lida.");
                refreshGrid();
            });

            button.setEnabled(!notification.isRead());
            return button;
        }).setHeader("Ação").setAutoWidth(true);
    }

    private void refreshGrid() {
        grid.setItems(notificationService.findAll());
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}