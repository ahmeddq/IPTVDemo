package com.example.asadabbas.iptvdemo.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "iptv", id = "dbId")
public class IPTV extends Model implements Parcelable {

    @Column(name = "id")
    long id1;

    public long getId1() {
        return id1;
    }

    public void setId1(long id1) {
        this.id1 = id1;
    }

    @Column(name = "Name")
    String name;

    @Column(name = "Path")
    String path;

    @Column(name = "isFav")
    boolean isFav;

    @Column(name = "Type")
    String Type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public IPTV() {
        super();
    }

    public IPTV(String name, String path, boolean isFav, String type) {
        this.name = name;
        this.path = path;
        this.isFav = isFav;
        this.Type = type;
    }

    protected IPTV(Parcel in) {
        name = in.readString();
        path = in.readString();
        isFav = in.readByte() != 0;
        Type = in.readString();
    }

    public static final Creator<IPTV> CREATOR = new Creator<IPTV>() {
        @Override
        public IPTV createFromParcel(Parcel in) {
            return new IPTV(in);
        }

        @Override
        public IPTV[] newArray(int size) {
            return new IPTV[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeByte((byte) (isFav ? 1 : 0));
        dest.writeString(Type);
    }
}
