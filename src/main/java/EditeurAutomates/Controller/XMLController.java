package EditeurAutomates.Controller;

import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Objects;

public class XMLController extends ViewController {
	private String initial_xml = ""; // Valeur par défaut du TextArea au chargement de l'application

	@FXML private TextArea editor;

	@FXML
	public void initialize() {
		HBox.setHgrow(editor, Priority.ALWAYS);
		editor.textProperty().addListener((ov, fromTab , toTab) -> textChangeHandler());
	}

	@Override
	public void updateModel() {
		String xml = editor.getText();

		if (xml == null || xml.equals("")) return; // On autorise à changer de vue lorsqu'elle est vide

		try	{
			curAutomate = XMLParser.parseXML(xml);
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

	private void textChangeHandler(){
		if (justLoaded){
			justLoaded = false;
			return;
		}

		if (!Objects.equals(editor.getText(), initial_xml)){
			fileIsUpToDate = false;
		}
	}

}
