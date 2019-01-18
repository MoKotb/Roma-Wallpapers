package com.yomko.romawallpapers.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String userID;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userType;

    public User() {
    }

    public User(String userName, String userEmail, String userPhone, String userType) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userType = userType;
    }

    protected User(Parcel in) {
        userID = in.readString();
        userName = in.readString();
        userEmail = in.readString();
        userPhone = in.readString();
        userType = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userID);
        dest.writeString(userName);
        dest.writeString(userEmail);
        dest.writeString(userPhone);
        dest.writeString(userType);
    }
}
