import Services.SystemHandler;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SystemHandler systemHandler = new SystemHandler(scanner);

        // Initialize the system
        if (!systemHandler.initializeSystem()) {
            return; // Exit if database connection fails
        }

        // Run the main application flow
        systemHandler.runApplication();
    }
}
