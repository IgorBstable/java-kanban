package service;

import model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void makeNewTask(Task task) {
        super.makeNewTask(task);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void makeNewSubtask(Task subtask) {
        super.makeNewSubtask(subtask);
        save();
    }

    @Override
    public void delSubtasks() {
        super.delSubtasks();
        save();
    }

    @Override
    public void delSubtaskById(int id) {
        super.delSubtaskById(id);
        save();
    }

    @Override
    public void updateSubTask(Task subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void makeNewEpic(Task epic) {
        super.makeNewEpic(epic);
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void delEpicById(int id) {
        super.delEpicById(id);
        save();
    }

    protected void updateEpicStatus(Task subtask) {
        super.updateEpicStatus(subtask);
        save();
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
            throw new ManagerSaveException(e.getMessage());
        }
    }

    private static Task fromString(String value) {
        String[] task = value.split(",");
        int id = Integer.parseInt(task[0]);
        TaskTypes type = TaskTypes.valueOf(task[1]);
        String name = task[2];
        TaskStatus status = TaskStatus.valueOf(task[3]);
        String description = task[4];
        if (type.equals(TaskTypes.TASK)) {
            Task taskFromS = new Task(0, name, description,
                    status, type);
            taskFromS.setId(id);
            return taskFromS;
        } else if (type.equals(TaskTypes.SUBTASK)) {
            int epicId = Integer.parseInt(task[5]);
            Task subTaskFromS = new Subtask(0, name, description,
                    status, type, epicId);
            subTaskFromS.setId(id);
            return subTaskFromS;
        }
        Task epicFromS = new Epic(0, name, description,
                type);
        epicFromS.setId(id);
        return epicFromS;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        String backedContent = Files.readString(file.toPath());
        String[] contentList = backedContent.split("\n");
        FileBackedTaskManager fbtm = new FileBackedTaskManager(file);
        int maxId = 0;
        for (int i = 1; i < contentList.length; i++) {
            Task task = fromString(contentList[i]);
            if (task.getId() > maxId) {
                maxId = task.getId();
            }
            if (task.getType().equals(TaskTypes.TASK)) {
                fbtm.tasks.put(task.getId(), task);
            } else if (task.getType().equals(TaskTypes.SUBTASK)) {
                fbtm.subTasks.put(task.getId(), task);
            } else if (task.getType().equals(TaskTypes.EPIC)) {
                fbtm.epics.put(task.getId(), task);
            }
            fbtm.taskId = maxId;
        }
        return fbtm;
    }
}
