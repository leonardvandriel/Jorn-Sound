package peen.jornsound.generator;

import peen.jornsound.function.Function;

public class Clip implements Generator {
	private static final double EPSILON = 0.0000001;
	private static final double MAX_FREQUENCY_PER_SECOND = 10;
	private Function function;
	private double goalFrequency = 1;
	private double goalPhase;
	private double frequency = 1;
	private double phase;
	private double totalTime;

	public Clip(Function function) {
		this.function = function;
	}

	private int i;

	public double generate(double timeStep) {

		double currentPhase = phase + frequency * totalTime;
		currentPhase -= Math.floor(currentPhase);
		double currentGoalPhase = goalPhase + goalFrequency * totalTime;
		currentGoalPhase -= Math.floor(currentGoalPhase);

		double df = goalFrequency - frequency;
		double dp = currentGoalPhase - currentPhase;

		double sdf = 5 * df;
		double sdp = 500 * (dp < 0.5 ? dp : dp - 1);
		double off = Math.sqrt(sdf * sdf + sdp * sdp);

		double freqPerSecond = getFrequencyPerSecond(df, dp);
		freqPerSecond *= Math.min(1, off);
		if (i++ % 10000 == 0) {
			System.out.println(String.format("%2.2f  %2.2f-%2.2f  %2.2f  %2.2f  %2.2f-%2.2f",
					Double.valueOf(totalTime), Double.valueOf(df), Double.valueOf(dp), Double.valueOf(off),
					Double.valueOf(freqPerSecond), Double.valueOf(frequency), Double.valueOf(currentPhase)));
		}
		frequency += freqPerSecond * timeStep;
		currentPhase += frequency * timeStep;
		totalTime += timeStep;

		phase = currentPhase - frequency * totalTime;
		phase -= Math.floor(phase);

		return function.get(currentPhase - Math.floor(currentPhase));
	}

	public static double getFrequencyPerSecond(double df, double dp) {
		double sign = df < 0 ? -1 : 1;
		double dpx = df * df / 2 / MAX_FREQUENCY_PER_SECOND;
		dp *= -sign;
		dp -= Math.floor(dp);
		double dpn = Math.floor(dpx) + dp;
		if (dpx - dpn > 0.5) {
			dpn += 1;
		} else if (dpn - dpx > 0.5) {
			dpx += 1;
		}
		double factor = 0;
		if (Math.abs(dpn) > EPSILON) {
			factor = dpx / dpn;
		}
		if (Math.abs(factor) < 0.8) {
			factor = -0.4;
		}
		return sign * factor * MAX_FREQUENCY_PER_SECOND;
	}

	public void setGoalFrequency(double frequency) {
		this.goalFrequency = frequency;
	}

	public void setGoalPhase(double phase) {
		this.goalPhase = phase - Math.floor(phase);
	}

	public double getGoalFrequency() {
		return goalFrequency;
	}

	public double getGoalPhase() {
		return goalPhase;
	}

	public double getFrequency() {
		return frequency;
	}

	public double getPhase() {
		return phase;
	}
}
