package com.project.biker.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.project.biker.R;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.service.APIService;
import com.project.biker.service.UserService;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.BarCodeQRCode;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.ImageUtility;

import org.json.JSONObject;

import static com.project.biker.tools.Constant.API_STATUS_ONE;
import static com.project.biker.utils.NetworkUtility.isNetworkAvailable;


/**
 * Created by Rahul Padaliya on 7/7/2017.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ProfileFragment";

    public static String PROFILE_PIC = "profile_pic";
    public static String COVER_PIC = "cover_pic";
    public static String FIRST_NAME = "firstname";
    public static String LAST_NAME = "lastname";
    public static String EMAIL = "email";
    public static String CITY = "city";
    public static String STATE = "state";
    public static String COUNTRY = "country";
    public static String GENDER = "gender";

    /**
     * ImageView
     */
    ImageView ivRowInviteFrndProfile, ivMarkerDialogCornerView,
            ivEdit, ivProfileFrgBarCode, ivProfileFrgQrCode;

    /**
     * TextView
     */
    TextView tvEditProfile, tvProfileName, tvProfileFrgBarCode, tvProfileFrgQRCode;
    TextView tvTotalFriend;

    /**
     * LinearLayout
     */
    LinearLayout llEdit, ll_user;

    ProgressBar progress;

    SharePref sharePref;
    UserParser.UserProfileParser userProfileParser;
    Bitmap barBitmap, qrBitMap;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_profile, null);

        sharePref = new SharePref(getContext());

        //Initialization View
        initializeView(view);
        //Initialize Listener
        initListener();

        // initialize the QRcode and BARcode
        initializeQrCodeAndBarCode();

        // call the user profile api
        userProfileApiCall();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvEditProfile:
            case R.id.ivEdit:
            case R.id.ivRowInviteFrndProfile:
                passBundleDataToEditProfile();
                break;
            case R.id.llEdit:
                passBundleDataToEditProfile();
                break;
            case R.id.tvProfileFrgQRCode:
                DialogUtility.dialogQrBarCode(getContext(), qrBitMap);
                break;
            case R.id.tvProfileFrgBarCode:
                DialogUtility.dialogQrBarCode(getContext(), barBitmap);
                break;
            case R.id.ll_user:
                ((MainActivity) getContext()).loadFragment(new FriendsFragment(), false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getContext()).textChange(new ProfileFragment());
    }

    /**
     * Initialization View
     *
     * @param view
     */
    private void initializeView(View view) {
        ivRowInviteFrndProfile = (ImageView) view.findViewById(R.id.ivRowInviteFrndProfile);
        ivMarkerDialogCornerView = (ImageView) view.findViewById(R.id.ivMarkerDialogCornerView);
        tvEditProfile = (TextView) view.findViewById(R.id.tvEditProfile);
        tvProfileName = (TextView) view.findViewById(R.id.tvProfileName);
        llEdit = (LinearLayout) view.findViewById(R.id.llEdit);
        ll_user = (LinearLayout) view.findViewById(R.id.ll_user);
        ivEdit = (ImageView) view.findViewById(R.id.ivEdit);
        ivProfileFrgBarCode = (ImageView) view.findViewById(R.id.ivProfileFrgBarCode);
        ivProfileFrgQrCode = (ImageView) view.findViewById(R.id.ivProfileFrgQrCode);
        tvProfileFrgBarCode = (TextView) view.findViewById(R.id.tvProfileFrgBarCode);
        tvProfileFrgQRCode = (TextView) view.findViewById(R.id.tvProfileFrgQRCode);
        tvTotalFriend = (TextView) view.findViewById(R.id.tvTotalFriend);
        progress = (ProgressBar) view.findViewById(R.id.progress);


    }

    /**
     * Register Click Listener
     */
    private void initListener() {
        ivRowInviteFrndProfile.setOnClickListener(this);
        tvEditProfile.setOnClickListener(this);
        llEdit.setOnClickListener(this);
        ll_user.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        tvProfileFrgQRCode.setOnClickListener(this);
        tvProfileFrgBarCode.setOnClickListener(this);
    }

    /**
     * Call Get User Profile Info API
     */
    private void getUserProfileInfoAPI() {
        UserService.getUserProfileInfo(getContext(),
                sharePref.getUserId(),
                sharePref.getSessionId(), new APIService.Success<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {

                        logDebug("Get User Profile Data---> " + response.toString());

                        userProfileParser = UserParser.UserProfileParser.parseGetProfileResponse(response);

                        logDebug("UserProfile status" + userProfileParser.getStatusCode());

                        if (userProfileParser != null && userProfileParser.getStatusCode() == API_STATUS_ONE) {
                            tvProfileName.setText(userProfileParser.getFirstName() + " " + userProfileParser.getLastName());
                            userProfileParser.getFirstName();
                            userProfileParser.getLastName();
                            userProfileParser.getEmail();
                            userProfileParser.getCity();
                            userProfileParser.getState();
                            userProfileParser.getCountry();
                            tvTotalFriend.setText(userProfileParser.getTotalFriends().toString());
                            if (userProfileParser.getGender().equalsIgnoreCase("male")) {
                                //  rb_edit_profile_male.setChecked(true);
                            } else if (userProfileParser.getGender().equalsIgnoreCase("female")) {
                                //rb_edit_profile_female.setChecked(true);
                            }

                            if (userProfileParser.getProfilePic() != null && userProfileParser.getProfilePic().length() > 0) {
                                Glide.with(getActivity())
                                        .load(userProfileParser.getProfilePic())
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(ivRowInviteFrndProfile);

                            } else {
                                Glide.with(getActivity())
                                        .load(R.drawable.man)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(ivRowInviteFrndProfile);
                            }

                            if (userProfileParser.getCoverPic() != null &&
                                    userProfileParser.getCoverPic().length() > 0) {

                               /* Glide.with(getActivity())
                                        .load(userProfileParser.getCoverPic())
                                        .placeholder(R.drawable.ic_cover_pic)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(ivMarkerDialogCornerView);*/
                                ImageUtility.loadImagesWithGlide(getContext(),
                                        userProfileParser.getCoverPic(),
                                        progress,
                                        ivMarkerDialogCornerView,
                                        null);

                            } else {
                                progress.setVisibility(View.GONE);
                                Glide.with(getActivity())
                                        .load(R.drawable.ic_cover_pic)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .skipMemoryCache(true)
                                        .into(ivMarkerDialogCornerView);
                            }

                        }
                    }
                });
    }

    /**
     * Pass Bundle Data To The EditProfile Fragment
     */
    private void passBundleDataToEditProfile() {
        Bundle bundle = new Bundle();
        if (userProfileParser != null) {
            bundle.putString(PROFILE_PIC, userProfileParser.getProfilePic());
            bundle.putString(COVER_PIC, userProfileParser.getCoverPic());
            bundle.putString(FIRST_NAME, userProfileParser.getFirstName());
            bundle.putString(LAST_NAME, userProfileParser.getLastName());
            bundle.putString(EMAIL, userProfileParser.getEmail());
            bundle.putString(CITY, userProfileParser.getCity());
            bundle.putString(STATE, userProfileParser.getState());
            bundle.putString(COUNTRY, userProfileParser.getCountry());
            bundle.putString(GENDER, userProfileParser.getGender());
            EditProfileFragment editProfileFragment = new EditProfileFragment();
            editProfileFragment.setArguments(bundle);
            ((MainActivity) getContext()).loadFragment(editProfileFragment, false);
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Intialize the Qrcode and Barcode
     */
    private void initializeQrCodeAndBarCode() {
        qrBitMap = BarCodeQRCode.generateQRCode("verified");
        barBitmap = BarCodeQRCode.generateBarCode("verified");

    }

    /**
     * Call user profile api
     */
    private void userProfileApiCall() {
        if (isNetworkAvailable(getActivity())) {
            progress.setVisibility(View.VISIBLE);
            getUserProfileInfoAPI();
        } else {
            Toast.makeText(getActivity(), getString(R.string.internet_message), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Method to Print Log
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
     *
     * @return
     */
    public static String getFragmentName() {
        return ProfileFragment.class.getName();
    }
}
