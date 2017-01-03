package com.jassdev.apps.andrroider.uradio.Utils;

/**
 * Created by Jackson on 30/12/2016.
 */

public class Const {

    /**
     * http://uradio.pro:8000/live обычный поток
     * http://uradio.pro:8000/liveHD в лучшем качестве
     */

//    public static String RUS_RADIO_PATH = "http://icecast.russkoeradio.cdnvideo.ru/rr.mp3";

    public static String RADIO_PATH = "http://uradio.pro:8000/liveHD";

    public static int PHOTO_LOAD_REFRESH_TIME = 20000;

    public static int VIBRATE_TIME = 5;

    public interface ACTION {
        String MAIN_ACTION = "com.jassdev.apps.andrroider.uradio.action.main";
        String PLAY_ACTION = "com.jassdev.apps.andrroider.uradio.action.play";
        String STARTFOREGROUND_ACTION = "com.jassdev.apps.andrroider.uradio.action.startforeground";
        String STOPFOREGROUND_ACTION = "com.jassdev.apps.andrroider.uradio.action.stopforeground";
    }
    public static int FOREGROUND_SERVICE = 101;

}