package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.CreativeRequest;
import com.agency.dashboard.domain.RequestStatus;
import com.agency.dashboard.domain.RequestType;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.CreativeRequestRepository;
import com.agency.dashboard.service.CurrentUserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDateTime;

@Route(value = "requests", layout = MainLayout.class)
@PageTitle("Pedidos | Creative Ops")
public class RequestsView extends VerticalLayout implements BeforeEnterObserver {

    private final CreativeRequestRepository repo;
    private final ClientRepository clientRepo;
    private final CurrentUserService currentUserService;

    private final Grid<CreativeRequest> grid = new Grid<>(CreativeRequest.class, false);

    public RequestsView(CreativeRequestRepository repo,
                        ClientRepository clientRepo,
                        CurrentUserService currentUserService) {
        this.repo = repo;
        this.clientRepo = clientRepo;
        this.currentUserService = currentUserService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Pedidos de criativos"));

        Button newBtn = new Button("Novo pedido", e -> openForm(new CreativeRequest()));
        add(newBtn);

        configureGrid();
        add(grid);

        refresh();
    }

    private void configureGrid() {
        grid.addColumn(r -> r.getClient().getName()).setHeader("Cliente").setAutoWidth(true);
        grid.addColumn(r -> r.getType().name()).setHeader("Tipo").setAutoWidth(true);
        grid.addColumn(r -> r.getStatus().name()).setHeader("Status").setAutoWidth(true);
        grid.addColumn(r -> r.getTitle() == null ? "-" : r.getTitle()).setHeader("Título").setFlexGrow(1);
        grid.addColumn(r -> r.getCreatedAt().toLocalDate()).setHeader("Criado").setAutoWidth(true);
        grid.addColumn(r -> r.getDeliveredAt() == null ? "-" : r.getDeliveredAt().toLocalDate().toString())
                .setHeader("Entregue").setAutoWidth(true);

        grid.addItemClickListener(e -> openForm(e.getItem()));
        grid.setSizeFull();
    }

    private void refresh() {
        grid.setItems(repo.findTop50ByOrderByCreatedAtDesc());
    }

    private void openForm(CreativeRequest model) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(model.getId() == null ? "Novo pedido" : "Editar pedido #" + model.getId());
        dialog.setWidth("720px");

        ComboBox<Client> client = new ComboBox<>("Cliente");
        client.setItems(clientRepo.findAll());
        client.setItemLabelGenerator(Client::getName);
        client.setClearButtonVisible(true);
        client.setValue(model.getClient());

        ComboBox<RequestType> type = new ComboBox<>("Tipo");
        type.setItems(RequestType.values());
        type.setClearButtonVisible(true);
        type.setValue(model.getType());

        ComboBox<RequestStatus> status = new ComboBox<>("Status");
        status.setItems(RequestStatus.values());
        status.setClearButtonVisible(true);
        status.setValue(model.getStatus());

        TextField title = new TextField("Título");
        title.setValue(model.getTitle() == null ? "" : model.getTitle());

        TextArea desc = new TextArea("Descrição");
        desc.setValue(model.getDescription() == null ? "" : model.getDescription());
        desc.setHeight("140px");

        FormLayout form = new FormLayout(client, type, status, title, desc);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("620px", 2)
        );
        form.setColspan(desc, 2);

        Button save = new Button("Salvar", e -> {
            if (client.getValue() == null || type.getValue() == null) {
                Notification.show("Preencha Cliente e Tipo.");
                return;
            }

            model.setClient(client.getValue());
            model.setType(type.getValue());
            model.setTitle(title.getValue().isBlank() ? null : title.getValue());
            model.setDescription(desc.getValue().isBlank() ? null : desc.getValue());

            RequestStatus previous = model.getStatus();
            model.setStatus(status.getValue() == null ? RequestStatus.ABERTO : status.getValue());

            if (previous != RequestStatus.ENTREGUE && model.getStatus() == RequestStatus.ENTREGUE) {
                model.setDeliveredAt(LocalDateTime.now());
            }

            if (model.getStatus() != RequestStatus.ENTREGUE) {
                model.setDeliveredAt(null);
            }

            repo.save(model);
            dialog.close();
            refresh();
            Notification.show("Salvo!");
        });

        Button delete = new Button("Excluir", e -> {
            if (model.getId() != null) {
                repo.deleteById(model.getId());
                dialog.close();
                refresh();
                Notification.show("Excluído!");
            }
        });
        delete.getStyle().set("color", "var(--lumo-error-text-color)");

        HorizontalLayout footer = new HorizontalLayout(save, delete);

        dialog.add(form);
        dialog.getFooter().add(footer);

        dialog.open();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (!currentUserService.isLoggedIn()) {
            event.forwardTo(LoginView.class);
        }
    }
}