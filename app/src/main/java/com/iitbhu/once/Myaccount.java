package com.iitbhu.once;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;


public class Myaccount extends AppCompatActivity {
    public SharedPreferences preferences;
    public String image_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        preferences = getSharedPreferences(QuickstartPreferences.USERDATA,0);
        String imageurl = preferences.getString("image","no_url");
        String name = preferences.getString("name","user");
        String email = preferences.getString("email","email");
        String phone = preferences.getString("phone","phone");
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        TextView nametext = (TextView)findViewById(R.id.acc_name);
        TextView emailtext = (TextView)findViewById(R.id.acc_email);
        TextView phonetext = (TextView)findViewById(R.id.acc_phone);
        nametext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_person_black_18dp, 0, 0, 0);
        emailtext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mail_black_24dp, 0, 0, 0);
        phonetext.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_local_phone_black_24dp, 0, 0, 0);
        nametext.setText(name);
        emailtext.setText(email);
        phonetext.setText(phone);

        boolean isProfileImageSaved = preferences.getBoolean("profilepic",false);
        Log.d("isProfileImageSaved",isProfileImageSaved?"true":"false");
        if(isProfileImageSaved){
            boolean loadedImageFromStorage =  loadImageFromStorage(preferences.getString("profilepicpath", "no_path"));
            if (!loadedImageFromStorage){
                Log.i("loadedImageFromStorage",loadedImageFromStorage?"true":"false");
                Imagedownload(imageView);
            }
        }
        else{
            Imagedownload(imageView);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_myaccount, menu);
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



    public void Imagedownload(ImageView imageView){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new ImageDownloader(imageView).execute("https://lh5.googleusercontent.com/-S9_1Kar2j9c/AAAAAAAAAAI/AAAAAAAAABc/3b1dD5FDrF4/photo.jpg?sz=200");
        } else {
            Toast.makeText(getApplicationContext(),"No network connection available!",Toast.LENGTH_SHORT).show();
        }
    }



    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            Log.i("result","result aaya");
            bmImage.setImageBitmap(result);
            try {
                image_path = saveToInternalSorage(result);
                Log.i("path",image_path);
                preferences.edit().putBoolean("profilepic", true).apply();
                preferences.edit().putString("profilepicpath",image_path).apply();
            }catch(Exception e){
                Log.i("imagenotsave",e.toString());
            }

        }
    }


    private String saveToInternalSorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("Images", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.png");

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.i("kya hua?","saving mein panga");
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    private boolean loadImageFromStorage(String path)
    {

        try {
            File f=new File(path, "profile.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ImageView img=(ImageView)findViewById(R.id.imageView);
            img.setImageBitmap(b);
            Log.i("loadImageFromStorage","try completed");
            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.e("loadImageFromStorage",e.toString());
            return false;
        }
    }


}
