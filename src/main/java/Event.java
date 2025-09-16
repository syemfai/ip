public class Event extends Task {
    protected String from;
    protected String to;
    protected String at;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }
    
    public Event(String description, boolean isDone, String at) {
        super(description);
        this.isDone = isDone;
        this.at = at;
    }

    @Override
    public String toStorageString() {
        return String.format("E | %d | %s | %s", isDone ? 1 : 0, description, at);
    }

    @Override
    public String toString() {
        return "[" + TaskType.EVENT.getCode() + "]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
