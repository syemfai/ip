import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Garfield {

    public ArrayList<String> items;

    public Garfield() {
        this.items = new ArrayList<>();
    }

    public void printItems() {
        System.out.println("____________________________________________________________");
        for (int i = 0; i < this.items.size(); i++) {
            System.out.println(" " + (i + 1) + ". " + this.items.get(i));
        }
        System.out.println("____________________________________________________________");
    }

    public void run(){
        Scanner sc = new Scanner(System.in);

        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Garfield");
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        while (true) {
            String input = sc.nextLine();

            if (input.equals("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            } else if (input.equals("list")) {
                this.printItems();
            } else {
                items.add(input);
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
