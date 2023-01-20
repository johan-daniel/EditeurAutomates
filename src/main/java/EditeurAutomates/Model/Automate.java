package EditeurAutomates.Model;

import java.util.ArrayList;

public class Automate {
	ArrayList<ArrayList<ArrayList<State>>> transitionMatrix;
	ArrayList<String> symbolsList;

	public Automate(){
		this.transitionMatrix = new ArrayList<>();
		this.symbolsList = new ArrayList<>();
	}

	public void setFromXML(String xml_string){

	}

	public void createState(int x, int y){

	}

	public void deleteState(int state_number){

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
		if (transitionMatrix.size() == 0) return 0;

		// si un état a été supprimé: l'array list correspondant dans la matrice (la ligne) est nulle
		int i = 0;
		while(transitionMatrix.get(i) != null) i++;
		return i;
	}

	@Override
	public String toString() {
		return "Automate";
	}
}
