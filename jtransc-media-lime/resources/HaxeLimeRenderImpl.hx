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

	private var screenWidth:Float = 640.0;
	private var screenHeight:Float = 480.0;
	private var virtualWidth:Float = 640.0;
	private var virtualHeight:Float = 480.0;
	private var virtualActualWidth:Float = 640.0;
	private var virtualActualHeight:Float = 480.0;
	private var virtualScaleX:Float = 1.0;
	private var virtualScaleY:Float = 1.0;

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

	public function setDisplayInfo(screenWidth:Float, screenHeight:Float, virtualWidth:Float, virtualHeight:Float, virtualActualWidth:Float, virtualActualHeight:Float, virtualScaleX:Float, virtualScaleY:Float) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.virtualWidth = virtualWidth;
		this.virtualHeight = virtualHeight;
		this.virtualActualWidth = virtualActualWidth;
		this.virtualActualHeight = virtualActualHeight;
		this.virtualScaleX = virtualScaleX;
		this.virtualScaleY = virtualScaleY;
	}

    public function render(
        _vertices:haxe.io.Float32Array, vertexCount:Int,
        _indices:haxe.io.UInt16Array, indexCount:Int,
        _batches:haxe.io.Int32Array, batchCount:Int
    ):Void {

    }
}