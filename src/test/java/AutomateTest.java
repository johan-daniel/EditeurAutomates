import EditeurAutomates.Model.Automate;
import EditeurAutomates.Model.State;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

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
        State state;

        // Regular state
        automate.createState(10, 10);
        assert(automate.getStatesList().size() == 1);
        assert(automate.getTransitionMatrix().size() == 1);
        state = automate.getStatesList().get(0);
        assert(state.numero == 0);

        // Initial state
        automate.createState(10, 10, true, false);
        assert(automate.getStatesList().size() == 2);
        assert(automate.getTransitionMatrix().size() == 2);
        state = automate.getStatesList().get(1);
        assert(state.numero == 1);
        assert(state.isInitial);

        // Final state
        automate.createState(10, 10, false, true);
        assert(automate.getStatesList().size() == 3);
        assert(automate.getTransitionMatrix().size() == 3);
        state = automate.getStatesList().get(2);
        assert(state.numero == 2);
        assert(state.isFinal);
    }

    @Test
    public void testDeleteState() {
        int size = 5;
        automate = new Automate();

        for(int i = 0; i < size; i++) {
            automate.createState(0,0);
        }
        assert(automate.getStatesList().size() == size);
        assert(automate.getTransitionMatrix().size() == size );

        int randIdx = ThreadLocalRandom.current().nextInt(0, size+1);
        automate.deleteState(randIdx);

        State state = automate.getStatesList().get(randIdx);
        assert(state == null);
        assert(automate.getTransitionMatrix().get(randIdx) == null);

        automate.createState(0,0);
        state = automate.getStatesList().get(randIdx);
        assert(state != null && state.numero == randIdx);
        assert(automate.getTransitionMatrix().get(randIdx) != null);
    }

}
