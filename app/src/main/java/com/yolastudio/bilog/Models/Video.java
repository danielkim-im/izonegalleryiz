package com.yolastudio.bilog.Models;

public class Video {

    private String postKey;
    private String videoID;
    private String thumbnail;
    private String name;

    public Video(String name, String videoID, String thumbnail) {
        this.name = name;
        this.videoID = videoID;
        this.thumbnail = thumbnail;
    }

    public Video(){

    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
