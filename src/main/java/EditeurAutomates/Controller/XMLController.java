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
		String edited_xml = editor.getText();

		try {
			curAutomate = XMLParser.parseXML(edited_xml);
		} catch(ParserException e){
			System.err.println("Caught parser exception: " + e.getMessage() + " ; Cannot load graphical view.");
		}

	}

	@Override
	public void pullModel() {
		String replacement;
		if (curAutomate==null) replacement = "";
		else {
			replacement = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<AutomateFile checksum=\"\">\n" + curAutomate.toXML() + "\n</AutomateFile>";
		}

		editor.replaceText(0, editor.getText().length(), replacement);
	}

}
