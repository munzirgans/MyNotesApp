package com.example.rifafauzi6.mynotesapp.Entity;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
    private int id;
    private String email;
    private String password;

    // Constructor
    public User(int id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    protected User(Parcel in) {
        id = in.readInt();
        email = in.readString();
        password = in.readString();
    }

    // Getter dan Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Menulis data ke Parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(email);
        dest.writeString(password);
    }

    @Override
    public int describeContents() {
        return 0;
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
}
