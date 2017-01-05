package com.jassdev.apps.andrroider.uradio;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jassdev.apps.andrroider.uradio.Model.Source;
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

    private CompositeSubscription compositeSubscription = new CompositeSubscription();
    private Subscriber<URadioStreamModel> subscriber;
    private Observable<URadioStreamModel> observable;


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
        getRequest().single().subscribe(getSubscriber());
        compositeSubscription.add(subscriber);
        Observable.timer(15, TimeUnit.SECONDS, Schedulers.io()).subscribe(new Subscriber<Long>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Long aLong) {
                getTrackInfo();
            }
        });
    }

    private Observable<URadioStreamModel> getRequest() {
        if (observable == null) {
            observable = api
                    .getRadioInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return observable;
    }

    private Subscriber<URadioStreamModel> getSubscriber() {
        subscriber = new Subscriber<URadioStreamModel>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                Toast.makeText(MainActivity.this, "Не удалось получить название трека, попробую ещё", Toast.LENGTH_LONG).show();
                getTrackInfo();
            }

            @Override
            public void onNext(URadioStreamModel uRadioStreamModel) {
                track = searchTitleInList(uRadioStreamModel);
                //небольшая оптимизация, чтобы мы обновляли тайтл только когда разные названия трека у нас и в ответе от сервера
                if (track != null && !track.equals(binding.trackTv.getText().toString())) {
                    refreshNotification(track);
                    binding.trackTv.setText(track);
                }
            }
        };
        return subscriber;
    }


    private String searchTitleInList(URadioStreamModel uRadioStreamModel) {
        for (Source source : uRadioStreamModel.getIcestats().getSource()) {
            if (source.getTitle() != null)
                return source.getTitle();
        }
        return null;
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
            //немного грязно, хотя работает отлично
            if (observable == null)
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

        //типо остановили руками
        togglePlayPause();
        //типо включили снова
        togglePlayPause();

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
    protected void onResume() {
        super.onResume();
        getTrackInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeSubscription != null)
            compositeSubscription.unsubscribe();

        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
