package EditeurAutomates.Controller;

import javafx.scene.control.TextArea;

public class XMLController extends ViewController {

	public TextArea editor;

	@Override
	public void updateModel() {
		System.out.println("Updating model");
	}

	@Override
	public void pullModel() {
		System.out.println("Pulling model");
		//		editor.replaceText(0, 0, "Salut"); // a tester
	}

}
