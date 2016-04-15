import lime.audio.AudioSource;

class HaxeLimeAudio {
    private var ids:Array<Int>;
    private var sources:Array<SoundWrapped>;

    public function new() {
        ids = [for (i in 1...1024) i];
        sources = [for (i in 1...1024) null];

		#if js
        var script = cast(js.Browser.document.createElement('script'), js.html.ScriptElement);
        script.type = 'text/javascript';
        script.src = 'https://code.createjs.com/soundjs-0.6.2.min.js';
        js.Browser.document.body.appendChild(script);
        #end
    }

    static private var instance:HaxeLimeAudio;

    static private function getInstance():HaxeLimeAudio {
        if (instance == null) instance = new HaxeLimeAudio();
        return instance;
    }

    static public function createSound(path:String) {
        var instance = getInstance();
        var soundId = instance.ids.pop();
        instance.sources[soundId] = new SoundWrapped(path);
        return soundId;
    }

    static public function disposeSound(soundId:Int) {
        var instance = getInstance();
        if (instance.sources[soundId] != null) {
            instance.sources[soundId].dispose();
            instance.sources[soundId] = null;
            instance.ids.push(soundId);
        }
    }

    static public function playSound(soundId:Int) {
        var instance = getInstance();
        if (instance.sources[soundId] != null) {
            instance.sources[soundId].play();
        }
    }
}

class SoundWrapped {
    private var path:String;
    #if js
    	private var audio:js.html.Audio;
    #else
		public var source:AudioSource;
		public var playOnComplete:Bool;
    #end

    public function new(path:String) {
        this.path = path;
        #if js
        	//this.audio = new js.html.Audio(HaxeLimeAssets.fixpath(path));
        	untyped __js__('createjs.Sound.registerSound({0});', HaxeLimeAssets.fixpath(path));
        #else
			var future = HaxeLimeAssets.loadAudioBuffer(path);
			future.onComplete(function(buffer) {
				this.source = new AudioSource(buffer);
				if (this.playOnComplete) {
					this.playOnComplete = false;
					play();
				}
			});
        #end
    }

    public function play() {
    	#if js
	        //audio.play();
	        try {
	        	//var audio = new js.html.Audio(HaxeLimeAssets.fixpath(path));
	        	//audio.play();
	        	untyped __js__('createjs.Sound.play({0});', HaxeLimeAssets.fixpath(path));
	        } catch (e:Dynamic) {
	        	trace(e);
	        }
    	#else
			if (this.source != null) {
				trace('source play');
				this.source.play();
			} else {
				this.playOnComplete = true;
			}
        #end
    }

    public function dispose() {
    	#if js
    		this.audio = null;
    	#else
			if (this.source != null) {
				this.source.dispose();
				this.source = null;
			}
        #end
    }
}