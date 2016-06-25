package com.jtransc.media.lime;

import com.jtransc.FastMemory;
import com.jtransc.annotation.JTranscAddFile;
import com.jtransc.annotation.JTranscMethodBody;
import com.jtransc.annotation.JTranscRegisterCommand;
import com.jtransc.annotation.JTranscRunCommand;
import com.jtransc.annotation.haxe.*;
import com.jtransc.io.JTranscSyncIO;
import com.jtransc.experimental.kotlin.JTranscKotlinReflectStripper;
import com.jtransc.media.*;

import java.util.Locale;

@HaxeAddFilesTemplate({
	"AGALMiniAssembler.hx",
	"HaxeLimeAssets.hx",
	"HaxeLimeAudio.hx",
	"HaxeLimeJTranscApplication.hx",
	"HaxeLimeRender.hx",
	"HaxeLimeRenderFlash.hx",
	"HaxeLimeRenderGL.hx",
	"HaxeLimeRenderImpl.hx",
	"HaxeLimeIO.hx",
	"HaxeLimeLanguage.hx"
})
@HaxeAddFilesBeforeBuildTemplate({
	"program.xml"
})
@HaxeCustomMain("" +
	"package {{ entryPointPackage }};\n" +
	"class {{ entryPointSimpleName }} extends HaxeLimeJTranscApplication {\n" +
	"    public function new() {\n" +
	"        super();\n" +
	"        {{ inits }}\n" +
	"        {{ mainClass }}.{{ mainMethod }}(HaxeNatives.strArray(HaxeNatives.args()));\n" +
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
	"@limebuild.cmd"
})
@HaxeCustomBuildAndRunCommandLine({
	"@limetest.cmd"
})
@HaxeAddLibraries({
	"lime:2.9.1"
})
@JTranscAddFile(target = "js", priority = -3004, process = true, prepend = "js/media_electron.js")
@JTranscAddFile(target = "js", priority = -3003, process = true, prepend = "js/media_polyfills.js")
@JTranscAddFile(target = "js", priority = -3002, process = true, prepend = "js/media_utils.js")
//@JTranscAddFile(target = "js", priority = -3001, process = true, prepend = "js/libgdx_keys.js")
@JTranscAddFile(target = "js", priority = -3000, process = true, prepend = "js/media.js")
@JTranscAddFile(target = "js", process = true, src = "js/template/index.html", dst = "index.html")
@JTranscAddFile(target = "js", process = true, src = "js/template/electron-main.js", dst = "electron-main.js")
//@JTranscAddFile(target = "all", process = false, isAsset = true, src = "com/badlogic/gdx/utils/arial-15.fnt", dst = "com/badlogic/gdx/utils/arial-15.fnt")
//@JTranscAddFile(target = "all", process = false, isAsset = true, src = "com/badlogic/gdx/utils/arial-15.png", dst = "com/badlogic/gdx/utils/arial-15.png")
@JTranscRegisterCommand(target = "js", name = "electron", command = "electron", check = { "electron", "--version" }, getFolderCmd = { "npm", "list", "-g" }, install = {"npm", "-g", "install", "electron-prebuilt" })
@JTranscRunCommand(target = "js", value = { "electron", "{{ outputFolder }}/electron-main.js" })
public class JTranscLime {
	static public void init() {
		JTranscKotlinReflectStripper.init();
		JTranscRender.impl = new JTranscRenderLimeImpl();
		JTranscAudio.impl = new JTranscAudioLimeImpl();
		JTranscIO.impl = new JTranscIOLimeImpl();
		JTranscEventLoop.impl = new JTranscEventLoopLimeImpl();
		JTranscSyncIO.impl = new JTranscSyncIOLimeImpl(JTranscSyncIO.impl);
		JTranscWindow.impl = new JTranscWindowImpl();
		//Locale.setDefault(new Locale(Utils.getLanguage()));
	}

	static public class JTranscWindowImpl extends JTranscWindow.Impl {
		@Override
		@JTranscMethodBody(target = "js", value = "libgdx.setTitle(N.istr(p0));")
		public void setTitle(String title) {
			super.setTitle(title);
		}

		@Override
		@JTranscMethodBody(target = "js", value = "libgdx.setSize(p0, p1);")
		public void setSize(int width, int height) {
			super.setSize(width, height);
		}

		@Override
		@JTranscMethodBody(target = "js", value = "libgdx.show();")
		public void show() {
			super.show();
		}
	}

	static public class Utils {
		@HaxeMethodBody("return N.str(HaxeLimeLanguage.getLanguage());")
		@JTranscMethodBody(target = "js", value = "return N.strLit('english');")
		native static public String getLanguage();
	}

	static private class JTranscEventLoopLimeImpl implements JTranscEventLoop.Impl {
		@Override
		@HaxeMethodBody("HaxeLimeJTranscApplication.loopInit(function() { p0.{% METHOD java.lang.Runnable:run %}(); });")
		@JTranscMethodBody(target = "js", value = "Media.EventLoop.loopInit(function() { p0['{% METHOD java.lang.Runnable:run %}'](); });")
		native public void init(Runnable init);

		@Override
		@HaxeMethodBody("HaxeLimeJTranscApplication.loopLoop(function() { p0.{% METHOD java.lang.Runnable:run %}(); }, function() { p1.{% METHOD java.lang.Runnable:run %}(); });")
		@JTranscMethodBody(target = "js", value = "Media.EventLoop.loopLoop(function() { p0['{% METHOD java.lang.Runnable:run %}'](); }, function() { p1['{% METHOD java.lang.Runnable:run %}'](); });")
		native public void loop(Runnable update, Runnable render);
	}

	static private class JTranscRenderLimeImpl implements JTranscRender.Impl {
		@Override
		@HaxeMethodBody("return HaxeLimeRender.createTexture(p0._str, p1, p2);")
		@JTranscMethodBody(target = "js", value = "return Media.Texture.create(N.istr(p0), p1, p2, p3);")
		native public int createTexture(String path, int width, int height, boolean mipmaps);

		@Override
		// haxe.io.Int32Array
		@HaxeMethodBody("return HaxeLimeRender.createTextureMemory(p0.data, p1, p2, p3);")
		@JTranscMethodBody(target = "js", value = "return Media.Texture.createMemory(p0.data, p1, p2, p3, p4);")
		native public int createTextureMemory(int[] data, int width, int height, int format, boolean mipmaps);

		@Override
		@JTranscMethodBody(target = "js", value = "return Media.Texture.createEncoded(p0.data, p1, p2, p3);")
		native public int createTextureEncoded(byte[] data, int width, int height, boolean mipmaps);

		@Override
		@HaxeMethodBody("HaxeLimeRender.disposeTexture(p0);")
		@JTranscMethodBody(target = "js", value = "return Media.Texture.dispose(p0);")
		native public void disposeTexture(int textureId);

		@Override
		@HaxeMethodBody("HaxeLimeRender.render(p0.floatData, p1, p2.data, p3, p4.data, p5);")
		@JTranscMethodBody(target = "js", value = "return Media.Render.render(p0.f32, p1, p2.data, p3, p4.data, p5);")
		native public void render(FastMemory vertices, int vertexCount, short[] indices, int indexCount, int[] batches, int batchCount);
	}

	static private class JTranscAudioLimeImpl implements JTranscAudio.Impl {
		@Override
		@HaxeMethodBody("return HaxeLimeAudio.createSound(p0._str);")
		@JTranscMethodBody(target = "js", value = "return Media.Sound.create(N.istr(p0));")
		native public int createSound(String path);

		@Override
		@HaxeMethodBody("HaxeLimeAudio.disposeSound(p0);")
		@JTranscMethodBody(target = "js", value = "Media.Sound.dispose(p0);")
		native public void disposeSound(int soundId);

		@Override
		@HaxeMethodBody("HaxeLimeAudio.playSound(p0);")
		@JTranscMethodBody(target = "js", value = "Media.Sound.play(p0);")
		native public void playSound(int soundId);
	}

	static private class JTranscIOLimeImpl implements JTranscIO.Impl {
		@Override
		@HaxeMethodBody("" +
			"var futureBytes = HaxeLimeAssets.loadBytes(p0._str);\n" +
			"futureBytes.onComplete(function(bytes) {\n" +
			"   p1.{% METHOD com.jtransc.media.JTranscCallback:handler %}(null, HaxeArrayByte.fromBytes(bytes));\n" +
			"});\n" +
			"\n"
		)
		@JTranscMethodBody(target = "js", value = {
			"Media.IO.readBytesAsync(N.istr(p0), function(data) {",
			"   var ba = JA_B.fromTypedArray(data);",
			"   p1['{% METHOD com.jtransc.media.JTranscCallback:handler %}'](null, ba);",
			"});",
		})
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
			"return {% CONSTRUCTOR com.jtransc.io.JTranscSyncIO$ByteStream:([B)V %}(HaxeArrayByte.fromBytes(bytes));\n"
		)
		native private JTranscSyncIO.ImplStream _open(String path, int mode);
	}
}


