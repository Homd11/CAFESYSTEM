package app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX GUI Launcher - Runs the ITI Cafeteria GUI Application
 * This class handles all JavaFX module path and classpath configuration
 * to run the GUI without needing batch files.
 */
public class GuiLauncher {

    // Configuration constants
    private static final String JAVA_HOME = "java/jdk-21.0.8";
    private static final String JAVAFX_PATH = "lib/javafx/javafx-sdk-21.0.1/lib";
    private static final String MYSQL_CONNECTOR = "lib/mysql-connector-j-9.4.0.jar";
    private static final String OUTPUT_DIR = "out";
    private static final String MAIN_CLASS = "GUI.Gui";

    public static void main(String[] args) {
        GuiLauncher launcher = new GuiLauncher();

        try {
            // Step 1: Validate environment (silent)
            if (!launcher.validateEnvironment()) {
                return;
            }

            // Step 2: Compile the project (silent)
            if (!launcher.compileProject()) {
                return;
            }

            // Step 3: Run the GUI application (silent)
            launcher.runApplication();

        } catch (Exception e) {
            // Silent failure - no console output
        }
    }

    /**
     * Validates that all required paths and files exist
     */
    private boolean validateEnvironment() {
        // Check Java
        String javacPath = JAVA_HOME + "/bin/javac.exe";
        if (!new File(javacPath).exists()) {
            return false;
        }

        // Check JavaFX
        if (!new File(JAVAFX_PATH).exists()) {
            return false;
        }

        // Check MySQL Connector
        if (!new File(MYSQL_CONNECTOR).exists()) {
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

            // Build compilation command
            List<String> command = new ArrayList<>();
            command.add(JAVA_HOME + "/bin/javac.exe");
            command.add("--module-path");
            command.add(JAVAFX_PATH);
            command.add("--add-modules");
            command.add("javafx.controls,javafx.fxml");
            command.add("-cp");
            command.add(MYSQL_CONNECTOR);
            command.add("-d");
            command.add(OUTPUT_DIR);
            command.add("-Xlint:none"); // Suppress compilation warnings

            // Add all Java source files
            addSourceFiles(command, "src/GUI/");
            addSourceFiles(command, "src/Core/");
            addSourceFiles(command, "src/Services/");
            addSourceFiles(command, "src/DB/");
            addSourceFiles(command, "src/Enums/");
            addSourceFiles(command, "src/Values/");
            addSourceFiles(command, "src/Interfaces/");
            addSourceFiles(command, "src/app/");

            // Execute compilation silently
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process process = pb.start();

            // Wait for compilation to complete
            int exitCode = process.waitFor();
            return exitCode == 0;

        } catch (IOException | InterruptedException e) {
            return false;
        }
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
     * Runs the compiled JavaFX application with complete output suppression
     */
    private void runApplication() {
        try {
            // Build runtime command with warning suppression
            List<String> command = new ArrayList<>();
            command.add(JAVA_HOME + "/bin/java.exe");
            command.add("--module-path");
            command.add(JAVAFX_PATH);
            command.add("--add-modules");
            command.add("javafx.controls,javafx.fxml");

            // Add JVM options to suppress warnings
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

            // Comprehensive warning suppression
            command.add("-Djava.util.logging.config.file=");
            command.add("-Dprism.verbose=false");
            command.add("-Djavafx.animation.pulse=false");
            command.add("-Dprism.dirtyopts=false");
            command.add("-Dprism.debug=false");
            command.add("-Dprism.trace=false");
            command.add("-Djavafx.pulseLogger=false");
            command.add("-Dcom.sun.javafx.isLoggingEnabled=false");

            command.add("-cp");
            command.add(OUTPUT_DIR + ";" + MYSQL_CONNECTOR);
            command.add(MAIN_CLASS);

            // Execute the application with complete output suppression
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            pb.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process process = pb.start();
            // Wait for the application to finish
            int exitCode = process.waitFor();

        } catch (IOException | InterruptedException e) {
            // Silent failure
        }
    }
}
