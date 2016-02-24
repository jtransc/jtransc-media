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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;

public class JTranscIO {
	static public Impl impl = new Impl() {
		@Override
		public void readAsync(String path, JTranscCallback<byte[]> handler) {
			try {
				File file = new File(path);
				byte[] fileData = new byte[(int) file.length()];
				DataInputStream dis = new DataInputStream(new FileInputStream(file));
				dis.readFully(fileData);
				dis.close();
				handler.handler(null, fileData);
			} catch (Throwable t) {
				handler.handler(t, null);
			}
		}
	};

	static public void readAsync(String path, JTranscCallback<byte[]> handler) {
		impl.readAsync(path, handler);
	}

	public interface Impl {
		void readAsync(String path, JTranscCallback<byte[]> handler);
	}
}
