package service;

import model.Task;
import model.TaskTypes;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    // Проверяем, что утилитарный класс всегда возвращает
    // проинициализированные и готовые к работе экземпляры менеджеров
    // (проверка сделана исходя из тезиса о том, что проинициализированный
    // и готовый к работе экземпляр менеджера должен быть способен исполнять методы
    // класса (в данном случае для примера взял метод makeNewTask)).
    @Test
    void newManager() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task(0, "Test newManager",
                "Test newManager description", NEW, TaskTypes.TASK);
        manager.makeNewTask(task);
        assertEquals(List.of(task), manager.getAllTasks());
    }

    // проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void taskEqualsAfterAdd() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task(0, "Test taskEqualsAfterAdd",
                "Test taskEqualsAfterAdd description", NEW, TaskTypes.TASK);
        manager.makeNewTask(task);

        assertEquals("Test taskEqualsAfterAdd", task.getName());
        assertEquals("Test taskEqualsAfterAdd description", task.getDescription());
        assertEquals(NEW, task.getStatus());
        assertEquals(1, task.getId());
    }
}