/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.iitbhu.once;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapterMessages extends RecyclerView.Adapter<CustomAdapterMessages.ViewHolder> {
    private static final String TAG = "CustomAdapterMessages";
    public  Context context;
    private String[][] mDataSet;
    private int messageCount;


    public void updateItems(String newItem,String tab){
        try{
            JSONArray jArray = new JSONArray(newItem);
            mDataSet = new String[jArray.length()][10];

            for (int i = 0; i < jArray.length(); i=i+1)

            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    mDataSet[i][0] = oneObject.getString("id");
                    mDataSet[i][1] = oneObject.getString("topic");
                    mDataSet[i][2] = oneObject.getString("date");
                    mDataSet[i][3] = oneObject.getString("time");
                    mDataSet[i][4] = oneObject.getString("title");
                    mDataSet[i][5] = oneObject.getString("sender");
                    mDataSet[i][6] = oneObject.getString("message");
                    mDataSet[i][7] = oneObject.getString("url");

                } catch (JSONException e) {
                    Log.i("excep_adapt_msgs1", e.toString());
                }
            }
        }catch (Exception e){

            Log.i("excep_adapt_msgs2",e.toString());

        }

    }



    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewContent;
        public TextView textViewSender;
        public TextView textViewDateTime;
        public TextView textViewTitle;
        public TextView textViewTopic;
        public ImageView myImageView;

        public ViewHolder(View v,int viewType) {
            super(v);
            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Log.d(TAG, "Element " + getPosition() + " clicked.");
//                    /***************test calender***********/
//
//                }
//            });

            textViewSender = (TextView) v.findViewById(R.id.textViewSender);
            textViewContent = (TextView) v.findViewById(R.id.textViewContent);
            textViewTopic = (TextView) v.findViewById(R.id.textViewTopic);
            textViewDateTime = (TextView) v.findViewById(R.id.textViewDateTime);
            textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            textViewContent = (TextView) v.findViewById(R.id.textViewContent);
            myImageView = (ImageView) v.findViewById(R.id.myImageView);
        }



    }


    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapterMessages(String[][] dataSet,Context context, int Count) {
        mDataSet = dataSet;
        this.context = context;
        messageCount = Count;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return 1;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ViewHolder v1;
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_messages, viewGroup, false);
        v1 = new ViewHolder(v,viewType);
        return v1;
    }



    // END_INCLUDE(recyclerViewOnCreateViewHolder)




    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.textViewTopic.setText(mDataSet[position][1]);
        viewHolder.textViewDateTime.setText(mDataSet[position][2]+" "+mDataSet[position][3]);
        viewHolder.textViewTitle.setText(mDataSet[position][4]);
        viewHolder.textViewSender.setText(mDataSet[position][5]);
        viewHolder.textViewContent.setText(mDataSet[position][6]);



            if (!mDataSet[position][7].equals(null)) {
                if (!mDataSet[position][7].equals("")) {

                    Log.i("url", mDataSet[position][7]);
                    String picname = "aol_" + mDataSet[position][0];
                    loadImageFromExternalStorage(viewHolder.myImageView, picname, mDataSet[position][7]);
                }
            }
        
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                try {
                    TextView txtview =(TextView)v.findViewById(R.id.textViewContent);
                    String txt = txtview.getText().toString();
                    dialogview(context, txt);
                }catch(Exception e){
                    Log.e("msgonclick",e.toString());

                }
//                Calendar beginTime = Calendar.getInstance();
//                beginTime.set(2012, 0, 19, 7, 30);
//                Calendar endTime = Calendar.getInstance();
//                endTime.set(2012, 0, 19, 8, 30);
//                Intent intent = new Intent(Intent.ACTION_INSERT)
//                        .setData(CalendarContract.Events.CONTENT_URI)
//                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
//                        .putExtra(CalendarContract.Events.TITLE, "Yoga")
//                        .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
//                        .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
//                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
//                        .putExtra(Intent.EXTRA_EMAIL, "omkarjadhav003@gmail.com,trevor@example.com");
//                v.getContext().startActivity(intent);
                return true;
            }
        });

    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return messageCount;
    }

    public void dialogview(final Context context, final String v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final String [] items = new String[] {"Add to calendar","Copy to clipboard","Share via"};
        final Integer[] icons = new Integer[] {R.drawable.ic_event_black_24dp, R.drawable.ic_content_copy_black_24dp,R.drawable.ic_share_black_24dp};
        ListAdapter adapter = new ArrayAdapterWithIcon(context, items, icons);

        builder.setAdapter(adapter,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        Log.i("Add to calendar","added");
                        break;
                    case 1:
                        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("AOL", v);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(context,"Copied to clipboard",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, v);
                        sendIntent.setType("text/plain");
                        context.startActivity(Intent.createChooser(sendIntent, context.getResources().getText(R.string.share)));
                        break;
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        private String pichalf;
        public ImageDownloader() {}

        protected Bitmap doInBackground(String... urls) {
            Log.i("Image downloading","Yes");
            String url = urls[0];
            pichalf = urls[1];
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
//            Log.i("image",result.toString());
            saveToExternalStorage(result, pichalf);
            Log.i("hua?","ha save hua");
        }
    }

    private String saveToExternalStorage(Bitmap bitmapImage,String email){
        String picname = email + ".jpg";
        File directory1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/AOL-Varanasi/");
        // Create imageDir
        directory1.mkdirs();
        File mypath=new File(directory1,picname);

        FileOutputStream fos = null;
        try {

            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            Log.i("kya hua?", "saving mein panga");
            e.printStackTrace();
            mypath.delete();
        }
        Log.d(TAG,directory1.getAbsolutePath());
        return directory1.getAbsolutePath();
    }

    private boolean loadImageFromExternalStorage( ImageView v, String picname, String url)
    {
        File directory1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/AOL-Varanasi/");
        String path = directory1.getAbsolutePath();
        try {
            File f=new File(path, picname+".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            v.setImageBitmap(b);
            Log.i("loadImageFromExtStorage","try completed");
            return true;
        }
        catch (FileNotFoundException e)
        {
            Log.i("loadImageFromStorage", e.toString());
            v.setImageResource(R.drawable.srisri);
//            new ImageDownloader().execute(url,picname);
            new DownloadBase64Task(v).execute(url,picname);
            return false;
        }
    }

    public class DownloadBase64Task extends AsyncTask<String, Void, String> {
        private String pichalf;
        private final WeakReference<ImageView> imageViewReference;
        public DownloadBase64Task(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            pichalf = urls[1];
            try {
                String statusCode = downloadUrl(urls[0]);


                return statusCode;

            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("Result", result);
            Log.i(TAG, Integer.toString((result.length())));
            try {
                byte[] decodedString = Base64.decode(result, Base64.URL_SAFE);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                saveToExternalStorage(decodedByte, pichalf);
                Log.i(TAG, pichalf + " saved to ext.");
                if (imageViewReference != null && decodedByte != null) {
                    final ImageView imageView = imageViewReference.get();
                    if (imageView != null) {
                        imageView.setImageBitmap(decodedByte);
                    }
                }
            }catch (Exception e){
                Log.e(TAG,pichalf+e.toString());
            }
        }
    }



    private String downloadUrl(String myurl) throws IOException{
        InputStream is = null;
//        int len = 50000;
        URL url;
        try {
            url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

//            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//            wr.write(postdata);
//            wr.flush();
//            wr.close();

            is = conn.getInputStream();
            //Convert the InputStream into a string
            String contentAsString = readIt(is);
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
