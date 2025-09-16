package garfield;

import java.util.List;

public class Ui {
    public void showLoadingError() {
        System.out.println("Error loading tasks from file.");
    }

    public void showWelcome() {
        System.out.println("Hello! I'm Garfield.");
    }

    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
    }

    /**
     * Displays the list of tasks found by the find command.
     *
     * @param foundTasks The list of matching tasks.
     */
    public void showFindResults(List<Task> foundTasks) {
        System.out.println("____________________________________________________________");
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < foundTasks.size(); i++) {
            System.out.println((i + 1) + "." + foundTasks.get(i));
        }
        System.out.println("____________________________________________________________");
    }
}