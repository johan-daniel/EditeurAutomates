package EditeurAutomates.Controller;

import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.util.Objects;

public class XMLController extends ViewController {
	private String initial_xml = ""; // Valeur par défaut du TextArea au chargement de l'application

	@FXML public TextArea editor;

	@FXML
	public void initialize() {
		editor.textProperty().addListener((ov, fromTab , toTab) -> textChangeHandler());
	}

	@Override
	public void updateModel() {
		if (!xmlChanged()) return; // Pas besoin d'update le modèle

		try	{
			String edited_xml = editor.getText();
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

	protected void loadContentToXMLView(String content){
		editor.replaceText(0, editor.getText().length(), content);
		initial_xml = content;
		fileIsUpToDate = true;
	}

	protected String getEditorText(){
		return editor.getText();
	}

	private boolean xmlChanged(){
		String field_content = editor.getText();
		return (!Objects.equals(field_content, initial_xml));
	}

	private void textChangeHandler(){
		if (xmlChanged()){
			fileIsUpToDate = false;
		}
	}

}
