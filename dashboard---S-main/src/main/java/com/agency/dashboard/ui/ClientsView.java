package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.ClientPlan;
import com.agency.dashboard.domain.ClientSector;
import com.agency.dashboard.domain.Task;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.TaskRepository;
import com.agency.dashboard.service.OnboardingService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.ItemDoubleClickEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "clients", layout = MainLayout.class)
@PageTitle("Clientes | Creative Ops")
public class ClientsView extends VerticalLayout {

    private final ClientRepository clientRepository;
    private final TaskRepository taskRepository;
    private final OnboardingService onboardingService;
    private final Grid<Client> grid = new Grid<>(Client.class, false);

    public ClientsView(
            ClientRepository clientRepository,
            TaskRepository taskRepository,
            OnboardingService onboardingService
    ) {
        this.clientRepository = clientRepository;
        this.taskRepository = taskRepository;
        this.onboardingService = onboardingService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Gestão de Clientes");

        Button newClientButton = new Button("Novo cliente", event -> openForm(new Client()));

        HorizontalLayout header = new HorizontalLayout(title, newClientButton);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        configureGrid();

        add(header, grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(Client::getName).setHeader("Cliente").setAutoWidth(true);
        grid.addColumn(client -> valueOrDash(client.getCompany())).setHeader("Empresa").setAutoWidth(true);
        grid.addColumn(client -> valueOrDash(client.getPlan() != null ? client.getPlan().getLabel() : null))
                .setHeader("Plano")
                .setAutoWidth(true);
        grid.addColumn(client -> valueOrDash(client.getSector() != null ? client.getSector().name() : null))
                .setHeader("Setor")
                .setAutoWidth(true);
        grid.addColumn(client -> valueOrDash(client.getSquad())).setHeader("Squad").setAutoWidth(true);
        grid.addColumn(client -> valueOrDash(client.getAccountManager())).setHeader("Gestor").setAutoWidth(true);
        grid.addColumn(client -> client.isOnboardingCompleted() ? "Concluído" : "Pendente")
                .setHeader("Onboarding")
                .setAutoWidth(true);
        grid.addColumn(client -> client.getOnboardingMeetingDate() != null ? client.getOnboardingMeetingDate().toString() : "—")
                .setHeader("Reunião alinhamento")
                .setAutoWidth(true);

        grid.addItemDoubleClickListener(this::handleEdit);
    }

    private void handleEdit(ItemDoubleClickEvent<Client> event) {
        openForm(event.getItem());
    }

    private void openForm(Client client) {
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");

        TextField name = new TextField("Nome do cliente");
        TextField company = new TextField("Empresa");
        TextField whatsapp = new TextField("WhatsApp");
        EmailField email = new EmailField("E-mail");
        TextField accountManager = new TextField("Gestor responsável");
        TextField squad = new TextField("Squad");

        ComboBox<ClientPlan> plan = new ComboBox<>("Plano");
        plan.setItems(ClientPlan.values());
        plan.setItemLabelGenerator(ClientPlan::getLabel);

        ComboBox<ClientSector> sector = new ComboBox<>("Setor responsável");
        sector.setItems(ClientSector.values());

        DatePicker onboardingMeetingDate = new DatePicker("Data da reunião de alinhamento");
        Checkbox onboardingCompleted = new Checkbox("Reunião de alinhamento concluída");

        TextArea notes = new TextArea("Observações");
        notes.setWidthFull();
        notes.setMinHeight("140px");

        name.setWidthFull();
        company.setWidthFull();
        whatsapp.setWidthFull();
        email.setWidthFull();
        accountManager.setWidthFull();
        squad.setWidthFull();
        plan.setWidthFull();
        sector.setWidthFull();
        onboardingMeetingDate.setWidthFull();

        name.setValue(nullSafe(client.getName()));
        company.setValue(nullSafe(client.getCompany()));
        whatsapp.setValue(nullSafe(client.getWhatsapp()));
        email.setValue(nullSafe(client.getEmail()));
        accountManager.setValue(nullSafe(client.getAccountManager()));
        squad.setValue(nullSafe(client.getSquad()));
        plan.setValue(client.getPlan());
        sector.setValue(client.getSector());
        onboardingMeetingDate.setValue(client.getOnboardingMeetingDate());
        onboardingCompleted.setValue(client.isOnboardingCompleted());
        notes.setValue(nullSafe(client.getNotes()));

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(
                name, company,
                whatsapp, email,
                plan, sector,
                squad, accountManager,
                onboardingMeetingDate, onboardingCompleted,
                notes
        );
        formLayout.setColspan(notes, 2);

        Button saveButton = new Button("Salvar", event -> {
            if (name.getValue() == null || name.getValue().isBlank()) {
                Notification.show("Informe o nome do cliente.");
                return;
            }

            boolean wasCompletedBefore = client.isOnboardingCompleted();
            boolean isNewClient = client.getId() == null;

            client.setName(name.getValue());
            client.setCompany(company.getValue());
            client.setWhatsapp(whatsapp.getValue());
            client.setEmail(email.getValue());
            client.setAccountManager(accountManager.getValue());
            client.setPlan(plan.getValue());
            client.setSector(sector.getValue());
            client.setSquad(squad.getValue());
            client.setOnboardingMeetingDate(onboardingMeetingDate.getValue());
            client.setOnboardingCompleted(onboardingCompleted.getValue());
            client.setNotes(notes.getValue());

            Client savedClient = clientRepository.save(client);

            onboardingService.createDefaultPipelineIfNeeded(savedClient);

            if (!wasCompletedBefore && onboardingCompleted.getValue() && onboardingMeetingDate.getValue() != null) {
                Task task = new Task();
                task.setClient(savedClient);
                task.setTaskType("FOLLOW_UP_MEETING");
                task.setTitle("Agendar reunião de acompanhamento - " + savedClient.getName());
                task.setDescription("Tarefa criada automaticamente 30 dias após a reunião de alinhamento.");
                task.setDueDate(onboardingMeetingDate.getValue().plusDays(30));
                task.setCompleted(false);

                taskRepository.save(task);

                if (isNewClient) {
                    Notification.show("Cliente criado, pipeline de onboarding gerado e tarefa automática de acompanhamento criada.");
                } else {
                    Notification.show("Cliente atualizado, pipeline conferido e tarefa automática de acompanhamento criada.");
                }
            } else {
                if (isNewClient) {
                    Notification.show("Cliente criado e pipeline de onboarding gerado com sucesso.");
                } else {
                    Notification.show("Cliente salvo com sucesso.");
                }
            }

            refreshGrid();
            dialog.close();
        });

        Button deleteButton = new Button("Excluir", event -> {
            if (client.getId() != null) {
                clientRepository.delete(client);
                refreshGrid();
                Notification.show("Cliente excluído.");
            }
            dialog.close();
        });

        Button cancelButton = new Button("Cancelar", event -> dialog.close());

        HorizontalLayout actions = new HorizontalLayout(saveButton, deleteButton, cancelButton);

        VerticalLayout content = new VerticalLayout(
                new H2(client.getId() == null ? "Novo cliente" : "Editar cliente"),
                formLayout,
                actions
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        deleteButton.setVisible(client.getId() != null);

        dialog.add(content);
        dialog.open();
    }

    private void refreshGrid() {
        grid.setItems(clientRepository.findAll());
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String valueOrDash(String value) {
        return (value == null || value.isBlank()) ? "—" : value;
    }
}