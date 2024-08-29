package com.example.cooltimer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView timeTextView;
    private Button startButton;
    private CountDownTimer timer;
    private MediaPlayer mediaPlayer;
    private boolean isTimerRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        isTimerRun = false;

        startButton = findViewById(R.id.startButton);

        mediaPlayer = MediaPlayer.create(this, R.raw.stop_signal);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setProgress(300);

        timeTextView = findViewById(R.id.timeTextView);
        timeTextView.setText(intToTime(seekBar.getProgress()));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                timeTextView.setText(intToTime(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    public void StartButton(View view) {

        if(mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startButton.setText("Start");

        } else if(!isTimerRun) {

            timer = new CountDownTimer(seekBar.getProgress() * 1000, 1000) {
                @Override
                public void onTick(long l) {
                    seekBar.setProgress(seekBar.getProgress() - 1);
                }

                @Override
                public void onFinish() {
                    seekBar.setEnabled(true);
                    isTimerRun = false;
                    mediaPlayer.start();
                }
            };
            timer.start();

            startButton.setText("Pause");
            seekBar.setEnabled(false);
            isTimerRun = true;
        } else {
            timer.cancel();
            startButton.setText("Start");
            seekBar.setEnabled(true);
            isTimerRun = false;
        }
    }

    public String intToTime(int intTime) {
        int min = intTime/60;
        int sec = intTime%60;

        if(sec >= 10) {
            return "" + min + ":" + sec;
        } else {
            return "" + min + ":0" + sec;
        }
    }
}