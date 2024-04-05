package service;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.*;
import static model.TaskStatus.NEW;

class InMemoryHistoryManagerTest {

    // убедимся, что задачи, добавляемые в HistoryManager,
    // не сохраняют предыдущую версию задачи и её данных
    @Test
    void taskVersionsInHistoryManager() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Test taskVersionsInHistoryManager",
                "Data 01", NEW);
        inMemoryTaskManager.makeNewTask(task);
        historyManager.add(task);
        task.setDescription("Data 02");
        historyManager.add(task);

        assertEquals(List.of(task), historyManager.getHistory());
        assertEquals("Data 02", historyManager.getHistory().getFirst().getDescription());
    }

    // Пример теста из ТЗ
    @Test
    void add() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        inMemoryTaskManager.makeNewTask(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    // Проверяем встроенный связный список версий
    // (реализация метода getHistory перекладывает задачи из
    // связного списка в ArrayList для формирования ответа).
    // Также проверяем операцию добавления.
    @Test
    void linkedListTest() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test linkedListTest",
                "Test linkedListTest description", NEW);
        taskManager.makeNewTask(task);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        assertEquals(List.of(task), taskManager.getHistoryManager());
    }

    // Проверяем операцию удаления
    @Test
    void remove() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Test remove #1",
                "Test remove description #1", NEW);
        taskManager.makeNewTask(task1);
        Task task2 = new Task("Test remove #2",
                "Test remove description #2", NEW);
        taskManager.makeNewTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.deleteTaskById(1);
        assertEquals(List.of(task2), taskManager.getHistoryManager());
    }

}