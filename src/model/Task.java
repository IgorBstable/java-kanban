package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private TaskTypes type;
    private String name;
    private TaskStatus status;
    private String description;
    protected LocalDateTime startTime;
    protected Duration duration;


    public Task(int id, String name, String description, TaskStatus status,
                TaskTypes type) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.status = status;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public TaskTypes getType() {
        return type;
    }

    public void setType(TaskTypes type) {
        this.type = type;
    }

    public LocalDateTime getStartTime() {
        if (startTime != null) {
            return startTime;
        }
        return null;
    }

    public Duration getDuration() {
        if (duration != null) {
            return duration;
        }
        return null;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plus(duration);
        }
        return null;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
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
        return id + "," + type + "," + name + ","
                + status + "," + description + ","
                + startTime + "," + taskDur + ","
                + getEndTime();
    }
}
