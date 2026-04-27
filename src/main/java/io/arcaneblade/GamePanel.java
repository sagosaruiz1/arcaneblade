package io.arcaneblade;

import io.inputs.*;
import static utilz.Constants.PlayerConstants.*;
import static utilz.Constants.Directions.*;
import static io.arcaneblade.Game.GAME_WIDTH;
import static io.arcaneblade.Game.GAME_HEIGHT;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class GamePanel extends JPanel {

	private MouseInputs mouseInputs;
	private Game game;

	public GamePanel(Game game) {

		mouseInputs = new MouseInputs(this);
		this.game = game;

		setPanelSize();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);

	}

	private void setPanelSize() {
		Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(size);
		System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);

	}

	public void updateGame() {

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		game.getPlayer().render(g);
	}

	public Game getGame() {
		return game;
	}

}
