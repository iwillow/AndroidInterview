package com.iwillow.app.android.interview;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by iwillow on 2017/10/17.
 */

public class Book  implements Parcelable{
    String bookId;
    String bookName;

    protected Book(Parcel in) {
        bookId = in.readString();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bookId);
        dest.writeString(bookName);
    }
}
