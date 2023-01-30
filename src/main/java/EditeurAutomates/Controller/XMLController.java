package EditeurAutomates.Controller;

import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.Objects;

public class XMLController extends ViewController {
	private String initial_xml;

	@FXML
	public TextArea editor;

	// TODO mettre à jour la variable fileIsUpToDate à false lors d'un changement

	// TODO rester sur la vue XML si erreur. Comment faire ? dans tabChangeHandler (avec un throw) ?
	@Override
	public void updateModel() {
		String edited_xml = editor.getText();

		if (Objects.equals(edited_xml, initial_xml)) return; // Pas besoin d'update le modèle
		try	{
			curAutomate = XMLParser.parseXML(edited_xml);
		} catch(ParserException e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public void pullModel() {
		String replacement;
		if (curAutomate==null) replacement = "";
		else replacement = XMLParser.getViewXML(curAutomate);

		editor.replaceText(0, editor.getText().length(), replacement);
		initial_xml = replacement;
	}

	public void loadContentToXMLView(String content){
		editor.replaceText(0, editor.getText().length(), content);
		initial_xml = content;
	}

}
