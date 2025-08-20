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
        System.out.println("🚀 Starting ITI Cafeteria JavaFX Application...");

        GuiLauncher launcher = new GuiLauncher();

        try {
            // Step 1: Validate environment
            if (!launcher.validateEnvironment()) {
                System.err.println("❌ Environment validation failed!");
                return;
            }

            // Step 2: Compile the project
            if (!launcher.compileProject()) {
                System.err.println("❌ Compilation failed!");
                return;
            }

            // Step 3: Run the GUI application
            launcher.runApplication();

        } catch (Exception e) {
            System.err.println("❌ Error launching application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates that all required paths and files exist
     */
    private boolean validateEnvironment() {
        System.out.println("🔍 Validating environment...");

        // Check Java
        String javacPath = JAVA_HOME + "/bin/javac.exe";
        if (!new File(javacPath).exists()) {
            System.err.println("❌ Java JDK not found at: " + JAVA_HOME);
            System.err.println("   Please ensure Java 21 is installed in the correct location.");
            return false;
        }

        // Check JavaFX
        if (!new File(JAVAFX_PATH).exists()) {
            System.err.println("❌ JavaFX not found at: " + JAVAFX_PATH);
            System.err.println("   Please download and extract JavaFX SDK 21 to the lib/javafx/ directory.");
            return false;
        }

        // Check MySQL Connector
        if (!new File(MYSQL_CONNECTOR).exists()) {
            System.err.println("❌ MySQL Connector not found at: " + MYSQL_CONNECTOR);
            System.err.println("   Please ensure mysql-connector-j-9.4.0.jar is in the lib/ directory.");
            return false;
        }

        System.out.println("✅ Environment validation successful!");
        return true;
    }

    /**
     * Compiles the Java project with proper JavaFX and MySQL dependencies
     */
    private boolean compileProject() {
        System.out.println("🔨 Compiling Java files...");

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

            // Execute compilation
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Wait for compilation to complete
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ Compilation successful!");
                return true;
            } else {
                System.err.println("❌ Compilation failed with exit code: " + exitCode);
                return false;
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Error during compilation: " + e.getMessage());
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
     * Runs the compiled JavaFX application with warning suppression
     */
    private void runApplication() {
        System.out.println("🎯 Starting GUI application...");
        System.out.println("⏳ Please wait for the application window to appear...");

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

            // Suppress specific warnings
            command.add("-Djava.util.logging.config.file=");
            command.add("-Dprism.verbose=false");
            command.add("-Djavafx.animation.pulse=false");
            command.add("-Dprism.dirtyopts=false");

            command.add("-cp");
            command.add(OUTPUT_DIR + ";" + MYSQL_CONNECTOR);
            command.add(MAIN_CLASS);

            // Execute the application
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.inheritIO(); // This allows the JavaFX application to show its GUI
            Process process = pb.start();

            System.out.println("✅ GUI application started successfully!");
            System.out.println("📱 The ITI Cafeteria application window should now be visible.");
            System.out.println("🔇 JavaFX warnings have been suppressed for cleaner output.");

            // Wait for the application to finish
            int exitCode = process.waitFor();
            System.out.println("🏁 Application finished with exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            System.err.println("❌ Error running application: " + e.getMessage());
        }
    }
}
