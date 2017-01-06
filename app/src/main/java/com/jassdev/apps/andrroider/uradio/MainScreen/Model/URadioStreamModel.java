
package com.jassdev.apps.andrroider.uradio.MainScreen.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class URadioStreamModel {

    @SerializedName("icestats")
    @Expose
    private Icestats icestats;

    public Icestats getIcestats() {
        return icestats;
    }
}
