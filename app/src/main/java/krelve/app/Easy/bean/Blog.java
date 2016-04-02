package krelve.app.Easy.bean;

import java.io.Serializable;
import java.security.KeyStore.PrivateKeyEntry;



public class Blog implements Serializable{
    private String title;
    private String content;
    private int blogsId;
    private int id;
    private String take_date;
    private int islight;
    private String limageurls;
    private String simageurls;
    private String username;
    private String userheader;
    private int seecount;
    private int replycount;
    private int replyid;
    private String imageurl;


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }



    public int getReplyid() {
        return replyid;
    }

    public void setReplyid(int replyid) {
        this.replyid = replyid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getBlogsId() {
        return blogsId;
    }

    public void setBlogsId(int blogsId) {
        this.blogsId = blogsId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTake_date() {
        return take_date;
    }

    public void setTake_date(String take_date) {
        this.take_date = take_date;
    }

    public int getIslight() {
        return islight;
    }

    public void setIslight(int islight) {
        this.islight = islight;
    }

    public String getLimageurls() {
        return limageurls;
    }

    public void setLimageurls(String limageurls) {
        this.limageurls = limageurls;
    }

    public String getSimageurls() {
        return simageurls;
    }

    public void setSimageurls(String simageurls) {
        this.simageurls = simageurls;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserheader() {
        return userheader;
    }

    public void setUserheader(String userheader) {
        this.userheader = userheader;
    }

    public int getSeecount() {
        return seecount;
    }

    public void setSeecount(int seecount) {
        this.seecount = seecount;
    }

    public int getReplycount() {
        return replycount;
    }

    public void setReplycount(int replycount) {
        this.replycount = replycount;
    }

}
