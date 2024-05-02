import model.*;
import service.*;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

//        TaskManager manager = Managers.getDefault();
//        Task task1 = new Task("Задача1", "Описание задачи1",
//                TaskStatus.NEW, TaskTypes.TASK);
//        manager.makeNewTask(task1);
//        Task task2 = new Task("Задача2", "Описание задачи2",
//                TaskStatus.NEW, TaskTypes.TASK);
//        manager.makeNewTask(task2);
//        Epic epic1 = new Epic("Эпик1", "Описание эпика1", TaskTypes.EPIC);
//        manager.makeNewEpic(epic1);
//        Subtask subTask1 = new Subtask("Подзадача1", "Описание подзадачи1",
//                TaskStatus.NEW, TaskTypes.SUBTASK, epic1.getId());
//        manager.makeNewSubtask(subTask1);
//        Subtask subTask2 = new Subtask("Подзадача2", "Описание подзадачи2",
//                TaskStatus.NEW, TaskTypes.SUBTASK, epic1.getId());
//        manager.makeNewSubtask(subTask2);
//        Subtask subTask3 = new Subtask("Подзадача3", "Описание подзадачи3",
//                TaskStatus.NEW, TaskTypes.SUBTASK, epic1.getId());
//        manager.makeNewSubtask(subTask3);
//        Epic epic2 = new Epic("Эпик2", "Описание эпика2", TaskTypes.EPIC);
//        manager.makeNewEpic(epic2);
//
//        // Проверка удаления из истории просмотров (в том числе при удалении задач)
//        // Сначала добавляем в историю задачи по несколько раз каждую
//        manager.getTaskById(2);
//        manager.getTaskById(1);
//        manager.getTaskById(1);
//        manager.getTaskById(2);
//        manager.getEpicById(3);
//        manager.getEpicById(3);
//        manager.getSubtaskById(5);
//        manager.getSubtaskById(4);
//        manager.getSubtaskById(6);
//        manager.getSubtaskById(5);
//        manager.getSubtaskById(6);
//        manager.getSubtaskById(4);
//        manager.getEpicById(7);
//        manager.getEpicById(7);
//        printAllTasks(manager);
//
//        // Теперь удаляем одну из задач
//        manager.deleteTaskById(2);
//        printAllTasks(manager);
//
//        // Удаляем эпик с тремя задачами
//        manager.delEpicById(3);
//        printAllTasks(manager);
//    }
//
//    private static void printAllTasks(TaskManager manager) {
//        System.out.println("Задачи:");
//        for (Task task : manager.getAllTasks()) {
//            System.out.println(task);
//        }
//        System.out.println("Эпики:");
//        for (Task epic : manager.getAllEpics()) {
//            System.out.println(epic);
//            for (Task task : manager.getListOfSubTasksOfEpic(epic.getId())) {
//                System.out.println("--> " + task);
//            }
//        }
//        System.out.println("Подзадачи:");
//        for (Task subtask : manager.getAllSubtasks()) {
//            System.out.println(subtask);
//        }
//
//        System.out.println("История:");
//        for (Task task : manager.getHistoryManager()) {
//            System.out.println(task);
//        }
//    }

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile
                (new File("C:\\Java_course\\homework\\Sprint_4_and_further\\java-kanban\\fileBacked"));
        Task task1 = new Task(0,"Задача1", "Описание задачи1",
                TaskStatus.NEW, TaskTypes.TASK);
        manager.makeNewTask(task1);
        Task task2 = new Task(0,"Задача2", "Описание задачи2",
                TaskStatus.NEW, TaskTypes.TASK);
        manager.makeNewTask(task2);
        Epic epic1 = new Epic(0, "Эпик1", "Описание эпика1",
                TaskStatus.NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic1);
        Subtask subTask1 = new Subtask(0, "Подзадача1",
                "Описание подзадачи1", TaskStatus.NEW, TaskTypes.SUBTASK,
                epic1.getId());
        manager.makeNewSubtask(subTask1);
        Subtask subTask2 = new Subtask(0,"Подзадача2",
                "Описание подзадачи2", TaskStatus.NEW, TaskTypes.SUBTASK,
                epic1.getId());
        manager.makeNewSubtask(subTask2);
        Subtask subTask3 = new Subtask(0, "Подзадача3",
                "Описание подзадачи3", TaskStatus.NEW, TaskTypes.SUBTASK,
                epic1.getId());
        manager.makeNewSubtask(subTask3);
        Epic epic2 = new Epic(0, "Эпик2", "Описание эпика2",
                TaskStatus.NEW, TaskTypes.EPIC);
        manager.makeNewEpic(epic2);
    }
}

