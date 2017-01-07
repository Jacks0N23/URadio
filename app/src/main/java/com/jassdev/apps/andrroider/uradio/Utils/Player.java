package com.jassdev.apps.andrroider.uradio.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.TrackRenderer;
import com.jassdev.apps.andrroider.uradio.MainScreen.View.MainView;
import com.jassdev.apps.andrroider.uradio.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Jackson on 30/12/2016.
 */

public class Player {

    static ExoPlayer exoPlayer;
    static TrackRenderer audioRenderer;
    private MainView mView;

    public Player(MainView mView, String URL, Context context) {
        this.mView = mView;
        Uri URI = Uri.parse(URL);
        FrameworkSampleSource sampleSource = new FrameworkSampleSource(context, URI, null);
        audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, null, true);
        exoPlayer = ExoPlayer.Factory.newInstance(1);
    }

    public void start() {
        if (exoPlayer != null) {
            exoPlayer.stop();
        }
        assert exoPlayer != null;
        exoPlayer.prepare(audioRenderer);
        exoPlayer.setPlayWhenReady(true);
        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                // This state if player is ready to work and loaded all data
                Log.d(TAG, "onPlayerStateChanged: "+ playbackState);
                if (playbackState == 4) {
                    mView.setVisibilityToPlayingAnimation(View.VISIBLE);
                    mView.setVisibilityToLoadingAnimation(View.GONE);
                    mView.setVisibilityToControlButton(View.VISIBLE);
                    mView.setControlButtonImageResource(R.drawable.pause);
                } else if (playbackState == 1) {
                    mView.setVisibilityToPlayingAnimation(View.GONE);
                    mView.setVisibilityToLoadingAnimation(View.GONE);
                     mView.setVisibilityToControlButton(View.VISIBLE);
                     mView.setControlButtonImageResource(R.drawable.play);
                }
            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError: ", error);
            }
        });
    }

    public boolean isPlaying() {
        if (exoPlayer != null && exoPlayer.getPlaybackState() > 1 && exoPlayer.getPlaybackState() < 5) {
            return true;
        }
        return false;
    }

    public void stop() {
        if (exoPlayer != null) {
            exoPlayer.stop();
        }
    }

    public void setMute(boolean toMute) {
        if (exoPlayer != null) {
            if (toMute) {
                exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, 0f);
            } else {
                exoPlayer.sendMessage(audioRenderer, MediaCodecAudioTrackRenderer.MSG_SET_VOLUME, 1f);
            }
        }

        else {
            mView.showToast("Нельзя выключить звук не включённого плеера");
        }
    }
}