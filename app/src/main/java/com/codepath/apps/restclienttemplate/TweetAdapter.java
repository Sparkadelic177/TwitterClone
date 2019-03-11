package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
   private Context context;
   private List<Tweet> tweets;



    //pass in the context of tweets / constructor
    public TweetAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
    }

    //for each row , inflate the layout we defined
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //inflating the parent layout with the layout tweet, returning a view
        View view = LayoutInflater.from(context).inflate(R.layout.layout_tweet, viewGroup, false);
        //calling the constructor passing the view to return a ViewHolder
        return new ViewHolder(view);
    }

    //binds values, based on the position of the element
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Tweet tweet = tweets.get(i);
        viewHolder.tvScreenName.setText(tweet.user.screenName);
        viewHolder.tvBody.setText(tweet.body);
        Glide.with(context).load(tweet.user.profileImageUrl).placeholder(R.drawable.ic_launcher).dontAnimate().into(viewHolder.ivProfileImage);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }

    //define the ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder{
        //where we reference the layout for each tweet that gonna be inflated
        public ImageView ivProfileImage;
        public TextView tvBody;
        public TextView tvScreenName;

        public ViewHolder(View itemView){
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
        }
    }
}
