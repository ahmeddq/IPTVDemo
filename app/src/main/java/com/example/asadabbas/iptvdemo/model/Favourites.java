package com.example.asadabbas.iptvdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "fav")
public class Favourites extends Model implements Parcelable {

    public Favourites() {
        super();
    }

    @Column(name = "Path")
    String path;

    @Column(name = "isFav")
    boolean isFav;

    @Column(name = "stream")
    boolean isStream;

    public boolean isStream() {
        return isStream;
    }

    public void setStream(boolean stream) {
        isStream = stream;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFav() {
        return isFav;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    protected Favourites(Parcel in) {
        path = in.readString();
        isFav = in.readByte() != 0;
        isStream = in.readByte() != 0;
    }

    public static final Creator<Favourites> CREATOR = new Creator<Favourites>() {
        @Override
        public Favourites createFromParcel(Parcel in) {
            return new Favourites(in);
        }

        @Override
        public Favourites[] newArray(int size) {
            return new Favourites[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeByte((byte) (isFav ? 1 : 0));
        dest.writeByte((byte) (isStream ? 1 : 0));
    }
}
