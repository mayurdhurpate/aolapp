package com.iitbhu.once;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.common.SignInButton;


public class Welcome extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    // ...



    private static final String TAG = "RegIntentService";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public BroadcastReceiver mRegistrationBroadcastReceiver;
    public ProgressBar mRegistrationProgressBar;
    public Button proceedbutton;
    public EditText editText;
    public final static String EXTRA_MESSAGE = "";

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

        editText.setEnabled(true);
        proceedbutton.setEnabled(true);
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
        editText = (EditText)findViewById(R.id.edit_phone);
        proceedbutton = (Button)findViewById(R.id.buttonProceed);
        editText.setEnabled(true);
        proceedbutton.setEnabled(true);
        editText.setVisibility(View.VISIBLE);
        proceedbutton.setVisibility(View.VISIBLE);
        SignInButton gplus = (SignInButton)findViewById(R.id.sign_in_button);
        gplus.setVisibility(View.GONE);


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
        mRegistrationProgressBar.setVisibility(ProgressBar.GONE);

        editText = (EditText)findViewById(R.id.edit_phone);
        proceedbutton = (Button)findViewById(R.id.buttonProceed);
        editText.setEnabled(false);
        proceedbutton.setEnabled(false);
        editText.setVisibility(View.GONE);
        proceedbutton.setVisibility(View.GONE);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
//                    Toast.makeText(getApplicationContext(),"tokensaved",Toast.LENGTH_SHORT).show();
                    Intent newintent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(newintent);
                    finish();
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
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
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



     /** Called when the user clicks the Proceed button */
    public void Proceed(View view) {
//        ProgressBar mRegistrationProgressBar = new ProgressBar(this);
//        Intent intent = new Intent(this, RegistrationIntentService.class);
//        startService(intent);
        mRegistrationProgressBar.setVisibility(ProgressBar.VISIBLE);
        if (checkPlayServices()) {


                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhoto = currentPerson.getImage().getUrl();
                String personGooglePlusId = currentPerson.getId();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);


//            Toast.makeText(getApplicationContext(), "Debug:showSignedInUI" + personPhoto+personGooglePlusId+personName, Toast.LENGTH_LONG).show();


            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            EditText editText = (EditText) findViewById(R.id.edit_phone);
            String message = editText.getText().toString();
            intent.putExtra(EXTRA_MESSAGE, message);
            intent.putExtra("name",personName);
            intent.putExtra("googlePlusId",personGooglePlusId);
            intent.putExtra("email",email);
            intent.putExtra("image",personPhoto);


            startService(intent);

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


}
