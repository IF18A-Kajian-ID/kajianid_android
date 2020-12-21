package com.kajianid.android;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    private String id;
    private String title;
    private String content;
    private String post_date;
    private boolean hasImg;

    public Article() {

    }

    protected Article(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        post_date = in.readString();
        hasImg = in.readByte() != 0;
        imgUrl = in.readString();
        likes = in.readInt();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPost_date() {
        return post_date;
    }

    public void setPost_date(String post_date) {
        this.post_date = post_date;
    }

    public boolean isHasImg() {
        return hasImg;
    }

    public void setHasImg(boolean hasImg) {
        this.hasImg = hasImg;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    private String imgUrl;
    private int likes;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(post_date);
        dest.writeByte((byte) (hasImg ? 1 : 0));
        dest.writeString(imgUrl);
        dest.writeInt(likes);
    }
}
