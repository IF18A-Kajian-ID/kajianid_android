package com.kajianid.android.activities;

import android.os.Parcel;
import android.os.Parcelable;

public class DataSessionHandler implements Parcelable {
    private String username;
    private String password;
    private String nama_lengkap;

    protected DataSessionHandler(Parcel in) {
        username = in.readString();
        password = in.readString();
        nama_lengkap = in.readString();
        email = in.readString();
    }

    public static final Creator<DataSessionHandler> CREATOR = new Creator<DataSessionHandler>() {
        @Override
        public DataSessionHandler createFromParcel(Parcel in) {
            return new DataSessionHandler(in);
        }

        @Override
        public DataSessionHandler[] newArray(int size) {
            return new DataSessionHandler[size];
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama_lengkap() {
        return nama_lengkap;
    }

    public void setNama_lengkap(String nama_lengkap) {
        this.nama_lengkap = nama_lengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private String email;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(nama_lengkap);
        parcel.writeString(email);
    }
}
