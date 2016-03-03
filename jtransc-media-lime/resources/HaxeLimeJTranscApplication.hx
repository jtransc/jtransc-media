import jtransc.media.JTranscInput_;

class HaxeLimeJTranscApplication extends lime.app.Application {
    static public var instance:HaxeLimeJTranscApplication;
    static public var initHandler: Void -> Void;
    static public var updateHandler: Void -> Void;
    static public var renderHandler: Void -> Void;

    static public function loopInit(init: Void -> Void) {
        HaxeLimeJTranscApplication.initHandler = init;
    }

    static public function loopLoop(update: Void -> Void, render: Void -> Void) {
        HaxeLimeJTranscApplication.updateHandler = update;
        HaxeLimeJTranscApplication.renderHandler = render;
    }

    public override function onPreloadComplete():Void {
        //switch (renderer.context) {
        //	case FLASH(sprite): #if flash initializeFlash(sprite); #end
        //	case OPENGL (gl):
        //	default:
        //	throw "Unsupported render context";
        //}
    }

    private var initialized = false;
    private var initializedRenderer = false;
    public override function render(renderer:lime.graphics.Renderer) {
        super.render(renderer);
        if (!initializedRenderer) {
            initializedRenderer = true;
            HaxeLimeRender.setRenderer(renderer);
        }
        HaxeLimeRender.setSize(window.width, window.height);
        if (HaxeLimeRender.isInitialized()) {
            if (!initialized && HaxeLimeJTranscApplication.initHandler != null) {
                initialized = true;
                HaxeLimeJTranscApplication.initHandler();
            }
            if (HaxeLimeJTranscApplication.renderHandler != null) HaxeLimeJTranscApplication.renderHandler();
        }
    }

    public override function update(deltaTime:Int) {
        super.update(deltaTime);
        if (HaxeLimeRender.isInitialized()) {
            if (HaxeLimeJTranscApplication.updateHandler != null) HaxeLimeJTranscApplication.updateHandler();
        }
    }

    public function new() {
        super();
        HaxeLimeJTranscApplication.instance = this;
        addModule(new JTranscModule());
    }
}

class JTranscModule extends lime.app.Module {
    override public function onMouseUp (window:lime.ui.Window, x:Float, y:Float, button:Int):Void {
		JTranscInput_.__hx_static__init__();
        JTranscInput_.mouseInfo.x = Std.int(x);
        JTranscInput_.mouseInfo.y = Std.int(y);
        JTranscInput_.mouseInfo.buttons &= ~(1 << button);
        JTranscInput_.impl.onMouseUp_Ljtransc_media_JTranscInput_MouseInfo__V(JTranscInput_.mouseInfo);
    }
    override public function onMouseDown (window:lime.ui.Window, x:Float, y:Float, button:Int):Void {
		JTranscInput_.__hx_static__init__();
        JTranscInput_.mouseInfo.x = Std.int(x);
        JTranscInput_.mouseInfo.y = Std.int(y);
        JTranscInput_.mouseInfo.buttons |= 1 << button;
        JTranscInput_.impl.onMouseDown_Ljtransc_media_JTranscInput_MouseInfo__V(JTranscInput_.mouseInfo);
    }
    override public function onMouseMove (window:lime.ui.Window, x:Float, y:Float):Void {
		JTranscInput_.__hx_static__init__();
        JTranscInput_.mouseInfo.x = Std.int(x);
        JTranscInput_.mouseInfo.y = Std.int(y);
        JTranscInput_.impl.onMouseMove_Ljtransc_media_JTranscInput_MouseInfo__V(JTranscInput_.mouseInfo);
    }
}