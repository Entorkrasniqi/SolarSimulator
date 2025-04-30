module com.solar {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires javafx.web;
    requires jdk.jsobject;

    opens com.solar to javafx.fxml;
    opens controller to javafx.fxml;
    exports controller;
    exports com.solar;
}
