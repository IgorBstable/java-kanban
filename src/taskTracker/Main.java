package taskTracker;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        while (true) {
            printMainMenu();
            String command = scanner.nextLine();
            switch (command) {
                case "1": // создать новую задачу
                    Task task = taskManager.makeNewTask();
                    taskManager.addTask(task);
                    break;
                case "2": // получить список задач
                    taskManager.printTasks();
                    break;
                case "3": // Удалить задачи
                    taskManager.deleteTasks();
                    break;
                case "4": // Получить задачу (по идентификатору)
                    Integer idToGetTask = taskManager.getId();
                    taskManager.getTaskById(idToGetTask);
                    break;
                case "5": // Обновить задачу (по идентификатору)
                    Integer idForTaskRenew = taskManager.getId();
                    Task taskToRenew = taskManager.makeNewTask();
                    taskManager.renewTask(idForTaskRenew, taskToRenew);
                    break;
                case "6": // Удалить задачу (по идентификатору)
                    Integer idForTaskDel = taskManager.getId();
                    taskManager.deleteTaskById(idForTaskDel);
                    break;
                case "7": // создать новую подзадачу
                    Epic epicToMakeSubTask = taskManager.getEpicByName();
                    Subtask subtask = taskManager.makeNewSubTask(epicToMakeSubTask);
                    taskManager.addNewSubtask(subtask);
                    break;
                case "8": // получить список подзадач
                    taskManager.printSubTasks();
                    break;
                case "9": // Удалить подзадачи
                    taskManager.deleteSubTasks();
                    break;
                case "10": // Получить подзадачу (по идентификатору)
                    Integer iDToGetSubTask = taskManager.getId();
                    taskManager.getSubtaskById(iDToGetSubTask);
                    break;
                case "11": // Получить список всех подзадач определенного эпика
                    taskManager.getListOfSubTasksOfEpic();
                    break;
                case "12": // Обновить подзадачу (по идентификатору)
                    Integer idForSubtaskRenew = taskManager.getId();
                    Epic epicToRenewSubtask = taskManager.getEpicByName();
                    Subtask subtaskToRenew = taskManager.makeNewSubTask(epicToRenewSubtask);
                    taskManager.renewSubTask(idForSubtaskRenew, subtaskToRenew);
                    break;
                case "13": // создать новый эпик
                    taskManager.addNewEpic();
                    break;
                case "14": // Получить список эпиков
                    taskManager.printEpics();
                    break;
                case "15": // Удалить эпики
                    taskManager.deleteEpics();
                    break;
                case "16": // Получить эпик (по идентификатору)
                    Integer iDToGetEpic = taskManager.getId();
                    taskManager.getEpicById(iDToGetEpic);
                    break;
                case "17":
                    return;
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("Выберите команду:");
        System.out.println("1 - Создать новую задачу.");
        System.out.println("2 - Получить список задач.");
        System.out.println("3 - Удалить задачи.");
        System.out.println("4 - Получить по идентификатору задачу.");
        System.out.println("5 - Обновить задачу (по идентификатору).");
        System.out.println("6 - Удалить задачу (по идентификатору).");
        System.out.println("7 - Создать новую подзадачу.");
        System.out.println("8 - Получить список подзадач.");
        System.out.println("9 - Удалить подзадачи.");
        System.out.println("10 - Получить по идентификатору подзадачу.");
        System.out.println("11 - Получить список всех подзадач определенного эпика.");
        System.out.println("12 - Обновить подзадачу (по идентификатору).");
        System.out.println("13 - Создать новый эпик.");
        System.out.println("14 - Получить список эпиков.");
        System.out.println("15 - Удалить эпики.");
        System.out.println("16 - Получить по идентификатору эпик.");
        System.out.println("17 - Выход.");
    }
}
