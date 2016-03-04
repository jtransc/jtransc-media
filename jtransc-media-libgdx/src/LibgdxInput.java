import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import jtransc.media.JTranscInput;

import java.lang.reflect.Field;
import java.util.HashMap;

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
				}

				Integer transformedKeyCode = map.get(keyCode);
				return (transformedKeyCode != null) ? transformedKeyCode.intValue() : JTranscInput.Keys.UNKNOWN;
			}

			@Override
			public boolean keyTyped(char character) {
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				mouseInfo.x = screenX;
				mouseInfo.y = screenY;
				mouseInfo.buttons = 1;
				JTranscInput.impl.onMouseDown(mouseInfo);
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				mouseInfo.x = screenX;
				mouseInfo.y = screenY;
				mouseInfo.buttons = 0;
				JTranscInput.impl.onMouseUp(mouseInfo);
				return false;
			}

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				return false;
			}

			@Override
			public boolean mouseMoved(int screenX, int screenY) {
				mouseInfo.x = screenX;
				mouseInfo.y = screenY;
				JTranscInput.impl.onMouseMove(mouseInfo);
				return false;
			}

			@Override
			public boolean scrolled(int amount) {
				return false;
			}
		});

	}
}
