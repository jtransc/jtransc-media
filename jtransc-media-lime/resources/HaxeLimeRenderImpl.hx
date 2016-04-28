import com.jtransc.media.JTranscWindow_;

class HaxeLimeRenderImpl {
    static public inline var MASK_NONE = 0;
    static public inline var MASK_SHAPE = 1;
    static public inline var MASK_CONTENT = 2;

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
        return JTranscWindow_.getVirtualActualWidth__D();
    }

    public function getVirtualActualHeight() {
        return JTranscWindow_.getVirtualActualHeight__D();
    }

    public function getScreenWidth() {
        return JTranscWindow_.getScreenWidth__D();
    }

    public function getScreenHeight() {
        return JTranscWindow_.getScreenHeight__D();
    }

    public function getVirtualScaleX() {
        return JTranscWindow_.getVirtualScaleX__D();
    }

    public function getVirtualScaleY() {
        return JTranscWindow_.getVirtualScaleY__D();
    }

    public function createTexture(path:String, width:Int, height:Int):Int {
        trace('HaxeLimeRenderImpl.createTexture($path)');
        //path = 'assets/image.png';
        //trace('HaxeLimeRenderGL.createTexture[2]($path)');
        return this._createTexture(null, HaxeLimeAssets.loadImage(path), width, height);
    }

    public function createTextureMemory(data:haxe.io.Int32Array, width:Int, height:Int, format:Int):Int {
        trace('HaxeLimeRenderImpl.createTextureMemory($width, $height)');
        var bytes = lime.utils.UInt8Array.fromBytes(data.view.buffer);
        var buffer = new lime.graphics.ImageBuffer(bytes, width, height);
        //trace(buffer);
        return this._createTexture(new lime.graphics.Image(buffer, 0, 0, width, height), null, width, height);
    }

    public function _createTexture(image:lime.graphics.Image, imageFuture:lime.app.Future<lime.graphics.Image>, width:Int, height:Int):Int {
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