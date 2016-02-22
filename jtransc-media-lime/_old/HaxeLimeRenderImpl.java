class HaxeLimeRenderImpl {
    static public final int BLEND_INVALID = -1;
    static public final int BLEND_AUTO = 0;
    static public final int BLEND_NORMAL = 1;
    static public final int BLEND_MULTIPLY = 3;
    static public final int BLEND_SCREEN = 4;
    static public final int BLEND_ADD = 8;
    static public final int BLEND_ERASE = 12;
    static public final int BLEND_NONE = 15;
    static public final int BLEND_BELOW = 16;
    static public final int BLEND_MAX = 17;

    public int createTexture(String path, int width, int height) {
        return -1;
    }

    public boolean isInitialized() {
        return true;
    }

    public void disposeTexture(int id) {

    }

    public void render(
            int width, int height,
            float[] _vertices, int vertexCount,
            char[] _indices, int indexCount,
            int[] _batches, int batchCount
    ) {

    }
}