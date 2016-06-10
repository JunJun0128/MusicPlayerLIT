package com.example.junekelectric.vocaloidplayer;

import android.media.MediaPlayer;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.carrier.CarrierMessagingService;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.junekelectric.musicplayer.R;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends ActionBarActivity {
    MediaPlayer mp;
    TextView title;
    Timer timer;
    Handler handler = new Handler();
    SeekBar seekBar;
    TextView currentTimeText, wholeTimeText;
    int track = 0;
    int[] soundID = {};
    String[] soundresource = {};
    String[] name = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mpcreate();

        final MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource("R.raw.noushou");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        title = (TextView) findViewById(R.id.title);
        title.setText("");
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        currentTimeText = (TextView) findViewById(R.id.current_time);
        wholeTimeText = (TextView) findViewById(R.id.whole_time);
        int duration = mp.getDuration();
        seekBar.setMax(duration);
        duration = duration / 1000;
        int minutes = duration / 60;
        int seconds = duration % 60;
        String m = String.format(Locale.JAPAN, "%02d", minutes);
        String s = String.format(Locale.JAPAN, "%02d", seconds);
        wholeTimeText.setText(m + ":" + s);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                mp.seekTo(progress);
                mp.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mp.pause();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    public void mpcreate() {
        mp = MediaPlayer.create(this, R.raw.noushou);
    }

    public void auto () {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
            @Override
            public void onCompletion(MediaPlayer mp){
                nextMusic();
            }
        });
    }

    public void play(){
        Log.d("start","push");
        mp.start();
        if (timer == null);{
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    int duration = mp.getCurrentPosition() / 1000;
                    int minutes = duration / 60;
                    int seconds = duration % 60;
                    final String m = String.format(Locale.JAPAN,"%02d", minutes);
                    final String s = String.format(Locale.JAPAN,"%02d", seconds);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int musicDuration = mp.getDuration();
                            int currentPosition = mp.getCurrentPosition();
                            currentTimeText.setText(m + ":" + s);
                            seekBar.setProgress(mp.getCurrentPosition());
                            Log.d("今の再生時間", currentPosition + "");
                            Log.d("全体の再生時間", musicDuration + "");
                            auto();
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    public void start(View v) {
        play();
    }

    public void pause(View v) {
        mp.pause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void stop(View v) {
        mp.stop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void next(View v) {
        nextMusic();
    }

    public void nextMusic () {
        track = track + 1;
        if (track == 20) {
            track = 1;
        }

        mp = MediaPlayer.create(this, soundID[track]);

        try {
            mp.setDataSource(soundresource[track]);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mp.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        title.setText(name[track]);
        int duration = mp.getDuration();
        seekBar.setMax(duration);
        duration = duration / 1000;
        int minutes = duration / 60;
        int seconds = duration % 60;
        String m = String.format(Locale.JAPAN, "%02d", minutes);
        String s = String.format(Locale.JAPAN, "%02d", seconds);
        wholeTimeText.setText(m + ":" + s);

        Log.d("track=", track + "");
        play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}