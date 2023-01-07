package EditeurAutomates.Controller;
import EditeurAutomates.AutomatesLab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

public class MainWindowController {
	// Paramètres
	private final boolean isMacos;

	// Objets du FXML
	@FXML private MenuBar mainMenuBar;
	@FXML private Tab graphicsTab;
	@FXML private Tab XMLTab;

	public MainWindowController() {
		final String os = System.getProperty("os.name");
		this.isMacos = (os != null && os.startsWith("Mac"));
	}

	@FXML
	public void initialize() {
		if (isMacos) mainMenuBar.setUseSystemMenuBar(true);

		// Load tabs icons
		String graphicView_icon_location = Objects.requireNonNull(AutomatesLab.class.getResource("Images/Graphic_Icon.png")).toString();
			// A voir si on le fait pas en CSS
			ImageView graphic_icon = new ImageView(graphicView_icon_location);
			graphic_icon.setFitWidth(40);
			graphic_icon.setFitHeight(40);
		graphicsTab.setGraphic(graphic_icon);
		String XMLView_icon_location = Objects.requireNonNull(AutomatesLab.class.getResource("Images/XML_Icon.png")).toString();
			ImageView xml_icon = new ImageView(XMLView_icon_location);
			xml_icon.setFitWidth(40);
			xml_icon.setFitHeight(40);
		XMLTab.setGraphic(xml_icon);

	}

	/**
	 * Function called when the "Fichier -> Créer" button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void createButton(ActionEvent ignored) {
		System.out.println("Create not implemented yet");
	}

	/**
	 * Function called when the "Fichier -> Enregistrer" button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void saveButton(ActionEvent ignored) {
		System.out.println("Save not implemented yet");
	}

	/**
	 * Function called when the "Fichier -> Enregistrer sous" button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void saveAsButton(ActionEvent ignored) {
		System.out.println("Save As not implemented yet");
	}

	/**
	 * Function called when the "Aide -> A propos" button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void openHelpWindow(ActionEvent ignored) {
		System.out.println("Help window not implemented yet");
	}

	/**
	 * Function called when the "Aide -> Représentation XML" button is pressed ;
	 * it opens the XML specifiation file.
	 * @param ignored ActionEvent
	 */
	public void openXMLDoc(ActionEvent ignored) {

		if (!Desktop.isDesktopSupported()) {
			System.err.println("Couldn't open PDF help file because Desktop class is not supported :(");
			return;
		}

		try {
			File myFile = new File("./temp.pdf"); // Buffer file
			Path path = FileSystems.getDefault().getPath("./temp.pdf");
			InputStream inputStream = AutomatesLab.class.getResourceAsStream("HelpResources/XML_representation_specifications.pdf"); // Reading original data (using stream for .jar compatibility)
			assert inputStream != null;

			// Copy stream to temporary file
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
			// Open by the OS with default pdf reader
			Desktop.getDesktop().open(myFile);
			// Delete temp file (after letting time at the reader to open it)
			Thread.sleep(300);
			if (!myFile.delete()) System.err.println("Le fichier temporaire \"temp.pdf\" n'a pas pu être supprimé");
		}
		catch (IOException e) {
			System.err.println("Couldn't copy or open PDF help file");
		}
		catch (InterruptedException ignored1) { }
	}

}