package service;

import model.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int taskId = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Task> subTasks = new HashMap<>();
    protected final Map<Integer, Task> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();


    private int makeId() {
        taskId++;
        return taskId;
    }

    @Override
    public void makeNewTask(Task task) {
        if (!tasks.containsKey(task.getId()) && !isTaskIntersected(task)) {
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
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
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
        if (tasks.containsKey(task.getId()) && !isTaskIntersected(task)) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void makeNewSubtask(Task subtask) {
        if (!tasks.containsKey(subtask.getId()) && !isTaskIntersected(subtask)) {
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

            // Обновляем статус эпика и его начало/конец
            updateEpicStatus(subtask);
            updateEpicStartAndEndTime(subtask);
        }
    }

    @Override
    public ArrayList<Task> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void delSubtasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }

        subTasks.clear();
        // Поскольку этот метод удаляет все подзадачи, то необходимо:
        // 1. очистить списки id подзадач у всех эпиков
        // 2. обновить статусы всех эпиков на NEW
        // 3. в поля начало/конец установить null
        for (Task epic : epics.values()) {
            Epic epic1 = (Epic) epic;
            epic1.getSubtasksIdInEpic().clear();
            epic1.setStatus(TaskStatus.NEW);
            epic1.setStartTime(null);
            epic1.setEndTime(null);
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
        epic1.getSubtasksIdInEpic().remove((Integer) subtask.getId());

        // удаляем подзадачу из мапы.
        subTasks.remove(subtask.getId());
        // Полностью пересчитываем статус эпика
        updateEpicStatus(subtask);
        // Обновляем поля начало/конец
        updateEpicStartAndEndTime(subtask);
        // удаляем подзадачу из истории
        historyManager.remove(id);
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
            // В связи с обновлением подзадачи обновляем статус эпика,
            // а также начало/конец.
            updateEpicStatus(subtask);
            updateEpicStartAndEndTime(subtask);
        }
    }

    @Override
    public void makeNewEpic(Task epic) {
        Epic epic1 = (Epic) epic;
        if (!tasks.containsKey(epic1.getId()) && !isTaskIntersected(epic1)) {
            int id = makeId();
            epic1.setId(id);
            epics.put(id, epic1);
        }
    }

    @Override
    public ArrayList<Task> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteEpics() {
        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }

        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }

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

        // Удаляем подзадачи эпика из мапы подзадач и из истории
        for (int val : epic1.getSubtasksIdInEpic()) {
            subTasks.remove(val);
            historyManager.remove(val);
        }
        epics.remove(id);
        historyManager.remove(id);
    }

    @Override
    public ArrayList<Task> getListOfSubTasksOfEpic(int id) {
        Task epic = getEpicById(id);
        Epic epic1 = (Epic) epic;
        ArrayList<Task> listOfSubTasksOfEpic = new ArrayList<>();

        epic1.getSubtasksIdInEpic().stream()
                .map(val -> listOfSubTasksOfEpic.add(subTasks.get(val)))
                .collect(Collectors.toList());

        return listOfSubTasksOfEpic;
    }

    protected void updateEpicStatus(Task subtask) {
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

    protected void updateEpicStartAndEndTime(Task subtask) {
        Subtask subtask1 = (Subtask) subtask;
        int epicId = subtask1.getEpicId();
        Task epic = epics.get(epicId);
        Epic epic1 = (Epic) epic;
        ArrayList<Task> subTasks = getListOfSubTasksOfEpic(epicId);
        if (subTasks.isEmpty()) {
            return;
        }
        LocalDateTime epicStart = subTasks.getFirst().getStartTime();
        LocalDateTime epicEnd = subTasks.getFirst().getEndTime();
        for (Task task : subTasks) {
            LocalDateTime taskStart = task.getStartTime();
            LocalDateTime taskEnd = task.getEndTime();
            if (taskStart != null) {
                if (epicStart.isAfter(taskStart)) {
                    epicStart = taskStart;
                    epic1.setStartTime(epicStart);
                }
            }
            if (taskEnd != null) {
                if (epicEnd.isBefore(taskEnd)) {
                    epicEnd = taskEnd;
                    epic1.setEndTime(epicEnd);
                }
            }
        }
    }

    public ArrayList<Task> getPrioritizedTasks() {
        ArrayList<Task> tasksAndSubtasks = new ArrayList<>();
        for (Task task : tasks.values()) {
            if (task.getStartTime() != null) {
                tasksAndSubtasks.add(task);
            }
        }
        for (Task task : subTasks.values()) {
            if (task.getStartTime() != null) {
                tasksAndSubtasks.add(task);
            }
        }

        TreeSet<Task> sortedTasks = new TreeSet<>((Task task1, Task task2) -> {
            if (task1.getStartTime()
                    .isAfter(task2.getStartTime())) {
                return 1;
            } else if (task1.getStartTime()
                    .isBefore(task2.getStartTime())) {
                return -1;
            }
            return 0;
        });
        sortedTasks.addAll(tasksAndSubtasks);
        return new ArrayList<>(sortedTasks);
    }

    public boolean isTaskIntersected(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }

        ArrayList<Task> sortedTasks = getPrioritizedTasks();
        if (sortedTasks.isEmpty()) {
            return false;
        }

        LocalDateTime firstStart = sortedTasks.getFirst().getStartTime();
        if (task.getStartTime().isBefore(firstStart)
                && task.getEndTime().isBefore(firstStart)) {
            return false;
        }

        for (Task t : sortedTasks) {
            if (task.getStartTime().isEqual(t.getStartTime())) {
                return true;
            }
        }

        for (Task t : sortedTasks) {
            if (task.getStartTime().isAfter(t.getStartTime())
                    && task.getStartTime().isAfter(t.getEndTime())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }
}
