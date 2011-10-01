package peen.jornsound.function;

public class BeatFunction implements Function {
	public double get(double f) {
		return Math.sin(f * (1 - 0.5 * f) * 800) * (1 - f);
	}
}
