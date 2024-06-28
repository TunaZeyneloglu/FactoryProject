module com.example.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;

    opens com.example.project.frontend to javafx.fxml;
    exports com.example.project.frontend;
}
