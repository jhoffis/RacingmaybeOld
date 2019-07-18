package audio;

import java.util.Random;

public class RaceAudio {

	private Random r = new Random();
	private MediaAudio[] gear;
	private MediaAudio idle;
	private MediaAudio motor;
	private MediaAudio[] turbo;
	private MediaAudio redline;
	private MediaAudio nos;
	private String carname;

	public RaceAudio(String carname) {
		// Maybe use action for something later, cause it's awesome
		this.carname = carname;
		
		gear = new MediaAudio[4];
		turbo = new MediaAudio[2];
		
		for(int i = 0; i < gear.length; i++) {
			gear[i] = new MediaAudio("/sfx/gear" + (i+1));
		}
		
		for(int i = 0; i < turbo.length; i++) {
			turbo[i] = new MediaAudio("/sfx/turbosurge" + (i+1));
		}
		
		idle = new MediaAudio("/sfx/motorIdle" + carname);
		motor= new MediaAudio("/sfx/motorAcc" + carname);
		redline = new MediaAudio("/sfx/redline");
		nos = new MediaAudio("/sfx/nos");
	}

	public void motorIdle() {

		try {
			if (motor != null && motor.isPlaying()) {
				motor.stop();
			}

			idle.loop();
		} catch (Exception e) {

		}

	}

	//FIXME baser lyd p� turtall
	
	public void motorAcc() {
		if (idle != null && idle.isPlaying()) {
			motor.stop();
		}
		if (redline != null && redline.isPlaying()) {
			redline.stop();
		}

		motor.play();

		if (turbo != null && isMediaArrayPlaying(turbo)) {
			stopMediaArray(turbo);
		}
	}

	public void motorDcc() {

//		dcc = new MediaAudio("/sfx/motorDcc" + carname);
//		dcc.play();

		if (motor != null && motor.isPlaying()) {
			motor.stop();
		}
	}
	
	public void redline() {
		if (motor != null && motor.isPlaying()) {
			motor.stop();
		}
		
		redline.play();
	}

	public void nos() {
		nos.play();
	}

	public void turboSurge() {

		int nextSfx = 0;
		nextSfx = r.nextInt(2);
		
		turbo[nextSfx].play();

//		if (turbo != null && turbo.isPlaying())
//			turbo.stop();
	}

	public void gearSound() {
		int nextSfx = 0;
		nextSfx = r.nextInt(4);
		
		gear[nextSfx].play();
	}
	
	private boolean isMediaArrayPlaying(MediaAudio[] arr) {
		for(MediaAudio ma : arr) {
			if(ma.isPlaying())
				return true;
		}
		return false;
	}
	
	private void stopMediaArray(MediaAudio[] arr) {
		for(MediaAudio ma : arr) {
			ma.stop();
		}
	}

	public void stopAll() {
		if (idle != null && idle.isPlaying()) {
			idle.stop();
		}
		if (motor != null && motor.isPlaying()) {
			motor.stop();
		}
		if (turbo != null && isMediaArrayPlaying(turbo)) {
			stopMediaArray(turbo);
		}
		if (nos != null && nos.isPlaying()) {
			nos.stop();
		}
		if (redline != null && redline.isPlaying()) {
			redline.stop();
		}
	}


}