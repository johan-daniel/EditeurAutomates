package EditeurAutomates.Controller;

import EditeurAutomates.AutomatesLab;
import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Optional;

public class MainWindowController extends Controller {
	// Constantes
	private final boolean isMacos;
	private static final String TEMP_SPEC_FILE_NAME = "./Specifications XML des Automates.pdf";
	private static final String DEFAULT_AUTOMATE = "AutomateDefault.xml";

	// Attributs
	private boolean canPullXmlModel = true;
	private File curFile = null;

	// Contrôleurs
	private XMLController xmlController;
	private GraphicController graphicController;

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

		// Load the views controllers and FXML
		FXMLLoader xmlLoader = new FXMLLoader(getClass().getResource("/EditeurAutomates/Views/XMLView.fxml"));
		xmlController = new XMLController();
		xmlLoader.setController(xmlController);
		xmlViewTab.setContent(xmlLoader.load());
		FXMLLoader graphicLoader = new FXMLLoader(getClass().getResource("/EditeurAutomates/Views/GraphicView.fxml"));
		graphicController = new GraphicController();
		graphicLoader.setController(graphicController);
		graphicViewTab.setContent(graphicLoader.load());

		// Add listener that updates view models
		viewsTabpane.getSelectionModel().selectedItemProperty().addListener((ov, fromTab , toTab) -> tabChangeHandler(fromTab, toTab));
	}

	private void tabChangeHandler(Tab fromTab, Tab toTab){
		// Previous loaded model pushes its changes to global curAutomate
		if (!canPullXmlModel){ // On vient d'une erreur de parsing (*)
			canPullXmlModel = true;
			return;
		}

		if(fromTab!=null){
			switch(fromTab.getId()){
				case "xmlViewTab" -> {
					try {
						xmlController.updateModel();
					} catch (RuntimeException e) {
						canPullXmlModel = false;
						viewsTabpane.getSelectionModel().select(xmlViewTab);
						showAlert("Parsing error", "Impossible de charger la vue graphique: erreur attrapée lors du parsing", e.getMessage()); // (*)
					}
				}
				case "graphicViewTab" -> graphicController.updateModel();
			}
		}

		if (!canPullXmlModel) return;

		// New loaded model fetches the changes from global curAutomate
		if (toTab!=null){
			switch(toTab.getId()) {
				case "xmlViewTab" -> xmlController.pullModel();
				case "graphicViewTab" -> graphicController.pullModel();
			}
		}

	}

	private void updateCurrentView(){
		Tab to = viewsTabpane.getSelectionModel().getSelectedItem();
		tabChangeHandler(null, to);
	}

	private void fetchCurrentViewChanges(){
		Tab cur_tab = viewsTabpane.getSelectionModel().getSelectedItem();
		tabChangeHandler(cur_tab, null);
	}

	// TODO: Tester le cas où une erreur de parsing est levée & dernier commentaire (dernier catch)
	private void loadFile(String filePath){
		String content;

		// On récupère le contenu
		try {
			content = Files.readString(Path.of(filePath));
		}
		// Fichier corrompu ou droits insuffisants
		catch (IOException | OutOfMemoryError | SecurityException e) {
			showAlert("Loading error", "Ce fichier ne peut pas être chargé: le fichier est corrompu, ou l'encodage est incompatible, ou l'application n'a pas suffisament de droits pour y accéder.", e.getMessage());
			return;
		}

		try {
			// Si checksum invalide, pop-up + chargement vue XML
			if (!XMLParser.verifyChecksum(content)) throw new ParserException("Couldn't verify checksum for file \"" + filePath + "\"");

			curAutomate = XMLParser.parseXML(content);
			curFile = new File(filePath);
			fileIsUpToDate = true;
		}

		// Ne devrait jamais être thrown ; on la catch par sécurité
		catch (InvalidPathException ignored){ }

		// La checksum est invalide, ou le fichier ne correspond pas à un automate
		catch (ParserException | RuntimeException e) { // Affichage de l'erreur de parsing et chargement de la vue XML
			showAlert("Parsing error", "Erreur attrapée lors du parsing", e.getMessage());

			// [ICI] Ajouter le chargement du fichier dans l'onglet XML

			xmlController.loadContentToXMLView(content);
			viewsTabpane.getSelectionModel().select(xmlViewTab);
		}
	}

	private static void showAlert(String title, String header, String content){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
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
			curFile = null;
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
		if (curFile==null) return; // Cannot save file to disk (no destination set)

		fetchCurrentViewChanges();
		// String xml = curAutomate.toXML();
	}

	protected boolean isSafeToContinue(){
		if (fileIsUpToDate) return true;

		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Alerte");
		alert.setHeaderText("Il y a des changements non enregistrés. Continuer ?");

		ButtonType cancel = ButtonType.CANCEL;
		ButtonType go_on = new ButtonType("Continuer sans sauvegarder");
		ButtonType save = new ButtonType("Sauvegarder et continuer");
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(cancel, go_on, save);

		Optional<ButtonType> res = alert.showAndWait();

		if (res.isEmpty()) return false;
		if (res.get() == cancel) return false;
		if (res.get() == go_on) return true;

		// Here res.get() == save
		saveCurrentFile();
		return true;
	}

	// Handler de boutons

	public void newButton(ActionEvent ignored) {
		if (isSafeToContinue()) {
			loadDefaultFile();
			updateCurrentView();
		}
	}

	public void openButton(ActionEvent ignored) {
		FileChooser fc = new FileChooser();
		fc.setTitle("AutomatesLab - Ouvrir un automate XML");

		File res = fc.showOpenDialog(null);
		if (res == null) return; // canceled by user
		if (!isSafeToContinue()) return; // Le fichier n'est pas à jour et l'utilisateur annule

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

	public void setActiveGraphicalView(ActionEvent ignored) {
		viewsTabpane.getSelectionModel().select(graphicViewTab);
	}

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