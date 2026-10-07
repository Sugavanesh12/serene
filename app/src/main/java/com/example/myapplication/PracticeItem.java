package com.example.myapplication;

public class PracticeItem {
    private String id;
    private String title;
    private String description;
    private String duration;
    private String category;
    private boolean isDownloaded;

    public PracticeItem(String id, String title, String description, String duration, String category, boolean isDownloaded) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.category = category;
        this.isDownloaded = isDownloaded;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDuration() { return duration; }
    public String getCategory() { return category; }
    public boolean isDownloaded() { return isDownloaded; }
    public void setDownloaded(boolean downloaded) { isDownloaded = downloaded; }
}
