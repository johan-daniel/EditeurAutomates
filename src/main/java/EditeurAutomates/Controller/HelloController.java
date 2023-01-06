package EditeurAutomates.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;

public class HelloController {
    private final boolean isMacos;
    @FXML private Label welcomeText;
    @FXML private MenuBar mainMenuBar;

    public HelloController() {
        final String os = System.getProperty("os.name");
        this.isMacos = (os != null && os.startsWith("Mac"));
    }

    @FXML
    public void initialize() {
        if (isMacos) mainMenuBar.setUseSystemMenuBar(true);
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}