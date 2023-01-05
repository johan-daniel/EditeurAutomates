package EditeurAutomates;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class AutomateViewer extends Application {

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(AutomateViewer.class.getResource("view/hello-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 320, 240);

		// Titre de la fenêtre
		stage.setTitle("AutomatesLab");

		// Icône de la fenêtre
		String icon_name = Objects.requireNonNull(AutomateViewer.class.getResource("Images/Icon.png")).toString();
		stage.getIcons().add(new Image(icon_name));

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}