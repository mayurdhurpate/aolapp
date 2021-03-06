/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.iitbhu.once.common.view.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Handler handler = new Handler();




    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;

    public Bundle bundle;

    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        bundle = this.getArguments();
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        // END_INCLUDE (setup_viewpager)

        // BEGIN_INCLUDE (setup_slidingtablayout)
        // Give the SlidingTabLayout the ViewPager, this must be done AFTER the ViewPager has had
        // it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);

        //setting indicator and divider color
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.maroon);    //define any color in xml resources and set it here, I have used white
            }
        });

        mSlidingTabLayout.setSelectedIndicatorColors(R.color.white, R.color.white,R.color.white,R.color.white);




        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */
        @Override
        public int getCount() {
            return 3;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Contacts";
                case 1: return "Messages";
                case 2: return "Broadcast";
                default: return "Item " + (position + 1);
            }

        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view;

            switch (position) {
                case 2:
                    view = getActivity().getLayoutInflater().inflate(R.layout.broadcast_layout, container, false);
                    container.addView(view);
                    EditText bmsg = (EditText)view.findViewById(R.id.edit_broadcast);
                    bmsg.setText(((MainActivity)getActivity()).bmsgholder);
                    ((MainActivity)getActivity()).pgbar = (LinearLayout)view.findViewById(R.id.broadcastProgressBar);
                    ((MainActivity)getActivity()).pgbar.setVisibility(View.GONE);
//                    Spinner spinner = (Spinner)view.findViewById(R.id.broadcast_spinner);

                    SharedPreferences topic_pref = (getActivity()).getSharedPreferences(QuickstartPreferences.TOPICS, 0);
                    Map<String, ?> keys = topic_pref.getAll();
                    String[] topics = new String[keys.size()];
                    int j = 0;
                    for(Map.Entry<String,?> entry : keys.entrySet()){
                        topics[j] = entry.getValue().toString();
                        j++;
                    }


                    Spinner spinner = (Spinner) view.findViewById(R.id.broadcast_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.topics_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
                    spinner.setAdapter(adapter);

                    break;

                case 0:
                    view = getActivity().getLayoutInflater().inflate(R.layout.contactslayout, container, false);
                    container.addView(view);
                    mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    String[][] contacts = new String[1][6];
                    SharedPreferences preferences =(getActivity()).getSharedPreferences(QuickstartPreferences.CONTACTS, 0);
                    String contacts_array = preferences.getString("contacts", "No contacts");
                    int itemCount = 0;
                    Log.i("contacts_array",contacts_array);
                    try{
                        JSONArray jArray = new JSONArray(contacts_array);
                        itemCount = jArray.length();
                        contacts = new String[itemCount][6];
                        for (int i=0; i < jArray.length(); i=i+1)
                        {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                contacts[i][0] = oneObject.getString("name");
                                contacts[i][1] = oneObject.getString("email");
                                Log.i("email",contacts[i][1]);
                                if(!oneObject.getString("role").equals("")){
                                    contacts[i][2] = oneObject.getString("role");
                                }else{
                                    contacts[i][2] = "Member";
                                }
                                contacts[i][3] = oneObject.getString("image_url");
                            } catch (JSONException e) {
                                Log.i("excep_sliding_conts1",e.toString());
                            }
                        }

                    }catch (Exception e){

                    Log.i("excep_sliding_conts2",e.toString());

                }



                    mAdapter = new CustomAdapterContacts(contacts,getActivity(),itemCount);
                    mRecyclerView.setAdapter(mAdapter);


                    final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.activity_main_swipe_refresh_layout);
                    mSwipeRefreshLayout.setColorSchemeResources( R.color.dark_orange,R.color.orange, R.color.maroon);
                     final Runnable refreshing = new Runnable() {
                        public void run() {
                            try {

                                boolean data = ((MainActivity) getActivity()).dataloaded;
                                // TODO : isRefreshing should be attached to your data request status
                                if (!data) {
                                    // re run the verification after 1 second
                                    handler.postDelayed(this, 1000);
                                } else {
                                    // stop the animation after the data is fully loaded
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    // TODO : update your list with the new data
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            ((MainActivity) getActivity()).dataloaded = false;
                            ((MainActivity) getActivity()).fetchContacts();
                            handler.post(refreshing);
                        }
                    });




                    break;


                case 1:
                    view = getActivity().getLayoutInflater().inflate(R.layout.messageslayout, container, false);
                    container.addView(view);
                    mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView2);
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    String[][] messages = new String[1][10];
                    int messageCount=0;
                    SharedPreferences preferences2 =(getActivity()).getSharedPreferences(QuickstartPreferences.MESSAGES,0);
                    String messages_array = preferences2.getString("messages","No messages");
                    Log.i("messages_array",messages_array);
                    try{
                        JSONArray jArray = new JSONArray(messages_array);
                        messageCount = jArray.length();
                        messages = new String[messageCount][10];
                        for (int i=0; i < jArray.length(); i=i+1)

                        {
                            try {
                                JSONObject oneObject = jArray.getJSONObject(i);
                                messages[i][0] = oneObject.getString("id");
                                messages[i][1] = oneObject.getString("topic");
                                messages[i][2] = oneObject.getString("date");
                                messages[i][3] = oneObject.getString("time");
                                messages[i][4] = oneObject.getString("title");
                                messages[i][5] = oneObject.getString("sender");
                                messages[i][6] = oneObject.getString("message");
                                messages[i][7] = oneObject.getString("url");

                            } catch (JSONException e) {
                                Log.i("excep_sliding_msgs1",e.toString());
                            }
                        }

                    }catch (Exception e){

                        Log.i("excep_sliding_msgs1",e.toString());

                    }
                    mAdapter = new CustomAdapterMessages(messages,getActivity(),messageCount);

                    mRecyclerView.setAdapter(mAdapter);
                    final SwipeRefreshLayout mSwipeRefreshLayout1 = (SwipeRefreshLayout)view.findViewById(R.id.activity_main_swipe_refresh_layout);
                    mSwipeRefreshLayout1.setColorSchemeResources(R.color.maroon, R.color.dark_orange,R.color.orange);
                    final Runnable refreshing1 = new Runnable() {
                        public void run() {
                            try {

                                boolean data = ((MainActivity) getActivity()).dataloaded1;
                                // TODO : isRefreshing should be attached to your data request status
                                if (!data) {
                                    // re run the verification after 1 second
                                    handler.postDelayed(this, 1000);
                                } else {
                                    // stop the animation after the data is fully loaded
                                    mSwipeRefreshLayout1.setRefreshing(false);
                                    // TODO : update your list with the new data
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    mSwipeRefreshLayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                            ((MainActivity) getActivity()).dataloaded1 = false;
                            ((MainActivity) getActivity()).fetchMessages();
                            handler.post(refreshing1);
                        }
                    });

                    break;



                default:
                    view = getActivity().getLayoutInflater().inflate(R.layout.pager_item, container, false);
                    container.addView(view);
                    TextView title = (TextView) view.findViewById(R.id.item_title);
                    title.setText(String.valueOf(position + 1));
                    break;
            }
            return view;
        }


        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view;
            if(position == 2){
                EditText bmsg = (EditText)container.findViewById(R.id.edit_broadcast);
                ((MainActivity)getActivity()).bmsgholder = bmsg.getText().toString();
            }
            container.removeView((View) object);
        }

    }



}
