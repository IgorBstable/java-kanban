package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;

public interface TaskManager {
    int makeId();

    void makeNewTask(Task task);

    ArrayList<Task> getAllTasks();

    void deleteTasks();

    Task getTaskById(int id);

    void updateTask(Task task);

    void deleteTaskById(int id);

    void makeNewSubtask(Subtask subtask);

    ArrayList<Subtask> getAllSubtasks();

    void delSubtasks();

    void delSubtaskById(int id);

    Subtask getSubtaskById(int id);

    void updateSubTask(Subtask subtask);

    void makeNewEpic(Epic epic);

    ArrayList<Epic> getAllEpics();

    void deleteEpics();

    Epic getEpicById(Integer id);

    void delEpicById(int id);

    ArrayList<Subtask> getListOfSubTasksOfEpic(int id);

    void updateEpicStatus(Subtask subtask);

    HistoryManager getHistoryManager();
}
