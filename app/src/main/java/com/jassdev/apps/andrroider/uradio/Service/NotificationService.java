package com.jassdev.apps.andrroider.uradio.Service;

/**
 * Created by Jackson on 30/12/2016.
 */

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.view.View;
import android.widget.RemoteViews;

import com.jassdev.apps.andrroider.uradio.MainActivity;
import com.jassdev.apps.andrroider.uradio.R;
import com.jassdev.apps.andrroider.uradio.Radio.View.MainView;
import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Player;


public class NotificationService extends Service {

    public String track = "";
    private Notification status;
    boolean isPause = true;
    private BroadcastReceiver broadcastReceiver;
    private MainView mView;
    private Player player;

    public NotificationService() {
        mView = BaseService.mView;
        if (mView.isHQ())
            player = new Player(mView, Const.RADIO_PATH_HQ, this);
        else
            player = new Player(mView, Const.RADIO_PATH, this);
    }

    private void showNotification(int pos) {
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Const.ACTION.PLAY_ACTION);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Const.ACTION.STOPFOREGROUND_ACTION);

        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.status_bar_play, pendingPlayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        if (track.isEmpty())
            views.setTextViewText(R.id.track_tv, mView.getTrackTitle());
        else
            views.setTextViewText(R.id.track_tv, track);

        if (pos == 0) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause);
        }

        if (pos == 1) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause);
            if (mView.getControlButton() != null) {
                mView.setControlButtonImageResource(R.drawable.pause);
                mView.setVisibilityToLoadingAnimation(View.VISIBLE);
                mView.setVisibilityToControlButton(View.GONE);
                mView.setIsControlActivated(true);
            }
        }
        if (pos == 2) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.play);
            if (mView.getControlButton() != null) {
                mView.setControlButtonImageResource(R.drawable.play);
                mView.setVisibilityToLoadingAnimation(View.GONE);
                mView.setVisibilityToControlButton(View.VISIBLE);
                mView.setIsControlActivated(false);
            }
        }

// .setSmallIcon(R.mipmap.ic_logo) - почему-то без него не работает кастомный лэйаут

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            status = new Notification.Builder(this)
                    .setCustomContentView(views)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
        } else {
            status = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .build();
            status.contentView = views;
        }
        startForeground(Const.FOREGROUND_SERVICE, status);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null && intent.getStringExtra("TRACK") != null) {
                    track = intent.getStringExtra("TRACK");
                    showNotification(player.isPlaying() ? 0 : 2);
                }
            }
        };
        IntentFilter filter = new IntentFilter(Const.ACTION.BROADCAST_MANAGER_INTENT);

        registerReceiver(broadcastReceiver, filter);

        if (intent.getAction().equals(Const.ACTION.STARTFOREGROUND_ACTION)) {
            isPause = false;
            showNotification(0);
            startHQorNot();

        } else if (intent.getAction().equals(Const.ACTION.PLAY_ACTION)) {
            if (!isPause) {
                showNotification(2);
                player.stop();
                player.release();
                isPause = true;
            } else {
                showNotification(1);
                isPause = false;
                startHQorNot();
            }
        } else if (intent.getAction().equals(
                Const.ACTION.STOPFOREGROUND_ACTION)) {
            if (mView.getControlButton() != null) {
                mView.setControlButtonImageResource(R.drawable.play);
                mView.setVisibilityToLoadingAnimation(View.GONE);
                mView.setVisibilityToControlButton(View.VISIBLE);
                mView.setIsControlActivated(false);
            }
            player.stop();
            player.release();
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    private void startHQorNot() {
        if (mView.isHQ()) {
            player = new Player(mView, Const.RADIO_PATH_HQ, this);
            player.start();
        } else {
            player = new Player(mView, Const.RADIO_PATH, this);
            player.start();
        }

    }
}
