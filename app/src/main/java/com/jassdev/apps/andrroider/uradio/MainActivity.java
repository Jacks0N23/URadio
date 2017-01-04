package com.jassdev.apps.andrroider.uradio;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jassdev.apps.andrroider.uradio.Model.URadioStreamModel;
import com.jassdev.apps.andrroider.uradio.Utils.CircularSeekBar;
import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Player;
import com.jassdev.apps.andrroider.uradio.Utils.Utils;
import com.jassdev.apps.andrroider.uradio.databinding.AnotherMainBinding;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    /**
     * https://bitbucket.org/mrcpp/rapliveradio/src/7548be6d5b6b9330421e91f756150099d06b0c3d/app/src/main/java/radio/raplive/ru/rapliveradio/ActivityMain.java?at=master&fileviewer=file-view-default
     * https://tproger.ru/articles/android-online-radio/
     */

    // Boolean for check if play/pause button is activated
    static boolean controlIsActivated = false;
    MediaPlayer mMediaPlayer;
    private AnotherMainBinding binding;

     //Strings for showing song data and detecting old album photo
    public static String track, genre; // жанр мб будет использоваться потом

    public static TextView track_tv;
    public static ImageView control_button;
    public static CircularSeekBar volumeChanger;

    // Animation on bottom of the screen when stream is loaded
    public static AVLoadingIndicatorView playing_animation;

    // Stram loading animation on the center of screen
    public static AVLoadingIndicatorView loading_animation;

    // Button for start/stop playing audio
    public static boolean isHQ = true;
    public boolean mute = false;
    private URadioApi api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.another_main);

        api = Utils.createRxService(URadioApi.class, Const.RADIO_BASE_URL, true);

        binding.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.loading_animation.setVisibility(View.VISIBLE);
                MainActivity.control_button.setVisibility(View.GONE);
                togglePlayPause();
            }
        });

        track_tv = binding.trackTv;
        volumeChanger = binding.volumeChanger;
        control_button = (ImageView) findViewById(R.id.play);
//        control_button = binding.play;
        playing_animation = binding.icluddedAnim.playingAnim;
        playing_animation.setVisibility(View.GONE);
        loading_animation = binding.loadAnimation;
        startListenVolume();
        getTrackInfo();

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


    private void getTrackInfo() {
        new CompositeSubscription().add(api
                .getRadioInfo()
//                .delay(15, TimeUnit.SECONDS, Schedulers.newThread())
//                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<URadioStreamModel>() {
                    @Override
                    public void onCompleted() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(15000);
                                    getTrackInfo();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: ", e);
                        Toast.makeText(MainActivity.this, "Не удалось получить название трека, попробую ещё", Toast.LENGTH_LONG).show();
                        getTrackInfo();
                    }

                    @Override
                    public void onNext(URadioStreamModel uRadioStreamModel) {
                        track = uRadioStreamModel.getIcestats().getSource().get(0).getTitle();
                        //небольшая оптимизация, чтобы мы обновляли тайтл только когда разные названия трека у нас и в ответе от сервера
                        if (!track.equals(binding.trackTv.getText().toString())) {
                            refreshNotification(track);
                            binding.trackTv.setText(track);
                        }
                    }
                }));
    }


    private void refreshNotification(String track) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Const.ACTION.BROADCAST_MANAGER_INTENT);
        broadcastIntent.putExtra("TRACK", track);
        sendBroadcast(broadcastIntent);
    }

    private void togglePlayPause() {
        if (!controlIsActivated) {
            startPlayerService();
            getTrackInfo();
            binding.play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause));
            controlIsActivated = true;
            vibrate();
        } else {
            Player.stop();
            refreshNotification("");
            binding.play.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play));
            playing_animation.setVisibility(View.GONE);
            controlIsActivated = false;
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

        startPlayerService();
        getTrackInfo();

        binding.loadAnimation.setVisibility(View.VISIBLE);
    }

    private void setVolumeOff() {
        if (mute) {
            Player.setMute(true);
            binding.volumeOff.setImageAlpha(255);
            mute = false;
        } else {
            Player.setMute(false);
            binding.volumeOff.setImageAlpha(150);
            mute = true;
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
