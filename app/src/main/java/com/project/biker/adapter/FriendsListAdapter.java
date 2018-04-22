package com.project.biker.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.biker.R;
import com.project.biker.ResponseParser.FriendParser;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.model.INTFConfirmYesNo;
import com.project.biker.service.APIService;
import com.project.biker.service.FriendsListService;
import com.project.biker.tools.Constant;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.RoundRectCornerImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vikas Patel on 7/13/2017.
 */

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.MyViewHolder>
        implements View.OnClickListener {

    String TAG = "FriendsListAdapter";

    public List<FriendParser.FriendsListParser.Record> friendList;
    Context context;
    private SharePref sharePref;

    @Override
    public void onClick(View v) {
    }

    /**
     * ViewHolder for getting FindViewById
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView tvRowInviteFrndNAme;
        ImageView ivRowInviteFrndRound;
        RoundRectCornerImageView ivMarkerDialogCornerView;
        CircleImageView ivRowInviteFrndProfile;
        ImageView multiSelectView,iv_row_friend_list_remove_friend;
        LinearLayout LLMain;


        public MyViewHolder(View view) {
            super(view);
            tvRowInviteFrndNAme = (TextView) view.findViewById(R.id.tvRowInviteFrndNAme);
            ivRowInviteFrndRound = (ImageView) view.findViewById(R.id.ivRowInviteFrndRound);
            ivMarkerDialogCornerView = (RoundRectCornerImageView) view.findViewById(R.id.ivMarkerDialogCornerView);
            ivRowInviteFrndProfile = (CircleImageView) view.findViewById(R.id.ivRowInviteFrndProfile);
            multiSelectView = (ImageView) view.findViewById(R.id.multiSelectView);
            iv_row_friend_list_remove_friend = (ImageView) view.findViewById(R.id.iv_row_friend_list_remove_friend);
        }
    }

    /**
     * Constructor
     * @param context
     * @param friendList
     */
    public FriendsListAdapter(Context context, List<FriendParser.FriendsListParser.Record> friendList) {
        this.friendList = friendList;
        this.context = context;
        sharePref = new SharePref(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_friend_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FriendParser.FriendsListParser.Record model = friendList.get(position);

        holder.tvRowInviteFrndNAme.setText(model.getFirstName().toUpperCase() + " " + model.getLastName().toUpperCase());
        if (model.getProfiePic() != null &&
                model.getProfiePic().length() > 0) {
            Glide.with(context)
                    .load(model.getProfiePic())
                    .into(holder.ivRowInviteFrndProfile);
        } else {
            Glide.with(context)
                    .load(R.drawable.man)
                    .into(holder.ivRowInviteFrndProfile);
        }

        if (model.getCoverPic() != null &&
                model.getCoverPic().length() > 0) {

            Glide.with(context)
                    .load(model.getCoverPic())
                    .into(holder.ivMarkerDialogCornerView);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_cover_pic)
                    .into(holder.ivMarkerDialogCornerView);
        }

        holder.iv_row_friend_list_remove_friend.setColorFilter(Color.RED);
        holder.iv_row_friend_list_remove_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUtility.confirmYesNo(context,
                        stringBuilderUserInfo(model.getFirstName(),model.getLastName()),
                        new INTFConfirmYesNo() {
                    @Override
                    public void yesClick() {
                        callUnFriendAPI(model.getId(),position);
                    }
                });
            }
        });

        logDebug("Cover Image " + model.getCoverPic());
        logDebug("Cover Image " + model.getProfiePic());

    }


    @Override
    public int getItemCount() {
        return friendList.size();
    }

    /**
     * Call UnFriend APi
     */
    private void callUnFriendAPI(String id, final int position) {
        HashMap<String,String> param = new HashMap<>();
        param.put("user_id",sharePref.getUserId());
        param.put("friend_id",id);
        param.put("session_id",sharePref.getSessionId());
        param.put("platform","app");
        FriendsListService.unFriend(context, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Un Friend Response-->" + response);
                int status_code;
                String message;
                if (response != null && response.length() > 0) {
                    try {
                        status_code = response.getInt("status_code");
                        if (status_code == Constant.API_STATUS_ONE) {
                            message = response.getString("message");
                            DialogUtility.alertOk(context, message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    friendList.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Make Full String Using String Builder
     * @param fName
     * @param lName
     * @return
     */
    private String stringBuilderUserInfo(String fName,String lName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(context.getString(R.string.msg_un_friend));
        stringBuilder.append(" ");
        stringBuilder.append(fName);
        stringBuilder.append(" ");
        stringBuilder.append(lName);
        stringBuilder.append(" ");
        stringBuilder.append(context.getString(R.string.msg_as_friend));
        return  stringBuilder.toString();
    }

    /**
     * Method To Print Log
     * @param msg
     */
    private void logDebug(String msg) {
        Log.e(TAG, msg);
    }
}