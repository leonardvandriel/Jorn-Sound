package peen.jornsound.generator;

public class TriangleGenerator implements Generator {
	private double frequency;
	private double time;

	public TriangleGenerator(double frequency) {
		this.frequency = frequency;
	}

	public double generate(double timeStep) {
		time += timeStep;
		double value = time * frequency;
		value = (value - Math.floor(value)) * 2 - 1;
		return (float) value;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
}
