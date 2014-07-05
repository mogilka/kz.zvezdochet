package kz.zvezdochet.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

public class ImageViewer extends Panel {
	private static final long serialVersionUID = -6348281361167415767L;

	private Image im;
	private Dimension dimMinSize;

	public ImageViewer(Image img, Dimension dim) {
		im = img;
	    dimMinSize = dim;
	}

	public void paint(Graphics g) {
		g.drawImage(im, 0, 0, this);
	}

	public Dimension getPreferredSize() {
		return dimMinSize;
	}

	public Dimension getMinimumSize() {
		return dimMinSize;
	}

	public Dimension preferredSize() {
		return dimMinSize;
	}

	public Dimension minimumSize() {
		return dimMinSize;
	}
}