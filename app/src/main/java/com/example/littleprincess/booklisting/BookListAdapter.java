package com.example.littleprincess.booklisting;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Little Princess on 10/20/2017.
 */

class BookListAdapter extends ArrayAdapter<Book> {
   private Activity context;
    private List<Book> list;


    BookListAdapter(Activity context,int resource, List<Book> objects) {
        super(context,resource, objects);
        this.context=context;
        this.list=objects;
    }


    private class Holder{
        TextView title,author,publisher,price;
        RatingBar ratingBar;
        ImageView imageView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        Holder holder;
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.book_list_item,parent,false);
            holder=new Holder();
            holder.title=view.findViewById(R.id.title);
            holder.author=view.findViewById(R.id.author);
            holder.publisher=view.findViewById(R.id.publisher);
            holder.price=view.findViewById(R.id.price);
            holder.ratingBar=view.findViewById(R.id.rating_bar);
            holder.imageView=view.findViewById(R.id.image);
            view.setTag(holder);
        } else {
            holder=(Holder)view.getTag();
        }
        Book currentBookItem=list.get(position);
        holder.title.setText(currentBookItem.getTitle());

        //check author String
        if(currentBookItem.getAuthors()!=null&&!TextUtils.isEmpty(currentBookItem.getAuthors())) {
            holder.author.setVisibility(View.VISIBLE);
            holder.author.setText(currentBookItem.getAuthors());
        }
        else
            holder.author.setVisibility(View.GONE);

        //check publisher String.
        if(currentBookItem.getPublisher()!=null&&!TextUtils.isEmpty(currentBookItem.getPublisher())) {
            holder.publisher.setVisibility(View.VISIBLE);
            //year is displayed with the publisher in the same TextView
            holder.publisher.setText(currentBookItem.getPublisher()+currentBookItem.getYear());
        }
        else
            holder.publisher.setVisibility(View.GONE);

        //check price String
        String price=currentBookItem.getPrice();
        if(!TextUtils.isEmpty(price)){
            holder.price.setVisibility(View.VISIBLE);
            holder.price.setText(currentBookItem.getPrice());
        } else {
            holder.price.setVisibility(View.GONE);
        }

        //check the rate
        float rate=currentBookItem.getRating();
        if(rate>=0){
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.ratingBar.setRating(rate);
        } else{
            holder.ratingBar.setVisibility(View.GONE);
        }

       String link=currentBookItem.getSmallImageLink();
        if(link!=null&&!TextUtils.isEmpty(link)) {
            //use Picasso API to handle downloading images
            Picasso.with(context).load(link).into(holder.imageView);
        } else {
            //if there is no image link retrieved from GoogleBooks API, use this image instead
            holder.imageView.setImageResource(R.drawable.no_image);
        }

        return view;
    }

}
