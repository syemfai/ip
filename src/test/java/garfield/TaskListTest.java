package garfield;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class TaskListTest {

    @Test
    public void addTask_taskAdded_success() {
        TaskList taskList = new TaskList();
        Task todo = new Todo("read book");
        taskList.addTask(todo);
        assertEquals(1, taskList.getTasks().size());
        assertEquals("read book", taskList.getTasks().get(0).description);
    }

    @Test
    public void deleteTask_taskDeleted_success() {
        TaskList taskList = new TaskList();
        Task todo = new Todo("read book");
        taskList.addTask(todo);
        taskList.deleteTask(0);
        assertEquals(0, taskList.getTasks().size());
    }

    @Test
    public void getTasks_returnsCorrectList() {
        TaskList taskList = new TaskList();
        Task todo1 = new Todo("read book");
        Task todo2 = new Todo("write essay");
        taskList.addTask(todo1);
        taskList.addTask(todo2);
        List<Task> tasks = taskList.getTasks();
        assertEquals(2, tasks.size());
        assertEquals("write essay", tasks.get(1).description);
    }
}