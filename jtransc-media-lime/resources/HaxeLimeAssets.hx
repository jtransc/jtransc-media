
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
}