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

public final class JTranscEventLoop {
	static public Impl impl = new ImplAdaptor();

	public interface Impl {
		void init(Runnable init);
		void loop(Runnable update, Runnable render);
	}

	public static class ImplAdaptor implements Impl {
		@Override
		public void init(Runnable init) {
			init.run();
		}

		@Override
		public void loop(Runnable update, Runnable render) {
			try {
				while (true) {
					update.run();
					render.run();
					Thread.sleep(20L);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	static public void init(Runnable init) {
		impl.init(init);
	}

	static public void loop(Runnable update, Runnable render) {
		impl.loop(update, render);
	}
}
