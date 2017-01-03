
package com.jassdev.apps.andrroider.uradio.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("listeners")
    @Expose
    private Integer listeners;
    @SerializedName("listenurl")
    @Expose
    private String listenurl;
    @SerializedName("dummy")
    @Expose
    private Object dummy;
    @SerializedName("bitrate")
    @Expose
    private Integer bitrate;
    @SerializedName("genre")
    @Expose
    private String genre;
    @SerializedName("listener_peak")
    @Expose
    private Integer listenerPeak;
    @SerializedName("server_description")
    @Expose
    private String serverDescription;
    @SerializedName("server_name")
    @Expose
    private String serverName;
    @SerializedName("server_type")
    @Expose
    private String serverType;
    @SerializedName("server_url")
    @Expose
    private String serverUrl;
    @SerializedName("stream_start")
    @Expose
    private String streamStart;
    @SerializedName("stream_start_iso8601")
    @Expose
    private String streamStartIso8601;
    @SerializedName("title")
    @Expose
    private String title;

    public Integer getListeners() {
        return listeners;
    }

    public void setListeners(Integer listeners) {
        this.listeners = listeners;
    }

    public String getListenurl() {
        return listenurl;
    }

    public void setListenurl(String listenurl) {
        this.listenurl = listenurl;
    }

    public Object getDummy() {
        return dummy;
    }

    public void setDummy(Object dummy) {
        this.dummy = dummy;
    }

    public Integer getBitrate() {
        return bitrate;
    }

    public void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getListenerPeak() {
        return listenerPeak;
    }

    public void setListenerPeak(Integer listenerPeak) {
        this.listenerPeak = listenerPeak;
    }

    public String getServerDescription() {
        return serverDescription;
    }

    public void setServerDescription(String serverDescription) {
        this.serverDescription = serverDescription;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getStreamStart() {
        return streamStart;
    }

    public void setStreamStart(String streamStart) {
        this.streamStart = streamStart;
    }

    public String getStreamStartIso8601() {
        return streamStartIso8601;
    }

    public void setStreamStartIso8601(String streamStartIso8601) {
        this.streamStartIso8601 = streamStartIso8601;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
