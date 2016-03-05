import lime.audio.AudioSource;

class HaxeLimeAudio {
    private var ids:Array<Int>;
    private var sources:Array<SoundWrapped>;

    public function new() {
        ids = [for (i in 1...1024) i];
        sources = [for (i in 1...1024) null];
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
    public var source:AudioSource;
    public var playOnComplete:Bool;

    public function new(path:String) {
        this.path = path;
        var future = HaxeLimeAssets.loadAudioBuffer(path);
        future.onComplete(function(buffer) {
            setSource(new AudioSource(buffer));
        });
    }

    private function setSource(source:AudioSource) {
        this.source = source;
        if (this.playOnComplete) {
            this.playOnComplete = false;
            this.source.play();
        }
    }

    public function play() {
        if (this.source != null) {
            this.source.play();
        } else {
            this.playOnComplete = true;
        }
    }

    public function dispose() {
        if (this.source != null) {
            this.source.dispose();
            this.source = null;
        }
    }
}