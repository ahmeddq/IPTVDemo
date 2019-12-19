/*
 * Copyright 2016 Emanuele Papa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.asadabbas.iptvdemo.parse.data;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Created by Emanuele on 31/08/2016.
 */
@Data
public class ExtInfo implements Parcelable {

    private String duration;
    private String tvgId;
    private String tvgName;
    private String tvgLogoUrl;
    private String groupTitle;
    private String title;
    private String tvUrl;

    public ExtInfo() {
    }


    protected ExtInfo(Parcel in) {
        duration = in.readString();
        tvgId = in.readString();
        tvgName = in.readString();
        tvgLogoUrl = in.readString();
        groupTitle = in.readString();
        title = in.readString();
        tvUrl = in.readString();
    }

    public static final Creator<ExtInfo> CREATOR = new Creator<ExtInfo>() {
        @Override
        public ExtInfo createFromParcel(Parcel in) {
            return new ExtInfo(in);
        }

        @Override
        public ExtInfo[] newArray(int size) {
            return new ExtInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(duration);
        dest.writeString(tvgId);
        dest.writeString(tvgName);
        dest.writeString(tvgLogoUrl);
        dest.writeString(groupTitle);
        dest.writeString(title);
        dest.writeString(tvUrl);
    }
}
