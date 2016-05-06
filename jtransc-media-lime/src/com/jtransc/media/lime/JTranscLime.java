package com.jtransc.media.lime;

import com.jtransc.FastMemory;
import com.jtransc.annotation.haxe.*;
import com.jtransc.io.JTranscSyncIO;
import com.jtransc.kotlin.JTranscKotlinReflectStripper;
import com.jtransc.media.*;

@HaxeAddFilesTemplate({
	"AGALMiniAssembler.hx",
	"HaxeLimeAssets.hx",
	"HaxeLimeAudio.hx",
	"HaxeLimeJTranscApplication.hx",
	"HaxeLimeRender.hx",
	"HaxeLimeRenderFlash.hx",
	"HaxeLimeRenderGL.hx",
	"HaxeLimeRenderImpl.hx",
	"HaxeLimeIO.hx"
})
@HaxeAddFilesBeforeBuildTemplate({
	"program.xml"
})
@HaxeCustomMain("" +
	"package $entryPointPackage;\n" +
	"class $entryPointSimpleName extends HaxeLimeJTranscApplication {\n" +
	"    public function new() {\n" +
	"        super();\n" +
	"        $inits\n" +
	"        $mainClass.$mainMethod(HaxeNatives.strArray(HaxeNatives.args()));\n" +
	"    }\n" +
	"}\n"
)
@HaxeAddSubtargetList({
	@HaxeAddSubtarget(name = "android"),
	@HaxeAddSubtarget(name = "blackberry"),
	@HaxeAddSubtarget(name = "desktop"),
	@HaxeAddSubtarget(name = "emscripten"),
	@HaxeAddSubtarget(name = "flash", alias = { "swf", "as3" }),
	@HaxeAddSubtarget(name = "html5", alias = { "js" }),
	@HaxeAddSubtarget(name = "ios"),
	@HaxeAddSubtarget(name = "linux"),
	@HaxeAddSubtarget(name = "mac"),
	@HaxeAddSubtarget(name = "tizen"),
	@HaxeAddSubtarget(name = "tvos"),
	@HaxeAddSubtarget(name = "webos"),
	@HaxeAddSubtarget(name = "windows"),
	@HaxeAddSubtarget(name = "neko")
})
@HaxeCustomBuildCommandLine({
	"haxelib", "run", "lime",
	"{% if debug %}-debug{% end %}",
	"build", "{{ actualSubtarget.name }}"
})
@HaxeAddLibraries({
	"lime:2.9.1"
})
public class JTranscLime {
	static public void init() {
		JTranscKotlinReflectStripper.init();
		JTranscRender.impl = new JTranscRenderLimeImpl();
		JTranscAudio.impl = new JTranscAudioLimeImpl();
		JTranscIO.impl = new JTranscIOLimeImpl();
		JTranscEventLoop.impl = new JTranscEventLoopLimeImpl();
		JTranscSyncIO.impl = new JTranscSyncIOLimeImpl(JTranscSyncIO.impl);
		JTranscWindow.referenced();
	}

	static private class JTranscEventLoopLimeImpl implements JTranscEventLoop.Impl {
		@Override
		@HaxeMethodBody("HaxeLimeJTranscApplication.loopInit(function() { p0.#METHOD:java.lang.Runnable:run#(); });")
		native public void init(Runnable init);

		@Override
		@HaxeMethodBody("HaxeLimeJTranscApplication.loopLoop(function() { p0.#METHOD:java.lang.Runnable:run#(); }, function() { p1.#METHOD:java.lang.Runnable:run#(); });")
		native public void loop(Runnable update, Runnable render);
	}

	static private class JTranscRenderLimeImpl implements JTranscRender.Impl {
		@Override
		@HaxeMethodBody("return HaxeLimeRender.createTexture(p0._str, p1, p2);")
		native public int createTexture(String path, int width, int height, boolean mipmaps);

		@Override
		// haxe.io.Int32Array
		@HaxeMethodBody("return HaxeLimeRender.createTextureMemory(p0.data, p1, p2, p3);")
		native public int createTextureMemory(int[] data, int width, int height, int format, boolean mipmaps);

		@Override
		native public int createTextureEncoded(byte[] data, int width, int height, boolean mipmaps);

		@Override
		@HaxeMethodBody("HaxeLimeRender.disposeTexture(p0);")
		native public void disposeTexture(int textureId);

		@Override
		@HaxeMethodBody("HaxeLimeRender.render(p0.floatData, p1, p2.data, p3, p4.data, p5);")
		native public void render(FastMemory vertices, int vertexCount, short[] indices, int indexCount, int[] batches, int batchCount);
	}

	static private class JTranscAudioLimeImpl implements JTranscAudio.Impl {
		@Override
		@HaxeMethodBody("return HaxeLimeAudio.createSound(p0._str);")
		native public int createSound(String path);

		@Override
		@HaxeMethodBody("HaxeLimeAudio.disposeSound(p0);")
		native public void disposeSound(int soundId);

		@Override
		@HaxeMethodBody("HaxeLimeAudio.playSound(p0);")
		native public void playSound(int soundId);
	}

	static private class JTranscIOLimeImpl implements JTranscIO.Impl {
		@Override
		@HaxeMethodBody("" +
			"var futureBytes = HaxeLimeAssets.loadBytes(p0._str);\n" +
			"futureBytes.onComplete(function(bytes) {\n" +
			"   p1.#METHOD:com.jtransc.media.JTranscCallback:handler#(null, HaxeArrayByte.fromBytes(bytes));\n" +
			"});\n" +
			"\n"
		)
		native public void readAsync(String path, JTranscCallback<byte[]> handler);

		@Override
		public void getResourceAsync(String path, JTranscCallback<byte[]> handler) {
			readAsync(path, handler);
		}
	}

	static private class JTranscSyncIOLimeImpl extends JTranscSyncIO.Impl {
		public JTranscSyncIOLimeImpl(JTranscSyncIO.Impl parent) {
			super(parent);
		}

		@Override
		public JTranscSyncIO.ImplStream open(String path, int mode) {
			//return _open("assets/" + path);
			return _open(path, mode);
		}

		@HaxeMethodBody("" +
			"var bytes = HaxeLimeAssets.getBytes(p0._str); // LIME >= 2.8\n" +
			"if (bytes == null) return null;\n" +
			"return #CONSTRUCTOR:com.jtransc.io.JTranscSyncIO$ByteStream:([B)V#(HaxeArrayByte.fromBytes(bytes));\n"
		)
		private JTranscSyncIO.ImplStream _open(String path, int mode) {
			return new JTranscSyncIO.ByteStream(new byte[0]);
		}
	}
}


