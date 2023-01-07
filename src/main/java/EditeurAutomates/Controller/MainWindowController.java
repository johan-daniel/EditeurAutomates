package EditeurAutomates.Controller;

import EditeurAutomates.AutomatesLab;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
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

}