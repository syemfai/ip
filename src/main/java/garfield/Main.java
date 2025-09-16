package garfield;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * The main JavaFX application class for Garfield chatbot.
 */
public class Main extends Application {
    private Garfield garfield = new Garfield("data/tasks.txt");
    private TextArea dialogContainer;
    private TextField userInput;

    @Override
    public void start(Stage stage) {
        dialogContainer = new TextArea();
        dialogContainer.setEditable(false);
        dialogContainer.setWrapText(true);

        userInput = new TextField();
        userInput.setPromptText("Type your command here...");
        userInput.setOnAction(event -> handleUserInput());

        VBox layout = new VBox(10, dialogContainer, userInput);
        layout.setPrefSize(400, 600);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.setTitle("Garfield Chatbot");
        stage.show();

        dialogContainer.appendText("Hello! I'm Garfield.\n");
    }

    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) return;
        dialogContainer.appendText("You: " + input + "\n");
        String response = garfield.getResponse(input);
        dialogContainer.appendText("Garfield: " + response + "\n");
        userInput.clear();
    }
}