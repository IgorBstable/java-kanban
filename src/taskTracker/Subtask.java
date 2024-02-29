package taskTracker;

class Subtask extends Task {
    String epicName;
    public Subtask(String name, String description, TaskStatus status, String epicName) {
        super(name, description, status);
        this.epicName = epicName;
    }
}
