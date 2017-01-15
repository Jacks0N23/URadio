package com.jassdev.apps.andrroider.uradio.Radio;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jassdev.apps.andrroider.uradio.R;
import com.jassdev.apps.andrroider.uradio.Radio.Presenter.RadioPresenter;
import com.jassdev.apps.andrroider.uradio.Radio.View.MainView;
import com.jassdev.apps.andrroider.uradio.Service.BaseService;
import com.jassdev.apps.andrroider.uradio.Service.NotificationService;
import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Player;
import com.jassdev.apps.andrroider.uradio.databinding.FragmentRadioBinding;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Jackson on 15/01/2017.
 */

public class RadioFragment extends Fragment implements MainView {

    /**
     * https://bitbucket.org/mrcpp/rapliveradio/src/7548be6d5b6b9330421e91f756150099d06b0c3d/app/src/main/java/radio/raplive/ru/rapliveradio/ActivityMain.java?at=master&fileviewer=file-view-default
     * https://tproger.ru/articles/android-online-radio/
     */

    // Boolean for check if play/pause button is activated
    private boolean controlIsActivated = false;
    public static boolean isHQ = true;
    private FragmentRadioBinding binding;
    private RadioPresenter presenter;
    private Player player;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRadioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        presenter = new RadioPresenter(this);
        player = new Player(this, Const.RADIO_PATH_HQ, getActivity());

        binding.controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.loadAnimation.setVisibility(View.VISIBLE);
                binding.controlButton.setVisibility(View.GONE);
                togglePlayPause();
            }
        });

        binding.highQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayingInHQ();
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getTrackTitle().equals(getString(R.string.too_many_positive))) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getTrackTitle() + "\n На " + Const.RADIO_BASE_URL_FOR_SHARE);
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Поделиться треком:"));
                } else {
                    showToast("Откуда мне знать, что играет, если радио выключено?");
                }
            }
        });
    }

    private void togglePlayPause() {
        if (!isControlActivated()) {
            startPlayerService();
            setIsControlActivated(true);
            presenter.getTrackInfo();
            binding.controlButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.pause));
            vibrate();
        } else {
            player.stop();
            setIsControlActivated(false);
            refreshNotification();
            binding.controlButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.play));
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

    // Service for background audio binding.playing
    public void startPlayerService() {
        new BaseService(this); // just for working service with mvp
        Intent serviceIntent = new Intent(getActivity(), NotificationService.class);
        serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
        getActivity().startService(serviceIntent);
    }

    // Vibrate when click on control button
    public void vibrate() {
        ((Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE)).vibrate(Const.VIBRATE_TIME);
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
        getActivity().sendBroadcast(broadcastIntent);
    }

    @Override
    public void showToast(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public String getTrackTitle() {
        return binding.trackTv.getText().toString();
    }

    @Override
    public void setTrackTitle(String track) {
        if (!isDetached())
            binding.trackTv.setText(getString(R.string.now_playing, track));
    }
}
