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
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

public class MainWindowController {
	// Paramètres
	private final boolean isMacos;
	private static final String TEMP_SPEC_FILE_NAME = "./Specifications XML des Automates.pdf";
	private static final String DEFAULT_AUTOMATE = "AutomateDefault.xml";

	protected Automate curAutomate = null;
	private File curFile = null;
	protected final boolean fileIsUpToDate = true;

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

	// TODO: Fichier corrompu & Le fichier ne correspond pas à un automate
	private void loadFile(String filePath){
		try {
			String content = Files.readString(Path.of(filePath));
			if (!XMLParser.verifyChecksum(content)) throw new ParserException("Couldn't verify checksum for file: " + filePath);
			this.curAutomate = XMLParser.parseXML(content);
			// charger vue graphique
		}

		catch (IOException | OutOfMemoryError | SecurityException e) { // Fichier corrompu ou droits insuffisants
			// Afficher pop-up d'erreur fichier corrompu
			throw new RuntimeException(e);
		}

		catch (InvalidPathException ignored){ } // Ne devrait jamais être thrown ; on la catch par sécurité

		catch (ParserException | RuntimeException e) { // Le fichier ne correspond pas à un automate
			// Si on catch une ParserException, c'est une erreur connue du parser
			// Si on catch une RuntimeException, l'erreur est inconnue (nous n'avons pas prévu cette erreur)
			// Dans les deux cas, on dit si l'erreur est connue ou non puis on affiche le message de l'erreur e.getMessage()
			// Puis on charge la vue XML
			System.err.println(e.getMessage());
		}
	}

	protected void loadDefaultFile(){
		final String PATH = "./temp.xml";
		try {
			File myFile = new File(PATH); // Buffer file
			Path path = FileSystems.getDefault().getPath(PATH);

			InputStream inputStream = AutomatesLab.class.getResourceAsStream(DEFAULT_AUTOMATE); // Reading original data (using stream for .jar compatibility)
			if (inputStream==null) throw new RuntimeException("Could not create input stream");

			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING); 	// Copy stream to temporary file
			loadFile(String.valueOf(path)); 										// Load default file (parse... etc)

			// Delete temp file (after letting time at the reader to open it)
			Thread.sleep(300);
			if (!myFile.delete()) System.err.println("Le fichier temporaire \"" + PATH + "\" n'a pas pu être supprimé");
		}

		catch (IOException e) { System.err.println("Couldn't load default automate because of an unexpected IOException"); }
		catch (RuntimeException e){ System.err.println("Couldn't load default automate because of an unexpected RuntimeException");	}
		catch (InterruptedException ignored0) { } // Awaken during wait (we don't care)
	}

	// TODO
	protected void saveCurrentFile(){
		// String xml = curAutomate.toXML();
	}

	// Handler de boutons

	// TODO vérifier si le fichier a changé => oui, mettre une pop-up de confirmation avant de quitter
	public void newButton(ActionEvent ignored) {
		if (curFile != null) return; // à faire: vérifier si le fichier a changé => oui, mettre une pop-up de confirmation avant de quitter
		loadDefaultFile();
	}

	public void openButton(ActionEvent ignored) {
		FileChooser fc = new FileChooser();
		fc.setTitle("AutomatesLab - Ouvrir un automate XML");

		File res = fc.showOpenDialog(null);
		if (res == null) return; // canceled by user

		loadFile(res.getAbsolutePath());
	}

	// TODO
	public void saveButton(ActionEvent ignored) {
		System.out.println("Save not implemented yet");
	}

	// TODO Sauvegarder le fichier
	public void saveAsButton(ActionEvent ignored) {
		System.out.println("Save As not implemented yet");

		FileChooser fc = new FileChooser();
		fc.setTitle("AutomatesLab - Ouvrir un automate XML");
		fc.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
		if (curFile!=null) fc.setInitialDirectory(new File(curFile.getAbsolutePath()));

		File res = fc.showSaveDialog(null);
		if (res != null) System.out.println(res.getAbsolutePath()); // change
	}

	// TODO pullModel/updateModel
	public void setActiveGraphicalView(ActionEvent ignored) {
		viewsTabpane.getSelectionModel().select(graphicViewTab);
	}

	// TODO pullModel/updateModel
	public void setActiveXMLView(ActionEvent ignored) {
		viewsTabpane.getSelectionModel().select(xmlViewTab);
	}

	// TODO
	public void openKeyboardShortcutsWindow(ActionEvent ignored) {
		System.out.println("KeyboardShortcuts window not implemented yet");
	}

	// TODO
	public void openHelpWindow(ActionEvent ignored) {
		System.out.println("Help window not implemented yet");
	}

	public void openXMLDoc(ActionEvent ignored) {
		if (!Desktop.isDesktopSupported()) {
			System.err.println("Couldn't open PDF help file because Desktop class is not supported :(");
			return;
		}

		try {
			File myFile = new File(TEMP_SPEC_FILE_NAME); // Buffer file
			Path path = FileSystems.getDefault().getPath(TEMP_SPEC_FILE_NAME);
			InputStream inputStream = AutomatesLab.class.getResourceAsStream("HelpResources/XML_representation_specifications.pdf"); // Reading original data (using stream for .jar compatibility)
			assert inputStream != null;

			// Copy stream to temporary file
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
			// Open by the OS with default pdf reader
			Desktop.getDesktop().open(myFile);
			// Delete temp file (after letting time at the reader to open it)
			Thread.sleep(300);
			if (!myFile.delete()) System.err.println("Le fichier temporaire \"" + TEMP_SPEC_FILE_NAME + "\" n'a pas pu être supprimé");
		}
		catch (IOException e) {
			System.err.println("Couldn't copy or open PDF help file");
		}
		catch (InterruptedException ignored0) { }
	}

}