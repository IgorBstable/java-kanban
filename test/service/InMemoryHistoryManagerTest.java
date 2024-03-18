package service;

import java.util.LinkedList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import model.*;
import static model.TaskStatus.NEW;

class InMemoryHistoryManagerTest {

    // убедимся, что задачи, добавляемые в HistoryManager,
    // сохраняют предыдущую версию задачи и её данных
    @Test
    void taskVersionsInHistoryManager() {
        Task task = new Task("Test taskVersionsInHistoryManager",
                "Data 01", NEW);
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        task = new Task("Test taskVersionsInHistoryManager",
                "Data 02", NEW);
        historyManager.add(task);

        assertEquals("Data 01", historyManager.getHistory().get(0).getDescription());
        assertEquals("Data 02", historyManager.getHistory().get(1).getDescription());
    }

    // Пример теста из ТЗ
    @Test
    void add() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        HistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.add(task);
        final LinkedList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }
}