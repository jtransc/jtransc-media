import jtransc.FastMemory;
import jtransc.annotation.haxe.HaxeAddFiles;
import jtransc.annotation.haxe.HaxeAddLibraries;
import jtransc.annotation.haxe.HaxeCustomMain;
import jtransc.annotation.haxe.HaxeMethodBody;
import jtransc.media.*;

@HaxeAddFiles({
	"AGALMiniAssembler.hx",
	"HaxeLimeAudio.hx",
	"HaxeLimeJTranscApplication.hx",
	"HaxeLimeRender.hx",
	"HaxeLimeRenderFlash.hx",
	"HaxeLimeRenderGL.hx",
	"HaxeLimeRenderImpl.hx",
	"HaxeLimeIO.hx"
})
@HaxeCustomMain(
	"package $entryPointPackage;\n" +
		"class $entryPointSimpleName extends HaxeLimeJTranscApplication {\n" +
		"    public function new() {\n" +
		"        super();\n" +
		"        $inits\n" +
		"        $mainClass.$mainMethod(HaxeNatives.strArray(HaxeNatives.args()));\n" +
		"    }\n" +
		"}\n"
)
@HaxeAddLibraries({ "lime:2.9.0" })
public class JTranscLime {
	static public void init() {
		JTranscRender.impl = new JTranscRenderLimeImpl();
		JTranscAudio.impl = new JTranscAudioLimeImpl();
		JTranscIO.impl = new JTranscIOLimeImpl();
		JTranscEventLoop.impl = new JTranscEventLoopLimeIml();
	}

	static public class JTranscEventLoopLimeIml implements JTranscEventLoop.Impl {
		@Override
		@HaxeMethodBody("HaxeLimeJTranscApplication.loopInit(p0.run__V);")
		native public void init(Runnable init);

		@Override
		@HaxeMethodBody("HaxeLimeJTranscApplication.loopLoop(p0.run__V, p1.run__V);")
		native public void loop(Runnable update, Runnable render);
	}

	static public class JTranscRenderLimeImpl implements JTranscRender.Impl {
		@Override
		@HaxeMethodBody("return HaxeLimeRender.createTexture(p0._str, p1, p2);")
		native public int createTexture(String path, int width, int height);

		@Override
		native public int createTextureMemory(int[] data, int width, int height, int format);

		@Override
		native public int createTextureEncoded(byte[] data, int width, int height);

		@Override
		@HaxeMethodBody("HaxeLimeRender.disposeTexture(p0);")
		native public void disposeTexture(int textureId);

		@Override
		@HaxeMethodBody("HaxeLimeRender.render(p0.floatData, p1, p2.data, p3, p4.data, p5);")
		native public void render(FastMemory vertices, int vertexCount, short[] indices, int indexCount, int[] batches, int batchCount);
	}

	static public class JTranscAudioLimeImpl implements JTranscAudio.Impl {
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

	static public class JTranscIOLimeImpl implements JTranscIO.Impl {
		@Override
		@HaxeMethodBody(
			"var bytes = lime.Assets.getBytes(p0._str); // LIME >= 2.8\n" +
				"//var byteArray = lime.Assets.getBytes(p0._str);\n" +
				"//var bytes = haxe.io.Bytes.alloc(byteArray.length);\n" +
				"//for (n in 0 ... byteArray.length) bytes.set(n, byteArray.__get(n));\n" +
				"p1.handler_Ljava_lang_Throwable_Ljava_lang_Object__V(null, HaxeByteArray.fromBytes(bytes));\n"
		)
		native public void readAsync(String path, JTranscCallback<byte[]> handler);
	}
}


