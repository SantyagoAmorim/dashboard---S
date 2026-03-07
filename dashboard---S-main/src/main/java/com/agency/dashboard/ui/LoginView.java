package com.agency.dashboard.ui;

import com.agency.dashboard.service.CurrentUserService;
import com.agency.dashboard.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Login | Creative Ops")
public class LoginView extends VerticalLayout {

    public LoginView(UserService userService, CurrentUserService currentUserService) {
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        H2 title = new H2("Creative Ops Dashboard");

        LoginForm loginForm = new LoginForm();
        loginForm.setForgotPasswordButtonVisible(false);

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Entrar");
        i18n.getForm().setUsername("E-mail");
        i18n.getForm().setPassword("Senha");
        i18n.getForm().setSubmit("Acessar");
        i18n.getErrorMessage().setTitle("Login inválido");
        i18n.getErrorMessage().setMessage("Confira seu e-mail e senha.");
        loginForm.setI18n(i18n);

        loginForm.addLoginListener(event -> {
            var userOpt = userService.authenticate(event.getUsername(), event.getPassword());

            if (userOpt.isPresent()) {
                currentUserService.login(userOpt.get());
                UI.getCurrent().navigate("");
            } else {
                loginForm.setError(true);
            }
        });

        add(title, loginForm);
    }
}