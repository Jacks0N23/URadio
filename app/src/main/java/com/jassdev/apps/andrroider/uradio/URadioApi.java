package com.jassdev.apps.andrroider.uradio;

import com.jassdev.apps.andrroider.uradio.Model.URadioStreamModel;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by Jackson on 04/01/2017.
 */

public interface URadioApi {

    @GET("/status-json.xsl")
    Observable<URadioStreamModel> getRadioInfo();
}
