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
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapterMessages extends RecyclerView.Adapter<CustomAdapterMessages.ViewHolder> {
    private static final String TAG = "CustomAdapterMessages";
    public  Context context;
    private String[] mDataSet;
    public String msgtxt;


    public void updateItems(String newItem,String tab){
        mDataSet = new String[mDataSet.length];

        try{
            String msg;
            String sender;
            JSONArray jArray = new JSONArray(newItem);




                for (int i = 0; i < jArray.length(); i=i+1)

                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        msg = oneObject.getString("message");
                        sender = oneObject.getString("sender");
                        mDataSet[2*i] = sender;
                        mDataSet[2*i+1] = msg;

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
        public TextView textView;

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
            switch (viewType){
                case 0:
                    textView = (TextView) v.findViewById(R.id.textViewSender);
                    break;
                case 1:
                    textView = (TextView) v.findViewById(R.id.textView);
                    break;

            }
        }

        public TextView getTextView() {
            return textView;
        }
    }


    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public CustomAdapterMessages(String[] dataSet,Context context) {
        mDataSet = dataSet;
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
        switch (viewType) {
            case 0:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.text_row_contact, viewGroup, false);
                v1 = new ViewHolder(v,viewType);
                break;

            case 1:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.text_row_contacts2, viewGroup, false);
                v1 = new ViewHolder(v,viewType);
                break;
            default:
                v = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.text_row_contact, viewGroup, false);
                v1 = new ViewHolder(v,viewType);
                break;

        }

        return v1;

    }


// {
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setView(R.layout.dialogmessages);
//
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//
//
//    }


    // END_INCLUDE(recyclerViewOnCreateViewHolder)




    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        switch (position % 2){
            case 0:
                viewHolder.getTextView().setText(mDataSet[position]);
                break;
            case 1:
                viewHolder.getTextView().setText(mDataSet[position]);
                break;
            default:
                viewHolder.getTextView().setText("default text");
                break;
//                viewHolder.getItemId();
        }

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override public boolean onLongClick(View v) {
                try {
                    TextView txtview =(TextView)v.findViewById(R.id.textView);
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
        int i;
        boolean p;
        for (i=0; i <mDataSet.length ; i++) {
            try{
                p = mDataSet[i].equals("");
            }catch (Exception e){
//                Log.i("ItemCount",e.toString());
                break;
            }
        }
        return i;
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
}
