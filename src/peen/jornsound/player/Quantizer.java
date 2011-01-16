package peen.jornsound.player;

import peen.jornsound.generator.Generator;

public class Quantizer {
	private static final float GAIN = 0.8f;
	private static final int MAX = 0x8000;
	private static final float STEP_SIZE = 1.f / 44100;

	public void quantize(Generator generator, byte[] buffer, int length) {
		for (int i = 0; i < buffer.length; i += 2) {
			float value = (float) generator.generate(STEP_SIZE);
			if (value > 1) {
				value = 1;
			}
			if (value < -1) {
				value = -1;
			}
			int sample = Math.round(value * MAX * GAIN);
			buffer[i] = (byte) ((sample >>> 8) & 0xFF);
			buffer[i + 1] = (byte) (sample & 0xFF);
		}
	}
}
