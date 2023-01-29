package EditeurAutomates.Controller;

import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class XMLController extends ViewController {

	@FXML
	public TextArea editor;

	@FXML
	public void initialize() {}

	@Override
	public void updateModel() {
		System.out.println("XML updating model");

		String new_xml = editor.getText();

		try {
			curAutomate = XMLParser.parseXML(new_xml);
		} catch(ParserException e){
		System.out.println("Caught parser exception. Cannot load graphical view.");
		}

	}

	@Override
	public void pullModel() {
		System.out.println("XML pulling model");
		//		editor.replaceText(0, 0, "Salut"); // a tester
	}

}
