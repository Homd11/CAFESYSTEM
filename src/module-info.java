module iti.cafeteria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    exports GUI;
    exports Core;
    exports Services;
    exports DB;
    exports Enums;
    exports Values;
    exports Interfaces;
    exports app;
}
