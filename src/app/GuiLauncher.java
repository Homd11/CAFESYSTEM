package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX GUI Launcher - Runs the ITI Cafeteria GUI Application
 * This class handles all JavaFX module path and classpath configuration
 * to run the GUI without needing batch files.
 */
public class GuiLauncher {

    // Configuration constants - using your local JDK and JavaFX paths
    private static final String JAVA_HOME = "java\\jdk-21.0.8";
    private static final String JAVAFX_PATH = "lib\\javafx\\javafx-sdk-21.0.1\\lib"; // Use JavaFX 21 instead of 24
    private static final String MYSQL_CONNECTOR = "lib\\mysql-connector-j-9.4.0.jar";
    private static final String OUTPUT_DIR = "out";
    private static final String MAIN_CLASS = "GUI.Gui";

    public static void main(String[] args) {
        GuiLauncher launcher = new GuiLauncher();

        try {
            System.out.println("🚀 Starting ITI Cafeteria Application...");

            // Step 1: Validate environment
            if (!launcher.validateEnvironment()) {
                System.err.println("❌ Environment validation failed");
                return;
            }
            System.out.println("✅ Environment validated");

            // Step 2: Compile the project
            if (!launcher.compileProject()) {
                System.err.println("❌ Compilation failed");
                return;
            }
            System.out.println("✅ Compilation successful");

            // Step 3: Run the GUI application
            System.out.println("🎯 Launching GUI...");
            launcher.runApplication();

        } catch (Exception e) {
            System.err.println("❌ Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates that all required paths and files exist
     */
    private boolean validateEnvironment() {
        // Check local JDK
        String javacPath = JAVA_HOME + "\\bin\\javac.exe";
        String javaPath = JAVA_HOME + "\\bin\\java.exe";

        if (!new File(javacPath).exists()) {
            System.err.println("❌ Java compiler not found at: " + javacPath);
            return false;
        }

        if (!new File(javaPath).exists()) {
            System.err.println("❌ Java runtime not found at: " + javaPath);
            return false;
        }

        // Check JavaFX
        if (!new File(JAVAFX_PATH).exists()) {
            System.err.println("❌ JavaFX not found at: " + JAVAFX_PATH);
            System.err.println("Please download JavaFX SDK and place it in the correct path.");
            return false;
        }

        // Check MySQL Connector
        if (!new File(MYSQL_CONNECTOR).exists()) {
            System.err.println("❌ MySQL Connector not found at: " + MYSQL_CONNECTOR);
            return false;
        }

        return true;
    }

    /**
     * Compiles the Java project with proper JavaFX and MySQL dependencies
     */
    private boolean compileProject() {
        try {
            // Create output directory
            File outDir = new File(OUTPUT_DIR);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }

            // Copy resources (FXML, CSS) to output directory
            copyResources();

            // Build compilation command using local JDK
            List<String> command = new ArrayList<>();
            command.add(JAVA_HOME + "\\bin\\javac.exe");
            command.add("--module-path");
            command.add(JAVAFX_PATH);
            command.add("--add-modules");
            command.add("javafx.controls,javafx.fxml");
            command.add("-cp");
            command.add(MYSQL_CONNECTOR);
            command.add("-d");
            command.add(OUTPUT_DIR);

            // Add all Java source files
            addSourceFiles(command, "src\\GUI\\");
            addSourceFiles(command, "src\\Core\\");
            addSourceFiles(command, "src\\Services\\");
            addSourceFiles(command, "src\\DB\\");
            addSourceFiles(command, "src\\Enums\\");
            addSourceFiles(command, "src\\Values\\");
            addSourceFiles(command, "src\\Interfaces\\");
            addSourceFiles(command, "src\\app\\");

            // Execute compilation
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read and display output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for compilation to complete
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("❌ Compilation failed with exit code: " + exitCode);
            }
            return exitCode == 0;

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Compilation error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Copies FXML and CSS resource files to the output directory
     */
    private void copyResources() {
        try {
            // Create resources directories in output
            File fxmlDir = new File(OUTPUT_DIR + "\\resources\\fxml");
            File cssDir = new File(OUTPUT_DIR + "\\resources\\css");
            fxmlDir.mkdirs();
            cssDir.mkdirs();

            // Copy FXML files
            copyFile("src\\resources\\fxml\\login.fxml", OUTPUT_DIR + "\\resources\\fxml\\login.fxml");
            copyFile("src\\resources\\fxml\\student_dashboard.fxml", OUTPUT_DIR + "\\resources\\fxml\\student_dashboard.fxml");
            copyFile("src\\resources\\fxml\\admin_dashboard.fxml", OUTPUT_DIR + "\\resources\\fxml\\admin_dashboard.fxml");

            // Copy CSS
            copyFile("src\\resources\\css\\app.css", OUTPUT_DIR + "\\resources\\css\\app.css");

            System.out.println("✅ Resources copied to output directory");
        } catch (IOException e) {
            System.err.println("❌ Failed to copy resources: " + e.getMessage());
        }
    }

    /**
     * Copies a single file from source to destination
     */
    private void copyFile(String sourcePath, String destPath) throws IOException {
        File sourceFile = new File(sourcePath);
        File destFile = new File(destPath);

        if (!sourceFile.exists()) {
            System.err.println("⚠️ Source file not found: " + sourcePath);
            return;
        }

        // Create parent directories if they don't exist
        destFile.getParentFile().mkdirs();

        // Copy file using Java NIO
        java.nio.file.Files.copy(
            sourceFile.toPath(),
            destFile.toPath(),
            java.nio.file.StandardCopyOption.REPLACE_EXISTING
        );
    }

    /**
     * Adds all .java files from a directory to the compilation command
     */
    private void addSourceFiles(List<String> command, String directory) {
        File dir = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.endsWith(".java"));
            if (files != null) {
                for (File file : files) {
                    command.add(file.getPath());
                }
            }
        }
    }

    /**
     * Runs the compiled JavaFX application
     */
    private void runApplication() {
        try {
            // Build runtime command using local JDK
            List<String> command = new ArrayList<>();
            command.add(JAVA_HOME + "\\bin\\java.exe");
            command.add("--module-path");
            command.add(JAVAFX_PATH);
            command.add("--add-modules");
            command.add("javafx.controls,javafx.fxml");

            // Add JVM options to suppress warnings but keep errors
            command.add("--add-opens");
            command.add("javafx.controls/com.sun.javafx.scene.control.behavior=ALL-UNNAMED");
            command.add("--add-opens");
            command.add("javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED");
            command.add("--add-opens");
            command.add("javafx.base/com.sun.javafx.binding=ALL-UNNAMED");
            command.add("--add-opens");
            command.add("javafx.base/com.sun.javafx.event=ALL-UNNAMED");
            command.add("--add-opens");
            command.add("javafx.graphics/com.sun.javafx.stage=ALL-UNNAMED");

            command.add("-cp");
            command.add(OUTPUT_DIR + ";" + MYSQL_CONNECTOR);
            command.add(MAIN_CLASS);

            // Execute the application
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO(); // This will show all output in the console
            Process process = pb.start();

            System.out.println("✅ GUI application started successfully");

            // Wait for the application to finish
            int exitCode = process.waitFor();
            System.out.println("Application finished with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Failed to start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
