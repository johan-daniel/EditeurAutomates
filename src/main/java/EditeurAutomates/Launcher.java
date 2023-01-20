package EditeurAutomates;

import EditeurAutomates.Model.Automate;

import java.util.ArrayList;
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
	}

}