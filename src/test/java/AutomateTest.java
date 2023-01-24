import EditeurAutomates.Model.Automate;
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
    }

}
