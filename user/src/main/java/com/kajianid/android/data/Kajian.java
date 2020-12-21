package com.kajianid.android.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Kajian implements Parcelable {
    protected Kajian(Parcel in) {
        id = in.readString();
        title = in.readString();
        mosque = in.readString();
        place = in.readString();
        ustadzName = in.readString();
        description = in.readString();
        address = in.readString();
        youtubelink = in.readString();
        date = in.readString();
        dateAnnounce = in.readString();
        imgResource = in.readString();
    }

    public Kajian() {

    }

    public static final Creator<Kajian> CREATOR = new Creator<Kajian>() {
        @Override
        public Kajian createFromParcel(Parcel in) {
            return new Kajian(in);
        }

        @Override
        public Kajian[] newArray(int size) {
            return new Kajian[size];
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

    public String getMosque() {
        return mosque;
    }

    public void setMosque(String mosque) {
        this.mosque = mosque;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getUstadzName() {
        return ustadzName;
    }

    public void setUstadzName(String ustadzName) {
        this.ustadzName = ustadzName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getYoutubelink() {
        return youtubelink;
    }

    public void setYoutubelink(String youtubelink) {
        this.youtubelink = youtubelink;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateAnnounce() {
        return dateAnnounce;
    }

    public void setDateAnnounce(String dateAnnounce) {
        this.dateAnnounce = dateAnnounce;
    }

    public String getImgResource() {
        return imgResource;
    }

    public void setImgResource(String imgResource) {
        this.imgResource = imgResource;
    }

    private String id;
    private String title;
    private String mosque;
    private String place;
    private String ustadzName;
    private String description;
    private String address;
    private String youtubelink;
    private String date;
    private String dateAnnounce;
    private String imgResource;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(mosque);
        dest.writeString(place);
        dest.writeString(ustadzName);
        dest.writeString(description);
        dest.writeString(address);
        dest.writeString(youtubelink);
        dest.writeString(date);
        dest.writeString(dateAnnounce);
        dest.writeString(imgResource);
    }
}
