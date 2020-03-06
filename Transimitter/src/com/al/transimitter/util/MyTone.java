package com.al.transimitter.util;

import com.al.transimitter.MainActivity;

import android.os.Handler;
import android.util.Log;

public class MyTone {
	private PlayToneThread playToneThread;
	private boolean isThreadRunning = false;
	private final Handler stopThread;
	
	private static final MyTone INSTANCE = new MyTone();
	
	private MyTone(){
		stopThread = new Handler();
	}
	
	public static MyTone getInstance(){
		return INSTANCE;
	}
	
	public void generate(int freq, int duration, float volume, final MainActivity activ, ToneStoppedListener toneStoppedListener){
		if (!isThreadRunning) {
			stop();
			playToneThread = new PlayToneThread(freq, duration, volume, toneStoppedListener);
			playToneThread.start();
			isThreadRunning = true;
			stopThread.postDelayed(new Runnable() {
				@Override
				public void run() {
					stop();
					Log.i("INFO", "play done");
					activ.button01.setText("Play");
				}
			}, duration * 1000);
		}
	}
	
	public void stop(){
		if (playToneThread != null) {
			Log.i("INFO", "STOP");
			playToneThread.stopTone();
			playToneThread.interrupt();
			playToneThread = null;
			isThreadRunning = false;
		}
	}
}
