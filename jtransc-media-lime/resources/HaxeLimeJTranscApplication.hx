import jtransc.media.JTranscInput_;

import lime.ui.Window;
import lime.ui.KeyCode;
import lime.graphics.Renderer;

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
    public override function render(renderer:Renderer) {
        super.render(renderer);
        if (!initializedRenderer) {
            initializedRenderer = true;
            HaxeLimeRender.setRenderer(renderer);
            jtransc.media.JTranscWindow_.setScreenSize_II_V(window.width, window.height);
        }
        if (HaxeLimeRender.isInitialized()) {
            if (!initialized && HaxeLimeJTranscApplication.initHandler != null) {
                initialized = true;
                HaxeLimeJTranscApplication.initHandler();
            }
            if (HaxeLimeJTranscApplication.renderHandler != null) HaxeLimeJTranscApplication.renderHandler();
        }
    }

	public override function onWindowResize(window:Window, width:Int, height:Int):Void {
		//jtransc.media.JTranscWindow_.dispatchResized__V();
        jtransc.media.JTranscWindow_.setScreenSize_II_V(width, height);
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
		JTranscInput_.SI();
        addModule(new JTranscModule());
    }
}

class JTranscModule extends lime.app.Module {
    override public function onMouseUp (window:Window, x:Float, y:Float, button:Int):Void {
        JTranscInput_.mouseInfo.setScreenXY_II_V(Std.int(x), Std.int(y));
        JTranscInput_.mouseInfo.buttons &= ~(1 << button);
        JTranscInput_.impl.onMouseUp_Ljtransc_media_JTranscInput_MouseInfo__V(JTranscInput_.mouseInfo);
    }
    override public function onMouseDown (window:Window, x:Float, y:Float, button:Int):Void {
        JTranscInput_.mouseInfo.setScreenXY_II_V(Std.int(x), Std.int(y));
        JTranscInput_.mouseInfo.buttons |= 1 << button;
        JTranscInput_.impl.onMouseDown_Ljtransc_media_JTranscInput_MouseInfo__V(JTranscInput_.mouseInfo);
    }
    override public function onMouseMove (window:Window, x:Float, y:Float):Void {
        JTranscInput_.mouseInfo.setScreenXY_II_V(Std.int(x), Std.int(y));
        JTranscInput_.impl.onMouseMove_Ljtransc_media_JTranscInput_MouseInfo__V(JTranscInput_.mouseInfo);
    }

	override public function onKeyDown(window:Window, keyCode:KeyCode, modifier:lime.ui.KeyModifier):Void {
		JTranscInput_.keyInfo.keyCode = keyCode;
		JTranscInput_.impl.onKeyDown_Ljtransc_media_JTranscInput_KeyInfo__V(JTranscInput_.keyInfo);
	}

	override public function onKeyUp(window:Window, keyCode:KeyCode, modifier:lime.ui.KeyModifier):Void {
        JTranscInput_.keyInfo.keyCode = keyCode;
		JTranscInput_.impl.onKeyUp_Ljtransc_media_JTranscInput_KeyInfo__V(JTranscInput_.keyInfo);
	}
}