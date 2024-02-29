package taskTracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {
    static Integer taskId = 0;
    HashMap<Integer, Task> listOfTasks = new HashMap<>();
    HashMap<Integer, Subtask> listOfSubTasks = new HashMap<>();
    HashMap<Integer, Epic> listOfEpics = new HashMap<>();

    Scanner scanner = new Scanner(System.in);


    Task makeNewTask() {
        System.out.println("СОЗДАНИЕ НОВОЙ ЗАДАЧИ (нажмите ВВОД)");
        scanner.nextLine();
        System.out.println("Введите название задачи:");
        String taskName = scanner.nextLine();
        System.out.println("Введите описание задачи:");
        String taskDescription = scanner.nextLine();
        System.out.println("Введите статус задачи (NEW, IN_PROGRESS, DONE):");
        String taskStatus = scanner.nextLine();
        return new Task(taskName, taskDescription, TaskStatus.valueOf(taskStatus));
    }

    void addTask(Task task) {
        taskId++;
        listOfTasks.put(taskId, task);
    }

    void printTasks() {
        System.out.println("СПИСОК ЗАДАЧ:");
        for (Integer val : listOfTasks.keySet()) {
            System.out.print("Идентификатор: " + val + " ");
            System.out.println(listOfTasks.get(val));
        }
    }

    void deleteTasks() {
        System.out.println("Удаление задач...");
        listOfTasks.clear();
        System.out.println("Задачи удалены!");
    }

    Integer getId() {
        System.out.println("Введите идентификатор: ");
        return scanner.nextInt();
    }

    void getTaskById(Integer iD) {
        Task task = listOfTasks.get(iD);
        System.out.println(task);
    }

    void renewTask(Integer iD, Task task) {
        System.out.println("ОБНОВЛЕНИЕ ЗАДАЧИ с идентификатором " + iD);
        listOfTasks.put(iD, task);
        System.out.println("Обновление успешно завершено! Вот новая задача: ");
        System.out.println(task);
    }

    void deleteTaskById(Integer iD) {
        System.out.println("УДАЛЕНИЕ ЗАДАЧИ");
        listOfTasks.remove(iD);
        System.out.println("Удаление задачи успешно завершено!");
    }

    Epic getEpicByName() {
        scanner.nextLine();
        System.out.println("Введите название эпика:");
        String epicName = scanner.nextLine();
        for (Epic val : listOfEpics.values()) {
            if (val.Name.equals(epicName)) {
                return val;
            }
        }
        return null;
    }

    Subtask makeNewSubTask(Epic epic) {
        if (epic == null) {
            System.out.println("Такого эпика пока нет.");
            System.out.println("Вот список доступных эпиков:");
            for (Epic val : listOfEpics.values()) {
                System.out.println(val);
            }
            return null;
        }
        System.out.println("Введите название подзадачи:");
        String taskName = scanner.nextLine();
        System.out.println("Введите описание подзадачи:");
        String taskDescription = scanner.nextLine();
        System.out.println("Введите статус подзадачи:");
        String taskStatus = scanner.nextLine();
        return new Subtask(taskName, taskDescription,
                TaskStatus.valueOf(taskStatus), epic.Name);
    }

    void addNewSubtask(Subtask subtask) {
        taskId++;
        listOfSubTasks.put(taskId, subtask);
    }

    void printSubTasks() {
        System.out.println("СПИСОК ПОДЗАДАЧ:");
        for (Integer val : listOfSubTasks.keySet()) {
            System.out.print("Идентификатор: " + val + " Эпик: " + listOfSubTasks.get(val).epicName + " ");
            System.out.println(listOfSubTasks.get(val));
        }
    }

    void deleteSubTasks() {
        System.out.println("Удаление подзадач...");
        listOfSubTasks.clear();
        System.out.println("Подзадачи удалены!");
    }

    void getSubtaskById(Integer iD) {
        Subtask subtask;
        for (Integer val : listOfSubTasks.keySet()) {
            if (val.equals(iD)) {
                subtask = listOfSubTasks.get(iD);
                System.out.println(subtask);
            }
        }
    }

    void renewSubTask(Integer iD, Subtask subtask) {
        System.out.println("ОБНОВЛЕНИЕ ПОДЗАДАЧИ с идентификатором " + iD);
        listOfSubTasks.put(iD, subtask);
        System.out.println("Обновление успешно завершено! Вот новая подзадача: ");
        System.out.println(subtask);
        // В случае, если у подзадачи статус IN_PROGRESS или DONE, необходимо
        // у соответствующего эпика также сделать статус IN_PROGRESS
        if (subtask.Status.equals(TaskStatus.IN_PROGRESS) || subtask.Status.equals(TaskStatus.DONE)) {
            for (Integer epicId : listOfEpics.keySet()) {
                if (listOfEpics.get(epicId).Name.equals(subtask.epicName)) { // ищем эпик по имени
                    // обновляем эпик, оставляя его наименование и описание прежним,
                    // заменяем лишь статус на IN_PROGRESS
                    String newTaskName = listOfEpics.get(epicId).Name;
                    String newTaskDesc = listOfEpics.get(epicId).Description;
                    Epic newEpic = new Epic(newTaskName, newTaskDesc, TaskStatus.IN_PROGRESS);
                    listOfEpics.put(epicId, newEpic);
                }
            }
        }
        // В случае, если у подзадачи статус DONE, необходимо также проверить,
        // у всех ли подзадач данного эпика статус DONE,
        // для этого используем метод getListOfSubTasksOfEpic().
        if (subtask.Status.equals(TaskStatus.DONE)) {
            ArrayList<Subtask> subTasksOfEpic = getListOfSubTasksOfEpic();
            boolean flag = true;
            for (Subtask val : subTasksOfEpic) {
                if (!val.Status.equals(TaskStatus.DONE)) {
                    flag = false;
                    break;
                }
            }
            // В случае, если у всех подзадач данного эпика статус DONE, необходимо
            // у соответствующего эпика также сделать статус DONE
            if (flag) {
                for (Integer epicId : listOfEpics.keySet()) {
                    if (listOfEpics.get(epicId).Name.equals(subtask.epicName)) { // ищем эпик по имени
                        // обновляем эпик, оставляя его наименование и описание прежним,
                        // заменяем лишь статус на DONE
                        String newName = listOfEpics.get(epicId).Name;
                        String newDesc = listOfEpics.get(epicId).Description;
                        Epic newEpic = new Epic(newName, newDesc, TaskStatus.DONE);
                        listOfEpics.put(epicId, newEpic);
                    }
                }
            }
        }
    }

    void addNewEpic() {
        System.out.println("СОЗДАНИЕ НОВОГО ЭПИКА");
        System.out.println("Введите название эпика:");
        String taskName = scanner.nextLine();
        System.out.println("Введите описание эпика:");
        String taskDescription = scanner.nextLine();
        Epic task = new Epic(taskName, taskDescription, TaskStatus.NEW);
        taskId++;
        listOfEpics.put(taskId, task);
    }

    void printEpics() {
        System.out.println("СПИСОК ЭПИКОВ:");
        for (Integer val : listOfEpics.keySet()) {
            System.out.print("Идентификатор " + val + " ");
            System.out.println(listOfEpics.get(val));
        }
    }

    void deleteEpics() {
        System.out.println("Удаление эпиков...");
        listOfEpics.clear();
        System.out.println("Эпики удалены!");
    }

    void getEpicById(Integer iD) {
        boolean flag = false;
        for (Integer val : listOfEpics.keySet()) {
            if (val.equals(iD)) {
                Epic task = listOfEpics.get(iD);
                System.out.println(task);
                flag = true;
            }
        }
        if (!flag) {
            System.out.println("Такого идентификатора в базе эпиков нет.");
        }
    }

    ArrayList<Subtask> getListOfSubTasksOfEpic() {
        boolean flag = false;
        ArrayList<Subtask> listOfSubtasksOfEpic = new ArrayList<>();
        System.out.println("Введите название эпика:");
        String epicName = scanner.nextLine();
        for (Epic epic : listOfEpics.values()) {
            if (epic.Name.equals(epicName)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            for (Subtask subtask : listOfSubTasks.values()) {
                if (subtask.epicName.equals(epicName)) {
                    listOfSubtasksOfEpic.add(subtask);
                }
            }
            System.out.println("СПИСОК ПОДЗАДАЧ ЭПИКА: " + epicName);
            for (Subtask task : listOfSubtasksOfEpic) {
                System.out.println(task);
            }
        } else {
            System.out.println("Такого эпика в базе эпиков нет.");
        }
        return listOfSubtasksOfEpic;
    }
}
