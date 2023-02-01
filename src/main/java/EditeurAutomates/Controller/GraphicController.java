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
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.ListIterator;

public class GraphicController extends ViewController {
	private Outils selectedTool;
	private GraphicalState selectedState;
	private final ArrayList<GraphicalState> states;
	private final ArrayList<GraphicalTransition> transitions;

	@FXML private Pane drawArea;
	@FXML private ToggleButton stateTool;
	@FXML private ToggleButton transitionTool;
	@FXML private VBox tools, objAttr;
	private double fromX;

	public GraphicController(){
		states = new ArrayList<>();
		transitions = new ArrayList<>();
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

			ListIterator<GraphicalTransition> li = transitions.listIterator();
			ArrayList<GraphicalTransition> buffer = new ArrayList<>(transitions);
			transitions.clear();
			buffer.forEach(transition -> addTransition(transition.from, transition.to));
		});

		// Pareil avec l'autre coordonnée
		drawArea.heightProperty().addListener((obs, oldVal, newVal) -> {
			drawArea.getChildren().clear();
			double change = newVal.doubleValue() / oldVal.doubleValue();

			for(GraphicalState state : states) {
				state.setTranslateY(change * state.getTranslateY());
				drawArea.getChildren().add(state);
			}

			ListIterator<GraphicalTransition> li = transitions.listIterator();
			ArrayList<GraphicalTransition> buffer = new ArrayList<>(transitions);
			transitions.clear();
			buffer.forEach(transition -> addTransition(transition.from, transition.to));
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

		double r = GraphicalState.STATE_RADIUS;
		double x_A = from.getTranslateX() + r;
		double y_A = from.getTranslateY() + r;

		GraphicalTransition trans = new GraphicalTransition();
		trans.from = from;
		trans.to = to;

		if(from != to)
		{
			double alpha = 1.5;
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

			trans.line.setControlX1(x_A + dx); trans.line.setControlY1(y_A + dy);
			trans.line.setControlX2(x_B - dx); trans.line.setControlY2(y_B - dy);
		}
		else
		{
			double theta1 = 3 * Math.PI / 4;
			double theta2 = Math.PI / 4;

			double x1 = r * Math.cos(theta1) + x_A;
			double y1 = r * Math.sin(theta1) + y_A;

			double x2 = r * Math.cos(theta2) + x_A;
			double y2 = r * Math.cos(theta2) + y_A;

			trans.line.setStartX(x1); trans.line.setStartY(y1);
			trans.line.setEndX(x2); trans.line.setEndY(y2);
			trans.line.setControlX1(x1-0.5*r); trans.line.setControlY1(y1 + 1.5*r);
			trans.line.setControlX2(x2+0.5*r); trans.line.setControlY2(y2 + 1.5*r);
			trans.getChildren().remove(1, 3);
		}
		trans.chars.setText("ε");
		double x = (from.getTranslateX() + to.getTranslateX()) / 2;
		double y = (from.getTranslateY() + to.getTranslateY()) / 2 ;
		trans.chars.setTranslateX(x);
		trans.chars.setTranslateY(y);
		curAutomate.createTransition(from.numero, to.numero, "", true);
		drawArea.getChildren().add(trans);
		transitions.add(trans);

		trans.setOnMouseClicked(click -> {
			trans.line.setStroke(Color.web("#c54607"));
		});
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
	protected Arrow arrow;
	protected Label numero_label;

	public GraphicalState(double x, double y, int nb) {
		circle = new Circle(STATE_RADIUS);
		circle.setFill(Color.WHITE);
		circle.setStroke(Color.BLACK);

		setBackground(Background.fill(Color.RED));

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

		if(isInitial) {
			arrow = new Arrow();

			double xEnd = 1.5 * STATE_RADIUS * Math.cos(Math.PI);
			double yEnd = 1.5 * STATE_RADIUS * Math.sin(Math.PI);
			double xStart = xEnd - 50;
			double yStart = yEnd;

			arrow.line.setEndX(xEnd); arrow.line.setEndY(yEnd);
			arrow.line.setStartX(xStart); arrow.line.setStartY(yStart);
			arrow.line.setControlX1(xStart); arrow.line.setControlY1(yStart);
			arrow.line.setControlX2(xEnd); arrow.line.setControlY2(yEnd);

			arrow.setTranslateX(-STATE_RADIUS*2);
			getChildren().add(arrow);
		}
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
	protected Label chars = new Label();
	protected GraphicalState from, to;
	public GraphicalTransition() {
		super();
		getChildren().add(chars);
	}
}