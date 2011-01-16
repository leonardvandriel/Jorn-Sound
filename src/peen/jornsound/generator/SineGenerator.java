package peen.jornsound.generator;

public class SineGenerator implements Generator {
	private double frequency;
	private double time;

	public SineGenerator(double frequency) {
		this.frequency = frequency;
	}

	public double generate(double timeStep) {
		time += timeStep;
		double value = Math.sin(time * frequency * 2 * Math.PI);
		return (float) value;
	}

	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
}
