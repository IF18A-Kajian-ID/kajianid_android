package com.kajianid.android.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {
    protected Article(Parcel in) {
        id = in.readString();
        title = in.readString();
        content = in.readString();
        postDate = in.readString();
        ustadzName = in.readString();
        hasImg = in.readString();
        imgUrl = in.readString();
        likes = in.readInt();
    }

    public Article() {

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

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getUstadzName() {
        return ustadzName;
    }

    public void setUstadzName(String ustadzName) {
        this.ustadzName = ustadzName;
    }

    public String getHasImg() {
        return hasImg;
    }

    public void setHasImg(String hasImg) {
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

    private String id;
    private String title;
    private String content;
    private String postDate;
    private String ustadzName;
    private String hasImg;
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
        dest.writeString(postDate);
        dest.writeString(ustadzName);
        dest.writeString(hasImg);
        dest.writeString(imgUrl);
        dest.writeInt(likes);
    }
}
