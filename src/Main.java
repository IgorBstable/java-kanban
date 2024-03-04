import model.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        // Формируем исходные данные
        Task task1 = new Task("Задача1", "Описание задачи1", TaskStatus.NEW, 1);
        taskManager.makeNewTask(task1);
        Task task2 = new Task("Задача2", "Описание задачи2", TaskStatus.NEW, 2);
        taskManager.makeNewTask(task2);
        Epic epic1 = new Epic("Эпик1", "Описание эпика1", 3);
        taskManager.makeNewEpic(epic1);
        Subtask subTask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                TaskStatus.NEW, 4, 3);
        taskManager.makeNewSubtask(subTask1);
        Subtask subTask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                TaskStatus.NEW, 5, 3);
        taskManager.makeNewSubtask(subTask2);
        Epic epic2 = new Epic("Эпик2", "Описание эпика2", 6);
        taskManager.makeNewEpic(epic2);
        Subtask subTask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                TaskStatus.NEW, 7, 6);
        taskManager.makeNewSubtask(subTask3);

        // Контрольная печать списков
        System.out.println("Задачи:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Подзадачи:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());

        // Изменяем статусы
        task1 = new Task("Задача1", "Описание задачи1", TaskStatus.IN_PROGRESS, 1);
        taskManager.updateTask(task1);
        task2 = new Task("Задача2", "Описание задачи2", TaskStatus.DONE, 2);
        taskManager.updateTask(task2);
        subTask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                TaskStatus.IN_PROGRESS, 4, 3);
        taskManager.updateSubTask(subTask1);
        subTask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                TaskStatus.DONE, 5, 3);
        taskManager.updateSubTask(subTask2);
        subTask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                TaskStatus.DONE, 7, 6);
        taskManager.updateSubTask(subTask3);

        // Контрольная печать списков
        System.out.println();
        System.out.println("Задачи:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Подзадачи:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());

        taskManager.deleteTaskById(2);
        taskManager.delEpicById(6);

        // Контрольная печать списков
        System.out.println();
        System.out.println("Задачи:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("Подзадачи:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("Эпики:");
        System.out.println(taskManager.getAllEpics());

    }
}
