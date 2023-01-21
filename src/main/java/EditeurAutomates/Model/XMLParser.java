package EditeurAutomates.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class XMLParser {
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static String parseFile(File f){
		return "";
	}

	public static Automate parseXML(String xml){
		Automate res = null;
		String temp = "";
		String[] tokens;
		State curState;
//		ArrayList<Transition> t = new ArrayList<>();

		// On récupère un tableau des tags (tableau des String entre chevrons)
		ArrayList<String> tags = tokenize(xml);

		if (!Objects.equals(tags.get(0), XML_HEADER)) throw new RuntimeException("XML_HEADER");

		for (String each_tag : tags) {
			temp = each_tag.replace("<", "");
			temp = temp.replace(">", "");
			tokens = temp.split(" ");

			if (tokens.length == 0) continue; // On ignore les chevrons ; raise une erreur ?

			temp = tokens[0].toLowerCase();

//			switch (temp) {
//				case "automatefile":
//					if (tokens.length < 2) throw new RuntimeException("No checksum");
//					verifyChecksum(xml, tokens[1]);
//					break;
//				case "automate":
//					res = new Automate();
//					break;
//				case "state":
//					curState = parseState(tokens);
//					if (res == null) throw new RuntimeException("State found outside of Automate");
//					res.createState(curState);
//					break;
//				case "transition":
//					t.add(parseTransition(tokens, curState));
//					break;
//				case "/state":
//					continue;
//				case "/automate":
//					applyTransitions(res, Transition[] t);
//					break;
//				default:
//					throw new RuntimeException("Unknown XML tag");
//			}

		}

		System.out.println(tags);
		System.out.println(temp);
		return res;
	}

	private static ArrayList<String> tokenize(String xml){
		ArrayList<String> res = new ArrayList<>();
		int i = 0;
		int debut_token = 0;
		String temp;
		char[] chars = xml.toCharArray();

		while (i < chars.length){
			if (chars[i] == '<') debut_token = i;
			if (chars[i] == '>'){
				temp = xml.substring(debut_token, i+1);
				temp = temp.replaceAll("\n", " ");
				temp = temp.replaceAll("\r", " ");
				temp = temp.replaceAll("\t", " ");
				res.add(temp);
			}
			i++;
		}

		return res;
	}

	public static long calculateChecksum(Automate input){
		return 0;
	}
}
