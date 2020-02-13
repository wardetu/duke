package duke;

import duke.command.Command;
import gui.components.DialogBox;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

/**
 * The Duke program
 */
public class Duke extends Application {
    private Controller controller;

    private Image user;
    private Image duke;

    /**
     * Constructs a Duke instance with the specified file path
     *
     * @param filePath a String value of the file path.
     */
    public Duke(String filePath) {
        Storage storageController = new Storage(filePath);
        this.controller = new Controller(storageController);
    }

    public Duke() {
        this("src/data/data.csv");
        try {
            user = SwingFXUtils.toFXImage(ImageIO.read(new File("src/main/resources/images/ricky.png")), null);
            duke = SwingFXUtils.toFXImage(ImageIO.read(new File("src/main/resources/images/andrew.png")), null);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Duke bot = new Duke("src/data/data.csv");
        bot.run();
    }

    private void run() {
        Scanner scan = new Scanner(System.in);
        Ui.greet();
        Ui.printTaskList();
        while (scan.hasNext()) {
            try {
                Optional<Command> parsed = Parser.parse(scan.nextLine());
                if (parsed.isPresent()) {
                    Command command = parsed.get();
                    if (controller.execute(command)) {
                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        System.exit(0);

    }

    @Override
    public void start(Stage stage) {
        ScrollPane scrollPane = new ScrollPane();
        VBox dialogContainer = new VBox();
        scrollPane.setContent(dialogContainer);

        TextField userInput = new TextField();
        Button sendButton = new Button("Send");

        AnchorPane mainLayout = new AnchorPane();
        mainLayout.getChildren().addAll(scrollPane, userInput, sendButton);

        Scene scene = new Scene(mainLayout);

        stage.setScene(scene);
        stage.show();

        stage.setTitle("Duke");
        stage.setResizable(false);
        stage.setMinHeight(600.0);
        stage.setMinWidth(400.0);

        mainLayout.setPrefSize(400.0, 600.0);

        scrollPane.setPrefSize(385, 535);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        scrollPane.setVvalue(1.0);
        scrollPane.setFitToWidth(true);

        // You will need to import `javafx.scene.layout.Region` for this.
        dialogContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);

        userInput.setPrefWidth(325.0);

        sendButton.setPrefWidth(55.0);

        AnchorPane.setTopAnchor(scrollPane, 1.0);

        AnchorPane.setBottomAnchor(sendButton, 1.0);
        AnchorPane.setRightAnchor(sendButton, 1.0);

        AnchorPane.setLeftAnchor(userInput, 1.0);
        AnchorPane.setBottomAnchor(userInput, 1.0);

        String initialLine = "";
        Ui.greet();
        initialLine += Ui.getContent() + "\n";
        Ui.printTaskList();
        initialLine += Ui.getContent();
        dialogContainer.getChildren().addAll(
                DialogBox.getDukeDialog(new Label(initialLine), new ImageView(duke))
        );

        sendButton.setOnMouseClicked((event) -> {
            handleUserInput(userInput, dialogContainer);
        });

        userInput.setOnAction((event) -> {
            handleUserInput(userInput, dialogContainer);
        });


        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

    }

    private void handleUserInput(TextField userInput, VBox dialogContainer) {
        Label userText = new Label(userInput.getText());
        Label dukeText = new Label(getResponse(userInput.getText()));
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, new ImageView(user)),
                DialogBox.getDukeDialog(dukeText, new ImageView(duke))
        );
        userInput.clear();

        if (controller.getStatus()) {
            try {
                Thread.sleep(1500);
                System.exit(0);
            } catch (Exception e) {
                Controller.raiseException(e);
            }
        }
    }

    private String getResponse(String text) {
        try {
            Optional<Command> parsed = Parser.parse(text);
            System.out.println(parsed.isPresent());
            parsed.ifPresent(command -> controller.execute(command));
        } catch (Exception e) {
            return e.getMessage();
        }
        return Ui.getContent();
    }


}
