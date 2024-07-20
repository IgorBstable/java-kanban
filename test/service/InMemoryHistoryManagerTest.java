package service;

import java.time.Duration;
import java.time.LocalDateTime;
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
        Task task = new Task(0, "Test taskVersionsInHistoryManager",
                "Data 01", NEW, TaskTypes.TASK);
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
        Task task = new Task(0, "Test addNewTask",
                "Test addNewTask description", NEW, TaskTypes.TASK);
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
        Task task = new Task(0, "Test linkedListTest",
                "Test linkedListTest description", NEW, TaskTypes.TASK);
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        assertEquals(List.of(task), taskManager.getHistory());
    }

    // Пустая история задач
    @Test
    void historyIsEmpty() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    // Дублирование
    @Test
    void noDouble() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task(0, "Test remove #1",
                "Test remove description #1", NEW, TaskTypes.TASK);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task1);
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        assertEquals(List.of(task1), taskManager.getHistory());
    }

    // Проверяем операцию удаления (начало)
    @Test
    void removeFirst() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task(0, "Test remove #1",
                "Test remove description #1", NEW, TaskTypes.TASK);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task1);
        Task task2 = new Task(0, "Test remove #2",
                "Test remove description #2", NEW, TaskTypes.TASK);
        task2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        task2.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.deleteTaskById(1);
        assertEquals(List.of(task2), taskManager.getHistory());
    }

    // Проверяем операцию удаления (конец)
    @Test
    void removeLast() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task(0, "Test remove #1",
                "Test remove description #1", NEW, TaskTypes.TASK);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task1);
        Task task2 = new Task(0, "Test remove #2",
                "Test remove description #2", NEW, TaskTypes.TASK);
        task2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        task2.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.deleteTaskById(2);
        assertEquals(List.of(task1), taskManager.getHistory());
    }

    // Проверяем операцию удаления (середина)
    @Test
    void removeMiddle() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task(0, "Test remove #1",
                "Test remove description #1", NEW, TaskTypes.TASK);
        task1.setStartTime(LocalDateTime.now());
        task1.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task1);
        Task task2 = new Task(0, "Test remove #2",
                "Test remove description #2", NEW, TaskTypes.TASK);
        task2.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(10)));
        task2.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task2);
        Task task3 = new Task(0, "Test remove #2",
                "Test remove description #2", NEW, TaskTypes.TASK);
        task3.setStartTime(LocalDateTime.now().plus(Duration.ofMinutes(20)));
        task3.setDuration(Duration.ofMinutes(5));
        taskManager.makeNewTask(task3);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.deleteTaskById(2);
        assertEquals(List.of(task1, task3), taskManager.getHistory());
    }


}