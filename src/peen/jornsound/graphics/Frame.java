package peen.jornsound.graphics;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import peen.jornsound.function.BeatFunction;
import peen.jornsound.function.Function;
import peen.jornsound.function.ZeroFunction;
import peen.jornsound.generator.Clip;
import peen.jornsound.generator.Generator;
import peen.jornsound.generator.MixerGenerator;
import peen.jornsound.generator.StepLimiterGenerator;
import peen.jornsound.phaser.ChasingPhaser;
import peen.jornsound.phaser.MonotonePhaser;
import peen.jornsound.player.Player;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final int clipCount = 2;
	private Player player;
	private List<Clip> monoClips;
	private List<Clip> chaseClips;
	private Slider slider;
	private JLabel frequencyLabel;
	private JLabel phaseLabel;
	private JButton switchButton;
	private List<Generator> generators;

	public Frame() {
		init();
		initComponents();
		initWindow();
	}

	private void init() {
		monoClips = new LinkedList<Clip>();
		chaseClips = new LinkedList<Clip>();
		Function beat = new BeatFunction();
		Function silent = new ZeroFunction();
		generators = new LinkedList<Generator>();
		for (int i = 0; i < clipCount; i++) {
			// create constant frequency clip
			MonotonePhaser monoPhaser = new MonotonePhaser(1 + i, .1 * (1 + i));
			Clip monoClip = new Clip(silent, monoPhaser);
			monoClips.add(monoClip);
			generators.add(new StepLimiterGenerator(monoClip, .1));
			// create chasing clip
			ChasingPhaser chasePhaser = new ChasingPhaser(monoClip);
			Clip chaseClip = new Clip(beat, chasePhaser);
			chaseClips.add(chaseClip);
			generators.add(new StepLimiterGenerator(chaseClip, .1));
		}
		player = new Player(new MixerGenerator(generators));
	}

	private void initWindow() {
		setSize(400, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void initComponents() {
		JPanel contentPanel = new JPanel(new BorderLayout());
		add(getLeftPanel(), BorderLayout.WEST);
		slider = new Slider(new Point(.1, 0), new Point(10, 1), monoClips, chaseClips);
		generators.add(slider);
		player.addSleepListener(new SleepListener() {
			public void onSleep() {
				slider.repaint();
			}
		});
		contentPanel.add(slider, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
	}

	private JPanel getLeftPanel() {
		JPanel result = new JPanel(new GridLayout(0, 1));
		switchButton = new JButton("Start");
		switchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (player.isPlaying()) {
					switchButton.setText("Start");
					player.stop();
				} else {
					switchButton.setText("Stop");
					player.start();
				}
			}
		});
		result.add(switchButton);
		frequencyLabel = new JLabel();
		result.add(frequencyLabel);
		phaseLabel = new JLabel();
		result.add(phaseLabel);
		return result;
	}
}
