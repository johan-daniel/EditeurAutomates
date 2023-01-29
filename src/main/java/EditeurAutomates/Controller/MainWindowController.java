package EditeurAutomates.Controller;

import EditeurAutomates.AutomatesLab;
import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;

public class MainWindowController extends Controller {
	// Paramètres
	private final boolean isMacos;
	private static final String TEMP_SPEC_FILE_NAME = "./Specifications XML des Automates.pdf";
	private static final String DEFAULT_AUTOMATE = "AutomateDefault.xml";

	private File curFile = null;
	protected boolean fileIsUpToDate = true;

	XMLController xmlController;
	GraphicController graphicController;

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
	public void initialize() throws IOException {
		if (isMacos) {
			// Use macOS menu bar
			mainMenuBar.setUseSystemMenuBar(true);
		}

		FXMLLoader xmlLoader = new FXMLLoader(getClass().getResource("/EditeurAutomates/Views/XMLView.fxml"));
		XMLController xmlController = new XMLController();
		xmlLoader.setController(xmlController);
		xmlViewTab.setContent(xmlLoader.load());

		FXMLLoader graphicLoader = new FXMLLoader(getClass().getResource("/EditeurAutomates/Views/GraphicView.fxml"));
		GraphicController graphicController = new GraphicController();
		graphicLoader.setController(graphicController);
		graphicViewTab.setContent(graphicLoader.load());

		viewsTabpane.getSelectionModel().selectedItemProperty().addListener((ov, fromTab , toTab) -> {
			switch(toTab.getId()) {
				case "xmlViewTab" -> {
					graphicController.updateModel();
					xmlController.pullModel();
				}
				case "graphicViewTab" -> {
					xmlController.updateModel();
					graphicController.pullModel();
				}
			}
		});

	}


	// TODO: Charger vues & vérifier erreur de parsing
	private void loadFile(String filePath){
		try {
			String content = Files.readString(Path.of(filePath));

			// Si checksum invalide, pop-up + chargement vue XML
			if (!XMLParser.verifyChecksum(content)) throw new ParserException("Couldn't verify checksum for file \"" + filePath + "\"");

			curAutomate = XMLParser.parseXML(content);
			curFile = new File(filePath);
			fileIsUpToDate = true;

			System.out.println("Chargement de la vue graphique"); // charger vue graphique
		}

		// Fichier corrompu ou droits insuffisants
		catch (IOException | OutOfMemoryError | SecurityException e) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ce fichier ne peut pas être chargé: le fichier est corrompu, ou l'encodage est incompatible, ou l'application n'a pas suffisament de droits pour y accéder.\n\n" + e.getMessage(), ButtonType.OK);
			alert.showAndWait();
		}

		// Ne devrait jamais être thrown ; on la catch par sécurité
		catch (InvalidPathException ignored){ }

		// La checksum est invalide, ou le fichier ne correspond pas à un automate
		catch (ParserException | RuntimeException e) {

			// Si on catch une ParserException, c'est une erreur connue du parser
			// Si on catch une RuntimeException, l'erreur est inconnue (nous n'avons pas prévu cette erreur)

			// Dans les deux cas, on dit si l'erreur est connue ou non puis on affiche le message de l'erreur e.getMessage()
			Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur attrapée lors du parsing\n\n" + e.getMessage(), ButtonType.OK);
			alert.showAndWait();

			// Puis on charge la vue XML
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
			fileIsUpToDate = true;

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