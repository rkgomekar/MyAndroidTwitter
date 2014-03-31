package com.example.androidtwitterapp;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.hintdesk.core.util.StringUtil;

import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ServusKevin
 * Date: 5/4/13
 * Time: 12:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TwitterActivity extends Activity {

    Button buttonUpdateStatus, buttonLogout,showTweets, showMyLocation;
    EditText editTextStatus;
    TextView textViewStatus, textViewUserName;
    
    List<Status> statuses = new ArrayList<Status>();
    
    public static Twitter twitter1;
    
    ArrayAdapter<twitter4j.Status> tweetAdapter ; 
    ArrayAdapter<String> tweetAdapter2 ; 
    
    private ListView timelineListView; 


List<String> tweetList = new ArrayList<String>();
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter);
        initializeComponent();
        initControl();
    }

    private void initControl() {
        Uri uri = getIntent().getData();
        if (uri != null && uri.toString().startsWith(ConstantValues.TWITTER_CALLBACK_URL)) {
            String verifier = uri.getQueryParameter(ConstantValues.URL_PARAMETER_TWITTER_OAUTH_VERIFIER);
            new TwitterGetAccessTokenTask().execute(verifier);
        } else
            new TwitterGetAccessTokenTask().execute("");
    }

    private void initializeComponent() {
        buttonUpdateStatus = (Button) findViewById(R.id.buttonUpdateStatus);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        editTextStatus = (EditText) findViewById(R.id.editTextStatus);
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        textViewUserName = (TextView) findViewById(R.id.textViewUserName);
        showTweets = (Button) findViewById(R.id.showTweets);
        showMyLocation = (Button) findViewById(R.id.showMyLocation);
        timelineListView = (ListView) findViewById(R.id.listView);
        buttonUpdateStatus.setOnClickListener(buttonUpdateStatusOnClickListener);
        buttonLogout.setOnClickListener(buttonLogoutOnClickListener);
        showTweets.setOnClickListener(showTweetOnClickListener);
        showMyLocation.setOnClickListener(showMyLocOnMapListener);
    }

    private View.OnClickListener showMyLocOnMapListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			Intent intent = new Intent(TwitterActivity.this, MapActivity.class);
			startActivity(intent);
		}
	};
    
    private View.OnClickListener showTweetOnClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
//			try {
//				statuses = twitter1.getHomeTimeline();
//			} catch (TwitterException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		
//			String strInitialDataSet  = DataObjectFactory.getRawJSON(statuses);
//			try {
//				JSONArray JATweets = new JSONArray(strInitialDataSet);
//				  for (int i = 0; i < JATweets.length(); i++) {
//                      JSONObject JOTweets = JATweets.getJSONObject(i);
//                      Log.e("TWEETS", JOTweets.toString());
//              }
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

//			updateTimeline timeline = new updateTimeline();
//			timeline.execute();
			Intent intent = new Intent(TwitterActivity.this, ListViewActivity.class);
			startActivity(intent);
		}
	};
    
    private View.OnClickListener buttonLogoutOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
            editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
            editor.putBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, false);
            editor.commit();
            TwitterUtil.getInstance().reset();
            Intent intent = new Intent(TwitterActivity.this, MainActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener buttonUpdateStatusOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String status = editTextStatus.getText().toString();
            if (!StringUtil.isNullOrWhitespace(status)) {
                new TwitterUpdateStatusTask().execute(status);
            } else {
                Toast.makeText(getApplicationContext(), "Please enter a status", Toast.LENGTH_SHORT).show();
            }

        }
    };

    class TwitterGetAccessTokenTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPostExecute(String userName) {
            textViewUserName.setText(Html.fromHtml("<b> Welcome " + userName + "</b>"));
        }

        @Override
        protected String doInBackground(String... params) {

            Twitter twitter = TwitterUtil.getInstance().getTwitter();
            RequestToken requestToken = TwitterUtil.getInstance().getRequestToken();
            if (!StringUtil.isNullOrWhitespace(params[0])) {
                try {

                    AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, accessToken.getToken());
                    editor.putString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, accessToken.getTokenSecret());
                    editor.putBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN, true);
                    editor.commit();
                    return twitter.showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");
                AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                try {
                    TwitterUtil.getInstance().setTwitterFactory(accessToken);
                    return TwitterUtil.getInstance().getTwitter().showUser(accessToken.getUserId()).getName();
                } catch (TwitterException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return null;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    class TwitterUpdateStatusTask extends AsyncTask<String, String, Boolean> {

        @Override
        protected void onPostExecute(Boolean result) {
            if (result){
                Toast.makeText(getApplicationContext(), "Tweeted successfully", Toast.LENGTH_SHORT).show();
                editTextStatus.setText("");
            }
            else{
                Toast.makeText(getApplicationContext(), "Tweet failed", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String accessTokenString = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN, "");
                String accessTokenSecret = sharedPreferences.getString(ConstantValues.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET, "");

                if (!StringUtil.isNullOrWhitespace(accessTokenString) && !StringUtil.isNullOrWhitespace(accessTokenSecret)) {
                    AccessToken accessToken = new AccessToken(accessTokenString, accessTokenSecret);
                    twitter4j.Status status = TwitterUtil.getInstance().getTwitterFactory().getInstance(accessToken).updateStatus(params[0]);
                    return true;
                }

            } catch (TwitterException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return false;  //To change body of implemented methods use File | Settings | File Templates.

        }
    }
    
    
    
    
    
    class updateTimeline extends AsyncTask <Void, Void, Void>{

        protected void onPreExecute(Void thing){

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
            //pDialog.dismiss();

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                Toast.makeText(getApplicationContext(),
                            "Timeline updated", Toast.LENGTH_SHORT)
                            .show();    
                tweetAdapter = new ArrayAdapter<twitter4j.Status>(TwitterActivity.this, android.R.layout.simple_list_item_1, statuses);
                tweetAdapter2 = new ArrayAdapter<String>(TwitterActivity.this, android.R.layout.simple_list_item_1, tweetList);
//                timelineListView.setAdapter(tweetAdapter);
                timelineListView.setAdapter(tweetAdapter2);
                }

            });
        }
    
    
}
    }