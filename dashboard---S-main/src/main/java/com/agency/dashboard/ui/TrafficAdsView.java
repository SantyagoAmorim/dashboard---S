package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Client;
import com.agency.dashboard.domain.TrafficAd;
import com.agency.dashboard.repo.ClientRepository;
import com.agency.dashboard.repo.TrafficAdRepository;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.*;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "traffic-ads", layout = MainLayout.class)
@PageTitle("Anúncios | Creative Ops")
public class TrafficAdsView extends VerticalLayout {

    private final TrafficAdRepository adRepository;
    private final ClientRepository clientRepository;
    private final Grid<TrafficAd> grid = new Grid<>(TrafficAd.class, false);

    public TrafficAdsView(TrafficAdRepository adRepository, ClientRepository clientRepository) {
        this.adRepository = adRepository;
        this.clientRepository = clientRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(buildHeader());
        add(buildGrid());

        configureGrid();
        refresh();
    }

    private Component buildHeader() {
        H2 title = new H2("Gestão de Anúncios");

        Button newAd = new Button("Novo anúncio", e -> openForm(new TrafficAd()));
        newAd.getStyle()
                .set("background", "var(--lumo-primary-color)")
                .set("color", "white");

        HorizontalLayout header = new HorizontalLayout(title, newAd);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.CENTER);

        return header;
    }

    private Component buildGrid() {
        VerticalLayout wrapper = new VerticalLayout(new H2("Lista de anúncios"), grid);

        wrapper.setSizeFull();
        wrapper.getStyle()
                .set("border", "1px solid var(--lumo-contrast-10pct)")
                .set("border-radius", "12px");

        return wrapper;
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(ad -> valueOrDash(ad.getName()))
                .setHeader("Anúncio");

        grid.addColumn(ad -> ad.getClient() != null ? ad.getClient().getName() : "—")
                .setHeader("Cliente");

        grid.addColumn(ad -> valueOrDash(ad.getPlatform()))
                .setHeader("Plataforma");

        grid.addColumn(ad -> valueOrDash(ad.getStatus()))
                .setHeader("Status");

        grid.addColumn(ad -> valueOrDash(ad.getObjective()))
                .setHeader("Objetivo");

        grid.addItemDoubleClickListener(e -> openForm(e.getItem()));
    }

    private void openForm(TrafficAd ad) {
        Dialog dialog = new Dialog();
        dialog.setWidth("700px");

        TextField name = new TextField("Nome do anúncio");
        ComboBox<Client> client = new ComboBox<>("Cliente");
        TextField platform = new TextField("Plataforma");
        TextField status = new TextField("Status");
        TextField objective = new TextField("Objetivo");
        TextArea notes = new TextArea("Observações");

        client.setItems(clientRepository.findAll());
        client.setItemLabelGenerator(Client::getName);

        name.setWidthFull();
        client.setWidthFull();
        platform.setWidthFull();
        status.setWidthFull();
        objective.setWidthFull();
        notes.setWidthFull();

        name.setValue(nullSafe(ad.getName()));
        client.setValue(ad.getClient());
        platform.setValue(nullSafe(ad.getPlatform()));
        status.setValue(nullSafe(ad.getStatus()));
        objective.setValue(nullSafe(ad.getObjective()));
        notes.setValue(nullSafe(ad.getNotes()));

        FormLayout form = new FormLayout(
                name, client,
                platform, status,
                objective, notes
        );

        Button save = new Button("Salvar", e -> {
            if (name.getValue().isBlank()) {
                Notification.show("Nome obrigatório");
                return;
            }

            ad.setName(name.getValue());
            ad.setClient(client.getValue());
            ad.setPlatform(platform.getValue());
            ad.setStatus(status.getValue());
            ad.setObjective(objective.getValue());
            ad.setNotes(notes.getValue());

            adRepository.save(ad);

            Notification.show("Anúncio salvo");
            dialog.close();
            refresh();
        });

        Button delete = new Button("Excluir", e -> {
            if (ad.getId() != null) {
                adRepository.delete(ad);
                Notification.show("Excluído");
                refresh();
            }
            dialog.close();
        });

        delete.setVisible(ad.getId() != null);

        HorizontalLayout actions = new HorizontalLayout(save, delete);

        VerticalLayout content = new VerticalLayout(
                new H2("Anúncio"),
                form,
                actions
        );

        dialog.add(content);
        dialog.open();
    }

    private void refresh() {
        grid.setItems(adRepository.findAll());
    }

    private String nullSafe(String v) {
        return v == null ? "" : v;
    }

    private String valueOrDash(String v) {
        return (v == null || v.isBlank()) ? "—" : v;
    }
}