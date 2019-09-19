package com.example.farmersnet.messages;


import java.util.Date;

public class Message {

    private String id;
    private String text;
    private String image;
    private String user_id;
    private Date timestamp;

    public Message() {
    }

    public Message(String text, String image, String user_id) {
        this.text = text;
        this.image = image;
        this.user_id = user_id;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}

