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

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapterContacts extends RecyclerView.Adapter<CustomAdapterContacts.ViewHolder> {
    private static final String TAG = "CustomAdapterContacts";

    private String[][] mDataSet;
    public  Context context;
    private int count;


    public void updateItems(String newItem,String tab){
        Log.i("newitem",newItem);

        try{
            JSONArray jArray = new JSONArray(newItem);
            mDataSet = new String[jArray.length()][6];

            for (int i = 0; i < jArray.length(); i=i+1)

                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        mDataSet[i][0] = oneObject.getString("name");
                        mDataSet[i][1] = oneObject.getString("email");
//                        Log.i("email",contacts[i][1]);
                        if(!oneObject.getString("role").equals("")){
                            mDataSet[i][2] = oneObject.getString("role");
                        }else{
                            mDataSet[i][2] = "Member";
                        }
                    } catch (JSONException e) {
                        Log.i("excep_adapt_cnts1", e.toString());
                    }
                }



            }

        catch (Exception e){

            Log.i("excep_adapt_cnts2",e.toString());

        }

    }



    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */



    public static class ViewHolder extends RecyclerView.ViewHolder {
       public TextView textViewContactRole;
        public TextView textViewContactName;
        public ImageView imageView;

        public ViewHolder(View v,int viewType) {
            super(v);
            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getPosition() + " clicked.");
//                }
//            });

            textViewContactName = (TextView) v.findViewById(R.id.textViewContactName);
            textViewContactRole = (TextView) v.findViewById(R.id.textViewContactRole);
                    imageView = (ImageView) v.findViewById(R.id.profileImage);

            }


        public ImageView getImageview() {return imageView;}
        public TextView getTextView() {
            return textViewContactRole;
    }
        public TextView getTextViewSender() {
            return textViewContactName;
        }

    }





    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapterContacts(String[][] dataSet, Context context, int itemCount) {
        mDataSet = dataSet;
        count = itemCount;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position % 2;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        ViewHolder v1;
        View v;
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.view_contact, viewGroup, false);
                v1 = new ViewHolder(v,viewType);


        return v1;

    }


    // END_INCLUDE(recyclerViewOnCreateViewHolder)



    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
//        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getTextViewSender().setText(mDataSet[position][0]);
        viewHolder.getTextView().setText(mDataSet[position][2]);
        String pic = mDataSet[position][1];
        String[] parts = pic.split("@");
        String picname = parts[0]+".png";

        loadImageFromStorage(viewHolder.getImageview(), picname,mDataSet[position][3],parts[0]);

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                try {
                    final TextView txtview = (TextView) v.findViewById(R.id.textViewContactName);
                    final String txt = txtview.getText().toString();
                    dialogtocontacts(context, txt);

                } catch (Exception e) {
                    Log.e("onclick", e.toString());

                }
                return true;

            }
        });

    }


    public void dialogtocontacts(final Context context,final String v){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // Use the Builder class for convenient dialog construction
        builder.setMessage(R.string.add_to_contacts)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        addAsContactConfirmed (context,v);

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        Button nbutton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(context.getResources().getColor(R.color.dark_orange));
        Button pbutton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(context.getResources().getColor(R.color.dark_orange));


    }

    public static void addAsContactConfirmed ( final Context context, final String name) {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME,name);
//        intent.putExtra(ContactsContract.Intents.Insert.PHONE, person.mobile);
//        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, person.email);

        context.startActivity(intent);

    }



    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return count;
    }

    private boolean loadImageFromStorage( ImageView v, String picname, String url,String pichalf)
    {
        SharedPreferences preferences = context.getSharedPreferences(QuickstartPreferences.CONTACTS,0);
        String path = preferences.getString("path","nopath");
        try {
            File f=new File(path, picname);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            v.setImageBitmap(b);
//            Log.i("loadImageFromStorage","try completed");
            return true;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.e("loadImageFromStorage", e.toString());
            v.setImageResource(R.drawable.ic_person_black_48dp);
            new ImageDownloader().execute(url,pichalf);
            return false;
        }
    }

    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        private String pichalf;
        public ImageDownloader() {}

        protected Bitmap doInBackground(String... urls) {
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
            saveToInternalSorage(result,pichalf);
            Log.i("hua?","ha save hua "+pichalf);
        }
    }


    private String saveToInternalSorage(Bitmap bitmapImage,String email){
        String picname = email + ".png";
        ContextWrapper cw = new ContextWrapper(context);
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
            mypath.delete();
            }
        return directory.getAbsolutePath();
    }

}
