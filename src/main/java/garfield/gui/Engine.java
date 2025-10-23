// Credit to Tsay Yong for code inspiration.
package garfield.gui;

import static garfield.util.Constants.LINE;
import static garfield.util.Constants.SEP_BY;
import static garfield.util.Constants.SEP_FROM;
import static garfield.util.Constants.SEP_TO;

import garfield.core.TaskList;
import garfield.io.Storage;
import garfield.task.Task;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

/**
 * Engine for GUI
 */
public class Engine {
    private final TaskList tasks;
    private final Storage storage;
    private boolean exit = false;

    public Engine() {
        Storage s = new Storage(Paths.get("data", "garfield.jsonl"));
        TaskList t;
        try {
            t = new TaskList(s.load());
        } catch (IOException e) {
            t = new TaskList();
        }
        this.storage = s;
        this.tasks = t;
    }

    public boolean isExit() {
        return exit;
    }

    public String reply(String input) {
        input = input.trim();
        if (input.isEmpty())
            return "Please type a command.";

        try {
            if (input.equals("bye")) {
                exit = true;
                return block("Bye. Hope to see you again soon!");
            }
            if (input.equals("list")) {
                return block(renderList(tasks.asList()));
            }
            if (input.startsWith("mark")) {
                int idx = parseIndex("mark", input, tasks.size());
                Task t = tasks.mark(idx);
                persist();
                return block("Nice! I've marked this task as done:\n  " + t);
            }
            if (input.startsWith("unmark")) {
                int idx = parseIndex("unmark", input, tasks.size());
                Task t = tasks.unmark(idx);
                persist();
                return block("OK, I've marked this task as not done yet:\n  " + t);
            }
            if (input.startsWith("delete")) {
                int idx = parseIndex("delete", input, tasks.size());
                Task t = tasks.delete(idx);
                persist();
                return block("Noted. I've removed this task:\n  " + t +
                        "\nNow you have " + tasks.size() + " tasks in the list.");
            }
            if (input.startsWith("todo")) {
                String desc = after(input, "todo");
                if (desc.isEmpty())
                    return block("The description of a todo cannot be empty.");
                Task t = tasks.addTodo(desc);
                persist();
                return block(added(t));
            }
            if (input.startsWith("deadline")) {
                String[] p = splitTwo(input, "deadline", SEP_BY);
                if (p == null)
                    return block("For deadlines, use: deadline <desc> /by <when>");
                Task t = tasks.addDeadline(p[0], p[1]);
                persist();
                return block(added(t));
            }
            if (input.startsWith("event")) {
                String[] p = splitThree(input, "event", SEP_FROM, SEP_TO);
                if (p == null)
                    return block("For events, use: event <desc> /from <start> /to <end>");
                Task t = tasks.addEvent(p[0], p[1], p[2]);
                persist();
                return block(added(t));
            }
            if (input.startsWith("snooze")) {
                String[] pBy = splitIndexAndTwo(input, "snooze", SEP_BY);
                if (pBy != null) {
                    int idx = parseIndexToken(pBy[0], tasks.size());
                    String when = pBy[1];
                    Task t = tasks.snoozeDeadline(idx, when);
                    persist();
                    return block("Rescheduled this task:\n  " + t);
                }
                String[] pRange = splitIndexAndRange(input, "snooze", SEP_FROM, SEP_TO);
                if (pRange != null) {
                    int idx = parseIndexToken(pRange[0], tasks.size());
                    String start = pRange[1];
                    String end = pRange[2];
                    Task t = tasks.snoozeEvent(idx, start, end);
                    persist();
                    return block("Rescheduled this task:\n  " + t);
                }
                return block("Usage:\n"
                        + "  snooze <task-number> " + SEP_BY + " <when>\n"
                        + "  snooze <task-number> " + SEP_FROM + " <start> " + SEP_TO + " <end>");
            }

            if (input.startsWith("find")) {
                String kw = after(input, "find");
                if (kw.isEmpty())
                    return block("Usage: find <keyword>");
                List<Task> matches = tasks.find(kw);
                return block(renderMatches(matches));
            }
            return block("I'm sorry, but I don't know what that means :-(");
        } catch (Exception e) {
            return block("Error: " + e.getMessage());
        }
    }

    private void persist() {
        try {
            storage.save(tasks.asList());
        } catch (IOException ignored) {
        }
    }

    private static String after(String input, String head) {
        return input.length() > head.length() ? input.substring(head.length()).trim() : "";
    }

    private static int parseIndex(String cmd, String input, int max) {
        String[] parts = input.split("\\s+");
        if (parts.length != 2)
            throw new IllegalArgumentException("Usage: " + cmd + " <task-number>");
        int idx = Integer.parseInt(parts[1]);
        if (idx < 1 || idx > max)
            throw new IllegalArgumentException("That task number is out of range.");
        return idx;
    }

    private static String[] splitTwo(String input, String head, String sep) {
        String rest = after(input, head);
        int pos = rest.indexOf(" " + sep + " ");
        if (pos < 0)
            return null;
        String a = rest.substring(0, pos).trim();
        String b = rest.substring(pos + sep.length() + 2).trim();
        if (a.isEmpty() || b.isEmpty())
            return null;
        return new String[] { a, b };
    }

    private static String[] splitThree(String input, String head, String sepA, String sepB) {
        String rest = after(input, head);
        int a = rest.indexOf(" " + sepA + " ");
        int b = rest.indexOf(" " + sepB + " ");
        if (a < 0 || b < 0 || b < a)
            return null;
        String d = rest.substring(0, a).trim();
        String f = rest.substring(a + sepA.length() + 2, b).trim();
        String t = rest.substring(b + sepB.length() + 2).trim();
        if (d.isEmpty() || f.isEmpty() || t.isEmpty())
            return null;
        return new String[] { d, f, t };
    }

    private static int parseIndexToken(String idxStr, int max) {
        int idx;
        try {
            idx = Integer.parseInt(idxStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The task number for 'snooze' must be an integer.");
        }
        if (idx < 1 || idx > max) {
            throw new IllegalArgumentException("That task number is out of range.");
        }
        return idx;
    }

    private static String[] splitIndexAndTwo(String input, String head, String sep) {
        String rest = after(input, head);
        String[] headTail = rest.split("\\s+", 2);
        if (headTail.length < 2)
            return null;

        String idx = headTail[0];
        String tail = headTail[1].trim();

        int pos = tail.indexOf(sep);
        if (pos < 0)
            return null;

        String value = tail.substring(pos + sep.length()).trim();
        if (value.isEmpty())
            return null;

        return new String[] { idx, value };
    }

    private static String[] splitIndexAndRange(String input, String head, String sepA, String sepB) {
        String rest = after(input, head);
        String[] headTail = rest.split("\\s+", 2);
        if (headTail.length < 2)
            return null;

        String idx = headTail[0];
        String tail = headTail[1].trim();

        int a = tail.indexOf(sepA);
        if (a < 0)
            return null;

        int b = tail.indexOf(sepB, a + sepA.length());
        if (b < 0)
            return null;

        String from = tail.substring(a + sepA.length(), b).trim();
        String to = tail.substring(b + sepB.length()).trim();

        if (from.isEmpty() || to.isEmpty())
            return null;

        return new String[] { idx, from, to };
    }

    private String added(Task t) {
        return "Got it. I've added this task:\n  " + t +
                "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    private static String renderList(List<Task> items) {
        if (items.isEmpty())
            return "(no tasks yet)";
        StringBuilder sb = new StringBuilder("Here are the tasks in your list:");
        for (int i = 0; i < items.size(); i++) {
            sb.append("\n").append(i + 1).append(".").append(items.get(i));
        }
        return sb.toString();
    }

    private static String renderMatches(List<Task> items) {
        if (items.isEmpty())
            return "No matching tasks found.";
        StringBuilder sb = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < items.size(); i++) {
            sb.append("\n").append(i + 1).append(".").append(items.get(i));
        }
        return sb.toString();
    }

    private static String block(String... lines) {
        StringBuilder sb = new StringBuilder(LINE + "\n");
        for (String s : lines) {
            sb.append(' ').append(s).append('\n');
        }
        sb.append(LINE);
        return sb.toString();
    }
}