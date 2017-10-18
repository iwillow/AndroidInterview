// IBookManager.aidl
package com.iwillow.app.android.interview;

import com.iwillow.app.android.interview.Book;

interface IBookManager {

    List<Book> getBookList();

    void addBook(in Book book);

}
