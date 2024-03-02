package model;

import java.util.ArrayList;

public class Epic extends Task {
    public ArrayList<Subtask> subtasksInEpic = new ArrayList<>();


    public Epic(String name, String description, TaskStatus status, int id) {
        super(name, description, status, id);
    }
}
