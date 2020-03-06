package com.example.morse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import util.MorseDecoder;
import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final int RECORDER_SAMPLE_RATE = 8000; // at least 2 times
                                                            // higher than sound
                                                            // frequency,
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord recorder = null;
    private int bufferSize = 0;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private Button startRecBtn;
    private Button stopRecBtn;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startRecBtn = (Button) findViewById(R.id.button1);
        stopRecBtn = (Button) findViewById(R.id.button2);

        startRecBtn.setEnabled(true);
        stopRecBtn.setEnabled(false);

        bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

        startRecBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("SOUNDCOMPARE", "Start Recording");

                startRecBtn.setEnabled(false);
                stopRecBtn.setEnabled(true);
                stopRecBtn.requestFocus();

                startRecording();
            }
        });

        stopRecBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("SOUNDCOMPARE", "Stop recording");

                startRecBtn.setEnabled(true);
                stopRecBtn.setEnabled(false);
                startRecBtn.requestFocus();

                stopRecording();
            }
        });
    }

    private void startRecording() {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLE_RATE, RECORDER_CHANNELS,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        recorder.startRecording();

        isRecording = true;

        recordingThread = new Thread(new Runnable() {

            @Override
            public void run() {
                writeAudioDataToTempFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    private String getTempFilename() {
        File file = new File(getFilesDir(), "tempaudio");

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(getFilesDir(), "signal.raw");

        if (tempFile.exists())
            tempFile.delete();

        return (file.getAbsolutePath() + "/" + "signal.raw");
    }

    private void writeAudioDataToTempFile() {
        byte data[] = new byte[bufferSize];
        String filename = getTempFilename();
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        int read = 0;

        if (os != null) {
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);

                if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                    try {
                        os.write(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteTempFile() {
        File file = new File(getTempFilename());

        file.delete();
    }

    private void stopRecording() {
        if (recorder != null) {
            isRecording = false;

            recorder.stop();
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        new MorseDecoder().execute(new File(getTempFilename()));    
    }
}