public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }

    public String toStorageString() {
        throw new UnsupportedOperationException("Use subclass implementation");
    }

    public static Task fromStorageString(String str) {
        String[] parts = str.split(" \\| ");
        switch (parts[0]) {
            case "T":
                return new Todo(parts[2], parts[1].equals("1"));
            case "D":
                return new Deadline(parts[2], parts[1].equals("1"), parts[3]);
            case "E":
                return new Event(parts[2], parts[1].equals("1"), parts[3]);
            default:
                throw new IllegalArgumentException("Unknown task type");
        }
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
