package com.project.biker.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropUtil;
import com.project.biker.R;
import com.project.biker.ResponseParser.UserParser;
import com.project.biker.activity.MainActivity;
import com.project.biker.google_place_api.GooglePlaceApi;
import com.project.biker.google_place_api.GooglePlaceParser;
import com.project.biker.google_place_api.PlacesAutoCompleteAdapter;
import com.project.biker.model.INTFAlertOk;
import com.project.biker.service.APIService;
import com.project.biker.service.UserService;
import com.project.biker.tools.SharePref;
import com.project.biker.utils.DialogUtility;
import com.project.biker.utils.ImageUtility;
import com.project.biker.utils.MarshMallowPermissionUtility;
import com.project.biker.utils.ValidationUtility;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getCacheDir;
import static com.soundcloud.android.crop.Crop.fileUri;
import static com.project.biker.tools.Constant.API_STATUS_ONE;


/**
 * Created by Rahul Padaliya on 7/7/2017.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "EditProfileFragment";

    /***
     * ImageView
     */
    private ImageView ivRowInviteFriendProfile, ivMarkerDialogCornerView, ivEditProfileEditCover, ivEditProfilePic;

    /**
     * Button
     */
    Button btnEditProfileSubmit;

    /***
     * Edit text
     */
    EditText etEditProfileFirstName, etEditProfileLastName,
            etEditProfileEmail;
           /* etEditProfileState, etEditProfileCountry*/;

    /**
     * AutoCompleteTextView
     */
    AutoCompleteTextView etEditProfileCity;

    /**
     * TextView
     */
    TextView tvEditProfileFrgName;

    /**
     * Radio Button
     */
    RadioButton rb_edit_profile_male, rb_edit_profile_female;

    ProgressBar progress;

    SharePref sharePref;
    UserParser.UserProfileParser userProfileParser;

    //Google Place APi Parser
    private GooglePlaceParser googlePlaceParser;

    private String profilePic, coverPic, firstName, lastName, email, city, country, state, gender;

    //Choose Photos
    public static int REQUEST_CAMERA_PROFILE_PIC = 400, REQUEST_CAMERA_COVER_PIC = 300, SELECT_FILE = 100, SELECT_COVER_PIC = 200;
    private String userChoosenTask;
    private boolean isNewImageSelect = false;
    private File file, fileCoverPic;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_profile, null);
        ((MainActivity) getContext()).textChange(new ProfileFragment());

        initializeView(view);
        initListener();
        sharePref = new SharePref(getContext());

        //Get Profile Data
        getEditProfileBundleData();
        callGooglePlaceAPI(getContext(), etEditProfileCity);

        //user cannot able to edit email id
        etEditProfileEmail.setEnabled(false);
        etEditProfileEmail.setClickable(false);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        logDebug("requestCode-->" + requestCode);
        logDebug("resultCode-->" + resultCode);


        logDebug("Call On Activity Result---->");
        if (resultCode == RESULT_OK) {

            //Choose Profile Picture From The Gallery
            if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                isNewImageSelect = true;
                if (ImageUtility.getPath(getActivity(), selectedImageUri) != null
                        && !ImageUtility.getPath(getActivity(), selectedImageUri).endsWith(".mp4")) {

                    file = new File(ImageUtility.getPath(getActivity(), selectedImageUri));
                    logDebug("Image Path---->" + ImageUtility.getPath(getActivity(), selectedImageUri));
                } else {
                    DialogUtility.dialogWithPositiveButton(getString(R.string.dialog_some_thing_wrong), getContext());
                }

                if (selectedImageUri == null && data.getExtras() != null) {
                    selectedImageUri = ImageUtility.getImageUri(getActivity(), (Bitmap) data.getExtras().get("data"));
                }
                logDebug("if-->" + selectedImageUri);
                if (ImageUtility.getPath(getActivity(), selectedImageUri) != null) {
                    logDebug("selectedImageUriRRR-->" + selectedImageUri);
                    /*Glide.with(getActivity()).load(selectedImageUri)
                            .error(R.mipmap.ic_launcher)
                            .into(ivRowInviteFriendProfile);*/
                    ivRowInviteFriendProfile.setImageURI(selectedImageUri);
                }

            } else if (requestCode == REQUEST_CAMERA_PROFILE_PIC) {

                //Take Profile Picture From The Camera
                Uri photosUri = data.getData();
                if (photosUri == null && data.getExtras() != null) {
                    photosUri = ImageUtility.getImageUri(getActivity(), (Bitmap) data.getExtras().get("data"));/*AndroidUtility.getLastCapturedImg(getContext());*/
                }

                Glide.with(getActivity()).load(photosUri)
                        .error(R.mipmap.ic_launcher)
                        .into(ivRowInviteFriendProfile);

                isNewImageSelect = true;
                file = new File(ImageUtility.getRealPathFromURI(getContext(), photosUri));
                logDebug("Camera Photo Path---->" + file);

            } else if (requestCode == REQUEST_CAMERA_COVER_PIC) {
                //Choose Cover Picture From The Gallery
              /*  Uri photosUri = data.getData();
                logDebug("photosUri--------->" + photosUri);

                if (photosUri == null && data.getExtras() != null) {
                    photosUri = ImageUtility.getImageUri(getActivity(), (Bitmap) data.getExtras().get("data"));*//*AndroidUtility.getLastCapturedImg(getContext());*//*
                }*/


                if (requestCode == Crop.REQUEST_CAMERA_COVER_PIC && resultCode == RESULT_OK) {
                    beginCrop(fileUri);
                }

           /* Glide.with(getActivity()).load(photosUri)
                        .error(R.mipmap.ic_launcher)
                        .into(ivMarkerDialogCornerView);*/

               /* isNewImageSelect = true;
                fileCoverPic = new File(ImageUtility.getRealPathFromURI(getContext(), photosUri));
                logDebug("Camera Cover Photo Path---->" + file);*/

            } else if (requestCode == SELECT_COVER_PIC) {
                //Take Profile Gallery From The Camera

                Uri selectedImageUri = data.getData();

                //Here Crop Image
                if (requestCode == Crop.SELECT_COVER_PIC && resultCode == RESULT_OK) {
                    beginCrop(selectedImageUri);
                }

                //Here Get Path Of The Images From The URI
                /*isNewImageSelect = true;
                if (ImageUtility.getPath(getActivity(), selectedImageUri) != null
                        && !ImageUtility.getPath(getActivity(), selectedImageUri).endsWith(".mp4")) {

                    fileCoverPic = new File(ImageUtility.getPath(getActivity(), selectedImageUri));
                    logDebug("Image Cover Photo From Gallery Path---->" + ImageUtility.getPath(getActivity(), selectedImageUri));
                } else {
                    DialogUtility.dialogWithPositiveButton(getString(R.string.dialog_some_thing_wrong), getContext());
                }

                if (selectedImageUri == null && data.getExtras() != null) {
                    selectedImageUri = ImageUtility.getImageUri(getActivity(), (Bitmap) data.getExtras().get("data"));
                }*/

                //Set Image To ImageView
               /* if (ImageUtility.getPath(getActivity(), selectedImageUri) != null) {
                    logDebug("Log1  selectedImageUri-->" + selectedImageUri);
                    Glide.with(getActivity())
                            .load(selectedImageUri)
                            .error(R.mipmap.ic_launcher)
                            .into(ivMarkerDialogCornerView);
                }*/
            } else if (requestCode == Crop.REQUEST_CROP) {
                handleCrop(resultCode, data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivEditProfilePic:
                if (!MarshMallowPermissionUtility.checkPermissionForCamera(getActivity())) {
                    MarshMallowPermissionUtility.requestPermissionForCamera(getActivity());
                } else if (!MarshMallowPermissionUtility.checkPermissionForExternalStorage(getActivity())) {
                    MarshMallowPermissionUtility.requestPermissionForExternalStorage(getActivity());
                } else if (!MarshMallowPermissionUtility.checkPermissionForReadExternalStorage(getActivity())) {
                    MarshMallowPermissionUtility.requestPermissionForReadExternalStorage(getActivity());
                } else {
                    selectImage(true);
                }
                break;
            case R.id.btnEditProfileSubmit:
                checkValidation();
                break;
            case R.id.ivEditProfileEditCover:
                if (!MarshMallowPermissionUtility.checkPermissionForCamera(getActivity())) {
                    MarshMallowPermissionUtility.requestPermissionForCamera(getActivity());
                } else if (!MarshMallowPermissionUtility.checkPermissionForExternalStorage(getActivity())) {
                    MarshMallowPermissionUtility.requestPermissionForExternalStorage(getActivity());
                } else if (!MarshMallowPermissionUtility.checkPermissionForReadExternalStorage(getActivity())) {
                    MarshMallowPermissionUtility.requestPermissionForReadExternalStorage(getActivity());
                } else {
                    selectImage(false);
                }
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
        ivRowInviteFriendProfile = (ImageView) view.findViewById(R.id.ivEditProfile);
        btnEditProfileSubmit = (Button) view.findViewById(R.id.btnEditProfileSubmit);
        etEditProfileFirstName = (EditText) view.findViewById(R.id.etEditProfileFname);
        etEditProfileLastName = (EditText) view.findViewById(R.id.etEditProfileLname);
        etEditProfileEmail = (EditText) view.findViewById(R.id.etEditProfileEmail);
        etEditProfileCity = (AutoCompleteTextView) view.findViewById(R.id.etEditProfileCity);
       /* etEditProfileState = (EditText) view.findViewById(R.id.etEditProfileState);
        etEditProfileCountry = (EditText) view.findViewById(R.id.etEditProfileCountry);*/
        ivMarkerDialogCornerView = (ImageView) view.findViewById(R.id.ivMarkerDialogCornerView);
        ivEditProfileEditCover = (ImageView) view.findViewById(R.id.ivEditProfileEditCover);
        ivEditProfilePic = (ImageView) view.findViewById(R.id.ivEditProfilePic);
        rb_edit_profile_male = (RadioButton) view.findViewById(R.id.rb_edit_profile_male);
        rb_edit_profile_female = (RadioButton) view.findViewById(R.id.rb_edit_profile_female);
        tvEditProfileFrgName = (TextView) view.findViewById(R.id.tvEditProfileFrgName);
        progress = (ProgressBar) view.findViewById(R.id.progress);
    }

    /**
     * Register Click Listener
     */
    private void initListener() {
        ivRowInviteFriendProfile.setOnClickListener(this);
        btnEditProfileSubmit.setOnClickListener(this);
        ivEditProfileEditCover.setOnClickListener(this);
        ivEditProfilePic.setOnClickListener(this);
    }

    /**
     * Dialog For Image Selection
     */
    private void selectImage(final Boolean isProfile) {
        final CharSequence[] items = {getString(R.string.lbl_take_photo), getString(R.string.lbl_choose_from_gallery),
                getString(R.string.lbl_cancel)};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.addPhoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(getString(R.string.lbl_take_photo))) {
                    userChoosenTask = getString(R.string.takePhoto);
                    cameraIntent(isProfile);

                } else if (items[item].equals(getString(R.string.lbl_choose_from_gallery))) {
                    userChoosenTask = getString(R.string.selectFromGallery);
                    galleryIntent(isProfile);
                } else if (items[item].equals(getString(R.string.lbl_cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * Start Camera
     */
    private void cameraIntent(Boolean isProfile) {
        if (isProfile) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getActivity().startActivityForResult(intent, REQUEST_CAMERA_PROFILE_PIC);
        } else {
           /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            getActivity().startActivityForResult(intent, REQUEST_CAMERA_COVER_PIC);*/
            Crop.pickImage(getActivity(), true);
        }
    }

    /**
     * Select Image From The Gallery
     */
    private void galleryIntent(Boolean isProfile) {
        if (isProfile) {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getActivity().startActivityForResult(i, SELECT_FILE);

        } else {
            /*Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            getActivity().startActivityForResult(i, SELECT_COVER_PIC);*/
            Crop.pickImage(getActivity(), false);
        }
    }

    /**
     * Update User Profile Data
     */
    private void updateUserProfileInfo() {
        String city = "";
        String state = "";
        String country = "";
        if (etEditProfileCity.getText().toString() != null &&
                etEditProfileCity.getText().toString().length() > 0) {
            String[] parts = etEditProfileCity.getText().toString().split("\\,");
            for (int i = 0; i < parts.length; i++) {
                if (i == 0) {
                    city = parts[i];
                } else if (i == 1) {
                    state = parts[i];
                } else if (i == 2) {
                    country = parts[i];
                }
            }
        }

        HashMap<String, Object> params = new HashMap<>();
        params.put("user_id", sharePref.getUserId());
        params.put("session_id", sharePref.getSessionId());
        params.put("firstname", etEditProfileFirstName.getText().toString());
        params.put("lastname", etEditProfileLastName.getText().toString());
        params.put("email", etEditProfileEmail.getText().toString());
        params.put("city", city/* etEditProfileCity.getText().toString()*/);
        params.put("state", state/* etEditProfileState.getText().toString()*/);
        params.put("country", country /*etEditProfileCountry.getText().toString()*/);

        if (rb_edit_profile_male.isChecked()) {
            params.put("gender", "male");
        } else if (rb_edit_profile_female.isChecked()) {
            params.put("gender", "female");
        }

        params.put("profile_pic", file);
        params.put("cover_pic", fileCoverPic);
        params.put("platform", "app");
        UserService.updateUserProfileInfo(getContext(), params, new APIService.Success<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                logDebug("Get User Profile Data---> " + response.toString());
                int status = response.optInt("status_code");
                String message = response.optString("message");

                if (status == API_STATUS_ONE) {
                    DialogUtility.alertOk(getActivity(), message, new INTFAlertOk() {
                        @Override
                        public void alertOk() {
                            ((MainActivity) getActivity()).loadFragment(new ProfileFragment(), false);
                            CropUtil.deleteImage();
                        }
                    });
                }
            }
        });
    }

    /**
     * Check Validation Of The Edit Text
     */
    private void checkValidation() {
        if (ValidationUtility.isValidEditText(etEditProfileFirstName, getString(R.string.firstname_validation)) &&
                ValidationUtility.isValidEditText(etEditProfileLastName, getString(R.string.lastname_validation)) &&
                ValidationUtility.isValidEditText(etEditProfileEmail, getString(R.string.lastname_validation)) &&
                ValidationUtility.isValidEmail(getContext(), etEditProfileEmail) &&
                ValidationUtility.isValidEmail(getContext(), etEditProfileEmail) &&
                ValidationUtility.isValidEditText(etEditProfileCity, getString(R.string.city_validation))/* &&
                ValidationUtility.isValidEditText(etEditProfileState, getString(R.string.state_validation)) &&
                ValidationUtility.isValidEditText(etEditProfileCountry, getString(R.string.country_validation))*/) {
            updateUserProfileInfo();
        }

    }

    /**
     * Get Bundle Data From The Profile Fragment
     */
    private void getEditProfileBundleData() {
        progress.setVisibility(View.VISIBLE);
        profilePic = getArguments().getString(ProfileFragment.PROFILE_PIC);
        coverPic = getArguments().getString(ProfileFragment.COVER_PIC);
        firstName = getArguments().getString(ProfileFragment.FIRST_NAME);
        lastName = getArguments().getString(ProfileFragment.LAST_NAME);
        city = getArguments().getString(ProfileFragment.CITY);
        country = getArguments().getString(ProfileFragment.COUNTRY);
        state = getArguments().getString(ProfileFragment.STATE);
        gender = getArguments().getString(ProfileFragment.GENDER);
        email = getArguments().getString(ProfileFragment.EMAIL);
        tvEditProfileFrgName.setText(firstName + " " + lastName);
        etEditProfileFirstName.setText(firstName);
        etEditProfileLastName.setText(lastName);
        etEditProfileEmail.setText(email);
        etEditProfileCity.setText(city);
      /*  etEditProfileState.setText(state);
        etEditProfileCountry.setText(country);*/
        if (gender.equalsIgnoreCase("male")) {
            rb_edit_profile_male.setChecked(true);
        } else if (gender.equalsIgnoreCase("female")) {
            rb_edit_profile_female.setChecked(true);
        }

        if (profilePic != null &&
                profilePic.length() > 0) {
            Glide.with(getActivity())
                    .load(profilePic)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivRowInviteFriendProfile);
        }

        if (coverPic != null &&
                coverPic.length() > 0) {
           /* Glide.with(getActivity())
                    .load(coverPic)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivMarkerDialogCornerView);*/
            ImageUtility.loadImagesWithGlide(getContext(),
                    coverPic,
                    progress,
                    ivMarkerDialogCornerView,
                    ivEditProfileEditCover);
        }else {
            progress.setVisibility(View.GONE);
            ivEditProfileEditCover.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(R.drawable.ic_cover_pic)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(ivMarkerDialogCornerView);

        }
    }

    /**
     * City,State and Country Change dialog
     */
    private void callGooglePlaceAPI(final Context context, final AutoCompleteTextView act_dialogChangeCityStateCountry) {


        act_dialogChangeCityStateCountry.setAdapter(new PlacesAutoCompleteAdapter(context,
                R.layout.row_google_autocomplete_text, true));
        act_dialogChangeCityStateCountry.setCursorVisible(false);

		/* AutoComplete on click */
        act_dialogChangeCityStateCountry.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                act_dialogChangeCityStateCountry.setFocusableInTouchMode(true);
                act_dialogChangeCityStateCountry.requestFocus();
                act_dialogChangeCityStateCountry.setCursorVisible(true);
            }
        });


		/* AutoComplete on item click */
        act_dialogChangeCityStateCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * Get data associated with the specified position in the list
				 * (AdapterView)
				 */

                act_dialogChangeCityStateCountry.setFocusableInTouchMode(false);
                act_dialogChangeCityStateCountry.setCursorVisible(false);
                HashMap<String, Object> mapPlaceIdAndDesc = GooglePlaceApi.getPlaceIdAndDesc(position);

                act_dialogChangeCityStateCountry.setText(mapPlaceIdAndDesc.get("DESCRIPTION").toString());

				/* set user selected city,country,state */
                //userSelectedCityStateCountry = mapPlaceIdAndDesc.get("DESCRIPTION").toString();
                String place_id = mapPlaceIdAndDesc.get("PLACE_ID").toString();
                // act_dialogChangeCityStateCountry.setText(userSelectedCityStateCountry);

                //Call API For Get The Address
                /*AddressService.getAddress(context, getPlaceInfo(place_id), new APIService.Success<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        Log.d("TAG", "Place Response-->" + response.toString());
                        googlePlaceParser = GooglePlaceParser.parseResponse(response);

                    }
                });*/
            }
        });
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
     *
     * @return
     */
    public static String getFragmentName() {
        return EditProfileFragment.class.getName();
    }

    /***
     * Start To Crop Image
     * @param source
     */
    private void beginCrop(Uri source) {
        Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
        new Crop(source).output(outputUri).asSquare().start(getActivity());
    }

    /**
     * After The Crop Image Store In Memory And Set To ImageView
     *
     * @param resultCode
     * @param result
     */
    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(new File(CropUtil.getImageFile().getAbsolutePath()));
            ivMarkerDialogCornerView.setImageURI(uri);
            fileCoverPic = CropUtil.getImageFile();

            //ivMarkerDialogCornerView.setImageURI(Uri.parse(fileUri.getPath()));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(getContext(), Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
