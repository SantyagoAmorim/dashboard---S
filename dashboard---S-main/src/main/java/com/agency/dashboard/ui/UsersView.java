package com.agency.dashboard.ui;

import com.agency.dashboard.domain.User;
import com.agency.dashboard.domain.UserRole;
import com.agency.dashboard.repo.UserRepository;
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
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Usuários | Creative Ops")
public class UsersView extends VerticalLayout implements BeforeEnterObserver {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    private final Grid<User> grid = new Grid<>(User.class, false);

    public UsersView(UserRepository userRepository, CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Usuários"));

        Button newBtn = new Button("Novo usuário", e -> openForm(new User()));
        add(newBtn);

        configureGrid();
        add(grid);

        refresh();
    }

    private void configureGrid() {
        grid.addColumn(User::getName).setHeader("Nome").setAutoWidth(true);
        grid.addColumn(User::getEmail).setHeader("E-mail").setAutoWidth(true);
        grid.addColumn(user -> user.getRole() != null ? user.getRole().name() : "-").setHeader("Role").setAutoWidth(true);
        grid.addColumn(user -> user.getSector() != null ? user.getSector() : "-").setHeader("Setor").setAutoWidth(true);
        grid.addColumn(user -> user.getSquad() != null ? user.getSquad() : "-").setHeader("Squad").setAutoWidth(true);

        grid.addItemClickListener(event -> openForm(event.getItem()));
        grid.setSizeFull();
    }

    private void refresh() {
        grid.setItems(userRepository.findAll());
    }

    private void openForm(User model) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(model.getId() == null ? "Novo usuário" : "Editar usuário");
        dialog.setWidth("720px");

        TextField name = new TextField("Nome");
        name.setWidthFull();
        name.setValue(model.getName() == null ? "" : model.getName());

        EmailField email = new EmailField("E-mail");
        email.setWidthFull();
        email.setValue(model.getEmail() == null ? "" : model.getEmail());

        PasswordField password = new PasswordField("Senha");
        password.setWidthFull();

        ComboBox<UserRole> role = new ComboBox<>("Role");
        role.setItems(UserRole.values());
        role.setWidthFull();
        role.setValue(model.getRole());

        TextField sector = new TextField("Setor");
        sector.setWidthFull();
        sector.setValue(model.getSector() == null ? "" : model.getSector());

        TextField squad = new TextField("Squad");
        squad.setWidthFull();
        squad.setValue(model.getSquad() == null ? "" : model.getSquad());

        FormLayout form = new FormLayout(name, email, password, role, sector, squad);
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("700px", 2)
        );

        Button save = new Button("Salvar", e -> {
            if (name.getValue().isBlank() || email.getValue().isBlank() || role.getValue() == null) {
                Notification.show("Preencha nome, e-mail e role.");
                return;
            }

            model.setName(name.getValue().trim());
            model.setEmail(email.getValue().trim());
            model.setRole(role.getValue());
            model.setSector(sector.getValue().isBlank() ? null : sector.getValue().trim());
            model.setSquad(squad.getValue().isBlank() ? null : squad.getValue().trim());

            if (!password.getValue().isBlank()) {
                model.setPassword(password.getValue());
            } else if (model.getId() == null) {
                Notification.show("Informe uma senha.");
                return;
            }

            userRepository.save(model);
            dialog.close();
            refresh();
            Notification.show("Usuário salvo!");
        });

        Button delete = new Button("Excluir", e -> {
            if (model.getId() != null) {
                userRepository.deleteById(model.getId());
                dialog.close();
                refresh();
                Notification.show("Usuário excluído!");
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
            return;
        }

        User currentUser = currentUserService.getCurrentUser();
        if (currentUser.getRole() != UserRole.ADMIN && currentUser.getRole() != UserRole.MANAGEMENT) {
            Notification.show("Você não tem permissão para acessar esta tela.");
            event.forwardTo(DashboardView.class);
        }
    }
}