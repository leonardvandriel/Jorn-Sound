package peen.jornsound.generator;

import peen.jornsound.function.Function;
import peen.jornsound.phaser.Chaseable;
import peen.jornsound.phaser.Phaser;

public class Clip implements Generator, Chaseable {
	private Function function;
	private Phaser phaser;
	private double frequency;
	private double phase;

	public Clip(Function function, Phaser phaser) {
		this.function = function;
		this.phaser = phaser;
	}

	public double generate(double timeStep) {
		double nextPhase = phaser.step(timeStep);
		if (timeStep > 0) {
			if (nextPhase >= phase) {
				frequency = (nextPhase - phase) / timeStep;
			} else {
				frequency = (nextPhase - phase + 1) / timeStep;
			}
		}
		phase = nextPhase;
		return function.get(nextPhase);
	}

	public double getPhase() {
		return phase;
	}
	
	public void setPhase(double phase) {
		this.phase = phase;
		phaser.setPhase(phase);
	}

	public double getFrequency() {
		return frequency;
	}
	
	public void setFrequency(double frequency) {
		this.frequency = frequency;
		phaser.setFrequency(frequency);
	}
}
