package model;

public class Subtask extends Task {
    private final String epic;

    public Subtask(String name, String description, TaskStatus status, int id, String epic) {
        super(name, description, status, id);
        this.epic = epic;
    }

    public String getEpic() {
        return epic;
    }
}
