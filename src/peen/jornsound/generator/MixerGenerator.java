package peen.jornsound.generator;

import java.util.List;

public class MixerGenerator implements Generator {
	private List<Generator> generators;

	public MixerGenerator(List<Generator> generators) {
		this.generators = generators;
	}

	public double generate(double timeStep) {
		double sum = 0;
		for (Generator generator : generators) {
			sum += generator.generate(timeStep);
		}
		return sum;
	}
}
