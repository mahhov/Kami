package ambient;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;

public class Music {
	
	public static final Music BGMUSIC = new Music("nightwishTaikatalvi", 240000, 6854927, true, -10);
	public static final Music HOOK = new Music("boom1", 0, 15645, false, -15);
	public static final Music ZIP = new Music("zip", 5000, 15000, false, 0);
	public static final Music WOOSH = new Music("woosh", 2500, 5500, false, 0);
	// keep volume between -25 and 0 (except the background music)
	
	private File file;
	private int start, end;
	private boolean loop;
	private double volume;
	private Clip clip;
	private FloatControl volumeControl;
	// volume from -80 to 6 inclusive
	// balance from -1 to 1 inclusive
	
	public static void main(String[] arg) throws InterruptedException {
		Music test = WOOSH;
		System.out.println(test.file.getAbsolutePath());
		System.out.println(test.getClip().getFrameLength());
		test.play();
		Thread.sleep(6000);
	}
	
	Music(String fileName, int start, int end, boolean loop, double volume) {
		file = new File("sounds/" + fileName + ".wav");
		this.start = start;
		this.end = end;
		this.loop = loop;
		this.volume = volume;
		clip = getClip();
		volumeControl = ((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN));
	}
	
	private Clip getClip() {
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(file); // todo: figure out how to not have to open audio file twice
			AudioFormat format = audio.getFormat();
			if (format.getChannels() == 1)
				format = new AudioFormat(format.getSampleRate(), format.getSampleSizeInBits(), 2, true, false);
			audio = new AudioInputStream(new FileInputStream(file), format, end + 1);
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			audio.close();
			return clip;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void play() {
		setVolume(volume);
		if (loop) {
			clip.setFramePosition(start);
			clip.setLoopPoints(start, end);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			clip.setFramePosition(start);
			clip.setLoopPoints(start, end);
			clip.start();
		}
	}
	
	public void setVolume(double volume) {
		volumeControl.setValue((float) volume);
	}
}