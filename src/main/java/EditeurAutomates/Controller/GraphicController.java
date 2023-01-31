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

public class GraphicController extends ViewController {

	private final double STATE_WIDTH = 10;

	@FXML private Pane drawArea;

	@FXML private ToggleButton stateTool;
	@FXML private ToggleButton transitionTool;

	private Outils selectedTool;
	private Point2D fromCoords;

	@FXML
	public void initialize() {
		HBox.setHgrow(drawArea, Priority.ALWAYS);

		ToggleGroup tGroup = new ToggleGroup();
		stateTool.setToggleGroup(tGroup);
		transitionTool.setToggleGroup(tGroup);

		stateTool.setOnAction(e -> { selectedTool = Outils.STATE; fromCoords = null; });
		transitionTool.setOnAction(e -> selectedTool = Outils.TRANSITION);

		drawArea.setOnMouseClicked(this::updateModel);
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
		GraphicalState state = new GraphicalState(x,y,false, false);
		Circle c = new Circle(STATE_WIDTH);

		double tX = state.posX * drawArea.getWidth();
		double tY = state.posY * drawArea.getHeight();

		c.setTranslateX(tX);
		c.setTranslateY(tY);

		drawArea.getChildren().add(c);
		//curAutomate.createState((int) x, (int) y);
	}
}

enum Outils {
	STATE,
	TRANSITION
}

class GraphicalState {
	public double posX, posY;
	public boolean isInitial, isFinal;

	public GraphicalState(double x, double y, boolean init, boolean fin) {
		isFinal = fin;
		isInitial = init;
		posX = x / 1080;
		posY = y / 720;
	}
}