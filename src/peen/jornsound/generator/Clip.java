package peen.jornsound.generator;

import peen.jornsound.function.Function;

public class Clip implements Generator {
	private static final double epsilon = 1e-10;
	private static final double maxFrequencyPerSecond = 1;
	private static final double minFrequencyPerSecond = .5;
	private static final double antiFrequencyPerSecond = .1;
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
		dp -= Math.floor(dp);

		if (Math.abs(df) < 1.1 * maxFrequencyPerSecond * timeStep
				&& Math.abs(dp < 0.5 ? dp : dp - 1) < 1.1 * goalFrequency * timeStep) {
			// if snapping cannot be heard, snap
			frequency = goalFrequency;
			phase = goalPhase + frequency * timeStep;
		} else {
			// change the frequency at a constant rate
			double frequencyPerSecond = getFrequencyPerSecondSymmetric(df, dp);
			double lastFrequency = frequency;
			frequency += frequencyPerSecond * timeStep;
			phase += (lastFrequency + frequency) / 2 * timeStep;
		}
		phase -= Math.floor(phase);
		totalTime += timeStep;

		return function.get(phase);
	}

	public static double getFrequencyPerSecondSymmetric(double df, double dp) {
		if (df >= 0) {
			return getFrequencyPerSecond(df, dp);
		}
		return -getFrequencyPerSecond(-df, Math.ceil(dp) - dp);
	}

	public static double getFrequencyPerSecond(double df, double dp) {
		// return getFrequencyPerSecondLinearFloor(df, dp);
		// return getFrequencyPerSecondLinearCeil(df, dp);
		// return getFrequencyPerSecondDoubleLinear(df, dp);
		return getFrequencyPerSecondDoubleLinearTwoWay(df, dp);
	}

	public static double getFrequencyPerSecondDoubleLinearTwoWay(double df, double dp) {
		double coefficient = 2 * maxFrequencyPerSecond * antiFrequencyPerSecond / (maxFrequencyPerSecond + antiFrequencyPerSecond);
		double maxCrossDp = (df * df + coefficient - df * Math.sqrt(df * df + 2 * coefficient)) / 2 / coefficient;
		double defaultDp = dp + df * df / 2 / maxFrequencyPerSecond;
		defaultDp -= Math.floor(defaultDp);
		if (defaultDp > maxCrossDp) {
			// go for round, i.e. first move away, then linear towards base
			return -antiFrequencyPerSecond;
		}
		// go for cross, i.e. first cross and move through, then linear towards base
		return maxFrequencyPerSecond;
	}
	
	public static double getFrequencyPerSecondDoubleLinear(double df, double dp) {
		double frequencyPerSecond = getFrequencyPerSecondLinearCeil(df, dp);
		if (frequencyPerSecond < minFrequencyPerSecond) {
			// if cannot get within frequency-per-second range, we go
			// opposite direction
			return -antiFrequencyPerSecond;
		}
		return frequencyPerSecond;
	}

	public static double getFrequencyPerSecondLinearCeil(double df, double dp) {
		double minimumArea = df * df / 2 / maxFrequencyPerSecond;
		double feasibleArea = Math.ceil(minimumArea + dp) - dp;
		if (Math.abs(feasibleArea) > epsilon) {
			return minimumArea / feasibleArea * maxFrequencyPerSecond;
		}
		return 0;
	}

	public static double getFrequencyPerSecondLinearFloor(double df, double dp) {
		double minimumArea = df * df / 2 / maxFrequencyPerSecond;
		double feasibleArea = Math.floor(minimumArea) + Math.ceil(dp) - dp;
		if (minimumArea - feasibleArea > 0.5) {
			feasibleArea += 1;
		} else if (feasibleArea - minimumArea > 0.5) {
			minimumArea += 1;
		}
		if (Math.abs(feasibleArea) > epsilon) {
			return minimumArea / feasibleArea * maxFrequencyPerSecond;
		}
		return 0;
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
