package com.yomko.romawallpapers.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {

    private String imageID;
    private String imageName;
    private String imageURL;
    private String categoryID;

    public Image() {
    }

    public Image(String imageName, String imageURL, String categoryID) {
        this.imageName = imageName;
        this.imageURL = imageURL;
        this.categoryID = categoryID;
    }

    protected Image(Parcel in) {
        imageID = in.readString();
        imageName = in.readString();
        imageURL = in.readString();
        categoryID = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(imageID);
        dest.writeString(imageName);
        dest.writeString(imageURL);
        dest.writeString(categoryID);
    }
}
