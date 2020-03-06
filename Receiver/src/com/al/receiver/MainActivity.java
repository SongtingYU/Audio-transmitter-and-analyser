package com.al.receiver;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import com.al.receiver.util.Goertzel;

import android.support.v7.app.ActionBarActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class MainActivity extends ActionBarActivity {

	Button taskButton;
	EditText editText1;
	TextView textView1;
	TextView textView2;
	Button button01;

	float sampleRate = 44100.0F;
	float targetFreq;

	int frequency = 8000;
	@SuppressWarnings("deprecation")
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	AudioRecord audioRecord;
	int blockSize = 256;// = 256;
	Button startStopButton;
	boolean started = false;
	boolean cancelled_flag = false;
	
	Goertzel goertzel = null;

	RecordAudio recordAudio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		taskButton = (Button) findViewById(R.id.task);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setText("400");
		textView2 = (TextView) findViewById(R.id.textView2);
		button01 = (Button) findViewById(R.id.button01);

		button01.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (started == false) {
					started = true;
					cancelled_flag = false;
					button01.setText("Started");
					recordAudio = new RecordAudio();
					recordAudio.execute();
				} else {
					started = false;
					cancelled_flag = true;
					button01.setText("Detect");
					try {
						audioRecord.stop();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class RecordAudio extends AsyncTask<Void, Double[], Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			int bufferSize = AudioRecord.getMinBufferSize(frequency,
					channelConfiguration, audioEncoding);
			audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
					frequency, channelConfiguration, audioEncoding, bufferSize);
			int bufferReadResult;
			short[] buffer = new short[blockSize];
			double[] toTransform = new double[blockSize];
			try {
				audioRecord.startRecording();
			} catch (IllegalStateException e) {
				Log.e("Recording failed", e.toString());
			}
			while (started) {
				if (isCancelled() || cancelled_flag == true) {
					started = false;
					break;
				} else {
//					bufferReadResult = audioRecord.read(buffer, 0, blockSize);
//					
//					Log.i("INFO-bufferReadResults", String.valueOf(bufferReadResult));
//					Log.i("INFO-sampleRate", String.valueOf(sampleRate));
//					Log.i("INFO-blockSize", String.valueOf(blockSize));
//					
//					for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
//						// signed 16 bit
//						toTransform[i] = (double) buffer[i] / 32768.0;
//					}
					ShortBuffer sbuf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
                    short[] audioShorts = new short[sbuf.capacity()];
                    sbuf.get(audioShorts);

                    float[] audioFloats = new float[audioShorts.length];

                    for (int j = 0; j < audioShorts.length; j++) {
                        audioFloats[j] = ((float)audioShorts[j]) / 0x8000;
                    }					
					targetFreq = Float.valueOf(editText1.getText().toString());
					Log.i("INFO-targetFreq", String.valueOf(targetFreq));
					
//					goertzel = new Goertzel(sampleRate, targetFreq, bufferSize, false);
					
//					double magnitute = goertzel.detectFreq(frequency, toTransform);
//					
//					Log.i("INFO-magnitute", String.valueOf(magnitute));
					
//					textView2.setText(String.valueOf(magnitute));
				}
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(Double[]... values) {
			super.onProgressUpdate(values);
			// pass
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			try {
				audioRecord.stop();
			} catch (IllegalStateException e) {
				Log.e("Stop failed", e.toString());
			}
		}

	}

}