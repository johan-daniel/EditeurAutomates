package EditeurAutomates.Model;

public class State {
	protected int numero;
	protected int x;
	protected int y;
	protected boolean isInitial = false;
	protected boolean isFinal = false;
	protected boolean acceptsEmptyWord = false;

	protected State(int numero, int x, int y){
		this.numero = numero;
		this.x = x;
		this.y = y;
	}

	protected State(int numero, int x, int y, boolean isInitial, boolean isFinal){
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.isInitial = isInitial;
		this.isFinal = isFinal;
	}

	protected State(int numero, int x, int y, boolean isInitial, boolean isFinal, boolean acceptsEmptyWord){
		this.numero = numero;
		this.x = x;
		this.y = y;
		this.isInitial = isInitial;
		this.isFinal = isFinal;
		this.acceptsEmptyWord = acceptsEmptyWord;
	}

	@Override
	public String toString() {
		return "State{" +
				"numero=" + numero +
				", x=" + x +
				", y=" + y +
				", isInitial=" + isInitial +
				", isFinal=" + isFinal +
				", acceptsEmptyWord=" + acceptsEmptyWord +
				'}';
	}
}
