package com.kajianid.ustadz.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Mosque implements Parcelable {
    protected Mosque(Parcel in) {
        id = in.readInt();
        mosqueName = in.readString();
        latLng = in.readString();
        address = in.readString();
    }

    public Mosque() {

    }

    public static final Creator<Mosque> CREATOR = new Creator<Mosque>() {
        @Override
        public Mosque createFromParcel(Parcel in) {
            return new Mosque(in);
        }

        @Override
        public Mosque[] newArray(int size) {
            return new Mosque[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMosqueName() {
        return mosqueName;
    }

    public void setMosqueName(String mosqueName) {
        this.mosqueName = mosqueName;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private int id;
    private String mosqueName;
    private String latLng;
    private String address;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mosqueName);
        dest.writeString(latLng);
        dest.writeString(address);
    }
}
