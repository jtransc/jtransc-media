import flash.display.Sprite;
import lime.app.Application;
import lime.graphics.RenderContext;
import lime.graphics.Renderer;
import lime.graphics.FlashRenderContext;
import lime.graphics.GLRenderContext;

class HaxeLimeRender {
    static public HaxeLimeRenderImpl impl;
    static public int width = 640;
    static public int height = 480;

    static public void setRenderer(Renderer renderer) {
        if (HaxeLimeRender.impl != null) return;

        RenderContext context = renderer.context;

        if (context instanceof RenderContext.FLASH) {
            HaxeLimeRender.impl = new HaxeLimeRenderFlash((Sprite)(Object) ((RenderContext.FLASH) context).stage);
        } else if (context instanceof RenderContext.OPENGL) {
            HaxeLimeRender.impl = new HaxeLimeRenderGL(((RenderContext.OPENGL) context).gl);
        } else {
            throw new RuntimeException("Not supported renderer $renderer");
        }
    }

    static public boolean isInitialized() {
        return HaxeLimeRender.impl != null && HaxeLimeRender.impl.isInitialized();
    }

    static public void setSize(int width, int height) {
        HaxeLimeRender.width = width;
        HaxeLimeRender.height = height;
    }

    static public int createTexture(String path, int width, int height) {
        return impl.createTexture(path, width, height);
    }

    static public void disposeTexture(int id) {
        impl.disposeTexture(id);
    }

    static public void render(
            float[] _vertices, int vertexCount,
            char[] _indices, int indexCount,
            int[] _batches, int batchCount
    ) {
        impl.render(
                width, height,
                _vertices, vertexCount,
                _indices, indexCount,
                _batches, batchCount
        );
    }
}