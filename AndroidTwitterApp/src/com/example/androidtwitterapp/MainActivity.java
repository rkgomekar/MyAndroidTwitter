package com.example.androidtwitterapp;

import twitter4j.auth.RequestToken;

import com.hintdesk.core.activities.AlertMessageBox;
import com.hintdesk.core.util.OSUtil;
import com.hintdesk.core.util.StringUtil;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;
import android.preference.PreferenceManager;

public class MainActivity extends ActionBarActivity {
	
	  Button buttonLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		if (savedInstanceState == null) {
//			getSupportFragmentManager().beginTransaction()
//					.add(R.id.container, new PlaceholderFragment()).commit();
//		}
		
		   if (!OSUtil.IsNetworkAvailable(getApplicationContext())) {
	            AlertMessageBox.Show(MainActivity.this, "Internet connection", "A valid internet connection can't be established", AlertMessageBox.AlertMessageBoxIcon.Info);
	            return;
	        }

	        if (StringUtil.isNullOrWhitespace(ConstantValues.TWITTER_CONSUMER_KEY) || StringUtil.isNullOrWhitespace(ConstantValues.TWITTER_CONSUMER_SECRET)) {
	            AlertMessageBox.Show(MainActivity.this, "Twitter oAuth infos", "Please set your twitter consumer key and consumer secret", AlertMessageBox.AlertMessageBoxIcon.Info);
	            return;
	        }

	        initializeComponent();
	    
	}
	
	 private void initializeComponent() {
	        buttonLogin = (Button) findViewById(R.id.buttonLogin);

	        buttonLogin.setOnClickListener(buttonLoginOnClickListener);
	    }
	 
	 private View.OnClickListener buttonLoginOnClickListener = new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {

	            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	            if (!sharedPreferences.getBoolean(ConstantValues.PREFERENCE_TWITTER_IS_LOGGED_IN,false))
	            {
	                new TwitterAuthenticateTask().execute();
	            }
	            else
	            {
	                Intent intent = new Intent(MainActivity.this, TwitterActivity.class);
	                startActivity(intent);
	            }

	        }
	    };
	    
	    class TwitterAuthenticateTask extends AsyncTask<String, String, RequestToken> {

	        @Override
	        protected void onPostExecute(RequestToken requestToken) {
	            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL()));
	            startActivity(intent);
	        }

	        @Override
	        protected RequestToken doInBackground(String... params) {
	            return TwitterUtil.getInstance().getRequestToken();
	        }
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.about) {
			alertDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void alertDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(
                MainActivity.this).create();

// Setting Dialog Title
alertDialog.setTitle("Assignment App");

// Setting Dialog Message
alertDialog.setMessage("Developed by Rushikesh Gomekar");


// Setting OK Button
alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        }
});

// Showing Alert Message
alertDialog.show();
		
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
