package com.jassdev.apps.andrroider.uradio.Service;


import com.jassdev.apps.andrroider.uradio.radio.view.MainView;

/**
 * Created by Jackson on 06/01/2017.
 */

public class BaseService {

    static MainView mView;
    public BaseService(MainView view) {
        mView = view;
    }

}
