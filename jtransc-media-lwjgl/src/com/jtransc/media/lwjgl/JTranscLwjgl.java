package com.jtransc.media.lwjgl;/*
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

import com.jtransc.media.*;
import com.jtransc.JTranscVersion;
import com.jtransc.io.JTranscIoTools;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class JTranscLwjgl {
	static private long window;
	static private Runnable r_update;
	static private Runnable r_render;

	static private GLFWErrorCallback errorCallback;
	static private GLFWWindowSizeCallback windowSizeCallback;

	static private void init(final Runnable init) {
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (glfwInit() != GLFW_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		int WIDTH = 640;
		int HEIGHT = 480;

		// Create the window
		window = glfwCreateWindow(WIDTH, HEIGHT, "JTransc " + JTranscVersion.getVersion(), NULL, NULL);
		if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(
			window,
			(vidmode.width() - WIDTH) / 2,
			(vidmode.height() - HEIGHT) / 2
		);

		glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
			@Override
			public void invoke(long window, int width, int height) {
				updatedScreenSize();

				if (JTranscLwjgl.r_render != null) {
					JTranscLwjgl.r_render.run();
				}
			}
		});

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);

		GL.createCapabilities();

		JTranscRender.impl = new LwjglRenderer(window);
		JTranscAudio.impl = new LwjglAudio();
		LwjglInput.config(window);
		if (init != null) init.run();
	}

	static private void frame() {
		if (r_update != null) r_update.run();
		if (r_render != null) r_render.run();
	}

	static private void updatedScreenSize() {
		glfwGetWindowSize(window, LwglTemps.intBuffer(0), LwglTemps.intBuffer(1));
		//glfw
		JTranscWindow.setScreenSize(
			LwglTemps.intValue(0),
			LwglTemps.intValue(1)
			//(int)(Gdx.graphics.getWidth() * Gdx.graphics.getDensity()),
			//(int)(Gdx.graphics.getHeight() * Gdx.graphics.getDensity())
		);
	}

	static public void init() {
		JTranscIO.impl = new JTranscIO.Impl() {
			@Override
			public void readAsync(String path, JTranscCallback<byte[]> handler) {
				try {
					handler.handler(null, JTranscIoTools.readFile(LwjglFiles.getResource(path)));
				} catch (Throwable t) {
					handler.handler(t, null);
				}
			}

			@Override
			public void getResourceAsync(String path, JTranscCallback<byte[]> handler) {
				byte[] bytes = JTranscIoTools.readStreamFully(JTranscLwjgl.class.getClassLoader().getResourceAsStream(path));
				handler.handler(null, bytes);
			}
		};

		JTranscEventLoop.impl = new JTranscEventLoop.Impl() {
			@Override
			public void init(final Runnable init) {

				//init.run();
				JTranscLwjgl.init(init);

				while (glfwWindowShouldClose(window) == GLFW_FALSE) {
					if (JTranscLwjgl.r_update != null) {
						JTranscLwjgl.r_update.run();
					}
					if (JTranscLwjgl.r_render != null) {
						JTranscLwjgl.r_render.run();
					}

					// Poll for window events. The key callback above will only be
					// invoked during this call.
					glfwPollEvents();
				}
			}

			@Override
			public void loop(Runnable update, Runnable render) {
				JTranscLwjgl.r_update = update;
				JTranscLwjgl.r_render = render;
			}
		};
	}
}
