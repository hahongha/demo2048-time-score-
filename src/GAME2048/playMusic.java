package GAME2048;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class playMusic{
	private AudioInputStream inputStream;
	private static Clip clip;
	private boolean checkMusic = false;
	private float currentVolume =0;
	private float previousVolume =0;
	FloatControl fc;
	String other;
	private boolean mute = false;
	public playMusic(String location) {
			run(location);
	}

	public void run(String location ) {
		File file = new File(location);
		try {
			inputStream = AudioSystem.getAudioInputStream(file.getAbsoluteFile());
			clip = AudioSystem.getClip();
			clip.open(inputStream);
			fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			clip.start();
			System.out.println("check "+location );
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setCheckMusic(true);
	}

	protected boolean isCheckMusic() {
		return checkMusic;
	}

	protected void setCheckMusic(boolean checkMusic) {
		this.checkMusic = checkMusic;
	}
	protected void changeMusic() {
		if (isCheckMusic() == false) {
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
			setCheckMusic(true);
		} else {
			clip.stop();
			setCheckMusic(false);
		}
	}
	protected void stopMusic() {
		clip.stop();
		setCheckMusic(false);
	}
	protected void playM() {
		clip.start();
		setCheckMusic(true);
	}
	protected void closeMusic() {
		clip.stop();
		clip.close();
		setCheckMusic(false);
	}
	protected void volumeUp() {
		currentVolume += 1.0f;
		if(currentVolume > 6.0f) {
			currentVolume = 6.0f;
		}
		fc.setValue(currentVolume);
//		System.out.println(currentVolume);
	}
	protected void volumeDown() {
		currentVolume -= 5f;
		if(currentVolume < -80.0f) {
			currentVolume = -80.0f;
		}
		fc.setValue(currentVolume);
//		System.out.println(currentVolume);
	}
	protected void volumeMute() {
		if(!mute) {
			previousVolume = currentVolume;
			currentVolume = -80.0f;
			fc.setValue(currentVolume);
			mute = true;
		}else {
			currentVolume= previousVolume;
			fc.setValue(currentVolume);
			mute = false;
		}
	}
	protected void resetAudioStream(String other){
		closeMusic();
		run(other);
	}
    
}
