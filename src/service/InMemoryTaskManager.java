package service;

import model.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class InMemoryTaskManager implements TaskManager {
    private int taskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Task> subTasks = new HashMap<>();
    private final HashMap<Integer, Task> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();


    private int makeId() {
        taskId++;
        return taskId;
    }

    @Override
    public void makeNewTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            int id = makeId();
            task.setId(id);
            tasks.put(id, task);
        }
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public void updateTask(Task task) {
        // Проверяем, что задача с таким id существует в мапе.
        // В положительном случае обновляем ее в мапе.
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    @Override
    public void makeNewSubtask(Task subtask) {
        if (subTasks.containsKey(subtask.getId())) {
            return;
        }

        Subtask subtask1 = (Subtask) subtask;
        int epicId = subtask1.getEpicId();

        // Если эпик уже существует, добавляем подзадачу в мапу,
        // а также в список подзадач эпика
        int id = makeId();
        subtask.setId(id);
        subTasks.put(id, subtask);
        Task epic = epics.get(epicId);
        Epic epic1 = (Epic) epic;
        epic1.getSubtasksIdInEpic().add(subtask.getId());

        // Обновляем статус эпика
        updateEpicStatus(subtask);
    }

    @Override
    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void delSubtasks() {
        subTasks.clear();
        // Поскольку этот метод удаляет все подзадачи, то необходимо:
        // 1. очистить списки id подзадач у всех эпиков
        // 2. обновить статусы всех эпиков на NEW
        for (Task epic : epics.values()) {
            Epic epic1 = (Epic) epic;
            epic1.getSubtasksIdInEpic().clear();
            epic1.setStatus(TaskStatus.NEW);
        }
    }

    @Override
    public void delSubtaskById(int id) {
        // Удаляем id подзадачи в списке конкретного эпика.
        Task subtask = subTasks.get(id);
        Subtask subtask1 = (Subtask) subtask;
        int epicId = subtask1.getEpicId();
        Task epic = epics.get(epicId);
        Epic epic1 = (Epic) epic;
        epic1.getSubtasksIdInEpic().remove(subtask.getId());

        // удаляем подзадачу из мапы.
        subTasks.remove(subtask.getId());
        // Полностью пересчитываем статус эпика
        updateEpicStatus(subtask);
    }

    @Override
    public Task getSubtaskById(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public void updateSubTask(Task subtask) {
        // Проверяем, что подзадача с таким id существует в мапе.
        // В положительном случае обновляем ее.
        if (subTasks.containsKey(subtask.getId())) {
            subTasks.put(subtask.getId(), subtask);
            // В связи с обновлением подзадачи обновляем статус эпика.
            updateEpicStatus(subtask);
        }
    }

    @Override
    public void makeNewEpic(Task epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        int id = makeId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subTasks.clear();
    }

    @Override
    public Task getEpicById(Integer id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void delEpicById(int id) {
        Task epic = epics.get(id);
        Epic epic1 = (Epic) epic;

        // Удаляем подзадачи эпика из мапы подзадач
        for (int val : epic1.getSubtasksIdInEpic()) {
            subTasks.remove(val);
        }
        epics.remove(id);
    }

    @Override
    public ArrayList<Task> getListOfSubTasksOfEpic(int id) {
        Task epic = getEpicById(id);
        Epic epic1 = (Epic) epic;
        ArrayList<Task> listOfSubTasksOfEpic = new ArrayList<>();
        for (int val : epic1.getSubtasksIdInEpic()) {
            listOfSubTasksOfEpic.add(subTasks.get(val));
        }
        return listOfSubTasksOfEpic;
    }

    private void updateEpicStatus(Task subtask) {
        Subtask subtask1 = (Subtask) subtask;
        int epicId = subtask1.getEpicId();
        Task epic = epics.get(epicId);
        Epic epic1 = (Epic) epic;

        // Проверяем, все ли подзадачи имеют статус NEW.
        // В положительном случае, а также в том случае, если у него нет подзадач,
        // обновляем у эпика статус на NEW.
        boolean flag = true;
        for (int val : epic1.getSubtasksIdInEpic()) {
            if (!subTasks.get(val).getStatus().equals(TaskStatus.NEW)) {
                flag = false;
                break;
            }
        }
        if (flag || epic1.getSubtasksIdInEpic().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        // Проверяем, все ли подзадачи имеют статус DONE.
        // В положительном случае обновляем у эпика статус на DONE.
        flag = true;
        for (int val : epic1.getSubtasksIdInEpic()) {
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


    @Override
    public LinkedList<Task> getHistoryManager() {
        return historyManager.getHistory();
    }
}
