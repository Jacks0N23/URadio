package com.jassdev.apps.andrroider.uradio;

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
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.RemoteViews;

import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Player;


public class NotificationService extends Service {

    public String track = "";
    Notification status;
    boolean isPause = true;
    BroadcastReceiver broadcastReceiver;

    private void showNotification(int pos) {
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Const.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent playIntent = new Intent(this, NotificationService.class);
        playIntent.setAction(Const.ACTION.PLAY_ACTION);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent closeIntent = new Intent(this, NotificationService.class);
        closeIntent.setAction(Const.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pendingPlayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        if (track.isEmpty())
            views.setTextViewText(R.id.track_tv, MainActivity.track);
        else
            views.setTextViewText(R.id.track_tv, track);

        if (pos == 0) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause);
        }

        if (pos == 1) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.pause);
            if (MainActivity.control_button != null) {
                MainActivity.control_button.setImageResource(R.drawable.play);
                MainActivity.playing_animation.setVisibility(View.GONE);
                MainActivity.loading_animation.setVisibility(View.VISIBLE);
                MainActivity.control_button.setVisibility(View.GONE);
                MainActivity.controlIsActivated = true;
            }
        }
        if (pos == 2) {
            views.setImageViewResource(R.id.status_bar_play,
                    R.drawable.play);
            if (MainActivity.control_button != null) {
                MainActivity.control_button.setImageResource(R.drawable.play);
                MainActivity.playing_animation.setVisibility(View.GONE);
                MainActivity.loading_animation.setVisibility(View.GONE);
                MainActivity.control_button.setVisibility(View.VISIBLE);
                MainActivity.controlIsActivated = false;
            }
        }

// .setSmallIcon(R.mipmap.ic_logo) - почему-то без него не работает кастомный лэйаут

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            status = new Notification.Builder(this)
                    .setCustomContentView(views)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .build();
        } else {
            status = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.ic_logo)
                    .build();
            status.contentView = views;
        }
        //закрепляет в штоке уведомление
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.contentIntent = pendingIntent;
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
                    showNotification(Player.isPlaing() ? 1 : 2);
                }
            }
        };
        IntentFilter filter = new IntentFilter(Const.ACTION.BROADCAST_MANAGER_INTENT);

        registerReceiver(broadcastReceiver, filter);

        if (intent.getAction().equals(Const.ACTION.STARTFOREGROUND_ACTION)) {
            isPause = false;
            showNotification(0);
            if (MainActivity.isHQ)
                Player.start(Const.RADIO_PATH_HQ, this);
            else
                Player.start(Const.RADIO_PATH, this);

        } else if (intent.getAction().equals(Const.ACTION.PLAY_ACTION)) {
            if (!isPause) {
                showNotification(2);
                Player.stop();
                isPause = true;
            } else {
                showNotification(1);
                isPause = false;
                if (MainActivity.isHQ)
                    Player.start(Const.RADIO_PATH_HQ, this);
                else
                    Player.start(Const.RADIO_PATH, this);
            }
        } else if (intent.getAction().equals(
                Const.ACTION.STOPFOREGROUND_ACTION)) {
            if (MainActivity.control_button != null) {
                MainActivity.control_button.setImageResource(R.drawable.play);
                MainActivity.playing_animation.setVisibility(View.GONE);
                MainActivity.loading_animation.setVisibility(View.GONE);
                MainActivity.control_button.setVisibility(View.VISIBLE);
                MainActivity.controlIsActivated = false;
            }
            Player.stop();
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }
}
