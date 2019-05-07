package com.kaadas.lock.utils.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by ${howard} on 2018/4/16.
 */

public class MediaItem implements Comparator<MediaItem>, Comparable<MediaItem>,Parcelable {
    private String name;
    private String path;
    private int mediaType;
    private String crateTime;
    private boolean isSelected;
    public MediaItem(){}
    public MediaItem(String name, String path){
        this.name=name;
        this.path=path;
    }

    protected MediaItem(Parcel in) {
        name = in.readString();
        path = in.readString();
        mediaType = in.readInt();
        crateTime = in.readString();
        isSelected = in.readByte() != 0;
    }

    public static final Creator<MediaItem> CREATOR = new Creator<MediaItem>() {
        @Override
        public MediaItem createFromParcel(Parcel in) {
            return new MediaItem(in);
        }

        @Override
        public MediaItem[] newArray(int size) {
            return new MediaItem[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }
    public void setCrateTime(String crateTime) {
        this.crateTime = crateTime;
    }
    public String getName(){
        return name;
    }
    public String getPath(){
        return path;
    }
    public int getMediaType() {
        return mediaType;
    }
    public String getCreateTime(){
        return crateTime;
    }
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getTime(String dateTime){
        return getTimeDate(new Date(dateTime));
    }
    public static String getTimeDate(Date date) {
        int year  = date.getYear() + 1900;
        int month = date.getMonth() + 1;
        int day   = date.getDate();
        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeInt(mediaType);
        dest.writeString(crateTime);
        dest.writeByte((byte) (isSelected ? 1 : 0));
    }

    @Override
    public int compareTo(@NonNull MediaItem o) {
        return 0;
    }

    @Override
    public int compare(MediaItem o1, MediaItem o2) {
        return 0;
    }


	@Override
	public String toString() {
		return "MediaItem{" +
			"name='" + name + '\'' +
			", path='" + path + '\'' +
			", mediaType=" + mediaType +
			", crateTime='" + crateTime + '\'' +
			", isSelected=" + isSelected +
			'}';
	}
}
