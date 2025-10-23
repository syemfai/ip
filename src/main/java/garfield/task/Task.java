// Credit to Tsay Yong for code inspiration.
package garfield.task;

public abstract class Task {
    protected final String description;
    protected boolean isDone;

    public Task(String description) {
        assert description != null : "Task description must not be null";
        assert !description.trim().isEmpty() : "Task description must not be empty";
        this.description = description;
        this.isDone = false;
    }

    public void markAsDone() {
        assert !isDone : "markAsDone should not be called if already done";
        this.isDone = true;
    }

    public void markAsNotDone() {
        assert isDone : "markAsNotDone should not be called if already not done";
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    protected abstract TaskType getType();

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return isDone;
    }

    public String pretty() {
        return String.format("[%s][%s] %s", getType().symbol(), getStatusIcon(), description);
    }

    @Override
    public String toString() {
        return pretty();
    }
}