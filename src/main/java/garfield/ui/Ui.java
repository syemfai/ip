// Credit to Tsay Yong for code inspiration.
package garfield.ui;

import static garfield.util.Constants.LINE;

import java.util.List;
import garfield.task.Task;

import java.util.Scanner;

public class Ui {

    private final Scanner sc = new Scanner(System.in);

    private void block(String... lines) {
        System.out.println(LINE);
        for (String s : lines) {
            System.out.println(" " + s);
        }
        System.out.println(LINE);
    }

    public void showWelcome() {
        block("Hello! I'm the Garfield Bot", "What can I do for you?");
    }

    public void showGoodbye() {
        block("Bye. Hope to see you again soon!");
    }

    public void showError(String msg) {
        block("Oops! " + msg);
    }

    public void showAdded(Task t, int count) {
        block("Got it. I've added this task:", "  " + t, String.format("Now you have %d tasks in the list.", count));
    }

    public void showRemoved(Task t, int count) {
        block("Noted. I've removed this task:", "  " + t, String.format("Now you have %d tasks in the list.", count));
    }

    public void showMarked(Task t) {
        block("Nice! I've marked this task as done:", "  " + t);
    }

    public void showUnmarked(Task t) {
        block("OK, I've marked this task as not done yet:", "  " + t);
    }

    public void showList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            block("(no tasks yet)");
            return;
        }
        String[] lines = new String[tasks.size() + 1];
        lines[0] = "Here are the tasks in your list:";
        for (int i = 0; i < tasks.size(); i++) {
            lines[i + 1] = (i + 1) + "." + tasks.get(i);
        }
        block(lines);
    }

    public void showFindResults(List<Task> matches) {
        if (matches.isEmpty()) {
            block("No matching tasks found.");
            return;
        }
        String[] lines = new String[matches.size() + 1];
        lines[0] = "Here are the matching tasks in your list:";
        for (int i = 0; i < matches.size(); i++)
            lines[i + 1] = (i + 1) + "." + matches.get(i);
        block(lines);
    }

    public String readCommand() {
        return sc.hasNextLine() ? sc.nextLine().trim() : null;
    }
}