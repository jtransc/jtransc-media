import lime.utils.Bytes;
import lime.graphics.Image;
import lime.audio.AudioBuffer;
import lime.app.Future;

class HaxeLimeAssets {
	// @TODO: HACK!
	static public function fixpath(path:String) {
		//return 'assets/$path';
		var pathWithoutAssetsFolder = path;
		while (pathWithoutAssetsFolder.indexOf('assets/') == 0) {
			pathWithoutAssetsFolder = pathWithoutAssetsFolder.substr(7);
		}
		return 'assets/' + pathWithoutAssetsFolder;
	}

	// Synchronous!
	static public function getBytes(path:String) {
		return lime.Assets.getBytes(fixpath(path));
	}

	static public function getImage(path:String) {
		return lime.Assets.getImage(fixpath(path));
	}

	static public function getAudioBuffer(path:String) {
		return lime.Assets.getAudioBuffer(fixpath(path));
	}

	static public function loadBytes(path:String):Future<Bytes> {
		return lime.Assets.loadBytes(fixpath(path));
	}

	static public function loadImage(path:String):Future<Image> {
		return lime.Assets.loadImage(fixpath(path));
	}

	static public function loadAudioBuffer(path:String):Future<AudioBuffer> {
		return lime.Assets.loadAudioBuffer(fixpath(path));
	}
}