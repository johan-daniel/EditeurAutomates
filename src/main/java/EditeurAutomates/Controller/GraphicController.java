package EditeurAutomates.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;

public class GraphicController extends ViewController {

	@FXML private Pane drawArea;

	@FXML private ToggleButton stateTool;
	@FXML private ToggleButton transitionTool;

	private Outils selectedTool;
	private Point2D fromCoords;

	@FXML
	public void initialize() {
		ToggleGroup tGroup = new ToggleGroup();
		stateTool.setToggleGroup(tGroup);
		transitionTool.setToggleGroup(tGroup);

		stateTool.setOnAction(e -> { selectedTool = Outils.STATE; fromCoords = null; });
		transitionTool.setOnAction(e -> selectedTool = Outils.TRANSITION);

		drawArea.setOnMouseClicked(click -> {
			if(selectedTool != null) {
				Controller.fileIsUpToDate = false;

				switch (selectedTool) {
					case STATE -> System.out.println("Ajouter un état à [" + click.getX() + ',' + click.getY() + ']');
					case TRANSITION -> {
						if (fromCoords == null) fromCoords = new Point2D(click.getX(), click.getY());
						else System.out.println("" +
								"Ajouter transition entre [" + fromCoords.getX() + ',' + fromCoords.getY() + ']'
								+ " et [" + click.getX() + ',' + click.getY() + ']'
						);
					}
				}
			}
		});
	}


	// TODO mettre à jour la variable fileIsUpToDate à false lors d'un changement

	// TODO
	@Override
	public void updateModel() {
		System.out.println("Vue graphique met à jour le modèle [A IMPLEMENTER]");
	}

	// TODO
	@Override
	public void pullModel() {
		System.out.println("Vue graphique fetch le modèle [A IMPLEMENTER]");
	}
}


enum Outils {
	STATE,
	TRANSITION
}