package EditeurAutomates.Controller;

import EditeurAutomates.Model.Automate;
import EditeurAutomates.Model.State;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class GraphicController extends ViewController {
	private Outils selectedTool;
	private GraphicalState selectedState;
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
			selectedState = null;
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

	// TODO Etats initiaux : petite flèche
	// TODO Etat sélectionné : fond de couleur ou jsp quoi trouve un truc jsuis pas ta daronne là oh

	// TODO ajouter transitions
	public void updateModel(MouseEvent click) {
		if(selectedTool == null) {
			if(selectedState != null && click.getTarget() == click.getSource()) deselectState();
			return;
		};
		if (curAutomate==null) curAutomate = new Automate();

		switch (selectedTool) {
			case STATE -> addState(click.getX(), click.getY());
			case TRANSITION -> {
				if(selectedState == null) break;
			}
		}
		fileIsUpToDate = false;
		justLoaded = false;
	}

	@Override
	protected void updateModel() {
		deselectTools();
		deselectState();
	}

	@Override
	public void pullModel() {
		if (curAutomate==null) return;

		drawArea.getChildren().clear();
		for(State state : curAutomate.getStatesList()) {
			if(state != null) {
				GraphicalState gs = new GraphicalState(state.x, state.y, state.numero);
				gs.setInitial(state.isInitial);
				gs.setFinal(state.isFinal);
				gs.setOnMouseClicked(me -> onStateClicked(gs));
				drawArea.getChildren().add(gs);
			}
		}
	}

	private void addState(double x, double y) {
		deselectTools();
		GraphicalState state = new GraphicalState(x, y, states.size());
		drawArea.getChildren().add(state);
		states.add(state);
		state.setOnMouseClicked(me -> onStateClicked(state));
		curAutomate.createState((int) x, (int) y, state.isInitial, state.isFinal);
	}

	private void onStateClicked(GraphicalState state) {
		if(selectedTool == null) displayStateParams(state);
		else if(selectedTool == Outils.TRANSITION) {
			if(selectedState == null) selectedState = state;
			else addTransition(selectedState, state);
		}
	}

	private void addTransition(GraphicalState from, GraphicalState to) {
		deselectTools();
		deselectState();
		GraphicalTransition trans = new GraphicalTransition();
		double fromX = from.getTranslateX() + GraphicalState.STATE_RADIUS;
		double fromY = from.getTranslateY() + GraphicalState.STATE_RADIUS;
		double toX = to.getTranslateX() + GraphicalState.STATE_RADIUS;
		double toY = to.getTranslateY() + GraphicalState.STATE_RADIUS;

		double coefDirecteurDroite = (toY - fromY) / (toX - fromX);
		double theta = Math.atan(coefDirecteurDroite / GraphicalState.STATE_RADIUS);

		double deltaX = coefDirecteurDroite * Math.cos(theta);
		double deltaY = coefDirecteurDroite * Math.sin(theta);

		trans.setStartX(fromX + deltaX); trans.setStartY(fromY + deltaY);
		trans.setEndX(toX); trans.setEndY(toY);
		drawArea.getChildren().add(trans);
	}

	private void deselectTools() {
		stateTool.setSelected(false);
		transitionTool.setSelected(false);
		selectedTool = null;
	}

	private void deselectState() {
		selectedState = null;
		objAttr.getChildren().clear();
	}

	private void displayStateParams(GraphicalState state) {
		objAttr.getChildren().clear();

		selectedState = state;

		Label label = new Label("Etat n°" + state.numero.getText());

		CheckBox isInitial = new CheckBox("Etat initial");
		isInitial.setSelected(state.isInitial);
		isInitial.selectedProperty().addListener((obs, oldVal, newVal) -> {
			state.setInitial(newVal);
			curAutomate.getStatesList().get(Integer.parseInt(state.numero.getText())).isInitial = state.isInitial;
		});

		CheckBox isFinal = new CheckBox("Etat final");
		isFinal.setSelected(state.isFinal);
		isFinal.selectedProperty().addListener((obs, oldVal, newVal) -> {
			state.setFinal(newVal);
			curAutomate.getStatesList().get(Integer.parseInt(state.numero.getText())).isFinal = state.isFinal;
		});

		objAttr.getChildren().add(label);
		objAttr.getChildren().add(isInitial);
		objAttr.getChildren().add(isFinal);
	}
}

enum Outils {
	STATE,
	TRANSITION
}

class GraphicalState extends StackPane {
	public static final double STATE_RADIUS = 15;
	public boolean isInitial, isFinal;
	public Circle circle, smallCircle;
	public Label numero;
	public GraphicalState(double x, double y, int nb) {

		circle = new Circle(STATE_RADIUS);
		circle.setFill(Color.WHITE);
		circle.setStroke(Color.BLACK);

		numero = new Label(Integer.toString(nb));
		numero.setTextFill(Color.BLACK);
		numero.setLayoutX(-numero.getWidth()/2);
		numero.setLayoutY(-numero.getHeight()/2);

		setTranslateX(x - STATE_RADIUS/2);
		setTranslateY(y - STATE_RADIUS/2);

		getChildren().add(circle);
		getChildren().add(numero);

		if(nb == 0) setInitial(true);
	}

	public void setInitial(boolean init) {
		if(isInitial && init) return;
		isInitial = init;

	}

	public void setFinal(boolean fin) {
		if(isFinal && fin) return;

		isFinal = fin;
		if(isFinal) {
			smallCircle = new Circle(0.8 * STATE_RADIUS);
			smallCircle.setFill(Color.TRANSPARENT);
			smallCircle.setStroke(Color.BLACK);
			getChildren().add(smallCircle);
		}
		else if(getChildren().size() == 3){
			getChildren().remove(getChildren().size() - 1);
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

class GraphicalTransition extends Arrow {
	String chars;
	public GraphicalTransition() { super(); }
}