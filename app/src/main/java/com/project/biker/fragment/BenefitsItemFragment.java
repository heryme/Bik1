package com.project.biker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.project.biker.R;
import com.project.biker.activity.MainActivity;
import com.project.biker.utils.CustomBoldTextView;
import com.project.biker.utils.CustomTextView;
import com.project.biker.utils.RoundRectCornerImageView;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.project.biker.service.APIService.BASE_URL;
import static com.project.biker.tools.Constant.SHOW_BENEFIT_LIST;

/**
 * Created by Vikas Patel on 7/21/2017.
 */

public class BenefitsItemFragment extends Fragment {


    RoundRectCornerImageView ivBenefitItemCover;
    CircleImageView ivBenefitStoreLogo;
    CustomBoldTextView tvStoreName;
    CustomTextView tvBenefitAddress;
    ImageView ivBack;
    WebView webview;


    String benefitName;
    String benefitDescriptionUrl;
    String storeAddress;
    String storeLogoUrl;
    String benefitId;

    String URL = BASE_URL + "/webview/displayWebView/";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_benefits_items, null);

        ((MainActivity) getContext()).textChange(new BenefitsFragment());

        getBundleData();

        initializeView(view);

        setData();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SHOW_BENEFIT_LIST = "1";
                ((MainActivity) getContext()).loadFragment(new BenefitsFragment(), false);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new BenefitsFragment());
    }

    /**
     * Getting Ids from xml component
     *
     * @param view
     */
    private void initializeView(View view) {

        ivBenefitItemCover = (RoundRectCornerImageView) view.findViewById(R.id.ivBenefitItemCover);
        ivBenefitStoreLogo = (CircleImageView) view.findViewById(R.id.ivBenefitStorLogo);
        tvStoreName = (CustomBoldTextView) view.findViewById(R.id.tvStoreName);
        tvBenefitAddress = (CustomTextView) view.findViewById(R.id.tvBenefitAddress);
        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        webview = (WebView) view.findViewById(R.id.webview);
    }

    /**
     * Read Data Coming From other fragment
     */
    private void getBundleData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            benefitDescriptionUrl = bundle.getString("CoverUrl", "");
            benefitName = bundle.getString("BenefitName", "");
            storeAddress = bundle.getString("StoreAddress", "");
            storeLogoUrl = bundle.getString("StoreLogo", "");
            benefitId = bundle.getString("BenefitId", "");

            URL = URL + benefitId;
        }
    }

    /**
     * setData Coming From other Fragment
     */
    private void setData() {


        if (benefitDescriptionUrl.equalsIgnoreCase("")) {
            Glide.with(getActivity())
                    .load(R.drawable.ic_cover_pic)
                    .into(ivBenefitItemCover);
        } else {
            Glide.with(getActivity())
                    .load(benefitDescriptionUrl)
                    .into(ivBenefitItemCover);
        }

        if (storeLogoUrl.equalsIgnoreCase("")) {
            Glide.with(getActivity())
                    .load(R.drawable.man)
                    .into(ivBenefitStoreLogo);
        } else {
            Glide.with(getActivity())
                    .load(storeLogoUrl)
                    .into(ivBenefitStoreLogo);
        }


        tvStoreName.setText(benefitName);
        tvBenefitAddress.setText(storeAddress);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(URL);

    }

    /**
     * Method Return Fragment FullName
     * this is for the tab text color change
     * according to selection
     *
     * @return
     */
    public static String getFragmentName() {
        return BenefitsItemFragment.class.getName();
    }

}
