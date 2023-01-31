package EditeurAutomates;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.swing.ImageIcon;
import java.awt.Taskbar;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class AutomatesLab extends Application {
	protected static String Icon = "Images/App_Icon.png";

	/**
	 * Définit l'icône du Stage en fonction du système d'exploitation
	 * @param stage Le Stage dont on définit l'icône de fenêtre
	 * @param icon_path Le lien de l'image (absolu ou relatif)
	 */
	private static void setAppIcon(Stage stage, String icon_path){
		URL iconURL = AutomatesLab.class.getResource(icon_path);
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
		}
		catch (final UnsupportedOperationException | SecurityException ignored){} // Pas besoin de traitements supplémentaires
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(AutomatesLab.class.getResource("Views/MainWindow.fxml"));
		Scene scene = new Scene(fxmlLoader.load());

		// Titre de la fenêtre
		stage.setTitle("AutomatesLab");

		// Icône de la fenêtre
		setAppIcon(stage, Icon);

		stage.setMinHeight(480);
		stage.setMinWidth(640);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}