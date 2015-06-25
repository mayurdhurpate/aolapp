package com.iitbhu.once;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
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
    private RecyclerView.Adapter mAdapter;
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private RecyclerView.LayoutManager mLayoutManager;
    public boolean dataloaded = false;
    public boolean dataloaded1 = false;
    public ProgressBar pgbar;
    public EditText bmsg;
    public Button bbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tokenmila = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

        SharedPreferences contactsfile = getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
        boolean inicnt = contactsfile.getBoolean("inicnt", false);
        String contacts = contactsfile.getString("contacts", "no contacts");
        SharedPreferences messagesfile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
        boolean inimsg = messagesfile.getBoolean("inimsg", false);
        final String messages = messagesfile.getString("messages", "no messages");



        if (!tokenmila) {
            Intent intent = new Intent(this, Welcome.class);
            startActivity(intent);
            finish();
        }
        else {
            if (!inicnt){
                fetchContacts();
            }
            if (!inimsg){
                fetchMessages();
            }
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences messagesfile1 = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
                final String messages1 = messagesfile1.getString("messages", "mpatanahi");
                updateUI(messages1,0,"messages");
                showToast("Messages updated");
            }
        };


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
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.NOTIFY));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
            new DownloadWebpageTask().execute("http://128.199.123.200/contacts/","passkey=hellolastry","fetch_contacts");
        } else {
            showToast("No network connection available!");
        }


    }

    public void fetchMessages(){
        showToast("Updating Messages");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute("http://128.199.123.200/messages/","passkey=hellolastry","fetch_messages");
        } else {
            showToast("No network connection available!");
        }
    }

    public void broadcastProceed(View view) {
        pgbar.setVisibility(View.VISIBLE);
        bmsg = (EditText)findViewById(R.id.edit_broadcast);
        bmsg.setEnabled(false);
        bbutton = (Button)findViewById(R.id.bbutton);
        bbutton.setEnabled(false);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString(QuickstartPreferences.USERNAME, "user");

        showToast("Sending message");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            new DownloadWebpageTask().execute("http://128.199.123.200/broadcastreceive/","username="+username+"&passkey=hellolastry"+"&bmsg="+bmsg.getText()+"&bmsg_title="+username+" says..","broadcast_msg");
        } else {
            showToast("No network connection available!");
        }

    }

    public void showToast(String text ){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

    }

    public void updateUI(String newItem, int flag ,String tab){
        RecyclerView mRecyclerView;

        if(tab.equals("contacts")){
            mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView);
            CustomAdapterContacts adapter =(CustomAdapterContacts) mRecyclerView.getAdapter();
            adapter.updateItems(newItem, tab);
            if(flag == 1) {
                adapter.notifyItemInserted(0);
            }
            else{
                adapter.notifyDataSetChanged();
            }

        }
        else{
             mRecyclerView= (RecyclerView) findViewById(R.id.recyclerView2);
            CustomAdapterMessages adapter =(CustomAdapterMessages) mRecyclerView.getAdapter();
            adapter.updateItems(newItem, tab);
            if(flag == 1) {
                adapter.notifyItemInserted(0);
            }
            else{
                adapter.notifyDataSetChanged();
            }
        }

    }






    /* --------------DownloadWebpageTask--------------------------*/

    public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                String statusCode = downloadUrl(urls[0], urls[1],urls[2]);
                return statusCode;

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(getApplicationContext(), "result: "+result, Toast.LENGTH_LONG).show();

            try {
                JSONObject jObject = new JSONObject(result);
                String action = jObject.getString("action");
                JSONArray jArray;
                switch (action){
                    case "fetch_contacts":
                        SharedPreferences contactfile = getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
                        jArray = jObject.getJSONArray("contacts");
                        contactfile.edit().putString("contacts", jArray.toString()).apply();
                        contactfile.edit().putBoolean("inicnt", true).apply();
                        updateUI(jArray.toString(), 0, "contacts");
                        showToast("Contacts updated");
                        dataloaded = true;
                        break;
                    case "fetch_messages":
                        SharedPreferences messagefile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
                        jArray = jObject.getJSONArray("messages");
                        messagefile.edit().putString("messages", jArray.toString()).apply();
                        messagefile.edit().putBoolean("inimsg", true).apply();
                        updateUI(jArray.toString(), 0,"messages");
                        showToast("Messages updated");
                        dataloaded1 = true;
                        break;
                    case "broadcast_msg":
                        pgbar.setVisibility(View.GONE);
                        bmsg.setEnabled(true);
                        bbutton.setEnabled(true);
                        break;
                    case "error":
                        String except = jObject.getString("exception");
                        String type = jObject.getString("type");
                        Log.i("error", except);
                        showToast(except);
                        if(type.equals("broadcast_msg")){
                            pgbar.setVisibility(View.GONE);
                            bmsg.setEnabled(true);
                            bbutton.setEnabled(true);
                        }
                        break;
                    default:
                        break;
                }
            }
            catch (Exception e){
//                Toast.makeText(getApplicationContext(), "onPostExecute: "+e.toString(), Toast.LENGTH_LONG).show();
                Log.i("JSON ka error",e.toString());
                dataloaded = true;
                dataloaded1 = true;
            }
            Log.i("onPostexecute",result);
//            Toast.makeText(getApplicationContext(), "onPostExecute: "+result, Toast.LENGTH_SHORT).show();
        }


        private String downloadUrl(String myurl, String postdata, String action) throws IOException {
            InputStream is = null;
            int len = 5000000;
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
//                showToast("Check your network connection");
                return  "{\"action\": \"error\",\"exception\":\""+e.toString()+"\",\"type\":\""+action+"\"}";
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





