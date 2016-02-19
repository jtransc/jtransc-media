import lime.app.Application;
import lime.graphics.Renderer;
import lime.graphics.FlashRenderContext;
import lime.graphics.GLRenderContext;

class HaxeLimeRender {
    static public HaxeLimeRenderImpl impl;
    static public int width = 640;
    static public int height = 480;

    static public void setRenderer(Renderer renderer) {
        if (HaxeLimeRender.impl != null) return;
        /*
        HaxeLimeRender.impl = switch (renderer.context) {
            #if flash
            case FLASH(sprite): new HaxeLimeRenderFlash(sprite);
                #else
            case OPENGL(gl): new HaxeLimeRenderGL(gl);
                #end
            default: throw 'Not supported renderer $renderer';
        }
        */
        throw new RuntimeException("Much implement this!")
    }

    static public boolean isInitialized() {
        if (HaxeLimeRender.impl == null) return false;
        return HaxeLimeRender.impl.isInitialized();
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