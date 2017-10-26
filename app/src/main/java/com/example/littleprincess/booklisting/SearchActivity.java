package com.example.littleprincess.booklisting;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.support.v7.widget.AppCompatButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//The first class which takes the user input and pass it to the BookListActivity to start searching
public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    AppCompatButton searchButton;
    EditText SearchEditText;
    Spinner searchBySpinner, filterSpinner;

    //the text entered by the user on the EditText
    String searchString;

    //uses the user selection on the filterString Spinner
    String filterString = "";

    //uses the user selection on the searchBy Spinner
    String searchByString = "";

    //the index of selected item on the spinner (the default is 0 ,the first item is selected)
    int selectedSearchBySpinnerItem = 0;
    int selectedFilterSpinnerItem = 0;


    private static final String FILTER = "&filterString=";
    private static final String[] SEARCH_BY_ARRAY = {"", "+intitle:", "+inauthor:", "+inpublisher:", "+subject:"};
    private static final String LOG_TAG = SearchActivity.class.getName();

    /*
        The general form for the final String which is passed to BookListActivity is : SEARCH_BY_ARRAY[selectedItem]+searchString+FILTER+stringFilter

        The final String  has three sections:
        1-in case of the user entered "high" on the EditText & selected the "General" item on the "Search By" Spinner and "Free Google EBooks" on the Filter Spinner
            finalString=SEARCH_BY_ARRAY[0]+searchString+FILTER+"free-ebooks"  ==>>>  "high&filterString=free-ebooks"

        2-in case of the user entered "high" on the EditText & selected the "Author" item on the "Search By" Spinner and "Paid Google EBooks" on the Filter Spinner
            finalString=SEARCH_BY_ARRAY[2]+searchString+FILTER+"paid-ebooks"  ==>>> "+inauthor:high&filterString=paid-ebooks"
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchButton = (AppCompatButton) findViewById(R.id.search_button);
        SearchEditText = (EditText) findViewById(R.id.advanced_search_text);
        searchBySpinner = (Spinner) findViewById(R.id.search_by_spinner);
        filterSpinner = (Spinner) findViewById(R.id.filter_spinner);

        registerSpinner();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchString = SearchEditText.getText().toString();
                //check whether user has entered a text or not
                if ((searchString == null || TextUtils.isEmpty(searchString))) {
                    Toast.makeText(SearchActivity.this, getString(R.string.enter_book_name_toast), Toast.LENGTH_LONG).show();
                    return;
                }
                //Get the user selection on the SearchBySpinner and store it on searchByString
                checkSearchBySpinner();

                //Get the user selection on the FilterSpinner and store it on filterString
                checkFilterSpinner();
                //Check Network Connection then start the BookListActivity if it is connected
                startSearching();
            }
        });


    }

    private void startSearching() {
        if (checkNetworkStatus()) {
            startBookListActivity();
        } else {
            Toast.makeText(SearchActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
        }

    }

    //Check Network Connection , return true if it is connected
    private boolean checkNetworkStatus() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    //Start the BookListActivity passing the finalString of the user input and the user selection
    private void startBookListActivity() {
        Intent intent = new Intent(this, BookListActivity.class);
        Log.i(LOG_TAG, "the search string is: " + searchByString + filterString);
        intent.putExtra(BookListActivity.LINK_TAG, searchByString + filterString);
        startActivity(intent);

    }


    //Set the ArrayAdapter and OnItemSelectedListener for both Filter Spinner and Search By Spinner
    //this Activity implements AdapterView.OnItemSelectedListener Interface so use it as a Listener
    private void registerSpinner() {
        ArrayAdapter<CharSequence> filterSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.filter_menu, android.R.layout.simple_spinner_item);
        filterSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filterSpinnerAdapter);
        filterSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> searchBySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.search_by_menu, android.R.layout.simple_spinner_item);
        searchBySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchBySpinner.setAdapter(searchBySpinnerAdapter);
        searchBySpinner.setOnItemSelectedListener(this);


    }

    //Update the searchByString according to the user selection on the Search By Spinner
    private void checkSearchBySpinner() {
        searchByString = SEARCH_BY_ARRAY[selectedSearchBySpinnerItem] + searchString;
    }

    //update the filterString according to the user selection on the Filter Spinner
    private void checkFilterSpinner() {
        switch (selectedFilterSpinnerItem) {
            case 0:
                //The selection is "All Books" so no filter is applied
                filterString = "";
                break;
            case 1:
                filterString = FILTER + "partial";
                break;
            case 2:
                filterString = FILTER + "full";
                break;
            case 3:
                filterString = FILTER + "free-ebooks";
                break;
            case 4:
                filterString = FILTER + "paid-ebooks";
                break;
            case 5:
                filterString = FILTER + "ebooks";
                break;
        }

    }

    //This Class overrides the AdapterView.OnItemSelectedListener unimplemented methods
    //This method is called when the Spinner selected item is changed and when the Activity is started
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Check which Spinner has changed its selected item and save the index of the selected item
        switch (adapterView.getId()) {
            case R.id.filter_spinner:
                selectedFilterSpinnerItem = i;
                break;
            case R.id.search_by_spinner:
                selectedSearchBySpinnerItem = i;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}
