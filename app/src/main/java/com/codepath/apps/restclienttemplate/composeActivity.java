package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;

public class composeActivity extends AppCompatActivity {
    private EditText text;
    private Button button;
    private TwitterClient client;
    private EditText textAmount;
    private final int MAX_TEXT = 140;
    private String amount;
    private int addedTotal = 0;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        text = findViewById(R.id.etCompose);
        textAmount = findViewById(R.id.tvAmount);
        button = findViewById(R.id.tweetBtn);
        client = TwitterApp.getRestClient(this);
        cancel = findViewById(R.id.cancelBtn);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //event to listen for text changed from user
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(text.length() > MAX_TEXT) {
                    button.setEnabled(false);
                    textAmount.setTextColor(getResources().getColor(R.color.red));
                }
                else {
                    button.setEnabled(true);
                    textAmount.setTextColor(getResources().getColor(R.color.black));
                }
                amount =  Integer.toString(MAX_TEXT - text.length());
                textAmount.setText(amount);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //when we click on the button then the text is sent to the news feed
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = text.getText().toString();
                //error handling
                if(content.isEmpty()){
                    Toast.makeText(composeActivity.this, "You need to write something", Toast.LENGTH_LONG).show();
                    return;
                }
                if(content.length() > 140){
                    Toast.makeText(composeActivity.this, "Your tweet content is too long", Toast.LENGTH_LONG).show();
                    return;
                }
                //if both conditions did not pass, then send the text to the api for news feed
                client.composeTweet(content, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("completed", "we sent the text to the news feed ");
                        Toast.makeText(composeActivity.this, " The text was posted", Toast.LENGTH_SHORT).show();
                        try {
                            Tweet tweet = Tweet.fromJson(response);//parsing the new data
                            Intent data = new Intent(); //creating a new intent object to pass data
                            data.putExtra("Tweet", Parcels.wrap(tweet)); //pass data to object
                            setResult(RESULT_OK, data ); //send the result code and data back to parent View
                            finish();// close the activity
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("failedNews", "the text was not sent to the api correctly");
                    }

                });
            }
        });
    }
}

