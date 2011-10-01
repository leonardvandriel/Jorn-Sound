package peen.jornsound.phaser;

public interface Phaser {
	double step(double timeStep);
	public void setPhase(double phase);
	public void setFrequency(double frequency);
}
