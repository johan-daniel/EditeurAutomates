package EditeurAutomates.Controller;

import EditeurAutomates.AutomatesLab;
import EditeurAutomates.Model.Automate;
import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
	}

	// Fichier

	protected void loadFile(String filePath){
		try {
			String content = Files.readString(Path.of(filePath));
			if (!XMLParser.verifyChecksum(content)) throw new ParserException("Invalid checksum");
			this.curAutomate = XMLParser.parseXML(content);
		} catch (IOException e) { // TODO Fichier corrompu
			// Afficher pop-up d'erreur fichier corrompu
			throw new RuntimeException(e);
		} catch (ParserException | RuntimeException e) { // TODO Le fichier ne correspond pas à un automate
			// Si on catch une ParserException, c'est une erreur connue du parser
			// Si on catch une RuntimeException, l'erreur est inconnue (nous n'avons pas prévu cette erreur)
			// Dans les deux cas, on dit si l'erreur est connue ou non puis on affiche le message de l'erreur e.getMessage()
			// Puis on charge la vue XML
			System.err.println(e.getMessage());
		}
	}

	protected void loadDefaultFile(){

	}

	protected void saveCurrentFile(){
		// String xml = curAutomate.toXML();
	}

	// Handler de boutons

	public void newButton(ActionEvent ignored) {
		System.out.println("New not implemented yet");
	}

	public void openButton(ActionEvent ignored) {
		System.out.println("Open not implemented yet");

		// TODO Afficher la fenêtre d'ouverture qui récupère le path
		String path = "C:\\Users\\Alex\\Desktop\\AutomateDefault.xml";

		loadFile(path);

	}

	public void saveButton(ActionEvent ignored) {
		System.out.println("Save not implemented yet");
	}

	public void saveAsButton(ActionEvent ignored) {
		System.out.println("Save As not implemented yet");
	}

	public void setActiveGraphicalView(ActionEvent ignored) {
		viewsTabpane.getSelectionModel().select(graphicViewTab);
	}

	public void setActiveXMLView(ActionEvent ignored) {
		viewsTabpane.getSelectionModel().select(xmlViewTab);
	}

	public void openKeyboardShortcutsWindow(ActionEvent ignored) {
		System.out.println("KeyboardShortcuts window not implemented yet");
	}

	public void openHelpWindow(ActionEvent ignored) {
		System.out.println("Help window not implemented yet");
	}

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