// Credit to Tsay Yong for code inspiration.
package garfield.gui;

import java.util.Objects;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

/**
 * A GUI for Garfield with asymmetric chat bubbles and error highlighting.
 * It uses Engine.reply(...) to generate responses and strips CLI ASCII boxes
 * before showing the reply in the GUI.
 */
public class Main extends Application {

    private final Engine engine = new Engine();

    private ScrollPane scrollPane;
    private VBox dialogContainer;
    private TextField input;
    private Button send;

    // Optional avatars (put images in resources if you have them)
    private Image userImage;
    private Image botImage;

    @Override
    public void start(Stage stage) {
        // Try to load avatars; fine if absent
        userImage = loadImage("/images/user.jpg");
        botImage = loadImage("/images/bot.jpg");

        // ==== Layout nodes ====
        dialogContainer = new VBox(8);
        dialogContainer.setPadding(new Insets(10));
        dialogContainer.setFillWidth(true);

        scrollPane = new ScrollPane(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        input = new TextField();
        input.setPromptText("Type a command…  (Enter to send)");
        Tooltip.install(input,
                new Tooltip("Commands: todo, deadline, event, list, mark, unmark, delete, find, snooze, bye"));

        send = new Button("Send");
        send.setDefaultButton(true); // Enter triggers send

        HBox inputBar = new HBox(8, input, send);
        inputBar.setPadding(new Insets(8, 10, 10, 10));
        HBox.setHgrow(input, Priority.ALWAYS);

        AnchorPane root = new AnchorPane(scrollPane, inputBar);
        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, 52.0); // leaves space for input bar

        AnchorPane.setLeftAnchor(inputBar, 0.0);
        AnchorPane.setRightAnchor(inputBar, 0.0);
        AnchorPane.setBottomAnchor(inputBar, 0.0);

        Scene scene = new Scene(root, 420, 560);
        scene.getStylesheets().addAll(
                resourceUrl("/css/main.css"),
                resourceUrl("/css/dialog-box.css"));

        stage.setTitle("Garfield");
        stage.setMinWidth(380);
        stage.setMinHeight(420);
        stage.setScene(scene);
        var icon = loadImage("/images/app-icon.png");
        if (icon != null) {
            stage.getIcons().add(icon);
        }
        stage.show();

        // ==== Handlers ====
        Runnable handleSend = () -> {
            String text = input.getText();
            if (text == null || text.isBlank())
                return;

            // 1) show user bubble (right-aligned)
            dialogContainer.getChildren().add(userBubble(text));

            // 2) get bot reply from Engine, strip ASCII block for GUI
            String raw = engine.reply(text);
            String reply = stripBlock(raw);
            boolean isError = raw.contains("Error:") || reply.startsWith("OOPS!!!");

            // 3) show bot bubble (left-aligned); red if error
            dialogContainer.getChildren().add(botBubble(reply, isError));

            input.clear();

            // 4) disable input on exit
            if (engine.isExit()) {
                input.setDisable(true);
                send.setDisable(true);
            }
        };

        send.setOnAction(e -> handleSend.run());
        input.setOnAction(e -> handleSend.run());
        // Ctrl+Enter also sends
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && e.isControlDown()) {
                handleSend.run();
            }
        });

        // Always scroll to bottom as content grows
        dialogContainer.heightProperty().addListener((obs, oldH, newH) -> scrollPane.setVvalue(1.0));
    }

    // ===== Helpers =====

    /** User bubble: right-aligned row, small avatar on RIGHT, distinct color. */
    private Node userBubble(String text) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.TOP_RIGHT);
        row.getStyleClass().add("user-row");

        Node bubble = bubbleLabel(text, false, false);
        Node avatar = avatarNode(userImage);

        row.getChildren().addAll(bubble, avatar);
        return row;
    }

    /** Bot bubble: left-aligned row, avatar on LEFT, error bubbles are red. */
    private Node botBubble(String text, boolean isError) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.TOP_LEFT);
        row.getStyleClass().add("bot-row");

        Node avatar = avatarNode(botImage);
        Node bubble = bubbleLabel(text, true, isError);

        row.getChildren().addAll(avatar, bubble);
        return row;
    }

    /** Creates the text bubble node with the appropriate style classes. */
    private Node bubbleLabel(String text, boolean isBot, boolean isError) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setWrapText(true);
        label.setMaxWidth(300); // keep readable width in narrow windows
        label.getStyleClass().add(isBot ? "bot-bubble" : "user-bubble");
        if (isError) {
            label.getStyleClass().add("error-bubble");
        }
        return label;
    }

    /** Tiny circular avatar with ring + shadow; falls back gracefully if absent. */
    private Node avatarNode(Image img) {
        if (img == null) {
            Region spacer = new Region();
            spacer.setMinSize(0, 0);
            spacer.setPrefSize(12, 12);
            return spacer;
        }

        // Image view sized for a compact chat bubble look
        ImageView iv = new ImageView(img);
        iv.setFitWidth(38);
        iv.setFitHeight(38);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);

        // Circular clip that tracks fit size
        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle();
        clip.radiusProperty().bind(iv.fitWidthProperty().divide(2));
        clip.centerXProperty().bind(iv.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(iv.fitHeightProperty().divide(2));
        iv.setClip(clip);

        // Ring behind the image
        javafx.scene.shape.Circle ring = new javafx.scene.shape.Circle();
        ring.radiusProperty().bind(iv.fitWidthProperty().divide(2));
        ring.setStroke(javafx.scene.paint.Color.web("#e5e7eb")); // subtle gray
        ring.setStrokeWidth(1.5);
        ring.setFill(javafx.scene.paint.Color.TRANSPARENT);

        // Stack ring and clipped image; add a drop shadow via CSS id (optional)
        javafx.scene.layout.StackPane wrapper = new javafx.scene.layout.StackPane(ring, iv);
        wrapper.setPadding(new Insets(0));
        wrapper.setId("displayPicture"); // we already style this in CSS
        return wrapper;
    }

    /** Remove the ASCII box that Engine.block(...) adds for CLI friendliness. */
    private static String stripBlock(String s) {
        if (s == null)
            return "";
        String[] lines = s.split("\\R");
        if (lines.length >= 2
                && lines[0].trim().startsWith("_")
                && lines[lines.length - 1].trim().startsWith("_")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < lines.length - 1; i++) {
                String line = lines[i];
                // Engine puts a single leading space inside box
                sb.append(line.startsWith(" ") ? line.substring(1) : line);
                if (i < lines.length - 2)
                    sb.append('\n');
            }
            return sb.toString();
        }
        return s;
    }

    /** Utility: robust resource to stylesheet URL. */
    private String resourceUrl(String path) {
        var url = getClass().getResource(path);
        return Objects.requireNonNull(url, "Missing resource: " + path).toExternalForm();
    }

    /** Utility: best-effort image load. */
    private Image loadImage(String path) {
        try {
            var is = getClass().getResourceAsStream(path);
            return is != null ? new Image(is) : null;
        } catch (Exception e) {
            return null;
        }
    }
}