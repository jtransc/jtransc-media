import jtransc.JTranscInput;
import lime.app.Module;
import lime.ui.Window;

public class JTranscModule extends Module {
	@Override
	public void onMouseUp(Window window, double x, double y, int button) {
		JTranscInput.mouseInfo.x = (int) (x);
		JTranscInput.mouseInfo.y = (int) (y);
		JTranscInput.mouseInfo.buttons &= ~(1 << button);
		JTranscInput.impl.onMouseUp(JTranscInput.mouseInfo);
	}

	@Override
	public void onMouseDown(Window window, double x, double y, int button) {
		JTranscInput.mouseInfo.x = (int) (x);
		JTranscInput.mouseInfo.y = (int) (y);
		JTranscInput.mouseInfo.buttons |= 1 << button;
		JTranscInput.impl.onMouseDown(JTranscInput.mouseInfo);
	}

	@Override
	public void onMouseMove(Window window, double x, double y) {
		JTranscInput.mouseInfo.x = (int) (x);
		JTranscInput.mouseInfo.y = (int) (y);
		JTranscInput.impl.onMouseMove(JTranscInput.mouseInfo);
	}
}