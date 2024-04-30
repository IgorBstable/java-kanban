package model;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static model.TaskStatus.*;
import service.*;


class TaskTest {

    // проверим, что экземпляры класса Task равны друг другу, если равен их id
    @Test
    void taskEqualsIfIdEquals() {
        Task task = new Task(0, "Test taskEqualsIfIdEquals",
                "Test taskEqualsIfIdEquals description", NEW, TaskTypes.TASK);
        TaskManager taskManager = Managers.getDefault();
        taskManager.makeNewTask(task);
        int taskId = task.getId();

        assertEquals(taskManager.getTaskById(taskId), taskManager.getTaskById(taskId),
                "Задачи с одинаковым id не совпадают");
    }

    // Проверяем, что задачи с заданным id и сгенерированным id
    // не конфликтуют внутри менеджера.
    @Test
    void ShouldNotConflictWhenSetIdAndGenerateId() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task(0, "Test ShouldNotConflictWhenSetIdAndGenerateId",
                "task", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task);
        int id = task.getId();
        Task task1 = new Task(0, "Test ShouldNotConflictWhenSetIdAndGenerateId",
                "task1", NEW, TaskTypes.TASK);
        task1.setId(id);
        taskManager.makeNewTask(task1);

        assertEquals(taskManager.getTaskById(id), task);
        assertNotEquals(taskManager.getTaskById(id), task1);

        // Менеджер при создании объекта Task сам присваивает id. Поэтому конфликтов не будет.
    }


    // Пример теста из ТЗ
    @Test
    void addNewTask() {
        Task task = new Task(0, "Test addNewTask",
                "Test addNewTask description", NEW, TaskTypes.TASK);
        TaskManager taskManager = Managers.getDefault();
        taskManager.makeNewTask(task);
        int taskId = task.getId();

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }
}