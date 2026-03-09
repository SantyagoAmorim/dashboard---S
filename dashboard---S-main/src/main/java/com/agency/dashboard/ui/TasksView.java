package com.agency.dashboard.ui;

import com.agency.dashboard.domain.Task;
import com.agency.dashboard.repo.TaskRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Comparator;

@Route(value = "tasks", layout = MainLayout.class)
@PageTitle("Tarefas | Creative Ops")
public class TasksView extends VerticalLayout {

    private final TaskRepository taskRepository;
    private final Grid<Task> grid = new Grid<>(Task.class, false);
    private final Checkbox showCompleted = new Checkbox("Mostrar concluídas");

    public TasksView(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Tarefas do Tráfego");

        Button refreshButton = new Button("Atualizar", event -> refreshGrid());

        showCompleted.addValueChangeListener(event -> refreshGrid());

        HorizontalLayout header = new HorizontalLayout(title, showCompleted, refreshButton);
        header.setWidthFull();
        header.setAlignItems(Alignment.CENTER);
        header.expand(title);

        configureGrid();

        add(header, grid);
        refreshGrid();
    }

    private void configureGrid() {
        grid.setSizeFull();

        grid.addColumn(Task::getTitle)
                .setHeader("Título")
                .setAutoWidth(true)
                .setFlexGrow(1);

        grid.addColumn(task -> task.getClient() != null ? task.getClient().getName() : "—")
                .setHeader("Cliente")
                .setAutoWidth(true);

        grid.addColumn(task -> task.getTaskType() != null ? task.getTaskType() : "—")
                .setHeader("Tipo")
                .setAutoWidth(true);

        grid.addColumn(task -> task.getDueDate() != null ? task.getDueDate().toString() : "—")
                .setHeader("Vencimento")
                .setAutoWidth(true);

        grid.addColumn(task -> task.isCompleted() ? "Concluída" : "Pendente")
                .setHeader("Status")
                .setAutoWidth(true);

        grid.addComponentColumn(task -> {
            Button toggleButton = new Button(task.isCompleted() ? "Reabrir" : "Concluir");

            toggleButton.addClickListener(event -> {
                task.setCompleted(!task.isCompleted());
                taskRepository.save(task);

                Notification.show(task.isCompleted()
                        ? "Tarefa marcada como concluída."
                        : "Tarefa reaberta.");

                refreshGrid();
            });

            return toggleButton;
        }).setHeader("Ações").setAutoWidth(true);
    }

    private void refreshGrid() {
        var tasks = taskRepository.findAll().stream()
                .filter(task -> showCompleted.getValue() || !task.isCompleted())
                .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        grid.setItems(tasks);
    }
}