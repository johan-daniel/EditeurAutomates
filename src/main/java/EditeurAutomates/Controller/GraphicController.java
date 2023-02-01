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

	// TODO Etat sélectionné : fond de couleur ou un autre truc
	// TODO Débugger: quand on switch puis revient, les transitions disparaissent
	// TODO Débugger: quand on switch puis revient, les états initiaux et finaux disparaissent
	// TODO Débugger: mettre un état comme inital le déplace légèrement
	// TODO Débugger: quand on ajoute des transitions, les couleurs des textes changent

	// TODO Bouton supprimer lorsque transition/état sélectionné(e)

	public void updateModel(MouseEvent click) {
		if(selectedTool == null) {
			if(selectedState != null && click.getTarget() == click.getSource()) deselectState();
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
	}

	@Override
	public void pullModel() {
		if (curAutomate==null) return;
		states.clear();
		transitions.clear();

		drawArea.getChildren().clear();
		for(int i=0; i < curAutomate.getStatesList().size(); i++) {
			State state = curAutomate.getStatesList().get(i);
			if(state != null) {
				GraphicalState gs = new GraphicalState(state.x, state.y, state.numero);
				gs.setInitial(state.isInitial);
				gs.setFinal(state.isFinal);
				gs.setOnMouseClicked(me -> onStateClicked(gs));
				drawArea.getChildren().add(gs);
				states.add(i, gs);
			}
		}

		for(int i=0; i < curAutomate.getTransitionMatrix().size(); i++) {
			for(int j=0; j < curAutomate.getTransitionMatrix().get(0).size(); j++) {
				GraphicalState stateFrom = states.get(i);

				if(curAutomate.getTransitionMatrix().get(i).get(j) == null) continue;

				for(int k=0; k < curAutomate.getTransitionMatrix().get(i).get(j).size(); k++) {
					GraphicalState stateTo = states.get(curAutomate.getTransitionMatrix().get(i).get(j).get(k));

					GraphicalTransition gt = createGraphicalTransIfNotExist(stateFrom, stateTo);

					if(curAutomate.getAlphabet().get(j) == null) gt.setAcceptsEmptyWord(true);
					else gt.addChar(curAutomate.getAlphabet().get(j));
				}
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

		GraphicalTransition trans = new GraphicalTransition(from, to);

		if(from != to) {
			double x_B = to.getTranslateX() + r;
			double y_B = to.getTranslateY() + r;
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

			trans.setStartX(fx_A);
			trans.setStartY(fy_A);
			trans.setEndX(fx_B);
			trans.setEndY(fy_B);

			// Points de contrôle des courbes de Bézier (on a besoin de droites en l'occurence
			trans.line.setControlX1(fx_A);
			trans.line.setControlY1(fy_A);
			trans.line.setControlX2(fx_B);
			trans.line.setControlY2(fy_B);
		}
		else {
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

		trans.hitbox.setFill(Color.RED);

		curAutomate.createTransition(from.numero, to.numero, "", false);
		drawArea.getChildren().add(trans);
		transitions.add(trans);

		trans.setOnMouseClicked(click -> displayTransitionParams(trans));
	}

	private void deselectTools() {
		stateTool.setSelected(false);
		transitionTool.setSelected(false);
		selectedTool = null;
	}

	private void deselectState() {
		selectedState.circle.setFill(Color.WHITE);
		selectedState = null;
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

		objAttr.getChildren().add(label);
		objAttr.getChildren().add(isInitial);
		objAttr.getChildren().add(isFinal);
	}

	private void displayTransitionParams(GraphicalTransition transition) {
		transition.line.setStroke(Color.web("#c54607"));
		transition.l1.setStroke(Color.web("#c54607"));
		transition.l2.setStroke(Color.web("#c54607"));

		Label transitionLabel = new Label("Transition de " + transition.from.numero + " vers " + transition.to.numero);
		transitionLabel.setWrapText(true);
		TextField chars = new TextField();
		chars.textProperty().bind(transition.chars.textProperty());
		chars.setStyle("-fx-text-fill: black;");
		CheckBox acceptsEmptyWord = new CheckBox("acceptsEmptyWord");
		acceptsEmptyWord.setSelected(transition.acceptsEmptyWord);

		chars.textProperty().addListener((obs, oldValue, newValue) -> {
			transition.chars.setText(newValue);
			curAutomate.editTransition(
					transition.from.numero,
					transition.to.numero,
					newValue,
					acceptsEmptyWord.isSelected()
			);
		});

		acceptsEmptyWord.selectedProperty().addListener((obs, oldValue, newValue) -> {
			transition.setAcceptsEmptyWord(newValue);
			curAutomate.editTransition(
					transition.from.numero,
					transition.to.numero,
					chars.getText(),
					newValue
			);
		});

		objAttr.getChildren().addAll(transitionLabel, chars, acceptsEmptyWord);
	}

	private GraphicalTransition createGraphicalTransIfNotExist(GraphicalState from, GraphicalState to) {
		for(GraphicalTransition transition: transitions) {
			if(transition.from == from && transition.to == to) return transition;
		}
		addTransition(from, to);
		return transitions.get(transitions.size() - 1);
	}
}

enum Outils {
	STATE,
	TRANSITION
}

class GraphicalState extends StackPane {
	protected static final double STATE_RADIUS = 15;
	protected static final double HITBOX_WIDTH = 20;

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
		else if(getChildren().size() >= 3){
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
	protected Rectangle hitbox = new Rectangle();
	protected Label chars = new Label();
	protected GraphicalState from, to;
	protected boolean acceptsEmptyWord;
	public GraphicalTransition(GraphicalState from, GraphicalState to) {
		super();
		this.from = from;
		this.to = to;
		getChildren().add(0, hitbox);
		getChildren().add(chars);
	}

	public void addChar(Character c) {
		chars.setText(chars.getText() + ' ' + c);
	}

	public void clearLabel() {
		chars.setText("");
	}

	public void setAcceptsEmptyWord(boolean b) {
		acceptsEmptyWord = b;
		if(acceptsEmptyWord) addChar('ε');
		else chars.getText().replace("ε", "");
	}
}