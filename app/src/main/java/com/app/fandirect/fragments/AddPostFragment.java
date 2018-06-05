package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.CameraHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ImageSetter;
import com.app.fandirect.interfaces.PostInterface;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;
import com.xw.repo.XEditText;

import java.io.File;
import java.io.IOException;

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

    //selected place

    private File profilePic;
    private String selectedName;
    private String selectedAddress;
    private String selectedCategory;
    private String selectedCategoryId;
    private String latitudeSelectedPlace;
    private String longitudeSelectedPlace;
    private ImageLoader imageLoader;


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
        setData();

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
                    if (txtCaption == null || txtCaption.getText().toString().isEmpty() || txtCaption.getText().toString().equals("")) {
                        txtCaption.setError("write status to proceed");
                    } else if (selectedCategory == null) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Select Category");
                    } /*else if (selectedName == null) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Select Location");
                    }*/ else {
                        if (profilePic != null) {
                            createPromotionImagePost();
                        } else {
                            createPromotionTextPost();
                        }
                    }
                } else {
                    if (txtCaption == null || txtCaption.getText().toString().isEmpty() || txtCaption.getText().toString().equals("")) {
                        txtCaption.setError("write status to proceed");
                    } /*else if (selectedName == null) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Select Location");
                    }*/ else {
                        if (profilePic != null) {
                            createFeedImagePost();
                        } else {
                            createFeedTextPost();
                        }
                    }

                }
            }
        });
        titleBar.setSubHeading(getString(R.string.new_post));
    }

    private void createFeedTextPost() {

        serviceHelper.enqueueCall(headerWebService.createFeedPost(txtCaption.getText().toString(), text, selectedName, latitudeSelectedPlace, longitudeSelectedPlace), createFeedTextPost);

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

        serviceHelper.enqueueCall(headerWebService.createFeedPost(
                RequestBody.create(MediaType.parse("text/plain"), txtCaption.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), image),
                RequestBody.create(MediaType.parse("text/plain"), selectedName!=null?selectedName:""),
                RequestBody.create(MediaType.parse("text/plain"), latitudeSelectedPlace!=null?latitudeSelectedPlace:""),
                RequestBody.create(MediaType.parse("text/plain"), longitudeSelectedPlace!=null?longitudeSelectedPlace:""),
                filePart
        ), createFeedImagePost);

    }

    private void createPromotionTextPost() {

        serviceHelper.enqueueCall(headerWebService.createPromotionPost(txtCaption.getText().toString(), text, selectedName, latitudeSelectedPlace, longitudeSelectedPlace, selectedCategoryId), createPromotionTextPost);

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

        serviceHelper.enqueueCall(headerWebService.createPromotionPost(
                RequestBody.create(MediaType.parse("text/plain"), txtCaption.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), image),
                RequestBody.create(MediaType.parse("text/plain"), selectedName!=null?selectedName:""),
                RequestBody.create(MediaType.parse("text/plain"), latitudeSelectedPlace!=null?latitudeSelectedPlace:""),
                RequestBody.create(MediaType.parse("text/plain"), longitudeSelectedPlace!=null?longitudeSelectedPlace:""),
                RequestBody.create(MediaType.parse("text/plain"), selectedCategoryId),
                filePart
        ), createPromotionImagePost);
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
                if (getMainActivity().statusCheck()) {
                    SelectLocationTagFragment selectLocationTagFragment = new SelectLocationTagFragment();
                    selectLocationTagFragment.setInterfaceListner(this);
                    getDockActivity().addDockableFragment(selectLocationTagFragment, "SelectLocationTagFragment");
                }
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
                CameraHelper.uploadPhotoDialog(getMainActivity());
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
                getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");
                break;

            case createPromotionImagePost:
                getDockActivity().popFragment();
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");
                break;

            case createFeedTextPost:
                getDockActivity().popFragment();
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");
                break;

            case createFeedImagePost:
                getDockActivity().popFragment();
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");
                break;

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
            imageLoader.displayImage("file:///" + imagePath,ivUploadPhoto);
          /*  Picasso.with(getDockActivity())
                    .load("file:///" + imagePath)
                    .into(ivUploadPhoto);*/
        }

    }

    @Override
    public void setFilePath(String filePath) {

    }

    @Override
    public void setVideo(String videoPath, String VideoThumbail) {

    }
}
