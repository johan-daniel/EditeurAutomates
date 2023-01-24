import EditeurAutomates.Model.Automate;
import EditeurAutomates.Model.State;
import org.junit.jupiter.api.Test;
public class AutomateTest {
    private Automate automate;
    @Test
    public void testInitAutomate() {
        automate = new Automate();
        assert(automate.getAlphabet().size() == 0);
        assert(automate.getStatesList().size() == 0);
        assert(automate.getTransitionMatrix().size() == 0);
    }

    @Test
    public void testCreateState() {
        automate = new Automate();

        // Regular state
        automate.createState(10, 10);
        assert(automate.getStatesList().size() == 1);
        assert(automate.getStatesList().get(0).numero == 0);

        // Initial state
        automate.createState(10, 10, true, false);
        assert(automate.getStatesList().size() == 2);
        State secondState =  automate.getStatesList().get(1);
        assert(secondState.numero == 1);
        assert(secondState.isInitial);

        // Final state
        automate.createState(10, 10, false, true);
        assert(automate.getStatesList().size() == 3);
        assert(automate.getStatesList().get(2).numero == 2);
        assert(automate.getStatesList().get(2).isFinal);
    }

}
