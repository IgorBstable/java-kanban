import model.*;
import service.*;

public class Main {
    public static void main(String[] args) {

        TaskManager manager = Managers.getDefault();
        Task task1 = new Task("Задача1", "Описание задачи1", TaskStatus.NEW);
        manager.makeNewTask(task1);
        Task task2 = new Task("Задача2", "Описание задачи2", TaskStatus.NEW);
        manager.makeNewTask(task2);
        Epic epic1 = new Epic("Эпик1", "Описание эпика1");
        manager.makeNewEpic(epic1);
        Subtask subTask1 = new Subtask("Подзадача1", "Описание подзадачи1",
                TaskStatus.NEW, epic1.getId());
        manager.makeNewSubtask(subTask1);
        Subtask subTask2 = new Subtask("Подзадача2", "Описание подзадачи2",
                TaskStatus.NEW, epic1.getId());
        manager.makeNewSubtask(subTask2);
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        manager.makeNewEpic(epic2);
        Subtask subTask3 = new Subtask("Подзадача3", "Описание подзадачи3",
                TaskStatus.NEW, epic2.getId());
        manager.makeNewSubtask(subTask3);

        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Task epic : manager.getAllEpics()) {
            System.out.println(epic);
            for (Task task : manager.getListOfSubTasksOfEpic(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getAllSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistoryManager().getHistory()) {
            System.out.println(task);
        }
    }
}
