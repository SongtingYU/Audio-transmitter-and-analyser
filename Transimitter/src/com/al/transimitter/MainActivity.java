package com.al.transimitter;

import com.al.transimitter.util.ToneStoppedListener;
import com.al.transimitter.util.MyTone;
import android.support.v7.app.ActionBarActivity;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends ActionBarActivity {

	Button taskButton;
	public Button button01;
	EditText editText1;
	EditText editText2;
	SeekBar seekBarFreq;
	SeekBar seekBarDuration;
	
	int freq = 350;
	int duration = 15;
	float volume = 0.2f;
	boolean isPlaying = false;
	boolean isPlaying2 = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        taskButton = (Button) findViewById(R.id.task);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText1.setText(String.valueOf(freq));
        editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setText(String.valueOf(duration));
        seekBarFreq = (SeekBar) findViewById(R.id.seekBarFreq);
        seekBarDuration = (SeekBar) findViewById(R.id.seekBarDuration);
        
        button01 = (Button) findViewById(R.id.button01);
        button01.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleTonePlay();
			}
		});
        
        seekBarFreq.setMax(20000);
        seekBarFreq.setProgress(freq);
        seekBarFreq.setContentDescription("Frequency");
        seekBarFreq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				MyTone.getInstance().stop();
				button01.setText("Play");
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				editText1.setText(String.valueOf(progress));
			}
		});
        
        seekBarDuration.setMax(60);
        seekBarDuration.setProgress(duration);
        seekBarDuration.setContentDescription("Time");
        seekBarDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				MyTone.getInstance().stop();
				button01.setText("Play");
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				editText2.setText(String.valueOf(progress));
			}
		});
    }

    private void handleTonePlay() {
        String freqString = editText1.getText().toString();
        String durationString = editText2.getText().toString();
        if (!"".equals(freqString) && !"".equals(durationString)) {
            if (!isPlaying) {
        	    button01.setText("Stop");
                freq = Integer.parseInt(freqString);
                duration = Integer.parseInt(durationString);
                // Play Tone
                MyTone.getInstance().generate(freq, duration, volume, this, new ToneStoppedListener() {
                    @Override public void onToneStopped() {
                        isPlaying = false;
                        button01.setText("Play");
                    }
                });
                isPlaying = true;
            } else {
                // Stop Tone
                MyTone.getInstance().stop();
                isPlaying = false;
                button01.setText("Play");
            }
        } else if ("".equals(freqString)) {
            Toast.makeText(MainActivity.this, "Please enter a frequency!", Toast.LENGTH_SHORT).show();
        } else if ("".equals(durationString)) {
            Toast.makeText(MainActivity.this, "Please enter duration!", Toast.LENGTH_SHORT).show();
        }
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
}
