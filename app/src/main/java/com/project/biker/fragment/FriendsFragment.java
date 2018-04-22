package com.project.biker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.project.biker.R;
import com.project.biker.ResponseParser.FriendParser;

import com.project.biker.activity.MainActivity;
import com.project.biker.adapter.FriendsListAdapter;
import com.project.biker.service.APIService;
import com.project.biker.service.FriendsListService;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.EndlessRecyclerViewScrollListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;


/**
 * Created by Rahul Padaliya on 7/7/2017
 */
public class FriendsFragment extends Fragment {

    private static final String TAG = "FriendsFragment";
    /**
     * RecyclerView
     */
    private RecyclerView rvFrgFriendList;

    /**
     * Text View
     */
    TextView tvFriendsList;

    /**
     * FriendsListAdapter
     */
    private FriendsListAdapter friendsListAdapter;

    /**
     * SharePreference
     */
    private SharePref sharePref;

    /**
     * FriendsListParser
     */
    private FriendParser.FriendsListParser friendsListParser;
    LinearLayoutManager mLayoutManager;

    private boolean loading = true;

    int pastVisibleItems, visibleItemCount, totalItemCount, totalPage = 0, current_page = 0, counter = 1;

    private ArrayList<FriendParser.FriendsListParser.Record> recordArrayList;

    private EndlessRecyclerViewScrollListener scrollListener;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_friendlist, null);

        //Initialization view
        initializationView(view);

        mLayoutManager = new LinearLayoutManager(getContext());
        sharePref = new SharePref(getContext());
        recordArrayList = new ArrayList<>();

        if (isNetworkAvailable(getActivity())) {
            totalPage = 0;
            current_page = 0;
            counter = 1;
            //Call Friend List API
            callFriendListAPI();
        }else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }

        friendsListAdapter = new FriendsListAdapter(getContext(), recordArrayList);
        rvFrgFriendList.setLayoutManager(mLayoutManager);
        rvFrgFriendList.setItemAnimator(new DefaultItemAnimator());
        rvFrgFriendList.setAdapter(friendsListAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                logDebug("page-->" + page);
                logDebug("totalItemsCount-->" + totalItemsCount);
                    if(counter <= totalPage) {
                        counter++;
                        current_page = totalItemsCount;
                        if (isNetworkAvailable(getActivity())) {
                            //Call Friend List API
                            callFriendListAPI();
                        }else {
                            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

        };
        rvFrgFriendList.addOnScrollListener(scrollListener);


        return view;
    }

    /**
     * Initialization View
     *
     * @param view
     */
    private void initializationView(View view) {
        rvFrgFriendList = (RecyclerView) view.findViewById(R.id.rvFrgFriendList);
        tvFriendsList = (TextView) view.findViewById(R.id.tvFriendsList);
        ((MainActivity) getContext()).textChange(new FriendsFragment());
    }

    /**
     * Call FriendList API
     */
    private void callFriendListAPI() {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");

        FriendsListService.friendList(getContext(), param, current_page, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("FriendList Response-->" + response.toString());
                friendsListParser = FriendParser.FriendsListParser.friendListParser(response);

                recordArrayList.addAll(friendsListParser.getRecordList());
                friendsListAdapter.notifyDataSetChanged();

                showLabelNotDataFound();

                totalPage = Integer.parseInt(friendsListParser.getLastPage());
            }
        });
    }

    /**
     * Show label not data found  When Recycle View Have Not Data
     */
    private void showLabelNotDataFound() {
        if (recordArrayList.size() == 0) {
            tvFriendsList.setVisibility(View.VISIBLE);
        } else {
            tvFriendsList.setVisibility(View.GONE);
        }
    }

    /**
     * Method for Log Printing
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }

    /**
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     * @return
     */
    public static String getFragmentName()
    {
        return FriendsFragment.class.getName();
    }
}
