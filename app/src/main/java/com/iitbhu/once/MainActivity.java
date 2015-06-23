package com.iitbhu.once;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "";
    private static final String TAG = "RegIntentService";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tokenmila = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);


        if (!tokenmila) {
            Intent intent = new Intent(this, Welcome.class);
            startActivity(intent);
            finish();
        }
        else{
            fetchContacts();
            fetchMessages();
        }

        SharedPreferences contactsfile = getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
        String contacts = contactsfile.getString("contacts", "cpatanahi");
        SharedPreferences messagesfile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
        String messages = messagesfile.getString("messages", "mpatanahi");
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            Bundle bundle = new Bundle();
            bundle.putString(QuickstartPreferences.CONTACTS,contacts);
            bundle.putString(QuickstartPreferences.MESSAGES,messages);
            fragment.setArguments(bundle);
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void fetchContacts(){
        showToast("Updating Contacts");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute("http://128.199.123.200/contacts/","passkey=hellolastry");
        } else {
            showToast("No network connection available!");
        }
    }

    public void fetchMessages(){
        showToast("Updating Contacts");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute("http://128.199.123.200/messages/","passkey=hellolastry");
        } else {
            showToast("No network connection available!");
        }
    }

    public void broadcastProceed(View view) {
        EditText bmsg = (EditText)findViewById(R.id.edit_broadcast);
        EditText bmsg_title = (EditText)findViewById(R.id.edit_broadcast_title);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString(QuickstartPreferences.USERNAME, "user");

        showToast("Sending message");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute("http://128.199.123.200/broadcastreceive/","username="+username+"&passkey=hellolastry"+"&bmsg="+bmsg.getText()+"&bmsg_title="+bmsg_title.getText());
        } else {
            showToast("No network connection available!");
        }

    }

    public void showToast(String text ){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

    }





    /* --------------DownloadWebpageTask--------------------------*/

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
            Toast.makeText(getApplicationContext(), "result: "+result, Toast.LENGTH_LONG).show();

            try {
                JSONObject jObject = new JSONObject(result);
                String action = jObject.getString("action");
                JSONArray jArray;
                switch (action){
                    case "fetch_contacts":
                        SharedPreferences contactfile = getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
                        jArray = jObject.getJSONArray("contacts");
                        contactfile.edit().putString("contacts",jArray.toString()).apply();
                        break;
                    case "fetch_messages":
                        SharedPreferences messagefile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
                        jArray = jObject.getJSONArray("messages");
                        messagefile.edit().putString("messages",jArray.toString()).apply();
                        break;
                    case "broadcast_msg":
                        break;
                    default:
                        break;
                }
            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(), "onPostExecute: "+e.toString(), Toast.LENGTH_LONG).show();
            }

            Toast.makeText(getApplicationContext(), "onPostExecute: "+result, Toast.LENGTH_SHORT).show();
        }


        private String downloadUrl(String myurl, String postdata) throws IOException {
            InputStream is = null;
            int len = 50000;
            URL url;
            try {
                url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(postdata);
                wr.flush();
                wr.close();

                is = conn.getInputStream();
                //Convert the InputStream into a string
                String contentAsString = readIt(is, len);
                return contentAsString;
//                int statusCode = conn.getResponseCode();
//                return Integer.toString(statusCode);

            } catch (Exception e) {
                return "downloadUrl: "+e.toString();
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


}





