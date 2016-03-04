package jtransc.media;

import jtransc.annotation.JTranscKeep;

import java.util.ArrayList;

public class JTranscWindow {
	public interface Handler {
		@JTranscKeep
		void onResized(int width, int height);
	}

	static public int width = 640;
	static public int height = 480;

	static private ArrayList<Handler> handlers = new ArrayList<Handler>();

	static public void addHandler(Handler handler) {
		handlers.add(handler);
	}

	static public void dispatchResized(int width, int height) {
		JTranscWindow.width = width;
		JTranscWindow.height = height;
		for (Handler handler : handlers) {
			handler.onResized(width, height);
		}
	}
}
