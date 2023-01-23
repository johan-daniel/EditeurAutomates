package EditeurAutomates.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Automate {
	protected ArrayList<State> statesList;
	protected ArrayList<Character> alphabet;
	protected ArrayList<ArrayList<Destinations>> transitionMatrix;

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
		for(char ignored : alphabet) transitions.add(new Destinations()); // on ajoute un groupe de Destinations par lettre de l'alphabet
		transitionMatrix.add(indice, transitions);
	}

	public void deleteState(int state_number){
		if (state_number>statesList.size()) return; // Il n'existe pas dans la liste d'états

		// On remplace l'état par une référence nulle : dans la liste des états, dans la matrice (sa ligne devient nulle)
		statesList.remove(state_number);
		statesList.add(state_number, null);
		transitionMatrix.remove(state_number);
		transitionMatrix.add(state_number, null);

		// On supprime toutes les références (transition) qui arrivent vers cet état
		for (ArrayList<Destinations> from_states : transitionMatrix) {
			if (from_states == null) continue; // L'état n'existe pas
			for(Destinations to_states : from_states){
				to_states.removeDestination(state_number);
			}
		}

		// Un symbole peut ne plus être référencé, on traite ce cas
		cleanAlphabet();
	}

	// TODO transformer la première ligne "Cannot add empty transition" en appel deleteTransition ?
	// TODO débugger: ne crash pas, mais la matrice ne contient pas de transitions
	public void createTransition(int from_state, int to_state, String symbols, boolean acceptsEmptyWord) throws RuntimeException {
		if ((symbols==null || symbols.equals("")) && !acceptsEmptyWord) return; // Cannot add empty transition
		if (from_state<0 || to_state<0) throw new RuntimeException("Cannot create transition between none existing states");

		Character[] symbols_array = getCharacterArray(symbols, acceptsEmptyWord);

		// Ajout des NOUVEAUX symboles à l'alphabet et à l'automate
		for(Character cur_symbol : symbols_array){
			if (getIndex(cur_symbol)==-1) {
				alphabet.add(cur_symbol);
				transitionMatrix.get(from_state).add(new Destinations()); // Ajout d'une liste de destinations à la fin de celles existantes, correspondant au nouveau symbole
			}
		}

		// Ajout de la destination
		Destinations d_temp;
		int char_index;
		for(Character c : alphabet){ 									// Pour chaque symbole de l'alphabet (on parcourt transitionMatrix selon j)
			char_index = getIndex(c);
			if (char_index <0 ) continue; 								// Si le symbole ne fait partie de ceux de la transition, on l'ignore
			d_temp = transitionMatrix.get(from_state).get(char_index); 	// en transitionMatrix[from_state, char]
			d_temp.add(to_state); 										// on ajoute la destination to_state aux Destinations
		}
	}

	// TODO
	public void editTransition(int from_state, int to_state, String new_symbols, boolean acceptsEmptyWord){
		System.out.println("Edit transition: " + from_state + " " +  to_state + " " + new_symbols + " " + acceptsEmptyWord);
	}

	public void setStateInitial(int state){
		State s = statesList.get(state);
		for(State each_state : statesList) if (each_state.isInitial) each_state.isInitial = false;
		s.isInitial = !s.isInitial;
	}

	public void setStateFinal(int state){
		State s = statesList.get(state);
		s.isFinal = !s.isFinal;
	}

	private int getNextFreeStateNumber(){
		if (statesList.size()==0) return 0;
		int i = 0;
		while (i<statesList.size() && statesList.get(i) != null) i++;
		return i;
	}

	// TODO : cette fonction doit supprimer les symboles de l'array-list alphabet si ils ne sont plus référencés par aucune transition dans la matrice
	private void cleanAlphabet(){
		// Pour cela, parcourir tout les symboles de la matrice, et si toutes les Destinations sont vides, on supprime le symbole
	}

	private Character[] getCharacterArray(String str, boolean acceptsEmptyWord){
		if(acceptsEmptyWord && (str==null)) return new Character[] {null};

		int taille_tab = acceptsEmptyWord ? str.length()+1 : str.length();
		Character[] res = new Character[taille_tab];

		int i = 0;
		if (acceptsEmptyWord) res[i++] = null;
		for( ; i<taille_tab ; i++) res[i] = str.charAt(i);

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

	static class Destinations extends ArrayList<Integer> {

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

		private boolean isInDestinations(Integer i){
			for(Integer each_i : this) if (Objects.equals(each_i, i)) return true;
			return false;
		}

	}

}