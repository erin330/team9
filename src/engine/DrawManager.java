package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;
import java.util.logging.Logger;
import engine.AchievementManager.Achievement;

import java.lang.Integer;

import entity.*;
import screen.GameScreen;
import screen.Screen;
import screen.SelectScreen;
import screen.SettingScreen;


/**
 * Manages screen drawing.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class DrawManager {

	/** Singleton instance of the class. */
	private static DrawManager instance;
	/** Current frame. */
	private static Frame frame;
	/** FileManager instance. */
	private static FileManager fileManager;
	/** Application logger. */
	private static Logger logger;
	/** Graphics context. */
	private static Graphics graphics;
	/** Buffer Graphics. */
	private static Graphics backBufferGraphics;
	/** Buffer image. */
	private static BufferedImage backBuffer;
	/** Normal sized font. */
	private static Font fontRegular;
	/** Normal sized font properties. */
	private static FontMetrics fontRegularMetrics;
	/** Big sized font. */
	private static Font fontBig;
	/** Big sized font properties. */
	private static FontMetrics fontBigMetrics;

	private static Font fontRegularKr;
	private static FontMetrics fontRegularKrMetrics;
	private static Font fontBigKr;
	private static FontMetrics fontBigKrMetrics;

	private static Font fontRegularCn;
	private static FontMetrics fontRegularCnMetrics;
	private static Font fontBigCn;
	private static FontMetrics fontBigCnMetrics;

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, boolean[][]> spriteMap;

	/** Sprite types. */
	public static enum SpriteType {
		/** Player1 ship. */
		Ship,
		/** Player2 ship. */
		Ship2,
		/** Destroyed player ship. */
		ShipDestroyed,
		/** Player bullet. */
		Bullet,
		/** Enemy bullet. */
		EnemyBullet,
		/** Player bullet. */
		BiggerBullet,
		/** Enemy bullet. */
		BiggerEnemyBullet,
		/** First enemy ship - first form. */
		EnemyShipA1,
		/** First enemy ship - second form. */
		EnemyShipA2,
		/** Second enemy ship - first form. */
		EnemyShipB1,
		/** Second enemy ship - second form. */
		EnemyShipB2,
		/** Third enemy ship - first form. */
		EnemyShipC1,
		/** Third enemy ship - second form. */
		EnemyShipC2,
		/** Bonus ship. */
		EnemyShipSpecial,
		/** Destroyed enemy ship. */
		Explosion,
		Item,

		Life,

		AuxiliaryShips,

		BombShape,

		InvincibleShape,

		SpeedUpShape,

		AuxiliaryShape


	};

	/**
	 * Private constructor.
	 */
	private DrawManager() {
		fileManager = Core.getFileManager();
		logger = Core.getLogger();
		logger.info("Started loading resources.");

		try {
			spriteMap = new LinkedHashMap<SpriteType, boolean[][]>();

			spriteMap.put(SpriteType.Ship, new boolean[13][8]);
			spriteMap.put(SpriteType.Ship2, new boolean[13][8]);
			spriteMap.put(SpriteType.ShipDestroyed, new boolean[13][8]);
			spriteMap.put(SpriteType.Bullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyBullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyShipA1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipA2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[16][7]);
			spriteMap.put(SpriteType.Explosion, new boolean[13][7]);
			spriteMap.put(SpriteType.Item, new boolean[3][3]);
			spriteMap.put(SpriteType.Life, new boolean[7][7]);
			spriteMap.put(SpriteType.AuxiliaryShips, new boolean[12][8]);
			spriteMap.put(SpriteType.BombShape, new boolean[4][6]);
			spriteMap.put(SpriteType.InvincibleShape, new boolean[5][5]);
			spriteMap.put(SpriteType.SpeedUpShape, new boolean[5][4]);
			spriteMap.put(SpriteType.AuxiliaryShape, new boolean[5][4]);

			fileManager.loadSprite(spriteMap);

			fileManager.changeSprite(spriteMap,SpriteType.Bullet,0);
			fileManager.changeSprite(spriteMap,SpriteType.Ship,0);
			fileManager.changeSprite(spriteMap,SpriteType.Ship2,0);
			logger.info("Finished loading the sprites.");

			// Font loading.
			fontRegular = fileManager.loadFont(14f);
			fontBig = fileManager.loadFont(24f);
			fontRegularKr = fileManager.loadKrFont(16f);
			fontBigKr = fileManager.loadKrFont(28f);
			fontRegularCn = fileManager.loadCnFont(16f);
			fontBigCn = fileManager.loadCnFont(28f);
			logger.info("Finished loading the fonts.");

		} catch (IOException e) {
			logger.warning("Loading failed.");
		} catch (FontFormatException e) {
			logger.warning("Font formatting failed.");
		}
	}

	/**
	 * Returns shared instance of DrawManager.
	 *
	 * @return Shared instance of DrawManager.
	 */
	protected static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}

	/**
	 * Sets the frame to draw the image on.
	 *
	 * @param currentFrame
	 *            Frame to draw on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
	}

	/**
	 * First part of the drawing process. Initialize buffers, draws the
	 * background and prepares the images.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	public void initDrawing(final Screen screen) {
		backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight() + frame.getBottomHudHeight(),
				BufferedImage.TYPE_INT_RGB);

		graphics = frame.getGraphics();
		backBufferGraphics = backBuffer.getGraphics();

		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics
				.fillRect(0, 0, screen.getWidth(), screen.getHeight() + frame.getBottomHudHeight());

		fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
		fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);
		fontRegularKrMetrics = backBufferGraphics.getFontMetrics(fontRegularKr);
		fontBigKrMetrics = backBufferGraphics.getFontMetrics(fontBigKr);
		fontRegularCnMetrics = backBufferGraphics.getFontMetrics(fontRegularCn);
		fontBigCnMetrics = backBufferGraphics.getFontMetrics(fontBigCn);

		// drawBorders(screen);
		// drawGrid(screen);
	}

	/**
	 * Draws the completed drawing on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void completeDrawing(final Screen screen) {
		graphics.drawImage(backBuffer, frame.getInsets().left,
				frame.getInsets().top, frame);
	}

	/**
	 * Draws an entity, using the appropriate image.
	 *
	 * @param entity
	 *            Entity to be drawn.
	 * @param positionX
	 *            Coordinates for the left side of the image.
	 * @param positionY
	 *            Coordinates for the upper side of the image.
	 */
	public void drawEntity(final Entity entity, final int positionX,
						   final int positionY) {
		boolean[][] image = spriteMap.get(entity.getSpriteType());

		backBufferGraphics.setColor(entity.getColor());
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.drawRect(positionX + i * 2, positionY
							+ j * 2, 1, 1);
	}
	public void clearEntity(final Entity entity, final int positionX, final int positionY) {
		boolean[][] image = spriteMap.get(entity.getSpriteType());

		Color clearColor = Color.black;

		backBufferGraphics.setColor(clearColor);
		for (int i = 0; i < image.length; i++) {
			for (int j = 0; j < image[i].length; j++) {
				if (image[i][j]) {
					backBufferGraphics.fillRect(positionX + i * 2, positionY + j * 2, 1, 1);
				}
			}
		}
	}


	/**
	 * For debugging purpouses, draws the canvas borders.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawBorders(final Screen screen) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
		backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
		backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
				screen.getWidth() - 1, screen.getHeight() - 1);
		backBufferGraphics.drawLine(0, screen.getHeight() - 1,
				screen.getWidth() - 1, screen.getHeight() - 1);
	}

	/**
	 * For debugging purposes, draws a grid over the canvas.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawGrid(final Screen screen) {
		backBufferGraphics.setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 1; i += 2)
			backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
		for (int j = 0; j < screen.getWidth() - 1; j += 2)
			backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
	}

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Current score.
	 */
	public void drawScore(final Screen screen, final int score) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("%04d", score);
		backBufferGraphics.drawString(scoreString, screen.getWidth() - 60, 25);
	}

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param highScore
	 *            Current score.
	 */
	public void drawHighScore(final Screen screen, final int highScore) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("%04d", highScore);
		backBufferGraphics.drawString(scoreString, screen.getWidth() - 120, 25);
	}

	/**
	 * Draws number of remaining lives from player1 on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param lives
	 *            Current player1's lives.
	 */
	public void drawLives(final Screen screen, final int lives) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(lives), 20, 25);
		Ship dummyShip = new Ship(0, 0, Color.GREEN, SpriteType.Life, false);
		for (int i = 0; i < lives; i++)
			drawEntity(dummyShip, 40 + 30 * i, 13);
	}


	/**
	 * Draws number of remaining lives from player2 on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param lives2
	 *            Current player2's lives.
	 */
	public void drawLives2(final Screen screen, final int lives2) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(lives2), 160, 25);
		Ship dummyShip = new Ship(0, 0, Color.RED, SpriteType.Life, false);
		for (int i = 0; i < lives2; i++)
			drawEntity(dummyShip, 180 + 30 * i, 13);
	}



	/**
	 * Draws number of items currently in inventory on screen.
	 *
	 * @param ItemQ
	 * 		  	ItemQueue
	 * @param inventory
	 * 			Number of items in inventory
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawItems(final Screen screen, Item[] ItemQ, final int inventory) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
    
		backBufferGraphics.drawString(Integer.toString(inventory), 205, screen.getHeight() + 25);

		Ship bombItem = new Ship(0, 0, Color.red, SpriteType.BombShape, false);
		Ship invincibleItem = new Ship(0, 0, Color.yellow, SpriteType.InvincibleShape, false);
		Ship SpeedUpItem = new Ship(0, 0, Color.orange, SpriteType.SpeedUpShape, false);
		Ship SubPlaneItem = new Ship(0, 0, Color.green, SpriteType.AuxiliaryShape, false);

		for (int i = 0; i < inventory; i++) {
			if (ItemQ[i].getItemType() == Item.ItemType.BombItem) {
				drawEntity(bombItem, 100 + 35 * i, screen.getHeight() + 15);
			}
			else if (ItemQ[i].getItemType() == Item.ItemType.InvincibleItem) {
				drawEntity(invincibleItem, 100 + 35 * i, screen.getHeight() + 15);
			}
			else if (ItemQ[i].getItemType() == Item.ItemType.SpeedUpItem) {
				drawEntity(SpeedUpItem, 100 + 35 * i, screen.getHeight() + 15);
			}
			else if (ItemQ[i].getItemType() == Item.ItemType.SubPlaneItem) {
				drawEntity(SubPlaneItem, 100 + 35 * i, screen.getHeight() + 15);
			}
		}
	}

	public void drawItems2(final Screen screen, Item[] ItemQ, final int inventory) {


		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(inventory), 415, screen.getHeight() + 25);


		Ship bombItem = new Ship(0, 0, Color.red, SpriteType.BombShape, false);
		Ship invincibleItem = new Ship(0, 0, Color.yellow, SpriteType.InvincibleShape, false);
		Ship SpeedUpItem = new Ship(0, 0, Color.orange, SpriteType.SpeedUpShape, false);
		Ship SubPlaneItem = new Ship(0, 0, Color.green, SpriteType.AuxiliaryShape, false);
		for (int i = 0; i < inventory; i++) {
			if (ItemQ[i].getItemType() == Item.ItemType.BombItem) {

				drawEntity(bombItem, 310 + 35 * i, screen.getHeight() + 15);
			}
			else if (ItemQ[i].getItemType() == Item.ItemType.InvincibleItem) {
				drawEntity(invincibleItem, 310 + 35 * i, screen.getHeight() + 15);
			}
			else if (ItemQ[i].getItemType() == Item.ItemType.SpeedUpItem) {
				drawEntity(SpeedUpItem, 310 + 35 * i, screen.getHeight() + 15);
			}
			else if (ItemQ[i].getItemType() == Item.ItemType.SubPlaneItem) {
				drawEntity(SubPlaneItem, 310 + 35 * i, screen.getHeight() + 15);

			}
		}
	}


	/**
	 * Draws number of items currently in inventory on screen.
	 *
	 * @param magazine
	 * 		  	Number of remaining magazines
	 * @param bullet_count
	 * 			Number of bullets fired
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawAmmo(final Screen screen, final int magazine, final int bullet_count) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString("BUL: " + Integer.toString(10-bullet_count) + "/" + Integer.toString(magazine), 10, screen.getHeight() + 25);
	}
	public void drawAmmo2(final Screen screen, final int magazine2, final int bullet_count2) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString("BUL: " + Integer.toString(10-bullet_count2) + "/" + Integer.toString(magazine2), 224, screen.getHeight() + 25);
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionY
	 *            Y coordinate of the line.
	 */
	public void drawHorizontalLine(final Screen screen, final int positionY, Color color) {
		backBufferGraphics.setColor(color);
		backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
		backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
				positionY + 1);
	}

	/**
	 * Lang Select
	 */

	public void drawLangDesc(final Screen screen) {
		String langDescString =
				"Language";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, langDescString,
				screen.getHeight() /2 - fontRegularMetrics.getHeight()*3/2-fontRegularMetrics.getHeight()*2);
	}

	public void drawLangMenu(final Screen screen, final int option) {
		String enString = "English";
		String krString = "한국어";
		String cnString = "中国語";

		if (option == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, enString,
				screen.getHeight() / 3 * 2 -fontRegularMetrics.getHeight()*3-fontRegularMetrics.getHeight()*2);
		if (option == 1)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularStringKr(screen, krString, screen.getHeight()
				/ 3 * 2 + fontRegularKrMetrics.getHeight()-fontRegularKrMetrics.getHeight()*2-fontRegularKrMetrics.getHeight()*2);

		if (option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularStringCn(screen, cnString, screen.getHeight() / 3
				* 2 + fontRegularCnMetrics.getHeight() * 3-fontRegularCnMetrics.getHeight()*2-fontRegularCnMetrics.getHeight()*2);
	}

	/**
	 * Draws game title.x
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawTitle(final Screen screen) {
		String titleString;
		String instructionsString;
		if (Lang.lang == 2) {
			titleString = "侵略者";
			instructionsString =
					"用w+s或箭头键选择,用空格键确认";
			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringCn(screen, instructionsString,
					screen.getHeight() /2 - fontRegularCnMetrics.getHeight()*3/2-fontRegularCnMetrics.getHeight()*2);

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringKr(screen, titleString, screen.getHeight() / 3-fontRegularKrMetrics.getHeight()*2);
		}
		else if (Lang.lang == 1) {
			titleString = "인베이더";
			instructionsString =
					"w+s나 화살표로 선택하고, 스페이스 바로 선택하세요";
			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringKr(screen, instructionsString,
					screen.getHeight() /2 - fontRegularKrMetrics.getHeight()*3/2-fontRegularKrMetrics.getHeight()*2);

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringKr(screen, titleString, screen.getHeight() / 3-fontRegularKrMetrics.getHeight()*2);
		}
		else {
			titleString = "Invaders";
			instructionsString =
					"select with w+s / arrows, confirm with space";
			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularString(screen, instructionsString,
					screen.getHeight() /2 - fontRegularMetrics.getHeight()*3/2-fontRegularMetrics.getHeight()*2);

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigString(screen, titleString, screen.getHeight() / 3-fontRegularMetrics.getHeight()*2);
		}
	}

	/**
	 * Draws main menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawMenu(final Screen screen, final int option) {
		String playString;
		String highScoresString;
		String shopString;
		String settingString;
		String exitString;
		String achievementString;

		if (Lang.lang == 2) {
			playString = "玩儿";
			highScoresString = "高分";
			shopString = "商店";
			settingString = "设置";
			exitString = "出口";
			achievementString = "业绩";

			if (option == 2)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, playString,
					screen.getHeight() / 3 * 2 -fontRegularCnMetrics.getHeight()*3-fontRegularCnMetrics.getHeight()*2);
			if (option == 3)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, highScoresString, screen.getHeight()
					/ 3 * 2 + fontRegularCnMetrics.getHeight()-fontRegularCnMetrics.getHeight()*2-fontRegularCnMetrics.getHeight()*2);

			if (option == 4)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, shopString, screen.getHeight() / 3
					* 2 + fontRegularCnMetrics.getHeight() * 3-fontRegularCnMetrics.getHeight()*2-fontRegularCnMetrics.getHeight()*2);

			if (option == 5)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, settingString, screen.getHeight() / 3
					* 2 + fontRegularCnMetrics.getHeight() * 3-fontRegularCnMetrics.getHeight()*2);
			if (option == 6)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, achievementString, screen.getHeight() / 3
					* 2 + fontRegularCnMetrics.getHeight() * 5-fontRegularCnMetrics.getHeight()*2);
			if (option == 0)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, exitString, screen.getHeight() / 3
					* 2 + fontRegularCnMetrics.getHeight() * 7-fontRegularCnMetrics.getHeight()*2);
		}
		else if (Lang.lang == 1) {
			playString = "플레이";
			highScoresString = "하이스코어";
			shopString = "상점";
			settingString = "설정";
			exitString = "나가기";
			achievementString = "업적";

			if (option == 2)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, playString,
					screen.getHeight() / 3 * 2 -fontRegularKrMetrics.getHeight()*3-fontRegularKrMetrics.getHeight()*2);
			if (option == 3)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, highScoresString, screen.getHeight()
					/ 3 * 2 + fontRegularKrMetrics.getHeight()-fontRegularKrMetrics.getHeight()*2-fontRegularKrMetrics.getHeight()*2);

			if (option == 4)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, shopString, screen.getHeight() / 3
					* 2 + fontRegularKrMetrics.getHeight() * 3-fontRegularKrMetrics.getHeight()*2-fontRegularKrMetrics.getHeight()*2);

			if (option == 5)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, settingString, screen.getHeight() / 3
					* 2 + fontRegularKrMetrics.getHeight() * 3-fontRegularKrMetrics.getHeight()*2);
			if (option == 6)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, achievementString, screen.getHeight() / 3
					* 2 + fontRegularKrMetrics.getHeight() * 5-fontRegularKrMetrics.getHeight()*2);
			if (option == 0)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, exitString, screen.getHeight() / 3
					* 2 + fontRegularKrMetrics.getHeight() * 7-fontRegularKrMetrics.getHeight()*2);
		}
		else {
			playString = "Play";
			highScoresString = "High scores";
			shopString = "Shop";
			settingString = "Setting";
			exitString = "exit";
			achievementString = "Achievements";

			if (option == 2)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, playString,
					screen.getHeight() / 3 * 2 -fontRegularMetrics.getHeight()*3-fontRegularMetrics.getHeight()*2);
			if (option == 3)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, highScoresString, screen.getHeight()
					/ 3 * 2 + fontRegularMetrics.getHeight()-fontRegularMetrics.getHeight()*2-fontRegularMetrics.getHeight()*2);

			if (option == 4)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, shopString, screen.getHeight() / 3
					* 2 + fontRegularMetrics.getHeight() * 3-fontRegularMetrics.getHeight()*2-fontRegularMetrics.getHeight()*2);

			if (option == 5)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, settingString, screen.getHeight() / 3
					* 2 + fontRegularMetrics.getHeight() * 3-fontRegularMetrics.getHeight()*2);
			if (option == 6)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, achievementString, screen.getHeight() / 3
					* 2 + fontRegularMetrics.getHeight() * 5-fontRegularMetrics.getHeight()*2);
			if (option == 0)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, exitString, screen.getHeight() / 3
					* 2 + fontRegularMetrics.getHeight() * 7-fontRegularMetrics.getHeight()*2);
		}
	}

	/**
	 * Draws game results for 1P mode.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Score obtained.
	 * @param livesRemaining1
	 *            1p's lives remaining when finished.
	 * @param shipsDestroyed
	 *            Total ships destroyed.
	 * @param accuracy
	 *            Total accuracy.
	 *
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawResults(final Screen screen, final int score,
							final int livesRemaining1, final int shipsDestroyed,
							final float accuracy, final boolean isNewRecord) {
		String scoreString;
		String acquiredCoins;
		String lives1RemainingString;
		String shipsDestroyedString;
		String accuracyString;

		int height;

		if (Lang.lang == 2) {
			scoreString = String.format("记分 %04d", score);
			acquiredCoins = "奖金硬币 " + score/10;
			lives1RemainingString = "剩余寿命 " + livesRemaining1;
			shipsDestroyedString = "被摧毁的敌人 " + shipsDestroyed;
			accuracyString = String
					.format("精度 %.2f%%", accuracy * 100);

			height = isNewRecord ? 4 : 2;

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, scoreString, screen.getHeight()
					/ height);
			drawCenteredRegularStringCn(screen, acquiredCoins,
					screen.getHeight() / height + fontRegularCnMetrics.getHeight()
							* 3);
			drawCenteredRegularStringCn(screen, lives1RemainingString,
					screen.getHeight() / height + fontRegularCnMetrics.getHeight()
							* 4);
			drawCenteredRegularStringCn(screen, shipsDestroyedString,
					screen.getHeight() / height + fontRegularCnMetrics.getHeight()
							* 5);
			drawCenteredRegularStringCn(screen, accuracyString, screen.getHeight()
					/ height + fontRegularCnMetrics.getHeight() * 6);
		}
		else if (Lang.lang == 1) {
			scoreString = String.format("점수 %04d", score);
			acquiredCoins = "보너스 코인 " + score/10;
			lives1RemainingString = "남은 목숨 " + livesRemaining1;
			shipsDestroyedString = "파괴된 적의 수 " + shipsDestroyed;
			accuracyString = String
					.format("명중률 %.2f%%", accuracy * 100);

			height = isNewRecord ? 4 : 2;

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, scoreString, screen.getHeight()
					/ height);
			drawCenteredRegularStringKr(screen, acquiredCoins,
					screen.getHeight() / height + fontRegularKrMetrics.getHeight()
							* 3);
			drawCenteredRegularStringKr(screen, lives1RemainingString,
					screen.getHeight() / height + fontRegularKrMetrics.getHeight()
							* 4);
			drawCenteredRegularStringKr(screen, shipsDestroyedString,
					screen.getHeight() / height + fontRegularKrMetrics.getHeight()
							* 5);
			drawCenteredRegularStringKr(screen, accuracyString, screen.getHeight()
					/ height + fontRegularKrMetrics.getHeight() * 6);
		}
		else {
			scoreString = String.format("score %04d", score);
			acquiredCoins = "bonus-coins " + score/10;
			lives1RemainingString = "lives remaining " + livesRemaining1;
			shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
			accuracyString = String
					.format("accuracy %.2f%%", accuracy * 100);

			height = isNewRecord ? 4 : 2;

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, scoreString, screen.getHeight()
					/ height);
			drawCenteredRegularString(screen, acquiredCoins,
					screen.getHeight() / height + fontRegularMetrics.getHeight()
							* 3);
			drawCenteredRegularString(screen, lives1RemainingString,
					screen.getHeight() / height + fontRegularMetrics.getHeight()
							* 4);
			drawCenteredRegularString(screen, shipsDestroyedString,
					screen.getHeight() / height + fontRegularMetrics.getHeight()
							* 5);
			drawCenteredRegularString(screen, accuracyString, screen.getHeight()
					/ height + fontRegularMetrics.getHeight() * 6);
		}
	}

	/**
	 * Draws game results for 2P mode.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Score obtained.
	 * @param livesRemaining1
	 *            1p's lives remaining when finished.
	 * @param livesRemaining2
	 *            2p's lives remaining when finished.
	 * @param shipsDestroyed
	 *            Total ships destroyed.
	 * @param accuracy
	 *            1p's accuracy.
	 * @param accuracy2
	 * 			  2p's accuracy.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawResults(final Screen screen, final int score,
							final int livesRemaining1, final int livesRemaining2, final int shipsDestroyed,
							final float accuracy, final float accuracy2, final boolean isNewRecord) {
		String scoreString;
		String acquiredCoins;
		String lives1RemainingString;
		String lives2RemainingString;
		String shipsDestroyedString;
		String accuracyString;
		String accuracyString2;
		int height;

		if (Lang.lang == 2) {
			scoreString = String.format("记分 %04d", score);
			acquiredCoins = String.format("奖金硬币 %04d", score%10);
			lives1RemainingString = "1p的剩余寿命 " + livesRemaining1;
			lives2RemainingString = "2p的剩余寿命 " + livesRemaining2;
			shipsDestroyedString = "被摧毁的敌人 " + shipsDestroyed;
			accuracyString = String
					.format("1p的精度 %.2f%%", accuracy * 100);
			accuracyString2 = String
					.format("2p的精度 %.2f%%", accuracy2 * 100);

			height = isNewRecord ? 4 : 3;

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, scoreString, screen.getHeight()
					/ height);
			drawCenteredRegularStringCn(screen, acquiredCoins, screen.getHeight()
			);
			drawCenteredRegularStringCn(screen, lives1RemainingString,
					screen.getHeight() / height + fontRegularCnMetrics.getHeight()
							* 2);
			drawCenteredRegularStringCn(screen, lives2RemainingString,
					screen.getHeight() / height + fontRegularCnMetrics.getHeight()
							* 4);
			drawCenteredRegularStringCn(screen, shipsDestroyedString,
					screen.getHeight() / height + fontRegularCnMetrics.getHeight()
							* 6);
			drawCenteredRegularStringCn(screen, accuracyString, screen.getHeight()
					/ height + fontRegularCnMetrics.getHeight() * 8);
			drawCenteredRegularStringCn(screen, accuracyString2, screen.getHeight()
					/ height + fontRegularCnMetrics.getHeight() * 10);
		}
		else if (Lang.lang == 1) {
			scoreString = String.format("점수 %04d", score);
			acquiredCoins = String.format("보너스 코인 %04d", score%10);
			lives1RemainingString = "1p의 남은 목숨 " + livesRemaining1;
			lives2RemainingString = "2p의 남은 목숨 " + livesRemaining2;
			shipsDestroyedString = "파괴된 적의 수 " + shipsDestroyed;
			accuracyString = String
					.format("1p의 명중률 %.2f%%", accuracy * 100);
			accuracyString2 = String
					.format("2p의 명중률 %.2f%%", accuracy2 * 100);

			height = isNewRecord ? 4 : 3;

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, scoreString, screen.getHeight()
					/ height);
			drawCenteredRegularStringKr(screen, acquiredCoins, screen.getHeight()
			);
			drawCenteredRegularStringKr(screen, lives1RemainingString,
					screen.getHeight() / height + fontRegularKrMetrics.getHeight()
							* 2);
			drawCenteredRegularStringKr(screen, lives2RemainingString,
					screen.getHeight() / height + fontRegularKrMetrics.getHeight()
							* 4);
			drawCenteredRegularStringKr(screen, shipsDestroyedString,
					screen.getHeight() / height + fontRegularKrMetrics.getHeight()
							* 6);
			drawCenteredRegularStringKr(screen, accuracyString, screen.getHeight()
					/ height + fontRegularKrMetrics.getHeight() * 8);
			drawCenteredRegularStringKr(screen, accuracyString2, screen.getHeight()
					/ height + fontRegularKrMetrics.getHeight() * 10);
		}
		else {
			scoreString = String.format("score %04d", score);
			acquiredCoins = String.format("bonus-coins %04d", score%10);
			lives1RemainingString = "1p's lives remaining " + livesRemaining1;
			lives2RemainingString = "2p's lives remaining " + livesRemaining2;
			shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
			accuracyString = String
					.format("1p's accuracy %.2f%%", accuracy * 100);
			accuracyString2 = String
					.format("2p's accuracy %.2f%%", accuracy2 * 100);

			height = isNewRecord ? 4 : 3;

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, scoreString, screen.getHeight()
					/ height);
			drawCenteredRegularString(screen, acquiredCoins, screen.getHeight()
			);
			drawCenteredRegularString(screen, lives1RemainingString,
					screen.getHeight() / height + fontRegularMetrics.getHeight()
							* 2);
			drawCenteredRegularString(screen, lives2RemainingString,
					screen.getHeight() / height + fontRegularMetrics.getHeight()
							* 4);
			drawCenteredRegularString(screen, shipsDestroyedString,
					screen.getHeight() / height + fontRegularMetrics.getHeight()
							* 6);
			drawCenteredRegularString(screen, accuracyString, screen.getHeight()
					/ height + fontRegularMetrics.getHeight() * 8);
			drawCenteredRegularString(screen, accuracyString2, screen.getHeight()
					/ height + fontRegularMetrics.getHeight() * 10);
		}
	}

	/**
	 * Draws interactive characters for name input.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param name
	 *            Current name selected.
	 * @param nameCharSelected
	 *            Current character selected for modification.
	 */
	public void drawNameInput(final Screen screen, final char[] name,
							  final int nameCharSelected) {
		String newRecordString;
		String introduceNameString;

		if (Lang.lang == 2) {
			newRecordString = "新记录！";
			introduceNameString = "输入姓名:";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredRegularStringCn(screen, newRecordString, screen.getHeight()
					* 11 / 60);
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, introduceNameString,
					screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 12);
		} else if (Lang.lang == 1) {
			newRecordString = "신기록 갱신!";
			introduceNameString = "이름 입력:";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredRegularStringKr(screen, newRecordString, screen.getHeight()
					* 11 / 60);
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, introduceNameString,
					screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 12);
		}
		else {
			newRecordString = "New Record!";
			introduceNameString = "Introduce name:";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredRegularString(screen, newRecordString, screen.getHeight()
					* 11 / 60);
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, introduceNameString,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 12);
		}

		// 3 letters name.
		int positionX = screen.getWidth()
				/ 2
				- (fontRegularMetrics.getWidths()[name[0]]
				+ fontRegularMetrics.getWidths()[name[1]]
				+ fontRegularMetrics.getWidths()[name[2]]
				+ fontRegularMetrics.getWidths()[' ']) / 2;

		for (int i = 0; i < 3; i++) {
			if (i == nameCharSelected)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);

			positionX += fontRegularMetrics.getWidths()[name[i]] / 2;
			positionX = i == 0 ? positionX
					: positionX
					+ (fontRegularMetrics.getWidths()[name[i - 1]]
					+ fontRegularMetrics.getWidths()[' ']) / 2;

			backBufferGraphics.drawString(Character.toString(name[i]),
					positionX,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight()
							* 14);
		}
	}
	//Login Screen Name Input
	public void drawUsernameInput(final Screen screen, final char[] name,
							  final int nameCharSelected) {
		String introduceUsernameString;

		if (Lang.lang == 2) {
			introduceUsernameString = "名称:";

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, introduceUsernameString,
					screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 3);
		}
		else if (Lang.lang == 1) {
			introduceUsernameString = "닉네임:";

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, introduceUsernameString,
					screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 3);
		}
		else {
			introduceUsernameString = "Username:";

			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, introduceUsernameString,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 3);
		}

		// 3 letters name.
		int positionX = screen.getWidth()
				/ 2
				- (fontRegularMetrics.getWidths()[name[0]]
				+ fontRegularMetrics.getWidths()[name[1]]
				+ fontRegularMetrics.getWidths()[name[2]]
				+ fontRegularMetrics.getWidths()[' ']) / 2;

		for (int i = 0; i < 3; i++) {
			if (i == nameCharSelected)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);

			positionX += fontRegularMetrics.getWidths()[name[i]] / 2;
			positionX = i == 0 ? positionX
					: positionX
					+ (fontRegularMetrics.getWidths()[name[i - 1]]
					+ fontRegularMetrics.getWidths()[' ']) / 2;

			backBufferGraphics.drawString(Character.toString(name[i]),
					positionX,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight()
							* 6);
		}
	}

	// Item Shop's Item holder
	public void drawItemShopMenu(final Screen screen,final int selectedItem) {
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredItemInfo(screen, "", screen.getHeight() / 8);

		String[] itemNames = {"Speed", "Additional Health", "Shooting Faster"};
		int[] itemPrices = {10, 15, 20};

		int startX = screen.getWidth() / 10; // Starting X position for the first item
		int itemWidth = 100; // Width of each item box
		int spacing = 20; // Horizontal spacing
		int itemX1 = startX;
		int itemX2 = startX + itemWidth + spacing;
		int itemX3 = startX + 2 * (itemWidth + spacing);
		Ship SpeedUpItem = new Ship(0, 0, Color.orange, SpriteType.SpeedUpShape, false);
		Ship BulletItem = new Ship(0, 0, Color.orange, SpriteType.Bullet, false);

		drawEntity(new Ship(0, 0, Color.RED, SpriteType.Life, false),itemX2+45, screen.getHeight() / 2-5);
		drawEntity(SpeedUpItem, itemX1+45, screen.getHeight() / 2-5);
		drawEntity(BulletItem, itemX3+48, screen.getHeight() / 2-5);

		// Draw the first item

		if (selectedItem == 70) {
			backBufferGraphics.setColor(Color.GREEN);
		} else {
			backBufferGraphics.setColor(Color.LIGHT_GRAY);
		}
		backBufferGraphics.drawRect(itemX1, screen.getHeight() / 2 - 50, itemWidth, itemWidth);

		backBufferGraphics.setColor(Color.GRAY);
		String itemName1 = itemNames[0];
		int itemNameX1 = itemX1 + (itemWidth - backBufferGraphics.getFontMetrics().stringWidth(itemName1)) / 2;
		backBufferGraphics.drawString(itemName1, itemNameX1, screen.getHeight() / 2 + 70);

		backBufferGraphics.setColor(Color.GRAY);
		String priceText1 = "Price: $" + itemPrices[0];
		int priceX1 = itemX1 + (itemWidth - backBufferGraphics.getFontMetrics().stringWidth(priceText1)) / 2;
		backBufferGraphics.drawString(priceText1, priceX1, screen.getHeight() / 2 + 90);

		// Draw the second item

		if (selectedItem == 71) {
			backBufferGraphics.setColor(Color.GREEN);
		} else {
			backBufferGraphics.setColor(Color.LIGHT_GRAY);
		}
		backBufferGraphics.drawRect(itemX2, screen.getHeight() / 2 - 50, itemWidth, itemWidth);

		backBufferGraphics.setColor(Color.GRAY);
		String itemName2 = itemNames[1];
		int itemNameX2 = itemX2 + (itemWidth - backBufferGraphics.getFontMetrics().stringWidth(itemName2)) / 2;
		backBufferGraphics.drawString(itemName2, itemNameX2, screen.getHeight() / 2 + 70);

		backBufferGraphics.setColor(Color.GRAY);
		String priceText2 = "Price: $" + itemPrices[1];
		int priceX2 = itemX2 + (itemWidth - backBufferGraphics.getFontMetrics().stringWidth(priceText2)) / 2;
		backBufferGraphics.drawString(priceText2, priceX2, screen.getHeight() / 2 + 90);

		// Draw the third item

		if (selectedItem == 72) {
			backBufferGraphics.setColor(Color.GREEN);
		} else {
			backBufferGraphics.setColor(Color.LIGHT_GRAY);
		}
		backBufferGraphics.drawRect(itemX3, screen.getHeight() / 2 - 50, itemWidth, itemWidth);

		backBufferGraphics.setColor(Color.GRAY);
		String itemName3 = itemNames[2];
		int itemNameX3 = itemX3 + (itemWidth - backBufferGraphics.getFontMetrics().stringWidth(itemName3)) / 2;
		backBufferGraphics.drawString(itemName3, itemNameX3, screen.getHeight() / 2 + 70);

		backBufferGraphics.setColor(Color.GRAY);
		String priceText3 = "Price: $" + itemPrices[2];
		int priceX3 = itemX3 + (itemWidth - backBufferGraphics.getFontMetrics().stringWidth(priceText3)) / 2;
		backBufferGraphics.drawString(priceText3, priceX3, screen.getHeight() / 2 + 90);

		// The title/guide for the item shop:
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, "Item Shop", screen.getHeight() / 10);
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, "Buy Your Upgrades Here!", screen.getHeight() / 6);
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, "Press Space to Buy", screen.getHeight() / 4);
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, "*If Player has the item already, Can't buy.*", screen.getHeight() / 5);
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, "Press Esc to Go to Menu", screen.getHeight() / 1);
		backBufferGraphics.setColor(Color.GRAY);

		try{
			drawCenteredRegularString(screen, "Current credits : " + Core.getFileManager().getCurrentPlayer().getCurrency(), screen.getHeight() / 3);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}

	// Helper method to draw centered text
	private void drawCenteredItemInfo(Screen screen, String text, int yPosition) {
		int xPosition = (screen.getWidth() - backBufferGraphics.getFontMetrics().stringWidth(text)) / 2;
		backBufferGraphics.drawString(text, xPosition, yPosition);
	}

	/**
	 * Draws basic content of game over screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param acceptsInput
	 *            If the screen accepts input.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawGameOver(final Screen screen, final boolean acceptsInput,
							 final boolean isNewRecord) {
		String gameOverString;
		String continueOrExitString;
		int height = isNewRecord ? 4 : 3;

		if (Lang.lang == 2) {
			gameOverString = "比赛结束！";
			continueOrExitString =
					"按 Space 重新播放, Esc 退出";
			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringCn(screen, gameOverString, screen.getHeight()
					/ height - fontBigCnMetrics.getHeight() * 2);
			if (acceptsInput)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringCn(screen, continueOrExitString,
					screen.getHeight() / 2 + fontRegularCnMetrics.getHeight() * 10);
		}
		else if (Lang.lang == 1) {
			gameOverString = "게임 오버!";
			continueOrExitString =
					"다시 플레이하려면 스페이스 바를, 나가려면 Esc를 누르세요";
			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringKr(screen, gameOverString, screen.getHeight()
					/ height - fontBigKrMetrics.getHeight() * 2);
			if (acceptsInput)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringKr(screen, continueOrExitString,
					screen.getHeight() / 2 + fontRegularKrMetrics.getHeight() * 10);
		}
		else {
			gameOverString = "Game Over";
			continueOrExitString =
					"Press Space to play again, Escape to exit";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigString(screen, gameOverString, screen.getHeight()
					/ height - fontBigMetrics.getHeight() * 2);

			if (acceptsInput)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularString(screen, continueOrExitString,
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
		}
	}

	/**
	 * Draws high score screen title and instructions.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void
	drawSkinSelectionMenu(final Screen screen, final int skincode1p,final int skincode2p){
		String SkinString;
		String skin1p = "1P";
		String skin2p = "2P";
		Ship[] shipskin = new Ship[6];
		Ship[] shipskin2 = new Ship[6];

		if (Lang.lang == 2) {
			SkinString = "选择您的船舶设计！";
		}
		else if (Lang.lang == 1) {
			SkinString = "우주선의 디자인을 고르세요!";
		}
		else {
			SkinString = "Select Your Ship Design!";
		}

		if (SelectScreen.gameMode == 2) {
			backBufferGraphics.setColor(Color.white);
			backBufferGraphics.drawLine(screen.getWidth()/2, screen.getHeight()/5, screen.getWidth()/2, 450);
			backBufferGraphics.setFont(fontBig);
			backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString(skin1p, screen.getWidth() / 4 - fontRegularMetrics.stringWidth(skin1p) / 2 - 1, 130);
			backBufferGraphics.drawString(skin2p, 3 * screen.getWidth() / 4 - fontRegularMetrics.stringWidth(skin1p) / 2 - 2, 130);
			try {
				fileManager.changeSprite(spriteMap, SpriteType.Ship, 0);
			} catch (IOException e) {
				logger.warning("Loading failed.");
			}
			for (int i = 0; i < 6; i++) {
				Ship dummyShip = new Ship(0, 0, Color.GREEN, SpriteType.Ship, false);
				Ship dummyShip2 = new Ship(0, 0, Color.RED, SpriteType.Ship, true);
				shipskin[i] = dummyShip;
				shipskin2[i] = dummyShip2;
				// 예: ships[i] = new Ship(i * 50, 100, Color.GREEN, SpriteType.Ship, spriteData, false);
				drawEntity(shipskin[i], screen.getWidth() / 4 - 13, 172 + 50*i);
				drawEntity(shipskin2[i], 3*screen.getWidth() / 4 - 13, 172 + 50*i);
				if(i !=5) {
					try {
						fileManager.changeSprite(spriteMap, SpriteType.Ship, i+1);
					} catch (IOException e) {
						logger.warning("Loading failed.");
					}
				}
			}
			if (skincode1p == 0) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 165, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 165, 30, 30);

			}
			if (skincode1p == 1) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 215, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 215, 30, 30);
			}
			if (skincode1p == 2) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 265, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 265, 30, 30);
			}
			if (skincode1p == 3) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 315, 30, 30);

			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 315, 30, 30);
			}
			if (skincode1p == 4) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 365, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 365, 30, 30);
			}
			if (skincode1p == 5) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 415, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 4 - 15, 415, 30, 30);
			}
			if (skincode2p == 0) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 165, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 165, 30, 30);
			}
			if (skincode2p == 1) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 215, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 215, 30, 30);
			}
			if (skincode2p == 2) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 265, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 265, 30, 30);
			}
			if (skincode2p == 3) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 315, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 315, 30, 30);
			}
			if (skincode2p == 4) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 365, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 365, 30, 30);
			}
			if (skincode2p == 5) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 415, 30, 30);
			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(3 * screen.getWidth() / 4 - 15, 415, 30, 30);
			}
			try {
				fileManager.changeSprite(spriteMap, SpriteType.Ship, skincode1p);
			} catch (IOException e) {
				logger.warning("Loading failed.");
			}
			try {
				fileManager.changeSprite(spriteMap, SpriteType.Ship2, skincode2p);
			} catch (IOException e) {
				logger.warning("Loading failed.");
			}

		} else {
			try {
				fileManager.changeSprite(spriteMap, SpriteType.Ship, 0);
			} catch (IOException e) {
				logger.warning("Loading failed.");
			}
			for (int i = 0; i < 6; i++) {
				Ship dummyShip = new Ship(0, 0, Color.GREEN, SpriteType.Ship, false);
				shipskin[i] = dummyShip;
				// 예: ships[i] = new Ship(i * 50, 100, Color.GREEN, SpriteType.Ship, spriteData, false);
				drawEntity(shipskin[i], screen.getWidth() / 2 - 13, 172 + 50*i);
				if(i !=5) {
					try {
						fileManager.changeSprite(spriteMap, SpriteType.Ship, i+1);
					} catch (IOException e) {
						logger.warning("Loading failed.");
					}
				}
			}
			if (skincode1p == 0) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 165, 30, 30);

			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 165, 30, 30);
			}
			if (skincode1p == 1) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 215, 30, 30);

			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 215, 30, 30);
			}
			if (skincode1p == 2) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 265, 30, 30);

			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 265, 30, 30);
			}
			if (skincode1p == 3) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 315, 30, 30);

			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 315, 30, 30);
			}
			if (skincode1p == 4) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 365, 30, 30);

			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 365, 30, 30);
			}
			if (skincode1p == 5) {
				backBufferGraphics.setColor(Color.GREEN);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 415, 30, 30);

			} else {
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawRect(screen.getWidth() / 2 - 15, 415, 30, 30);
			}
			backBufferGraphics.setFont(fontBig);
			backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString(skin1p, screen.getWidth() / 2 - fontRegularMetrics.stringWidth(skin1p) / 2 - 1, 130);
			try {
				fileManager.changeSprite(spriteMap, SpriteType.Ship, skincode1p);
			} catch (IOException e) {
				logger.warning("Loading failed.");
			}
		}
			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigString(screen, SkinString, screen.getHeight() / 8);
		}


	public void drawHighScoreMenu(final Screen screen) {
		String highScoreString;
		String instructionsString;
		String gameMode_1;
		String gameMode_2;

		if (Lang.lang == 2) {
			highScoreString = "高分";
			instructionsString = "按Space返回";
			gameMode_1 = "1P_模式";
			gameMode_2 = "2P_模式";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringCn(screen, highScoreString, screen.getHeight() / 8);


			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringCn(screen, instructionsString,
					screen.getHeight() / 5);

			backBufferGraphics.setColor(Color.GRAY);
			drawLeftsideRegularStringCn(screen, gameMode_1,
					screen.getHeight()*4 / 15);

			backBufferGraphics.setColor(Color.GRAY);
			drawRightsideRegularStringCn(screen, gameMode_2,
					screen.getHeight()*4 / 15);
		}
		else if (Lang.lang == 1) {
			highScoreString = "하이 스코어";
			instructionsString = "돌아가려면 스페이스 바를 누르세요";
			gameMode_1 = "1P_모드";
			gameMode_2 = "2P_모드";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringKr(screen, highScoreString, screen.getHeight() / 8);


			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringKr(screen, instructionsString,
					screen.getHeight() / 5);

			backBufferGraphics.setColor(Color.GRAY);
			drawLeftsideRegularStringKr(screen, gameMode_1,
					screen.getHeight()*4 / 15);

			backBufferGraphics.setColor(Color.GRAY);
			drawRightsideRegularStringKr(screen, gameMode_2,
					screen.getHeight()*4 / 15);
		}
		else {
			highScoreString = "High Scores";
			instructionsString = "Press Space to return";
			gameMode_1 = "1P_Mode";
			gameMode_2 = "2P_Mode";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigString(screen, highScoreString, screen.getHeight() / 8);


			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularString(screen, instructionsString,
					screen.getHeight() / 5);

			backBufferGraphics.setColor(Color.GRAY);
			drawLeftsideRegularString(screen, gameMode_1,
					screen.getHeight()*4 / 15);

			backBufferGraphics.setColor(Color.GRAY);
			drawRightsideRegularString(screen, gameMode_2,
					screen.getHeight()*4 / 15);
		}
	}

	/**
	 * Draws high scores from 1p mode.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param highScores
	 *            List of high scores.
	 */
	public void drawHighScores_1p(final Screen screen,
							   final List<Score> highScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		String scoreString = "";

		for (Score score : highScores) {
			scoreString = String.format("%s        %04d", score.getName(),
					score.getScore());
			drawLeftsideRegularString(screen, scoreString, screen.getHeight()
					/ 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			i++;
		}
	}

	/**
	 * Draws high scores from 2p mode.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param highScores
	 *            List of high scores.
	 */
	public void drawHighScores_2p(final Screen screen,
								  final List<Score> highScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		String scoreString = "";

		for (Score score : highScores) {
			scoreString = String.format("%s        %04d", score.getName(),
					score.getScore());
			drawRightsideRegularString(screen, scoreString, screen.getHeight()
					/ 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			i++;
		}
	}

	/**
	 * Draws a centered string on regular font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	public void drawCenteredRegularStringKr(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegularKr);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularKrMetrics.stringWidth(string) / 2, height);
	}

	public void drawCenteredRegularStringCn(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegularCn);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularCnMetrics.stringWidth(string) / 2, height);
	}
	/**
	 * Draws a left sided string on regular font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawLeftsideRegularString(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth()*3 / 13
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	public void drawLeftsideRegularStringKr(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegularKr);
		backBufferGraphics.drawString(string, screen.getWidth()*3 / 13
				- fontRegularKrMetrics.stringWidth(string) / 2, height);
	}

	public void drawLeftsideRegularStringCn(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegularCn);
		backBufferGraphics.drawString(string, screen.getWidth()*3 / 13
				- fontRegularCnMetrics.stringWidth(string) / 2, height);
	}
	/**
	 * Draws a right sided string on regular font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawRightsideRegularString(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth()*10 / 13
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	public void drawRightsideRegularStringKr(final Screen screen,
										   final String string, final int height) {
		backBufferGraphics.setFont(fontRegularKr);
		backBufferGraphics.drawString(string, screen.getWidth()*10 / 13
				- fontRegularKrMetrics.stringWidth(string) / 2, height);
	}

	public void drawRightsideRegularStringCn(final Screen screen,
											 final String string, final int height) {
		backBufferGraphics.setFont(fontRegularCn);
		backBufferGraphics.drawString(string, screen.getWidth()*10 / 13
				- fontRegularCnMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws a centered string on big font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredBigString(final Screen screen, final String string,
									  final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigMetrics.stringWidth(string) / 2, height);
	}

	public void drawCenteredBigStringKr(final Screen screen, final String string,
									  final int height) {
		backBufferGraphics.setFont(fontBigKr);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigKrMetrics.stringWidth(string) / 2, height);
	}

	public void drawCenteredBigStringCn(final Screen screen, final String string,
									  final int height) {
		backBufferGraphics.setFont(fontBigCn);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigCnMetrics.stringWidth(string) / 2, height);
	}


	/**
	 * Countdown to game start.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Game difficulty level.
	 * @param number
	 *            Countdown number.
	 * @param bonusLife
	 *            Checks if a bonus life is received.
	 */
	public void drawCountDown(final Screen screen, final int level,
							  final int number, final boolean bonusLife) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		backBufferGraphics.setColor(Color.GREEN);

		if (Lang.lang == 2) {
			if (number >= 4)
				if (!bonusLife) {
					drawCenteredBigString(screen, "水平 " + level,
							screen.getHeight() / 2
									+ fontBigCnMetrics.getHeight() / 3);
				} else {
					drawCenteredBigString(screen, "水平 " + level
									+ " - 奖励生命!",
							screen.getHeight() / 2
									+ fontBigCnMetrics.getHeight() / 3);
				}
			else if (number != 0)
				drawCenteredBigString(screen, Integer.toString(number),
						screen.getHeight() / 2 + fontBigCnMetrics.getHeight() / 3);
			else
				drawCenteredBigString(screen, "走！", screen.getHeight() / 2
						+ fontBigCnMetrics.getHeight() / 3);
		}
		else if (Lang.lang == 1) {
			if (number >= 4)
				if (!bonusLife) {
					drawCenteredBigString(screen, "레벨 " + level,
							screen.getHeight() / 2
									+ fontBigKrMetrics.getHeight() / 3);
				} else {
					drawCenteredBigString(screen, "레벨 " + level
									+ " - 보너스 라이프!",
							screen.getHeight() / 2
									+ fontBigKrMetrics.getHeight() / 3);
				}
			else if (number != 0)
				drawCenteredBigString(screen, Integer.toString(number),
						screen.getHeight() / 2 + fontBigKrMetrics.getHeight() / 3);
			else
				drawCenteredBigString(screen, "출발!", screen.getHeight() / 2
						+ fontBigKrMetrics.getHeight() / 3);
		}
		else {
			if (number >= 4)
				if (!bonusLife) {
					drawCenteredBigString(screen, "Level " + level,
							screen.getHeight() / 2
									+ fontBigMetrics.getHeight() / 3);
				} else {
					drawCenteredBigString(screen, "Level " + level
									+ " - Bonus life!",
							screen.getHeight() / 2
									+ fontBigMetrics.getHeight() / 3);
				}
			else if (number != 0)
				drawCenteredBigString(screen, Integer.toString(number),
						screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
			else
				drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
						+ fontBigMetrics.getHeight() / 3);
		}
	}

	public void drawClear(final Screen screen, final int option, final int level) {
		String titleString;

		if (Lang.lang == 2) {
			titleString = level + "级清除！";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringCn(screen, titleString, screen.getHeight() / 3 +  fontRegularCnMetrics.getHeight() * 2);
		}
		else if (Lang.lang == 1) {
			titleString = "레벨 " + level + "클리어!";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringKr(screen, titleString, screen.getHeight() / 3 +  fontRegularKrMetrics.getHeight() * 2);
		}
		else {
			titleString = "LEVEL  " + level + "  Clear";

			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigString(screen, titleString, screen.getHeight() / 3 +  fontRegularMetrics.getHeight() * 2);
		}

		String continueString;
		String exitString;

		if (Lang.lang == 2) {
			continueString = "继续";
			exitString = "出口";

			if (option == 2)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, continueString,
					screen.getHeight() / 4 * 3);

			if (option == 1)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, exitString,
					screen.getHeight() / 4 * 3 + fontRegularCnMetrics.getHeight() * 2);
		}
		else if (Lang.lang == 1) {
			continueString = "계속";
			exitString = "나가기";

			if (option == 2)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, continueString,
					screen.getHeight() / 4 * 3);

			if (option == 1)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, exitString,
					screen.getHeight() / 4 * 3 + fontRegularKrMetrics.getHeight() * 2);
		}
		else {
			continueString = "Continue";
			exitString = "Exit";

			if (option == 2)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, continueString,
					screen.getHeight() / 4 * 3);

			if (option == 1)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, exitString,
					screen.getHeight() / 4 * 3 + fontRegularMetrics.getHeight() * 2);
		}
	}

	public void drawWindow(final Screen screen, int x, int y, int w){
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(x, y,
				rectWidth, rectHeight + w);
	}

	public void drawPauseMenu(final Screen screen, final int option) {
		String quit;
		String resume;

		if (Lang.lang == 2) {
			quit = "辞去";
			resume = "复职";

			if (option == 1)
				backBufferGraphics.setColor(Color.YELLOW);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, quit, screen.getHeight() / 2 - 10);

			if (option == 0)
				backBufferGraphics.setColor(Color.YELLOW);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringCn(screen, resume, screen.getHeight() / 2 + 20 );

			//How to operate in the pause window
			backBufferGraphics.setColor(Color.YELLOW);
			drawCenteredRegularStringCn(screen, "更改内容: Ctrl" + " / " + "选择: Spacebar",
					screen.getHeight() / 2 - screen.getHeight() / 12 - 15);
		}
		else if (Lang.lang == 1) {
			quit = "나가기";
			resume = "돌아가기";

			if (option == 1)
				backBufferGraphics.setColor(Color.YELLOW);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, quit, screen.getHeight() / 2 - 10);

			if (option == 0)
				backBufferGraphics.setColor(Color.YELLOW);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularStringKr(screen, resume, screen.getHeight() / 2 + 20 );

			//How to operate in the pause window
			backBufferGraphics.setColor(Color.YELLOW);
			drawCenteredRegularStringKr(screen, "바꾸기: Ctrl" + " / " + "고르기: Spacebar",
					screen.getHeight() / 2 - screen.getHeight() / 12 - 15);
		}
		else {
			quit = "Quit";
			resume = "Resume";

			if (option == 1)
				backBufferGraphics.setColor(Color.YELLOW);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, quit, screen.getHeight() / 2 - 10);

			if (option == 0)
				backBufferGraphics.setColor(Color.YELLOW);
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, resume, screen.getHeight() / 2 + 20 );

			//How to operate in the pause window
			backBufferGraphics.setColor(Color.YELLOW);
			drawCenteredRegularString(screen, "Change: Ctrl" + " / " + "Select: Spacebar",
					screen.getHeight() / 2 - screen.getHeight() / 12 - 15);
		}
	}
	public void drawManualMenu(final Screen screen) {

		String[] keyInfo = {"left", "right", "attack", "burst1", "burst2", "reload", "booster", "item"};
		String[] keyValue = Core.getKeySettingStringArray();

		backBufferGraphics.setColor(Color.CYAN);
		drawCenteredRegularString(screen, "Play manual", screen.getHeight() / 2 - 105);
		backBufferGraphics.drawString("Player1", screen.getWidth() / 2 - 140, screen.getHeight() / 2 - 60);
		backBufferGraphics.drawString("Player2", screen.getWidth() / 2 + 65, screen.getHeight() / 2 - 60);

		backBufferGraphics.setColor(Color.WHITE);
		int y = screen.getHeight() / 2 - 30;
		int x1 = screen.getWidth() / 2 - 150; //player1_manual
		int x2 = screen.getWidth() / 2 - 50; //player1_setting
		int x3 = screen.getWidth() / 2 + 50; //player2
		int x4 = screen.getWidth() / 2 + 150; //player2_setting
		for(int i=0; i<8; i++){
			backBufferGraphics.drawString(keyInfo[i], x1-fontRegularMetrics.stringWidth(keyInfo[i])/2, y+20*i);
			backBufferGraphics.drawString(keyValue[i], x2-fontRegularMetrics.stringWidth(keyValue[i])/2, y+20*i);
			backBufferGraphics.drawString(keyInfo[i], x3-fontRegularMetrics.stringWidth(keyInfo[i])/2, y+20*i);
			backBufferGraphics.drawString(keyValue[i+8], x4-fontRegularMetrics.stringWidth(keyValue[i+8])/2, y+20*i);
		}


	}

	public void drawOneFifthRegularString(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 5
				- fontRegularMetrics.stringWidth(string) / 2 , height);
	}

	public void drawOneFifthRegularStringCn(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegularCn);
		backBufferGraphics.drawString(string, screen.getWidth() / 5
				- fontRegularCnMetrics.stringWidth(string) / 2 , height);
	}

	public void drawOneFifthRegularStringKr(final Screen screen,
										  final String string, final int height) {
		backBufferGraphics.setFont(fontRegularKr);
		backBufferGraphics.drawString(string, screen.getWidth() / 5
				- fontRegularKrMetrics.stringWidth(string) / 2 , height);
	}

	public void drawSevenTenthRegularString(final Screen screen,
											final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 10 * 7
				- fontRegularMetrics.stringWidth(string) / 2 , height);
	}

	public void drawSevenTenthRegularStringCn(final Screen screen,
											final String string, final int height) {
		backBufferGraphics.setFont(fontRegularCn);
		backBufferGraphics.drawString(string, screen.getWidth() / 10 * 7
				- fontRegularCnMetrics.stringWidth(string) / 2 , height);
	}

	public void drawSevenTenthRegularStringKr(final Screen screen,
											final String string, final int height) {
		backBufferGraphics.setFont(fontRegularKr);
		backBufferGraphics.drawString(string, screen.getWidth() / 10 * 7
				- fontRegularKrMetrics.stringWidth(string) / 2 , height);
	}

	public void drawStar(final Screen screen,
						 final String string, final int height){
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString("*", screen.getWidth() / 5
				- fontRegularMetrics.stringWidth(string) / 2 -16, height);
	}

	public void drawSetting(final Screen screen, final int option, final boolean selected){
		String settingString;
		String instructionsString1;
		String instructionsString2;

		String volumeString;
		String bgmString;
		String keysString1;
		String keysString2;

		if (Lang.lang == 2) {
			settingString = "设置";
			instructionsString1 = "使用上移、下移/使用Space选择";
			instructionsString2 = "按ESC键返回";

			volumeString = "音量";
			bgmString = "BGM";
			keysString1 = "1P 按钮";
			keysString2 = "2P 按钮";


			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringCn(screen, settingString, screen.getHeight() / 8);

			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringCn(screen, instructionsString1,
					screen.getHeight() / 5 - fontRegularCnMetrics.getHeight() / 2);
			drawCenteredRegularStringCn(screen, instructionsString2,
					screen.getHeight() / 5 + fontRegularCnMetrics.getHeight() / 2);


			if (option == 0) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, volumeString,
						screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 2);
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringCn(screen, volumeString,
					screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 2);

			if (option == 1) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, bgmString,
						screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 4);
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringCn(screen, bgmString,
					screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 4);

			if (option == 2) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, keysString1,
						screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 6);;
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringCn(screen, keysString1,
					screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 6);

			if (option == 3) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, keysString2,
						screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 8);;
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringCn(screen, keysString2,
					screen.getHeight() / 4 + fontRegularCnMetrics.getHeight() * 8);
		}

		else if (Lang.lang == 1) {
			settingString = "설정";
			instructionsString1 = "위아래 방향키로 움직이기 / 스페이스 바로 선택하기";
			instructionsString2 = "Esc로 돌아가기";

			volumeString = "음량";
			bgmString = "BGM";
			keysString1 = "1P 키";
			keysString2 = "2P 키";


			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringKr(screen, settingString, screen.getHeight() / 8);

			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringKr(screen, instructionsString1,
					screen.getHeight() / 5 - fontRegularKrMetrics.getHeight() / 2);
			drawCenteredRegularStringKr(screen, instructionsString2,
					screen.getHeight() / 5 + fontRegularKrMetrics.getHeight() / 2);


			if (option == 0) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, volumeString,
						screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 2);
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringKr(screen, volumeString,
					screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 2);

			if (option == 1) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, bgmString,
						screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 4);
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringKr(screen, bgmString,
					screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 4);

			if (option == 2) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, keysString1,
						screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 6);;
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringKr(screen, keysString1,
					screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 6);

			if (option == 3) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, keysString2,
						screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 8);;
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularStringKr(screen, keysString2,
					screen.getHeight() / 4 + fontRegularKrMetrics.getHeight() * 8);
		}

		else {
			settingString = "Setting";
			instructionsString1 = "Move with UP, DOWN / Select with SPACE";
			instructionsString2 = "Press ESC to return";

			volumeString = "Volume";
			bgmString = "BGM";
			keysString1 = "1P Keys";
			keysString2 = "2P Keys";


			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigString(screen, settingString, screen.getHeight() / 8);

			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularString(screen, instructionsString1,
					screen.getHeight() / 5 - fontRegularMetrics.getHeight() / 2);
			drawCenteredRegularString(screen, instructionsString2,
					screen.getHeight() / 5 + fontRegularMetrics.getHeight() / 2);


			if (option == 0) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, volumeString,
						screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 2);
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularString(screen, volumeString,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 2);

			if (option == 1) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, bgmString,
						screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 4);
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularString(screen, bgmString,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 4);

			if (option == 2) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, keysString1,
						screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 6);;
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularString(screen, keysString1,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 6);

			if (option == 3) {
				backBufferGraphics.setColor(Color.GREEN);
				if(selected) drawStar(screen, keysString2,
						screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 8);;
			}
			else
				backBufferGraphics.setColor(Color.WHITE);
			drawOneFifthRegularString(screen, keysString2,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 8);
		}

		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(screen.getWidth()/5*2 -1,screen.getHeight() / 4,
				screen.getWidth()/5*2 -1,screen.getHeight() / 10 * 9);
		backBufferGraphics.drawLine(screen.getWidth()/5*2,screen.getHeight() / 4,
				screen.getWidth()/5*2,screen.getHeight() / 10 * 9);

	}


	public void drawSettingDetail(final Screen screen, final int option, final boolean selected,
								  int volume, boolean bgmOn, int keyNum) {
		String[] keyString = Core.getKeySettingStringArray();
		if(option == 0 || option == 1){
			if (option == 0 && selected)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);

			backBufferGraphics.drawRect(screen.getWidth() / 2, screen.getHeight() / 4 + fontRegularMetrics.getHeight() / 8 * 12,
					screen.getWidth() / 4, fontRegularMetrics.getHeight());
			backBufferGraphics.fillRect(screen.getWidth() / 2, screen.getHeight() / 4 + fontRegularMetrics.getHeight() / 8 * 12,
					screen.getWidth() / 4 * volume / 100, fontRegularMetrics.getHeight());
			backBufferGraphics.drawString(Integer.toString(volume), screen.getWidth() / 4 * 3
					+ fontRegularMetrics.stringWidth("A") * 2, screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 2);

			if (option == 1 && selected)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);

			if(bgmOn) drawSevenTenthRegularString(screen,"ON",screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 4);
			else drawSevenTenthRegularString(screen,"OFF",screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 4);
		}

		if (option == 2){
			drawKeyString(screen,"LEFT", "RIGHT", "ATTACK", "BURST 1", "BURST 2","RELOAD","BOOSTER","ITEM",6);
			drawKeyString(screen,keyString[0],keyString[1],keyString[2],keyString[3],keyString[4],keyString[5],keyString[6],keyString[7], 8);
			if(selected){
				drawGreenKeyString(screen, keyNum);
			}

		}

		if (option == 3){
			drawKeyString(screen,"LEFT", "RIGHT", "ATTACK", "BURST 1", "BURST 2","RELOAD","BOOSTER","ITEM",6);
			drawKeyString(screen,keyString[8],keyString[9],keyString[10],keyString[11],keyString[12],keyString[13],keyString[14],keyString[15], 8);
			if(selected){
				drawGreenKeyString(screen, keyNum);
			}

		}



	}
	private void drawKeyString(Screen screen, String s1,String s2,String s3,String s4,String s5,String s6,String s7,String s8,int num){
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(s1, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s1) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight());
		backBufferGraphics.drawString(s2, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s2) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 3);
		backBufferGraphics.drawString(s3, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s3) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 5);
		backBufferGraphics.drawString(s4, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s4) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 7);
		backBufferGraphics.drawString(s5, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s5) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 9);
		backBufferGraphics.drawString(s6, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s6) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 11);
		backBufferGraphics.drawString(s7, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s7) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 13);
		backBufferGraphics.drawString(s8, screen.getWidth() / 10 * num
				- fontRegularMetrics.stringWidth(s8) / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 15);
	}

	private  void drawGreenKeyString(Screen screen,int keyNum){
		backBufferGraphics.setFont(fontRegular);
		if(keyNum == 0){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("LEFT", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("LEFT") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight());
		}
		if(keyNum == 1){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("RIGHT", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("RIGHT") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 3);
		}
		if(keyNum == 2){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("ATTACK", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("ATTACK") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 5);
		}
		if(keyNum == 3){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("BURST 1", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("BURST 1") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 7);
		}
		if(keyNum == 4){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("BURST 2", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("BURST 2") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 9);
		}
		if(keyNum == 5){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("RELOAD", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("RELOAD") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 11);
		}
		if(keyNum == 6){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("BOOSTER", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("BOOSTER") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 13);
		}if(keyNum == 7){
			backBufferGraphics.setColor(Color.GREEN);
			backBufferGraphics.drawString("ITEM", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("ITEM") / 2 , screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 15);
		}
	}

	public void drawSelect2PModeAndSkillModeScreen(Screen screen, int gameMode, boolean skillModeOn, boolean nextItem){
		String selectString;
		String instructionsString;

		if (Lang.lang == 2) {
			selectString = "选择模式";
			instructionsString =
					"用a+d/箭头选择,用空格确认";

			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringCn(screen, instructionsString, screen.getHeight() / 5);
			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringCn(screen, selectString, screen.getHeight() / 8);
			backBufferGraphics.drawString("球员", screen.getWidth() / 5
					- fontRegularCnMetrics.stringWidth("球员") / 2 , screen.getHeight() / 8 * 3);
			backBufferGraphics.drawString("技能模式", screen.getWidth() / 5
					- fontRegularCnMetrics.stringWidth("球员") / 2 , screen.getHeight() / 8 * 5);

			if(gameMode == 1) backBufferGraphics.setColor(Color.GREEN);
			else backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("1P", screen.getWidth() / 10 * 6
					- fontRegularCnMetrics.stringWidth("1P") / 2 , screen.getHeight()
					/ 8 * 3 + fontRegularCnMetrics.getHeight() * 2);

			if(gameMode == 2) backBufferGraphics.setColor(Color.GREEN);
			else backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("2P", screen.getWidth() / 10 * 8
					- fontRegularCnMetrics.stringWidth("2P") / 2 , screen.getHeight()
					/ 8 * 3 + fontRegularCnMetrics.getHeight() * 2);

			backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("ON", screen.getWidth() / 10 * 6
					- fontRegularCnMetrics.stringWidth("ON") / 2 , screen.getHeight()
					/ 8 * 5 + fontRegularCnMetrics.getHeight() * 2);
			backBufferGraphics.drawString("OFF", screen.getWidth() / 10 * 8
					- fontRegularCnMetrics.stringWidth("OFF") / 2 , screen.getHeight()
					/ 8 * 5 + fontRegularCnMetrics.getHeight() * 2);
			if(nextItem){
				backBufferGraphics.setColor(Color.GREEN);
				if(skillModeOn) backBufferGraphics.drawString("ON", screen.getWidth() / 10 * 6
						- fontRegularCnMetrics.stringWidth("ON") / 2 , screen.getHeight()
						/ 8 * 5 + fontRegularCnMetrics.getHeight() * 2);
				else backBufferGraphics.drawString("OFF", screen.getWidth() / 10 * 8
						- fontRegularCnMetrics.stringWidth("OFF") / 2 , screen.getHeight()
						/ 8 * 5 + fontRegularCnMetrics.getHeight() * 2);
			}
		}

		else if (Lang.lang == 1) {
			selectString = "모드 선택";
			instructionsString =
					"a+d / 방향키로 선택하고, 스페이스 바로 정하세요";

			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularStringKr(screen, instructionsString, screen.getHeight() / 5);
			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigStringKr(screen, selectString, screen.getHeight() / 8);
			backBufferGraphics.drawString("플레이어", screen.getWidth() / 5
					- fontRegularKrMetrics.stringWidth("플레이어") / 2 , screen.getHeight() / 8 * 3);
			backBufferGraphics.drawString("스킬 모드", screen.getWidth() / 5
					- fontRegularKrMetrics.stringWidth("플레이어") / 2 , screen.getHeight() / 8 * 5);

			if(gameMode == 1) backBufferGraphics.setColor(Color.GREEN);
			else backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("1P", screen.getWidth() / 10 * 6
					- fontRegularKrMetrics.stringWidth("1P") / 2 , screen.getHeight()
					/ 8 * 3 + fontRegularKrMetrics.getHeight() * 2);

			if(gameMode == 2) backBufferGraphics.setColor(Color.GREEN);
			else backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("2P", screen.getWidth() / 10 * 8
					- fontRegularKrMetrics.stringWidth("2P") / 2 , screen.getHeight()
					/ 8 * 3 + fontRegularKrMetrics.getHeight() * 2);

			backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("ON", screen.getWidth() / 10 * 6
					- fontRegularKrMetrics.stringWidth("ON") / 2 , screen.getHeight()
					/ 8 * 5 + fontRegularKrMetrics.getHeight() * 2);
			backBufferGraphics.drawString("OFF", screen.getWidth() / 10 * 8
					- fontRegularKrMetrics.stringWidth("OFF") / 2 , screen.getHeight()
					/ 8 * 5 + fontRegularKrMetrics.getHeight() * 2);
			if(nextItem){
				backBufferGraphics.setColor(Color.GREEN);
				if(skillModeOn) backBufferGraphics.drawString("ON", screen.getWidth() / 10 * 6
						- fontRegularKrMetrics.stringWidth("ON") / 2 , screen.getHeight()
						/ 8 * 5 + fontRegularKrMetrics.getHeight() * 2);
				else backBufferGraphics.drawString("OFF", screen.getWidth() / 10 * 8
						- fontRegularKrMetrics.stringWidth("OFF") / 2 , screen.getHeight()
						/ 8 * 5 + fontRegularKrMetrics.getHeight() * 2);
			}
		}

		else {
			selectString = "Select Mode";
			instructionsString =
					"select with a+d / arrows, confirm with space";

			backBufferGraphics.setColor(Color.GRAY);
			drawCenteredRegularString(screen, instructionsString, screen.getHeight() / 5);
			backBufferGraphics.setColor(Color.GREEN);
			drawCenteredBigString(screen, selectString, screen.getHeight() / 8);
			backBufferGraphics.drawString("Player", screen.getWidth() / 5
					- fontRegularMetrics.stringWidth("Player") / 2 , screen.getHeight() / 8 * 3);
			backBufferGraphics.drawString("Skill Mode", screen.getWidth() / 5
					- fontRegularMetrics.stringWidth("Player") / 2 , screen.getHeight() / 8 * 5);

			if(gameMode == 1) backBufferGraphics.setColor(Color.GREEN);
			else backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("1P", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("1P") / 2 , screen.getHeight()
					/ 8 * 3 + fontRegularMetrics.getHeight() * 2);

			if(gameMode == 2) backBufferGraphics.setColor(Color.GREEN);
			else backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("2P", screen.getWidth() / 10 * 8
					- fontRegularMetrics.stringWidth("2P") / 2 , screen.getHeight()
					/ 8 * 3 + fontRegularMetrics.getHeight() * 2);

			backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.drawString("ON", screen.getWidth() / 10 * 6
					- fontRegularMetrics.stringWidth("ON") / 2 , screen.getHeight()
					/ 8 * 5 + fontRegularMetrics.getHeight() * 2);
			backBufferGraphics.drawString("OFF", screen.getWidth() / 10 * 8
					- fontRegularMetrics.stringWidth("OFF") / 2 , screen.getHeight()
					/ 8 * 5 + fontRegularMetrics.getHeight() * 2);
			if(nextItem){
				backBufferGraphics.setColor(Color.GREEN);
				if(skillModeOn) backBufferGraphics.drawString("ON", screen.getWidth() / 10 * 6
						- fontRegularMetrics.stringWidth("ON") / 2 , screen.getHeight()
						/ 8 * 5 + fontRegularMetrics.getHeight() * 2);
				else backBufferGraphics.drawString("OFF", screen.getWidth() / 10 * 8
						- fontRegularMetrics.stringWidth("OFF") / 2 , screen.getHeight()
						/ 8 * 5 + fontRegularMetrics.getHeight() * 2);
			}
		}
	}

	/**
	 * Draws achievement information on the screen based on the achievements map.
	 *
	 * @param screen      Screen to draw on.
	 * @param achievements Map of achievements with their completion status.
	 */
	public void drawAchievements(final Screen screen, Map<Achievement, Boolean> achievements) {
		backBufferGraphics.setFont(fontRegular);

		int x = 20; // Fixed X-coordinate for achievement titles.
		int y = 50; // Fixed Y-coordinate for the initial position.

		// Loop through the achievements map and display each achievement.
		for (Map.Entry<Achievement, Boolean> entry : achievements.entrySet()) {
				String achievementTitle = entry.getKey().toString().replace('_', ' ');
				boolean isCompleted = entry.getValue();

				// Display the achievement title.
				backBufferGraphics.setColor(Color.YELLOW);
				backBufferGraphics.drawString(achievementTitle, x, y);

				// Check if the achievement is completed and adjust the color accordingly.
				if (isCompleted) {
						backBufferGraphics.setColor(Color.GREEN);
				} else {
						backBufferGraphics.setColor(Color.RED);
				}

				// Calculate the position to display achievementStatus (completed or incomplete) on the right of achievementTitle.
				int titleWidth = fontRegularMetrics.stringWidth(achievementTitle);
				int statusX = x + titleWidth + 10; // You can adjust the spacing as needed.

				// Display whether the achievement is completed or not.
				String achievementStatus = isCompleted ? "Completed" : "Incomplete";
				backBufferGraphics.drawString(achievementStatus, statusX, y);

				// You can add more information about the achievement if needed.
				// For example, you can display the progress or description.

				// Increase the Y-coordinate for the next achievement entry.
				y += 40;
		}
	}
}
