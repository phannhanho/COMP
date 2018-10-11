package com.example.nhan.assignmentapp.helper;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Image implements Serializable {
    private long id;
    private Date timestamp;
    private String caption;
    private String filePath;

    private Image() {
        this(Calendar.getInstance().getTime().getTime());
    }

    private Image(long id) {
        this.id = id;
    }

    public Image(Date timestamp, String filePath) {
        this();
        this.caption = "";
        this.timestamp = timestamp;
        this.filePath = filePath;
    }

    public Image(Date timestamp, String caption, String filePath) {
        this();
        this.timestamp = timestamp;
        this.caption = caption;
        this.filePath = filePath;
    }

    public Image(long id, Date timestamp, String caption, String filePath) {
        this.id = id;
        this.timestamp = timestamp;
        this.caption = caption;
        this.filePath = filePath;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
    public void setTimestamp(String timestamp) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        this.timestamp = sdf.parse(timestamp);
    }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}

