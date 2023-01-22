package EditeurAutomates.Model;

public class State {
	protected int numero;
	protected int x;
	protected int y;
	protected boolean isInitial = false;
	protected boolean isFinal = false;

	protected State(int numero, int x, int y, boolean isInitial, boolean isFinal){
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.isInitial = isInitial;
		this.isFinal = isFinal;
	}

	@Override
	public String toString() {
		return "State{" +
				"numero=" + numero +
				", x=" + x +
				", y=" + y +
				", isInitial=" + isInitial +
				", isFinal=" + isFinal +
				'}';
	}
}
