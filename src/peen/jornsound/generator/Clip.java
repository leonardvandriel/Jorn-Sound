package peen.jornsound.generator;

import peen.jornsound.function.Function;

public class Clip implements Generator {
	private static final double epsilon = 1e-10;
	private static final double maxFrequencyPerSecond = 4;
	private static final double antiFrequencyPerSecond = 1;
	private Function function;
	private double goalFrequency = 1;
	private double goalBasePhase;
	private double frequency = 1;
	private double phase;
	private double totalTime;

	public Clip(Function function) {
		this.function = function;
	}

	static int i = 0;

	public double generate(double timeStep) {
		double goalPhase = goalBasePhase + goalFrequency * totalTime;
		goalPhase -= Math.floor(goalPhase);

		double df = goalFrequency - frequency;
		double dp = goalPhase - phase;
		dp -= Math.floor(dp);

		if (Math.abs(df) < 1.1 * maxFrequencyPerSecond * timeStep
				&& Math.abs(dp < 0.5 ? dp : dp - 1) < 1.1 * goalFrequency * timeStep) {
//			if (i++ % 1000 == 0)
//				System.out.println("S" + df + " " + dp);
//			 if snapping cannot be heard, snap
			frequency = goalFrequency;
			phase = goalPhase + frequency * timeStep;
		} else {
//			if (i++ % 1000 == 0)
//				System.out.println(" " + df + " " + dp);
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

	public double getFrequencyPerSecondSymmetric(double df, double dp) {
		if (df < 0) {
			return -getFrequencyPerSecond(-df, Math.ceil(dp) - dp);
		}
		return getFrequencyPerSecond(df, dp);
	}

	public double getFrequencyPerSecond(double df, double dp) {
//		 return getFrequencyPerSecondLinearFloor(df, dp);
//		return getFrequencyPerSecondLinearRound(df, dp);
		return getFrequencyPerSecondDoubleLinear(df, dp);
	}

	public double getFrequencyPerSecondDoubleLinear(double df, double dp) {
//		double frequencyPerSecond = getFrequencyPerSecondLinearFloor(df, dp);
		double frequencyPerSecond = getFrequencyPerSecondLinearRound(df, dp);
		if (Math.abs(Math.abs(frequencyPerSecond) / maxFrequencyPerSecond - 1) > .1) {
			// if cannot get within 10% reach max frequency per second, we go
			// opposite direction
			return (df < 0 ? antiFrequencyPerSecond : -antiFrequencyPerSecond);
		}
		return frequencyPerSecond;
	}

	public double getFrequencyPerSecondLinearRound(double df, double dp) {
		double D = df * df / 2 / maxFrequencyPerSecond;
		double Z = Math.ceil(D + dp) - dp;
		if (Math.abs(Z) > epsilon) {
			return D / Z * maxFrequencyPerSecond;
		}
		return 0;
	}

	public static double getFrequencyPerSecondLinearFloor(double df, double dp) {
		double D = df * df / 2 / maxFrequencyPerSecond;
		double Z = Math.floor(D) + Math.ceil(dp) - dp;
		if (D - Z > 0.5) {
			Z += 1;
		} else if (Z - D > 0.5) {
			D += 1;
		}
		if (Math.abs(Z) > epsilon) {
			return D / Z * maxFrequencyPerSecond;
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
