public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    public Todo(String description, boolean isDone) {
        super(description);
        this.isDone = isDone;
    }

    @Override
    public String toStorageString() {
        return String.format("T | %d | %s", isDone ? 1 : 0, description);
    }

    @Override
    public String toString() {
        return "[" + TaskType.TODO.getCode() + "]" + super.toString();
    }
}