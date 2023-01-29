package EditeurAutomates.Controller;

import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class XMLController extends ViewController {

	@FXML
	public TextArea editor;

	@Override
	public void updateModel() {
		System.out.println("Updating model from XML view");

//		String new_xml = editor.getText();
//
//		try {
//			curAutomate = XMLParser.parseXML(new_xml);
//		} catch(ParserException e){
//			System.out.println("Caught parser exception. Cannot load graphical view.");
//		}

	}

	@Override
	public void pullModel() {
		System.out.println("Pulling model to XML view");
		//		editor.replaceText(0, 0, "Salut"); // a tester
	}

}
