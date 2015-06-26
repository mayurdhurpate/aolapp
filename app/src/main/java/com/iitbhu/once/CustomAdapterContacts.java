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

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class CustomAdapterContacts extends RecyclerView.Adapter<CustomAdapterContacts.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private String[] mDataSet;


    public void updateItems(String newItem,String tab){
        Log.i("newitem",newItem);
        mDataSet = new String[mDataSet.length];

        try{
            JSONArray jArray = new JSONArray(newItem);


                for (int i = 0; i < jArray.length(); i=i+1)

                {
                    try {
                        JSONObject oneObject = jArray.getJSONObject(i);
                        mDataSet[2*i] = oneObject.getString("name");
                        mDataSet[2*i+1] = "Member";
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
       public TextView textView;

        public ViewHolder(View v,int viewType) {
            super(v);
            // Define click listener for the ViewHolder's View.
//            v.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.d(TAG, "Element " + getPosition() + " clicked.");
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
    public CustomAdapterContacts(String[] dataSet) {
        mDataSet = dataSet;
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
                Log.i("ItemCount",e.toString());
                break;
            }
        }
        return i;
    }
}
