package garfield;

import garfield.*;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {

    private static final String TEST_FILE_PATH = "data/test_tasks.txt";
    private Storage storage;

    @BeforeEach
    public void setUp() {
        storage = new Storage(TEST_FILE_PATH);
        // Clean up before each test
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        } catch (Exception ignored) {}
    }

    @Test
    public void saveAndLoad_tasksPersisted_success() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book", false));
        tasks.add(new Deadline("return book", true, "2023-09-16"));
        storage.save(tasks);
        
        List<Task> loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (GarfieldException e) {
            fail("Loading tasks failed with exception: " + e.getMessage());
            return;
        }
        assertEquals(2, loadedTasks.size());
        assertEquals("read book", loadedTasks.get(0).description);
        assertTrue(loadedTasks.get(1).isDone);
        assertTrue(loadedTasks.get(1) instanceof Deadline);
    }

    @Test
    public void load_nonExistentFile_returnsEmptyList() {
        List<Task> loadedTasks;
        try {
            loadedTasks = storage.load();
        } catch (GarfieldException e) {
            fail("Loading tasks failed with exception: " + e.getMessage());
            return;
        }
        assertTrue(loadedTasks.isEmpty());
    }

    @AfterEach
    public void tearDown() {
        try {
            Files.deleteIfExists(Paths.get(TEST_FILE_PATH));
        } catch (Exception ignored) {}
    }
}