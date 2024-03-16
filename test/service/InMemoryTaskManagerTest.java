package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    // проверяем, что InMemoryTaskManager действительно
    // добавляет задачи разного типа и может найти их по id;
    @Test
    void addTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task = new Task("Test addTasks",
                "Test addTasks description", NEW);
        inMemoryTaskManager.makeNewTask(task);
        Epic epic = new Epic("Test addTasks",
                "Test addTasks description");
        inMemoryTaskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask("Test addTasks",
                "Test addTasks description", NEW, epic.getId());
        inMemoryTaskManager.makeNewSubtask(subtask);

        assertEquals(task, inMemoryTaskManager.getTaskById(task.getId()));
        assertEquals(epic, inMemoryTaskManager.getEpicById(epic.getId()));
        assertEquals(subtask, inMemoryTaskManager.getSubtaskById(subtask.getId()));
    }
}