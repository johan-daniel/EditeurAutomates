package EditeurAutomates.Controller;

import javafx.fxml.FXML;
import javafx.scene.layout.HBox;

public class GraphicController extends ViewController {

	@FXML public HBox toolbox;
	@FXML public HBox drawArea;

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
