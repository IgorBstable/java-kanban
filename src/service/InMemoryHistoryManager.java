package service;

import model.Task;
import java.util.List;
import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> history = new LinkedList<>();
    private static final int HISTORY_SIZE = 10;

    public LinkedList<Task> getHistory() {
        return new LinkedList<>(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() < HISTORY_SIZE) {
            history.add(task);
        } else {
            history.addFirst(task);
            history.removeLast();
        }
    }
}
