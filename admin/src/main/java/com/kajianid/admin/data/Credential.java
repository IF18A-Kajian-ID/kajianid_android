package com.kajianid.admin.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Credential implements Parcelable {

    public static final Creator<Credential> CREATOR = new Creator<Credential>() {
        @Override
        public Credential createFromParcel(Parcel in) {
            return new Credential(in);
        }

        @Override
        public Credential[] newArray(int size) {
            return new Credential[size];
        }
    };
    String username = null;
    String password = null;

    public Credential() {
        // empty public constructor
    }

    protected Credential(Parcel in) {
        username = in.readString();
        password = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(password);
    }
}
