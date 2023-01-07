package EditeurAutomates;

import java.util.Set;

public class Launcher {

	public static void main(String[] args) {

		// Debug functions
		Set<String> arguments = Set.of(args);
		if (arguments.contains("--debugModel") || arguments.contains("-dm")){
			debugModel();
			return;
		}
		if (arguments.contains("--debugController") || arguments.contains("-dc")){
			debugController();
			return;
		}

		// Default: launch main View
		AutomatesLab.main(args);

		System.out.println("Closing application AutomatesLab");
	}

	private static void debugModel(){
		System.out.println("debugModel");
	}

	private static void debugController(){
		System.out.println("debugController");
	}

}