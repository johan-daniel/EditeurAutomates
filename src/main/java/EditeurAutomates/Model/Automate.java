package EditeurAutomates.Model;

import java.util.ArrayList;

public class Automate {
	ArrayList<ArrayList<ArrayList<State>>> transitionMatrix;
	ArrayList<String> symbolsList;
	ArrayList<State> statesList;

	public Automate(){
		this.transitionMatrix = new ArrayList<>();
		this.symbolsList = new ArrayList<>();
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
		statesList.add(indice, s);
		transitionMatrix.add(indice, new ArrayList<>());
	}

	public void deleteState(int state_number){
		int indice = getStateIndex(state_number);
		statesList.remove(indice);

//		for (ArrayList<ArrayList<State>> each_state : transitionMatrix) {
//			for(ArrayList<State> each_transition : each_state){
//				each_transition.remove(state_number);
//			}
//		}
	}

	public void createTransition(int from_state, int to_state){

	}

	public void editTransition(int from_state, int to_state, String new_symbols){

	}

	public void setStateInitial(int state){

	}

	public void setStateFinal(int state){

	}

	private int getNextFreeStateNumber(){
		// Fonction locale lambda
		java.util.function.BiFunction<ArrayList<State>, Integer, Boolean> indiceIsInList = (s, i) -> {
			for(State state : s){
				if (state.numero == i) return true;
			}
			return false;
		};

		int i = 0; // on cherche le dernier indice de la suite avant le premier trou
		while(indiceIsInList.apply(statesList, i)) i++; // tant que les éléments se suivent, on incrémente i
		return i;
	}

	private int getStateIndex(int state_number){
		int i=0;
		for(State s : statesList){
			if (s.numero == state_number) return i;
			i++;
		}
		return -1; // pas dans la liste
	}

	@Override
	public String toString() {
		return "Automate{" + "\n" +
				"\t" + "transitionMatrix=" + transitionMatrix + "\n" +
				"\t" + "symbolsList=" + symbolsList + "\n" +
				"\t" + "statesList=" + statesList + "\n" +
				'}';
	}
}
