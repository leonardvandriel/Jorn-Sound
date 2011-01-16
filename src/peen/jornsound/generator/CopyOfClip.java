package peen.jornsound.generator;

import peen.jornsound.function.Function;

public class CopyOfClip implements Generator {
	private static final double EPSILON = 0.01;
	private static final double MAX_FREQUENCY_PER_SECOND = 50;
	private Function function;
	private double goalFrequency = 1;
	private double goalPhaseStart;
	private double frequency = 1;
	private double phase;
	private double totalTime;

	public CopyOfClip(Function function) {
		this.function = function;
	}

	public double generate(double timeStep) {
		totalTime += timeStep;

		double goalPhase = goalPhaseStart + goalFrequency * totalTime;
		goalPhase -= Math.floor(goalPhase);
		// double freqPerSecond = getFrequencyPerSecond(frequency, phase,
		// goalFrequency, goalPhase);
		// frequency += freqPerSecond * timeStep;
		// phase += frequency * timeStep;
		frequency = 0.999 * frequency + 0.001 * goalFrequency;
		phase = 0.999 * phase + 0.001 * goalPhase;
		phase -= Math.floor(phase);
		return function.get(phase);
	}

	public static double getFrequencyPerSecond(double f0, double p0, double f1, double p1) {
		double sign = f1 < f0 ? -1 : 1;
		double dff = (f1 - f0) * (f1 - f0) / 2;
		double dpx = dff / MAX_FREQUENCY_PER_SECOND;
		double dp = sign * (p0 - p1);
		dp -= Math.floor(dp);
		double dpn = Math.floor(dpx) + dp;
		if (dpn < dpx) {
			dpn += 1;
		}
		double a = 0;
		if (Math.abs(dpn) > EPSILON) {
			a = dff / dpn;
		}
		return sign * a;
	}

	public void setGoalFrequency(double frequency) {
		this.goalFrequency = frequency;
	}

	public void setGoalPhase(double phase) {
		this.goalPhaseStart = phase;
	}

	public double getGoalFrequency() {
		return goalFrequency;
	}

	public double getGoalPhase() {
		return goalPhaseStart;
	}

	public double getFrequency() {
		return frequency;
	}

	public double getPhase() {
		double phaseStart = phase - frequency * totalTime;
		return phaseStart - Math.floor(phaseStart);
	}
}
