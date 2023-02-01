package EditeurAutomates.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Destinations extends ArrayList<Integer> {

	@Override
	public boolean add(Integer to){
		if (isInDestinations(to)) return false;

		super.add(to);
		return true;
	}

	public void removeDestination(Integer to){
		Integer temp;
		for (Iterator<Integer> it = this.iterator(); it.hasNext(); ) {
			temp = it.next();
			if (Objects.equals(temp, to)) it.remove();
		}
	}

	public boolean isInDestinations(Integer i){
		for(Integer each_i : this) if (Objects.equals(each_i, i)) return true;
		return false;
	}

}
