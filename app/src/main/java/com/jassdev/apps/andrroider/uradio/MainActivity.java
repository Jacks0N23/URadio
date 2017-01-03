package com.jassdev.apps.andrroider.uradio;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jassdev.apps.andrroider.uradio.Utils.CircularSeekBar;
import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Player;
import com.jassdev.apps.andrroider.uradio.databinding.AnotherMainBinding;

public class MainActivity extends AppCompatActivity {

    /**
     * https://bitbucket.org/mrcpp/rapliveradio/src/7548be6d5b6b9330421e91f756150099d06b0c3d/app/src/main/java/radio/raplive/ru/rapliveradio/ActivityMain.java?at=master&fileviewer=file-view-default
     * https://tproger.ru/articles/android-online-radio/
     */

    private final static String stream = "http://uradio.pro:8000/liveHD";
    // Boolean for check if play/pause button is activated
    static boolean controlIsActivated = false;
    MediaPlayer mMediaPlayer;
    private AnotherMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.another_main);
        setSupportActionBar(binding.toolbar);

        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                togglePlayPause();
            }
        });

        startListenVolume();
    }


    private void togglePlayPause() {
        if (!controlIsActivated) {
            startPlayerService();
            binding.play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause));
            controlIsActivated = true;
            vibrate();
        } else {
            Player.stop();
            binding.play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play));
            controlIsActivated = false;
            vibrate();
        }
    }

    // Service for background audio binding.playing
    public void startPlayerService() {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    // Vibrate when click on control button
    public void vibrate() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(Const.VIBRATE_TIME);
    }

    // Function for listen and change volume from seek bar to binding.player
    void startListenVolume() {
        binding.volumeChanger.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                Player.setVolume((100 - circularSeekBar.getProgress()) / 100f);
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
