package service;

import model.Task;
import org.junit.jupiter.api.Test;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    // Проверяем, что утилитарный класс всегда возвращает
    // проинициализированные и готовые к работе экземпляры менеджеров
    // (проверка сделана исходя из тезиса о том, что проинициализированный
    // и готовый к работе экземпляр менеджера должен быть способен исполнять методы
    // класса (в данном случае для примера взял метод makeId()).
    @Test
    void newManager() {
        TaskManager manager = Managers.getDefault();

        assertEquals(1, manager.makeId());
    }

    // проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void taskEqualsAfterAdd() {
        TaskManager manager = Managers.getDefault();
        Task task = new Task("Test taskEqualsAfterAdd",
                "Test taskEqualsAfterAdd description", NEW);
        manager.makeNewTask(task);

        assertEquals("Test taskEqualsAfterAdd", task.getName());
        assertEquals("Test taskEqualsAfterAdd description", task.getDescription());
        assertEquals(NEW, task.getStatus());
        assertEquals(1, task.getId());
    }
}