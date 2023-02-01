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

		double alpha = 1.5;
		double r = GraphicalState.STATE_RADIUS;
		double x_A = from.getTranslateX() + r;
		double y_A = from.getTranslateY() + r;
		double x_B = to.getTranslateX() + r;
		double y_B = to.getTranslateY() + r;

		double coefDirecteurDroite = (y_B - y_A) / (x_B - x_A);
		double theta = Math.atan(coefDirecteurDroite);
		double dx = alpha * r * Math.cos(theta);
		double dy = alpha * r * Math.sin(theta);

		trans.setStartX(x_A + dx);
		trans.setStartY(y_A + dy);
		trans.setEndX(x_B - dx);
		trans.setEndY(y_B - dy);

		curAutomate.createTransition(from.numero, to.numero, "a", false);
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

		Label label = new Label("Etat n°" + state.numero_label.getText());

		CheckBox isInitial = new CheckBox("Etat initial");
		isInitial.setSelected(state.isInitial);
		isInitial.selectedProperty().addListener((obs, oldVal, newVal) -> {
			state.setInitial(newVal);
			curAutomate.getStatesList().get(Integer.parseInt(state.numero_label.getText())).isInitial = state.isInitial;
		});

		CheckBox isFinal = new CheckBox("Etat final");
		isFinal.setSelected(state.isFinal);
		isFinal.selectedProperty().addListener((obs, oldVal, newVal) -> {
			state.setFinal(newVal);
			curAutomate.getStatesList().get(Integer.parseInt(state.numero_label.getText())).isFinal = state.isFinal;
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
	protected static final double STATE_RADIUS = 15;
	protected boolean isInitial, isFinal;
	protected Circle circle, smallCircle;
	protected int numero;
	protected Label numero_label;

	public GraphicalState(double x, double y, int nb) {
		circle = new Circle(STATE_RADIUS);
		circle.setFill(Color.WHITE);
		circle.setStroke(Color.BLACK);

		numero = nb;
		numero_label = new Label(Integer.toString(nb));
		numero_label.setTextFill(Color.BLACK);
		numero_label.setLayoutX(-numero_label.getWidth()/2);
		numero_label.setLayoutY(-numero_label.getHeight()/2);

		setTranslateX(x - STATE_RADIUS);
		setTranslateY(y - STATE_RADIUS);

		getChildren().add(circle);
		getChildren().add(numero_label);

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
				", numero_label=" + numero_label +
				'}';
	}
}

class GraphicalTransition extends Arrow {
	String chars;
	public GraphicalTransition() { super(); }
}