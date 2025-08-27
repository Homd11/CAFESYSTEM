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

    // Configuration constants - detect system Java and use relative paths
    private static final String JAVA_HOME = getJavaHome();
    private static final String JAVAFX_PATH = "lib" + File.separator + "javafx" + File.separator + "javafx-sdk-21.0.1" + File.separator + "lib";
    private static final String MYSQL_CONNECTOR = "lib" + File.separator + "mysql-connector-j-9.4.0.jar";
    private static final String OUTPUT_DIR = "out";
    private static final String MAIN_CLASS = "GUI.Gui";
    
    /**
     * Detects the Java home directory from environment or system
     */
    private static String getJavaHome() {
        // First try JAVA_HOME environment variable
        String javaHome = System.getenv("JAVA_HOME");
        if (javaHome != null && !javaHome.isEmpty()) {
            return javaHome;
        }
        
        // Fallback to system property
        javaHome = System.getProperty("java.home");
        if (javaHome != null && !javaHome.isEmpty()) {
            return javaHome;
        }
        
        // Last resort - try to find Java on PATH
        String javaCmd = isWindows() ? "javac.exe" : "javac";
        String pathEnv = System.getenv("PATH");
        if (pathEnv != null) {
            String[] paths = pathEnv.split(File.pathSeparator);
            for (String path : paths) {
                File javacFile = new File(path, javaCmd);
                if (javacFile.exists()) {
                    // Return parent of bin directory
                    return javacFile.getParentFile().getParent();
                }
            }
        }
        
        return ""; // Will cause validation to fail with proper error message
    }
    
    /**
     * Checks if running on Windows
     */
    private static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

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
        // Check Java installation
        String javacCmd = isWindows() ? "javac.exe" : "javac";
        String javaCmd = isWindows() ? "java.exe" : "java";
        String javacPath = JAVA_HOME + File.separator + "bin" + File.separator + javacCmd;
        String javaPath = JAVA_HOME + File.separator + "bin" + File.separator + javaCmd;

        if (!new File(javacPath).exists()) {
            System.err.println("❌ Java compiler not found at: " + javacPath);
            System.err.println("💡 Please ensure Java JDK is installed and JAVA_HOME is set correctly.");
            System.err.println("   Current JAVA_HOME: " + JAVA_HOME);
            System.err.println("   You can also add javac to your PATH environment variable.");
            return false;
        }

        if (!new File(javaPath).exists()) {
            System.err.println("❌ Java runtime not found at: " + javaPath);
            System.err.println("💡 Please ensure Java JDK is installed and JAVA_HOME is set correctly.");
            return false;
        }

        // Check JavaFX (make it optional for now)
        if (!new File(JAVAFX_PATH).exists()) {
            System.err.println("⚠️  JavaFX not found at: " + JAVAFX_PATH);
            System.err.println("💡 JavaFX SDK is required for the GUI. Please:");
            System.err.println("   1. Download JavaFX SDK 21 from https://openjfx.io/");
            System.err.println("   2. Extract it to: " + JAVAFX_PATH.replace(File.separator + "lib", ""));
            System.err.println("   3. Or install JavaFX via your system package manager");
            return false;
        }

        // Check MySQL Connector
        if (!new File(MYSQL_CONNECTOR).exists()) {
            System.err.println("❌ MySQL Connector not found at: " + MYSQL_CONNECTOR);
            System.err.println("💡 The MySQL connector JAR should be present in the lib directory.");
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

            // Build compilation command using detected Java
            List<String> command = new ArrayList<>();
            String javacCmd = isWindows() ? "javac.exe" : "javac";
            command.add(JAVA_HOME + File.separator + "bin" + File.separator + javacCmd);
            command.add("--module-path");
            command.add(JAVAFX_PATH);
            command.add("--add-modules");
            command.add("javafx.controls,javafx.fxml");
            command.add("-cp");
            command.add(MYSQL_CONNECTOR);
            command.add("-d");
            command.add(OUTPUT_DIR);

            // Add all Java source files
            addSourceFiles(command, "src" + File.separator + "GUI" + File.separator);
            addSourceFiles(command, "src" + File.separator + "Core" + File.separator);
            addSourceFiles(command, "src" + File.separator + "Services" + File.separator);
            addSourceFiles(command, "src" + File.separator + "DB" + File.separator);
            addSourceFiles(command, "src" + File.separator + "Enums" + File.separator);
            addSourceFiles(command, "src" + File.separator + "Values" + File.separator);
            addSourceFiles(command, "src" + File.separator + "Interfaces" + File.separator);
            addSourceFiles(command, "src" + File.separator + "app" + File.separator);

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
            File fxmlDir = new File(OUTPUT_DIR + File.separator + "resources" + File.separator + "fxml");
            File cssDir = new File(OUTPUT_DIR + File.separator + "resources" + File.separator + "css");
            fxmlDir.mkdirs();
            cssDir.mkdirs();

            // Copy FXML files
            copyFile("src" + File.separator + "resources" + File.separator + "fxml" + File.separator + "login.fxml", 
                    OUTPUT_DIR + File.separator + "resources" + File.separator + "fxml" + File.separator + "login.fxml");
            copyFile("src" + File.separator + "resources" + File.separator + "fxml" + File.separator + "student_dashboard.fxml", 
                    OUTPUT_DIR + File.separator + "resources" + File.separator + "fxml" + File.separator + "student_dashboard.fxml");
            copyFile("src" + File.separator + "resources" + File.separator + "fxml" + File.separator + "admin_dashboard.fxml", 
                    OUTPUT_DIR + File.separator + "resources" + File.separator + "fxml" + File.separator + "admin_dashboard.fxml");

            // Copy CSS
            copyFile("src" + File.separator + "resources" + File.separator + "css" + File.separator + "app.css", 
                    OUTPUT_DIR + File.separator + "resources" + File.separator + "css" + File.separator + "app.css");

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
            // Build runtime command using detected Java
            List<String> command = new ArrayList<>();
            String javaCmd = isWindows() ? "java.exe" : "java";
            command.add(JAVA_HOME + File.separator + "bin" + File.separator + javaCmd);
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
            command.add(OUTPUT_DIR + File.pathSeparator + MYSQL_CONNECTOR);
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
