package com.jassdev.apps.andrroider.uradio.radio;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.jassdev.apps.andrroider.uradio.R;
import com.jassdev.apps.andrroider.uradio.Service.BaseService;
import com.jassdev.apps.andrroider.uradio.Service.NotificationService;
import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Player;
import com.jassdev.apps.andrroider.uradio.databinding.FragmentRadioBinding;
import com.jassdev.apps.andrroider.uradio.radio.presenter.RadioPresenter;
import com.jassdev.apps.andrroider.uradio.radio.view.MainView;

import static android.content.ContentValues.TAG;
import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Jackson on 15/01/2017.
 */

public class RadioFragment extends Fragment implements MainView {

    /**
     * https://bitbucket.org/mrcpp/rapliveradio/src/7548be6d5b6b9330421e91f756150099d06b0c3d/app/src/main/java/radio/raplive/ru/rapliveradio/ActivityMain.java?at=master&fileviewer=file-view-default
     * https://tproger.ru/articles/android-online-radio/
     */

    public static RadioFragment newInstance() {
        RadioFragment fragment = new RadioFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    // Boolean for check if play/pause button is activated
    private boolean controlIsActivated = false;
    public static boolean isHQ = true;
    private FragmentRadioBinding binding;
    private RadioPresenter presenter;
    private Player player;
    private MusicIntentReceiver headsetPlugReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentRadioBinding.inflate(inflater, container, false);

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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (presenter == null || player == null || headsetPlugReceiver == null) {
            presenter = new RadioPresenter(this);
            if (getActivity() != null) {
                player = new Player(this, Const.RADIO_PATH_HQ, getActivity());
            }
            headsetPlugReceiver = new MusicIntentReceiver();
            IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
            getActivity().registerReceiver(headsetPlugReceiver, filter);
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(headsetPlugReceiver);
        super.onDestroy();
    }

    private void togglePlayPause() {
        if (getActivity() != null) {
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
        if (getActivity() != null) {
            new BaseService(this); // just for working service with mvp
            Intent serviceIntent = new Intent(getActivity(), NotificationService.class);
            serviceIntent.setAction(Const.ACTION.STARTFOREGROUND_ACTION);
            getActivity().startService(serviceIntent);
        }
    }

    public void vibrate() {
        if (getActivity() != null)
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
        if (getActivity() != null) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(Const.ACTION.BROADCAST_MANAGER_INTENT);
            broadcastIntent.putExtra("TRACK", getTrackTitle());
            getActivity().sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void showToast(String text) {
        if (getActivity() != null)
            Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    public String getTrackTitle() {
        return binding.trackTv.getText().toString();
    }

    @Override
    public void setTrackTitle(String track) {
        if (isAdded())
            binding.trackTv.setText(getString(R.string.now_playing, track));
    }


    public class MusicIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
                int state = intent.getIntExtra("state", -1);
                switch (state) {
                    case 0:
                        if (isControlActivated())
                            togglePlayPause();
                        Log.d(TAG, "Headset is unplugged");
                        break;
                    case 1:
                        Log.d(TAG, "Headset is plugged");
                        break;
                    default:
                        Log.d(TAG, "I have no idea what the headset state is");
                }
            }
        }
    }
}
