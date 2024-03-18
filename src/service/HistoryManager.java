package service;

import java.util.LinkedList;
import model.Task;

public interface HistoryManager {

    void add(Task task);

    LinkedList<Task> getHistory();
}
