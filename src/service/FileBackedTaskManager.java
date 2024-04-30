package service;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    File file;
    private int taskId = 0;
    private static final Map<Integer, Task> tasks = new HashMap<>();
    private static final Map<Integer, Task> subTasks = new HashMap<>();
    private static final Map<Integer, Task> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

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
        save();
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
        save();
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
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        tasks.remove(id);
        historyManager.remove(id);
        save();
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

        save();
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
        for (Task epic : epics.values()) {
            Epic epic1 = (Epic) epic;
            epic1.getSubtasksIdInEpic().clear();
            epic1.setStatus(TaskStatus.NEW);
        }
        save();
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
        // удаляем подзадачу из истории
        historyManager.remove(id);

        save();
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
        save();
    }

    @Override
    public void makeNewEpic(Task epic) {
        if (epics.containsKey(epic.getId())) {
            return;
        }
        int id = makeId();
        epic.setId(id);
        epics.put(id, epic);

        save();
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

        save();
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

        save();
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

        save();
    }


    @Override
    public List<Task> getHistoryManager() {
        return historyManager.getHistory();
    }

    private void save() {
        try (Writer filewriter = new FileWriter(file)) {
            filewriter.write("id,type,name,status,description,epic\n");
            ArrayList<Task> tasks = getAllTasks();
            for (Task task : tasks) {
                filewriter.write(task.toString() + "\n");
            }
            ArrayList<Task> subTasks = getAllSubtasks();
            for (Task task : subTasks) {
                filewriter.write(task.toString() + "\n");
            }
            ArrayList<Task> epics = getAllEpics();
            for (Task task : epics) {
                filewriter.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Ошибка ввода!");
            } catch (ManagerSaveException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static Task fromString(String value) {
        String[] task = value.split(",");
        if (task[1].equals(TaskTypes.TASK.toString())) {
            int id = Integer.parseInt(task[0]);
            Task taskFromS = new Task(0, task[2], task[4],
                    TaskStatus.valueOf(task[3]), TaskTypes.valueOf(task[1]));
            taskFromS.setId(id);
            return taskFromS;
        } else if (task[1].equals(TaskTypes.SUBTASK.toString())) {
            int id = Integer.parseInt(task[0]);
            Task subTaskFromS = new Subtask(0, task[2], task[4],
                    TaskStatus.valueOf(task[3]), TaskTypes.valueOf(task[1]),
                    Integer.parseInt(task[5]));
            subTaskFromS.setId(id);
            return subTaskFromS;
        }
        int id = Integer.parseInt(task[0]);
        Task epicFromS = new Epic(0, task[2], task[4],
                TaskStatus.valueOf(task[3]), TaskTypes.valueOf(task[1]));
        epicFromS.setId(id);
        return epicFromS;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        String backedContent = Files.readString(file.toPath());
        String[] contentList = backedContent.split("\n");
        FileBackedTaskManager fbtm = new FileBackedTaskManager(file);
        for (int i = 1; i < contentList.length; i++) {
            Task task = fromString(contentList[i]);
            if (task.getType().equals(TaskTypes.TASK)) {
                fbtm.tasks.put(task.getId(), task);
            } else if (task.getType().equals(TaskTypes.SUBTASK)) {
                fbtm.subTasks.put(task.getId(), task);
            } else if (task.getType().equals(TaskTypes.EPIC)) {
                fbtm.epics.put(task.getId(), task);
            }
            Task lastTask = fromString(contentList[contentList.length - 1]);
            fbtm.taskId = lastTask.getId();
        }
        return fbtm;
    }
}
