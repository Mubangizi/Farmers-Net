package com.example.farmersnet.users;

import com.example.farmersnet.post.PostID;

public class User extends PostID{

    private String fname;
    private String lname;
    private String profile_image;
    private String email;

    public User(){

    }

    public User(String fname, String lname, String profile_image, String email) {
        this.fname = fname;
        this.lname = lname;
        this.profile_image = profile_image;
        this.email = email;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
