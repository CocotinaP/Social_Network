module com.example.socialnetwork_1connetiondb {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;

    opens com.example.socialnetwork_1connetiondb to javafx.fxml;
    exports com.example.socialnetwork_1connetiondb;
}