module EditeurAutomates {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens EditeurAutomates to javafx.fxml;
    opens EditeurAutomates.Controller to javafx.fxml;

    exports EditeurAutomates to javafx.fxml, javafx.graphics;
    exports EditeurAutomates.Controller to javafx.fxml, javafx.graphics;

    exports EditeurAutomates.Model;
}