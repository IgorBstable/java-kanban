package model;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description,
                   TaskStatus status, int epicId) {
        super(id, name, description, status, TaskTypes.SUBTASK);
        this.epicId = epicId;
    }

    public Integer getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        String taskDur;
        if (duration != null) {
            taskDur = duration.toString();
            taskDur = taskDur.substring(2, taskDur.length() - 1);
        } else {
            taskDur = null;
        }
        return super.getId() + "," + super.getType() + ","
                + super.getName() + "," + super.getStatus() + ","
                + super.getDescription() + ","
                + super.getStartTime() + ","
                + taskDur + "," + super.getEndTime() + "," + epicId;
    }
}
