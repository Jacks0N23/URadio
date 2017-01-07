package com.jassdev.apps.andrroider.uradio.MainScreen;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jassdev.apps.andrroider.uradio.MainScreen.Presenter.MainPresenter;
import com.jassdev.apps.andrroider.uradio.MainScreen.View.MainView;
import com.jassdev.apps.andrroider.uradio.Service.BaseService;
import com.jassdev.apps.andrroider.uradio.Service.NotificationService;
import com.jassdev.apps.andrroider.uradio.R;
import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Player;
import com.jassdev.apps.andrroider.uradio.databinding.AnotherMainBinding;

public class MainActivity extends AppCompatActivity implements MainView {

    private final String TAG = "MainActivity";

    /**
     * https://bitbucket.org/mrcpp/rapliveradio/src/7548be6d5b6b9330421e91f756150099d06b0c3d/app/src/main/java/radio/raplive/ru/rapliveradio/ActivityMain.java?at=master&fileviewer=file-view-default
     * https://tproger.ru/articles/android-online-radio/
     */

    // Boolean for check if play/pause button is activated
    private boolean controlIsActivated = false;
    private AnotherMainBinding binding;

    public static boolean isHQ = true;
    public boolean mute = false;

    private MainPresenter presenter;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.another_main);
        presenter = new MainPresenter(this);
        player = new Player(this, Const.RADIO_PATH_HQ, this);

        binding.controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loadAnimation.setVisibility(View.VISIBLE);
                binding.controlButton.setVisibility(View.GONE);
                togglePlayPause();
            }
        });

        binding.playingAnim.setVisibility(View.GONE);

        binding.highQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayingInHQ();
            }
        });

        binding.volumeOff.setImageAlpha(150);
        binding.volumeOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVolumeOff();
            }
        });

    }

    private void togglePlayPause() {
        if (!isControlActivated()) {
            startPlayerService();
            setIsControlActivated(true);
            presenter.getTrackInfo();
            binding.controlButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause));
            vibrate();
        } else {
            player.stop();
            setIsControlActivated(false);
            refreshNotification();
            binding.controlButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play));
            setVisibilityToPlayingAnimation(View.GONE);
            vibrate();
        }
    }

    private void startPlayingInHQ() {
        if (isHQ) {
            binding.highQuality.setImageAlpha(150);
            isHQ = false;
        } else {
            binding.highQuality.setImageAlpha(255);
            isHQ = true;
        }

        //типо остановили руками
        togglePlayPause();
        //типо включили снова
        togglePlayPause();

        setVisibilityToLoadingAnimation(View.VISIBLE);
    }

    private void setVolumeOff() {
        if (mute) {
            player.setMute(true);
            binding.volumeOff.setImageAlpha(255);
            mute = false;
        } else {
            player.setMute(false);
            binding.volumeOff.setImageAlpha(150);
            mute = true;
        }
    }

    // Service for background audio binding.playing
    public void startPlayerService() {
        BaseService service = new BaseService(this);
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
        startService(serviceIntent);
    }

    // Vibrate when click on control button
    public void vibrate() {
        ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(Const.VIBRATE_TIME);
    }

    @Override
    public boolean isControlActivated() {
        return controlIsActivated;
    }

    @Override
    public void setIsControlActivated(boolean isActivated) {
        controlIsActivated = isActivated;
    }

    @Override
    public void setControlButtonImageResource(int resource) {
        binding.controlButton.setImageResource(resource);
    }

    @Override
    public boolean isHQ() {
        return isHQ;
    }

    @Override
    public void setVisibilityToLoadingAnimation(int visibility) {
        binding.loadAnimation.setVisibility(visibility);
    }

    @Override
    public void setVisibilityToPlayingAnimation(int visibility) {
        binding.playingAnim.setVisibility(visibility);
    }

    @Override
    public void setVisibilityToControlButton(int visibility) {
        binding.controlButton.setVisibility(visibility);
    }

    @Override
    public ImageButton getControlButton() {
        return binding.controlButton;
    }

    @Override
    public void refreshNotification() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Const.ACTION.BROADCAST_MANAGER_INTENT);
        broadcastIntent.putExtra("TRACK", getTrackTitle());
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public String getTrackTitle() {
        return binding.trackTv.getText().toString();
    }

    @Override
    public void setTrackTitle(String track) {
        binding.trackTv.setText(getString(R.string.now_playing, track));
    }
}
