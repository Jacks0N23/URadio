package com.jassdev.apps.andrroider.uradio.radio.presenter

import android.util.Log
import com.jassdev.apps.andrroider.uradio.Api.URadioApi
import com.jassdev.apps.andrroider.uradio.Utils.Const
import com.jassdev.apps.andrroider.uradio.Utils.Utils
import com.jassdev.apps.andrroider.uradio.radio.model.URadioStreamModel
import com.jassdev.apps.andrroider.uradio.radio.view.MainView
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * Created by Jackson on 06/01/2017.
 */
class RadioPresenter(private val mView: MainView) : Presenter {

    private val TAG = "RadioPresenter"
    private val api: URadioApi = Utils.createRxService(URadioApi::class.java, Const.RADIO_BASE_URL, true)
    private var radioInfo: Observable<URadioStreamModel> = api.radioInfo.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    private var track: String? = null

    override fun getTrackInfo() {
        if (mView.isControlActivated) {
            radioInfo.repeatWhen {
                it.flatMap { Observable.timer(Const.LOAD_REFRESH_TIME.toLong(), TimeUnit.SECONDS, Schedulers.io()) }
            }
                .subscribe({
                    track = it.nonstopHD.title ?: it.nonstop.title
                    if (track != null && track != mView.trackTitle) {
                        mView.trackTitle = track
                        mView.refreshNotification()
                    }
                }, {
                    Log.e(TAG, "onError: ", it)
                    mView.showToast("Не удалось получить название трека")
                })
        }
    }
}
