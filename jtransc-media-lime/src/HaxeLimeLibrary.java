import jtransc.JTranscEventLoop;
import jtransc.annotation.JTranscIncludeFiles;
import jtransc.annotation.JTranscMain;
import jtransc.annotation.JTranscMappings;

@JTranscIncludeFiles({
	"HaxeLimeAudio.hx",
	"HaxeLimeJTranscApplication.hx",
	"HaxeLimeRender.hx",
	"HaxeLimeRenderFlash.hx",
	"HaxeLimeRenderGL.hx",
	"HaxeLimeRenderImpl.hx",
	"HaxeLimeIO.hx"
})
@JTranscMain("HaxeLimeMain.hx.template")
@JTranscMappings("HaxeLimeMappings.xml")
public class HaxeLimeLibrary {
	static public void init() {
		JTranscEventLoop.impl = new JTranscEventLoop.Impl() {
			@Override
			public void init(Runnable init) {
			}

			@Override
			public void loop(Runnable update, Runnable render) {
			}
		};
	}
}
