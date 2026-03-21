package com.agency.dashboard.security;

import com.agency.dashboard.domain.User;
import com.agency.dashboard.service.CurrentUserService;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public abstract class SecureView extends VerticalLayout implements BeforeEnterObserver {

    protected final CurrentUserService currentUserService;

    protected SecureView(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    protected abstract boolean hasAccess(User user);

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User user = currentUserService.getCurrentUser();

        if (user == null) {
            event.forwardTo("login");
            return;
        }

        if (!hasAccess(user)) {
            event.forwardTo("login");
        }
    }
}