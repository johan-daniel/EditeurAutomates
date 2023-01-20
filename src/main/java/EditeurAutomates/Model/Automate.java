package EditeurAutomates.Model;

import java.util.ArrayList;
import java.util.Iterator;

public class Automate {
	ArrayList<State> statesList;
	ArrayList<ArrayList<ArrayList<State>>> transitionMatrix;
	ArrayList<String> alphabet;

	public Automate(){
		this.transitionMatrix = new ArrayList<>();
		this.alphabet = new ArrayList<>();
		this.statesList = new ArrayList<>();
	}

	public void createState(int x, int y){
		createState(x, y, false, false, false);
	}
	public void createState(int x, int y, boolean isInitial, boolean isFinal){
		createState(x, y, isInitial, isFinal, false);
	}
	public void createState(int x, int y, boolean isInitial, boolean isFinal, boolean acceptsEmptyWord){
		int indice = getNextFreeStateNumber();
		State s = new State(indice, x, y, isInitial, isFinal, acceptsEmptyWord);

		// Si on insère dans un trou (état existant puis supprimé), on supprime la référence nulle
		if (indice<statesList.size()) {
			statesList.remove(indice);
			transitionMatrix.remove(indice);
		}

		// Ajout de l'état
		statesList.add(indice, s);
		transitionMatrix.add(indice, new ArrayList<>());
	}

	public void deleteState(int state_number){
		int indice = getStateIndex(state_number);
		State temp;

		// On remplace l'état de la liste des états par une référence nulle
		statesList.remove(indice);
		statesList.add(indice, null);

		// On remplace la ligne de la matrice associée à l'état par une référence nulle
		transitionMatrix.remove(indice);
		transitionMatrix.add(indice, null);

		// On supprime toutes les références (transition) qui arrivent vers cet état
		for (ArrayList<ArrayList<State>> from_states : transitionMatrix) {
			if (from_states == null) continue;
			for(ArrayList<State> to_states : from_states){
				if (to_states == null) continue;
				for (Iterator<State> it = to_states.iterator(); it.hasNext(); ) {
					temp = it.next();
					if (temp.numero == state_number) it.remove();
				}
			}
		}

		// Eventuellement à rajouter: supprimer les symboles de l'array-list si ils ne sont plus référencés par aucune transition
	}

	// TODO finir la dernière partie "Ajout de la transition dans la matrice"
	public void createTransition(int from_state, int to_state, String symbols){
		if (from_state<0 || to_state<0) return; // Les états n'existent pas dans l'automate

		// Ajout des symboles à l'alphabet de l'automate
		char[] symbols_array = symbols.toCharArray();
		for(char cur_symbol : symbols_array){
			if (getCharIndex(cur_symbol)==-1) alphabet.add(String.valueOf(cur_symbol));
		}

		// Ajout de la transition dans la matrice
		State destination_state = statesList.get(to_state);
//		ArrayList<State> destinations_list = transitionMatrix.get(from_state).get(to_state); // remplacer 0 par l'indice du char
//		destinations_list.add(destination_state);
	}

	public void editTransition(int from_state, int to_state, String new_symbols){

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

	public int getStateIndex(int state_number){
		int i = 0;
		for(State s : statesList){
			if (s!=null && s.numero == state_number) return i;
			i++;
		}
		return -1; // pas dans la liste
	}

	public int getCharIndex(char c){
		int i = 0;
		for(String s : alphabet){
			if (s.equals(String.valueOf(c))) return i;
			i++;
		}
		return -1; // pas dans la liste
	}

	@Override
	public String toString() {
		return "Automate:" + "\n" +
				"\t" + "transitionMatrix=" + transitionMatrix + "\n" +
				"\t" + "alphabet=" + alphabet + "\n" +
				"\t" + "statesList=" + statesList;
	}
}
