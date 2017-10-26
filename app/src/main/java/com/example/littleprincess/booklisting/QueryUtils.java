package com.example.littleprincess.booklisting;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Little Princess on 10/19/2017.
 */

//A class which is used to connect to GoogleBooks API, retrieving data from it and parsing the data to get the results as a List of Books.
final class QueryUtils {
    private static final String LOG_TAG=QueryUtils.class.getName();
    private static final String TITLE="title";
    private static final String AUTHORS="authors";
    private static final String PUBLISHER="publisher";
    private static final String DATE="publishedDate";
    private static final String RATING="averageRating";
    private static final String IMAGE_LINK="imageLinks";
    private static final String INFO_LINK="infoLink";
    private static final String PRICE="retailPrice";
    private static final String AMOUNT="amount";
    private static final String CURRENCY="currencyCode";
    private static final String GOOGLE_BOOKS_API="https://www.googleapis.com/books/v1/volumes?q=";

    //Private constructor to guarantee not to create objects of this class
    private QueryUtils(){

    }
    // Adding the searchString to url of GoogleBooks API, getting the form of:
    //"https://www.googleapis.com/books/v1/volumes?q=searchString"
    private static String setSearchLink(String bookName){
        return GOOGLE_BOOKS_API+bookName;
    }

    //Getting URL object & handling its Exception
    private static URL getURL(String link){
        URL url=null;
        if (!TextUtils.isEmpty(link)&&link!=null) {
            try{
                url=new URL(link);
            } catch (MalformedURLException e){
                Log.e(LOG_TAG,"Error while getting URL object",e);
            }
        }
        return url;
    }

    //Connecting to the API and getting the InputStream, then convert it into String
    private static String startHttpURLConnection(URL url){
        HttpURLConnection urlConnection=null;
        InputStream inputStream=null;
        String output="";
        if(url!=null){

            try{
                urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                //If the connection was successful
                if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){
                    inputStream=urlConnection.getInputStream();
                    output=getJSONResponse(inputStream);
                }
            } catch (IOException e){
              Log.e(LOG_TAG,"Error while HttpURLConnection ",e);
            }finally {
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }
                if(inputStream!=null){
                  try {
                      inputStream.close();
                  }catch(IOException e){
                      Log.e(LOG_TAG,"Error while closing inputStream",e);
                  }
                }
            }


        }
        return output;
    }

    //Reading the result String from the InputStream
    private static String getJSONResponse(InputStream inputStream){
        String jsonResponse="";
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        if(inputStream!=null){
            inputStreamReader=new InputStreamReader(inputStream);
            bufferedReader=new BufferedReader(inputStreamReader);
            StringBuilder output=new StringBuilder();
           try{
               String line=bufferedReader.readLine();
               while(line!=null){
                   output.append(line);
                   line=bufferedReader.readLine();
               }
               jsonResponse=output.toString();
           }catch(IOException e){
               Log.e(LOG_TAG,"Error while reading String from inputStream",e);
           }finally {
              try {
                      inputStreamReader.close();
                      bufferedReader.close();
              }catch(IOException e){
                  Log.e(LOG_TAG,"Error while closing inputStreamReader and bufferedReader");
              }
           }

        }
        return jsonResponse;
    }

    //Parsing the data into JSONObjects to retrieve the details of each Book
    //Then add the Book to the List that will be returned to the BookListActivity
    private static List<Book> parseJSONResponse(String response){
        //initialize a new empty List to add Book items to it.
        List<Book> list=new ArrayList<>();
        if(response!=null && !TextUtils.isEmpty(response)){
          try{
              JSONObject mainJsonObj=new JSONObject(response);
              JSONArray arrayItems=mainJsonObj.getJSONArray("items");
              //To get every item in the list
              for (int i=0;i<arrayItems.length();i++){
                  JSONObject currentItem=arrayItems.getJSONObject(i);
                  JSONObject bookInfoJSONObject=currentItem.optJSONObject("volumeInfo");
                  String title=bookInfoJSONObject.optString(TITLE);

                  //authorsJSONArray will be null in case there is no key with name AUTHORS (some books in the API don't have authors)
                  JSONArray authorsJSONArray=bookInfoJSONObject.optJSONArray(AUTHORS);
                  String authorsNames="";

                  //If authorsJSONArray==null you will get a NullPointerException when invoking opt() method on it, so check it first
                  if(authorsJSONArray!=null) {
                      for (int j = 0; j < authorsJSONArray.length(); j++) {
                          authorsNames += (String) authorsJSONArray.opt(j);
                      }
                  }

                  String publisher =bookInfoJSONObject.optString(PUBLISHER);

                  String date=bookInfoJSONObject.optString(DATE);

                  float rate= ((float) bookInfoJSONObject.optDouble(RATING));


                  String infoLink=bookInfoJSONObject.optString(INFO_LINK);


                  //imageJSON will be null in case there is no key with name IMAGE_LINK
                  JSONObject imageJSON=bookInfoJSONObject.optJSONObject(IMAGE_LINK);
                  String imageLink="";
                  //some books don't have images so check it first to avoid NullPointerException
                  if(imageJSON!=null)
                  imageLink=(bookInfoJSONObject.optJSONObject(IMAGE_LINK)).optString("smallThumbnail");


                  JSONObject priceJson=currentItem.optJSONObject("saleInfo").optJSONObject(PRICE);
                  String price="";
                  if(priceJson!=null)
                  price=priceJson.optString(AMOUNT)+priceJson.optString(CURRENCY);

                  //Create a new Book object with the retrieved data then add it to the List
                  Book book=new Book(title,authorsNames,publisher,date,imageLink,infoLink,price,rate);
                  list.add(book);
              }

          } catch (JSONException e){
              Log.e(LOG_TAG,"Error while parsing JSONObject",e);
          }
        }
        return list;
    }


    //  A method which is called from SearchAsyncTaskLoader to download List of books
    static List<Book> getBookList(String link){
        return parseJSONResponse(startHttpURLConnection(getURL(setSearchLink(link))));
    }


}
