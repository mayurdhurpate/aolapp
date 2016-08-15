package com.iitbhu.once;


import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_MESSAGE = "";
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private RecyclerView.Adapter mAdapter;
    public BroadcastReceiver mRegistrationBroadcastReceiver;
    private RecyclerView.LayoutManager mLayoutManager;
    public boolean dataloaded = false;
    public boolean dataloaded1 = false;
    public LinearLayout pgbar;
    public EditText bmsg;
    public EditText bmsgtitle;
    public Button bbutton;
    public TextView imtxt;
    public  String bmsgholder="";
    public String topic_selected;

    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private Bitmap bitmapBroadcast;
    private ImageView imageViewBroadcast;
    private String encodedImage;
    public SharedPreferences userFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean tokenmila = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);

        SharedPreferences contactsfile = getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
        boolean inicnt = contactsfile.getBoolean("inicnt", false);

        SharedPreferences messagesfile = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
        boolean inimsg = messagesfile.getBoolean("inimsg", false);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences messagesfile1 = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
                final String messages1 = messagesfile1.getString("messages", "mpatanahi");
                updateUI(messages1,0,"messages");
                showToast("Messages updated");
            }
        };

        SharedPreferences contactsfile1 = getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
        String contacts = contactsfile1.getString("contacts", "no contacts");
        SharedPreferences messagesfile1 = getSharedPreferences(QuickstartPreferences.MESSAGES, 0);
        String messages = messagesfile1.getString("messages", "no messages");

        if(savedInstanceState==null) {

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
            Bundle bundle = new Bundle();
            bundle.putString("contacts", contacts);
            bundle.putString("messages", messages);
            fragment.setArguments(bundle);
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();

        }

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

        userFile = getSharedPreferences(QuickstartPreferences.USERDATA, 0);
//        ActionBar bar = getSActionBar();
//        bar.setBackgroundDrawable(new ColorDrawable(R.color.maroon));
        encodedImage = "";




}
    @Override
    protected void onResume(){
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.NOTIFY));
    }

    @Override
    protected void onPause(){
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
            Intent newintent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(newintent);
            return true;
        }
        else if(id == R.id.action_account){
            Intent newintent = new Intent(getBaseContext(), Myaccount.class);
            startActivity(newintent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchContacts(){

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            showToast("Updating Contacts");
            new DownloadWebpageTask().execute("http://128.199.123.200/contacts/", "passkey=hellolastry", "fetch_contacts");
        } else {
            showToast("No network connection available!");
            dataloaded = true;
        }
    }

    public void fetchMessages(){

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            showToast("Updating Messages");
            new DownloadWebpageTask().execute("http://128.199.123.200/messages/", "passkey=hellolastry", "fetch_messages");
        } else {
            showToast("No network connection available!");
            dataloaded1 = true;
        }
    }





    public void broadcastProceed(View view) {

        bmsg = (EditText)findViewById(R.id.edit_broadcast);
        bbutton = (Button)findViewById(R.id.bbutton);
        bmsgtitle = (EditText)findViewById(R.id.edit_broadcast_title);

        pgbar.setVisibility(View.VISIBLE);
        bmsg.setEnabled(false);
        bbutton.setEnabled(false);
        bmsgtitle.setEnabled(false);

        Spinner spinner = (Spinner) findViewById(R.id.broadcast_spinner);
        String spin_val = spinner.getSelectedItem().toString();


        String username = userFile.getString("name", "user");
        String email = userFile.getString("email","no_email");
        String token = userFile.getString("token", "no_token");

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            showToast("Sending message");
            new DownloadWebpageTask().execute("http://128.199.123.200/broadcastreceive/","username="+username+"&token="+token+"&email="+email+"&passkey=hellolastry"+"&bmsg="+bmsg.getText()+"&bmsg_title="+bmsgtitle.getText()+"&topic="+spin_val+"&image="+encodedImage,"broadcast_msg");
        } else {
            showToast("No network connection available!");
            pgbar.setVisibility(View.GONE);
            bmsg.setEnabled(true);
            bbutton.setEnabled(true);

        }

    }

    public void showToast(String text ){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();

    }

    public void updateUI(String newItem, int flag ,String tab){
        RecyclerView mRecyclerView;

        if(tab.equals("messages")){
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
        else{
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
    }

    public void getImage(View arg0){
        TextView username = (TextView)findViewById(R.id.textViewSender_msg);
        username.setText(userFile.getString("name","Name Surname"));
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Log.d(TAG,selectedImageUri.toString());
                selectedImagePath = getPath(selectedImageUri);
                Log.d(TAG,selectedImagePath);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath,options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;
                String imageType = options.outMimeType;
                Log.d(TAG,Integer.toString(imageHeight)+" "+Integer.toString(imageHeight)+imageType);
                options.inSampleSize = calculateInSampleSize(options, 300, 300);
                options.inJustDecodeBounds = false;
                bitmapBroadcast = BitmapFactory.decodeFile(selectedImagePath, options);
                imageViewBroadcast = (ImageView)findViewById(R.id.broadcastImageView);

                //test
//                saveToExternalStorage(bitmapBroadcast, "image");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapBroadcast.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                imageViewBroadcast.setImageBitmap(bitmapBroadcast);
                byte[] b = baos.toByteArray();
                encodedImage = Base64.encodeToString(b, Base64.URL_SAFE);
                Log.d(TAG, Integer.toString(encodedImage.length()));

//
//                bmsgtitle = (EditText)findViewById(R.id.edit_broadcast_title);
//                bmsgtitle.setText(encodedImage);
            }
        }
    }

    private String saveToExternalStorage(Bitmap bitmapImage,String email){
        String picname = email + ".jpg";
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("Images", Context.MODE_PRIVATE);
        File directory1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES+ "/AOL-Varanasi/");
        // Create imageDir
        if (!directory1.exists()) {
            directory1.mkdirs();
        }
        File mypath=new File(directory1,picname);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.i("kya hua?", "saving mein panga");
            e.printStackTrace();
        }
        Log.d(TAG,directory1.getAbsolutePath());
        return directory1.getAbsolutePath();
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            showToast("uri:null Image obtained");
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
                        String error = jObject.getString("error");
                        pgbar.setVisibility(View.GONE);
                        bmsg.setEnabled(true);
                        bmsgtitle.setEnabled(true);
                        bbutton.setEnabled(true);
                        bmsg.setText("");
                        bmsgholder = "";
                        if(error.equals("true")){
                            Toast.makeText(getApplicationContext(),"Please login again",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "error":
                        String except = jObject.getString("exception");
                        String type = jObject.getString("type");
                        Log.i("error", except);
                        showToast(except);
                        if(type.equals("broadcast_msg")){
                            pgbar.setVisibility(View.GONE);
                            bmsg.setEnabled(true);
                            bmsgtitle.setEnabled(true);
                            bbutton.setEnabled(true);
                        }
                        else if(type.equals("fetch_messages")){
                            dataloaded1 = true;
                        }
                        else {
                            dataloaded = true;
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
//            Log.i("postdata",postdata);
//            Log.i("posdata",Integer.toString(postdata.length()));
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

                int statusCode = conn.getResponseCode();
                Log.i("statuscode", Integer.toString(statusCode));
                try {
                    is = conn.getInputStream();
                }
                catch (Exception e){
                    Log.d(TAG,e.toString());
                    is = conn.getErrorStream();
                    generateNoteOnSD("error.html",readIt(is));
                    generateNoteOnSD("error.txt",readIt(is));
                }
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


    public void generateNoteOnSD(String sFileName, String sBody){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
//            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            Log.d(TAG,e.toString());
        }
    }




}





