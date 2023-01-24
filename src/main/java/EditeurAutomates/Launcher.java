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

		a.createState(0, 0, true, false); // 0
		a.createState(1, 1); // 1
		a.createState(0, 0); // 2
		a.createState(0, 0); // 3
		a.createState(0, 0, false, true); // 4
		a.createState(0, 0); // 5
		a.createState(0, 0); // 6

		a.deleteTransition(0,0);
		a.deleteTransition(4,5);

		a.createTransition(0, 1, "a", false);
		a.createTransition(1, 2, "a", false);
		a.createTransition(2, 3, "b", true);
		a.createTransition(3, 3, "a", false);
		a.createTransition(3, 4, "c", false);
		a.createTransition(4, 4, "d", false);
		a.createTransition(4, 5, "a", false);
		a.createTransition(5, 2, "ce", false);
		a.createTransition(2, 5, "ace", false);
		a.createTransition(5, 6, "d", true);
		a.createTransition(6, 1, "b", false);

		a.deleteTransition(0,0);
		a.deleteTransition(4,5);

//		a.deleteState(0);
//		a.deleteState(1);
//		a.deleteState(2);
//		a.deleteState(3);
//		a.deleteState(4);
//		a.deleteState(5);
//		a.deleteState(6);

		System.out.println(a.toDetails());

		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<>\noiuoiuoiu" +
				"<AutomateFile checksum=\"57\">\n" +
				"    \n" +
				"    <Automate>\n" +
				"   \t <State number=\"0\" isInitial=\"true\" X=\"15\" Y=\"25\">\n" +
				"   \t\t <Transition destination=\"0\" letters=\"ab\"/>\n" +
				"   \t\t <Transition destination=\"1\" letters=\"b\" acceptsEmptyWord=\"true\"/>\n" +
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

		System.out.println(res.toDetails());
	}

}