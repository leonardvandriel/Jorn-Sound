package peen.jornsound.generator;

public class StepLimiterGenerator implements Generator {
	private Generator generator;
	private double maxStep;
	private double lastValue;

	public StepLimiterGenerator(Generator generator, double maxStep) {
		this.generator = generator;
		this.maxStep = maxStep;
	}

	public double generate(double timeStep) {
		double result = generator.generate(timeStep);
		double diff = result - lastValue;
		if (diff > maxStep) {
			result = lastValue + maxStep;
		} else if (diff < -maxStep) {
			result = lastValue - maxStep;
		}
		lastValue = result;
		return result;
	}
}
