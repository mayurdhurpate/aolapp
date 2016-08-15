package com.iitbhu.once;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.common.SignInButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class Welcome extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    public GoogleApiClient mGoogleApiClient;

    // ...



    private static final String TAG = "Welcome";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public BroadcastReceiver mRegistrationBroadcastReceiver;
    public ProgressBar mRegistrationProgressBar;
//    public Button proceedbutton;
    public LinearLayout phoneLinear;
    public EditText editText;
    public TextView textView;
    public TextView textView_no;
    public TextView  textView_code;
    public final static String EXTRA_MESSAGE = "";
    public String[][] contacts;
    public int number;
    public int contacts_array_length;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }

        // ...
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();

        // Show a message to the user that we are signing in.
//        mStatusTextView.setText(R.string.signing_in);
        Toast.makeText(getApplicationContext(), "Signing in", Toast.LENGTH_SHORT).show();
        mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
//                showErrorDialog(connectionResult);
                mRegistrationProgressBar.setVisibility(View.GONE);
//                Toast.makeText(getApplicationContext(), connectionResult.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(),"Poor network connection", Toast.LENGTH_LONG).show();
            }
        } else {
            // Show the signed-out UI
//            showSignedOutUI();
            mRegistrationProgressBar.setVisibility(View.GONE);
//            Toast.makeText(getApplicationContext(), "Debug:Show the signed-out UI", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.i(TAG, "debug:onConnectionSuspended running");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
//        showSignedInUI();

        editText = (EditText)findViewById(R.id.edit_phone);
        SignInButton gplus = (SignInButton)findViewById(R.id.sign_in_button);

        editText.setVisibility(View.VISIBLE);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        phoneLinear.setVisibility(View.VISIBLE);
        textView_code.setVisibility(View.VISIBLE);
        textView.setText("Type your contact number");
        gplus.setVisibility(View.GONE);
        showSoftKeyboard(editText);


    }






    /****************Activity Functions********************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
        findViewById(R.id.sign_in_button).setOnClickListener(this);


        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        textView_code = (TextView)findViewById(R.id.edit_phone_code);
        editText = (EditText)findViewById(R.id.edit_phone);
        textView = (TextView)findViewById(R.id.progress_view);
        textView_no = (TextView)findViewById(R.id.progress_view_number);
        phoneLinear = (LinearLayout)findViewById(R.id.phone_layout);


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);

                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    Proceed(v);
                    handled = true;
                }
                return handled;
            }
        });

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("Loaded","Token");
//                proceedbutton = (Button)findViewById(R.id.buttonProceed);
//                proceedbutton.setEnabled(true);
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    phoneLinear.setVisibility(View.VISIBLE);
                    SharedPreferences contactfile = getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
                    SharedPreferences messagefile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
                    if (!contactfile.getBoolean("inicnt", false)){
                        fetchContacts();
                    }else if(!messagefile.getBoolean("inimsg", false)){
                        fetchMessages();
                    }else{
                        loadimages();
                    }

                } else {
                    Toast.makeText(getApplicationContext(),"Token not saved",Toast.LENGTH_LONG);
//                    mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }


     /** Called when the user clicks the Proceed button */
    public void Proceed(View view) {
        textView.setText("Registering...");
        mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);

        if (checkPlayServices()) {
            if(Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhoto = currentPerson.getImage().getUrl();
                String personGooglePlusId = currentPerson.getId();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                // Toast.makeText(getApplicationContext(), "Debug:showSignedInUI" + personPhoto+personGooglePlusId+personName, Toast.LENGTH_LONG).show();

                // Start IntentService to register this application with GCM.

                String message = editText.getText().toString();
                Intent intent = new Intent(this, RegistrationIntentService.class);
                intent.putExtra(EXTRA_MESSAGE, message);
                intent.putExtra("name", personName);
                intent.putExtra("googlePlusId", personGooglePlusId);
                intent.putExtra("email", email);
                intent.putExtra("image", personPhoto);
                startService(intent);

            }

        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void fetchContacts(){

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
//            showToast("Updating Contacts");
            textView.setText("Loading contacts");
            new DownloadWebpageTask().execute("http://128.199.123.200/contacts/","passkey=hellolastry","fetch_contacts");
        } else {
            showToast("No network connection available!");
//            dataloaded = true;
        }
    }

    public void fetchMessages(){

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            textView.setText("Loading messages");
            new DownloadWebpageTask().execute("http://128.199.123.200/messages/","passkey=hellolastry","fetch_messages");
        } else {
            showToast("No network connection available!");
        }
    }

    public void showToast(String text ){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

    }

    public void loadimages(){
        contacts = new String[50][6];

        SharedPreferences preferences =getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
        String contacts_array = preferences.getString("contacts", "No contacts");
        try{
            JSONArray jArray = new JSONArray(contacts_array);
            contacts_array_length = jArray.length();
            for (int i=0; i < jArray.length(); i=i+1)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    contacts[i][0]= oneObject.getString("image_url");
                    contacts[i][1]= oneObject.getString("email");
//                    Toast.makeText(getApplicationContext(),contacts[i][1],Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Log.i("excep_welcome_img1",e.toString());
                }
            }
            imageloop(0);

        }catch (Exception e){

            Log.i("excep_welcome_img2",e.toString());

        }
    }


    public void imageloop(int x){
        if (x < contacts_array_length) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                String string = "Loading images";
                textView.setText(string);
                textView_no.setText( Integer.toString(x+1));
                new ImageDownloader().execute(contacts[x][0], Integer.toString(x));
            } else {
                Toast.makeText(getApplicationContext(), "No network connection available!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Log.i("Loaded", "Images");
            Intent newintent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(newintent);
            finish();
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
            textView.setText("");
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
                        Log.i("Loaded","Contacts");
                        fetchMessages();
                        break;
                    case "fetch_messages":
                        SharedPreferences messagefile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
                        jArray = jObject.getJSONArray("messages");
                        messagefile.edit().putString("messages", jArray.toString()).apply();
                        messagefile.edit().putBoolean("inimsg", true).apply();
                        Log.i("Loaded", "Messages");
                        loadimages();
                        break;
                    case "error":
                        String except = jObject.getString("exception");
                        String type = jObject.getString("type");
                        Log.i("error", except);
//                        showToast(except);
                        if(type.equals("fetch_messages")){
//                            dataloaded1 = true;
                        }
                        else {
//                            dataloaded = true;
                        }
                        break;
                    default:
                        break;
                }
            }
            catch (Exception e){
//                Toast.makeText(getApplicationContext(), "onPostExecute: "+e.toString(), Toast.LENGTH_LONG).show();
                Log.i("JSON ka error",e.toString());
//                dataloaded = true;
//                dataloaded1 = true;
            }
            Log.i("onPostexecute",result);
//            Toast.makeText(getApplicationContext(), "onPostExecute: "+result, Toast.LENGTH_SHORT).show();
        }


        private String downloadUrl(String myurl, String postdata, String action) throws IOException {
            InputStream is = null;
            int len = 5000000;
            Log.i("postdata",postdata);
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
                int statusCode = conn.getResponseCode();
                Log.i("statuscode",Integer.toString(statusCode));
//                showToast(Integer.toString(statusCode));
//                Convert the InputStream into a string
                String contentAsString = readIt(is);
                return contentAsString;


            } catch (IOException e) {
                Log.i("printstack",e.toString());
//                showToast("Check your network connection");
                return  "{\"action\": \"error\",\"exception\":\""+e.toString()+"\",\"type\":\""+action+"\"}";
            }
//        String contentAsString = readIt(myInputStream, len);
//            return contentAsString;

        }

        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            InputStream in = stream;
            InputStreamReader is = new InputStreamReader(in);
            StringBuilder sb=new StringBuilder();
            BufferedReader br = new BufferedReader(is);
            String read = br.readLine();

            while(read != null) {
                //System.out.println(read);
                sb.append(read);
                read =br.readLine();

            }

            return sb.toString();
        }
    }


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {


        public ImageDownloader() {

        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            number = Integer.parseInt(urls[1]);
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.toString());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            textView.setText("");
            Log.i("result", Integer.toString(number));
//            Toast.makeText(getApplicationContext(),contacts[number][1],Toast.LENGTH_SHORT).show();
//            int len1 = contacts[number][1].length() - 1;
//            int len2 = contacts[number][1].length() - 6;
//            String email[] = contacts[number][1].split(".");
            String[] emails = contacts[number][1].split("@");
            String path = saveToInternalSorage(result,emails[0]);
            SharedPreferences preferences = getSharedPreferences(QuickstartPreferences.CONTACTS,0);
            preferences.edit().putString("path",path).apply();
            Log.i("path",path);
            imageloop(number+1);

        }
    }


    private String saveToInternalSorage(Bitmap bitmapImage,String email){
        String picname = email + ".png";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("Images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,picname);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.i("kya hua?", "saving mein panga");
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }


}
