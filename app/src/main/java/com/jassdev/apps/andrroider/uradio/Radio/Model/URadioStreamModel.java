
package com.jassdev.apps.andrroider.uradio.Radio.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class URadioStreamModel {

    @SerializedName("/live")
    @Expose
    private Live live;
    @SerializedName("/nonstop")
    @Expose
    private NonstopHD nonstop;

    public Live getLive() {
        return live;
    }

    public void setLive(Live live) {
        this.live = live;
    }

    public NonstopHD getNonstop() {
        return nonstop;
    }

    public void setNonstop(NonstopHD nonstop) {
        this.nonstop = nonstop;
    }

}
