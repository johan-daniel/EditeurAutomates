package EditeurAutomates.Model;

public class State {
	public int numero;
	public int x;
	public int y;
	public boolean isInitial;
	public boolean isFinal;

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
