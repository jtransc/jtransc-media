import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import jtransc.media.JTranscInput;
import jtransc.media.JTranscWindow;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class LibgdxInput {
	static public void config() {
		Gdx.input.setInputProcessor(new InputProcessor() {
			private JTranscInput.MouseInfo mouseInfo = new JTranscInput.MouseInfo();
			private JTranscInput.KeyInfo keyInfo = new JTranscInput.KeyInfo();

			@Override
			public boolean keyDown(int keyCode) {
				keyInfo.keyCode = transformKeyCode(keyCode);
				JTranscInput.impl.onKeyDown(keyInfo);
				return false;
			}

			@Override
			public boolean keyUp(int keyCode) {
				keyInfo.keyCode = transformKeyCode(keyCode);
				JTranscInput.impl.onKeyUp(keyInfo);
				return false;
			}

			private HashMap<Integer, Integer> map = null;

			private void populateMap(HashMap<Integer, Integer> map) {
				map.put(Input.Keys.A, JTranscInput.Keys.A);
				map.put(Input.Keys.ALT_LEFT, JTranscInput.Keys.ALT);
				map.put(Input.Keys.ALT_RIGHT, JTranscInput.Keys.ALT);
				map.put(Input.Keys.META_SYM_ON, JTranscInput.Keys.LEFT_WINDOW_KEY);
				map.put(Input.Keys.SHIFT_LEFT, JTranscInput.Keys.SHIFT);
				map.put(Input.Keys.SHIFT_RIGHT, JTranscInput.Keys.SHIFT);
				map.put(Input.Keys.CONTROL_LEFT, JTranscInput.Keys.CTRL);
				map.put(Input.Keys.CONTROL_RIGHT, JTranscInput.Keys.CTRL);
				map.put(Input.Keys.UP, JTranscInput.Keys.UP_ARROW);
				map.put(Input.Keys.DOWN, JTranscInput.Keys.DOWN_ARROW);
				map.put(Input.Keys.LEFT, JTranscInput.Keys.LEFT_ARROW);
				map.put(Input.Keys.RIGHT, JTranscInput.Keys.RIGHT_ARROW);
			}

			private int transformKeyCode(int keyCode) {
				if (map == null) {
					map = new HashMap<Integer, Integer>();
					Class<?> gdxKeys = Input.Keys.class;
					Class<?> jtranscKeys = JTranscInput.Keys.class;

					for (Field gdxField : gdxKeys.getDeclaredFields()) {
						try {
							Field jtranscField = jtranscKeys.getField(gdxField.getName());
							if (jtranscField != null) {
								int from = gdxField.getInt(null);
								int to = jtranscField.getInt(null);
								map.put(from, to);
							}
						} catch (NoSuchFieldException e) {
							//e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					}
					populateMap(map);
				}

				Integer transformedKeyCode = map.get(keyCode);
				return (transformedKeyCode != null) ? transformedKeyCode.intValue() : JTranscInput.Keys.UNKNOWN;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			private void setRawMouse(int screenX, int screenY, int button) {
				mouseInfo.setScreenXY(
					(int)(screenX * Gdx.graphics.getDensity()),
					(int)(screenY * Gdx.graphics.getDensity())
				);
				mouseInfo.buttons = button;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				//* Gdx.graphics.getDensity()
				setRawMouse(screenX, screenY, 1);
				JTranscInput.impl.onMouseDown(mouseInfo);
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				setRawMouse(screenX, screenY, 0);
				JTranscInput.impl.onMouseUp(mouseInfo);
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				//System.out.println("touchDragged!!");
				setRawMouse(screenX, screenY, 1);
				JTranscInput.impl.onMouseMove(mouseInfo);
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				//System.out.println("mouseMoved!!");
				setRawMouse(screenX, screenY, mouseInfo.buttons);
				JTranscInput.impl.onMouseMove(mouseInfo);
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				JTranscInput.impl.onMouseWheel(amount);
				return false;
			}
		});

	}
}
