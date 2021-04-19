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
package hk.edu.cuhk.ie.iems5722.a2_1155069587;


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = getSharedPreferences("iems5722", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i(TAG, "enter");

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.i(TAG, "GCM Registration Token: " + token);

            // TODO: Implement this method to send any registration to your app's servers.
            sendRegistrationToServer(token);

            // Subscribe to topic channels
            //subscribeTopics(token);

            // You should store a boolean that indicates whether the generated token has been
            // sent to your server. If the boolean is false, send the token to your server,
            // otherwise your server should have already received the token.
            editor.putString("token",token);
            editor.commit();
            // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            editor.putString("token","");
            editor.commit();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        //Intent registrationComplete = new Intent(AppSharedPreference.REGISTRATION_COMPLETE);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        String sendRegApi = getString(R.string.sendreg_api);
        Log.d("sendregid", ""+sendRegApi+" "+getResources().getInteger(R.integer.myuserid)+" "+token);

        new sendRegID().execute(sendRegApi,""+getResources().getInteger(R.integer.myuserid), token);

        // Add custom implementation, as needed.
    }

    class sendRegID extends AsyncTask<String, Void, String> {
        private Exception exception;

        @Override
        protected String doInBackground(String... params) {
            String sendResult = "";
            String line;

            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter(getString(R.string.sendregid_param1), params[1]);
                builder.appendQueryParameter(getString(R.string.sendregid_param2), params[2]);

                String query = builder.build().getEncodedQuery();

                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int response = conn.getResponseCode();
                if (response != 200) {
                    Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_SHORT).show();
                    return null;

                }

                Log.d("sendregid", "response = " + response);
                InputStream is = conn.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                while ((line = br.readLine()) != null) {
                    sendResult += line;
                }


            } catch (Exception e) {
               // Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_SHORT).show();
                exception = e;
                return null;
            }
            return sendResult;
        }
        @Override
        protected void onPostExecute(String s) {
            if (s != null){
                try {
                    JSONObject json = new JSONObject(s);
                    String status = json.getString("status");
                    if (!status.equals("OK")) {
                        Toast.makeText(getApplicationContext(), getString(R.string.servererror), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(),getString(R.string.servererror),Toast.LENGTH_SHORT).show();
                }
            }
            super.onPostExecute(s);
        }


    }
}