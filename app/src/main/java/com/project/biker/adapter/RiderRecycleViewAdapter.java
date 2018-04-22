package com.project.biker.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.project.biker.R;
import com.project.biker.model.ModelBikerList;
import com.project.biker.utils.RoundRectCornerImageView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vikas Patel on 7/13/2017.
 */

public class RiderRecycleViewAdapter extends RecyclerView.Adapter<RiderRecycleViewAdapter.MyViewHolder>
        implements View.OnClickListener {

    private static final String TAG = "RiderRecycleViewAdapter";
    public ArrayList<ModelBikerList> bikerLst;
    public static ArrayList<String> selectedBikerLst = new ArrayList<>();

    Context context;

    ImageView ivNext;
    boolean flag = false;

    Boolean glide_status = false;

    @Override
    public void onClick(View v) {
    }

    /**
     * ViewHolder for getting FindViewById
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvRowInviteFriendNAme;
        ImageView ivRowInviteFriendRound;
        RoundRectCornerImageView ivMarkerDialogCornerView;
        CircleImageView ivRowInviteFriendProfile;
        RelativeLayout rrFrgProfile;
        ImageView multiSelectView;


        public MyViewHolder(View view) {
            super(view);
            tvRowInviteFriendNAme = (TextView) view.findViewById(R.id.tvRowInviteFrndNAme);
            ivRowInviteFriendRound = (ImageView) view.findViewById(R.id.ivRowInviteFrndRound);
            ivMarkerDialogCornerView = (RoundRectCornerImageView) view.findViewById(R.id.ivMarkerDialogCornerView);
            ivRowInviteFriendProfile = (CircleImageView) view.findViewById(R.id.ivRowInviteFrndProfile);
            rrFrgProfile = (RelativeLayout) view.findViewById(R.id.rrFrgProfile);
            multiSelectView = (ImageView) view.findViewById(R.id.multiSelectView);
        }
    }

    /**
     * Constructor
     *
     * @param context
     * @param bikerLst
     * @param ivNext
     */
    public RiderRecycleViewAdapter(Context context, ArrayList<ModelBikerList> bikerLst, ImageView ivNext, Boolean glide_status) {
        this.bikerLst = bikerLst;
        this.context = context;
        this.ivNext = ivNext;
        this.glide_status = glide_status;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.row_ride_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final ModelBikerList model = bikerLst.get(position);
        holder.tvRowInviteFriendNAme.setText(model.getFirstName().toUpperCase() + " " + model.getLastName().toUpperCase());

        logDebug("Cover Image" + model.getCoverPic());
        logDebug("Profile Image" + model.getProfilePic());

        if (model.getCoverPic().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(R.drawable.ic_cover_pic)
                    .into(holder.ivMarkerDialogCornerView);

        } else {
            Glide.with(context)
                    .load(model.getCoverPic())
                    .into(holder.ivMarkerDialogCornerView);
        }

        if (model.getProfilePic().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(R.drawable.man)
                    .into(holder.ivRowInviteFriendProfile);
        } else {
            Glide.with(context)
                    .load(model.getProfilePic())
                    .into(holder.ivRowInviteFriendProfile);
        }

        if (model.getIsFriend() == 1) {
            holder.ivRowInviteFriendRound.setBackgroundResource(R.drawable.green_selec);
        } else {
            holder.ivRowInviteFriendRound.setBackgroundResource(R.drawable.red_select);
        }

        if (model.getIsSelected()) {
            holder.multiSelectView.setVisibility(View.VISIBLE);
        } else {
            holder.multiSelectView.setVisibility(View.GONE);
        }

        holder.rrFrgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivNext.setVisibility(View.VISIBLE);
                if (model.getIsSelected()) {
                    model.setIsSelected(false);
                    glide_status = false;
                    notifyDataSetChanged();
                } else {
                    model.setIsSelected(true);
                    glide_status = false;
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return bikerLst.size();
    }

    /**
     * Method to Print Log
     *
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }
}