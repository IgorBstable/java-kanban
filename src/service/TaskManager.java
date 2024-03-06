package service;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    public int makeId() {
        taskId++;
        return taskId;
    }

    public void makeNewTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            tasks.put(makeId(), task);
        }
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void updateTask(Task task) {
        // Проверяем, что задача с таким id существует в мапе.
        // В положительном случае обновляем ее в мапе.
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    public void makeNewSubtask(Subtask subtask) {
        if (subTasks.containsKey(subtask.getId())) {
            return;
        }

        int epicId = subtask.getEpicId();

        // Если эпик уже существует, добавляем подзадачу в мапу,
        // а также в список подзадач эпика
        subTasks.put(makeId(), subtask);
        Epic epic = epics.get(epicId);
        epic.getSubtasksIdInEpic().add(subtask.getId());

        // Обновляем статус эпика
        updateEpicStatus(subtask);
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<Subtask>(subTasks.values());
    }

    public void delSubtasks() {
        subTasks.clear();
        // Поскольку этот метод удаляет все подзадачи, то необходимо:
        // 1. очистить списки id подзадач у всех эпиков
        // 2. обновить статусы всех эпиков на NEW
        for (Epic epic : epics.values()) {
            epic.getSubtasksIdInEpic().clear();
            epic.setStatus(TaskStatus.NEW);
        }
    }

    public void delSubtaskById(int id) {
        // Удаляем id подзадачи в списке конкретного эпика.
        Subtask subtask = subTasks.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        epic.getSubtasksIdInEpic().remove(subtask.getId());

        // удаляем подзадачу из мапы.
        subTasks.remove(subtask.getId());

        // Полностью пересчитываем статус эпика
        updateEpicStatus(subtask);
    }

    public Subtask getSubtaskById(int id) {
        return subTasks.get(id);
    }

    public void updateSubTask(Subtask subtask) {
        // Проверяем, что подзадача с таким id существует в мапе.
        // В положительном случае обновляем ее.
        if (subTasks.containsKey(subtask.getId())) {
            subTasks.put(subtask.getId(), subtask);
            // В связи с обновлением подзадачи обновляем статус эпика.
            updateEpicStatus(subtask);
        }
    }

    public void makeNewEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        epics.put(makeId(), epic);
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public void delEpicById (int id) {
        Epic epic = epics.get(id);

        // Удаляем подзадачи эпика из мапы подзадач
        for (int val : epic.getSubtasksIdInEpic()) {
            subTasks.remove(val);
        }
        epics.remove(id);
    }

    public ArrayList<Subtask> getListOfSubTasksOfEpic(int id) {
        Epic epic = getEpicById(id);
        ArrayList<Subtask> listOfSubTasksOfEpic = new ArrayList<>();
        for (int val : epic.getSubtasksIdInEpic()) {
            listOfSubTasksOfEpic.add(subTasks.get(val));
        }
        return listOfSubTasksOfEpic;
    }

    private void updateEpicStatus(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        // Проверяем, все ли подзадачи имеют статус NEW.
        // В положительном случае, а также в том случае, если у него нет подзадач,
        // обновляем у эпика статус на NEW.
        boolean flag = true;
        for (int val : epic.getSubtasksIdInEpic()) {
            if (!subTasks.get(val).getStatus().equals(TaskStatus.NEW)) {
                flag = false;
                break;
            }
        }
        if (flag || epic.getSubtasksIdInEpic().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        // Проверяем, все ли подзадачи имеют статус DONE.
        // В положительном случае обновляем у эпика статус на DONE.
        flag = true;
        for (int val : epic.getSubtasksIdInEpic()) {
            if (!subTasks.get(val).getStatus().equals(TaskStatus.DONE)) {
                flag = false;
                break;
            }
        }
        if (flag) {
            epic.setStatus(TaskStatus.DONE);
            return;
        }

        // Если первые два условия не выполнены,
        // обновляем статус эпика на IN_PROGRESS
        epic.setStatus(TaskStatus.IN_PROGRESS);
    }
}
