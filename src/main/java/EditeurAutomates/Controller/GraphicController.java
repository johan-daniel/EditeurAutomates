package EditeurAutomates.Controller;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

public class GraphicController extends ViewController {

	@FXML private Pane drawArea;

	@FXML private ToggleButton stateTool;
	@FXML private ToggleButton transitionTool;

	private Outils selectedTool;
	private Point2D fromCoords;
	private final ArrayList<GraphicalState> states = new ArrayList<>();

	@FXML
	public void initialize() {
		HBox.setHgrow(drawArea, Priority.ALWAYS);

		ToggleGroup tGroup = new ToggleGroup();
		stateTool.setToggleGroup(tGroup);
		transitionTool.setToggleGroup(tGroup);

		stateTool.setOnAction(e -> { selectedTool = Outils.STATE; fromCoords = null; });
		transitionTool.setOnAction(e -> selectedTool = Outils.TRANSITION);

		drawArea.setOnMouseClicked(this::updateModel);

		drawArea.widthProperty().addListener((obs, oldVal, newVal) -> {
			drawArea.getChildren().clear();		// Supprime tous les états affichés
			double change = newVal.doubleValue() / oldVal.doubleValue();	// Calcule le changement dans la largeur

			for(GraphicalState state : states) {	// Pour tous les états
				state.circle.setCenterX(change * state.circle.getCenterX());	// Applique le changement à la coordonnée du cercle
				drawArea.getChildren().add(state.circle);		// L'ajoute à l'affichage
			}
		});

		// Pareil avec l'autre coordonnée
		drawArea.heightProperty().addListener((obs, oldVal, newVal) -> {
			drawArea.getChildren().clear();
			double change = newVal.doubleValue() / oldVal.doubleValue();

			for(GraphicalState state : states) {
				state.circle.setCenterY(change * state.circle.getCenterY());
				drawArea.getChildren().add(state.circle);
			}
		});
	}


	// TODO mettre à jour la variable fileIsUpToDate à false lors d'un changement

	// TODO
	public void updateModel(MouseEvent click) {
		if(selectedTool == null || click == null) return;
		Controller.fileIsUpToDate = false;

		switch (selectedTool) {
			case STATE -> {
				addState(click.getX(), click.getY());
			}
			case TRANSITION -> {
				if (fromCoords == null) fromCoords = new Point2D(click.getX(), click.getY());
				else System.out.println("" +
						"Ajouter transition entre [" + fromCoords.getX() + ',' + fromCoords.getY() + ']'
						+ " et [" + click.getX() + ',' + click.getY() + ']'
				);
			}
		}
	}

	@Override
	protected void updateModel() {
		if(stateTool.isSelected()) stateTool.setSelected(false);
		if(transitionTool.isSelected()) transitionTool.setSelected(false);
		selectedTool = null;
		updateModel(null);
	}

	// TODO
	@Override
	public void pullModel() {
		System.out.println("Vue graphique fetch le modèle [A IMPLEMENTER]");
	}

	private void addState(double x, double y) {
		GraphicalState state = new GraphicalState();
		state.isFinal = false;
		state.isInitial = false;

		double STATE_WIDTH = 10;
		Circle c = new Circle(STATE_WIDTH);

		c.setCenterX(x);
		c.setCenterY(y);
		state.circle = c;

		drawArea.getChildren().add(c);
		states.add(state);
		curAutomate.createState((int) x, (int) y);
	}
}

enum Outils {
	STATE,
	TRANSITION
}

class GraphicalState {
	public boolean isInitial, isFinal;
	public Circle circle;
	public GraphicalState() {}
}