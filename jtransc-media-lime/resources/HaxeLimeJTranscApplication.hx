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
            {% SMETHOD com.jtransc.media.JTranscWindow:setScreenSize %}(window.width, window.height);
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
        {% SMETHOD com.jtransc.media.JTranscWindow:setScreenSize %}(width, height);
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
        {% SINIT com.jtransc.media.JTranscInput %}
        addModule(new JTranscModule());
    }
}

class JTranscModule extends lime.app.Module {
	private function mouseInfo() return {% SFIELD com.jtransc.media.JTranscInput:mouseInfo %};
	private function keyInfo() return {% SFIELD com.jtransc.media.JTranscInput:keyInfo %};
	private function inputImpl() return {% SFIELD com.jtransc.media.JTranscInput:impl %};

    override public function onMouseUp(window:Window, x:Float, y:Float, button:Int):Void {
        mouseInfo().{% METHOD com.jtransc.media.JTranscInput$MouseInfo:setScreenXY %}(Std.int(x), Std.int(y));
        mouseInfo().{% FIELD com.jtransc.media.JTranscInput$MouseInfo:buttons %} &= ~(1 << button);
        inputImpl().{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseUp %}(mouseInfo());
    }

    override public function onMouseWheel (window:Window, deltaX:Float, deltaY:Float):Void {
        inputImpl().{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseWheel %}(Std.int(deltaY));
    }

    override public function onMouseDown(window:Window, x:Float, y:Float, button:Int):Void {
        mouseInfo().{% METHOD com.jtransc.media.JTranscInput$MouseInfo:setScreenXY %}(Std.int(x), Std.int(y));
        mouseInfo().{% FIELD com.jtransc.media.JTranscInput$MouseInfo:buttons %} |= 1 << button;
        inputImpl().{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseDown %}(mouseInfo());
    }

    override public function onMouseMove(window:Window, x:Float, y:Float):Void {
        mouseInfo().{% METHOD com.jtransc.media.JTranscInput$MouseInfo:setScreenXY %}(Std.int(x), Std.int(y));
        inputImpl().{% METHOD com.jtransc.media.JTranscInput$Handler:onMouseMove %}(mouseInfo());
    }

	override public function onKeyDown(window:Window, keyCode:KeyCode, modifier:lime.ui.KeyModifier):Void {
		keyInfo().{% FIELD com.jtransc.media.JTranscInput$KeyInfo:keyCode %} = keyCode;
		inputImpl().{% METHOD com.jtransc.media.JTranscInput$Handler:onKeyDown %}(keyInfo());
	}

	override public function onKeyUp(window:Window, keyCode:KeyCode, modifier:lime.ui.KeyModifier):Void {
        keyInfo().{% FIELD com.jtransc.media.JTranscInput$KeyInfo:keyCode %} = keyCode;
		inputImpl().{% METHOD com.jtransc.media.JTranscInput$Handler:onKeyUp %}(keyInfo());
	}
}