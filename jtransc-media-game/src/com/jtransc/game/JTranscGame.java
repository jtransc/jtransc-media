package com.jtransc.game;

import com.jtransc.JTranscEndian;
import com.jtransc.JTranscSystem;
import com.jtransc.JTranscVersion;
import com.jtransc.game.audio.Sound;
import com.jtransc.game.canvas.Canvas;
import com.jtransc.game.canvas.Texture;
import com.jtransc.game.event.KeyEvent;
import com.jtransc.game.event.MouseEvent;
import com.jtransc.game.math.Point;
import com.jtransc.game.stage.Sprite;
import com.jtransc.game.stage.Stage;
import com.jtransc.game.ui.Keys;
import com.jtransc.media.JTranscEventLoop;
import com.jtransc.media.JTranscInput;
import com.jtransc.media.JTranscWindow;

public class JTranscGame {
	public final Canvas canvas;
	public final Stage stage;
	public final Sprite root;

	private double lastTime = -1;
	final public Point mouse = new Point(-1000, -1000);
	public int mouseButtons = 0;

	//private boolean[] pressingKeys = new boolean[Keys.MAX]; // @TODO: Bug with treeshaking?
	public boolean[] pressingKeys = new boolean[Keys.MAX];

	static public JTranscGame instance;

	public JTranscGame(Canvas canvas, Stage stage) {
		this.canvas = canvas;
		this.stage = stage;
		this.root = stage.root;
		JTranscGame.instance = this;
	}

	public Sound sound(String path) {
		return new Sound(path);
	}

	public Texture image(String path, int width, int height) {
		return canvas.image(path, width, height);
	}

	public boolean isPressing(int keyCode) {
		return pressingKeys[keyCode];
	}

	public interface Handler {
		void init(JTranscGame game);
	}

	static public void setVirtualSize(int virtualWidth, int virtualHeight) {
		JTranscWindow.setVirtualSize(virtualWidth, virtualHeight);
	}

	static public void init(int virtualWidth, int virtualHeight, final Handler entry) {
		final Canvas canvas = new Canvas();
		JTranscWindow.setVirtualSize(virtualWidth, virtualHeight);
		JTranscEventLoop.init(new Runnable() {
			@Override
			public void run() {
				System.out.println("JTranscGame.Init");
				System.out.println("JTransc version:" + JTranscVersion.getVersion());
				System.out.println("Endian isLittleEndian,isBigEndian:" + JTranscEndian.isLittleEndian() + ", " + JTranscEndian.isBigEndian());

				final Stage stage = new Stage();
				final JTranscGame game = new JTranscGame(canvas, stage);

				entry.init(game);
				game.registerEvents();

				JTranscEventLoop.loop(new Runnable() {
					@Override
					public void run() {
						double currentTime = JTranscSystem.stamp();
						if (game.lastTime < 0) game.lastTime = currentTime;
						int elapsed = (int) (currentTime - game.lastTime);
						stage.update(elapsed);
						game.lastTime = currentTime;
					}
				}, new Runnable() {
					@Override
					public void run() {
						stage.render(canvas);
					}
				});
			}
		});
	}

	private void registerEvents() {
		final JTranscGame game = this;

		final KeyEvent keyEvent = new KeyEvent();

		JTranscInput.addHandler(new JTranscInput.HandlerAdaptor() {
			@Override
			public void onKeyDown(JTranscInput.KeyInfo info) {
				_onKeyDownUp(info, true);
			}

			@Override
			public void onKeyUp(JTranscInput.KeyInfo info) {
				_onKeyDownUp(info, false);
			}

			private void _onKeyDownUp(JTranscInput.KeyInfo info, boolean down) {
				keyEvent.type = down ? KeyEvent.Type.DOWN : KeyEvent.Type.UP;
				keyEvent.keyCode = info.keyCode;
				stage.root.dispatchEvent(keyEvent);
				game.pressingKeys[info.keyCode & 0x1FF] = true;
			}

			@Override
			public void onMouseDown(JTranscInput.MouseInfo info) {
				game.setMouseInfo(info);
			}

			@Override
			public void onMouseUp(JTranscInput.MouseInfo info) {
				game.setMouseInfo(info);
			}

			@Override
			public void onMouseMove(JTranscInput.MouseInfo info) {
				game.setMouseInfo(info);
			}

			@Override
			public void onMouseScroll(JTranscInput.MouseInfo info) {
				game.setMouseInfo(info);
			}
		});
	}

	private final MouseEvent mouseEvent = new MouseEvent();

	private void setMouseInfo(JTranscInput.MouseInfo info) {
		mouseEvent.position.x = info.x;
		mouseEvent.position.y = info.y;
		mouseEvent.buttons = info.buttons;
		mouse.setTo(info.x, info.y);
		mouseButtons = info.buttons;
		stage.root.dispatchEvent(mouseEvent);
		//System.out.println("Event: " + getMouse);
	}
}
