package com.example.farmersnet.post;

public class Post {
    String title;
    String article;

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

    public Post() {

    }
    public Post(String title, String article) {
        this.title = title;
        this.article = article;
    }
}
