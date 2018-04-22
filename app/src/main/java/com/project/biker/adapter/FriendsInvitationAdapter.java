package com.project.biker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.project.biker.R;
import com.project.biker.ResponseParser.FriendParser;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.service.APIService;
import com.project.biker.service.FriendsListService;
import com.project.biker.tools.Constant;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.CustomBoldTextView;
import com.project.biker.utils.DialogUtility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;

/**
 * Created by Vikas Patel on 7/13/2017.
 */

public class FriendsInvitationAdapter extends RecyclerView.Adapter<FriendsInvitationAdapter.MyViewHolder>
        implements View.OnClickListener {

    private static final String TAG = "FriendsInvitaAdapter";
    SharePref sharePref;
    public List<FriendParser.FriendPending.Record> friendList;
    Context context;
    TextView dataNotFound;

    @Override
    public void onClick(View v) {
    }

    /**
     * ViewHolder for getting FindViewById
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRideMessage;
        LinearLayout llAccept, llReject;
        CustomBoldTextView txtName;
        CircleImageView circleImageView;


        public MyViewHolder(View view) {
            super(view);
            llAccept = (LinearLayout) view.findViewById(R.id.llAccept);
            llReject = (LinearLayout) view.findViewById(R.id.llReject);
            txtName = (CustomBoldTextView)view.findViewById(R.id.txtName);
            circleImageView = (CircleImageView) view.findViewById(R.id.circleImageView);
        }
    }

    /**
     * Constructor
     *
     * @param context
     * @param friendList
     */
    public FriendsInvitationAdapter(Context context,
                                    List<FriendParser.FriendPending.Record> friendList,
                                    TextView dataNotFound) {
        this.friendList = friendList;
        this.context = context;
        this.dataNotFound = dataNotFound;
        sharePref = new SharePref(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_friend_invitationt, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final FriendParser.FriendPending.Record model = friendList.get(position);
        holder.txtName.setText(model.getFullName());

        logDebug("Profile Pic" + model.getProfilePic());

        if (model.getProfilePic().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(R.drawable.man)
                    .into(holder.circleImageView);

        } else {
            Glide.with(context)
                    .load(model.getProfilePic())
                    .into(holder.circleImageView);
        }




        holder.llAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(context)) {
                    callFriendAcceptAPI(model.getId(), position);
                }else {
                    Toast.makeText(context,context.getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.llReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(context)) {
                    callFriendRejectAPI(model.getId(), position);
                }else {
                    Toast.makeText(context,context.getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    @Override
    public int getItemCount() {
        return friendList.size();
    }

    /**
     * Call Friend Request API
     */
    private void callFriendAcceptAPI(String id, final int position) {
        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("friend_id", id);
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");
        FriendsListService.friendAccept(context, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
               logDebug("Friend Accept Response--->" + response.toString());
                int status;
                String message;
                if (response != null && response.length() > 0) {
                    try {
                        status = response.optInt("status_code");
                        message = response.getString("message");
                        if (status == Constant.API_STATUS_ONE) {
                            DialogUtility.alertOk(context, message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    //relMain.setVisibility(View.GONE);
                                    friendList.remove(position);
                                    notifyDataSetChanged();
                                    showLabelNotDataFound();

                                }
                            });
                        }
                    } catch (JSONException e) {
                        logDebug("<<<<< Friend Accept Api Response Exception>>>>>>");
                        e.printStackTrace();
                    }
                }

            }
        });
    }


    /**
     * Call Friend Reject API
     */
    private void callFriendRejectAPI(String id, final int position) {

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", sharePref.getUserId());
        param.put("friend_id", id);
        param.put("session_id", sharePref.getSessionId());
        param.put("platform", "app");

        FriendsListService.friendReject(context, param, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
              logDebug("Friend Reject Response--->" + response.toString());
                int status;
                String message;
                if (response != null && response.length() > 0) {
                    try {
                        status = response.getInt("status_code");
                        message = response.getString("message");
                        if (status == Constant.API_STATUS_ONE) {
                            DialogUtility.alertOk(context, message, new INTFAlertOk() {
                                @Override
                                public void alertOk() {
                                    //relMain.setVisibility(View.GONE);
                                    friendList.remove(position);
                                    notifyDataSetChanged();
                                    showLabelNotDataFound();

                                }
                            });
                        }
                    } catch (JSONException e) {
                        logDebug("<<<<< Friend Reject Api Response Exception>>>>>>");
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * Show label not data found  When Recycle View Have Not Data
     */
    private void showLabelNotDataFound() {
        if (friendList.size() == 0) {
            dataNotFound.setVisibility(View.VISIBLE);
        } else {
            dataNotFound.setVisibility(View.GONE);
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

}