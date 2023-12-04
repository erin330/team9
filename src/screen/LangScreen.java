package screen;

import engine.Cooldown;
import engine.Core;
import engine.Player;
import engine.SoundManager;
import engine.Lang;

import java.awt.event.KeyEvent;
import java.io.IOException;

/**
 * Implements the title screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class LangScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;

	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

	private int lang;

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public LangScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		// Default Lang.
		this.returnCode = 0;
		this.lang = 0;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		draw();
		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				previousLangItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				nextLangItem();
				this.selectionCooldown.reset();
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				if (this.returnCode == 2) Lang.lang = 2;
				else if (this.returnCode == 1) Lang.lang = 1;
				else Lang.lang = 0;

				// Proceed to login screen.
				this.returnCode = 9;
				this.isRunning = false;
			}
		}
	}

	/**
	 * Shifts the focus to the next menu item.
	 */
	private void nextLangItem() {
		if (this.returnCode == 2)
			this.returnCode = 0;
		else
			this.returnCode++;
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousLangItem() {
		if (this.returnCode == 0)
			this.returnCode = 2;
		else
			this.returnCode--;
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawLangDesc(this);
		drawManager.drawLangMenu(this, this.returnCode);

		drawManager.completeDrawing(this);
	}
}
