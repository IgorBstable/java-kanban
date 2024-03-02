package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    int taskId = 0;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subTasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public int makeiD() {
        taskId++;
        return taskId;
    }

    public boolean checkTask(Task task) {
        return tasks.containsKey(task.getId());
    }

    public void makeNewTask(Task task) {
        if (!checkTask(task)) {
            tasks.put(makeiD(), task);
            System.out.println("Новая задача создана!");
        } else {
            System.out.println("Такая задача уже существует!" +
                    "Но ее можно обновить :)");
        }
    }

    public HashMap<Integer, Task> getAllTasks() {
        return tasks;
    }

    public void deleteTasks() {
        System.out.println("Удаление задач...");
        tasks.clear();
        System.out.println("Задачи удалены!");
    }

    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        System.out.println(task);
        return task;
    }

    public void updateTask(Task task) {
        // Проверяем, что задача с таким id существует в мапе.
        // В положительном случае обновляем ее в мапе.
        if (checkTask(task)) {
            System.out.println("ОБНОВЛЕНИЕ ЗАДАЧИ с идентификатором " + task.getId());
            tasks.put(task.getId(), task);
            System.out.println("Обновление успешно завершено!");
        } else {
            System.out.println("Задачи с таким id в базе данных нет," +
                    "уточните, пожалуйста, id и тогда мы все обновим :)");
        }

    }

    public void deleteTaskById(int id) {
        System.out.println("УДАЛЕНИЕ ЗАДАЧИ по id");
        tasks.remove(id);
        System.out.println("Удаление задачи по id успешно завершено!");
    }

    public boolean checkSubtask(Subtask subtask) {
        return subTasks.containsKey(subtask.getId());
    }

    public void makeNewSubtask(Subtask subtask) {
        // Проверяем, нет ли такой подзадачи в эпике.
        if (checkSubtask(subtask)) {
            System.out.println("Такая подзадача уже существует!" +
                    "Но ее можно обновить :)");
            return;
        }

        // Получаем id эпика по его имени
        int epicId = getEpicIdByName(subtask.getEpic());

        // Если такого эпика пока не существует (epicId = 0),
        // предупреждаем пользователя.
        if (epicId == 0) {
            System.out.println("Такого эпика пока не существует." +
                    "Создайте его, пожалуйста, перед созданием подзадачи :)");
            return;
        }

        // Если эпик уже существует, добавляем подзадачу в мапу.
        subTasks.put(makeiD(), subtask);
        // А также в список подзадач эпика
        Epic epic = epics.get(epicId);
        epic.subtasksInEpic.add(subtask);

        // Обновляем статус эпика
        updateEpicStatus(subtask);
        System.out.println("Новая подзадача создана!");
    }

    public HashMap<Integer, Subtask> getAllSubtasks() {
        return subTasks;
    }

    public void delSubtasks() {
        subTasks.clear();
        System.out.println("Подзадачи удалены!");

        // Поскольку этот метод удаляет все подзадачи, то необходимо:
        // 1. очистить списки подзадач у всех эпиков
        // 2. обновить статусы всех эпиков на NEW
        for (Epic epic : epics.values()) {
            epic.subtasksInEpic.clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void delSubtaskById(int id) {
        // Удаляем в списке конкретного эпика.
        Subtask subtask = subTasks.get(id);
        // получаем имя эпика
        int epicId = getEpicIdByName(subtask.getEpic()); // получаем id эпика
        Epic epic = epics.get(epicId); // находим эпик в мапе
        Subtask subTaskToRenew = subtask; // вводим временный объект класса Subtask
        for (Subtask val : epic.subtasksInEpic) {
            if (val.getId() == subtask.getId()) { // по id находим подзадачу в списке эпика
                subTaskToRenew = val; // присваиваем ссылку на нее временному объекту
                break;
            }
        }
        epic.subtasksInEpic.remove(subTaskToRenew); // удаляем старую подзадачу из списка
        subTasks.remove(id); // удаляем подзадачу из мапы.
        if (epic.subtasksInEpic.isEmpty()) { // если после этого список эпика пуст,
            epic.setStatus(TaskStatus.NEW); // устанавливаем ему статус NEW
        }
    }

    public Subtask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    public void updateSubTask(Subtask subtask) {
        // Проверяем, что подзадача с таким id существует в мапе.
        // В положительном случае обновляем ее.
        if (checkSubtask(subtask)) {
            System.out.println("ОБНОВЛЕНИЕ ПОДЗАДАЧИ с идентификатором " + subtask.getId());
            // Обновляем в мапе.
            subTasks.put(subtask.getId(), subtask);
            // Обновляем в списке конкретного эпика.
            int epicId = getEpicIdByName(subtask.getEpic()); // получаем id эпика
            Epic epic = epics.get(epicId); // находим эпик в мапе
            Subtask subTaskToRenew = subtask; // вводим временный объект класса Subtask
            for (Subtask val : epic.subtasksInEpic) {
                if (val.getId() == subtask.getId()) { // по id находим подзадачу в списке эпика
                    subTaskToRenew = val; // присваиваем ссылку на нее временному объекту
                    break;
                }
            }
            epic.subtasksInEpic.remove(subTaskToRenew); // удаляем старую подзадачу из списка
            epic.subtasksInEpic.add(subtask); // добавляем новую подзадачу в список
            System.out.println("Обновление успешно завершено!");

            updateEpicStatus(subtask); // Обновляем статус эпика.
        } else {
            System.out.println("Подзадачи с таким id в базе данных нет," +
                    "уточните, пожалуйста, id и тогда мы все обновим :)");
        }
    }

    public boolean checkEpic(Epic epic) {
        return epics.containsKey(epic.getId());
    }

    public void makeNewEpic(Epic epic) {
        // Проверяем, есть ли такой эпик в базе.
        if (checkEpic(epic)) {
            System.out.println("Такой эпик уже есть в базе!");
            return;
        }
        epics.put(makeiD(), epic);
        System.out.println("Новый эпик создан!");
    }

    public HashMap<Integer, Epic> getAllEpics() {
        return epics;
    }

    public void deleteEpics() {
        epics.clear();
        System.out.println("Все эпики удалены!");
    }

    public Epic getEpicById(Integer id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Такого id в базе эпиков нет.");
        }
        return null;
    }

    public void delEpicById (int id) {
        ArrayList<Integer> ids = new ArrayList<>();
        Epic epic = epics.get(id);
        for (Subtask val : epic.subtasksInEpic) { // находим id подзадач эпика
            ids.add(val.getId());                 // и помещаем их в лист
        }
        for (int i : ids) {                     // проходим по созданному листу
            subTasks.remove(i);   // и удаляем из мапы подзадачи эпика
        }

        epics.remove(id); // удаляем эпик из мапы эпиков
    }

    public ArrayList<Subtask> getListOfSubTasksOfEpic(int id) {
        Epic epic = getEpicById(id);
        return epic.subtasksInEpic;
    }

    public int getEpicIdByName(String epicName) {
        int epicId = 0;
        for (Integer val : epics.keySet()) {
            if (epics.get(val).getName().equals(epicName)) {
                epicId = val;
            }
        }
        return epicId;
    }

    public void updateEpicStatus(Subtask subtask) {
        int epicId = getEpicIdByName(subtask.getEpic());
        Epic epic = epics.get(epicId);

        // Проверяем, все ли подзадачи имеют статус NEW.
        // В положительном случае, а также в том случае, если у него нет подзадач,
        // обновляем у эпика статус на NEW и помещаем его в мапу.
        boolean flag = true;
        for (Subtask val : epic.subtasksInEpic) {
            if (!val.getStatus().equals(TaskStatus.NEW)) {
                flag = false;
                break;
            }
        }
        if (flag || epic.subtasksInEpic.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            epics.put(epicId, epic);
            return;
        }

        // Проверяем, все ли подзадачи имеют статус DONE.
        // В положительном случае обновляем у эпика статус на DONE и помещаем его в мапу.
        flag = true;
        for (Subtask val : epic.subtasksInEpic) {
            if (!val.getStatus().equals(TaskStatus.DONE)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            epic.setStatus(TaskStatus.DONE);
            epics.put(epicId, epic);
            return;
        }

        // Если первые два условия не выполнены, обновляем статус эпика на IN_PROGRESS
        // и помещаем его в мапу.
        epic.setStatus(TaskStatus.IN_PROGRESS);
        epics.put(epicId, epic);
    }
}
