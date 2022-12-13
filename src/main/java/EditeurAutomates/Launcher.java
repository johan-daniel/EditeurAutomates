package EditeurAutomates;

import java.util.Objects;

public class Launcher {

    public static void main(String[] args) {

        // Debug functions
        if (args.length > 1 && (Objects.equals(args[1], "--debugModel") || Objects.equals(args[1], "-dm"))){
            debugModel();
            return;
        }
        if (args.length > 1 && (Objects.equals(args[1], "--debugController") || Objects.equals(args[1], "-dc"))){
            debugController();
            return;
        }

        // Default: launch main view
        AutomateViewer.main(args);
    }

    private static void debugModel(){
        System.out.println("debugModel");
    }

    private static void debugController(){
        System.out.println("debugController");
    }

}