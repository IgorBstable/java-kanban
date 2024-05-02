package service;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static model.TaskStatus.IN_PROGRESS;
import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    // проверяем, что FileBackedTaskManagerTest действительно
    // добавляет задачи разного типа и может найти их по id;
    @Test
    void addTasks() {
        TaskManager manager = Managers.
                getDefaultFileBacked(new File("fileBacked"));
        Task task = new Task(0, "Test addTasks",
                "Test addTasks description", NEW, TaskTypes.TASK);
        manager.makeNewTask(task);
        Epic epic = new Epic(0, "Test addTasks",
                "Test addTasks description", NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic);
        Subtask subtask = new Subtask(0, "Test addTasks",
                "Test addTasks description", NEW, TaskTypes.SUBTASK, epic.getId());
        manager.makeNewSubtask(subtask);

        assertEquals(task, manager.getTaskById(task.getId()));
        assertEquals(epic, manager.getEpicById(epic.getId()));
        assertEquals(subtask, manager.getSubtaskById(subtask.getId()));
    }

    @Test
    void updateTask() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task task = new Task(0, "Test updateTask",
                "Test updateTask description", NEW, TaskTypes.TASK);
        manager.makeNewTask(task);
        Task task1 = new Task(0, "Test addTasks #1",
                "Test addTasks description #1", NEW, TaskTypes.TASK);
        manager.makeNewTask(task1);
        task1.setId(1);
        manager.updateTask(task1);
        assertEquals("Test addTasks #1", manager.getTaskById(1).getName());
    }

    @Test
    void deleteTasks() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task task = new Task(0, "Test deleteTasks",
                "Test deleteTasks description", NEW, TaskTypes.TASK);
        manager.makeNewTask(task);
        Task task1 = new Task(0, "Test deleteTasks #1",
                "Test deleteTasks description #1", NEW, TaskTypes.TASK);
        manager.makeNewTask(task1);
        manager.deleteTasks();
        assertEquals(0, manager.getAllTasks().size());
    }

    @Test
    void updateSubTask() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task epic = new Epic(0, "Test updateSubTask",
                "Test updateSubTask description", NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic);
        Task subtask = new Subtask(0, "Test updateSubTask",
                "Test updateSubTask description", NEW,
                TaskTypes.SUBTASK, 1);
        manager.makeNewSubtask(subtask);
        Task subtask1 = new Subtask(0, "Test updateSubTask #1",
                "Test updateSubTask description #1", NEW,
                TaskTypes.SUBTASK, 1);
        subtask1.setId(2);
        manager.updateSubTask(subtask1);
        assertEquals("Test updateSubTask #1", manager.getSubtaskById(2).getName());
    }

    @Test
    void delSubtasks() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task epic = new Epic(0, "Test delSubtasks",
                "Test delSubtasks description", NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic);
        Task subtask = new Subtask(0, "Test delSubtasks",
                "Test delSubtasks description", NEW,
                TaskTypes.SUBTASK, 1);
        manager.makeNewSubtask(subtask);
        Task subtask1 = new Subtask(0, "Test delSubtasks #1",
                "Test delSubtasks description #1", NEW,
                TaskTypes.SUBTASK, 1);
        manager.makeNewSubtask(subtask1);
        manager.delSubtasks();
        assertEquals(0, manager.getAllSubtasks().size());
    }

    @Test
    void deleteEpics() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task epic = new Epic(0, "Test deleteEpics",
                "Test deleteEpics description", NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic);
        Task epic1 = new Epic(0, "Test deleteEpics #1",
                "Test deleteEpics description #1", NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic1);
        manager.deleteEpics();
        assertEquals(0, manager.getAllEpics().size());
    }

    @Test
    void delEpicById() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task epic = new Epic(0, "Test delEpicById",
                "Test delEpicById description", NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic);
        Task epic1 = new Epic(0, "Test delEpicById #1",
                "Test delEpicById description #1", NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic1);
        manager.delEpicById(1);
        assertEquals(List.of(epic1), manager.getAllEpics());
    }

    // Удаляемые подзадачи не должны хранить внутри себя старые id
    @Test
    void subtaskShouldNotSaveOldId() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task epic = new Epic(0, "Test subtaskShouldNotSaveOldId",
                "Test subtaskShouldNotSaveOldId description",
                NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic);
        Task subtask = new Subtask(0, "Test subtaskShouldNotSaveOldId",
                "Test subtaskShouldNotSaveOldId description",
                NEW, TaskTypes.SUBTASK, epic.getId());
        manager.makeNewSubtask(subtask);
        manager.delSubtaskById(2);
        assertEquals(0, manager.getAllSubtasks().size());
    }

    // Внутри эпиков не должно оставаться неактуальных id подзадач
    @Test
    void epicDoNotContainNonActualSubtaskId() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task epic = new Epic(0, "Test epicDoNotContainNonActualSubtaskId",
                "Test epicDoNotContainNonActualSubtaskId description",
                NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic);
        Task subtask1 = new Subtask(0, "Test epicDoNotContainNonActualSubtaskId #1",
                "Test epicDoNotContainNonActualSubtaskId description #1",
                NEW, TaskTypes.SUBTASK, epic.getId());
        manager.makeNewSubtask(subtask1);
        Task subtask2 = new Subtask(0, "Test epicDoNotContainNonActualSubtaskId #2",
                "Test epicDoNotContainNonActualSubtaskId description #2",
                NEW, TaskTypes.SUBTASK, epic.getId());
        manager.makeNewSubtask(subtask2);
        // Удаляем подзадачу №1 и убеждаемся, что список подзадач эпика содержит только подзадачу №2
        manager.delSubtaskById(2);
        assertEquals(List.of(subtask2), manager.getListOfSubTasksOfEpic(epic.getId()));
    }

    // Проверяем, что с помощью сеттеров экземпляры задач позволяют изменить любое свое поле,
    // но это может повлиять на данные внутри менеджера
    @Test
    void settersTest() {
        TaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task task = new Task(0, "Name in manager",
                "Test settersTest description", NEW, TaskTypes.TASK);
        manager.makeNewTask(task);
        task.setName("Name, changed by setter");
        task.setId(2);
        task.setDescription("Description, changed by setter");
        task.setStatus(IN_PROGRESS);
        assertEquals("Name, changed by setter", manager.getTaskById(1).getName());
        assertEquals(2, manager.getTaskById(1).getId());
        assertEquals("Description, changed by setter", manager.getTaskById(1).getDescription());
        assertEquals(IN_PROGRESS, manager.getTaskById(1).getStatus());
    }
    // Проверка сеттеров подтвердила наше предположение.
    // Проблема может быть решена изменением модификаторов доступа для сеттеров на private.

    // Проверяем загрузку пустого файла
//    @Test
//    void uploadEmptyFileTest() throws IOException {
//        File file = new File("fileBacked");
//        Writer fileWriter = new FileWriter(file);
//        fileWriter.write("");
//        fileWriter.close();
//        TaskManager manager = FileBackedTaskManager.
//                loadFromFile(new File("fileBacked"));
//        ArrayList<Task> tasks = manager.getAllTasks();
//        ArrayList<Task> subTasks = manager.getAllSubtasks();
//        ArrayList<Task> epics = manager.getAllEpics();
//        assertEquals(tasks.size(), 0);
//        assertEquals(subTasks.size(), 0);
//        assertEquals(epics.size(), 0);
//    }

    // Проверяем сохранение пустого файла
//    @Test
//    void saveEmptyFileTest() throws IOException {
//        File file = new File("fileBacked");
//        String backedContent = Files.readString(file.toPath());
//        assertEquals(backedContent.length(), 0);
//    }

    // Проверяем сохранение нескольких задач
//    @Test
//    void saveSeveralTasks() throws IOException {
//        File file = new File("fileBacked");
//        Writer fileWriter = new FileWriter(file);
//        fileWriter.write("");
//        fileWriter.close();
//        FileBackedTaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
//        Task task1 = new Task(0,"Задача1", "Описание задачи1",
//                TaskStatus.NEW, TaskTypes.TASK);
//        manager.makeNewTask(task1);
//        Task task2 = new Task(0,"Задача2", "Описание задачи2",
//                TaskStatus.NEW, TaskTypes.TASK);
//        manager.makeNewTask(task2);
//        String content = """
//                id,type,name,status,description,epic
//                1,TASK,Задача1,NEW,Описание задачи1
//                2,TASK,Задача2,NEW,Описание задачи2
//                """;
//        String backedContent = Files.readString(file.toPath());
//        assertEquals(backedContent, content);
//    }

    // Проверяем загрузку нескольких задач
    @Test
    void uploadSeveralTasks() throws IOException {
        File file = new File("fileBacked");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        String content = """
                id,type,name,status,description,epic
                1,TASK,Задача1,NEW,Описание задачи1
                2,TASK,Задача2,NEW,Описание задачи2
                """;
        fileWriter.write(content);
        fileWriter.close();
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);
        String name = manager.getTaskById(1).getName();
        assertEquals(name, "Задача1");
    }
}

