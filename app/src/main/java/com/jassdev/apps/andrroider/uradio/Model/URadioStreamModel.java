
package com.jassdev.apps.andrroider.uradio.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jassdev.apps.andrroider.uradio.Model.Icestats;

public class URadioStreamModel {

    @SerializedName("icestats")
    @Expose
    private Icestats icestats;

    public Icestats getIcestats() {
        return icestats;
    }

    public void setIcestats(Icestats icestats) {
        this.icestats = icestats;
    }

}
