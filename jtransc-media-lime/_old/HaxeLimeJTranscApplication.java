import lime.graphics.FlashRenderContext;
import lime.graphics.GLRenderContext;
import lime.graphics.RenderContext;

public class HaxeLimeJTranscApplication extends lime.app.Application {
    @Override
    public void onPreloadComplete() {
    }

    private boolean initialized = false;
    private boolean initializedRenderer = false;

    @Override
    public void render(lime.graphics.Renderer renderer) {
        super.render(renderer);
        if (!initializedRenderer) {
            initializedRenderer = true;
            HaxeLimeRender.setRenderer(renderer);
        }
        HaxeLimeRender.setSize(window.width, window.height);
        if (HaxeLimeRender.isInitialized()) {
            if (!initialized && N.initHandler != null) {
                initialized = true;
				N.initHandler();
            }
            if (N.renderHandler != null) N.renderHandler();
        }
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        if (HaxeLimeRender.isInitialized()) {
            if (N.updateHandler != null) N.updateHandler();
        }
    }

    public HaxeLimeJTranscApplication() {
        super();
		N.enabledDefaultEventLoop = false;
        addModule(new JTranscModule());
    }
}

