package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.Lead;
import com.agency.dashboard.domain.LeadStatus;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.LeadRepository;
import com.agency.dashboard.service.NotificationService;
import com.agency.dashboard.service.OnboardingService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;
import java.util.List;

@Route(value = "sales", layout = MainLayout.class)
@PageTitle("Pipeline Comercial")
public class SalesPipelineView extends VerticalLayout {

    private final LeadRepository leadRepository;
    private final ClientRepository clientRepository;
    private final OnboardingService onboardingService;
    private final NotificationService notificationService;

    private final VerticalLayout leadCol = createCardsContainer();
    private final VerticalLayout diagCol = createCardsContainer();
    private final VerticalLayout propCol = createCardsContainer();
    private final VerticalLayout negoCol = createCardsContainer();
    private final VerticalLayout closedCol = createCardsContainer();
    private final VerticalLayout lostCol = createCardsContainer();

    public SalesPipelineView(
            LeadRepository leadRepository,
            ClientRepository clientRepository,
            OnboardingService onboardingService,
            NotificationService notificationService
    ) {
        this.leadRepository = leadRepository;
        this.clientRepository = clientRepository;
        this.onboardingService = onboardingService;
        this.notificationService = notificationService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H3 title = new H3("Pipeline Comercial");

        Button newLeadButton = new Button("Novo lead", e -> openForm(new Lead()));
        Button refreshButton = new Button("Atualizar", e -> refresh());

        HorizontalLayout top = new HorizontalLayout(title, newLeadButton, refreshButton);
        top.setWidthFull();
        top.setAlignItems(Alignment.CENTER);
        top.expand(title);

        HorizontalLayout board = new HorizontalLayout(
                createColumn("Lead", leadCol),
                createColumn("Diagnóstico", diagCol),
                createColumn("Proposta", propCol),
                createColumn("Negociação", negoCol),
                createColumn("Fechado", closedCol),
                createColumn("Perdido", lostCol)
        );

        board.setSizeFull();
        board.setSpacing(true);

        add(top, board);
        expand(board);

        refresh();
    }

    private VerticalLayout createColumn(String title, VerticalLayout column) {
        Span header = new Span(title);
        header.getStyle().set("font-weight", "700");

        VerticalLayout wrapper = new VerticalLayout(header, column);
        wrapper.setWidth("16.6%");
        wrapper.setHeightFull();
        wrapper.setPadding(true);
        wrapper.setSpacing(true);

        wrapper.getStyle()
                .set("background", "var(--lumo-contrast-5pct)")
                .set("border-radius", "12px")
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

    private void refresh() {
        leadCol.removeAll();
        diagCol.removeAll();
        propCol.removeAll();
        negoCol.removeAll();
        closedCol.removeAll();
        lostCol.removeAll();

        List<Lead> leads = leadRepository.findAll().stream()
                .sorted(Comparator.comparing(Lead::getCreatedAt).reversed())
                .toList();

        for (Lead lead : leads) {
            Div card = createCard(lead);

            switch (lead.getStatus()) {
                case LEAD -> leadCol.add(card);
                case DIAGNOSTICO -> diagCol.add(card);
                case PROPOSTA -> propCol.add(card);
                case NEGOCIACAO -> negoCol.add(card);
                case FECHADO -> closedCol.add(card);
                case PERDIDO -> lostCol.add(card);
            }
        }
    }

    private Div createCard(Lead lead) {
        Div card = new Div();

        card.getStyle()
                .set("background", "white")
                .set("padding", "12px")
                .set("border-radius", "10px")
                .set("box-shadow", "var(--lumo-box-shadow-xs)")
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("cursor", "pointer");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        Span name = new Span(lead.getName() != null ? lead.getName() : "Sem nome");
        name.getStyle().set("font-weight", "700");

        Span company = new Span("Empresa: " + valueOrDash(lead.getCompany()));
        Span phone = new Span("Telefone: " + valueOrDash(lead.getPhone()));
        Span responsible = new Span("Responsável: " + valueOrDash(lead.getResponsible()));
        Span value = new Span("Proposta: " + (lead.getProposalValue() != null ? "R$ " + lead.getProposalValue() : "—"));

        company.getStyle().set("font-size", "12px");
        phone.getStyle().set("font-size", "12px");
        responsible.getStyle().set("font-size", "12px");
        value.getStyle().set("font-size", "12px");

        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);

        Button backButton = new Button("←", e -> moveBack(lead));
        Button editButton = new Button("Editar", e -> openForm(lead));
        Button nextButton = new Button("→", e -> moveNext(lead));
        Button lostButton = new Button("Perdido", e -> markAsLost(lead));

        backButton.setEnabled(lead.getStatus() != LeadStatus.LEAD && lead.getStatus() != LeadStatus.PERDIDO);
        nextButton.setEnabled(lead.getStatus() != LeadStatus.FECHADO && lead.getStatus() != LeadStatus.PERDIDO);
        lostButton.setEnabled(lead.getStatus() != LeadStatus.FECHADO && lead.getStatus() != LeadStatus.PERDIDO);

        actions.add(backButton, editButton, nextButton, lostButton);

        content.add(name, company, phone, responsible, value, actions);
        card.add(content);

        card.addClickListener(e -> openForm(lead));

        return card;
    }

    private void openForm(Lead lead) {
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");

        TextField name = new TextField("Nome");
        TextField company = new TextField("Empresa");
        TextField phone = new TextField("Telefone");
        TextField instagram = new TextField("Instagram");
        TextField source = new TextField("Origem");
        TextField responsible = new TextField("Responsável");
        NumberField proposalValue = new NumberField("Valor da proposta");
        TextArea notes = new TextArea("Observações");

        name.setWidthFull();
        company.setWidthFull();
        phone.setWidthFull();
        instagram.setWidthFull();
        source.setWidthFull();
        responsible.setWidthFull();
        proposalValue.setWidthFull();
        notes.setWidthFull();
        notes.setMinHeight("140px");

        name.setValue(nullSafe(lead.getName()));
        company.setValue(nullSafe(lead.getCompany()));
        phone.setValue(nullSafe(lead.getPhone()));
        instagram.setValue(nullSafe(lead.getInstagram()));
        source.setValue(nullSafe(lead.getSource()));
        responsible.setValue(nullSafe(lead.getResponsible()));
        proposalValue.setValue(lead.getProposalValue());
        notes.setValue(nullSafe(lead.getNotes()));

        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        formLayout.add(
                name, company,
                phone, instagram,
                source, responsible,
                proposalValue, notes
        );
        formLayout.setColspan(notes, 2);

        Button saveButton = new Button("Salvar", e -> {
            if (name.getValue() == null || name.getValue().isBlank()) {
                Notification.show("Informe o nome do lead.");
                return;
            }

            boolean isNewLead = lead.getId() == null;

            lead.setName(name.getValue());
            lead.setCompany(company.getValue());
            lead.setPhone(phone.getValue());
            lead.setInstagram(instagram.getValue());
            lead.setSource(source.getValue());
            lead.setResponsible(responsible.getValue());
            lead.setProposalValue(proposalValue.getValue());
            lead.setNotes(notes.getValue());

            if (lead.getStatus() == null) {
                lead.setStatus(LeadStatus.LEAD);
            }

            Lead savedLead = leadRepository.save(lead);

            if (isNewLead) {
                notificationService.createNotification(
                        "Novo lead cadastrado",
                        "Lead " + savedLead.getName() + " foi cadastrado no pipeline comercial.",
                        "COMERCIAL",
                        savedLead.getResponsible(),
                        "LEAD",
                        savedLead.getId()
                );
            }

            Notification.show("Lead salvo com sucesso.");
            dialog.close();
            refresh();
        });

        Button deleteButton = new Button("Excluir", e -> {
            if (lead.getId() != null) {
                leadRepository.delete(lead);
                Notification.show("Lead excluído.");
                refresh();
            }
            dialog.close();
        });

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        deleteButton.setVisible(lead.getId() != null);

        HorizontalLayout actions = new HorizontalLayout(saveButton, deleteButton, cancelButton);

        VerticalLayout content = new VerticalLayout(
                new H3(lead.getId() == null ? "Novo lead" : "Editar lead"),
                formLayout,
                actions
        );
        content.setPadding(false);
        content.setSpacing(true);
        content.setWidthFull();

        dialog.add(content);
        dialog.open();
    }

    private void moveNext(Lead lead) {
        switch (lead.getStatus()) {
            case LEAD -> lead.setStatus(LeadStatus.DIAGNOSTICO);
            case DIAGNOSTICO -> lead.setStatus(LeadStatus.PROPOSTA);
            case PROPOSTA -> lead.setStatus(LeadStatus.NEGOCIACAO);
            case NEGOCIACAO -> {
                lead.setStatus(LeadStatus.FECHADO);
                createClientFromLead(lead);

                notificationService.createNotification(
                        "Lead fechado",
                        "O lead " + lead.getName() + " foi fechado e virou cliente.",
                        "GESTAO",
                        null,
                        "LEAD",
                        lead.getId()
                );
            }
            case FECHADO, PERDIDO -> {
                return;
            }
        }

        leadRepository.save(lead);
        Notification.show("Lead atualizado.");
        refresh();
    }

    private void moveBack(Lead lead) {
        switch (lead.getStatus()) {
            case LEAD, PERDIDO -> {
                return;
            }
            case DIAGNOSTICO -> lead.setStatus(LeadStatus.LEAD);
            case PROPOSTA -> lead.setStatus(LeadStatus.DIAGNOSTICO);
            case NEGOCIACAO -> lead.setStatus(LeadStatus.PROPOSTA);
            case FECHADO -> lead.setStatus(LeadStatus.NEGOCIACAO);
        }

        leadRepository.save(lead);
        Notification.show("Lead atualizado.");
        refresh();
    }

    private void markAsLost(Lead lead) {
        lead.setStatus(LeadStatus.PERDIDO);
        leadRepository.save(lead);
        Notification.show("Lead marcado como perdido.");
        refresh();
    }

    private void createClientFromLead(Lead lead) {
        Client client = new Client();

        client.setName(lead.getName());
        client.setCompany(lead.getCompany());
        client.setWhatsapp(lead.getPhone());

        Client savedClient = clientRepository.save(client);

        onboardingService.createDefaultPipelineIfNeeded(savedClient);

        notificationService.createNotification(
                "Novo cliente criado",
                "Cliente " + savedClient.getName() + " foi criado automaticamente a partir do comercial.",
                "TRAFEGO",
                null,
                "CLIENT",
                savedClient.getId()
        );

        Notification.show("Cliente criado automaticamente e onboarding iniciado.");
    }

    private String nullSafe(String value) {
        return value == null ? "" : value;
    }

    private String valueOrDash(String value) {
        return value == null || value.isBlank() ? "—" : value;
    }
}