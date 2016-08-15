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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    private SharedPreferences pref_def;
//    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        pref_def = PreferenceManager.getDefaultSharedPreferences(this);
        String topic = data.getString("topic");

        SharedPreferences messagefile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
        String messages_array = messagefile.getString("messages", "no_messages");
        int lastid = 0;
        try {
            JSONArray jArray = new JSONArray(messages_array);
            JSONObject oneObject = jArray.getJSONObject(0);
            lastid = oneObject.getInt("id");

        } catch (Exception e) {

            Log.i("excep_gcm_msgs1", e.toString());

        }
        Log.i("lastid", Integer.toString(lastid));

        String newidstring = data.getString("id");
        int newid = Integer.parseInt(newidstring);
        Log.i("newid", data.getString("id"));

        if (newid > lastid) {
            String message = data.getString("message1");
            String title = data.getString("title");
            String sender = data.getString("sender");
            String date = data.getString("date");
            String time = data.getString("time");
            String url = data.getString("img_url");
            Log.d(TAG, "From: " + from);
            Log.d(TAG, "Message: " + message);
            Log.d(TAG, "id: " + newid);
            String messages = messagefile.getString("messages", "{}");
            messages = "[{\"sender\":\"" + sender + "\",\"message\":\"" + message + "\",\"id\":\"" + newidstring + "\",\"topic\":\"" + topic + "\",\"title\":\""+title+"\",\"date\":\""+date+"\",\"time\":\""+time+"\",\"url\":\""+url+"\"}," + messages.substring(1);
            Log.i("messages", messages);
            messagefile.edit().putString("messages", messages).apply();

            Intent msgReceived = new Intent(QuickstartPreferences.NOTIFY);
            LocalBroadcastManager.getInstance(this).sendBroadcast(msgReceived);


            boolean b = pref_def.getBoolean("notifications_new_message", false);
            if (b) {
                Map<String, ?> keys_topic_settings = pref_def.getAll();
                if (keys_topic_settings.containsKey(topic)) {
                    if(pref_def.getBoolean(topic,true)) {
                        sendNotification(message, title);
                    }
                }
                else{
                    sendNotification(message, title);
                }
            }
        }


    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message,String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.myicon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        boolean vib = pref_def.getBoolean("notifications_new_message_vibrate",true);
        if (vib){
            notificationBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
