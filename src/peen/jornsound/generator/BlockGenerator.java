package peen.jornsound.generator;

public class BlockGenerator implements Generator {
	private double frequency;
	private double time;

	public BlockGenerator(double frequency) {
		this.frequency = frequency;
	}

	public double generate(double timeStep) {
		time += timeStep;
		double value = time * frequency;
		value = (value - Math.floor(value)) > 0.5 ? 1 : -1;
		return (float) value;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
}
