package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIdInEpic = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, TaskTypes.EPIC);
        this.endTime = null;
    }

    public ArrayList<Integer> getSubtasksIdInEpic() {
        return subtasksIdInEpic;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
