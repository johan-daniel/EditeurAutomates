package EditeurAutomates;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class AutomateViewer extends Application {
	protected static String Icon = "oui";

	/**
	 * Définit l'icône du Stage en fonction du système d'exploitation
	 * @param stage Le Stage dont on définit l'icône de fenêtre
	 * @param icon_path Le lien de l'image (absolu ou relatif)
	 */
	private static void setAppIcon(Stage stage, String icon_path){
		URL iconURL = AutomateViewer.class.getResource(icon_path);
		assert iconURL != null;
		String icon_name = Objects.requireNonNull(iconURL.toString());
		// Windows et linux
		stage.getIcons().add(new Image(icon_name));
		// macOS
		java.awt.Image image = new ImageIcon(iconURL).getImage();
		try {
			final Taskbar taskbar = Taskbar.getTaskbar();
			//set icon for mac os (and other systems which do support this method)
			taskbar.setIconImage(image);
		} catch (final UnsupportedOperationException e) {
			System.out.println("The os does not support the taskbar API");
		} catch (final SecurityException e) {
			System.out.println("There was a security exception for: 'taskbar.setIconImage'");
		}
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(AutomateViewer.class.getResource("view/hello-view.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 320, 240);

		// Titre de la fenêtre
		stage.setTitle("AutomatesLab");

		// Icône de la fenêtre
		setAppIcon(stage, Icon);

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}