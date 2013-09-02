package com.meet_me;


import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.content.res.Resources.Theme;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Facebook_twitter_signup extends Activity {
	
	private Handler myHandler=null;
	private Runnable updateRunnable=null;
    static String TWITTER_CONSUMER_KEY = "tC8jwC5VYWlTExdW0ff0yQ";
    static String TWITTER_CONSUMER_SECRET = "HjsNunT3Zr5JiCgPfP8ipt2KPrjRqsVtf4dHhL0BY";
    
    GraphUser Guser=null;
    public boolean register=false;
 
    // Preference Constants
    static String PREFERENCE_NAME = "twitter_oauth";
    static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
 
    static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
 
    // Twitter oauth urls
    static final String URL_TWITTER_AUTH = "auth_url";
    static final String URL_TWITTER_OAUTH_VERIFIER="oauth_verifier";
    static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
    private static Twitter twitter;
    private static RequestToken requestToken;
    private static SharedPreferences mSharedPreferences;
    
    Vector<String> twitterUserdata=new Vector<String>();
    
    EditText name;
    EditText username;
    EditText email;
    EditText password;
    EditText confirmpassword;
    Button RegButton;
    Vector<String> userdata;
    ArrayList<HashMap<String, String>> mylist=null;
    ImageView add;
    ImageView back;
    double latitude;
    double longitude;
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_twitter_signup);
		Register_login.GetPref(getApplicationContext());
		
		password= (EditText) findViewById(R.id.password);
		confirmpassword=(EditText) findViewById(R.id.confirmpasswrod);
		email=(EditText) findViewById(R.id.email);
		username=(EditText) findViewById(R.id.username);
		add=(ImageView) findViewById(R.id.add);
		back=(ImageView) findViewById(R.id.back);
		name=(EditText) findViewById(R.id.name);
		
		RegButton=(Button) findViewById(R.id.regbutton);
		Button FacebookButton=(Button) findViewById(R.id.facebookBuuton);
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//collect info
				Intent intent= new Intent(getApplicationContext(), Add_Service.class);
				startActivity(intent);
			}
		});
	    back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent= new Intent(getApplicationContext(), Register_login.class);
				startActivity(intent);
			}
		});
		RegButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					
				if((password.getText().toString().equals(confirmpassword.getText().toString()))&(username.getText()!=null)&(email.getText()!=null)&name.getText()!=null){
					getRegSharedUserdata();
					
					Log.d(email.getText().toString(), email.getText().toString()+(email.getText()!=null));
					if(isConnectingToInternet(getApplicationContext())){
						register=true;
						httpdatacall saveDataToServer=new httpdatacall();
						saveDataToServer.execute(new String[]{"http://p2mu.net/meetme/meetme.pl"});
					//	Intent facebook_intent= new Intent(getApplicationContext(),Add_Service.class);
			         //   startActivity(facebook_intent);
					
					}
					else{
						Getalert("Internet Connection", "oops! please check your internet connection");
					}
				}
				else{
					Getalert("Error", "oops! Ensure all fields are filled");
				}
				
			}
		});
		FacebookButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				Log.d("activesession", "b4getActive session");
				Log.d("connection to internet", ""+isConnectingToInternet(getApplicationContext()));
				//getting null pointer exception...
				if(isConnectingToInternet(getApplicationContext())){
					Log.d("connection to internet", ""+isConnectingToInternet(getApplicationContext()));
					register=false;
					 getActiveSession();
				}
				
			else {
				Getalert("Internet Connection", "oops! please check your internet connection");
			}
			}
		});
	}
		/*linkedinButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(isConnectingToInternet(getApplicationContext())){
					getConnectionAlert(getApplicationContext(),  "Internet Connection Error",
		                    "Please connect to working Internet connection", false);
		            return;
				}
				 mSharedPreferences = getApplicationContext().getSharedPreferences(
			                "MyPref", 0);
				 loginToTwitter();
				 twitterLoggedUser();
			}
		});
	*/	


	public GraphUser getActiveSession(){
		
		Log.d("activesession", "Open Method");
		Session.openActiveSession(this, true, new Session.StatusCallback() {
			
	        // callback when session changes state
			@SuppressWarnings("deprecation")
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	        	Log.d("activesession", "    "+session.isOpened());
	        	 Toast.makeText(getApplicationContext(), "checking open"+session.isOpened(), Toast.LENGTH_LONG).show(); 
	        	//printHashKey();
	          if (session.isOpened()) {
	        	  Toast.makeText(getApplicationContext(), "checking open"+session.isOpened()+"seen", Toast.LENGTH_LONG).show();    
	            // make request to the /me API
	            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	            	
	              // callback after Graph API response with user object
	              @Override
	              public void onCompleted(GraphUser user, Response response) {
	            	  Log.d("completed", "Request");
	                if (user != null) {
	                	 Log.d("completed", user.getUsername());
	                  getUser(user);
	                  Guser=user;
	                  getFacebookSharedData(user.getFirstName(), user.getLastName(), user.getUsername(), user.getLink());	                  
	                  Toast.makeText(getApplicationContext(), "Username/Email Already exist"+user.getUsername(), Toast.LENGTH_LONG).show();    
	                  httpdatacall data=new httpdatacall();
					  data.execute(new String[]{"http://p2mu.net/meetme/meetme.pl"});
	                  
	                  }
	                else{
	                	Getalert("Internet Connection", "oops! please check your internet connection");
	                }
	                }
	              
	            });
	          }
	        }
	      });
   	 Log.d("Aftercompleted", "schecking");
		return Guser;
	}

	  @Override
	  public void onActivityResult(int requestCode, int resultCode, Intent data) {
	      super.onActivityResult(requestCode, resultCode, data);
	      Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	  }

/*	  public void printHashKey() {

	        try {
	            PackageInfo info = getPackageManager().getPackageInfo("com.meet_me",
	                    PackageManager.GET_SIGNATURES);
	            for (Signature signature : info.signatures) {
	                MessageDigest md = MessageDigest.getInstance("SHA");
	                md.update(signature.toByteArray());
	                Log.d("TEMPTAGHASH KEY:",
	                        Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	        } catch (NameNotFoundException e) {
	        	e.printStackTrace();
	        } catch (NoSuchAlgorithmException e) {
	        	e.printStackTrace();
	        }

	    }*/
	public GraphUser getUser(GraphUser user){
		return user;
	}
	public void getFacebookSharedData(String firstname, String Lastname, String username, String facebookUri){
		
		
		Register_login.editor.putString("NAME", firstname+" "+Lastname);
		Register_login.editor.putString("USER_NAME", username);
	//	Register_login.editor.putString("F_USERNAME", username);
		Register_login.editor.putString("EMAIL", username+"@facebook.com");
		Register_login.editor.putString("MEETME_URI", username+"@meet_me.com");
		
		Register_login.editor.commit();
		
		Log.d("preference",Register_login.my_pref.getString("EMAIL", "does not exist"));

		} 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.facebook_twitter_signup, menu);
		return true;
	}
	public boolean isConnectingToInternet(Context context){
		 ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
         if (connectivity != null)
         {
             NetworkInfo[] info = connectivity.getAllNetworkInfo();
             if (info != null)
                 for (int i = 0; i < info.length; i++)
                     if (info[i].getState() == NetworkInfo.State.CONNECTED)
                     {
                         return true;
                     }
 
         }
         return false;
	}
	@SuppressWarnings("deprecation")
	public void getConnectionAlert(Context context, String title, String message,
            Boolean status) {
  final      AlertDialog alertDialog = new AlertDialog.Builder(context).create();
 
        // Setting Dialog Title
        alertDialog.setTitle(title);
 
        // Setting Dialog Message
        alertDialog.setMessage(message);
 
        if(status != null)
            // Setting alert dialog icon fail or pass
            alertDialog.setIcon((status) ? R.drawable.meet_logo: R.drawable.twitter_logo);
 
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
    }
	 private void loginToTwitter() {
	        // Check if already logged in
	        if (!isTwitterLoggedInAlready()) {
	            ConfigurationBuilder builder = new ConfigurationBuilder();
	            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
	            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
	            Configuration configuration = builder.build();
	             
	            TwitterFactory factory = new TwitterFactory(configuration);
	            twitter = factory.getInstance();
	 
	            try {
	                requestToken = twitter
	                        .getOAuthRequestToken(TWITTER_CALLBACK_URL);
	                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
	                        .parse(requestToken.getAuthenticationURL())));
	            } catch (TwitterException e) {
	                e.printStackTrace();
	            }
	        } else {
	            // user already logged into twitter
	            Toast.makeText(getApplicationContext(),
	                    "Already Logged into twitter", Toast.LENGTH_LONG).show();
	        }
	    }
	 
	    /**
	     * Check user already logged in your application using twitter Login flag is
	     * fetched from Shared Preferences
	     * */
	    private boolean isTwitterLoggedInAlready() {
	        // return twitter login status from Shared Preferences
	        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	    }
	    
	    public void twitterLoggedUser(){
	    	 if (!isTwitterLoggedInAlready()) {
	    	        Uri uri = getIntent().getData();
	    	        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
	    	            // oAuth verifier
	    	            String verifier = uri
	    	                    .getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
	    	 
	    	            try {
	    	                // Get the access token
	    	                AccessToken accessToken = twitter.getOAuthAccessToken(
	    	                        requestToken, verifier);
	    	 
	    	                // Shared Preferences
	    	                Editor e = mSharedPreferences.edit();
	    	 
	    	                // After getting access token, access token secret
	    	                // store them in application preferences
	    	                e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
	    	                e.putString(PREF_KEY_OAUTH_SECRET,
	    	                        accessToken.getTokenSecret());
	    	                // Store login status - true
	    	                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
	    	                e.commit(); // save changes
	    	 
	    	                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
	    	 
	    	                long userID = accessToken.getUserId();
	    	                User user = twitter.showUser(userID);
	    	                String username = user.getName();
	    	                getTwitterData(user.getScreenName(), " ", user.getName(), user.getURL().toString());
	    	    	            } catch (Exception e) {
	    	                // Check log for login errors
	    	                Log.e("Twitter Login Error", "> " + e.getMessage());
	    	            }
	    	        }
	    	    }
	    	 
	    	}
	    public void Getalert(String header, String content){
	    	AlertDialog.Builder alertinternet= new AlertDialog.Builder(Facebook_twitter_signup.this);
        	alertinternet.setTitle(header);
			  
		        // Setting Dialog Message
		        alertinternet.setMessage(content);
		        alertinternet.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
	
					}
				});
		        alertinternet.show();
	    }
	    public void getTwitterData(String firstname, String Lastname, String username, String twitterUri){
			twitterUserdata.add(firstname);
			twitterUserdata.add(Lastname);
			twitterUserdata.add(username);
			twitterUserdata.add(twitterUri);
			
			return ;
		} 
	    public void getRegSharedUserdata(){
	
	    	Register_login.editor.putString("NAME", name.getText().toString());
	    	Register_login.editor.putString("USER_NAME", username.getText().toString());
	    	Register_login.editor.putString("EMAIL", email.getText().toString());
	    	Register_login.editor.putString("MEETME_URI", username.getText().toString()+"@meet_me.com");
	    	
	    	Register_login.editor.commit();
	    	
	    	Log.d("preference",Register_login.my_pref.getString("EMAIL", "does not exist"));
	    }
	
	private class httpdatacall extends AsyncTask<String, Void, String>{

		private ProgressDialog pDialog;
		@Override
		protected String doInBackground(String... url) {

			 String responseBody=" ";
			try {

		        HttpClient httpclient = new DefaultHttpClient();
		  
		        HttpPost httppost = new HttpPost(url[0]);
		            Log.i(getClass().getSimpleName(), "send  task - start");
		            //
		            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		          //  use a for statement to add
		            if(register){
		              nameValuePairs.add(new BasicNameValuePair("action", "register"));
		              nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString()));
		              nameValuePairs.add(new BasicNameValuePair("password",password.getText().toString()));
		              nameValuePairs.add(new BasicNameValuePair("username", username.getText().toString()));
		            }
		            else{
		              nameValuePairs.add(new BasicNameValuePair("action", "register"));
		              nameValuePairs.add(new BasicNameValuePair("username", Guser.getUsername()));
		              nameValuePairs.add(new BasicNameValuePair("email", Guser.getUsername()+"@facebook.com"));
		              Toast.makeText(getApplicationContext(), "Basic value pair", Toast.LENGTH_LONG).show();    
		            }
		              httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		              ResponseHandler<String> responseHandler = new BasicResponseHandler();
		              Toast.makeText(getApplicationContext(), "set entity", Toast.LENGTH_LONG).show();    
		              responseBody = httpclient.execute(httppost,responseHandler);
		              Toast.makeText(getApplicationContext(), "Execute", Toast.LENGTH_LONG).show();    
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
		        	return responseBody;
		}
		@Override
		protected void onPostExecute(String result) {
			pDialog.cancel();
			Log.d("json response", result);
			if(!result.contains("exist")){
		     Log.d("json response....", result);
			 Toast.makeText(getApplicationContext(), "Location", Toast.LENGTH_LONG).show();   
		    httpLocationcall location=new httpLocationcall();
			location.execute(new String[]{"http://p2mu.net/meetme/meetme.pl"});
 
			}
			else{
			Toast.makeText(getApplicationContext(), "Facebook activity", Toast.LENGTH_LONG).show();    
				Intent facebook_intent= new Intent(getApplicationContext(), Facebook_twitter_signup.class);
	            startActivity(facebook_intent);
	        //    getConnectionAlert(getApplicationContext(), "", message, status)
			}
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(getApplicationContext(), "OnPreExecute", Toast.LENGTH_LONG).show();    
            pDialog = new ProgressDialog(Facebook_twitter_signup.this);
            pDialog.setMessage("Connecting ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
		}
	
	}
	private class httpLocationcall extends AsyncTask<String, Void, String>{
		
		private ProgressDialog pDialog;
		@Override
		protected String doInBackground(String... url) {
			// TODO Auto-generated method stub

			 String responseBody=" ";
			try {

		        HttpClient httpclient = new DefaultHttpClient();
		  
		        HttpPost httppost = new HttpPost(url[0]);
		            Log.i(getClass().getSimpleName(), "send  task - start");
		            //
		            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		          //  pass the right key and value
		              nameValuePairs.add(new BasicNameValuePair("action", "location"));
		              nameValuePairs.add(new BasicNameValuePair("uid", "1"));
		              nameValuePairs.add(new BasicNameValuePair("lat", ""+latitude));
		              nameValuePairs.add(new BasicNameValuePair("long", ""+longitude));
		            
		              httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		              ResponseHandler<String> responseHandler = new BasicResponseHandler();
		              responseBody = httpclient.execute(httppost,responseHandler);
		              Toast.makeText(getApplicationContext(), "Location Execute", Toast.LENGTH_LONG).show();    
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
		        	return responseBody;
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.cancel();
		/*	try{
				 Log.d("json_response", result);
		            JSONObject json = new JSONObject(result);
		            JSONArray jArray = json.getJSONArray("posts");
		             mylist = new ArrayList<HashMap<String, String>>();

		            for (int i = 0; i < jArray.length(); i++) {
		                HashMap<String, String> map = new HashMap<String, String>();
		                JSONObject e = jArray.getJSONObject(i);
		                String s = e.getString("post");
		                JSONObject jObject = new JSONObject(s);
		              //  to add header of json and value individually
			                map.put(" ", jObject.getString(" "));

		                mylist.add(map);
		            }
				}catch(Exception e)
				{
					
				}*/
			Intent add_service_intent= new Intent(getApplicationContext(), Add_Service.class);
            startActivity(add_service_intent);
			
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			LocationActivity gps=new LocationActivity(getApplicationContext());
			
			gps.getLocation();
			if(gps.canGetLocation()){
          
          latitude = gps.getLatitude();
          longitude = gps.getLongitude();  
         Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();    
         
         pDialog = new ProgressDialog(Facebook_twitter_signup.this);
         pDialog.setMessage("Connecting ...");
         pDialog.setIndeterminate(false);
         pDialog.setCancelable(false);
         pDialog.show();
			}
		}
		
	}
}

