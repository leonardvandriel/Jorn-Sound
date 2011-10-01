package peen.jornsound.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import peen.jornsound.generator.Clip;
import peen.jornsound.generator.Generator;

public class Slider extends JPanel implements Generator {
	private static final long serialVersionUID = 1L;
	private static final double snapDistance = 10;
	private List<Color> colors;
	private Point min;
	private Point max;
	private List<Clip> controlClips;
	private List<Clip> drawClips;
	private double totalTime;

	public Slider(Point min, Point max, List<Clip> controlClips, List<Clip> otherClips) {
		this.min = min;
		this.max = max;
		this.controlClips = controlClips;
		drawClips = new ArrayList<Clip>();
		drawClips.addAll(controlClips);
		drawClips.addAll(otherClips);
		colors = new ArrayList<Color>(drawClips.size());
		for (int i = 0; i < drawClips.size(); i++) {
			colors.add(Color.getHSBColor(i / (float) drawClips.size(), 1, 1));
		}
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				click(e);
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				click(e);
			}
		});
	}
	
	public double generate(double timeStep) {
		totalTime += timeStep;
		return 0;
	}

	private void click(MouseEvent e) {
		Clip clip = getClipWith(e);
		if (clip != null) {
			Point p = transformFromWindow(new Coord(e.getX(), e.getY()));
			clip.setFrequency(p.x);
			setNormalPhase(clip, p.y);
			snap(clip);
			repaint();
			fireStateChanged();
		}
	}

	private Clip getClipWith(MouseEvent e) {
		int index = 0;
		if (e.isShiftDown()) {
			index = 1;
		} else if (e.isControlDown()) {
			index = 2;
		} else if (e.isAltDown()) {
			index = 3;
		}
		if (index < controlClips.size()) {
			return controlClips.get(index);
		}
		return null;
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(Color.lightGray);
		g.fillRect(0, 0, getWidth(), getHeight());
		int i = 0;
		for (Clip clip : drawClips) {
			fillDot(g, new Point(clip.getFrequency(), getNormalPhase(clip)), colors.get(i).darker());
			i++;
		}
	}

	public void fillDot(Graphics g, Point pos, Color color) {
		g.setColor(color);
		Coord coord = transformToWindow(pos);
		g.fillOval(coord.x - 5, coord.y - 5, 7, 7);
	}

	private Point transformFromWindow(Coord c) {
		double x = min.x + c.x * (max.x - min.x) / getWidth();
		double y = min.y + c.y * (max.y - min.y) / getHeight();
		return new Point(x, y);
	}

	private Coord transformToWindow(Point p) {
		int x = (int) Math.round(getWidth() * (p.x - min.x) / (max.x - min.x));
		int y = (int) Math.round(getHeight() * (p.y - min.y) / (max.y - min.y));
		return new Coord(x, y);
	}

	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	protected void fireStateChanged() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				ChangeEvent changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	private void snap(Clip clip) {
		Coord coord = transformToWindow(new Point(clip.getFrequency(), getNormalPhase(clip)));
		double minx = snapDistance;
		double miny = snapDistance;
		Clip resultx = null;
		Clip resulty = null;
		for (Clip cl : controlClips) {
			if (!clip.equals(cl)) {
				Coord c = transformToWindow(new Point(cl.getFrequency(), getNormalPhase(cl)));
				double distancex = Math.abs(coord.x - c.x);
				double distancey = Math.abs(coord.y - c.y);
				if (minx > distancex) {
					minx = distancex;
					resultx = cl;
				}
				if (miny > distancey) {
					miny = distancey;
					resulty = cl;
				}
			}
		}
		if (resultx != null) {
			clip.setFrequency(resultx.getFrequency());
		}
		if (resulty != null) {
			setNormalPhase(clip, getNormalPhase(resulty));
		}
	}

	private double getNormalPhase(Clip clip) {
		double p = clip.getPhase() - clip.getFrequency() * totalTime;
		return p - Math.floor(p);
	}
	
	private void setNormalPhase(Clip clip, double normalPhase) {
		double p = normalPhase + clip.getFrequency() * totalTime;
		clip.setPhase(p - Math.floor(p));
	}
}
