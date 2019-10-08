package com.yun.software.kaadas.UI.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class HomeCategoriesBean implements Parcelable {

    private String id;

    private String img;

    private String name;

    private String description;

    public HomeCategoriesBean(String id,String name){
        this.id = id;
        this.name = name;
    }


    protected HomeCategoriesBean(Parcel in) {
        id = in.readString();
        img = in.readString();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<HomeCategoriesBean> CREATOR = new Creator<HomeCategoriesBean>() {
        @Override
        public HomeCategoriesBean createFromParcel(Parcel in) {
            return new HomeCategoriesBean(in);
        }

        @Override
        public HomeCategoriesBean[] newArray(int size) {
            return new HomeCategoriesBean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "HomeCategoriesBean{" +
                "id='" + id + '\'' +
                ", img='" + img + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(img);
        dest.writeString(name);
        dest.writeString(description);
    }
}
