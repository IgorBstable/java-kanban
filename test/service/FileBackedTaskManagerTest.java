package service;

import model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest extends TaskManagerTest <FileBackedTaskManager> {

    public FileBackedTaskManagerTest setup() {
        FileBackedTaskManagerTest fbtmt = new FileBackedTaskManagerTest();
        fbtmt.taskManager = new FileBackedTaskManager(new File("fileBacked.csv"));
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
        setup().ifEpicExistsAndHasStatus();
        setup().ifEpicExistsAndHasStatus();
    }

    // Далее следуют тесты, специфичные для FileBackedTaskManager

    // Проверяем загрузку пустого файла
    @Test
    void uploadEmptyFileTest() throws IOException {
        File file = new File("fileBacked.csv");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.close();
        TaskManager manager = FileBackedTaskManager.
                loadFromFile(new File("fileBacked.csv"));
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
        File file = new File("fileBacked.csv");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.close();
        String backedContent = Files.readString(file.toPath());
        assertEquals(backedContent.length(), 0);
    }

    // Проверяем сохранение нескольких задач
    @Test
    void saveSeveralTasks() throws IOException {
        File file = new File("fileBacked.csv");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        fileWriter.close();
        FileBackedTaskManager manager = Managers.getDefaultFileBacked(new File("fileBacked.csv"));
        Task task1 = new Task(0,"Задача1", "Описание задачи1",
                TaskStatus.NEW, TaskTypes.TASK);
        task1.setStartTime(LocalDateTime.of(2024,5,19,9,0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.makeNewTask(task1);
        Task task2 = new Task(0,"Задача2", "Описание задачи2",
                TaskStatus.NEW, TaskTypes.TASK);
        task2.setStartTime(LocalDateTime.of(2024,5,19,10,0));
        task2.setDuration(Duration.ofMinutes(40));
        manager.makeNewTask(task2);
        String content = """
                id,type,name,status,description,start,duration,end,epic
                1,TASK,Задача1,NEW,Описание задачи1,2024-05-19T09:00,30,2024-05-19T09:30
                2,TASK,Задача2,NEW,Описание задачи2,2024-05-19T10:00,40,2024-05-19T10:40
                """;
        String backedContent = Files.readString(file.toPath());
        assertEquals(backedContent, content);
    }

    // Проверяем загрузку нескольких задач
    @Test
    void uploadSeveralTasks() throws IOException {
        File file = new File("fileBacked.csv");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        String content = """
                id,type,name,status,description,start,duration,end,epic
                1,TASK,Задача1,NEW,Описание задачи1,2024-05-19T09:00,30,2024-05-19T09:30
                2,TASK,Задача2,NEW,Описание задачи2,2024-05-19T10:00,40,2024-05-19T10:40
                """;
        fileWriter.write(content);
        fileWriter.close();
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);
        String name = manager.getTaskById(1).getName();
        assertEquals(name, "Задача1");
    }

    // Проверяем корректный перехват исключений при работе с файлами
    @Test
    void testException() throws IOException {
        File file = new File("fileBacked.csv");
        Writer fileWriter = new FileWriter(file);
        fileWriter.write("");
        String content = """
                id,type,name,status,description,start,duration,end,epic
                1,TASK,Задача1,NEW,Описание задачи1
                """;
        fileWriter.write(content);
        fileWriter.close();
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> FileBackedTaskManager.loadFromFile(file));
    }
}

