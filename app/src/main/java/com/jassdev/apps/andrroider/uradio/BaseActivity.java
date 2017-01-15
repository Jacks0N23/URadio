package com.jassdev.apps.andrroider.uradio;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.jassdev.apps.andrroider.uradio.Utils.Utils;

/**
 * Created by Jackson on 15/01/2017.
 */

public class BaseActivity extends AppCompatActivity {

    public void initInternetConnectionDialog(final Context context) {

        new AlertDialog.Builder(context).setTitle("Соединение нестабильно или прервано")
                .setMessage("Проверьте своё соединение с интернетом и перезайдите в приложение")

                .setPositiveButton("Перезайти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNeutralButton("Включить Wi-Fi?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utils.enableWiFi(context, true);
                    }
                })
                .create().show();
    }
}
