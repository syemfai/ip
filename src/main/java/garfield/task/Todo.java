// Credit to Tsay Yong for code inspiration.
package garfield.task;

public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    @Override
    protected TaskType getType() {
        return TaskType.TODO;
    }
}