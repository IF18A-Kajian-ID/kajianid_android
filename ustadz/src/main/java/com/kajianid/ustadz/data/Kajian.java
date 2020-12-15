package com.kajianid.ustadz.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Kajian implements Parcelable {

    public Kajian() {

    }

    protected Kajian(Parcel in) {
        id = in.readString();
        title = in.readString();
        mosqueId = in.readString();
        mosqueName = in.readString();
        place = in.readString();
        address = in.readString();
        date = in.readString();
        description = in.readString();
        imgResource = in.readString();
        ytLink = in.readString();
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

    public String getMosqueId() {
        return mosqueId;
    }

    public void setMosqueId(String mosqueId) {
        this.mosqueId = mosqueId;
    }

    public String getMosqueName() {
        return mosqueName;
    }

    public void setMosqueName(String mosqueName) {
        this.mosqueName = mosqueName;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgResource() {
        return imgResource;
    }

    public void setImgResource(String imgResource) {
        this.imgResource = imgResource;
    }

    public String getYtLink() {
        return ytLink;
    }

    public void setYtLink(String ytLink) {
        this.ytLink = ytLink;
    }

    private String id;
    private String title;
    private String mosqueId;
    private String mosqueName;
    private String place;
    private String address;
    private String date;
    private String description;
    private String imgResource;
    private String ytLink;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(mosqueId);
        dest.writeString(mosqueName);
        dest.writeString(place);
        dest.writeString(address);
        dest.writeString(date);
        dest.writeString(description);
        dest.writeString(imgResource);
        dest.writeString(ytLink);
    }
}
