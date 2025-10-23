// Credit to Tsay Yong for code inspiration.
package garfield.task;

import garfield.core.Dates;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Deadline extends Task {
    protected String by;
    private LocalDate date;
    private LocalDateTime dateTime;

    public Deadline(String description, String by) {
        super(description);
        assert by != null : "Deadline 'by' must not be null";
        assert !by.trim().isEmpty() : "Deadline 'by' must not be empty";
        setBy(by);
    }

    public void setBy(String by) {
        this.by = by;
        this.dateTime = Dates.tryParseDateTime(by);
        this.date = (this.dateTime == null) ? Dates.tryParseDate(by) : null;
    }

    public String getBy() {
        return by;
    }

    @Override
    protected TaskType getType() {
        return TaskType.DEADLINE;
    }

    @Override
    public String toString() {
        String display;
        if (dateTime != null) {
            display = Dates.format(dateTime);
        } else if (date != null) {
            display = Dates.format(date);
        } else {
            display = by;
        }
        return super.toString() + " (by: " + display + ")";
    }
}