package EditeurAutomates.Controller;

import EditeurAutomates.Model.Automate;
import EditeurAutomates.Model.Destinations;
import EditeurAutomates.Model.State;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Objects;

public class GraphicController extends ViewController {
	private Outils selectedTool;
	private GraphicalState selectedState;
	private GraphicalTransition selectedTransition;
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
			deselectState();
			deselectTransition();
		});
		transitionTool.setOnAction(e -> {
			deselectState();
			deselectTransition();
			selectedTool = transitionTool.isSelected() ? Outils.TRANSITION : null;
		});

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


	// TODO Débugger: quand on ajoute des transitions, les couleurs des textes changent

	public void updateModel(MouseEvent click) {
		if(selectedTool == null) {
			if(click.getSource() == click.getTarget()) {
				if(selectedState != null) deselectState();
				if(selectedTransition != null) deselectTransition();
			}
			return;
		};
		if (curAutomate==null) curAutomate = new Automate();

		if (Objects.requireNonNull(selectedTool) == Outils.STATE) {
			addState(click.getX(), click.getY());
		}
		fileIsUpToDate = false;
		justLoaded = false;
	}

	@Override
	protected void updateModel() {
		deselectTools();
		deselectState();
		deselectTransition();
	}

	@Override
	public void pullModel() {
		if (curAutomate==null) return;

		// Vide les caches de données et l'écran
		states.clear();
		transitions.clear();
		drawArea.getChildren().clear();

		for(int i=0; i < curAutomate.getStatesList().size(); i++) {
			State state = curAutomate.getStatesList().get(i);
			if (state != null) {
				GraphicalState gs = new GraphicalState(state.x, state.y, state.numero);
				gs.setInitial(state.isInitial);
				gs.setFinal(state.isFinal);
				gs.setOnMouseClicked(me -> onStateClicked(gs));
				drawArea.getChildren().add(gs);
				states.add(i, gs);
			}
		}

		GraphicalState fromState, toState;
		Character character;

		for(int stateIdx=0; stateIdx < curAutomate.getTransitionMatrix().size(); stateIdx++) {
			fromState = states.get(stateIdx);

			for(int charIdx=0; charIdx < curAutomate.getTransitionMatrix().get(stateIdx).size(); charIdx++) {
				character = curAutomate.getAlphabet().get(charIdx);

				for(int destIdx=0; destIdx < curAutomate.getTransitionMatrix().get(stateIdx).get(charIdx).size(); destIdx++) {
					toState = states.get(curAutomate.getTransitionMatrix().get(stateIdx).get(charIdx).get(destIdx));

					GraphicalTransition transition = null;
					if(!transitionExists(fromState.numero, toState.numero)) {
						transition = new GraphicalTransition(fromState, toState);
						transitions.add(transition);
					}
					else {
						transition = getTransition(fromState.numero, toState.numero);
					}

					assert transition != null;
					if(character == null) transition.setAcceptsEmptyWord(true);
					else transition.addChar(character);
				}
			}
		}
		for(GraphicalTransition transition : transitions) {
			transition.setOnMouseClicked(me -> displayTransitionParams(transition));
			drawArea.getChildren().add(transition);
		}
	}

	private GraphicalTransition getTransition(int numero, int numero1) {
		for(GraphicalTransition gt : transitions) {
			if(gt != null && gt.from.numero == numero && gt.to.numero == numero1) return gt;
		}
		return null;
	}

	private void addState(double x, double y) {
		deselectTools();
		GraphicalState state = new GraphicalState(x, y, states.size());
		drawArea.getChildren().add(state);
		states.add(state);
		state.setOnMouseClicked(me -> onStateClicked(state));
		curAutomate.createState((int) x, (int) y, state.isInitial, state.isFinal);
	}

	private boolean transitionExists(int from, int to) {
		for(GraphicalTransition gt : transitions) {
			if(gt != null && gt.from.numero == from && gt.to.numero == to) return true;
		}
		return false;
	}

	private void addTransition(GraphicalState from, GraphicalState to) {
		deselectTools();
		deselectState();

		GraphicalTransition trans = new GraphicalTransition(from, to);

		curAutomate.createTransition(from.numero, to.numero, "", false);
		drawArea.getChildren().add(trans);
		transitions.add(trans);

		trans.setOnMouseClicked(click -> displayTransitionParams(trans));
	}

	private void onStateClicked(GraphicalState state) {
		if(selectedTool == null) displayStateParams(state);
		else if(selectedTool == Outils.TRANSITION) {
			if(selectedState == null) selectedState = state;
			else addTransition(selectedState, state);
		}
	}

	private void deselectTools() {
		if(selectedTool == null) return;
		stateTool.setSelected(false);
		transitionTool.setSelected(false);
		selectedTool = null;
	}

	private void deselectState() {
		if(selectedState == null) return;
		selectedState.circle.setFill(Color.WHITE);
		selectedState = null;
		objAttr.getChildren().clear();
	}

	private void deselectTransition() {
		if(selectedTransition == null) return;
		selectedTransition.line.setStroke(Color.BLACK);
		selectedTransition.l1.setStroke(Color.BLACK);
		selectedTransition.l2.setStroke(Color.BLACK);
		selectedTransition = null;
		objAttr.getChildren().clear();
	}

	private void displayStateParams(GraphicalState state) {
		objAttr.getChildren().clear();

		selectedState = state;
		state.circle.setFill(Color.web("#c54607"));

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

		Button deleteBtn = new Button("Supprimer état");
		deleteBtn.setTextFill(Color.BLACK);
		deleteBtn.setOnAction(e -> deleteState(state));

		objAttr.getChildren().addAll(label, isInitial, isFinal, deleteBtn);
	}

	private void displayTransitionParams(GraphicalTransition transition) {
		objAttr.getChildren().clear();
		selectedTransition = transition;

		transition.line.setStroke(Color.web("#c54607"));
		transition.l1.setStroke(Color.web("#c54607"));
		transition.l2.setStroke(Color.web("#c54607"));

		Label transitionLabel = new Label("Transition de " + transition.from.numero + " vers " + transition.to.numero);
		transitionLabel.setWrapText(true);

		TextField chars = new TextField();
		chars.textProperty().addListener((obv, oldValue, newValue) -> {
			transition.chars.setText(newValue);
		});
		chars.setStyle("-fx-text-fill: black;");
		chars.setText(transition.chars.getText());

		transition.chars.textProperty().addListener((obs, oldVal, newVal) -> {
			chars.setText(newVal.replace(" ", ""));
		});

		CheckBox acceptsEmptyWord = new CheckBox("acceptsEmptyWord");
		acceptsEmptyWord.setSelected(transition.acceptsEmptyWord);
		acceptsEmptyWord.wrapTextProperty();

		chars.textProperty().addListener((obs, oldValue, newValue) -> {
			transition.chars.setText(newValue);
			curAutomate.editTransition(
					transition.from.numero,
					transition.to.numero,
					newValue.replace("ε", ""),
					acceptsEmptyWord.isSelected()
			);
		});

		acceptsEmptyWord.selectedProperty().addListener((obs, oldValue, newValue) -> {
			transition.setAcceptsEmptyWord(newValue);
			curAutomate.editTransition(
					transition.from.numero,
					transition.to.numero,
					chars.getText().replace("ε", ""),
					newValue
			);
		});

		Button deleteBtn = new Button("Supprimer transition");
		deleteBtn.setTextFill(Color.BLACK);
		deleteBtn.setOnAction(e -> deleteTransition(transition));

		objAttr.getChildren().addAll(transitionLabel, chars, acceptsEmptyWord, deleteBtn);
	}

	private void deleteTransition(GraphicalTransition transition) {
		drawArea.getChildren().remove(transition);
		curAutomate.deleteTransition(transition.from.numero, transition.to.numero);
		deselectTransition();
	}

	private void deleteState(GraphicalState state) {
		drawArea.getChildren().remove(state);
		curAutomate.deleteState(state.numero);
		deselectState();
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

			Point2D end = new Point2D.Double(STATE_RADIUS * Math.cos(Math.PI), STATE_RADIUS * Math.sin(Math.PI));
			Point2D start = new Point2D.Double(end.getX() - 50, end.getY());

			arrow.line.setEndX(end.getX()); arrow.line.setEndY(end.getY());
			arrow.line.setStartX(start.getX()); arrow.line.setStartY(start.getY());
			arrow.line.setControlX1(start.getX()); arrow.line.setControlY1(start.getY());
			arrow.line.setControlX2(end.getX()); arrow.line.setControlY2(end.getY());

			double arrowLength = start.distance(end);
			arrow.setTranslateX(-arrowLength * 0.75);
			getChildren().add(arrow);
		}
		else getChildren().remove(arrow);
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
		else {
			getChildren().remove(smallCircle);
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
	protected boolean acceptsEmptyWord;
	public GraphicalTransition(GraphicalState from, GraphicalState to) {
		super();
		this.from = from;
		this.to = to;
		getChildren().add(chars);

		line.setStrokeWidth(2);
		l1.setStrokeWidth(2);
		l2.setStrokeWidth(2);

		double r = GraphicalState.STATE_RADIUS;
		double x_A = from.getTranslateX() + r;
		double y_A = from.getTranslateY() + r;

		// Transition entre i et j, i!=j
		if(from != to) {
			double x_B = to.getTranslateX() + r;
			double y_B = to.getTranslateY() + r;

			// Flèche de la transition

			double alpha = 2.0;
			double coefDirecteurDroite = (y_B - y_A) / (x_B - x_A);
			if (!((x_A>x_B && y_A>y_B) || (x_A<x_B && y_A<y_B))) coefDirecteurDroite = -1.0 * coefDirecteurDroite;
			double theta = Math.atan(coefDirecteurDroite);
			double dx = alpha * r * Math.cos(theta);
			double dy = alpha * r * Math.sin(theta);

			double sign_X = 1, sign_Y = 1;
			if(x_A > x_B) sign_X = -1;
			if(y_A > y_B) sign_Y = -1;
			double fx_A = x_A + sign_X*dx;
			double fy_A = y_A + sign_Y*dy;
			double fx_B = x_B - sign_X*dx;
			double fy_B = y_B - sign_Y*dy;

			setStartX(fx_A);
			setStartY(fy_A);
			setEndX(fx_B);
			setEndY(fy_B);

			// Points de contrôle des courbes de Bézier (on a besoin de droites en l'occurence)
			line.setControlX1(fx_A);
			line.setControlY1(fy_A);
			line.setControlX2(fx_B);
			line.setControlY2(fy_B);

			// Label des symboles de la transition
			double x = (from.getTranslateX() + to.getTranslateX()) / 2;
			double y = (from.getTranslateY() + to.getTranslateY()) / 2 ;
			chars.setTranslateX(x);
			chars.setTranslateY(y);
		}
		// Transition entre i et i
		else {
			double theta1 = 3 * Math.PI / 4;
			double theta2 = Math.PI / 4;

			double x1 = r * Math.cos(theta1) + x_A;
			double y1 = r * Math.sin(theta1) + y_A;

			double x2 = r * Math.cos(theta2) + x_A;
			double y2 = r * Math.sin(theta2) + y_A;

			line.setStartX(x1); line.setStartY(y1);
			line.setEndX(x2); line.setEndY(y2);
			line.setControlX1(x1-0.5*r); line.setControlY1(y1 + 1.5*r);
			line.setControlX2(x2+0.5*r); line.setControlY2(y2 + 1.5*r);
			getChildren().remove(l1); getChildren().remove(l2);

			if(from.isInitial) line.setTranslateX(GraphicalState.STATE_RADIUS * 0.75);

			double x = (x1-0.5*r + x2+0.5*r) / 2;
			double y = (y1 + 1.5*r + y2 + 1.5*r) / 2 ;
			chars.setTranslateX(x);
			chars.setTranslateY(y - GraphicalState.STATE_RADIUS + 7);
		}
	}

	public void addChar(Character c) {
		chars.setText(chars.getText() + ' ' + c);
	}

	public void setAcceptsEmptyWord(boolean b) {
		acceptsEmptyWord = b;
		if(acceptsEmptyWord) addChar('ε');
		else chars.setText(chars.getText().replace("ε", ""));
	}
}