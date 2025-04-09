module com.solar {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;


    opens com.solar to javafx.fxml;
    exports com.solar;
}
