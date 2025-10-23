// Credit to Tsay Yong for code inspiration.
package garfield.core;

import garfield.task.Task;
import garfield.task.Todo;
import garfield.task.Deadline;
import garfield.task.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Mutable list of tasks with operations to add, update, delete, and search.
 */
public class TaskList {

    private final List<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public TaskList(List<Task> initial) {
        assert initial != null : "TaskList initial list must not be null";
        for (Task t : initial) {
            assert t != null : "TaskList must not contain null tasks";
        }
        this.tasks = new ArrayList<>(initial);
    }

    public List<Task> asList() {
        return tasks;
    }

    public int size() {
        return tasks.size();
    }

    private void ensureInRange(int idx) throws GarfieldException {
        if (idx < 1 || idx > tasks.size()) {
            throw new GarfieldException("Usage: <cmd> <task-number>");
        }
    }

    public Task addTodo(String desc) {
        Task t = new Todo(desc);
        tasks.add(t);
        return t;
    }

    public Task addDeadline(String desc, String by) {
        Task t = new Deadline(desc, by);
        tasks.add(t);
        return t;
    }

    public Task addEvent(String desc, String from, String to) {
        Task t = new Event(desc, from, to);
        tasks.add(t);
        return t;
    }

    public Task delete(int idx1) throws GarfieldException {
        ensureInRange(idx1);
        return tasks.remove(idx1 - 1);
    }

    public Task mark(int idx1) throws GarfieldException {
        ensureInRange(idx1);
        Task t = tasks.get(idx1 - 1);
        t.markAsDone();
        return t;
    }

    public Task unmark(int idx1) throws GarfieldException {
        ensureInRange(idx1);
        Task t = tasks.get(idx1 - 1);
        t.markAsNotDone();
        return t;
    }

    public Task snoozeDeadline(int oneBasedIndex, String newBy) {
        int i = oneBasedIndex - 1;
        if (i < 0 || i >= tasks.size()) {
            throw new IllegalArgumentException("That task number is out of range.");
        }
        Task t = tasks.get(i);
        if (!(t instanceof Deadline)) {
            throw new IllegalArgumentException("Snooze with /by works only for deadlines.");
        }
        ((Deadline) t).setBy(newBy);
        return t;
    }

    public Task snoozeEvent(int oneBasedIndex, String newFrom, String newTo) {
        int i = oneBasedIndex - 1;
        if (i < 0 || i >= tasks.size()) {
            throw new IllegalArgumentException("That task number is out of range.");
        }
        Task t = tasks.get(i);
        if (!(t instanceof Event)) {
            throw new IllegalArgumentException("Snooze with /from ... /to ... works only for events.");
        }
        ((Event) t).setSchedule(newFrom, newTo);
        return t;
    }

    /**
     * Returns tasks whose descriptions contain the given keyword
     * (case-insensitive).
     * 
     * @param keyword term to search for
     * @return matching tasks in original order
     */
    public List<Task> find(String keyword) {
        final String kw = keyword.trim().toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(t -> t.toString().toLowerCase(Locale.ROOT).contains(kw))
                .collect(Collectors.toList());
    }
}