import java.util.Scanner;
import java.util.ArrayList;

public class Garfield {

    public ArrayList<Task> items;

    public Garfield() {
        this.items = new ArrayList<>();
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

        while (true) {
            String input = sc.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            } else if (input.equals("list")) {
                this.printItems();
            } else if (input.startsWith("mark ")) {
                try {
                    int index = Integer.parseInt(input.split(" ")[1]) - 1;
                    Task task = items.get(index);
                    task.markAsDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + task);
                    System.out.println("____________________________________________________________");
                } catch (Exception e) {
                    System.out.println("Invalid task number.");
                }
            } else if (input.startsWith("unmark ")) {
                try {
                    int index = Integer.parseInt(input.split(" ")[1]) - 1;
                    Task task = items.get(index);
                    task.markAsNotDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + task);
                    System.out.println("____________________________________________________________");
                } catch (Exception e) {
                    System.out.println("Invalid task number.");
                }
            } else {
                Task task = new Task(input);
                items.add(task);
                System.out.println("____________________________________________________________");
                System.out.println(" added: " + input);
                System.out.println("____________________________________________________________");
            }
        }

        sc.close();
    }

    public static void main(String[] args) {
        Garfield g = new Garfield();
        g.run();
    }
}
