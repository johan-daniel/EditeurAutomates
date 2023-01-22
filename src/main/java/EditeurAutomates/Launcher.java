package EditeurAutomates;

import EditeurAutomates.Model.Automate;
import EditeurAutomates.Model.ParserException;
import EditeurAutomates.Model.XMLParser;

import java.util.Set;

public class Launcher {

	public static void main(String[] args) {

		// Debug functions
		Set<String> arguments = Set.of(args);
		if (arguments.contains("--debugModel") || arguments.contains("-dm")){
			debugModel();
			return;
		}

		// Default: launch main View
		AutomatesLab.main(args);

		System.out.println("Closing application AutomatesLab");
	}

	private static void debugModel(){
		Automate a = new Automate();
		a.createState(0, 0);
		a.createState(1, 1);
		a.createState(1, 2);
		System.out.println(a);
		a.deleteState(0);
		System.out.println(a);
		a.createTransition(1, 2, "abc", false);
		a.createTransition(1, 2, "a", false);
		System.out.println(a);
		a.createState(0,0);
		System.out.println(a);

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<>\noiuoiuoiu" +
				"<AutomateFile checksum=\"57\">\n" +
				"    \n" +
				"    <Automate>\n" +
				"   \t <State number=\"0\" isInitial=\"true\" X=\"15\" Y=\"25\">\n" +
				"   \t\t <Transition destination=\"0\" letters=\"ab\"/>\n" +
				"   \t\t <Transition destination=\"1\" letters=\"b\" acceptsEmptyWord=\"false\"/>\n" +
				"   \t </State>\n" +
				"   \t <State number=\"1\" X=\"35\" Y=\"25\">\n" +
				"   \t\t <Transition destination=\"2\" letters=\"a\"/>\n" +
				"   \t </State>\n" +
				"   \t <State number=\"2\" X=\"65\" Y=\"25\">\n" +
				"   \t\t <Transition destination=\"3\" letters=\"b\"/>\n" +
				"   \t </State>\n" +
				"   \t <State number=\"3\" isFinal=\"true\" X=\"95\" Y=\"25\">\n" +
				"   \t </State>\n" +
				"    </Automate>\n" +
				"    \n" +
				"</AutomateFile>";


		Automate res;

		try{
			res = XMLParser.parseXML(xml);
		} catch (ParserException e) {
			throw new RuntimeException(e);
		}

		System.out.println(res);
	}

}