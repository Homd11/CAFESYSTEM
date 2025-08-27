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

    // Configuration constants - cross-platform paths
    private static final String OUTPUT_DIR = "out";
    private static final String MAIN_CLASS = "GUI.Gui";
    
    // Dynamic paths - will be determined at runtime
    private static String javaHome;
    private static String javafxPath;
    private static String mysqlConnector;

    public static void main(String[] args) {
        GuiLauncher launcher = new GuiLauncher();

        try {
            System.out.println("🚀 Starting ITI Cafeteria Application...");

            // Step 1: Initialize paths based on the current system
            launcher.initializePaths();
            
            // Step 2: Validate environment
            if (!launcher.validateEnvironment()) {
                System.err.println("❌ Environment validation failed");
                return;
            }
            System.out.println("✅ Environment validated");

            // Step 3: Compile the project
            if (!launcher.compileProject()) {
                System.err.println("❌ Compilation failed");
                return;
            }
            System.out.println("✅ Compilation successful");

            // Step 4: Run the GUI application
            System.out.println("🎯 Launching GUI...");
            launcher.runApplication();

        } catch (Exception e) {
            System.err.println("❌ Fatal error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialize paths based on the current system
     */
    private void initializePaths() {
        // Get Java home from system property or try to detect
        javaHome = System.getProperty("java.home");
        if (javaHome == null) {
            javaHome = System.getenv("JAVA_HOME");
        }
        
        // Set MySQL connector path
        mysqlConnector = "lib" + File.separator + "mysql-connector-j-9.4.0.jar";
        
        // Try to find JavaFX in multiple locations
        javafxPath = findJavaFXPath();
        
        System.out.println("🔍 Detected paths:");
        System.out.println("   Java Home: " + javaHome);
        System.out.println("   JavaFX Path: " + javafxPath);
        System.out.println("   MySQL Connector: " + mysqlConnector);
    }

    /**
     * Find JavaFX installation in various possible locations
     */
    private String findJavaFXPath() {
        // Try multiple possible JavaFX locations
        String[] possiblePaths = {
            // Local SDK download locations
            "lib" + File.separator + "javafx" + File.separator + "javafx-sdk-21.0.1" + File.separator + "lib",
            "lib" + File.separator + "javafx" + File.separator + "lib",
            "javafx-sdk-21.0.1" + File.separator + "lib",
            
            // System installation locations (Linux/Ubuntu)
            "/usr/share/openjfx/lib",
            "/usr/lib/jvm/javafx/lib",
            
            // System installation locations (other Linux distributions)
            "/usr/lib/java/javafx/lib",
            "/opt/javafx/lib"
        };
        
        for (String path : possiblePaths) {
            File jfxDir = new File(path);
            if (jfxDir.exists() && jfxDir.isDirectory()) {
                // Check if it contains JavaFX jar files
                File[] jfxFiles = jfxDir.listFiles((dir, name) -> 
                    name.startsWith("javafx-") && name.endsWith(".jar"));
                if (jfxFiles != null && jfxFiles.length > 0) {
                    return path;
                }
            }
        }
        
        // Check for system installation with individual jar files (Ubuntu package style)
        File javaShare = new File("/usr/share/java");
        if (javaShare.exists() && javaShare.isDirectory()) {
            File[] jfxFiles = javaShare.listFiles((dir, name) -> 
                name.startsWith("javafx-") && name.endsWith(".jar"));
            if (jfxFiles != null && jfxFiles.length >= 3) { // At least controls, base, fxml
                // Create a module path with only the JavaFX jars
                return buildJavaFXModulePath(javaShare);
            }
        }
        
        return null; // JavaFX not found
    }
    
    /**
     * Build a module path string with only JavaFX jars from a directory
     */
    private String buildJavaFXModulePath(File directory) {
        StringBuilder modulePath = new StringBuilder();
        File[] jfxFiles = directory.listFiles((dir, name) -> 
            name.startsWith("javafx-") && name.endsWith(".jar"));
        
        if (jfxFiles != null) {
            for (int i = 0; i < jfxFiles.length; i++) {
                if (i > 0) {
                    modulePath.append(File.pathSeparator);
                }
                modulePath.append(jfxFiles[i].getAbsolutePath());
            }
        }
        
        return modulePath.toString();
    }

    /**
     * Validates that all required paths and files exist
     */
    private boolean validateEnvironment() {
        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String executableSuffix = isWindows ? ".exe" : "";
        
        // Check Java installation
        if (javaHome == null || javaHome.trim().isEmpty()) {
            System.err.println("❌ Java home not found");
            System.err.println("💡 Please ensure Java is properly installed and JAVA_HOME is set");
            return false;
        }
        
        String javacPath = javaHome + File.separator + "bin" + File.separator + "javac" + executableSuffix;
        String javaPath = javaHome + File.separator + "bin" + File.separator + "java" + executableSuffix;

        // For system Java installations, javac might be in /usr/bin
        if (!new File(javacPath).exists()) {
            javacPath = isWindows ? "javac.exe" : "/usr/bin/javac";
        }
        if (!new File(javaPath).exists()) {
            javaPath = isWindows ? "java.exe" : "/usr/bin/java";
        }
        
        if (!new File(javacPath).exists()) {
            System.err.println("❌ Java compiler not found at: " + javacPath);
            System.err.println("💡 Please install JDK (Java Development Kit)");
            return false;
        }

        if (!new File(javaPath).exists()) {
            System.err.println("❌ Java runtime not found at: " + javaPath);
            System.err.println("💡 Please install JDK (Java Development Kit)");
            return false;
        }

        // Check JavaFX
        if (javafxPath == null || javafxPath.trim().isEmpty()) {
            System.err.println("❌ JavaFX not found");
            System.err.println("💡 JavaFX SDK is required for the GUI. Please:");
            System.err.println("   1. Download JavaFX SDK 21 from https://openjfx.io/");
            System.err.println("   2. Extract it to: lib" + File.separator + "javafx" + File.separator + "javafx-sdk-21.0.1");
            System.err.println("   3. Or install JavaFX via your system package manager");
            if (!isWindows) {
                System.err.println("   4. On Ubuntu/Debian: sudo apt install openjfx");
            }
            return false;
        }
        
        // Validate JavaFX path (could be directory or list of jar files)
        boolean javafxValid = false;
        if (javafxPath.contains(File.pathSeparator)) {
            // Multiple jar files - check if all exist
            String[] jfxJars = javafxPath.split(File.pathSeparator);
            javafxValid = true;
            for (String jarPath : jfxJars) {
                if (!new File(jarPath).exists()) {
                    javafxValid = false;
                    break;
                }
            }
        } else {
            // Single directory path
            javafxValid = new File(javafxPath).exists();
        }
        
        if (!javafxValid) {
            System.err.println("❌ JavaFX path validation failed: " + javafxPath);
            return false;
        }

        // Check MySQL Connector
        if (!new File(mysqlConnector).exists()) {
            System.err.println("❌ MySQL Connector not found at: " + mysqlConnector);
            System.err.println("💡 Please download MySQL Connector/J and place it in the lib directory");
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

            // Build compilation command using system Java
            List<String> command = new ArrayList<>();
            
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String executableSuffix = isWindows ? ".exe" : "";
            
            // Use system javac
            String javacPath = javaHome + File.separator + "bin" + File.separator + "javac" + executableSuffix;
            if (!new File(javacPath).exists()) {
                javacPath = isWindows ? "javac.exe" : "javac"; // fallback to system PATH
            }
            
            command.add(javacPath);
            command.add("--module-path");
            command.add(javafxPath);
            command.add("--add-modules");
            command.add("javafx.controls,javafx.fxml");
            command.add("-cp");
            command.add(mysqlConnector);
            command.add("-d");
            command.add(OUTPUT_DIR);

            // Add all Java source files using cross-platform separators
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
            // Create resources directories in output using cross-platform separators
            File fxmlDir = new File(OUTPUT_DIR + File.separator + "resources" + File.separator + "fxml");
            File cssDir = new File(OUTPUT_DIR + File.separator + "resources" + File.separator + "css");
            fxmlDir.mkdirs();
            cssDir.mkdirs();

            // Copy FXML files using cross-platform separators
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
            // Build runtime command using system Java
            List<String> command = new ArrayList<>();
            
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            String executableSuffix = isWindows ? ".exe" : "";
            
            // Use system java
            String javaPath = javaHome + File.separator + "bin" + File.separator + "java" + executableSuffix;
            if (!new File(javaPath).exists()) {
                javaPath = isWindows ? "java.exe" : "java"; // fallback to system PATH
            }
            
            command.add(javaPath);
            command.add("--module-path");
            command.add(javafxPath);
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
            String classpath = OUTPUT_DIR + File.pathSeparator + mysqlConnector;
            command.add(classpath);
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
