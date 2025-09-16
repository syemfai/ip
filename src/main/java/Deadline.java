public class Deadline extends Task {
    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    public Deadline(String description, boolean isDone, String by) {
        super(description);
        this.isDone = isDone;
        this.by = by;
    }

    @Override
    public String toStorageString() {
        return String.format("D | %d | %s | %s", isDone ? 1 : 0, description, by);
    }

    @Override
    public String toString() {
        return "[" + TaskType.DEADLINE.getCode() + "]" + super.toString() + " (by: " + by + ")";
    }
}
