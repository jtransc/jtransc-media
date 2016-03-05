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

package jtransc.media;

import jtransc.io.JTranscIoTools;

import java.io.FileInputStream;

public final class JTranscIO {
	static public Impl impl = new Impl() {
		private ClassLoader classLoader = JTranscIO.class.getClassLoader();

		@Override
		public void readAsync(String path, JTranscCallback<byte[]> handler) {
			try {
				byte[] data = JTranscIoTools.readStreamFully(new FileInputStream(path));
				handler.handler(null, data);
			} catch (Throwable t) {
				handler.handler(t, null);
			}
		}

		@Override
		public void getResourceAsync(String path, JTranscCallback<byte[]> handler) {
			try {
				byte[] data = JTranscIoTools.readStreamFully(classLoader.getResourceAsStream(path));
				handler.handler(null, data);
			} catch (Throwable t) {
				handler.handler(t, null);
			}
		}
	};

	static public void readAsync(String path, JTranscCallback<byte[]> handler) {
		impl.readAsync(path, handler);
	}

	static public void getResourceAsync(String path, JTranscCallback<byte[]> handler) {
		impl.getResourceAsync(path, handler);
	}

	public interface Impl {
		void readAsync(String path, JTranscCallback<byte[]> handler);

		void getResourceAsync(String path, JTranscCallback<byte[]> handler);
	}
}
