import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TestGui extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("✅ JavaFX Application Starting...");

        Label label = new Label("🎯 ITI Cafeteria Test GUI");
        Button button = new Button("Click Me!");
        button.setOnAction(e -> {
            label.setText("✅ JavaFX is working!");
            System.out.println("✅ Button clicked - JavaFX is working properly!");
        });

        VBox root = new VBox(10);
        root.getChildren().addAll(label, button);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Scene scene = new Scene(root, 400, 200);
        primaryStage.setTitle("JavaFX Test");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("✅ Test GUI displayed successfully");
    }

    public static void main(String[] args) {
        System.out.println("🚀 Starting JavaFX Test...");
        launch(args);
    }
}
