package com.kajianid.admin.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Ustadz implements Parcelable {
    public static final Creator<com.kajianid.ustadz.data.Ustadz> CREATOR = new Creator<com.kajianid.ustadz.data.Ustadz>() {
        @Override
        public com.kajianid.ustadz.data.Ustadz createFromParcel(Parcel in) {
            return new com.kajianid.ustadz.data.Ustadz(in);
        }

        @Override
        public com.kajianid.ustadz.data.Ustadz[] newArray(int size) {
            return new com.kajianid.ustadz.data.Ustadz[size];
        }
    };
    private String name;
    private String phone;
    private String address;
    private String email;
    private String gender;

    public Ustadz() {

    }

    protected Ustadz(Parcel in) {
        name = in.readString();
        phone = in.readString();
        address = in.readString();
        email = in.readString();
        gender = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(email);
        dest.writeString(gender);
    }
}
