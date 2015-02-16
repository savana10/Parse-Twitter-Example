package com.example.twitter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;


public class MainActivity extends Activity {

	//Create a new app at Parse.com ,go to settings and copy following values
	private static String PARSE_APP_ID = "";
	private static String PARSE_CLIENT_KEY = "";
	
	// Create a new twitter app and copy app key and secret
	
	private static String PARSE_TWITTER_KEY = "";
	private static String PARSE_TWITTER_SECRET = "";
	
	private static String App_Activity_Log = "Main_Activity";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Parse.initialize(getApplicationContext(), PARSE_APP_ID, PARSE_CLIENT_KEY);

		ParseTwitterUtils.initialize(PARSE_TWITTER_KEY, PARSE_TWITTER_SECRET);
		
		// 	Signup/Login using twitter , irrespective of user logs in or signup we will get user details
		
		ParseTwitterUtils.logIn(this, new LogInCallback() {

			@Override
			public void done(ParseUser arg0, ParseException arg1) {
				// TODO Auto-generated method stub

				if (arg0 == null)
				{
					Log.i(App_Activity_Log,"Uh oh. The user cancelled the Twitter login.");
				}
				else if (arg0.isNew())
				{
					Log.i(App_Activity_Log,"User signed up and logged in through Twitter!");
					new Myasynctask(MainActivity.this).execute();
				}
				else
				{
					Log.i(App_Activity_Log, "User logged in through Twitter!"+arg0.toString());

					new Myasynctask(MainActivity.this).execute();
				}
			}
		});


	}

	class Myasynctask extends AsyncTask<Void, Void, Void>{

		public Myasynctask(MainActivity mainActivity) {
			// TODO Auto-generated constructor stub
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			
			Twitter twitter = TwitterFactory.getSingleton();
			//get keys from ParseTwitterUtils and set it to Twitter(Twitter4j)
			twitter.setOAuthConsumer(ParseTwitterUtils.getTwitter().getConsumerKey(), ParseTwitterUtils.getTwitter().getConsumerSecret());

			Log.i("@@@@@@", "Twitter name:"+""+ParseTwitterUtils.getTwitter().getScreenName()+" "+ParseTwitterUtils.getTwitter().getUserId());
			Log.i("@@@@@@", "User ID:"+""+ParseTwitterUtils.getTwitter().getUserId());


			try {
				String uId= ParseTwitterUtils.getTwitter().getUserId();
				long uID= Long.parseLong(uId);
				AccessToken t = new AccessToken(ParseTwitterUtils.getTwitter().getAuthToken(), ParseTwitterUtils.getTwitter().getAuthTokenSecret(),uID);

				twitter.setOAuthAccessToken(t);
				User tg = twitter.showUser(ParseTwitterUtils.getTwitter().getScreenName());

				Log.i("profile url",tg.getBiggerProfileImageURL());
				Log.i("profile description",tg.getDescription());
				Log.i("profile name",tg.getName());
			// once after getting Image URL download bytes from that link
				DownloadImageFromPath(tg.getBiggerProfileImageURL().toString());

			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

	}

	@SuppressLint("NewApi")
	public void DownloadImageFromPath(String path){
		InputStream in =null;
		Bitmap bmp=null;
		int responseCode = -1;
		try{

			URL url = new URL(path);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setDoInput(true);
			con.connect();
			responseCode = con.getResponseCode();
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				//download 
				in = con.getInputStream();
				bmp = BitmapFactory.decodeStream(in);
				Log.e("BMP SIZE", ""+bmp.getByteCount());
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] byteArray = stream.toByteArray();
				Log.e("byte SIZE", ""+byteArray.length);
				in.close();
			}
		}
		catch(Exception ex){
			Log.e("Exception",ex.toString());
		}
	}

}
