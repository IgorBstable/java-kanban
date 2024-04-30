package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, TaskStatus status, TaskTypes type, int epicId) {
        super(id, name, description, status, type);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return super.getId() + "," + super.getType() + ","
                + super.getName() + "," + super.getStatus() + ","
                + super.getDescription() + "," + epicId;
    }
}
