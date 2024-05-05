package model;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIdInEpic = new ArrayList<>();

    public Epic(int id, String name, String description, TaskTypes type) {
        super(id, name, description, TaskStatus.NEW, type);
    }

    public ArrayList<Integer> getSubtasksIdInEpic() {
        return subtasksIdInEpic;
    }
}
