package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history = new ArrayList<>();

    public ArrayList<Task> getHistory() {
        return history;
    }

    @Override
    public void add(Task task) {
        if (history.size() < 10) {
            history.add(task);
        } else {
            history.addFirst(task);
            history.remove(10);
        }
    }
}
