package peen.jornsound.phaser;


public class MonotonePhaser implements Phaser {
	private double phase;
	private double frequency;
	
	public MonotonePhaser(double frequency, double phase) {
		this.frequency = frequency;
		this.phase = phase;
	}
	
	public double step(double timeStep) {
		phase += frequency * timeStep;
		phase -= Math.floor(phase);
		return phase;
	}
	
	public void setPhase(double phase) {
		this.phase = phase;
	}
	
	public void setFrequency(double frequency) {
		this.frequency = frequency;
	}
}
