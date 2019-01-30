package com.app.fandirect.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetProfileEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.CameraHelper;
import com.app.fandirect.helpers.DatePickerHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ImageSetter;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.AutoCompleteLocation;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.app.fandirect.global.WebServiceConstants.UpdateUserProfile;

/**
 * Created by saeedhyder on 3/5/2018.
 */
public class UserEditProfile extends BaseFragment implements ImageSetter {


    @BindView(R.id.iv_profileImage)
    CircleImageView ivProfileImage;
    @BindView(R.id.btn_UploadProfile)
    AnyTextView btnUploadProfile;
    @BindView(R.id.txt_name)
    AnyEditTextView txtName;
    @BindView(R.id.txt_email)
    AnyTextView txtEmail;
    @BindView(R.id.txt_phone)
    AnyEditTextView txtPhone;
    @BindView(R.id.txt_gender)
    AnyEditTextView txtGender;
    @BindView(R.id.btn_male)
    AnyTextView btnMale;
    @BindView(R.id.btn_female)
    AnyTextView btnFemale;
    @BindView(R.id.txt_profession)
    AnyEditTextView txtProfession;
    @BindView(R.id.txt_WorksAt)
    AnyEditTextView txtWorksAt;
    @BindView(R.id.txt_Hobbies)
    AnyEditTextView txtHobbies;
    @BindView(R.id.txt_aboutUs)
    XEditText txtAboutUs;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    Unbinder unbinder;
    @BindView(R.id.txt_birthday)
    AnyTextView txtBirthday;
    @BindView(R.id.txt_autoComplete)
    AutoCompleteLocation txtAutoComplete;
    @BindView(R.id.txt_Education)
    AnyEditTextView txtEducation;
    private String gender = AppConstants.male;
    ;

    private Date DateSelected;

    private File profilePic;
    private String profilePath;
    private GetProfileEnt getProfileEnt;
    private static String getProfileKey = "getProfileKey";
    private String getProfile;
    ImageLoader imageLoader;
    private String locationName;
    private LatLng locationLatLng;

    public static UserEditProfile newInstance() {
        Bundle args = new Bundle();

        UserEditProfile fragment = new UserEditProfile();
        fragment.setArguments(args);
        return fragment;
    }

    public static UserEditProfile newInstance(GetProfileEnt ent) {
        Bundle args = new Bundle();
        args.putString(getProfileKey, new Gson().toJson(ent));
        UserEditProfile fragment = new UserEditProfile();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        if (getArguments() != null) {
            getProfile = getArguments().getString(getProfileKey);
        }
        if (getProfile != null) {
            getProfileEnt = new Gson().fromJson(getProfile, GetProfileEnt.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().setImageSetter(this);
        setListners();
        getMainActivity().hideBottomBar();
        autoCompleteLocationListner();
        setData();
    }

    private void autoCompleteLocationListner() {

        txtAutoComplete.setAutoCompleteTextListener(new AutoCompleteLocation.AutoCompleteLocationListener() {
            @Override
            public void onTextClear() {

            }

            @Override
            public void onItemSelected(Place selectedPlace) {
                locationName = selectedPlace.getName().toString();
                locationLatLng = selectedPlace.getLatLng();
            }
        });
    }

    private void setData() {

        if (getProfileEnt != null) {

            if (getProfileEnt.getImageUrl() != null)
                imageLoader.displayImage(getProfileEnt.getImageUrl() + "", ivProfileImage);
            if (getProfileEnt.getUserName() != null && !getProfileEnt.getUserName().equals("") && !getProfileEnt.getUserName().equals("null")) {
                txtName.setText(getProfileEnt.getUserName() + "");
            }
            if (getProfileEnt.getEmail() != null && !getProfileEnt.getEmail().equals("") && !getProfileEnt.getEmail().equals("null")) {
                txtEmail.setText(getProfileEnt.getEmail());
            }
            if (getProfileEnt.getPhone() != null && !getProfileEnt.getPhone().equals("") && !getProfileEnt.getPhone().equals("null")) {
                txtPhone.setText(getProfileEnt.getPhone() + "");
            }

            if (getProfileEnt.getDob() != null && !getProfileEnt.getDob().equals("") && !getProfileEnt.getDob().equals("null")) {
                txtBirthday.setText(getProfileEnt.getDob() + "");
            }
            if (getProfileEnt.getLocation() != null && !getProfileEnt.getLocation().equals("") && !getProfileEnt.getLocation().equals("null")) {
                txtAutoComplete.setText(getProfileEnt.getLocation() + "");
            }
            if (getProfileEnt.getProfession() != null && !getProfileEnt.getProfession().equals("") && !getProfileEnt.getProfession().equals("null")) {
                txtProfession.setText(getProfileEnt.getProfession() + "");
            }
            if (getProfileEnt.getHobbies() != null && !getProfileEnt.getHobbies().equals("") && !getProfileEnt.getHobbies().equals("null")) {
                txtHobbies.setText(getProfileEnt.getHobbies() + "");
            }
            if (getProfileEnt.getWorkAt() != null && !getProfileEnt.getWorkAt().equals("") && !getProfileEnt.getWorkAt().equals("null")) {
                txtWorksAt.setText(getProfileEnt.getWorkAt() + "");
            }
            if (getProfileEnt.getAbout() != null && !getProfileEnt.getAbout().equals("") && !getProfileEnt.getAbout().equals("null")) {
                txtAboutUs.setText(getProfileEnt.getAbout() + "");
            }
            if (locationLatLng == null && !getProfileEnt.getLatitude().equals("")) {
                locationLatLng = new LatLng(Double.parseDouble(getProfileEnt.getLatitude()), Double.parseDouble(getProfileEnt.getLongitude()));
            }
            if (getProfileEnt.getEducation() != null && !getProfileEnt.getEducation().equals("") && !getProfileEnt.getEducation().equals("null")) {
                txtEducation.setText(getProfileEnt.getEducation() != null ? getProfileEnt.getEducation() : "");
            }

            if (getProfileEnt.getGender() != null) {
                if (getProfileEnt.getGender().equals("male")) {
                    btnMale.setTextColor(getResources().getColor(R.color.app_blue));
                    btnFemale.setTextColor(getResources().getColor(R.color.app_dark_gray_2));
                    gender = AppConstants.male;
                } else if (getProfileEnt.getGender().equals("female")) {
                    btnMale.setTextColor(getResources().getColor(R.color.app_dark_gray_2));
                    btnFemale.setTextColor(getResources().getColor(R.color.app_blue));
                    gender = AppConstants.female;
                }
            }
        }
    }

    private void setListners() {

        txtAboutUs.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
    }

    private boolean isValidated() {
        if (txtName.getText() == null || (txtName.getText().toString().isEmpty())) {
            if (txtName.requestFocus()) {
                setEditTextFocus(txtName);
            }
            txtName.setError(getString(R.string.enter_FullName));
            return false;
        } else if (txtAutoComplete.getText() == null || (txtAutoComplete.getText().toString().isEmpty())) {

            txtAutoComplete.setError("Location cannot be Empty");
            return false;
        } else if (txtWorksAt.getText() == null || (txtWorksAt.getText().toString().isEmpty())) {

            if (txtWorksAt.requestFocus()) {
                setEditTextFocus(txtWorksAt);
            }
            txtWorksAt.setError("Workat cannot be Empty");
            return false;
        } /*else if (txtPhone.getText() == null || (txtPhone.getText().toString().isEmpty())) {
            if (txtPhone.requestFocus()) {
                setEditTextFocus(txtPhone);
            }
            txtPhone.setError(getString(R.string.enter_MobileNum));
            return false;
        } else if (txtBirthday.getText() == null || (txtBirthday.getText().toString().isEmpty())) {
            UIHelper.showShortToastInCenter(getDockActivity(),"Date of birth cannot be Empty");
           // txtBirthday.setError("Date of birth cannot be Empty");
            return false;
        }else if (txtProfession.getText() == null || (txtProfession.getText().toString().isEmpty())) {

            if (txtProfession.requestFocus()) {
                setEditTextFocus(txtProfession);
            }
            txtProfession.setError("Profession cannot be Empty");
            return false;
        }  else if (txtHobbies.getText() == null || (txtHobbies.getText().toString().isEmpty())) {

            if (txtHobbies.requestFocus()) {
                setEditTextFocus(txtHobbies);
            }
            txtHobbies.setError("Hobbies cannot be Empty");
            return false;
        } else if (txtAboutUs.getText() == null || (txtAboutUs.getText().toString().isEmpty())) {
          if (txtAboutUs.requestFocus()) {
                setEditTextFocus(txtAboutUs);
            }
            txtAboutUs.setError("Aboutus cannot be Empty");
            return false;
        }*/ else {
            // txtBirthday.setError(null);
            return true;
        }

    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.edit_profile));
    }


    @OnClick({R.id.btn_UploadProfile, R.id.btn_update, R.id.btn_male, R.id.btn_female, R.id.txt_birthday})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_UploadProfile:
                requestCameraPermission();

                break;
            case R.id.btn_update:
                if (isValidated()) {
                    updateProfileService();
                }

                break;

            case R.id.btn_male:
                btnMale.setTextColor(getResources().getColor(R.color.app_blue));
                btnFemale.setTextColor(getResources().getColor(R.color.app_dark_gray_2));
                gender = AppConstants.male;
                break;
            case R.id.btn_female:
                btnMale.setTextColor(getResources().getColor(R.color.app_dark_gray_2));
                btnFemale.setTextColor(getResources().getColor(R.color.app_blue));
                gender = AppConstants.female;
                break;

            case R.id.txt_birthday:
                initDatePicker(txtBirthday);
                break;

        }
    }

    private void initDatePicker(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        final DatePickerHelper datePickerHelper = new DatePickerHelper();
        datePickerHelper.initDateDialog(
                getDockActivity(),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
                , new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date date = new Date();
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.YEAR, year);
                        c.set(Calendar.MONTH, month);
                        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

// and get that as a Date
                        Date dateSpecified = c.getTime();
                        if (dateSpecified.after(date)) {
                            UIHelper.showShortToastInCenter(getDockActivity(), getString(R.string.date_after_error));
                        } else {
                            DateSelected = dateSpecified;
                            String predate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

                            textView.setText(predate);
                            textView.setPaintFlags(Typeface.BOLD);
                        }

                    }
                }, "PreferredDate", 1);

        datePickerHelper.showDate();
    }

    private void updateProfileService() {

        MultipartBody.Part filePart;
        if (profilePic != null) {
            filePart = MultipartBody.Part.createFormData("avatar",
                    profilePic.getName(), RequestBody.create(MediaType.parse("image/*"), profilePic));
        } else {
            filePart = MultipartBody.Part.createFormData("avatar", "",
                    RequestBody.create(MediaType.parse("*/*"), ""));
        }
      /*  serviceHelper.enqueueCall(headerWebService.updateUserProfile(
                RequestBody.create(MediaType.parse("text/plain"), txtName.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtEmail.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtPhone.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), gender),
                RequestBody.create(MediaType.parse("text/plain"), (locationName != null) ? locationName : txtAutoComplete.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), (String.valueOf(locationLatLng.latitude))),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(locationLatLng.longitude)),
                RequestBody.create(MediaType.parse("text/plain"), txtBirthday.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtProfession.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtWorksAt.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtHobbies.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtAboutUs.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtEducation.getText().toString()),
                filePart
        ), UpdateUserProfile);*/
        serviceHelper.enqueueCall(headerWebService.updateUserProfile(
                RequestBody.create(MediaType.parse("text/plain"), txtName.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtEmail.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtPhone.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), gender),
                RequestBody.create(MediaType.parse("text/plain"), (locationName != null) ? locationName : txtAutoComplete.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), (String.valueOf(locationLatLng.latitude))),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(locationLatLng.longitude)),
                RequestBody.create(MediaType.parse("text/plain"), txtProfession.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtWorksAt.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtHobbies.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtAboutUs.getText().toString()),
                filePart
        ), UpdateUserProfile);

    }

    @Override
    public void setImage(String imagePath) {
        if (imagePath != null) {

            try {
                profilePic = new Compressor(getDockActivity()).compressToFile(new File(imagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // profilePic = new File(imagePath);
            profilePath = imagePath;

            Picasso.with(getDockActivity())
                    .load("file:///" + imagePath)
                    .into(ivProfileImage);
            //   imageLoader.displayImage("file:///" + imagePath, ivProfileImage);

        }
    }

    @Override
    public void setFilePath(String filePath) {

    }

    @Override
    public void setVideo(String videoPath, String VideoThumbail) {

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case UpdateUserProfile:
                UserEnt entity = (UserEnt) result;
                prefHelper.putUser(entity);
                prefHelper.setUserType(getString(R.string.user));
                prefHelper.set_TOKEN(entity.getToken());
                UIHelper.showShortToastInCenter(getDockActivity(),"Profile updated successfully");
                //getDockActivity().popBackStackTillEntry(1);
               // getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(), "UserProfileFragment");
                getDockActivity().popFragment();

                break;
        }
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



}
