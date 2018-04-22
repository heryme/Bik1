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
import com.project.biker.adapter.FriendsInvitationAdapter;
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
public class FriendsInvitationFragment extends Fragment {

    private static final String TAG = "FriendsInvitatFragment";

    /**
     * RecyclerView
     */
    private RecyclerView rvFrgFriendsInvitation;

    /**
     * TextView
     */
    TextView tvFrgFriendsInvitation;

    FriendParser.FriendPending friendPending;

    FriendsInvitationAdapter friendsInvitationAdapter;

    private ArrayList<FriendParser.FriendPending.Record> pendingArrayList;

    LinearLayoutManager mLayoutManager;
    private EndlessRecyclerViewScrollListener scrollListener;

    SharePref sharePref;
    private boolean loading = true;
    int pastVisibleItems, visibleItemCount, totalItemCount, totalPage = 0, current_page = 0, counter = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_friends_invitation, null);

        //Initialization view
        initializationView(view);

        pendingArrayList = new ArrayList<>();
        sharePref = new SharePref(getContext());
        mLayoutManager = new LinearLayoutManager(getContext());

        if (isNetworkAvailable(getActivity())) {
            totalPage = 0;
            current_page = 0;
            counter = 1;
            //Call Pending Friend
            callPendingFriendAPI();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }


        friendsInvitationAdapter = new FriendsInvitationAdapter(getContext(), pendingArrayList, tvFrgFriendsInvitation);
        rvFrgFriendsInvitation.setLayoutManager(mLayoutManager);
        rvFrgFriendsInvitation.setItemAnimator(new DefaultItemAnimator());
        rvFrgFriendsInvitation.setAdapter(friendsInvitationAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                logDebug("page-->" + page);
                logDebug("totalItemsCount-->" + totalItemsCount);
                if (counter <= totalPage) {

                    counter++;
                    current_page = totalItemsCount;
                    if (isNetworkAvailable(getActivity())) {
                        //Call Pending Friend
                        callPendingFriendAPI();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        rvFrgFriendsInvitation.addOnScrollListener(scrollListener);
        return view;
    }

    /**
     * Initialization View
     *
     * @param view
     */
    private void initializationView(View view) {
        rvFrgFriendsInvitation = (RecyclerView) view.findViewById(R.id.rvFrgFriendsInvitation);
        tvFrgFriendsInvitation = (TextView) view.findViewById(R.id.tvFrgFriendsInvitation);

        ((MainActivity) getContext()).textChange(new FriendsInvitationFragment());
    }

    /**
     * Call FriendList API
     */
    private void callPendingFriendAPI() {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");
        FriendsListService.friendPending(getContext(), current_page, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("FriendList Response-->" + response.toString());

                friendPending = FriendParser.FriendPending.pendingFriendParser(response);
                pendingArrayList.addAll(friendPending.getRecordList());
                friendsInvitationAdapter.notifyDataSetChanged();
                showLabelNotDataFound();
                totalPage = Integer.parseInt(friendPending.getLastPage());
                current_page = Integer.parseInt(friendPending.getCurrentPage());
            }
        });
    }

    /**
     * Show label not data found  When Recycle View Have Not Data
     */
    private void showLabelNotDataFound() {
        if (pendingArrayList.size() == 0) {
            tvFrgFriendsInvitation.setVisibility(View.VISIBLE);
        } else {
            tvFrgFriendsInvitation.setVisibility(View.GONE);
        }
    }

    /**
     * Method To Print Log
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.e(TAG, msg);
    }

    /**
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     *
     * @return
     */
    public static String getFragmentName() {
        return FriendsInvitationFragment.class.getName();
    }

}
