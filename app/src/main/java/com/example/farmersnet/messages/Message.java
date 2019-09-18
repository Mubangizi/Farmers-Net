package com.example.farmersnet.messages;


import java.util.Date;

public class Message {

    private String id;
    private String text;
    private String image;
    private Date timestamp;

    public Message() {
    }

    public Message(String text, String photoUrl, String image) {
        this.text = text;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return image;
    }

    public void setImageUrl(String imageUrl) {
        this.image = imageUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

