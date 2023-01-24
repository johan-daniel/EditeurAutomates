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
		int size = 5; // { 0, 1, 2, 3, 4 }
		automate = new Automate();

		for(int i = 0; i < size; i++) {
			automate.createState(0,0);
		}
		assert(automate.getStatesList().size() == size);
		assert(automate.getTransitionMatrix().size() == size);

		int randIdx = ThreadLocalRandom.current().nextInt(0, size);
//		randIdx = size-1; // pour débugger ton histoire joj
		automate.deleteState(randIdx);

		State state;
		try {
			state = automate.getStatesList().get(randIdx);
			assert(state == null);
		} catch (IndexOutOfBoundsException ignored){
			assert(automate.getStatesList().size() == size - 1);
		}

		// @Joj TODO: débugger en dessous quand on getState si randIdx = size-1

//		assert(automate.getTransitionMatrix().get(randIdx) == null);
//
//		automate.createState(0,0);
//		state = automate.getStatesList().get(randIdx);
//		assert(state != null && state.numero == randIdx);
//		assert(automate.getTransitionMatrix().get(randIdx) != null);
	}

	@Test
	public void testCreateTransition() {
		int nbStates = 2; // States = { 0, 1 }
		automate = new Automate();

		for(int i=0; i < nbStates; i++) {
			automate.createState(0,0);
			assert(automate.getTransitionMatrix().get(i).size() == 0);
		}
		assert(automate.getAlphabet().size() == 0);

		automate.createTransition(0,1, "a", false);
		assert(automate.getAlphabet().size() == 1);
		assert(automate.getAlphabet().get(0) == 'a');

		automate.createTransition(1,0, "a", true);
		assert(automate.getAlphabet().size() == 2);
		assert(automate.getAlphabet().get(0) == 'a');
		assert(automate.getAlphabet().get(1) == null);
	}

}
