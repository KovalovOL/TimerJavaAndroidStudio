package com.example.cooltimer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private SeekBar seekBar;
    private TextView timeTextView;
    private Button startButton;
    private CountDownTimer timer;
    private MediaPlayer mediaPlayer;
    private boolean isTimerRun;
    private int defaultStartTime;
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

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(900);

        timeTextView = findViewById(R.id.timeTextView);
        timeTextView.setText(intToTime(seekBar.getProgress()));

        setDefaultStartTime(PreferenceManager.getDefaultSharedPreferences(this));

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

        if(mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            startButton.setText("Start");
        }if(!isTimerRun) {

            timer = new CountDownTimer(seekBar.getProgress() * 1000,
                    1000) {
                @Override
                public void onTick(long l) {
                    seekBar.setProgress(seekBar.getProgress() - 1);
                }

                @Override
                public void onFinish() {
                    SharedPreferences sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


                    if(sharedPreferences.getBoolean("enable_sound", true)) {
                        String melody = sharedPreferences.getString("timer_melody", "stop_signal");

                        if (mediaPlayer != null) {
                            mediaPlayer.release();
                        }

                        switch (melody){
                            case ("stop_signal"):
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.stop_signal);
                                break;
                            case ("bip_sound"):
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bip_sound);
                                break;
                            case ("bell_sound"):
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.bell_sound);
                                break;
                            case ("alan_siren_sound"):
                                mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm_siren_sound);
                                break;
                        }

                        mediaPlayer.start();
                    }
                    seekBar.setEnabled(true);
                    isTimerRun = false;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.time_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.actionSettings) {
            Intent openSetting = new Intent(this, SettingsActivity.class);
            startActivity(openSetting);
            return true;
        } else if(id == R.id.actionReset) {
            setDefaultStartTime(PreferenceManager.getDefaultSharedPreferences(this));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDefaultStartTime(SharedPreferences sharedPreferences) {
        defaultStartTime = Integer.parseInt(sharedPreferences.getString(
                "default_start_time", "300"));
        timeTextView.setText(intToTime(defaultStartTime));
        seekBar.setProgress(defaultStartTime);
    }
}