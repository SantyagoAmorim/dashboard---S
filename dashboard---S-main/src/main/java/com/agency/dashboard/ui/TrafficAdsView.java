package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.TrafficAdStatus;
import com.agency.dashboard.domain.TrafficAdTask;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.TrafficAdTaskRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;

@Route(value = "traffic-ads", layout = MainLayout.class)
@PageTitle("Anúncios do Tráfego | Creative Ops")
public class TrafficAdsView extends VerticalLayout {

    private final TrafficAdTaskRepository trafficAdTaskRepository;
    private final ClientRepository clientRepository;

    private final Grid<TrafficAdTask> grid = new Grid<>(TrafficAdTask.class, false);

    private final ComboBox<Client> clientFilter = new ComboBox<>("Cliente");
    private final ComboBox<TrafficAdStatus> statusFilter = new ComboBox<>("Status");

    public TrafficAdsView(
            TrafficAdTaskRepository trafficAdTaskRepository,
            ClientRepository clientRepository
    ) {
        this.trafficAdTaskRepository = trafficAdTaskRepository;
        this.clientRepository = clientRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Fluxo de Anúncios");

        Button newTaskButton = new Button("Novo card", event -> openForm(new TrafficAdTask()));
        Button refreshButton = new Button("Atualizar", event -> refreshGrid());

        configureFilters();
        configureGrid();

        HorizontalLayout top = new HorizontalLayout(title, newTaskButton, refreshButton);
        top.setWidthFull();
        top.setAlignItems(Alignment.CENTER);
        top.expand(title);

        HorizontalLayout filters = new HorizontalLayout(clientFilter, statusFilter);
        filters.setWidthFull();
        filters.setAlignItems(Alignment.END);

        add(top, filters, grid);

        refreshGrid();
    }

    private void configureFilters() {
        clientFilter.setItems(clientRepository.findAll().stream()
                .sorted(Comparator.comparing(Client::getName))
                .toList());
        clientFilter.setItemLabelGenerator(Client::getName);
        clientFilter.setClearButtonVisible(true);
        clientFilter.addValueChangeListener(event -> refreshGrid());

        statusFilter.setItems(TrafficAdStatus.values());
        statusFilter.setItemLabelGenerator(TrafficAdStatus::getLabel);
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(event -> refreshGrid());
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(task -> task.getClient() != null ? task.getClient().getName() : "—")
                .setHeader("Cliente")
                .setAutoWidth(true);

        grid.addColumn(TrafficAdTask::getTitle)
                .setHeader("Título")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(task -> task.getStatus() != null ? task.getStatus().getLabel() : "—")
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addColumn(task -> valueOrDash(task.getResponsible()))
                .setHeader("Responsável")
                .setAutoWidth(true);

        grid.addColumn(task -> valueOrDash(task.getCreatedBySector()))
                .setHeader("Setor")
                .setAutoWidth(true);

        grid.addColumn(task -> task.getDueDate() != null ? task.getDueDate().toString() : "—")
                .setHeader("Prazo")
                .setAutoWidth(true);

        grid.addComponentColumn(task -> {
            if (task.getMediaUrl() == null || task.getMediaUrl().isBlank()) {
                return new com.vaadin.flow.component.html.Span("—");
            }
            Anchor anchor = new Anchor(task.getMediaUrl(), "Ver mídia");
            anchor.setTarget("_blank");
            return anchor;
        }).setHeader("Mídia").setAutoWidth(true);

        grid.addComponentColumn(task -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button approval = new Button("Aprovação", e -> updateStatus(task, TrafficAdStatus.ESPERANDO_APROVACAO_CRIATIVO));
            Button pending = new Button("Pendente", e -> updateStatus(task, TrafficAdStatus.PENDENTE));
            Button progress = new Button("Andamento", e -> updateStatus(task, TrafficAdStatus.EM_ANDAMENTO));
            Button done = new Button("Concluir", e -> updateStatus(task, TrafficAdStatus.CONCLUIDO));

            actions.add(approval, pending, progress, done);
            return actions;
        }).setHeader("Mover para").setAutoWidth(true);

        grid.addItemDoubleClickListener(this::handleEdit);
    }

    private void handleEdit(ItemDoubleClickEvent<TrafficAdTask> event) {
        openForm(event.getItem());
    }

    private void openForm(TrafficAdTask task) {
        Dialog dialog = new Dialog();
        dialog.setWidth("950px");

        ComboBox<Client> client = new ComboBox<>("Cliente");
        client.setItems(clientRepository.findAll().stream()
                .sorted(Comparator.comparing(Client::getName))
                .toList());
        client.setItemLabelGenerator(Client::getName);
        client.setWidthFull();

        TextField title = new TextField("Título");
        title.setWidthFull();

        ComboBox<TrafficAdStatus> status = new ComboBox<>("Status");
        status.setItems(TrafficAdStatus.values());
        status.setItemLabelGenerator(TrafficAdStatus::getLabel);
        status.setWidthFull();

        TextField responsible = new TextField("Responsável");
        responsible.setWidthFull();

        TextField createdBySector = new TextField("Setor criador");
        createdBySector.setWidthFull();
        createdBySector.setPlaceholder("Ex: DESIGN ou TRAFEGO");

        DatePicker dueDate = new DatePicker("Prazo");
        dueDate.setWidthFull();

        TextField mediaUrl = new TextField("Link da mídia / criativo");
        mediaUrl.setWidthFull();

        TextArea description = new TextArea("Descrição geral");
        description.setWidthFull();
        description.setMinHeight("120px");

        TextArea designNotes = new TextArea("Observações do Design");
        designNotes.setWidthFull();
        designNotes.setMinHeight("120px");

        TextArea trafficNotes = new TextArea("Observações do Tráfego");
        trafficNotes.setWidthFull();
        trafficNotes.setMinHeight("120px");

        client.setValue(task.getClient());
        title.setValue(task.getTitle() == null ? "" : task.getTitle());
        status.setValue(task.getStatus());
        responsible.setValue(task.getResponsible() == null ? "" : task.getResponsible());
        createdBySector.setValue(task.getCreatedBySector() == null ? "" : task.getCreatedBySector());
        dueDate.setValue(task.getDueDate());
        mediaUrl.setValue(task.getMediaUrl() == null ? "" : task.getMediaUrl());
        description.setValue(task.getDescription() == null ? "" : task.getDescription());
        designNotes.setValue(task.getDesignNotes() == null ? "" : task.getDesignNotes());
        trafficNotes.setValue(task.getTrafficNotes() == null ? "" : task.getTrafficNotes());

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(
                client, title,
                status, responsible,
                createdBySector, dueDate,
                mediaUrl, description,
                designNotes, trafficNotes
        );

        formLayout.setColspan(description, 2);
        formLayout.setColspan(designNotes, 2);
        formLayout.setColspan(trafficNotes, 2);

        Button saveButton = new Button("Salvar", e -> {
            if (client.getValue() == null) {
                Notification.show("Selecione o cliente.");
                return;
            }

            if (title.getValue() == null || title.getValue().isBlank()) {
                Notification.show("Informe o título.");
                return;
            }

            task.setClient(client.getValue());
            task.setTitle(title.getValue());
            task.setStatus(status.getValue() == null ? TrafficAdStatus.PENDENTE : status.getValue());
            task.setResponsible(responsible.getValue());
            task.setCreatedBySector(createdBySector.getValue());
            task.setDueDate(dueDate.getValue());
            task.setMediaUrl(mediaUrl.getValue());
            task.setDescription(description.getValue());
            task.setDesignNotes(designNotes.getValue());
            task.setTrafficNotes(trafficNotes.getValue());

            trafficAdTaskRepository.save(task);
            Notification.show("Card salvo com sucesso.");
            dialog.close();
            refreshGrid();
        });

        Button deleteButton = new Button("Excluir", e -> {
            if (task.getId() != null) {
                trafficAdTaskRepository.delete(task);
                Notification.show("Card excluído.");
                refreshGrid();
            }
            dialog.close();
        });

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        deleteButton.setVisible(task.getId() != null);

        HorizontalLayout actions = new HorizontalLayout(saveButton, deleteButton, cancelButton);

        VerticalLayout content = new VerticalLayout(
                new H2(task.getId() == null ? "Novo card de anúncio" : "Editar card de anúncio"),
                formLayout,
                actions
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        dialog.add(content);
        dialog.open();
    }

    private void updateStatus(TrafficAdTask task, TrafficAdStatus newStatus) {
        task.setStatus(newStatus);
        trafficAdTaskRepository.save(task);
        Notification.show("Status atualizado para: " + newStatus.getLabel());
        refreshGrid();
    }

    private void refreshGrid() {
        Client selectedClient = clientFilter.getValue();
        TrafficAdStatus selectedStatus = statusFilter.getValue();

        List<TrafficAdTask> tasks;

        if (selectedClient != null && selectedStatus != null) {
            tasks = trafficAdTaskRepository.findByClientAndStatusOrderByCreatedAtDesc(selectedClient, selectedStatus);
        } else if (selectedClient != null) {
            tasks = trafficAdTaskRepository.findByClientOrderByCreatedAtDesc(selectedClient);
        } else if (selectedStatus != null) {
            tasks = trafficAdTaskRepository.findByStatusOrderByCreatedAtDesc(selectedStatus);
        } else {
            tasks = trafficAdTaskRepository.findAllByOrderByCreatedAtDesc();
        }

        grid.setItems(tasks);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}