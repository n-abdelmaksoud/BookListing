package com.example.littleprincess.booklisting;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Little Princess on 10/20/2017.
 */

// This Activity displays the search results in ListView

public class BookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>>{
    static final String LINK_TAG="link tag";
    private static final String LOG_TAG=BookListActivity.class.getName();
    ListView listView;
    private static final int BOOK_LIST_LOADER=1;
    private String search;
    private BookListAdapter bookListAdapter;
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        listView=(ListView)findViewById(R.id.list_view);
        bar=(ProgressBar)findViewById(R.id.progress);

        //The keyword of the search (from Search Activity) which will used in url to retrieve its according data
        search=getIntent().getStringExtra(LINK_TAG);

        //initialize the adapter with new empty List of books
        bookListAdapter=new BookListAdapter(this,0,new ArrayList<Book>());
        listView.setAdapter(bookListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String link=(bookListAdapter.getItem(i)).getInfoLink();
                if(link!=null&& !TextUtils.isEmpty(link)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    startActivity(intent);
                }
            }
        });

        //show the progress bar till the load is finished
        bar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        //start the Loader to load the required data
        getLoaderManager().initLoader(BOOK_LIST_LOADER,null,this);


    }

    //This method will be called after initLoader() when there is no Loader with the entered id and it is the first time to create it.
    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        //create a new AsyncTaskLoader which takes two arguments:
        //first one: this activity which is a listener to LoaderManager.LoaderCallbacks<List<Book>>
        //second one: the String search to be used in url
        return new SearchAsyncTaskLoader(this,search);
    }
    //This method will be called when the loading is finished passing the List of books
    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        //set progress bat to be invisible because loading is finished and the data will be displayed
        bar.setVisibility(View.INVISIBLE);

        //clear the data stored in the list from previous search
        bookListAdapter.clear();

        //check the retrieved list.
        if(!books.isEmpty()) {
            //retrieved list has items to display so add all of the list to the adapter list
            bookListAdapter.addAll(books);
            bookListAdapter.notifyDataSetChanged();
            listView.setVisibility(View.VISIBLE);
        } else {
            //retrieved list is empty so finish this activity and get back to Search Activity
            Toast.makeText(this,getString(R.string.no_results),Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        bookListAdapter.clear();
    }
}
