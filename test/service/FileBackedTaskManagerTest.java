package service;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest extends TaskManagerTest <FileBackedTaskManager> {

    public FileBackedTaskManagerTest setup() {
        FileBackedTaskManagerTest fbtmt = new FileBackedTaskManagerTest();
        fbtmt.taskManager = new FileBackedTaskManager(new File("fileBacked"));
        return fbtmt;
    }

    @Test
    void standardManagerTests() {
        setup().addTasks();
        setup().updateTask();
        setup().deleteTasks();
        setup().updateSubTask();
        setup().delSubtasks();
        setup().deleteEpics();
        setup().delEpicById();
        setup().subtaskShouldNotSaveOldId();
        setup().epicDoNotContainNonActualSubtaskId();
        setup().settersTest();
        setup().newManager();
        setup().taskEqualsAfterAdd();
    }

    // Далее следуют тесты, специфичные для FileBackedTaskManager

    // Проверяем загрузку пустого файла
    @Test
    void uploadEmptyFileTest() throws IOException {
        File file = new File("fileBacked");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.close();
        TaskManager manager = FileBackedTaskManager.
                loadFromFile(new File("fileBacked"));
        ArrayList<Task> tasks = manager.getAllTasks();
        ArrayList<Task> subTasks = manager.getAllSubtasks();
        ArrayList<Task> epics = manager.getAllEpics();
        assertEquals(tasks.size(), 0);
        assertEquals(subTasks.size(), 0);
        assertEquals(epics.size(), 0);
    }

    // Проверяем сохранение пустого файла
    @Test
    void saveEmptyFileTest() throws IOException {
        File file = new File("fileBacked");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.close();
        String backedContent = Files.readString(file.toPath());
        assertEquals(backedContent.length(), 0);
    }

    // Проверяем сохранение нескольких задач
    @Test
    void saveSeveralTasks() throws IOException {
        File file = new File("fileBacked");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.close();
        FileBackedTaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked"));
        Task task1 = new Task(0,"Задача1", "Описание задачи1",
                TaskStatus.NEW, TaskTypes.TASK);
        manager.makeNewTask(task1);
        Task task2 = new Task(0,"Задача2", "Описание задачи2",
                TaskStatus.NEW, TaskTypes.TASK);
        manager.makeNewTask(task2);
        String content = """
                id,type,name,status,description,epic
                1,TASK,Задача1,NEW,Описание задачи1
                2,TASK,Задача2,NEW,Описание задачи2
                """;
        String backedContent = Files.readString(file.toPath());
        assertEquals(backedContent, content);
    }

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

