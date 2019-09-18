package com.example.farmersnet.post;

public class PostID {
    public String PostId;

    public <T extends PostID> T withId(final String id){
        this.PostId = id;
        return (T)this;
    }
//    public withId(String id){
//        this.PostId = id;
//        return ;
//    }
}
