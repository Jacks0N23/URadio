
package com.jassdev.apps.andrroider.uradio.radio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class URadioStreamModel {

    @SerializedName("/nonstop128kbs")
    @Expose
    private Nonstop nonstop;
    @SerializedName("/nonstop320kbs")
    @Expose
    private Nonstop nonstopHD;

    public Nonstop getNonstop() {
        return nonstop;
    }

    public Nonstop getNonstopHD() {
        return nonstopHD;
    }
}
