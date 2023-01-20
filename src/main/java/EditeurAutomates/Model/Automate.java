package EditeurAutomates.Model;

import java.util.ArrayList;

public class Automate {
	ArrayList<ArrayList<ArrayList<State>>> transitionMatrix;

	public void setFromXML(String xml_string){

	}

	public void createState(){

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
		return 0;
	}

	@Override
	public String toString() {
		return "Automate";
	}
}
