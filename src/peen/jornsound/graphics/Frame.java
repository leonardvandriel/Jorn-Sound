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

import peen.jornsound.function.SomeFunction;
import peen.jornsound.generator.Clip;
import peen.jornsound.generator.Generator;
import peen.jornsound.generator.MixerGenerator;
import peen.jornsound.generator.StepLimiterGenerator;
import peen.jornsound.player.Player;

public class Frame extends JFrame {
	private static final long serialVersionUID = 1L;
	private Player player;
	private List<Clip> clips;
	private Slider slider;
	private JLabel frequencyLabel;
	private JLabel phaseLabel;

	public Frame() {
		init();
		initComponents();
		initWindow();
	}

	private void init() {
		clips = new LinkedList<Clip>();
		List<Generator> generators = new LinkedList<Generator>();
		clips.add(new Clip(new SomeFunction()));
		clips.add(new Clip(new SomeFunction()));
//		clips.add(new Clip(new SomeFunction()));
//		clips.add(new Clip(new SomeFunction()));
		for (Clip clip : clips) {
			generators.add(new StepLimiterGenerator(clip, .1));
		}
		// clips[1].setGoalFrequency(clips[1].getGoalFrequency() * 2);
		player = new Player(new MixerGenerator(generators));

	}

	private void initWindow() {
		setSize(400, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

//		KeyStroke selectAllStroke = KeyStroke.getKeyStroke(KeyEvent.VK_A, MatchUtils.getOsCtrlKeyIntMask());
//		KeyStroke copyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_C, MatchUtils.getOsCtrlKeyIntMask());
//		getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(selectAllStroke, "SELECT_ALL");
//		getRootPane().getActionMap().put("SELECT_ALL", new AbstractAction() {
//			private static final long serialVersionUID = 1L;
//
//			public void actionPerformed(ActionEvent e) {
//				displayArea.selectAll();
//			}
//		});
	}

	private void initComponents() {
		JPanel contentPanel = new JPanel(new BorderLayout());
		add(getLeftPanel(), BorderLayout.WEST);
		slider = new Slider(new Point(.1, 0), new Point(10, 1), clips);
		player.addSleepListener(new SleepListener() {
			public void onSleep() {
				slider.repaint();
			}
		});
		contentPanel.add(slider, BorderLayout.CENTER);
		add(contentPanel, BorderLayout.CENTER);
	}

	@SuppressWarnings("unused")
	private float trim(float f) {
		return (int) (f * 100) / 100.f;
	}

	private JPanel getLeftPanel() {
		JPanel result = new JPanel(new GridLayout(0, 1));
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.start();
			}
		});
		result.add(startButton);
		JButton stopButton = new JButton("Stop");
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				player.stop();
			}
		});
		result.add(stopButton);
		frequencyLabel = new JLabel();
		result.add(frequencyLabel);
		phaseLabel = new JLabel();
		result.add(phaseLabel);
		return result;
	}
}
