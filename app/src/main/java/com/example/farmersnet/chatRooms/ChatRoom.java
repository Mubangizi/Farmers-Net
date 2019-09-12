package com.example.farmersnet.chatRooms;

import java.util.Date;

public class ChatRoom {
    private String name;
    private String description;
    private String image;
    private Date timestamp;

    public ChatRoom(){
    }

    public ChatRoom(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
