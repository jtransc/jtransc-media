import jtransc.IntStack;
import lime.audio.AudioSource;
import lime.Assets;

public class HaxeLimeAudio {
    private IntStack ids;
    private AudioSource[] sources;

    public HaxeLimeAudio() {
        this.ids = new IntStack();
        this.sources = new AudioSource[2048];
        for (int n = 0; n < this.sources.length; n++) {
            this.ids.push(n);
            this.sources[n] = null;
        }
    }

    static private HaxeLimeAudio instance;

    static private HaxeLimeAudio getInstance() {
        if (instance == null) instance = new HaxeLimeAudio();
        return instance;
    }


    static public void createSound(String path) {
        HaxeLimeAudio instance = getInstance();
        int soundId = instance.ids.pop();
        instance.sources[soundId] = new AudioSource(Assets.getAudioBuffer(path));
        return soundId;
    }

    static public void disposeSound(int soundId) {
        HaxeLimeAudio instance = getInstance();
        instance.sources[soundId].dispose();
        instance.sources[soundId] = null;
        instance.ids.push(soundId);
    }

    static public void playSound(int soundId) {
        HaxeLimeAudio instance = getInstance();
        instance.sources[soundId].play();
    }
}