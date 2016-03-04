class HaxeLimeRenderImpl {
    static public inline var BLEND_INVALID:Int = -1;
    static public inline var BLEND_AUTO:Int = 0;
    static public inline var BLEND_NORMAL:Int = 1;
    static public inline var BLEND_MULTIPLY:Int = 3;
    static public inline var BLEND_SCREEN:Int = 4;
    static public inline var BLEND_ADD:Int = 8;
    static public inline var BLEND_ERASE:Int = 12;
    static public inline var BLEND_NONE:Int = 15;
    static public inline var BLEND_BELOW:Int = 16;
    static public inline var BLEND_MAX:Int = 17;

    public function getVirtualActualWidth() {
        return jtransc.media.JTranscWindow_.getVirtualActualWidth__D();
    }

    public function getVirtualActualHeight() {
        return jtransc.media.JTranscWindow_.getVirtualActualHeight__D();
    }

    public function getScreenWidth() {
        return jtransc.media.JTranscWindow_.getScreenWidth__D();
    }

    public function getScreenHeight() {
        return jtransc.media.JTranscWindow_.getScreenHeight__D();
    }

    public function createTexture(path:String, width:Int, height:Int):Int {
        return -1;
    }

    public function createTextureMemory(data:haxe.io.Int32Array, width:Int, height:Int, format:Int):Int {
        return -1;
    }

    public function isInitialized():Bool {
        return true;
    }

    public function disposeTexture(id:Int):Void {

    }

    public function render(
        _vertices:haxe.io.Float32Array, vertexCount:Int,
        _indices:haxe.io.UInt16Array, indexCount:Int,
        _batches:haxe.io.Int32Array, batchCount:Int
    ):Void {

    }
}