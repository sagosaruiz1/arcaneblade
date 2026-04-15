package io.arcaneblade;

import io.inputs.*;
import static utilz.Constants.PlayerConstants.*;
import static utilz.Constants.Directions.*;

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

//	private BufferedImage img;
	private BufferedImage[][] animations;
	private int playerAction = IDLE;
	private int playerDir;
	private boolean moving = false;

	private int aniTick, aniIndex, aniSpeed = 15;

	public GamePanel() {

		mouseInputs = new MouseInputs(this);

//		importImg();
		loadAnimations();

		setPanelSize();
		addKeyListener(new KeyboardInputs(this));
		addMouseListener(mouseInputs);
		addMouseMotionListener(mouseInputs);

	}

	private void loadAnimations() {
		animations = new BufferedImage[9][];

		animations[IDLE] = loadAnimRow("/entities/player/Idle.png", GetSpriteAmount(IDLE));
		animations[RUNNING] = loadAnimRow("/entities/player/Run.png", GetSpriteAmount(RUNNING));

//		if (aniIndex >= GetSpriteAmount(playerAction)) {
//
//		}

	}

	private BufferedImage[] loadAnimRow(String path, int frameCount) {
		BufferedImage[] tempRow = new BufferedImage[frameCount];
		try (InputStream is = getClass().getResourceAsStream(path)) {
			BufferedImage fullSheet = ImageIO.read(is);

			for (int i = 0; i < frameCount; i++) {
				tempRow[i] = fullSheet.getSubimage(i * 144, 0, 144, 144);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return tempRow;
	}

//	private void importImg() {
//		InputStream is = getClass().getResourceAsStream("/entities/player/Idle.png");
//
//		try {
//			img = ImageIO.read(is);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} finally {
//			try {
//				is.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

	private void setPanelSize() {
		Dimension size = new Dimension(1280, 800);
		setPreferredSize(size);

	}

	public void setDirection(int direction) {
		this.playerDir = direction;
		moving = true;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	private void updateAnimationTick() {

		aniTick++;
		if (aniTick >= aniSpeed) {
			aniTick = 0;
			aniIndex++;

			if (aniIndex >= GetSpriteAmount(playerAction))
				aniIndex = 0;
		}

	}

	private void setAnimation() {
		int startAni = playerAction;

		if (moving)
			playerAction = RUNNING;
		else
			playerAction = IDLE;
		
		if (startAni != playerAction) {
			aniTick = 0;
			aniIndex = 0;
		}

	}

	private void updatePos() {

		if (moving) {
			switch (playerDir) {
			case LEFT:
				xDelta -= 5;
				break;
			case UP:
				yDelta -= 5;
				break;
			case DOWN:
				yDelta += 5;
				break;
			case RIGHT:
				xDelta += 5;
				break;
			}
		}

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		updateAnimationTick();

		setAnimation();
		updatePos();

		g.drawImage(animations[playerAction][aniIndex], (int) xDelta, (int) yDelta, 432, 432, null);

	}

}
