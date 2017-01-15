package com.jassdev.apps.andrroider.uradio.Radio.View;


import android.widget.ImageButton;

/**
 * Created by Jackson on 06/01/2017.
 */

public interface MainView {

    boolean isControlActivated();

    void setIsControlActivated(boolean isActivated);

    void setControlButtonImageResource(int resource);

    boolean isHQ();

    void setVisibilityToLoadingAnimation(int visibility);

    void setVisibilityToControlButton(int visibility);

    ImageButton getControlButton();

    void refreshNotification();

    void showToast(String text);

    String getTrackTitle();

    void setTrackTitle(String track);
}
