import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Deadline extends Task {
    protected LocalDate by;

    public Deadline(String description, String by) {
        super(description);
        this.by = LocalDate.parse(by); // expects yyyy-MM-dd
    }

    public Deadline(String description, boolean isDone, String by) {
        super(description);
        this.isDone = isDone;
        this.by = LocalDate.parse(by); // expects yyyy-MM-dd
    }

    @Override
    public String toStorageString() {
        return String.format("D | %d | %s | %s", isDone ? 1 : 0, description, by.toString());
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by.format(DateTimeFormatter.ofPattern("MMM d yyyy")) + ")";
    }
}