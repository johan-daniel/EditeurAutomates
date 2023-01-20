package EditeurAutomates.Controller;

import EditeurAutomates.AutomatesLab;

import EditeurAutomates.Model.Automate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
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
	private static final String spec_temp_file_name = "./Specifications XML des Automates.pdf";

	protected Automate curAutomate = null;
	private File curFile = null;

	// Objets du FXML
	@FXML private MenuBar mainMenuBar;
	@FXML private TabPane viewsTabpane;
	@FXML private Tab graphicViewTab;
	@FXML private Tab xmlViewTab;

	public MainWindowController() {
		final String os = System.getProperty("os.name");
		this.isMacos = (os != null && os.startsWith("Mac"));
	}

	@FXML
	public void initialize() {

		if (isMacos){
			// Use macOS menu bar
			mainMenuBar.setUseSystemMenuBar(true);
		}

		// Load tabs icons
		String graphicView_icon_location = Objects.requireNonNull(AutomatesLab.class.getResource("Images/Graphic_Icon.png")).toString();
			// A voir si on le fait pas en CSS
			ImageView graphic_icon = new ImageView(graphicView_icon_location);
			graphic_icon.setFitWidth(40);
			graphic_icon.setFitHeight(40);
		graphicViewTab.setGraphic(graphic_icon);
		String XMLView_icon_location = Objects.requireNonNull(AutomatesLab.class.getResource("Images/XML_Icon.png")).toString();
			ImageView xml_icon = new ImageView(XMLView_icon_location);
			xml_icon.setFitWidth(40);
			xml_icon.setFitHeight(40);
		xmlViewTab.setGraphic(xml_icon);
	}

	// Fichier

	protected void loadFile(File f){

	}

	protected void loadDefaultFile(){

	}

	protected void saveCurrentFile(){
		// String xml = curAutomate.toXML();
	}

	// Handler de boutons

	/**
	 * Function called when the "Fichier -> Créer" button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void newButton(ActionEvent ignored) {
		System.out.println("New not implemented yet");
	}

	/**
	 * Function called when the "Fichier -> Ouvrir..." button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void openButton(ActionEvent ignored) {
		System.out.println("Open not implemented yet");
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
	 * Function called when the "Fichier -> Enregistrer sous..." button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void saveAsButton(ActionEvent ignored) {
		System.out.println("Save As not implemented yet");
	}

	/**
	 * Function called when the "Vue -> Affichage graphique" button is pressed.
	 * Sets the current active view tab to the graphical view.
	 * @param ignored ActionEvent
	 */
	public void setActiveGraphicalView(ActionEvent ignored) {
		viewsTabpane.getSelectionModel().select(graphicViewTab);
	}

	/**
	 * Function called when the "Vue -> Affichage XML" button is pressed.
	 * Sets the current active view tab to the XML view.
	 * @param ignored ActionEvent
	 */
	public void setActiveXMLView(ActionEvent ignored) {
		viewsTabpane.getSelectionModel().select(xmlViewTab);
	}

	/**
	 * Function called when the "Aide -> Raccourcis clavier" button is pressed
	 * TODO Implement, and rename "ignored" parameter if used in future implementation
	 * @param ignored ActionEvent
	 */
	public void openKeyboardShortcutsWindow(ActionEvent ignored) {
		System.out.println("KeyboardShortcuts window not implemented yet");
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
			File myFile = new File(spec_temp_file_name); // Buffer file
			Path path = FileSystems.getDefault().getPath(spec_temp_file_name);
			InputStream inputStream = AutomatesLab.class.getResourceAsStream("HelpResources/XML_representation_specifications.pdf"); // Reading original data (using stream for .jar compatibility)
			assert inputStream != null;

			// Copy stream to temporary file
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
			// Open by the OS with default pdf reader
			Desktop.getDesktop().open(myFile);
			// Delete temp file (after letting time at the reader to open it)
			Thread.sleep(300);
			if (!myFile.delete()) System.err.println("Le fichier temporaire \"" + spec_temp_file_name + "\" n'a pas pu être supprimé");
		}
		catch (IOException e) {
			System.err.println("Couldn't copy or open PDF help file");
		}
		catch (InterruptedException ignored1) { }
	}

}