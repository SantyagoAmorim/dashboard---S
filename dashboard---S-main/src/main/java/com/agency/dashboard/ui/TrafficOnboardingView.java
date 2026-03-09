package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.OnboardingTask;
import com.agency.dashboard.domain.OnboardingTaskStatus;
import com.agency.dashboard.domain.OnboardingTaskType;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.OnboardingTaskRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;

@Route(value = "traffic-onboarding", layout = MainLayout.class)
@PageTitle("Onboarding do Tráfego | Creative Ops")
public class TrafficOnboardingView extends VerticalLayout {

    private final OnboardingTaskRepository onboardingTaskRepository;
    private final ClientRepository clientRepository;

    private final Grid<OnboardingTask> grid = new Grid<>(OnboardingTask.class, false);

    private final ComboBox<Client> clientFilter = new ComboBox<>("Cliente");
    private final ComboBox<OnboardingTaskStatus> statusFilter = new ComboBox<>("Status");
    private final ComboBox<OnboardingTaskType> typeFilter = new ComboBox<>("Etapa");

    public TrafficOnboardingView(
            OnboardingTaskRepository onboardingTaskRepository,
            ClientRepository clientRepository
    ) {
        this.onboardingTaskRepository = onboardingTaskRepository;
        this.clientRepository = clientRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Onboarding do Tráfego");

        configureFilters();
        configureGrid();

        Button refreshButton = new Button("Atualizar", event -> refreshGrid());

        HorizontalLayout filters = new HorizontalLayout(clientFilter, statusFilter, typeFilter, refreshButton);
        filters.setWidthFull();
        filters.setAlignItems(Alignment.END);

        add(title, filters, grid);

        refreshGrid();
    }

    private void configureFilters() {
        clientFilter.setItems(clientRepository.findAll().stream()
                .sorted(Comparator.comparing(Client::getName))
                .toList());
        clientFilter.setItemLabelGenerator(Client::getName);
        clientFilter.setClearButtonVisible(true);
        clientFilter.addValueChangeListener(event -> refreshGrid());

        statusFilter.setItems(OnboardingTaskStatus.values());
        statusFilter.setItemLabelGenerator(Enum::name);
        statusFilter.setClearButtonVisible(true);
        statusFilter.addValueChangeListener(event -> refreshGrid());

        typeFilter.setItems(OnboardingTaskType.values());
        typeFilter.setItemLabelGenerator(OnboardingTaskType::getLabel);
        typeFilter.setClearButtonVisible(true);
        typeFilter.addValueChangeListener(event -> refreshGrid());
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(task -> task.getClient() != null ? task.getClient().getName() : "—")
                .setHeader("Cliente")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(task -> task.getTaskType() != null ? task.getTaskType().getLabel() : "—")
                .setHeader("Etapa")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(task -> task.getStatus() != null ? task.getStatus().name() : "—")
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addColumn(task -> valueOrDash(task.getResponsible()))
                .setHeader("Responsável")
                .setAutoWidth(true);

        grid.addColumn(task -> task.getDueDate() != null ? task.getDueDate().toString() : "—")
                .setHeader("Vencimento")
                .setAutoWidth(true);

        grid.addColumn(OnboardingTask::getSortOrder)
                .setHeader("Ordem")
                .setAutoWidth(true);

        grid.addComponentColumn(task -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button pendingButton = new Button("Pendente", e -> updateStatus(task, OnboardingTaskStatus.PENDING));
            Button progressButton = new Button("Em andamento", e -> updateStatus(task, OnboardingTaskStatus.IN_PROGRESS));
            Button doneButton = new Button("Concluir", e -> updateStatus(task, OnboardingTaskStatus.DONE));

            actions.add(pendingButton, progressButton, doneButton);
            return actions;
        }).setHeader("Ações").setAutoWidth(true);

        grid.addItemDoubleClickListener(this::openEditDialog);
    }

    private void updateStatus(OnboardingTask task, OnboardingTaskStatus newStatus) {
        task.setStatus(newStatus);
        onboardingTaskRepository.save(task);
        Notification.show("Status atualizado para " + newStatus.name());
        refreshGrid();
    }

    private void openEditDialog(ItemDoubleClickEvent<OnboardingTask> event) {
        OnboardingTask task = event.getItem();

        Dialog dialog = new Dialog();
        dialog.setWidth("700px");

        TextField responsible = new TextField("Responsável");
        responsible.setWidthFull();
        responsible.setValue(task.getResponsible() == null ? "" : task.getResponsible());

        ComboBox<OnboardingTaskStatus> status = new ComboBox<>("Status");
        status.setItems(OnboardingTaskStatus.values());
        status.setValue(task.getStatus());
        status.setWidthFull();

        TextArea notes = new TextArea("Observações");
        notes.setWidthFull();
        notes.setMinHeight("160px");
        notes.setValue(task.getNotes() == null ? "" : task.getNotes());

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(responsible, status, notes);
        formLayout.setColspan(notes, 2);

        Button saveButton = new Button("Salvar", e -> {
            task.setResponsible(responsible.getValue());
            task.setStatus(status.getValue());
            task.setNotes(notes.getValue());

            onboardingTaskRepository.save(task);
            Notification.show("Etapa atualizada com sucesso.");
            dialog.close();
            refreshGrid();
        });

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);

        VerticalLayout content = new VerticalLayout(
                new H2(task.getTaskType() != null ? task.getTaskType().getLabel() : "Editar etapa"),
                formLayout,
                actions
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        dialog.add(content);
        dialog.open();
    }

    private void refreshGrid() {
        Client selectedClient = clientFilter.getValue();
        OnboardingTaskStatus selectedStatus = statusFilter.getValue();
        OnboardingTaskType selectedType = typeFilter.getValue();

        List<OnboardingTask> tasks;

        if (selectedClient != null && selectedStatus != null && selectedType != null) {
            tasks = onboardingTaskRepository.findByClientAndStatusAndTaskTypeOrderBySortOrderAsc(
                    selectedClient, selectedStatus, selectedType
            );
        } else if (selectedClient != null && selectedStatus != null) {
            tasks = onboardingTaskRepository.findByClientAndStatusOrderBySortOrderAsc(
                    selectedClient, selectedStatus
            );
        } else if (selectedClient != null && selectedType != null) {
            tasks = onboardingTaskRepository.findByClientAndTaskTypeOrderBySortOrderAsc(
                    selectedClient, selectedType
            );
        } else if (selectedStatus != null && selectedType != null) {
            tasks = onboardingTaskRepository.findByStatusAndTaskTypeOrderBySortOrderAsc(
                    selectedStatus, selectedType
            );
        } else if (selectedClient != null) {
            tasks = onboardingTaskRepository.findByClientOrderBySortOrderAsc(selectedClient);
        } else if (selectedStatus != null) {
            tasks = onboardingTaskRepository.findByStatusOrderBySortOrderAsc(selectedStatus);
        } else if (selectedType != null) {
            tasks = onboardingTaskRepository.findByTaskTypeOrderBySortOrderAsc(selectedType);
        } else {
            tasks = onboardingTaskRepository.findAllByOrderByCreatedAtDesc();
        }

        grid.setItems(tasks);
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}