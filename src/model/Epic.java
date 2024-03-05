package model;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIdInEpic = new ArrayList<>();

    public Epic(String name, String description, int id) {
        super(name, description, TaskStatus.NEW, id);
    }

    public ArrayList<Integer> getSubtasksIdInEpic() {
        return subtasksIdInEpic;
    }
}
