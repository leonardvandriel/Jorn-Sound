package peen.jornsound.player;

import java.util.LinkedList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import peen.jornsound.generator.Generator;
import peen.jornsound.graphics.SleepListener;

public class Player {
	private Generator generator;
	private volatile boolean stop = true;
	private List<SleepListener> listenerList = new LinkedList<SleepListener>();

	public Player(Generator generators) {
		this.generator = generators;
	}

	public void start() {
		new Thread(new Runnable() {
			public void run() {
				try {
					play();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private void play() throws Exception {
		AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
		line.open(format);
		line.start();
		Quantizer q = new Quantizer();
		byte[] buffer = new byte[256];
		stop = false;
		while (!stop) {
			int availableForPlay = line.getBufferSize() - line.available();
			while (availableForPlay > 40000) {
				for (SleepListener listener : listenerList) {
					listener.onSleep();
				}
				Thread.sleep(1);
				availableForPlay = line.getBufferSize() - line.available();
			}
			q.quantize(generator, buffer);
			line.write(buffer, 0, buffer.length);
		}
		line.drain();
		line.stop();
	}

	public void stop() {
		stop = true;
	}

	public void addSleepListener(SleepListener listener) {
		listenerList.add(listener);
	}
	
	public boolean isPlaying() {
		return !stop;
	}
}
