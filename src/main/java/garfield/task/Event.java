// Credit to Tsay Yong for code inspiration.
package garfield.task;

import garfield.core.Dates;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Event extends Task {
    protected String from;
    protected String to;

    private LocalDateTime fromDt, toDt;
    private LocalDate fromDate, toDate;

    public Event(String description, String from, String to) {
        super(description);
        assert from != null && to != null : "Event times must not be null";
        assert !from.trim().isEmpty() && !to.trim().isEmpty() : "Event times must not be empty";
        setSchedule(from, to);
        assertNonDecreasing();
    }

    public void setSchedule(String from, String to) {
        this.from = from;
        this.to = to;

        this.fromDt = Dates.tryParseDateTime(from);
        this.toDt = Dates.tryParseDateTime(to);

        this.fromDate = (fromDt == null) ? Dates.tryParseDate(from) : null;
        this.toDate = (toDt == null) ? Dates.tryParseDate(to) : null;

        assertNonDecreasing();
    }

    private void assertNonDecreasing() {
        // If both sides are date-times, compare them
        if (fromDt != null && toDt != null) {
            assert !toDt.isBefore(fromDt) : "Event end must be >= start";
        }

        // If both sides are dates (no time), compare them
        if (fromDate != null && toDate != null) {
            assert !toDate.isBefore(fromDate) : "Event end must be >= start";
        }

        // Mixed types (one parsed as date, the other as datetime) are not asserted
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    @Override
    protected TaskType getType() {
        return TaskType.EVENT;
    }

    @Override
    public String toString() {
        String fromDisp = (fromDt != null) ? Dates.format(fromDt)
                : (fromDate != null) ? Dates.format(fromDate)
                        : from;
        String toDisp = (toDt != null) ? Dates.format(toDt)
                : (toDate != null) ? Dates.format(toDate)
                        : to;
        return super.toString() + " (from: " + fromDisp + " to: " + toDisp + ")";
    }
}