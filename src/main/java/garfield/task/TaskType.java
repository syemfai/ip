package garfield.task;

public enum TaskType {
    TODO('T'),
    DEADLINE('D'),
    EVENT('E');

    private final char symbol;

    TaskType(char symbol) {
        this.symbol = symbol;
    }

    public char symbol() {
        return symbol;
    }
}