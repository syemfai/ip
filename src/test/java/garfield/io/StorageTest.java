package garfield.io;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import garfield.core.TaskList;
import garfield.task.Task;
import garfield.task.Todo;
import garfield.task.Deadline;
import garfield.task.Event;

public class StorageTest {

    @TempDir
    Path tempDir;

    @Test
    void saveThenLoad_roundTrip() throws Exception {
        Path file = tempDir.resolve("data").resolve("garfield.jsonl");
        Storage storage = new Storage(file);

        TaskList list = new TaskList();
        Task t1 = list.addTodo("read book");
        Task t2 = list.addDeadline("return book", "2019-12-02");
        Task t3 = list.addEvent("meet boss", "2019-12-02 1400", "2019-12-02 1500");
        t2.markAsDone();

        storage.save(list.asList());

        List<Task> loaded = storage.load();
        assertEquals(3, loaded.size());

        Task a = loaded.get(0);
        assertTrue(a instanceof Todo);
        assertEquals("read book", a.getDescription());
        assertFalse(a.isDone());

        Task b = loaded.get(1);
        assertTrue(b instanceof Deadline);
        Deadline d = (Deadline) b;
        assertEquals("return book", d.getDescription());
        assertEquals("2019-12-02", d.getBy());
        assertTrue(d.isDone());

        Task c = loaded.get(2);
        assertTrue(c instanceof Event);
        Event e = (Event) c;
        assertEquals("meet boss", e.getDescription());
        assertEquals("2019-12-02 1400", e.getFrom());
        assertEquals("2019-12-02 1500", e.getTo());
    }
}