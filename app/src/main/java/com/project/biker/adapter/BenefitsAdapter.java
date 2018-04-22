package com.project.biker.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.project.biker.R;
import com.project.biker.ResponseParser.BenefitParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.fragment.BenefitsItemFragment;
import com.project.biker.model.ModelBenefitList;
import com.project.biker.utils.CustomBoldTextView;
import com.project.biker.utils.RoundRectCornerImageView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Rahul Padaliya on 7/20/2017.
 */
public class BenefitsAdapter extends RecyclerView.Adapter<BenefitsAdapter.MyViewHolder> {

    private static final String TAG = "BenefitsAdapter";
    private List<ModelBenefitList> benefitList;
    private Context context;
    /**
     * ViewHolder for findViewById
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {

        RoundRectCornerImageView ivBenefitCover;
        RelativeLayout rrFrgBenefitDetail;
        CircleImageView ivBenefitStoreLogo;
        CustomBoldTextView tvStoreName;

        public MyViewHolder(View view) {
            super(view);
            ivBenefitCover = (RoundRectCornerImageView) view.findViewById(R.id.ivBenefitCover);
            rrFrgBenefitDetail = (RelativeLayout) view.findViewById(R.id.rrFrgBenefitDetail);
            ivBenefitStoreLogo = (CircleImageView) view.findViewById(R.id.ivBenefitStorLogo);
            tvStoreName = (CustomBoldTextView) view.findViewById(R.id.tvStoreName);
        }
    }

    /**
     * Adapter Constructor
     * @param context
     * @param benefitList
     */

    public BenefitsAdapter(Context context, List<ModelBenefitList> benefitList) {
        this.benefitList = benefitList;
        this.context = context;
        logDebug("benefitList Size--->" + benefitList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_benefits_slider_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ModelBenefitList modelBenefitList = benefitList.get(position);
        logDebug("CoverUrl-->" + modelBenefitList.getBenefitCoverUrl());

        if (modelBenefitList.getBenefitCoverUrl().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(R.drawable.ic_cover_pic)
                    .into(holder.ivBenefitCover);
        } else {
            Glide.with(context)
                    .load(modelBenefitList.getBenefitCoverUrl())
                    .into(holder.ivBenefitCover);
        }

        if (modelBenefitList.getBenefitLogoUrl().equalsIgnoreCase("")) {
            Glide.with(context)
                    .load(R.drawable.man)
                    .into(holder.ivBenefitStoreLogo);
        } else {
            Glide.with(context)
                    .load(modelBenefitList.getBenefitLogoUrl())
                    .into(holder.ivBenefitStoreLogo);

        }


        holder.tvStoreName.setText(modelBenefitList.getBenefitName());

        holder.rrFrgBenefitDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ModelBenefitList modelBenefitList1 = BenefitParser.benefitLists.get(position);
                BenefitsItemFragment benefitsItemFragment = new BenefitsItemFragment();
                Bundle bundle = new Bundle();
                bundle.putString("BenefitName", modelBenefitList1.getBenefitName());
                bundle.putString("CoverUrl", modelBenefitList1.getBenefitCoverUrl());
                bundle.putString("StoreAddress", modelBenefitList1.getBenefitFormattedAddress());
                bundle.putString("StoreLogo", modelBenefitList1.getBenefitLogoUrl());
                bundle.putString("BenefitId",modelBenefitList1.getBenefitId());
                benefitsItemFragment.setArguments(bundle);
                ((MainActivity) context).loadFragment(benefitsItemFragment, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return benefitList.size();
    }

    /**
     * Method to Print Log
     * @param msg
     */
    private void logDebug(String msg) {
        Log.d(TAG, msg);
    }

}
