package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.util.List;

import static model.TaskStatus.IN_PROGRESS;
import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    // проверяем, что InMemoryTaskManager действительно
    // добавляет задачи разного типа и может найти их по id;
    @Test
    void addTasks() {
        TaskManager inMemoryTaskManager = Managers.getDefault();
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

    // Тестируем функции менеджера задач, которые реализованы в предыдущих спринтах
    @Test
    void updateTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test updateTask",
                "Test updateTask description", NEW);
        taskManager.makeNewTask(task);
        Task task1 = new Task("Test addTasks #1",
                "Test addTasks description #1", NEW);
        taskManager.makeNewTask(task1);
        task1.setId(1);
        taskManager.updateTask(task1);
        assertEquals("Test addTasks #1", taskManager.getTaskById(1).getName());
    }

    @Test
    void deleteTasks() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Test deleteTasks",
                "Test deleteTasks description", NEW);
        taskManager.makeNewTask(task);
        Task task1 = new Task("Test deleteTasks #1",
                "Test deleteTasks description #1", NEW);
        taskManager.makeNewTask(task1);
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void updateSubTask() {
        TaskManager taskManager = Managers.getDefault();
        Task epic = new Epic("Test updateSubTask",
                "Test updateSubTask description");
        taskManager.makeNewEpic(epic);
        Task subtask = new Subtask("Test updateSubTask",
                "Test updateSubTask description", NEW, 1);
        taskManager.makeNewSubtask(subtask);
        Task subtask1 = new Subtask("Test updateSubTask #1",
                "Test updateSubTask description #1", NEW, 1);
        subtask1.setId(2);
        taskManager.updateSubTask(subtask1);
        assertEquals("Test updateSubTask #1", taskManager.getSubtaskById(2).getName());
    }

    @Test
    void delSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Task epic = new Epic("Test delSubtasks",
                "Test delSubtasks description");
        taskManager.makeNewEpic(epic);
        Task subtask = new Subtask("Test delSubtasks",
                "Test delSubtasks description", NEW, 1);
        taskManager.makeNewSubtask(subtask);
        Task subtask1 = new Subtask("Test delSubtasks #1",
                "Test delSubtasks description #1", NEW, 1);
        taskManager.makeNewSubtask(subtask1);
        taskManager.delSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void deleteEpics() {
        TaskManager taskManager = Managers.getDefault();
        Task epic = new Epic("Test deleteEpics",
                "Test deleteEpics description");
        taskManager.makeNewEpic(epic);
        Task epic1 = new Epic("Test deleteEpics #1",
                "Test deleteEpics description #1");
        taskManager.makeNewEpic(epic1);
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void delEpicById() {
        TaskManager taskManager = Managers.getDefault();
        Task epic = new Epic("Test delEpicById",
                "Test delEpicById description");
        taskManager.makeNewEpic(epic);
        Task epic1 = new Epic("Test delEpicById #1",
                "Test delEpicById description #1");
        taskManager.makeNewEpic(epic1);
        taskManager.delEpicById(1);
        assertEquals(List.of(epic1), taskManager.getAllEpics());
    }

    // Удаляемые подзадачи не должны хранить внутри себя старые id
    @Test
    void subtaskShouldNotSaveOldId() {
        TaskManager taskManager = Managers.getDefault();
        Task epic = new Epic("Test subtaskShouldNotSaveOldId",
                "Test subtaskShouldNotSaveOldId description");
        taskManager.makeNewEpic(epic);
        Task subtask = new Subtask("Test subtaskShouldNotSaveOldId",
                "Test subtaskShouldNotSaveOldId description", NEW, epic.getId());
        taskManager.makeNewSubtask(subtask);
        taskManager.delSubtaskById(2);
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    // Внутри эпиков не должно оставаться неактуальных id подзадач
    @Test
    void epicDoNotContainNonActualSubtaskId() {
        TaskManager taskManager = Managers.getDefault();
        Task epic = new Epic("Test epicDoNotContainNonActualSubtaskId",
                "Test epicDoNotContainNonActualSubtaskId description");
        taskManager.makeNewEpic(epic);
        Task subtask1 = new Subtask("Test epicDoNotContainNonActualSubtaskId #1",
                "Test epicDoNotContainNonActualSubtaskId description #1", NEW, epic.getId());
        taskManager.makeNewSubtask(subtask1);
        Task subtask2 = new Subtask("Test epicDoNotContainNonActualSubtaskId #2",
                "Test epicDoNotContainNonActualSubtaskId description #2", NEW, epic.getId());
        taskManager.makeNewSubtask(subtask2);
        // Удаляем подзадачу №1 и убеждаемся, что список подзадач эпика содержит только подзадачу №2
        taskManager.delSubtaskById(2);
        assertEquals(List.of(subtask2), taskManager.getListOfSubTasksOfEpic(epic.getId()));
    }

    // Проверяем, что с помощью сеттеров экземпляры задач позволяют изменить любое свое поле,
    // но это может повлиять на данные внутри менеджера
    @Test
    void settersTest() {
        TaskManager taskManager = Managers.getDefault();
        Task task = new Task("Name in manager", "Test settersTest description", NEW);
        taskManager.makeNewTask(task);
        task.setName("Name, changed by setter");
        task.setId(2);
        task.setDescription("Description, changed by setter");
        task.setStatus(IN_PROGRESS);
        assertEquals("Name, changed by setter", taskManager.getTaskById(1).getName());
        assertEquals(2, taskManager.getTaskById(1).getId());
        assertEquals("Description, changed by setter", taskManager.getTaskById(1).getDescription());
        assertEquals(IN_PROGRESS, taskManager.getTaskById(1).getStatus());
    }
    // Проверка сеттеров подтвердила наше предположение.
    // Проблема может быть решена изменением модификаторов доступа для сеттеров на private.
}