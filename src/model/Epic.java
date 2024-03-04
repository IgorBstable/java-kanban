package model;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Integer> subtasksIdInEpic = new ArrayList<>();


    public Epic(String name, String description, int id) {
        super(name, description, TaskStatus.NEW, id);
    }
}
