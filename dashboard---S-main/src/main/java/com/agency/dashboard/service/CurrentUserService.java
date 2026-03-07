package com.agency.dashboard.service;

import com.agency.dashboard.domain.User;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    private static final String SESSION_USER_KEY = "loggedUser";

    public void login(User user) {
        VaadinSession.getCurrent().setAttribute(SESSION_USER_KEY, user);
    }

    public void logout() {
        VaadinSession.getCurrent().setAttribute(SESSION_USER_KEY, null);
    }

    public User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(SESSION_USER_KEY);
    }

    public boolean isLoggedIn() {
        return getCurrentUser() != null;
    }
}