package peen.jornsound.main;

import java.util.LinkedList;
import java.util.List;

import peen.jornsound.generator.Generator;
import peen.jornsound.generator.MixerGenerator;
import peen.jornsound.generator.SineGenerator;
import peen.jornsound.generator.StepLimiterGenerator;
import peen.jornsound.player.Player;

public class Test {
	public static void main(String[] args) throws Throwable {
		List<Generator> generators = new LinkedList<Generator>();
		generators.add(new SineGenerator(100));
		Player player = new Player(new StepLimiterGenerator(new MixerGenerator(generators), .1));
		player.start();
		Thread.sleep(5000);
		player.stop();
	}
}
