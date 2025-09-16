package garfield;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main class for the Garfield chatbot application.
 * Handles initialization of UI, storage, and task list, and runs the main program loop.
 */
public class Garfield {

    public ArrayList<Task> items;
    private Storage storage;
    private TaskList tasks;
    private Ui ui;

    public Garfield() {
        this.items = new ArrayList<>();
    }

    public Garfield(String filePath) {
        ui =  new Ui();
        storage = new Storage(filePath);
        try {
            tasks = new TaskList(storage.load());
        } catch (GarfieldException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }

    }

    public void printItems() {
        System.out.println("____________________________________________________________");
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < this.items.size(); i++) {
            System.out.println(" " + (i + 1) + "." + this.items.get(i));
        }
        System.out.println("____________________________________________________________");
    }

    public void run() {
        Scanner sc = new Scanner(System.in);

        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Garfield");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        while (sc.hasNextLine()) {
            String input = sc.nextLine().trim();

            try {
                if (input.equals("bye")) {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println("____________________________________________________________");
                    break;
                } else if (input.equals("list")) {
                    this.printItems();
                } else if (input.startsWith("mark ")) {
                    handleMark(input);
                } else if (input.startsWith("unmark ")) {
                    handleUnmark(input);
                } else if (input.startsWith("delete ")) {
                    handleDelete(input);
                } else if (input.startsWith("todo ")) {
                    handleTodo(input);
                } else if (input.startsWith("deadline ")) {
                    handleDeadline(input);
                } else if (input.startsWith("event ")) {
                    handleEvent(input);
                } else if (input.equals("todo") || input.equals("deadline") || input.equals("event")) {
                    throw new GarfieldException("OOPS!!! The description of a " + input + " cannot be empty.");
                } else if (input.startsWith("find ")) {
                    handleFind(input);
                } else {
                    throw new GarfieldException("OOPS!!! I'm sorry, I don't recognize that command.");
                }
            } catch (GarfieldException e) {
                System.out.println("____________________________________________________________");
                System.out.println(" " + e.getMessage());
                System.out.println("____________________________________________________________");
            }
        }

        sc.close();
    }

    private void handleMark(String input) throws GarfieldException {
        try {
            int index = Integer.parseInt(input.split(" ")[1]) - 1;
            Task task = items.get(index);
            task.markAsDone();
            System.out.println("____________________________________________________________");
            System.out.println(" Nice! I've marked this task as done:");
            System.out.println("   " + task);
            System.out.println("____________________________________________________________");
        } catch (Exception e) {
            throw new GarfieldException("OOPS!!! Invalid task number for mark.");
        }
    }

    private void handleDelete(String input) throws GarfieldException {
        try {
            int index = Integer.parseInt(input.split(" ")[1]) - 1;
            Task task = items.remove(index);
            System.out.println("____________________________________________________________");
            System.out.println(" Noted. I've removed this task:");
            System.out.println("   " + task);
            System.out.println(" Now you have " + items.size() + " tasks in the list.");
            System.out.println("____________________________________________________________");
        } catch (Exception e) {
            throw new GarfieldException("OOPS!!! Invalid task number for delete.");
        }
    }

    private void handleUnmark(String input) throws GarfieldException {
        try {
            int index = Integer.parseInt(input.split(" ")[1]) - 1;
            Task task = items.get(index);
            task.markAsNotDone();
            System.out.println("____________________________________________________________");
            System.out.println(" OK, I've marked this task as not done yet:");
            System.out.println("   " + task);
            System.out.println("____________________________________________________________");
        } catch (Exception e) {
            throw new GarfieldException("OOPS!!! Invalid task number for unmark.");
        }
    }

    private void handleTodo(String input) throws GarfieldException {
        String desc = input.substring(5).trim();
        if (desc.isEmpty()) throw new GarfieldException("OOPS!!! The description of a todo cannot be empty.");
        Task task = new Todo(desc);
        items.add(task);
        System.out.println("____________________________________________________________");
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + items.size() + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    private void handleDeadline(String input) throws GarfieldException {
        try {
            String[] parts = input.substring(9).split(" /by ");
            if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty())
                throw new GarfieldException("OOPS!!! Deadline command requires description and /by date/time.");
            Task task = new Deadline(parts[0].trim(), parts[1].trim());
            items.add(task);
            System.out.println("____________________________________________________________");
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + task);
            System.out.println(" Now you have " + items.size() + " tasks in the list.");
            System.out.println("____________________________________________________________");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GarfieldException("OOPS!!! Deadline command requires /by date/time.");
        }
    }

    private void handleEvent(String input) throws GarfieldException {
        try {
            String[] parts = input.substring(6).split(" /from ");
            if (parts.length < 2) throw new GarfieldException("OOPS!!! Event command requires /from and /to times.");
            String desc = parts[0].trim();
            String[] timeParts = parts[1].split(" /to ");
            if (timeParts.length < 2) throw new GarfieldException("OOPS!!! Event command requires /to time.");
            String from = timeParts[0].trim();
            String to = timeParts[1].trim();
            Task task = new Event(desc, from, to);
            items.add(task);
            System.out.println("____________________________________________________________");
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + task);
            System.out.println(" Now you have " + items.size() + " tasks in the list.");
            System.out.println("____________________________________________________________");
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new GarfieldException("OOPS!!! Event command requires /from and /to times.");
        }
    }

    private void handleFind(String input) throws GarfieldException {
        String keyword = input.substring(5).trim();
        List<Task> found = tasks.findTasks(keyword);
        ui.showFindResults(found);
    }

    /**
     * Returns Garfield's response to user input.
     *
     * @param input The user's input string.
     * @return Garfield's response string.
     */
    public String getResponse(String input) {
        // Parse input, execute command, and return response.
        // For minimal demo, just echo the input:
        if (input.equalsIgnoreCase("bye")) {
            return "Bye. Hope to see you again soon!";
        }
        // TODO: Integrate with your parser and command execution logic.
        return "You said: " + input;
    }

    public static void main(String[] args) {
        Garfield g = new Garfield("data/tasks.txt");
        g.run();
    }
}
