package peen.jornsound.generator;

import peen.jornsound.function.Function;

public class Clip implements Generator {
	private static final double EPSILON = 0.0000001;
	private static final double MAX_FREQUENCY_PER_SECOND = 1;
	private Function function;
	private double goalFrequency = 1;
	private double goalBasePhase;
	private double frequency = 1;
	private double phase;
	private double totalTime;

	public Clip(Function function) {
		this.function = function;
	}

	public double generate(double timeStep) {
		double goalPhase = goalBasePhase + goalFrequency * totalTime;
		goalPhase -= Math.floor(goalPhase);

		double df = goalFrequency - frequency;
		double dp = goalPhase - phase;
		
		if (Math.abs(df) < 1.1 * MAX_FREQUENCY_PER_SECOND * timeStep &&
				Math.abs(dp) < 1.1 * goalFrequency * timeStep) {
			// if snapping cannot be heard, snap
			frequency = goalFrequency;
			phase = goalPhase + frequency * timeStep;
		} else {
			// change the frequency at a constant rate
			double frequencyPerSecond = getFrequencyPerSecondOld(df, dp);
			double lastFrequency = frequency;
			frequency += frequencyPerSecond * timeStep;
			phase += (lastFrequency + frequency) / 2 * timeStep;
		}
		phase -= Math.floor(phase);
		totalTime += timeStep;
		
		return function.get(phase);
	}
	
	public double getFrequencyPerSecondSlowDown(double df, double dp) {
		double result = getFrequencyPerSecondOld(df, dp);
		double sdf = 5 * df;
		double sdp = 500 * (dp < 0.5 ? dp : dp - 1);
		double off = Math.sqrt(sdf * sdf + sdp * sdp);
		result *= Math.min(1, off);
		return result;
	}

	public static double getFrequencyPerSecondOld(double df, double dp) {
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
		this.goalBasePhase = phase - Math.floor(phase);
	}

	public double getGoalFrequency() {
		return goalFrequency;
	}

	public double getGoalPhase() {
		return goalBasePhase;
	}

	public double getFrequency() {
		return frequency;
	}

	public double getBasePhase() {
		double basePhase = phase - frequency * totalTime;
		return basePhase - Math.floor(basePhase);
	}
}
