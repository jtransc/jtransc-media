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
            if (!initialized && HaxeNatives.initHandler != null) {
                initialized = true;
                HaxeNatives.initHandler();
            }
            if (HaxeNatives.renderHandler != null) HaxeNatives.renderHandler();
        }
    }

    @Override
    public void update(int deltaTime) {
        super.update(deltaTime);
        if (HaxeLimeRender.isInitialized()) {
            if (HaxeNatives.updateHandler != null) HaxeNatives.updateHandler();
        }
    }

    public HaxeLimeJTranscApplication() {
        super();
        HaxeNatives.enabledDefaultEventLoop = false;
        addModule(new JTranscModule());
    }
}

