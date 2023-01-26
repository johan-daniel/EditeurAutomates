package EditeurAutomates.Model;

import java.util.ArrayList;
import java.util.Objects;

public class XMLParser {
	private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public static Automate parseXML(String xml) throws ParserException, RuntimeException {
		Automate cur_automate = null;
		State cur_state = null;
		ArrayList<Transition> all_transitions = null;
		boolean first_tag_found = false;
		boolean last_tag_reached = false;

		Automate res = null;

		String temp, first_token;
		String[] tokens;

		// On récupère le tableau des balises (tags) XML
		ArrayList<String> tags = tokenize(xml);
		if (tags.size() == 0) throw new ParserException("No token found in XML string");

		// Vérification du header
		if (!Objects.equals(tags.get(0), XML_HEADER)) throw new ParserException("Incorrect XML Header");

		for (String each_tag : tags) {

			if (last_tag_reached) throw new ParserException("AutomateFile should be the last tag of the file");

			temp = each_tag.replace("<", "");
			temp = temp.replace(">", "");

			// On récupère les tokens
			tokens = temp.split(" ");
			if (tokens.length == 0) continue; // On ignore les chevrons vides
			if (Objects.equals(tokens[0], "")) continue;

			// Switch sur le premier token
			first_token = tokens[0].toLowerCase();

			switch (first_token) {
				case "?xml" -> { }
				case "automatefile" -> {
					first_tag_found = true;
					if (tokens.length < 2) throw new ParserException("No checksum in string");
				}
				case "/automatefile" -> {
					last_tag_reached = true; // doit être la dernière balise du fichier
					if (cur_automate != null) throw new ParserException("Any opened tag must be closed in correct order [Automate]");
					if (cur_state != null) throw new ParserException("Any opened tag must be closed in correct order [State]");
				}
				case "automate" -> {
					if (cur_automate != null) throw new ParserException("Any opened tag must be closed in correct order [Automate]");
					if (cur_state != null) throw new ParserException("Any opened tag must be closed in correct order [State]");
					cur_automate = new Automate();
					all_transitions = new ArrayList<>();
				}
				case "/automate" -> {
					if (cur_automate == null) throw new ParserException("Found closing tag without opening [Automate]");
					if (cur_state != null) throw new ParserException("Any opened tag must be closed in correct order [State]");
					applyTransitions(cur_automate, all_transitions);
					res = cur_automate;
					cur_automate = null;
				}
				case "state" -> {
					if (cur_automate == null) throw new ParserException("State tag found outside of Automate");
					if (cur_state != null) throw new ParserException("State tag openend inside a state");
					try {
						cur_state = parseState(tokens);
					} catch (NumberFormatException e){
						throw new ParserException("Incorrect integer attribute value in State");
					}
					if (cur_state.numero < cur_automate.statesList.size() && cur_automate.statesList.get(cur_state.numero) != null) throw new ParserException("Automate contains two States with same number");
					cur_automate.createState(cur_state); // On l'ajoute à l'automate
				}
				case "/state" -> cur_state = null;
				case "transition" -> {
					if (cur_state == null) {
						throw new ParserException("Cannot add Transition outside of source State");
					}
					try {
						all_transitions.add(parseTransition(tokens, cur_state));
					} catch (NumberFormatException e){
						throw new ParserException("Incorrect integer attribute value in transition");
					}
				}
				case "/transition" -> throw new ParserException("Transition tags must be auto-closing !");
				default -> throw new ParserException("Unknown XML tag");
			}

		}

		if(!first_tag_found) throw new ParserException("Couldn't find opening AutomateFile tag");
		if(!last_tag_reached) throw new ParserException("Couldn't find closing AutomateFile tag");
		if (cur_automate != null) throw new ParserException("Any opened tag must be closed in correct order [Automate]");
		if (cur_state != null) throw new ParserException("Any opened tag must be closed in correct order [State]");

		return res;
	}

	private static State parseState(String[] tokens) throws ParserException, NumberFormatException {
		Integer n = null;
		int x = 0, y = 0;
		boolean isInitial = false, isFinal = false;

		for(String token : tokens){
			token = token.toLowerCase();

			// Number
			if (token.startsWith("number=\"") && token.endsWith("\"")){
				token = token.replace("number=\"", "");
				token = token.substring(0, token.length()-1);
				n = Integer.parseInt(token);
			}
			// X
			if (token.startsWith("x=\"") && token.endsWith("\"")){
				token = token.replace("x=\"", "");
				token = token.substring(0, token.length()-1);
				x = Integer.parseInt(token);
			}
			// Y
			if (token.startsWith("y=\"") && token.endsWith("\"")){
				token = token.replace("y=\"", "");
				token = token.substring(0, token.length()-1);
				y = Integer.parseInt(token);
			}
			// isInitial
			if (token.startsWith("isinitial=\"") && token.endsWith("\"")){
				token = token.replace("isinitial=\"", "");
				token = token.substring(0, token.length()-1);
				isInitial = Boolean.parseBoolean(token);
			}
			// isFinal
			if (token.startsWith("isfinal=\"") && token.endsWith("\"")){
				token = token.replace("isfinal=\"", "");
				token = token.substring(0, token.length()-1);
				isFinal = Boolean.parseBoolean(token);
			}
		}

		if(n==null) throw new ParserException("State does not contain number");
		return new State(n, x, y, isInitial, isFinal);
	}

	private static Transition parseTransition(String[] tokens, State from) throws ParserException, NumberFormatException {
		Integer to = null;
		String symboles = "";
		boolean acceptsEmptyWord = false;

		if (tokens.length<1) throw new ParserException("Error, state has not enought attributes (missing at least destination state)");
		int temp = tokens.length-1;
		tokens[temp] = tokens[temp].substring(0, tokens[temp].length()-1); // On enlève le / de la balise auto-fermante

		for(String token : tokens){
			token = token.toLowerCase();

			// Number
			if (token.startsWith("destination=\"") && token.endsWith("\"")){
				token = token.replace("destination=\"", "");
				token = token.substring(0, token.length()-1);
				to = Integer.parseInt(token);
			}
			// Symboles
			if (token.startsWith("letters=\"") && token.endsWith("\"")){
				token = token.replace("letters=\"", "");
				token = token.substring(0, token.length()-1);
				symboles = token;
			}
			// AcceptsEmptyWord
			if (token.startsWith("acceptsEmptyWord=\"") && token.endsWith("\"")){
				token = token.replace("acceptsEmptyWord=\"", "");
				token = token.substring(0, token.length()-1);
				acceptsEmptyWord = Boolean.parseBoolean(token);
			}
		}

		if (to == null) throw new ParserException("Transition has no destination State");
		return new Transition(from.numero, to, symboles, acceptsEmptyWord);
	}

	private static void applyTransitions(Automate mato, ArrayList<Transition> transitions) throws ParserException {
		int res;
		for(Transition t : transitions){
			res = mato.createTransition(t.from, t.to, t.symbols, t.acceptsEmptyWord);
			if (res!=0) throw new ParserException("Cannot create transition between none existing states"); // State does not exist
		}
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

	// TODO: calculateChecksum doit supprimer la valeur de la checksum du string avant de le hasher
	public static long calculateChecksum(String input){
		// 64 bits - adapted from String.hashCode()
		// See https://stackoverflow.com/questions/1660501/what-is-a-good-64bit-hash-function-in-java-for-textual-strings

		long hash = 1125799906942597L; // prime
		int len = input.length();

		for (int i = 0; i < len; i++) {
			hash = 31*hash + input.charAt(i);
		}
		return hash;
	}

	public static boolean verifyChecksum(String xml){
		if (!xml.contains("checksum=\"")) return false;

		String str_checksum;
		long checksum;

		// On récupère le string de la checksum (le contenu entre les guillemets du string "checksum=\"57\"" ; => on doit obtenir "57")
		int debut = 0; // Début du sous-string checksum="5742"
		int fin;
		while(!xml.startsWith("checksum=\"", debut)) debut++;
		debut += 10; // taille du string checksum="
		fin = debut + 1;
		while(!xml.startsWith("\"", fin)) fin++; // On va jusqu'au guillemet suivant

		try {
			str_checksum = xml.substring(debut, fin);
			checksum = Long.parseLong(str_checksum);
			System.out.println(checksum);
			return (checksum == calculateChecksum(xml));
		} catch (IndexOutOfBoundsException | SecurityException | NullPointerException ignored){
			return false;
		}
	}

	record Transition(int from, int to, String symbols, boolean acceptsEmptyWord) {	}

}