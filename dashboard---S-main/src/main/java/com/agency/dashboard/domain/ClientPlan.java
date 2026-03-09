package com.agency.dashboard.domain;

public enum ClientPlan {

    START_1("Start 1"),
    START_2("Start 2"),
    START_3("Start 3"),
    START_4("Start 4");

    private final String label;

    ClientPlan(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}