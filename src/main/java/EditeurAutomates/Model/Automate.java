package EditeurAutomates.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Automate implements XMLConvertible {
	protected ArrayList<State> statesList;
	protected ArrayList<Character> alphabet;
	protected ArrayList<ArrayList<Destinations>> transitionMatrix;

	public ArrayList<State> getStatesList() {
		return statesList;
	}

	public ArrayList<Character> getAlphabet() {
		return alphabet;
	}

	public ArrayList<ArrayList<Destinations>> getTransitionMatrix() {
		return transitionMatrix;
	}

	public Automate(){
		this.statesList = new ArrayList<>();
		this.alphabet = new ArrayList<>();
		this.transitionMatrix = new ArrayList<>();
	}

	public void createState(int x, int y){
		int indice = getNextFreeStateNumber();
		createState(new State(indice, x, y, false, false));
	}

	public void createState(int x, int y, boolean isInitial, boolean isFinal){
		int indice = getNextFreeStateNumber();
		createState(new State(indice, x, y, isInitial, isFinal));
	}

	public void createState(State s){
		int indice = s.numero;

		// Si on insère dans un trou (état existant puis supprimé), on supprime la référence nulle
		if (indice<statesList.size()) {
			statesList.remove(indice);
			transitionMatrix.remove(indice);
		}

		// Si on insère après la taille de la matrice/liste d'états, il faut ajouter des références nulles pour les états inexistants
		if (indice>statesList.size()){
			for(int i=statesList.size() ; i<indice ; i++) {
				statesList.add(null);
				transitionMatrix.add(null);
			}
		}

		// Si on insère pile à la fin, (indice==statesList.size()), on n'a rien de plus à faire qu'à insérer à l'indice

		// Ajout de l'état
		statesList.add(indice, s);
		ArrayList<Destinations> transitions = new ArrayList<>();
		for(Character ignored : alphabet) transitions.add(new Destinations()); // on ajoute un groupe de Destinations par lettre de l'alphabet
		transitionMatrix.add(indice, transitions);
	}

	public void deleteState(int state_number) {
		int dernier_indice = statesList.size()-1;
		if (state_number>dernier_indice) return;
		if (isInvalidState(state_number)) return;

		// On enlève l'état de la liste et de la matrice
		statesList.remove(state_number);
		transitionMatrix.remove(state_number);
		statesList.add(state_number, null);			// On remplace l'état par une référence nulle (pour conserver
		transitionMatrix.add(state_number, null);	// la propriété: chaque Etat est rangé à son numéro

		// On enlève les null à la fin de la liste d'états et de la matrice
		int i = transitionMatrix.size()-1;
		while(i>=0 && transitionMatrix.get(i)==null){
			statesList.remove(i);
			transitionMatrix.remove(i);
			i--;
		}

		// On supprime toutes les références (transition) qui arrivent vers cet état
		for (ArrayList<Destinations> from_states : transitionMatrix) {
			if (from_states == null) continue; // L'état n'existe pas
			for(Destinations to_states : from_states){
				to_states.removeDestination(state_number);
			}
		}

		// Un symbole peut ne plus être référencé, on traite ce cas
		clearAlphabet();
	}

	public int createTransition(int from_state, int to_state, String symbols, boolean acceptsEmptyWord) {
		if (isInvalidState(from_state) || isInvalidState(to_state)) return -1; // Error: Cannot create transition between none existing states

		if ((symbols==null || symbols.equals("")) && !acceptsEmptyWord) {
			deleteTransition(from_state, to_state);
			return 0; // Success
		}

		ArrayList<Destinations> temp;
		Character[] symbols_array = getCharacterArray(symbols, acceptsEmptyWord);

		// On ajoute les nouveaux symboles, si il y en a
		updateAlphabet(symbols_array);

		// Ajout de la destination
		temp = transitionMatrix.get(from_state);
		Destinations d_temp;
		int char_index;
		for(Character c : symbols_array){ 	// Pour chaque symbole à ajouter
			char_index = getIndex(c);		// On récupère son indice
			d_temp = temp.get(char_index); 	// en transitionMatrix[from_state, char]
			d_temp.add(to_state); 			// on ajoute la destination to_state aux Destinations
		}

		return 0; // Success
	}

	public void editTransition(int from_state, int to_state, String new_symbols, boolean acceptsEmptyWord) {
		if (isInvalidState(from_state) || isInvalidState(to_state)) return;

		if ((Objects.equals(new_symbols, "") || new_symbols==null) && !acceptsEmptyWord){
			deleteTransition(from_state, to_state);
			return;
		}

		ArrayList<Destinations> destinations = transitionMatrix.get(from_state);
		Character[] symbols_array = getCharacterArray(new_symbols, acceptsEmptyWord);

		// On ajoute les nouveaux symboles, si il y en a
		updateAlphabet(symbols_array);

		// Les symboles sont concernés par la transition à éditer
		for(Character c : symbols_array){
			destinations.get(getIndex(c)).add(to_state);
		}

		// Les symboles ne sont pas/plus concernés par la transition à éditer
		for(Character c : alphabet){
			if (c==null) { // On traite null à part (car on ne peut pas appeler contains(null))
				if (!acceptsEmptyWord) destinations.get(getIndex(null)).removeDestination(to_state);
				continue;
			}
			if (!new_symbols.contains(c.toString())){
				destinations.get(getIndex(c)).removeDestination(to_state);
			}
		}

		clearAlphabet();
	}

	public void deleteTransition(int from_state, int to_state) {
		if (isInvalidState(from_state) || isInvalidState(to_state)) return;

		for(Destinations d : transitionMatrix.get(from_state)) d.removeDestination(to_state);
		clearAlphabet();
	}

	public void setStateInitial(int state) {
		if (isInvalidState(state)) return;

		State s = statesList.get(state);
		for(State each_state : statesList) if (each_state.isInitial) each_state.isInitial = false;
		s.isInitial = !s.isInitial;
	}

	public void setStateFinal(int state) {
		if (isInvalidState(state)) return;

		State s = statesList.get(state);
		s.isFinal = !s.isFinal;
	}

	private int getNextFreeStateNumber(){
		if (statesList.size()==0) return 0;
		int i = 0;
		while (i<statesList.size() && statesList.get(i) != null) i++;
		return i;
	}

	/**Met à jour l'alphabet et la matrice des nouvelles lettres (si il y en a) contenues dans le tableau symbols.*/
	private void updateAlphabet(Character[] symbols){
		ArrayList<Destinations> temp;
		for(Character cur_symbol : symbols){
			if (getIndex(cur_symbol)==-1) {
				alphabet.add(cur_symbol); 								// On ajoute le symbole à fin de l'alphabet
				for(int i=0 ; i<statesList.size() ; i++ ){ 				// Pour tout les états
					temp = transitionMatrix.get(i); 					// On ajoute une liste de destinations, lisant le nouveau symbole
					if (temp != null) temp.add(new Destinations()); 	// (à la fin, comme pour l'alphabet)
				}
			}
		}
	}

	/**Nettoie l'alphabet et la matrice des lettres qui ne sont plus utilisées.*/
	private void clearAlphabet(){
		for(int i=0 ; i<alphabet.size() ; i++){ 	// Pour chaque lettre dans l'alphabet
			if (!isInAnyTransition(i)){				// Si elle n'est référencée par aucune transition
				removeChar(i);						// Ça dégage
				i--;
			}
		}
	}

	// Pour clearAlphabet
	private boolean isInAnyTransition(int char_index){
		for (ArrayList<Destinations> state : transitionMatrix) {
			if (state == null) continue;
			if (state.get(char_index).size() > 0) return true;
		}
		return false;
	}

	// Pour clearAlphabet
	private void removeChar(int char_index){
		alphabet.remove(char_index);
		for (ArrayList<Destinations> state : transitionMatrix) {
			if (state == null) continue;
			state.remove(char_index);
		}
	}

	/**Retourne un tableau de Character pour chaque char dans str, plus une référence vide pour représenter acceptsEmptyWord si acceptsEmptyWord vaut true.*/
	private static Character[] getCharacterArray(String str, boolean acceptsEmptyWord){
		if (str==null) return acceptsEmptyWord ? new Character[]{null} : new Character[0];

		int offset = acceptsEmptyWord ? 1 : 0; // On rajoute une case pour le mot vide
		int taille_tab = str.length() + offset;
		Character[] res = new Character[taille_tab];

		int i = 0;
		if (acceptsEmptyWord) res[i++] = null;
		for( ; i<taille_tab ; i++) res[i] = str.charAt(i-offset);

		return res;
	}

	/**Retourne l'index du char dans l'alphabet (et dans la matrice, ce sont les mêmes indices). -1 si le char n'est pas dans l'alphabet.*/
	private int getIndex(Character c){
		int i = 0;
		for(Character s : alphabet){
			if (s == c) return i;
			i++;
		}
		return -1; // pas dans la liste
	}

	private boolean isInvalidState(int state){
		return (state < 0 || state >= statesList.size() || statesList.get(state) == null);
	}

	@Override
	public String toString() {
		return "Automate{" +
				"statesList=" + statesList +
				", alphabet=" + alphabet +
				", transitionMatrix=" + transitionMatrix +
				'}';
	}

	public String toDetails(){
		return "Automate{" + "\n" +
				"\tstatesList=" + statesList + "\n" +
				"\talphabet=" + alphabet + "\n" +
				"\ttransitionMatrix=" + transitionMatrix + "\n" +
				'}';
	}

	@Override
	public String toXML() {
		StringBuilder res = new StringBuilder("\t<Automate>\n");

		// On ajoute tous les états
		for(State cur_state : statesList){
			if(cur_state==null) continue;

			res.append("\t\t");
			res.append(cur_state.toXML());
			res.append("\n");

			// On ajoute toutes les transitions partant de cet état
			for(State s : statesList){ // Etat de destination (transitions: cur_state -> s)
				if(s==null) continue;
				StringBuilder symbols = new StringBuilder();
				boolean acceptsEmptyWord = false;
				for(Character c : alphabet){
					if (transitionMatrix.get(cur_state.numero).get(getIndex(c)).isInDestinations(s.numero)){ // On a une transition
						if (c == null) {
							acceptsEmptyWord = true;
						}
						else {
							symbols.append(c);
						}
					}
				}
				res.append("\t\t\t<Transition destination=\"");
				res.append(s.numero);
				res.append("\" letters=\"");
				res.append(symbols);
				res.append("\" acceptsEmptyWord=\"");
				res.append(acceptsEmptyWord);
				res.append("\"/>\n");
			}

			res.append("\t\t</State>\n"); // State.toXML ne renvoie que la balise ouvrante (avec les attributs) et pas la balise fermante ; on l'ajoute ici
		}

		res.append("\t</Automate>");
		return res.toString();
	}


}

class Destinations extends ArrayList<Integer> {

	@Override
	public boolean add(Integer to){
		if (isInDestinations(to)) return false;

		super.add(to);
		return true;
	}

	public void removeDestination(Integer to){
		Integer temp;
		for (Iterator<Integer> it = this.iterator(); it.hasNext(); ) {
			temp = it.next();
			if (Objects.equals(temp, to)) it.remove();
		}
	}

	public boolean isInDestinations(Integer i){
		for(Integer each_i : this) if (Objects.equals(each_i, i)) return true;
		return false;
	}

}
