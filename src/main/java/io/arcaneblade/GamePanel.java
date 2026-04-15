package io.arcaneblade;

import io.inputs.*;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private int xDelta = 100, yDelta = 100;
	private BufferedImage img, subImg;

	public GamePanel() {

		mouseInputs = new MouseInputs(this);

		importImg();

		setPanelSize();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);

	}

	private void importImg() {
		InputStream is = getClass().getResourceAsStream("/entities/player/Idle.png");

		try {
			img = ImageIO.read(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setPanelSize() {
		Dimension size = new Dimension(1280, 800);
		setPreferredSize(size);

	}

	public void changeXDelta(int value) {
		this.xDelta += value;

	}

	public void changeYDelta(int value) {
		this.yDelta += value;

	}

	public void setRectPos(int x, int y) {
		this.xDelta = x;
		this.yDelta = y;

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		subImg = img.getSubimage(1 * 144, 0, 144, 144);
		g.drawImage(subImg, (int) xDelta, (int) yDelta, 432, 432, null);

	}

}
