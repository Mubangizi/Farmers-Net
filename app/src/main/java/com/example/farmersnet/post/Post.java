package com.example.farmersnet.post;

import java.util.Date;

public class Post extends PostID {
    private String title;
    private String article;
    private String image;
    private String user_id;
    private Date timestamp;

    public Post() {

    }
    public Post(String title, String article) {
        this.title = title;
        this.article = article;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
