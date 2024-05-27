import model.*;
import service.*;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {

        File file = new File("fileBacked.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.loadFromFile(file);

        System.out.println("Выводим сортированный список задач и подзадач");
        List<Task> sortedTasks0 = manager.getPrioritizedTasks();
        for (Task task : sortedTasks0) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Отдельно выводим все задачи");
        ArrayList<Task> tasks0 = manager.getAllTasks();
        for (Task task : tasks0) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Отдельно выводим все подзадачи");
        ArrayList<Task> subtasks0 = manager.getAllSubtasks();
        for (Task task : subtasks0) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("ЗАГРУЗКА ИЗ ФАЙЛА ЗАВЕРШЕНА");

        Task task1 = new Task(0, "Задача1", "Описание задачи1",
                TaskStatus.NEW, TaskTypes.TASK);
        task1.setStartTime(LocalDateTime.of(2024, 5, 19, 9,0));
        task1.setDuration(Duration.ofMinutes(30));
        manager.makeNewTask(task1);
        Task task2 = new Task(0, "Задача2", "Описание задачи2",
                TaskStatus.NEW, TaskTypes.TASK);
        task2.setStartTime(LocalDateTime.of(2024, 5, 19, 9,31));
        task2.setDuration(Duration.ofMinutes(40));
        manager.makeNewTask(task2);
        Epic epic1 = new Epic(0, "Эпик1", "Описание эпика1");
//        epic1.setStartTime(LocalDateTime.of(2024, 5, 19, 11,0));
//        epic1.setDuration(Duration.ofMinutes(30));
        manager.makeNewEpic(epic1);
        Subtask subTask1 = new Subtask(0, "Подзадача1",
                "Описание подзадачи1", TaskStatus.IN_PROGRESS,
                epic1.getId());
        subTask1.setStartTime(LocalDateTime.of(2024, 5, 19, 11,30));
        subTask1.setDuration(Duration.ofMinutes(10));
        manager.makeNewSubtask(subTask1);
        Subtask subTask2 = new Subtask(0, "Подзадача2",
                "Описание подзадачи2", TaskStatus.NEW,
                epic1.getId());
        subTask2.setStartTime(LocalDateTime.of(2024, 5, 19, 11,15));
        subTask2.setDuration(Duration.ofMinutes(10));
        manager.makeNewSubtask(subTask2);
        Subtask subTask3 = new Subtask(0, "Подзадача3",
                "Описание подзадачи3", TaskStatus.NEW,
                epic1.getId());
        subTask3.setStartTime(LocalDateTime.of(2024, 5, 19, 11,0));
        subTask3.setDuration(Duration.ofMinutes(10));
        manager.makeNewSubtask(subTask3);
        Epic epic2 = new Epic(0, "Эпик2", "Описание эпика2");
        manager.makeNewEpic(epic2);

        System.out.println();
        System.out.println("ПОСЛЕ ВЫПОЛНЕНИЯ");
        System.out.println("Выводим сортированный список задач и подзадач");
        List<Task> sortedTasks1 = manager.getPrioritizedTasks();
        for (Task task : sortedTasks1) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Отдельно выводим все задачи");
        ArrayList<Task> tasks = manager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Отдельно выводим все подзадачи");
        ArrayList<Task> subtasks = manager.getAllSubtasks();
        for (Task task : subtasks) {
            System.out.println(task);
        }

        System.out.println();
        System.out.println("Отдельно выводим все эпики");
        ArrayList<Task> epics = manager.getAllEpics();
        for (Task task : epics) {
            System.out.println(task);
        }
    }
}



