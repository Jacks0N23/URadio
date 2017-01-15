package com.jassdev.apps.andrroider.uradio.Radio.Presenter;

import android.util.Log;

import com.jassdev.apps.andrroider.uradio.Api.URadioApi;
import com.jassdev.apps.andrroider.uradio.Radio.Model.URadioStreamModel;
import com.jassdev.apps.andrroider.uradio.Radio.View.MainView;
import com.jassdev.apps.andrroider.uradio.Utils.Const;
import com.jassdev.apps.andrroider.uradio.Utils.Utils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jackson on 06/01/2017.
 */

public class RadioPresenter implements Presenter {

    private static final String TAG = "RadioPresenter";
    private MainView mView;
    private URadioApi api;
    private Observable<URadioStreamModel> observable;
    private String track;


    public RadioPresenter(MainView mView) {
        this.mView = mView;
        api = Utils.createRxService(URadioApi.class, Const.RADIO_BASE_URL, true);
    }

    @Override
    public void getTrackInfo() {
        if (mView.isControlActivated()) {
            getRequest().single().subscribe(getSubscriber());
            Observable.timer(Const.LOAD_REFRESH_TIME, TimeUnit.SECONDS, Schedulers.io()).subscribe(new Subscriber<Long>() {
                @Override
                public void onCompleted() {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onNext(Long aLong) {
                    getTrackInfo();
                }
            });
        }
    }

    private Observable<URadioStreamModel> getRequest() {
        if (observable == null) {
            observable = api
                    .getRadioInfo()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return observable;
    }

    private Subscriber<URadioStreamModel> getSubscriber() {
        return new Subscriber<URadioStreamModel>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                mView.showToast("Не удалось получить название трека");
            }

            @Override
            public void onNext(URadioStreamModel uRadioStreamModel) {
                track = searchTitleInList(uRadioStreamModel);
                //небольшая оптимизация, чтобы мы обновляли тайтл только когда разные названия трека у нас и в ответе от сервера
                if (track != null && !track.equals(mView.getTrackTitle())) {
                    mView.setTrackTitle(track);
                    mView.refreshNotification();
                }
            }
        };
    }


    private String searchTitleInList(URadioStreamModel uRadioStreamModel) {
        if (uRadioStreamModel.getNonstop().getTitle() != null)
            return uRadioStreamModel.getNonstop().getTitle();
        else if (uRadioStreamModel.getLive().getTitle() != null)
            return uRadioStreamModel.getLive().getTitle();
        else
            return null;
    }

}
