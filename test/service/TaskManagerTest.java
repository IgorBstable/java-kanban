package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskTypes;

import java.util.List;

import static model.TaskStatus.IN_PROGRESS;
import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    // проверяем, что TaskManager действительно
    // добавляет задачи разного типа и может найти их по id;
    public void addTasks() {
        Task task = new Task(0, "Test addTasks",
                "Test addTasks description", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task);
        Epic epic = new Epic(0, "Test addTasks",
                "Test addTasks description", TaskTypes.EPIC);
        taskManager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test addTasks",
                "Test addTasks description", NEW, TaskTypes.SUBTASK, epic.getId());
        taskManager.makeNewSubtask(subtask);

        assertEquals(task, taskManager.getTaskById(task.getId()));
        assertEquals(epic, taskManager.getEpicById(epic.getId()));
        assertEquals(subtask, taskManager.getSubtaskById(subtask.getId()));
    }

    // Тестируем функции менеджера задач, которые реализованы в предыдущих спринтах
    public void updateTask() {
        Task task = new Task(0, "Test updateTask",
                "Test updateTask description", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task);
        Task task1 = new Task(0, "Test addTasks #1",
                "Test addTasks description #1", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task1);
        task1.setId(1);
        taskManager.updateTask(task1);
        assertEquals("Test addTasks #1", taskManager.getTaskById(1).getName());
    }

    public void deleteTasks() {
        Task task = new Task(0, "Test deleteTasks",
                "Test deleteTasks description", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task);
        Task task1 = new Task(0, "Test deleteTasks #1",
                "Test deleteTasks description #1", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task1);
        taskManager.deleteTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    public void updateSubTask() {
        Task epic = new Epic(0, "Test updateSubTask",
                "Test updateSubTask description", TaskTypes.EPIC);
        taskManager.makeNewEpic(epic);
        Task subtask = new Subtask(0, "Test updateSubTask",
                "Test updateSubTask description", NEW,
                TaskTypes.SUBTASK, 1);
        taskManager.makeNewSubtask(subtask);
        Task subtask1 = new Subtask(0, "Test updateSubTask #1",
                "Test updateSubTask description #1", NEW,
                TaskTypes.SUBTASK, 1);
        subtask1.setId(2);
        taskManager.updateSubTask(subtask1);
        assertEquals("Test updateSubTask #1", taskManager.getSubtaskById(2).getName());
    }

    public void delSubtasks() {
        Task epic = new Epic(0, "Test delSubtasks",
                "Test delSubtasks description", TaskTypes.EPIC);
        taskManager.makeNewEpic(epic);
        Task subtask = new Subtask(0, "Test delSubtasks",
                "Test delSubtasks description", NEW,
                TaskTypes.SUBTASK, 1);
        taskManager.makeNewSubtask(subtask);
        Task subtask1 = new Subtask(0, "Test delSubtasks #1",
                "Test delSubtasks description #1", NEW,
                TaskTypes.SUBTASK, 1);
        taskManager.makeNewSubtask(subtask1);
        taskManager.delSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    public void deleteEpics() {
        Task epic = new Epic(0, "Test deleteEpics",
                "Test deleteEpics description", TaskTypes.EPIC);
        taskManager.makeNewEpic(epic);
        Task epic1 = new Epic(0, "Test deleteEpics #1",
                "Test deleteEpics description #1", TaskTypes.EPIC);
        taskManager.makeNewEpic(epic1);
        taskManager.deleteEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    public void delEpicById() {
        Task epic = new Epic(0, "Test delEpicById",
                "Test delEpicById description", TaskTypes.EPIC);
        taskManager.makeNewEpic(epic);
        Task epic1 = new Epic(0, "Test delEpicById #1",
                "Test delEpicById description #1", TaskTypes.EPIC);
        taskManager.makeNewEpic(epic1);
        taskManager.delEpicById(1);
        assertEquals(List.of(epic1), taskManager.getAllEpics());
    }

    // Удаляемые подзадачи не должны хранить внутри себя старые id
    public void subtaskShouldNotSaveOldId() {
        Task epic = new Epic(0, "Test subtaskShouldNotSaveOldId",
                "Test subtaskShouldNotSaveOldId description",
                TaskTypes.EPIC);
        taskManager.makeNewEpic(epic);
        Task subtask = new Subtask(0, "Test subtaskShouldNotSaveOldId",
                "Test subtaskShouldNotSaveOldId description",
                NEW, TaskTypes.SUBTASK, epic.getId());
        taskManager.makeNewSubtask(subtask);
        taskManager.delSubtaskById(2);
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    // Внутри эпиков не должно оставаться неактуальных id подзадач
    public void epicDoNotContainNonActualSubtaskId() {
        Task epic = new Epic(0, "Test epicDoNotContainNonActualSubtaskId",
                "Test epicDoNotContainNonActualSubtaskId description",
                TaskTypes.EPIC);
        taskManager.makeNewEpic(epic);
        Task subtask1 = new Subtask(0, "Test epicDoNotContainNonActualSubtaskId #1",
                "Test epicDoNotContainNonActualSubtaskId description #1",
                NEW, TaskTypes.SUBTASK, epic.getId());
        taskManager.makeNewSubtask(subtask1);
        Task subtask2 = new Subtask(0, "Test epicDoNotContainNonActualSubtaskId #2",
                "Test epicDoNotContainNonActualSubtaskId description #2",
                NEW, TaskTypes.SUBTASK, epic.getId());
        taskManager.makeNewSubtask(subtask2);
        // Удаляем подзадачу №1 и убеждаемся, что список подзадач эпика содержит только подзадачу №2
        taskManager.delSubtaskById(2);
        assertEquals(List.of(subtask2), taskManager.getListOfSubTasksOfEpic(epic.getId()));
    }

    // Проверяем, что с помощью сеттеров экземпляры задач позволяют изменить любое свое поле,
    // но это может повлиять на данные внутри менеджера
    public void settersTest() {
        Task task = new Task(0, "Name in manager",
                "Test settersTest description", NEW, TaskTypes.TASK);
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

    // Проверяем, что утилитарный класс всегда возвращает
    // проинициализированные и готовые к работе экземпляры менеджеров
    // (проверка сделана исходя из тезиса о том, что проинициализированный
    // и готовый к работе экземпляр менеджера должен быть способен исполнять методы
    // класса (в данном случае для примера взял метод makeNewTask)).
    void newManager() {
        Task task = new Task(0, "Test newManager",
                "Test newManager description", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task);
        assertEquals(List.of(task), taskManager.getAllTasks());
    }

    // проверяем неизменность задачи (по всем полям) при добавлении задачи в менеджер
    void taskEqualsAfterAdd() {
        Task task = new Task(0, "Test taskEqualsAfterAdd",
                "Test taskEqualsAfterAdd description", NEW, TaskTypes.TASK);
        taskManager.makeNewTask(task);

        assertEquals("Test taskEqualsAfterAdd", task.getName());
        assertEquals("Test taskEqualsAfterAdd description", task.getDescription());
        assertEquals(NEW, task.getStatus());
        assertEquals(1, task.getId());
    }
}

