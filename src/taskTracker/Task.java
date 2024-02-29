package taskTracker;

import java.util.Objects;

class Task {
    String Name;
    String Description;
    TaskStatus Status;


    public Task(String name, String description, TaskStatus status) {
        Name = name;
        Description = description;
        Status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(Name, task.Name) && Objects.equals(Description, task.Description) && Status == task.Status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Name, Description, Status);
    }

    @Override
    public String toString() {
        return "Название='" + Name + '\'' +
                ", Описание='" + Description + '\'' +
                ", Статус=" + Status;
    }
}
