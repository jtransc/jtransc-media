public class JTranscModule extends lime.app.Module {
    @Override
    public void onMouseUp (lime.ui.Window window, double x, double y, int button) {
        jtransc.JTranscInput.mouseInfo.x = (int)(x);
        jtransc.JTranscInput.mouseInfo.y = (int)(y);
        jtransc.JTranscInput.mouseInfo.buttons &= ~(1 << button);
        jtransc.JTranscInput.impl.onMouseUp(jtransc.JTranscInput.mouseInfo);
    }

    @Override
    public void onMouseDown (lime.ui.Window window, double x, double y, int button) {
        jtransc.JTranscInput.mouseInfo.x = (int)(x);
        jtransc.JTranscInput.mouseInfo.y = (int)(y);
        jtransc.JTranscInput.mouseInfo.buttons |= 1 << button;
        jtransc.JTranscInput.impl.onMouseDown(jtransc.JTranscInput.mouseInfo);
    }
    @Override
    public void onMouseMove (lime.ui.Window window, double x, double y) {
        jtransc.JTranscInput.mouseInfo.x = (int)(x);
        jtransc.JTranscInput.mouseInfo.y = (int)(y);
        jtransc.JTranscInput.impl.onMouseMove(jtransc.JTranscInput.mouseInfo);
    }
}