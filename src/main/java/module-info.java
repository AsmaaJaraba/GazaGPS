module com.gazagps {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.gazagps to javafx.fxml;
    exports com.gazagps;
}