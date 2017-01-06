package com.jassdev.apps.andrroider.uradio.Api;

import com.jassdev.apps.andrroider.uradio.MainScreen.Model.URadioStreamModel;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Jackson on 04/01/2017.
 */

public interface URadioApi {

    @GET("/status-json.xsl")
    Observable<URadioStreamModel> getRadioInfo();
}
