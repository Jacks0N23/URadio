package com.jassdev.apps.andrroider.uradio.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Jackson on 04/01/2017.
 */

public class Utils {

    public static <T> T createRxService(final Class<T> rxService, String baseUrl, boolean withLog) {
        if (withLog) {
            Retrofit rxRetrofit =
                    new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(baseUrl)
                            .client(HttpClientWithLog())
                            .build();
            return rxRetrofit.create(rxService);
        } else {
            return new Retrofit.Builder().addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .client(HttpClientWithoutLog())
                    .build()
                    .create(rxService);
        }
    }

    private static OkHttpClient HttpClientWithoutLog() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
        client.retryOnConnectionFailure(true);
        return client.build();
    }

    private static OkHttpClient HttpClientWithLog() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.addInterceptor(logging);
        client.connectTimeout(15, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
        client.retryOnConnectionFailure(true);
        return client.build();
    }

    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager connectivity =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return false;
    }

    public static void enableWiFi(Context c, boolean wifi) {
        WifiManager wifiConfiguration = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
        wifiConfiguration.setWifiEnabled(wifi);
    }

}
