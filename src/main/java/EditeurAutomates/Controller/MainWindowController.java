package EditeurAutomates.Controller;

import EditeurAutomates.AutomatesLab;
import EditeurAutomates.Model.Automate;
import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.MenuBar;
import javafx.stage.FileChooser;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Thread.sleep;

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
		// La vue précédemment chargée push ses changements vers le curAutomate
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

		// La vue nouvellement chargée récupère les changements du curAutomate
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

			// Si tout vas bien, on charge l'automate
			curAutomate = XMLParser.parseXML(content);
			curFile = new File(filePath);
			fileIsUpToDate = true;
			justLoaded = true;
		}

		// Ne devrait jamais être thrown ; on la catch par sécurité
		catch (InvalidPathException ignored){ }

		// La checksum est invalide, ou le fichier ne correspond pas à un automate
		catch (ParserException | RuntimeException e) {
			// Affichage de l'erreur de parsing et chargement de la vue XML
			showAlert("Parsing error", "Erreur attrapée lors du parsing", e.getMessage());
			curFile = new File(filePath);
			fileIsUpToDate = true;
			justLoaded = true;

			canPullXmlModel = false;
			viewsTabpane.getSelectionModel().select(xmlViewTab); // Load XML View without updating it
			xmlController.loadContentToXMLView(content);
		}
	}

	private void loadDefaultFile(){
		final String PATH = "./temp.xml";

		try {
			File myFile = new File(PATH); // Buffer file
			Path path = FileSystems.getDefault().getPath(PATH);

			InputStream inputStream = AutomatesLab.class.getResourceAsStream(DEFAULT_AUTOMATE); // Reading original data (using stream for .jar compatibility)
			if (inputStream==null) throw new RuntimeException("Could not create input stream");

			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING); 	// Copy stream to temporary file
			loadFile(String.valueOf(path)); 										// Load default file (parse... etc)
			curFile = null;

			// Delete temp file (after letting time at the reader to open it)
			sleep(300);
			if (!myFile.delete()) System.err.println("Le fichier temporaire \"" + PATH + "\" n'a pas pu être supprimé");
		}

		catch (IOException e) { System.err.println("Couldn't load default automate because of an unexpected IOException"); }
		catch (RuntimeException e){ System.err.println("Couldn't load default automate because of an unexpected RuntimeException");	}
		catch (InterruptedException ignored0) { } // Awaken during wait (we don't care)
	}

	private void saveCurrentFile(){
		if (curFile==null) return; // Cannot save file to disk (no destination set) (this should not happen since only saveButton calls this function)

		String cur_view = viewsTabpane.getSelectionModel().getSelectedItem().getId();
		String contenu_du_fichier;

		// Dans le cas général, on fetch les changements de la vue courrante, puis on enregistre l'automate.
		// Pour la vue XML, on supporte en plus le cas ou le XML ne donnerait pas un automate valide ; on autorise à enregistrer quand même dans ce cas
		if (Objects.equals(cur_view, "xmlViewTab")){
			String xml = xmlController.getEditorText();
			try {
				Automate a = XMLParser.parseXML(xml);
				contenu_du_fichier = XMLParser.getFileXML(a);
			} catch (ParserException e){
				long old_checksum = XMLParser.getChecksum(xml);
				long checksum = XMLParser.calculateChecksum(xml);
				if (old_checksum!=-1){ // On remplace l'ancienne checksum par la nouvelle
					int debut = xml.indexOf("checksum=\"") + 10;
					int nb_char_old_checksum = String.valueOf(old_checksum).length();
					int fin = xml.length();
					contenu_du_fichier = xml.substring(0, debut) + checksum + xml.substring(debut + nb_char_old_checksum, fin);
				}
				else { // On ajoute la nouvelle checksum
					contenu_du_fichier = "checksum=\"" + checksum + "\"" + xml;
				}
			}
		}
		else {
			fetchCurrentViewChanges();
			contenu_du_fichier = XMLParser.getFileXML(curAutomate);
		}

		try (FileWriter f = new FileWriter(curFile)) {
			f.write(contenu_du_fichier);
			f.flush();
			fileIsUpToDate = true;
		} catch(IOException e){
			showError("Couldn't save file", "Le fichier n'a pas pu être sauvegardé en raison d'une erreur inattendue", e.getMessage());
		}
	}

	private boolean isSafeToContinue(){
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

	private static void showAlert(String title, String header, String content){
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private static void showError(String title, String header, String content){
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}

	// Handler de boutons

	public void newButton() {
		if (isSafeToContinue()) {
			loadDefaultFile();
			updateCurrentView();
		}
	}

	public void openButton() {
		FileChooser fc = new FileChooser();
		fc.setTitle("AutomatesLab - Ouvrir...");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));

		File res = fc.showOpenDialog(null);
		if (res == null) return; // canceled by user
		if (!isSafeToContinue()) return; // Le fichier n'est pas à jour et l'utilisateur annule

		loadFile(res.getAbsolutePath());
		updateCurrentView();
	}

	public void saveButton() {
		if (curFile==null) saveAsButton();
		saveCurrentFile();
	}

	public void saveAsButton() {
		FileChooser fc = new FileChooser();
		fc.setTitle("AutomatesLab - Enregistrer sous...");
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml"));
		if (curFile!=null) fc.setInitialDirectory(new File(curFile.getParent()));

		File res = fc.showSaveDialog(null);

		if (res == null) return; // Cancel by user

		curFile = res;
		saveButton();
	}

	public void setActiveGraphicalView() {
		viewsTabpane.getSelectionModel().select(graphicViewTab);
	}

	public void setActiveXMLView() {
		viewsTabpane.getSelectionModel().select(xmlViewTab);
	}

	public void openKeyboardShortcutsWindow() {
		Alert about_window = new Alert(Alert.AlertType.INFORMATION);
		about_window.setTitle("Raccourcis clavier");
		about_window.setHeaderText(null);
		about_window.setGraphic(null);
		about_window.setContentText("""
				Sur Windows et Linux, le raccourcis "Shortcut" est la touche "Ctrl", sur macOS il s'agit de la touche "Command".
				
				Shortcut+N\t\t\tNouveau fichier
				Shortcut+S\t\t\tEnregistrer
				Shortcut+Shift+S\tEnregistrer sous
				F1\t\t\t\t\tDocumentation XML
				Ctrl+1\t\t\t\tVue graphique
				Ctrl+2\t\t\t\tVue XML
				
				""");
		about_window.showAndWait();
	}

	public void openAboutWindow() {
		Alert about_window = new Alert(Alert.AlertType.INFORMATION);
		about_window.setTitle("À propos");
		about_window.setHeaderText(null);
		about_window.setGraphic(null);
		about_window.setContentText("""
				AutomatesLab v1.0
				02/02/2023
				
				Auteurs:
				Lola ALBIN
				Alexandre BROCHART
				Johan DANIEL
				Victor ELOY
				""");
		about_window.showAndWait();
	}

	public void openXMLDoc() {
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