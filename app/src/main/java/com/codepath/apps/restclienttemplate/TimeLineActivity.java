package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class TimeLineActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeContainer;
    EndlessRecyclerViewScrollListener scrollListener;
    private final int REQUEST_CODE = 20;
    private TwitterClient client;
    RecyclerView rvTweets;
    private TweetAdapter adapter;
    private List<Tweet> tweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        client = TwitterApp.getRestClient(this);

        //find the recycler view
        rvTweets = findViewById(R.id.rvTweets);

        //init the list of tweets and adapter from data
        tweets = new ArrayList<>();
        adapter = new TweetAdapter(this, tweets);

        //Recycler View setup, setting the layoutmanager and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvTweets.setLayoutManager(linearLayoutManager);

        //infinite scrolling
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                log.d("end","The page is requesting for more");
                loadMoreData(page);
                scrollListener.resetState();
            }
        };

        rvTweets.addOnScrollListener(scrollListener);

        //setting up the list data (adapter) to the recycler view
        rvTweets.setAdapter(adapter);

        //calling the api and receiving the data / parsing
        populateTimeLine();

        //referencing the swipe container
        swipeContainer = findViewById(R.id.swipeContainer);

        //swipe icon colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //getting access to the twitter client api
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateTimeLine();
            }
        });

    }//end of onCreate method

    //inflating the menu bar with the created xml
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //making a method for the action bar icon
    //listens for result code from that view to send back data
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.compose) {
            Intent i = new Intent(this, composeActivity.class);
            startActivityForResult(i, REQUEST_CODE); //unique code and listen for this code in activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //method used for when the activity sends back the data and the code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //define the request code passed to the activity
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            //PULL INFO OUT OF THE DATA INTENT (tweet object)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("Tweet"));
            //update the recycler view with this tweet
            tweets.add(0, tweet);
            adapter.notifyItemInserted(0);
            //tells the recycler view to scroll back up after new data has been published
            rvTweets.smoothScrollToPosition(0);
        }
    }

    //used to load more data to the end of the timeline
    public void loadMoreData(int index) {
        client.getNextPageOfTweets(new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //iterate through the array
                List<Tweet> tweetsToAdd = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        //convert each JSON object into a java object
                        JSONObject jsonObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonObject);
                        //Add the tweet into our data source - adapter
                        tweetsToAdd.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.addAll(tweetsToAdd);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("smile", errorResponse.toString());
            }
        }, index);
    }


    //making the http request to get news feed data
    private void populateTimeLine() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //iterate through the array
                List<Tweet> tweetsToAdd = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        //convert each JSON object into a java object
                        JSONObject jsonObject = response.getJSONObject(i);
                        Tweet tweet = Tweet.fromJson(jsonObject);
                        //Add the tweet into our data source - adapter
                        tweetsToAdd.add(tweet);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.clear();
                adapter.addAll(tweetsToAdd);
                //letting know the refresher that it has been refreshed;
                swipeContainer.setRefreshing(false);
                scrollListener.resetState();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                Log.e("smile", errorResponse.toString());
            }
        });
    }
}
