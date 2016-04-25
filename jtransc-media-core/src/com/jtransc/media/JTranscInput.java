/*
 * Copyright 2016 Carlos Ballesteros Velasco
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jtransc.media;

import com.jtransc.annotation.JTranscKeep;

import java.util.ArrayList;

public final class JTranscInput {
	@JTranscKeep
	static public ListHandler impl = new ListHandler();
	@JTranscKeep
	static public MouseInfo mouseInfo = new MouseInfo();
	@JTranscKeep
	static public KeyInfo keyInfo = new KeyInfo();
	@JTranscKeep
	static public GamepadInfo gamepadInfo = new GamepadInfo();

	public interface Impl {
		void addHandler(Handler handler);
	}

	@JTranscKeep
	static public class KeyInfo {
		public int keyCode;
	}

	@JTranscKeep
	static public class MouseInfo {
		public int x;
		public int y;
		public int buttons;

		public void setScreenXY(int screenX, int screenY) {
			this.x = (int) (screenX / JTranscWindow.getVirtualScaleX());
			this.y = (int) (screenY / JTranscWindow.getVirtualScaleY());
		}
	}

	@JTranscKeep
	static public class GamepadInfo {

	}

	@JTranscKeep
	static public class TouchInfo {
	}

	public interface Handler {
		@JTranscKeep
		void onKeyTyped(KeyInfo info);

		@JTranscKeep
		void onKeyDown(KeyInfo info);

		@JTranscKeep
		void onKeyUp(KeyInfo info);

		@JTranscKeep
		void onGamepadPressed(GamepadInfo info);

		@JTranscKeep
		void onGamepadRelepased(GamepadInfo info);

		@JTranscKeep
		void onMouseDown(MouseInfo info);

		@JTranscKeep
		void onMouseUp(MouseInfo info);

		@JTranscKeep
		void onMouseMove(MouseInfo info);

		@JTranscKeep
		void onMouseWheel(int amount);

		@JTranscKeep
		void onMouseScroll(MouseInfo info);

		@JTranscKeep
		void onTouchDown(TouchInfo info);

		@JTranscKeep
		void onTouchDrag(TouchInfo info);

		@JTranscKeep
		void onTouchUp(TouchInfo info);
	}

	static public class HandlerAdaptor implements Handler {
		@Override
		public void onKeyTyped(KeyInfo info) {
		}

		@Override
		public void onKeyDown(KeyInfo info) {
		}

		@Override
		public void onKeyUp(KeyInfo info) {
		}

		@Override
		public void onGamepadPressed(GamepadInfo info) {
		}

		@Override
		public void onGamepadRelepased(GamepadInfo info) {
		}

		@Override
		public void onMouseDown(MouseInfo info) {
		}

		@Override
		public void onMouseUp(MouseInfo info) {
		}

		@Override
		public void onMouseMove(MouseInfo info) {
		}

		@Override
		public void onMouseWheel(int amount) {
		}

		@Override
		public void onMouseScroll(MouseInfo info) {
		}

		@Override
		public void onTouchDown(TouchInfo info) {
		}

		@Override
		public void onTouchDrag(TouchInfo info) {
		}

		@Override
		public void onTouchUp(TouchInfo info) {
		}
	}

	static public class ListHandler implements Handler, Impl {
		private ArrayList<Handler> handlers = new ArrayList<Handler>();

		public void addHandler(Handler handler) {
			handlers.add(handler);
		}

		@Override
		public void onKeyTyped(KeyInfo info) {
			for (Handler handler : handlers) handler.onKeyTyped(info);
		}

		@Override
		public void onKeyDown(KeyInfo info) {
			for (Handler handler : handlers) handler.onKeyDown(info);
		}

		@Override
		public void onKeyUp(KeyInfo info) {
			for (Handler handler : handlers) handler.onKeyUp(info);
		}

		@Override
		public void onGamepadPressed(GamepadInfo info) {
			for (Handler handler : handlers) handler.onGamepadPressed(info);
		}

		@Override
		public void onGamepadRelepased(GamepadInfo info) {
			for (Handler handler : handlers) handler.onGamepadRelepased(info);
		}

		@Override
		public void onMouseDown(MouseInfo info) {
			for (Handler handler : handlers) handler.onMouseDown(info);
		}

		@Override
		public void onMouseUp(MouseInfo info) {
			for (Handler handler : handlers) handler.onMouseUp(info);
		}

		@Override
		public void onMouseMove(MouseInfo info) {
			for (Handler handler : handlers) handler.onMouseMove(info);
		}

		@Override
		public void onMouseWheel(int amount) {
			for (Handler handler : handlers) handler.onMouseWheel(amount);
		}

		@Override
		public void onMouseScroll(MouseInfo info) {
			for (Handler handler : handlers) handler.onMouseScroll(info);
		}

		@Override
		public void onTouchDown(TouchInfo info) {
			for (Handler handler : handlers) handler.onTouchDown(info);
		}

		@Override
		public void onTouchDrag(TouchInfo info) {
			for (Handler handler : handlers) handler.onTouchDrag(info);
		}

		@Override
		public void onTouchUp(TouchInfo info) {
			for (Handler handler : handlers) handler.onTouchUp(info);
		}
	}

	static public void addHandler(Handler handler) {
		impl.addHandler(handler);
	}

	// HTML5/Javascript keyCodes
	public class Keys {
		public static final int UNKNOWN = 0;
		public static final int BACKSPACE = 8;
		public static final int TAB = 9;
		public static final int ENTER = 13;
		public static final int SHIFT = 16;
		public static final int CTRL = 17;
		public static final int ALT = 18;
		public static final int PAUSE_BREAK = 19;
		public static final int CAPS_LOCK = 20;
		public static final int ESCAPE = 27;
		public static final int PAGE_UP = 33;
		public static final int PAGE_DOWN = 34;
		public static final int END = 35;
		public static final int HOME = 36;
		public static final int LEFT_ARROW = 37;
		public static final int UP_ARROW = 38;
		public static final int RIGHT_ARROW = 39;
		public static final int DOWN_ARROW = 40;
		public static final int INSERT = 45;
		public static final int DELETE = 46;
		public static final int NUM_0 = 48;
		public static final int NUM_1 = 49;
		public static final int NUM_2 = 50;
		public static final int NUM_3 = 51;
		public static final int NUM_4 = 52;
		public static final int NUM_5 = 53;
		public static final int NUM_6 = 54;
		public static final int NUM_7 = 55;
		public static final int NUM_8 = 56;
		public static final int NUM_9 = 57;
		public static final int NUM_A = 65;
		public static final int NUM_B = 66;
		public static final int NUM_C = 67;
		public static final int NUM_D = 68;
		public static final int A = 65;
		public static final int B = 66;
		public static final int C = 67;
		public static final int D = 68;
		public static final int E = 69;
		public static final int F = 70;
		public static final int G = 71;
		public static final int H = 72;
		public static final int I = 73;
		public static final int J = 74;
		public static final int K = 75;
		public static final int L = 76;
		public static final int M = 77;
		public static final int N = 78;
		public static final int O = 79;
		public static final int P = 80;
		public static final int Q = 81;
		public static final int R = 82;
		public static final int S = 83;
		public static final int T = 84;
		public static final int U = 85;
		public static final int V = 86;
		public static final int W = 87;
		public static final int X = 88;
		public static final int Y = 89;
		public static final int Z = 90;
		public static final int LEFT_WINDOW_KEY = 91;
		public static final int RIGHT_WINDOW_KEY = 92;
		public static final int SELECT_KEY = 93;
		public static final int NUMPAD_0 = 96;
		public static final int NUMPAD_1 = 97;
		public static final int NUMPAD_2 = 98;
		public static final int NUMPAD_3 = 99;
		public static final int NUMPAD_4 = 100;
		public static final int NUMPAD_5 = 101;
		public static final int NUMPAD_6 = 102;
		public static final int NUMPAD_7 = 103;
		public static final int NUMPAD_8 = 104;
		public static final int NUMPAD_9 = 105;
		public static final int MULTIPLY = 106;
		public static final int ADD = 107;
		public static final int SUBTRACT = 109;
		public static final int DECIMAL_POINT = 110;
		public static final int DIVIDE = 111;
		public static final int F1 = 112;
		public static final int F2 = 113;
		public static final int F3 = 114;
		public static final int F4 = 115;
		public static final int F5 = 116;
		public static final int F6 = 117;
		public static final int F7 = 118;
		public static final int F8 = 119;
		public static final int F9 = 120;
		public static final int F10 = 121;
		public static final int F11 = 122;
		public static final int F12 = 123;
		public static final int NUM_LOCK = 144;
		public static final int SCROLL_LOCK = 145;
		public static final int SEMI_COLON = 186;
		public static final int EQUAL_SIGN = 187;
		public static final int COMMA = 188;
		public static final int DASH = 189;
		public static final int PERIOD = 190;
		public static final int FORWARD_SLASH = 191;
		public static final int GRAVE_ACCENT = 192;
		public static final int OPEN_BRACKET = 219;
		public static final int BACK_SLASH = 220;
		public static final int CLOSE_BRAKET = 221;
		public static final int SINGLE_QUOTE = 222;
	}
}
