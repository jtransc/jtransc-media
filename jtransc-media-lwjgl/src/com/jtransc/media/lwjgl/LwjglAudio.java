package com.jtransc.media.lwjgl;

import com.jtransc.ds.IntStack;
import com.jtransc.media.JTranscAudio;

public class LwjglAudio implements JTranscAudio.Impl {
	IntStack audioIds = new IntStack(2048);
	//WrappedSound[] sounds = new WrappedSound[2048];

	public LwjglAudio() {
		for (int n = 2047; n >= 0; n--) audioIds.push(n);
	}

	@Override
	public int createSound(String path) {
		int soundId = audioIds.pop();
		//FileHandle fileHandle = Gdx.files.internal(path);
		//Sound sound = null;
		//try {
		//	sound = Gdx.audio.newSound(fileHandle);
		//} catch (RuntimeException e) {
		//	e.printStackTrace();
		//}
		//sounds[soundId] = new WrappedSound(sound);
		return soundId;
	}

	@Override
	public void disposeSound(int soundId) {
		//sounds[soundId].dispose();
		//sounds[soundId] = null;
		audioIds.push(soundId);
	}

	@Override
	public void playSound(int soundId) {
		//sounds[soundId].play();
	}

//	static public class WrappedSound {
//		public Sound sound;
//
//		public WrappedSound(Sound sound) {
//			this.sound = sound;
//		}
//
//		public void dispose() {
//			if (sound != null) sound.dispose();
//			sound = null;
//		}
//
//		public void play() {
//			if (sound != null) sound.play();
//		}
//	}
}
