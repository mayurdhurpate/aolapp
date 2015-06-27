/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iitbhu.once;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    public final static String EXTRA_MESSAGE = "";


    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]


                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken("798268941620",
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);


                Log.i(TAG, "GCM Registration Token: " + token);
                String email = intent.getStringExtra("email");
                String googlePlusId = intent.getStringExtra("googlePlusId");
                String googlePlusPhoto = intent.getStringExtra("image");
                String name = intent.getStringExtra("name");
                String phone = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);


                // TODO: Implement this method to send any registration to your app's servers.
                sendRegistrationToServer(token, email, phone, googlePlusPhoto, googlePlusId, name);
//                notifytoui(token);

                // Subscribe to topic channels
//                subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                sharedPreferences.edit().putString(QuickstartPreferences.USERNAME, name).apply();
                sharedPreferences.edit().putString(QuickstartPreferences.EMAIL, email).apply();

                SharedPreferences userFile = getSharedPreferences(QuickstartPreferences.USERDATA, 0);
                userFile.edit().putString("email", email).apply();
                userFile.edit().putString("name", name).apply();
                userFile.edit().putString("googlePlusId", googlePlusId).apply();
                userFile.edit().putString("image", googlePlusPhoto).apply();
                userFile.edit().putString("phone", phone).apply();
                userFile.edit().putString("token", token).apply();


                // [END register_for_gcm]
            }
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.

    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }


    private void sendRegistrationToServer(String token,String email, String phone, String image, String id, String name) {


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute("http://128.199.123.200/register/","name="+name+"&token="+token+"&passkey=hellolastry"+"&email="+email+"&social_id="+id+"&image_url="+image+"&phone_no="+phone);
        } else {
            // display error
            Context con = getApplicationContext();
            CharSequence text1 = "No network connection available!";
            int duration1 = Toast.LENGTH_SHORT;

            Toast toast1 = Toast.makeText(con, text1, duration1);
            toast1.show();

        }

    }

    private void sendBroadcast(){
        Log.i("sendBroadcast", "log");
//        Toast.makeText(getApplicationContext(),"sendBroadcast",Toast.LENGTH_SHORT).show();
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }



     public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
         @Override
         protected String doInBackground(String... urls) {

             // params comes from the execute() call: params[0] is the url.
             try {
                 String statusCode = downloadUrl(urls[0], urls[1]);


                 return statusCode;

             } catch (IOException e) {
                 return "Unable to retrieve web page. URL may be invalid.";
             }
         }

         // onPostExecute displays the results of the AsyncTask.
         @Override
         protected void onPostExecute(String result) {
             String res;
             try {
                 JSONObject jsonObject = new JSONObject(result);
                 res = jsonObject.getString("message");
                 if (res.equals("User Registered")) {
//                     Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
                     sendBroadcast();
                 } else {
                     Toast.makeText(getApplicationContext(), "else" + result, Toast.LENGTH_LONG).show();
                 }
             } catch (Exception e) {
                 Log.e("json at result", e.toString());

             }

             Log.i("Result", result);

         }
     }



    private String downloadUrl(String myurl, String postdata) throws IOException{
        InputStream is = null;
        int len = 50000;
        URL url;
        try {
            url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(postdata);
            wr.flush();
            wr.close();

            is = conn.getInputStream();
            //Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;
//            int statusCode = conn.getResponseCode();
//            return Integer.toString(statusCode);

        } catch (Exception e) {
            //handle the exception !
//            Log.d(DEBUG_TAG,e.getMessage());
            return e.toString();
        }
//        String contentAsString = readIt(myInputStream, len);
//            return contentAsString;

    }



    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}


