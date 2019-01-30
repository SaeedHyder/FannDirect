package com.app.fandirect.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.TagUser;
import com.app.fandirect.entities.commentEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.CameraHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ImageSetter;
import com.app.fandirect.interfaces.PostInterface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.adapters.TagUserAdapter;
import com.app.fandirect.ui.dialogs.DialogFactory;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.maps.model.LatLng;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.xw.repo.XEditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.app.fandirect.global.AppConstants.image;
import static com.app.fandirect.global.AppConstants.text;
import static com.app.fandirect.global.WebServiceConstants.createFeedImagePost;
import static com.app.fandirect.global.WebServiceConstants.createFeedTextPost;
import static com.app.fandirect.global.WebServiceConstants.createPromotionImagePost;
import static com.app.fandirect.global.WebServiceConstants.createPromotionTextPost;
import static com.app.fandirect.global.WebServiceConstants.getMyFanns;
import static com.app.fandirect.global.WebServiceConstants.postComments;


/**
 * Created by saeedhyder on 5/10/2018.
 */
public class AddPostFragment extends BaseFragment implements PostInterface, ImageSetter {
    @BindView(R.id.iv_image)
    CircleImageView ivImage;
    @BindView(R.id.txt_caption)
    XEditText txtCaption;
    @BindView(R.id.iv_upload_photo)
    ImageView ivUploadPhoto;
    @BindView(R.id.ll_add_category)
    LinearLayout llAddCategory;
    @BindView(R.id.txt_category_name)
    AnyTextView txtCategoryName;
    @BindView(R.id.iv_location_category)
    ImageView ivLocationCategory;
    @BindView(R.id.ll_cross_category)
    LinearLayout llCrossCategory;
    @BindView(R.id.rl_selected_category)
    RelativeLayout rlSelectedCategory;
    @BindView(R.id.ll_add_location)
    LinearLayout llAddLocation;
    @BindView(R.id.txt_location_name)
    AnyTextView txtLocationName;
    @BindView(R.id.txt_location_vacinity)
    AnyTextView txtLocationVacinity;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.ll_cross)
    LinearLayout llCross;
    @BindView(R.id.rl_selected_place)
    RelativeLayout rlSelectedPlace;
    Unbinder unbinder;
    private static String isPromotionKey = "isPromotionKey";
    boolean isTwise = false;

    //selected place

    private File profilePic;
    private String selectedName;
    private String selectedAddress;
    private String selectedCategory;
    private String selectedCategoryId;
    private String latitudeSelectedPlace;
    private String longitudeSelectedPlace;
    private ImageLoader imageLoader;

    private ListPopupWindow popupWindow;
    private TagUserAdapter adapterTag;
    private HashMap<String, GetMyFannsEnt> hashMap = new HashMap<>();
    private static ArrayList<TagUser> tagUserList = new ArrayList<>();
    private static ArrayList<String> tagIds = new ArrayList<>();
    private static ArrayList<String> tagNames = new ArrayList<>();
    private ArrayList<GetMyFannsEnt> userList = new ArrayList<>();
    private ArrayList<GetMyFannsEnt> filterList = new ArrayList<>();

    private ArrayListAdapter<commentEnt> adapter;
    private ArrayList<commentEnt> collection;
    private static String postIdKey = "postIdKey";
    private String postId;
    private String idComment;
    private int pos = -1;

    boolean isFromPromotion = false;

    public static AddPostFragment newInstance(boolean isPromotion) {
        Bundle args = new Bundle();
        args.putBoolean(isPromotionKey, isPromotion);
        AddPostFragment fragment = new AddPostFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        if (getArguments() != null) {
            isFromPromotion = getArguments().getBoolean(isPromotionKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_post, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
        getMainActivity().setImageSetter(this);

        tagIds = new ArrayList<>();
        initListPopupAdapter();

        double scaletype = getResources().getDisplayMetrics().density;
        if (scaletype >= 3.0) {
            isTwise = true;
        }

        serviceHelper.enqueueCall(headerWebService.getMyFanns(), getMyFanns);

        setData();
        setEditTextListener();

    }

    public void initListPopupAdapter() {
        userList = new ArrayList<>();
        popupWindow = new ListPopupWindow(getDockActivity());
        adapterTag = new TagUserAdapter(getDockActivity(), userList, prefHelper.getUser().getId());
        popupWindow.setAnchorView(txtCaption);
        popupWindow.setAdapter(adapter);
    }





    public String getUserName(GetMyFannsEnt entity) {
        if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {
            if (entity.getReceiverDetail() != null) {
                return entity.getReceiverDetail().getUserName();

            }
        } else if (entity.getSenderDetail() != null) {
            return entity.getSenderDetail().getUserName();
        }

        return "";
    }

    public String getUserId(GetMyFannsEnt entity) {
        if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {
            if (entity.getReceiverDetail() != null) {
                return entity.getReceiverDetail().getId();
            }
        } else if (entity.getSenderDetail() != null) {
            return entity.getSenderDetail().getId();
        }

        return "";
    }



    private void setData() {

        if (isFromPromotion) {
            llAddCategory.setVisibility(View.VISIBLE);
        }

        if (prefHelper.getUser().getImageUrl() != null) {

            imageLoader.displayImage(prefHelper.getUser().getImageUrl() + "", ivImage);
        }

    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.showPostBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFromPromotion) {
                    if (isValidatePromotion()) {
                        if (profilePic != null) {
                            createPromotionImagePost();
                        } else {
                            createPromotionTextPost();
                        }
                    }
                } else {
                    if(isValidateFeeds()){
                        if (profilePic != null) {
                            createFeedImagePost();
                        } else {
                            createFeedTextPost();
                        }
                    }
                }

               /* if (isFromPromotion) {
                    if (txtCaption == null || txtCaption.getText().toString().isEmpty() || txtCaption.getText().toString().trim().equals("")) {
                        txtCaption.setError("write status to proceed");
                    } else if (selectedCategory == null) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Select Category");
                    } else if (selectedName == null) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Select Location");
                    } else {
                        if (profilePic != null) {
                            createPromotionImagePost();
                        } else {
                            createPromotionTextPost();
                        }
                    }
                } *//*else {
                    if (txtCaption == null || txtCaption.getText().toString().isEmpty() || txtCaption.getText().toString().trim().equals("")) {
                        txtCaption.setError("write status to proceed");
                    } else if (selectedName == null) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Select Location");
                    } else {
                        if (profilePic != null) {
                            createFeedImagePost();
                        } else {
                            createFeedTextPost();
                        }
                    }

                }*/
            }
        });
        titleBar.setSubHeading(getString(R.string.new_post));
    }

    private boolean isValidatePromotion() {
        if (txtCaption == null || txtCaption.getText().toString().isEmpty() || txtCaption.getText().toString().trim().equals("")) {
            if (txtCaption.requestFocus()) {
                setTextFocus(txtCaption);
            }
            txtCaption.setError("write status to proceed");
            return false;
        } else if (selectedCategory == null) {
            UIHelper.showShortToastInCenter(getDockActivity(), "Select Category");
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidateFeeds() {
        if (txtCaption == null || txtCaption.getText().toString().isEmpty() || txtCaption.getText().toString().trim().equals("")) {
            if (txtCaption.requestFocus()) {
                setTextFocus(txtCaption);
            }
            txtCaption.setError("write status to proceed");
            return false;
        } /*else if (selectedName == null) {
            UIHelper.showShortToastInCenter(getDockActivity(), "Select Location");
            return false;
        }*/ else {
           return true;
        }
    }

    private void createFeedTextPost() {

        if (idComment != null && !idComment.equals("")) {
            idComment = txtCaption.getText().toString();
            tagIds=new ArrayList<>();

            for (int j = 0; j < userList.size(); j++) {
                if (getUserName(userList.get(j)) != null && !getUserName(userList.get(j)).equals("") && (idComment.toString().contains("@" + getUserName(userList.get(j))))) {
                    tagIds.add(getUserId(userList.get(j)));
                    idComment = idComment.replace("@" + getUserName(userList.get(j)), "@" + getUserId(userList.get(j)) + "@");
                }
            }
            String Ids = android.text.TextUtils.join(",", tagIds);

            serviceHelper.enqueueCall(headerWebService.createFeedPost(Html.fromHtml(idComment + "").toString(), text, selectedName != null ? selectedName : "", latitudeSelectedPlace, longitudeSelectedPlace,Ids), createFeedTextPost);

        } else {

            serviceHelper.enqueueCall(headerWebService.createFeedPost(txtCaption.getText().toString(), text, selectedName != null ? selectedName : "", latitudeSelectedPlace, longitudeSelectedPlace,""), createFeedTextPost);
        }



    }

    private void createFeedImagePost() {

        MultipartBody.Part filePart;
        if (profilePic != null) {
            filePart = MultipartBody.Part.createFormData("image",
                    profilePic.getName(), RequestBody.create(MediaType.parse("image/*"), profilePic));
        } else {
            filePart = MultipartBody.Part.createFormData("image", "",
                    RequestBody.create(MediaType.parse("*/*"), ""));
        }

        if (idComment != null && !idComment.equals("")) {
            idComment = txtCaption.getText().toString();
            tagIds=new ArrayList<>();

            for (int j = 0; j < userList.size(); j++) {
                if (getUserName(userList.get(j)) != null && !getUserName(userList.get(j)).equals("") && (idComment.toString().contains("@" + getUserName(userList.get(j))))) {
                    tagIds.add(getUserId(userList.get(j)));
                    idComment = idComment.replace("@" + getUserName(userList.get(j)), "@" + getUserId(userList.get(j)) + "@");
                }
            }
            String Ids = android.text.TextUtils.join(",", tagIds);

            serviceHelper.enqueueCall(headerWebService.createFeedPost(
                    RequestBody.create(MediaType.parse("text/plain"), Html.fromHtml(idComment + "").toString()),
                    RequestBody.create(MediaType.parse("text/plain"), image),
                    RequestBody.create(MediaType.parse("text/plain"), selectedName != null ? selectedName : ""),
                    RequestBody.create(MediaType.parse("text/plain"), latitudeSelectedPlace != null ? latitudeSelectedPlace : ""),
                    RequestBody.create(MediaType.parse("text/plain"), longitudeSelectedPlace != null ? longitudeSelectedPlace : ""),
                    filePart,
                    RequestBody.create(MediaType.parse("text/plain"), Ids)
            ), createFeedImagePost);

        } else {

            serviceHelper.enqueueCall(headerWebService.createFeedPost(
                    RequestBody.create(MediaType.parse("text/plain"), txtCaption.getText().toString()),
                    RequestBody.create(MediaType.parse("text/plain"), image),
                    RequestBody.create(MediaType.parse("text/plain"), selectedName != null ? selectedName : ""),
                    RequestBody.create(MediaType.parse("text/plain"), latitudeSelectedPlace != null ? latitudeSelectedPlace : ""),
                    RequestBody.create(MediaType.parse("text/plain"), longitudeSelectedPlace != null ? longitudeSelectedPlace : ""),
                    filePart,
                    RequestBody.create(MediaType.parse("text/plain"), "")
            ), createFeedImagePost);
        }



    }

    private void createPromotionTextPost() {

        if (idComment != null && !idComment.equals("")) {
            idComment = txtCaption.getText().toString();
            tagIds=new ArrayList<>();

            for (int j = 0; j < userList.size(); j++) {
                if (getUserName(userList.get(j)) != null && !getUserName(userList.get(j)).equals("") && (idComment.toString().contains("@" + getUserName(userList.get(j))))) {
                    tagIds.add(getUserId(userList.get(j)));
                    idComment = idComment.replace("@" + getUserName(userList.get(j)), "@" + getUserId(userList.get(j)) + "@");
                }
            }
            String Ids = android.text.TextUtils.join(",", tagIds);

            serviceHelper.enqueueCall(headerWebService.createPromotionPost(Html.fromHtml(idComment + "").toString(), text, selectedName != null ? selectedName : "", latitudeSelectedPlace, longitudeSelectedPlace, selectedCategoryId,Ids), createPromotionTextPost);

        } else {
            serviceHelper.enqueueCall(headerWebService.createPromotionPost(txtCaption.getText().toString(), text, selectedName != null ? selectedName : "", latitudeSelectedPlace, longitudeSelectedPlace, selectedCategoryId,""), createPromotionTextPost);
        }



    }

    private void createPromotionImagePost() {


        MultipartBody.Part filePart;
        if (profilePic != null) {
            filePart = MultipartBody.Part.createFormData("image",
                    profilePic.getName(), RequestBody.create(MediaType.parse("image/*"), profilePic));
        } else {
            filePart = MultipartBody.Part.createFormData("image", "",
                    RequestBody.create(MediaType.parse("*/*"), ""));
        }

        if (idComment != null && !idComment.equals("")) {
            idComment = txtCaption.getText().toString();
            tagIds=new ArrayList<>();

            for (int j = 0; j < userList.size(); j++) {
                if (getUserName(userList.get(j)) != null && !getUserName(userList.get(j)).equals("") && (idComment.toString().contains("@" + getUserName(userList.get(j))))) {
                    tagIds.add(getUserId(userList.get(j)));
                    idComment = idComment.replace("@" + getUserName(userList.get(j)), "@" + getUserId(userList.get(j)) + "@");
                }
            }
            String Ids = android.text.TextUtils.join(",", tagIds);


            serviceHelper.enqueueCall(headerWebService.createPromotionPost(
                    RequestBody.create(MediaType.parse("text/plain"), Html.fromHtml(idComment + "").toString()),
                    RequestBody.create(MediaType.parse("text/plain"), image),
                    RequestBody.create(MediaType.parse("text/plain"), selectedName != null ? selectedName : ""),
                    RequestBody.create(MediaType.parse("text/plain"), latitudeSelectedPlace != null ? latitudeSelectedPlace : ""),
                    RequestBody.create(MediaType.parse("text/plain"), longitudeSelectedPlace != null ? longitudeSelectedPlace : ""),
                    RequestBody.create(MediaType.parse("text/plain"), selectedCategoryId),
                    filePart,
                    RequestBody.create(MediaType.parse("text/plain"), Ids)

            ), createPromotionImagePost);

        } else {
            serviceHelper.enqueueCall(headerWebService.createPromotionPost(
                    RequestBody.create(MediaType.parse("text/plain"), txtCaption.getText().toString()),
                    RequestBody.create(MediaType.parse("text/plain"), image),
                    RequestBody.create(MediaType.parse("text/plain"), selectedName != null ? selectedName : ""),
                    RequestBody.create(MediaType.parse("text/plain"), latitudeSelectedPlace != null ? latitudeSelectedPlace : ""),
                    RequestBody.create(MediaType.parse("text/plain"), longitudeSelectedPlace != null ? longitudeSelectedPlace : ""),
                    RequestBody.create(MediaType.parse("text/plain"), selectedCategoryId),
                    filePart,
                    RequestBody.create(MediaType.parse("text/plain"), "")

            ), createPromotionImagePost);
        }


    }


    @OnClick({R.id.ll_add_category, R.id.ll_cross_category, R.id.ll_add_location, R.id.ll_cross, R.id.btn_uploadPhoto})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_add_category:
                SelectedCategoryFragment selectedCategoryFragment = new SelectedCategoryFragment();
                selectedCategoryFragment.setInterfaceListner(this);
                getDockActivity().addDockableFragment(selectedCategoryFragment, "SelectLocationTagFragment");
                break;

            case R.id.ll_add_location:
                requestLocationPermission();


                break;

            case R.id.ll_cross_category:
                llAddCategory.setVisibility(View.VISIBLE);
                rlSelectedCategory.setVisibility(View.GONE);
                selectedCategory = null;
                break;
            case R.id.ll_cross:
                llAddLocation.setVisibility(View.VISIBLE);
                rlSelectedPlace.setVisibility(View.GONE);
                selectedName = null;
                break;

            case R.id.btn_uploadPhoto:
                requestCameraPermission();

                break;
        }
    }

    @Override
    public void getCategoryData(GetServicesEnt categoryName) {

        selectedCategory = categoryName.getName();
        selectedCategoryId = categoryName.getId() + "";

        if (selectedCategory != null && !selectedCategory.equals("")) {
            llAddCategory.setVisibility(View.GONE);
            rlSelectedCategory.setVisibility(View.VISIBLE);

            txtCategoryName.setText(selectedCategory);
        }

    }

    @Override
    public void getSelectedPlaceData(String placeName, String placeAddress, String placeLat, String placeLng) {

        selectedName = placeName;
        selectedAddress = placeAddress;
        latitudeSelectedPlace = placeLat;
        longitudeSelectedPlace = placeLng;

        if (selectedName != null && !selectedName.equals("") && selectedAddress != null && !selectedAddress.equals("")) {
            llAddLocation.setVisibility(View.GONE);
            rlSelectedPlace.setVisibility(View.VISIBLE);

            txtLocationName.setText(selectedName);
            txtLocationVacinity.setText(selectedAddress);

        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case createPromotionTextPost:
                getDockActivity().popFragment();
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                //  getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");
                break;

            case createPromotionImagePost:
                getDockActivity().popFragment();
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                //  getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");
                break;

            case createFeedTextPost:
                getDockActivity().popFragment();
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                //   getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");
                break;

            case createFeedImagePost:
                getDockActivity().popFragment();
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                //  getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");
                break;

            case getMyFanns:
                userList = (ArrayList<GetMyFannsEnt>) result;

        }
    }

    @Override
    public void setImage(String imagePath) {
        if (imagePath != null) {

            try {
                profilePic = new Compressor(getDockActivity()).compressToFile(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            ivUploadPhoto.setVisibility(View.VISIBLE);
            //     imageLoader.displayImage("file:///" + imagePath,ivUploadPhoto);
            Picasso.with(getDockActivity())
                    .load("file:///" + imagePath)
                    .into(ivUploadPhoto);
        }

    }

    @Override
    public void setFilePath(String filePath) {

    }

    @Override
    public void setVideo(String videoPath, String VideoThumbail) {

    }

    private void requestCameraPermission() {
        Dexter.withActivity(getDockActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            CameraHelper.uploadPhotoDialog(getMainActivity());
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestCameraPermission();

                        } else if (report.getDeniedPermissionResponses().size() > 0) {
                            requestCameraPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Grant Camera And Storage Permission to processed");
                        openSettings();
                    }
                })

                .onSameThread()
                .check();


    }

    private void requestLocationPermission() {
        Dexter.withActivity(getDockActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (getMainActivity() != null && getMainActivity().statusCheck()) {
                                SelectLocationTagFragment selectLocationTagFragment = new SelectLocationTagFragment();
                                selectLocationTagFragment.setInterfaceListner(AddPostFragment.this);
                                getDockActivity().addDockableFragment(selectLocationTagFragment, "SelectLocationTagFragment");
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestLocationPermission();

                        } else if (report.getDeniedPermissionResponses().size() > 0) {
                            requestLocationPermission();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Grant Location Permission to processed");
                        openSettings();
                    }
                })

                .onSameThread()
                .check();


    }

    private void openSettings() {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        Uri uri = Uri.fromParts("package", getDockActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if(getTitleBar()!=null){
            getTitleBar().hideNotification();
        }*/
    }

    public void setEditTextListener() {
        txtCaption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (popupWindow != null)
                    popupWindow.dismiss();
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, int before, int count) {

                Thread t = new Thread() {
                    public void run() {


                        if (s.toString().length() == 0) {
                            tagUserList = new ArrayList<>();
                            tagIds = new ArrayList<>();
                        }

                        try {
                            if (s.toString().substring(start).startsWith("@") || s.toString().substring(start).startsWith("#")) {
                                pos = start;
                            } else if (s.toString().substring(start).startsWith(" ") || s.toString().substring(start).startsWith("\n")) {
                                pos = -1;
                            }


                            if (pos != -1 && s.toString().length() >= pos && s.toString().substring(pos).startsWith("@")) {
                                if (s.toString().substring(pos).length() >= 3) {
                                    String tagUserName = s.toString().substring(pos).replace("@", "");
                                    showListPopupWindow(txtCaption, getFilteredArray(tagUserName), s.toString().substring(pos));
                                    //  showListPopupWindow(txtMessageBox, getFilteredArray(tagUserName), tagUserName);
                                }
                            } else if (pos != -1 && s.toString().length() >= pos && s.toString().substring(pos).startsWith("#")) {
                                if (s.toString().substring(pos).length() >= 3) {
                                    String tagUserName = s.toString().substring(pos).replace("#", "");
                                    showListPopupWindow(txtCaption, getFilteredArray(tagUserName), s.toString().substring(pos));
                                    //    showListPopupWindow(txtMessageBox, getFilteredArray(tagUserName), tagUserName);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                };
                t.start();


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (pos == -1)
                    popupWindow.dismiss();
            }
        });

    }

    public ArrayList<GetMyFannsEnt> getFilteredArray(String keyword) {
        if (userList.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<GetMyFannsEnt> arrayList = new ArrayList<>();

        String UserName = "";
        for (GetMyFannsEnt item : userList) {
            if (item.getSenderDetail().getId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                if (item.getReceiverDetail() != null) {
                    UserName = item.getReceiverDetail().getUserName();
                }
            } else if (item.getSenderDetail() != null) {
                UserName = item.getSenderDetail().getUserName();
            }

            if(keyword.toString().length()>3){
                if(UserName.toLowerCase().toString().contains(keyword.substring(0,2).toLowerCase().toString())){
                    arrayList.add(item);
                }
            } else{
                if(UserName.toLowerCase().toString().contains(keyword.toLowerCase().toString())){
                    arrayList.add(item);
                }
            }

           /* if (Pattern.compile(Pattern.quote(keyword.toLowerCase().toString()), Pattern.CASE_INSENSITIVE).matcher(UserName.toLowerCase().toString()).find()) {
                arrayList.add(item);
            }*/
        }
        return arrayList;

    }

    private void showListPopupWindow(final View anchor, final ArrayList<GetMyFannsEnt> list, final String name) {

        hashMap = new HashMap<>();
        tagIds=new ArrayList<>();
        for (GetMyFannsEnt item : list) {
            hashMap.put(getUserId(item), item);
        }

        for (int j = 0; j < list.size(); j++) {
            if (getUserName(list.get(j)) != null && !getUserName(list.get(j)).equals("") && (txtCaption.getText().toString().contains("@" + getUserName(list.get(j))))) {
                tagIds.add(getUserId(list.get(j)));
            }
        }
        for (String item : tagIds) {
            hashMap.remove(item);
        }

        filterList = new ArrayList<>();
        filterList.addAll(hashMap.values());


        getDockActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterTag.clearAllList();
                adapterTag.addAllList(filterList);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (filterList.size() > 0) {
                        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_FROM_FOCUSABLE);
                        UIHelper.hideSoftKeyboard(getDockActivity(), txtCaption);
                    }
                }
                popupWindow.setAnchorView(anchor);
                popupWindow.setAdapter(adapterTag);
                popupWindow.show();


            }
        });


        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                idComment = txtCaption.getText().toString();

                String textComment = txtCaption.getText().toString();
                String[] tagedName = name.split("\\s+");
                textComment = textComment.replace(tagedName[0], "@" + getUserName(filterList.get(i)));


                final SpannableStringBuilder spannableString = new SpannableStringBuilder(textComment);
                /*for (int j = 0; j < userList.size(); j++) {
                    int startIndexOfLink = textComment.indexOf("@" + getUserName(userList.get(j)));
                    if (startIndexOfLink != -1) {
                        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), startIndexOfLink, startIndexOfLink + ("@" + getUserName(userList.get(j))).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }*/
                for (int j = 0; j < userList.size(); j++) {
                    if (getUserName(userList.get(j)) != null && !getUserName(userList.get(j)).equals("") && !getUserName(userList.get(j)).isEmpty()) {
                        int startIndexOfLink = textComment.indexOf("@" + getUserName(userList.get(j)));
                        if (startIndexOfLink != -1) {
                            BitmapDrawable bitmapDrawable = setTag("@" + getUserName(userList.get(j)));
                            spannableString.setSpan(new ImageSpan(bitmapDrawable), startIndexOfLink, startIndexOfLink + ("@" + getUserName(userList.get(j))).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.append(" ");
                        }
                    }

                }

                getDockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            UIHelper.showSoftKeyboard(getDockActivity(), txtCaption);
                        }

                        txtCaption.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
                        txtCaption.setMovementMethod(LinkMovementMethod.getInstance());
                        txtCaption.setText(spannableString, TextView.BufferType.SPANNABLE);
                        txtCaption.setSelection(txtCaption.getText().length());
                        popupWindow.dismiss();

                    }
                });
                pos = -1;

            }
        });


    }

    public BitmapDrawable setTag(String tagName) {
        String chip = tagName;

        LayoutInflater lf = (LayoutInflater) getDockActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        TextView textView = (TextView) lf.inflate(R.layout.tag_edittext, null);
        textView.setText(chip); // set text
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(spec, spec);
        textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());
        Bitmap b = Bitmap.createBitmap(textView.getWidth(), textView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(b);
        canvas.translate(-textView.getScrollX(), -textView.getScrollY());
        textView.draw(canvas);
        textView.setDrawingCacheEnabled(true);
        Bitmap cacheBmp = textView.getDrawingCache();
        Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
        textView.destroyDrawingCache(); // destory drawable
        BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
        int width = bmpDrawable.getIntrinsicWidth();
        int height = bmpDrawable.getIntrinsicHeight();
        if (isTwise) {
            width = width * 2;
            height = height * 2;
        }
        bmpDrawable.setBounds(0, 0, width, height);

        return bmpDrawable;


    }

    @Override
    public void onPause() {
        super.onPause();
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
    }
}
