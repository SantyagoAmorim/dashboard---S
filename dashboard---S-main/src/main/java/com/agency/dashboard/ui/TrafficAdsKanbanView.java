package com.agency.dashboard.ui;

import com.agency.dashboard.domain.TrafficAdStatus;
import com.agency.dashboard.domain.TrafficAdTask;
import com.agency.dashboard.repo.TrafficAdTaskRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;

@Route(value = "traffic-ads-board", layout = MainLayout.class)
@PageTitle("Board de Anúncios | Creative Ops")
public class TrafficAdsKanbanView extends VerticalLayout {

    private final TrafficAdTaskRepository trafficAdTaskRepository;

    private final VerticalLayout approvalColumn = createCardsContainer();
    private final VerticalLayout pendingColumn = createCardsContainer();
    private final VerticalLayout progressColumn = createCardsContainer();
    private final VerticalLayout doneColumn = createCardsContainer();

    public TrafficAdsKanbanView(TrafficAdTaskRepository trafficAdTaskRepository) {
        this.trafficAdTaskRepository = trafficAdTaskRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H3 title = new H3("Board de Anúncios");

        HorizontalLayout board = new HorizontalLayout(
                createColumn("Esperando aprovação do criativo", approvalColumn),
                createColumn("Pendente", pendingColumn),
                createColumn("Em andamento", progressColumn),
                createColumn("Concluído", doneColumn)
        );

        board.setSizeFull();
        board.setSpacing(true);

        add(title, board);
        expand(board);

        refreshBoard();
    }

    private VerticalLayout createColumn(String title, VerticalLayout cardsContainer) {
        H4 header = new H4(title);
        header.getStyle().set("margin", "0");

        VerticalLayout wrapper = new VerticalLayout(header, cardsContainer);
        wrapper.setWidth("25%");
        wrapper.setHeightFull();
        wrapper.setPadding(true);
        wrapper.setSpacing(true);

        wrapper.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "14px")
                .set("border", "1px solid var(--lumo-contrast-10pct)");

        return wrapper;
    }

    private VerticalLayout createCardsContainer() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(false);
        container.setSpacing(true);
        container.setWidthFull();
        return container;
    }

    private void refreshBoard() {
        approvalColumn.removeAll();
        pendingColumn.removeAll();
        progressColumn.removeAll();
        doneColumn.removeAll();

        List<TrafficAdTask> tasks = trafficAdTaskRepository.findAll().stream()
                .sorted(Comparator.comparing(TrafficAdTask::getCreatedAt).reversed())
                .toList();

        for (TrafficAdTask task : tasks) {
            Div card = buildCard(task);

            switch (task.getStatus()) {
                case ESPERANDO_APROVACAO_CRIATIVO -> approvalColumn.add(card);
                case PENDENTE -> pendingColumn.add(card);
                case EM_ANDAMENTO -> progressColumn.add(card);
                case CONCLUIDO -> doneColumn.add(card);
            }
        }
    }

    private Div buildCard(TrafficAdTask task) {
        Div card = new Div();
        card.getStyle()
                .set("background", "white")
                .set("padding", "12px")
                .set("border-radius", "12px")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("box-shadow", "var(--lumo-box-shadow-xs)");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        Span client = new Span(task.getClient() != null ? task.getClient().getName() : "—");
        client.getStyle()
                .set("font-size", "12px")
                .set("color", "var(--lumo-secondary-text-color)");

        Span title = new Span(task.getTitle() != null ? task.getTitle() : "Sem título");
        title.getStyle()
                .set("font-weight", "700")
                .set("font-size", "14px");

        Span responsible = new Span("Responsável: " + valueOrDash(task.getResponsible()));
        Span sector = new Span("Setor: " + valueOrDash(task.getCreatedBySector()));
        Span dueDate = new Span("Prazo: " + (task.getDueDate() != null ? task.getDueDate().toString() : "—"));

        responsible.getStyle().set("font-size", "12px");
        sector.getStyle().set("font-size", "12px");
        dueDate.getStyle().set("font-size", "12px");

        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button backButton = new Button("←", e -> moveBack(task));
        Button editButton = new Button("Editar", e -> getUI().ifPresent(ui -> ui.navigate("traffic-ads")));
        Button nextButton = new Button("→", e -> moveNext(task));

        backButton.setEnabled(task.getStatus() != TrafficAdStatus.ESPERANDO_APROVACAO_CRIATIVO);
        nextButton.setEnabled(task.getStatus() != TrafficAdStatus.CONCLUIDO);

        actions.add(backButton, editButton, nextButton);

        content.add(client, title, responsible, sector, dueDate, actions);
        card.add(content);

        return card;
    }

    private void moveNext(TrafficAdTask task) {
        switch (task.getStatus()) {
            case ESPERANDO_APROVACAO_CRIATIVO -> task.setStatus(TrafficAdStatus.PENDENTE);
            case PENDENTE -> task.setStatus(TrafficAdStatus.EM_ANDAMENTO);
            case EM_ANDAMENTO -> task.setStatus(TrafficAdStatus.CONCLUIDO);
            case CONCLUIDO -> {
                return;
            }
        }

        trafficAdTaskRepository.save(task);
        Notification.show("Card movido com sucesso.");
        refreshBoard();
    }

    private void moveBack(TrafficAdTask task) {
        switch (task.getStatus()) {
            case ESPERANDO_APROVACAO_CRIATIVO -> {
                return;
            }
            case PENDENTE -> task.setStatus(TrafficAdStatus.ESPERANDO_APROVACAO_CRIATIVO);
            case EM_ANDAMENTO -> task.setStatus(TrafficAdStatus.PENDENTE);
            case CONCLUIDO -> task.setStatus(TrafficAdStatus.EM_ANDAMENTO);
        }

        trafficAdTaskRepository.save(task);
        Notification.show("Card movido com sucesso.");
        refreshBoard();
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}