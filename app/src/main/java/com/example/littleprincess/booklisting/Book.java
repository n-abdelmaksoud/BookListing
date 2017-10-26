package com.example.littleprincess.booklisting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Little Princess on 10/19/2017.
 */

 //Class to contain all the details of each book which is retrieved from Google Books API

 class Book {

    private String title,authors,publisher,smallImageLink,infoLink,price;
    private float rating;
    private String year="";


    public Book(String title, String authors, String publisher, String date,String smallImageLink, String infoLink, String price, float rating) {
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.smallImageLink = smallImageLink;
        this.infoLink = infoLink;
        this.price = price;
        this.rating = rating;

        //the retrieved date has two forms: "yyyy" and "yyyy-MM-dd"
        // i want to display date in form "yyyy" only
        if(date.length()<=4 && date.length()>0)
            this.year=", "+date;
        else if(date.length()>4)
        this.year=", "+getYearString(covertDateToMilliseconds(date));
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getSmallImageLink() {
        return smallImageLink;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public String getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public String getYear(){return year;}


    // method which takes a date as String "yyyy-MM-dd" and return it in Milliseconds long number
    private long covertDateToMilliseconds(String date){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
        Date dateInMill=new Date();
        try {
            dateInMill = dateFormat.parse(date);
        }catch(ParseException e){
            e.printStackTrace();
        }
        return dateInMill.getTime();
    }

    //This method is used to get only the year as String from a long date in Milliseconds(which is input to the method)
    private String getYearString(long dateInMilli){
        Date dateObject=new Date(dateInMilli);
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
        return dateFormat.format(dateObject);
    }
}
