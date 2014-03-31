package com.example.androidtwitterapp;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewActivity extends Activity{
	
	List<Status> statuses = new ArrayList<Status>();
	List<String> tweetList = new ArrayList<String>();
	  ArrayAdapter<String> tweetAdapter2 ; 
	  ArrayAdapter<String> tweetAdapter ; 
	  private ListView timelineListView; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_layout);
		timelineListView = (ListView) findViewById(R.id.listView);
		UpdateTimeline updateTimeline = new UpdateTimeline();
		updateTimeline.execute();
	}
	
	
	
	 class UpdateTimeline extends AsyncTask <Void, Void, Void>{
		 
		 private ProgressDialog progressDialog;

	       @Override
	    protected void onPreExecute() {
	    	// TODO Auto-generated method stub
	    	super.onPreExecute();
	    	
	    	progressDialog = new ProgressDialog(ListViewActivity.this);
    		progressDialog.setMessage("Loading...");
    		progressDialog.show();  

	    }
	        protected Void doInBackground(Void... arg0) {           
	            try {
	                ConfigurationBuilder builder = new ConfigurationBuilder();
	                builder.setOAuthConsumerKey(ConstantValues.TWITTER_CONSUMER_KEY);
	                builder.setOAuthConsumerSecret(ConstantValues.TWITTER_CONSUMER_SECRET);

	                
	                SharedPreferences mSharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	                
	                // Access Token
	                String access_token = mSharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
	                // Access Token Secret
	                String access_token_secret = mSharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

	                AccessToken accessToken = new AccessToken(access_token, access_token_secret);
	                Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
	                User user = twitter.verifyCredentials();

	                statuses = twitter.getHomeTimeline();


	                String temp ="";
	                System.out.println("Showing @" + user.getScreenName() + "'s home timeline.");
	                for (twitter4j.Status status : statuses) {
	                    System.out.println("@" + status.getUser().getScreenName() + " - " + status.getText()); 
	                 temp=  "@" + status.getUser().getScreenName() + " - " + status.getText()+"\n"; 
	                 
	                    
	                    tweetList.add(temp);
	                    }

	            } catch (TwitterException te) {
	                te.printStackTrace();
	                System.out.println("Failed to get timeline: " + te.getMessage());
	                //System.exit(-1);
	            }
	            return null;
	        }




	        protected void onPostExecute(Void result) {
	            // dismiss the dialog after getting all products
	        	progressDialog.dismiss();
	            // updating UI from Background Thread
	            runOnUiThread(new Runnable() {
	                @Override
	                public void run() {
	                Toast.makeText(getApplicationContext(),
	                            "Timeline updated", Toast.LENGTH_SHORT)
	                            .show();    
//	                tweetAdapter = new ArrayAdapter<twitter4j.Status>(ListViewActivity.this, android.R.layout.simple_list_item_1, statuses);
	                tweetAdapter2 = new ArrayAdapter<String>(ListViewActivity.this, android.R.layout.simple_list_item_1, tweetList);
//	                timelineListView.setAdapter(tweetAdapter);
	                timelineListView.setAdapter(tweetAdapter2);
	                }

	            });
	        }
	    
	    
	}

}
