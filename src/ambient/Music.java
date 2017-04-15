package ambient;

import engine.Math3D;

import javax.sound.sampled.*;
import java.io.File;

public class Music {
	
	public static final Music BGMUSIC = new Music("nightwishTaikatalvi.wav", 240000, 6854927, true, -10, 0);
	// keep volume between -25 and 0 (except the background music)
	
	private File file;
	private int start, end;
	private boolean loop;
	private double volume, balance;
	// volume from -80 to 6 inclusive
	// balance from -1 to 1 inclusive
	
	public static void main(String[] arg) throws InterruptedException {
		Music test = BGMUSIC;
		System.out.println(test.file.getAbsolutePath());
		System.out.println(test.getClip().getFrameLength());
		test.start = 240000;
		test.end = test.start + 6000000;
		test.loop = true;
		test.play(0, 0);
		Thread.sleep(6000);
	}
	
	Music(String fileName, int start, int end, boolean loop, double volume, double balance) {
		file = new File("sounds/" + fileName);
		this.start = start;
		this.end = end;
		this.loop = loop;
		this.volume = volume;
		this.balance = balance;
	}
	
	private Clip getClip() {
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(file);
			AudioFormat format = audio.getFormat();
			if (format.getChannels() == 1) {
				format = new AudioFormat(format.getSampleRate(),
						format.getSampleSizeInBits(), 2, true, false);
				audio = AudioSystem.getAudioInputStream(format, audio);
			}
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
		play(0, 0);
	}
	
	public void play(double dx, double dy) {
		double d = Math3D.magnitude(dx, dy);
		if (d < 50)
			d = 50;
		double volume = 50 - Math.log(d * d) * 5;
		if (volume < -55)
			volume = -55;
		double balance = dx / 350f;
		if (balance > 1)
			balance = 1;
		if (balance < -1)
			balance = -1;
		playHelper(volume, balance);
	}
	
	private void playHelper(double volume, double balance) {
		Clip clip = getClip();
		
		((FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue((float) (volume + this.volume));
		((FloatControl) clip.getControl(FloatControl.Type.PAN)).setValue((float) (balance + this.balance));
		
		if (loop) {
			clip.setFramePosition(start);
			clip.setLoopPoints(start, end);
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			clip.setFramePosition(start);
			clip.start();
		}
	}
}