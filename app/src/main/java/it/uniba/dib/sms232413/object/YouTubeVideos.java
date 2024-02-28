package it.uniba.dib.sms232413.object;

public class YouTubeVideos {

    private String videoUrl;
    private String videoTitle;

    public YouTubeVideos() {
    }

    public YouTubeVideos(String videoUrl, String videoTitle) {
        this.videoUrl = videoUrl;
        this.videoTitle = videoTitle;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

}