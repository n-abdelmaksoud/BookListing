package com.example.littleprincess.booklisting;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

/**
 * Created by Little Princess on 10/19/2017.
 */

 class SearchAsyncTaskLoader extends AsyncTaskLoader<List<Book>>{

   private String bookNameSearch;

    SearchAsyncTaskLoader(Context c,String search){
        super(c);
        this.bookNameSearch=search;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        //Getting the List of results from QueryUtils.getBookList() passing the bookNameSearch String which represents the user input and selection
        return QueryUtils.getBookList(bookNameSearch);
    }
}
