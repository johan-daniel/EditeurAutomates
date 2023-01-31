package EditeurAutomates.Controller;

import EditeurAutomates.Model.Automate;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import javax.swing.*;
import java.util.ArrayList;

public class GraphicController extends ViewController {
	private Outils selectedTool;
	private Point2D fromCoords;
	private final ArrayList<GraphicalState> states;

	@FXML private Pane drawArea;
	@FXML private ToggleButton stateTool;
	@FXML private ToggleButton transitionTool;
	@FXML private VBox tools, objAttr;

	public GraphicController(){
		states = new ArrayList<>();
	}

	@FXML
	public void initialize() {
		HBox.setHgrow(drawArea, Priority.ALWAYS);
		VBox.setVgrow(tools, Priority.ALWAYS);
		VBox.setVgrow(objAttr, Priority.ALWAYS);

		ToggleGroup tGroup = new ToggleGroup();
		stateTool.setToggleGroup(tGroup);
		transitionTool.setToggleGroup(tGroup);

		stateTool.setOnAction(e -> {
			selectedTool = stateTool.isSelected() ? Outils.STATE : null;
			fromCoords = null;
		});
		transitionTool.setOnAction(e -> selectedTool = transitionTool.isSelected() ? Outils.TRANSITION : null);

		drawArea.setOnMouseClicked(this::updateModel);

		drawArea.widthProperty().addListener((obs, oldVal, newVal) -> {
			drawArea.getChildren().clear();		// Supprime tous les états affichés
			double change = newVal.doubleValue() / oldVal.doubleValue();	// Calcule le changement dans la largeur

			for(GraphicalState state : states) {	// Pour tous les états
				state.setTranslateX(change * state.getTranslateX());	// Applique le changement à la coordonnée du cercle
				drawArea.getChildren().add(state);		// L'ajoute à l'affichage
			}
		});

		// Pareil avec l'autre coordonnée
		drawArea.heightProperty().addListener((obs, oldVal, newVal) -> {
			drawArea.getChildren().clear();
			double change = newVal.doubleValue() / oldVal.doubleValue();

			for(GraphicalState state : states) {
				state.setTranslateY(change * state.getTranslateY());
				drawArea.getChildren().add(state);
			}
		});
	}

	// TODO @JoJ rajouter les outils setInitial et setFinal (et graphismes associés)
	// TODO ajouter transitions

	public void updateModel(MouseEvent click) {
		if(selectedTool == null || click == null) return;

		if (curAutomate==null) curAutomate = new Automate();

		switch (selectedTool) {
			case STATE -> addState(click.getX(), click.getY());
			case TRANSITION -> {
				if (fromCoords == null) fromCoords = new Point2D(click.getX(), click.getY());
				else System.out.println("" +
						"Ajouter transition entre [" + fromCoords.getX() + ',' + fromCoords.getY() + ']'
						+ " et [" + click.getX() + ',' + click.getY() + ']'
				);
			}
		}
		fileIsUpToDate = false;
		justLoaded = false;
	}

	@Override
	protected void updateModel() {
		deselectTools();
		updateModel(null);
	}

	// TODO
	@Override
	public void pullModel() {
//		System.out.println("Vue graphique fetch le modèle [A IMPLEMENTER]"); // azy tg tu me saoules
	}

	private void addState(double x, double y) {
		deselectTools();
		GraphicalState state = new GraphicalState(x, y, states.size());
		drawArea.getChildren().add(state);
		states.add(state);
		state.setOnMouseClicked(me -> displayStateParams(state));
		curAutomate.createState((int) x, (int) y);
	}

	private void deselectTools() {
		stateTool.setSelected(false);
		transitionTool.setSelected(false);
		selectedTool = null;
	}

	private void displayStateParams(GraphicalState state) {
		System.out.println(state);
	}
}

enum Outils {
	STATE,
	TRANSITION
}

class GraphicalState extends StackPane {
	private static final double STATE_WIDTH = 15;
	private Color cirCol, txtCol;
	public boolean isInitial, isFinal;
	public Circle circle;
	public Label numero;
	public GraphicalState(double x, double y, int nb) {
		cirCol = Color.WHITE;
		txtCol = Color.BLACK;

		circle = new Circle(STATE_WIDTH);
		circle.setFill(cirCol);
		circle.setStroke(txtCol);

		numero = new Label(Integer.toString(nb));
		numero.setTextFill(txtCol);
		numero.setLayoutX(-numero.getWidth()/2);
		numero.setLayoutY(-numero.getHeight()/2);

		setTranslateX(x - STATE_WIDTH/2);
		setTranslateY(y - STATE_WIDTH/2);

		getChildren().add(circle);
		getChildren().add(numero);
	}

	public void setInitial(boolean init) {
		isInitial = init;
		if(isInitial) {
			Circle smallCircle = new Circle(0.8 * STATE_WIDTH);
			smallCircle.setFill(Color.TRANSPARENT);
			smallCircle.setStroke(txtCol);
			getChildren().add(smallCircle);
		}
	}

	@Override
	public String toString() {
		return "GraphicalState{" +
				"isInitial=" + isInitial +
				", isFinal=" + isFinal +
				", circle=" + circle +
				", numero=" + numero +
				'}';
	}
}