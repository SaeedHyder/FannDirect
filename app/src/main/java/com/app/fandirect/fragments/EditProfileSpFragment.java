package com.app.fandirect.fragments;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetProfileEnt;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.CameraHelper;
import com.app.fandirect.helpers.DatePickerHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ImageSetter;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.binders.CategoryItemBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.AutoCompleteLocation;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.app.fandirect.global.WebServiceConstants.AllServicesCategories;
import static com.app.fandirect.global.WebServiceConstants.UpdateUserProfile;

/**
 * Created by saeedhyder on 3/12/2018.
 */
public class EditProfileSpFragment extends BaseFragment implements ImageSetter, RecyclerViewItemListener {
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
    @BindView(R.id.txt_certificate_license)
    AnyEditTextView txtCertificateLicense;
    @BindView(R.id.txt_type_of_license)
    AnyEditTextView txtTypeOfLicense;
    @BindView(R.id.txt_company_name)
    AnyEditTextView txtCompanyName;
    @BindView(R.id.txt_company_category)
    AnyEditTextView txtCompanyCategory;
    @BindView(R.id.txt_insurance_avaliable)
    AnyTextView txtInsuranceAvaliable;
    @BindView(R.id.toggleNotification)
    ToggleButton toggleNotification;
    @BindView(R.id.txt_insurance_information)
    AnyEditTextView txtInsuranceInformation;
    @BindView(R.id.txt_aboutUs)
    XEditText txtAboutUs;
    @BindView(R.id.btn_update)
    Button btnUpdate;
    Unbinder unbinder;
    @BindView(R.id.btn_male)
    AnyTextView btnMale;
    @BindView(R.id.btn_female)
    AnyTextView btnFemale;
    @BindView(R.id.txt_birthday)
    AnyTextView txtBirthday;
    @BindView(R.id.txt_autoComplete)
    AutoCompleteLocation txtAutoComplete;
    @BindView(R.id.spn_services)
    Spinner spnServices;
    @BindView(R.id.lv_categories)
    CustomRecyclerView lvCategories;
    private Date DateSelected;

    private String gender = AppConstants.male;

    private File profilePic;
    private String profilePath;
    private GetProfileEnt getProfileEnt;
    private static String getProfileKey = "getProfileKey";
    private String getProfile;
    ImageLoader imageLoader;
    private String locationName;
    private LatLng locationLatLng;
    private ArrayList<String> userCollections = new ArrayList<>();
    private ArrayList<String> categoriesIds = new ArrayList<>();
    private ArrayList<String> genderCollection;
    private ArrayList<String> categoriesResult = new ArrayList<>();
    private ArrayList<GetServicesEnt> categoriesCollection;
    private String resultString = "";
    private String resultIds = "";
    private boolean isSpinnerFirstTime = true;

    public static EditProfileSpFragment newInstance() {
        Bundle args = new Bundle();

        EditProfileSpFragment fragment = new EditProfileSpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EditProfileSpFragment newInstance(GetProfileEnt ent) {
        Bundle args = new Bundle();
        args.putString(getProfileKey, new Gson().toJson(ent));
        EditProfileSpFragment fragment = new EditProfileSpFragment();
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
        View view = inflater.inflate(R.layout.fragment_sp_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().setImageSetter(this);
        getMainActivity().hideBottomBar();
        setListners();
        autoCompleteLocationListner();
        setData();
        setCategoryRecyclerView();
        serviceHelper.enqueueCall(webService.getServices(), AllServicesCategories);

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
            txtName.setText(getProfileEnt.getUserName());
            txtEmail.setText(getProfileEnt.getEmail());
            if (getProfileEnt.getPhone() != null)
                txtPhone.setText(getProfileEnt.getPhone() + "");


            if (getProfileEnt.getDob() != null)
                txtBirthday.setText(getProfileEnt.getDob() + "");
            txtAutoComplete.setText(getProfileEnt.getLocation() + "");
            txtCertificateLicense.setText(getProfileEnt.getCertificateLicense() + "");
            txtTypeOfLicense.setText(getProfileEnt.getLicenseType() + "");
            txtCompanyName.setText(getProfileEnt.getCompanyName() + "");
            txtCompanyCategory.setText(getProfileEnt.getCompanyCategory() + "");
            txtInsuranceInformation.setText(getProfileEnt.getInsuranceInformation() + "");
            if (getProfileEnt.getAbout() != null)
                txtAboutUs.setText(getProfileEnt.getAbout() + "");

            if (getProfileEnt.getMyServices().size() > 0) {
                for (GetServicesEnt item : getProfileEnt.getMyServices()) {
                    categoriesIds.add(item.getId() + "");
                }
            }


            if (getProfileEnt.getInsuranceAvailable().equals("yes")) {
                toggleNotification.setChecked(true);
            } else {
                toggleNotification.setChecked(false);
            }

            if (locationLatLng == null) {
                locationLatLng = new LatLng(Double.parseDouble(getProfileEnt.getLatitude()), Double.parseDouble(getProfileEnt.getLongitude()));
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

            spnServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        //    if (!isSpinnerFirstTime) {
                        categoriesIds.add(categoriesCollection.get(position).getId().toString());
                        userCollections.add(genderCollection.get(position).toString());
                        lvCategories.notifyDataSetChanged();

                      /*  } else {
                            isSpinnerFirstTime = false;
                        }*/
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.edit_profile));
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
        } /*else if (txtEmail.getText() == null || (txtEmail.getText().toString().isEmpty()) ||
                (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches())) {
            txtEmail.setError(getString(R.string.enter_email));
            return false;
        } else if (txtPhone.getText() == null || (txtPhone.getText().toString().isEmpty())) {
            if (txtPhone.requestFocus()) {
                setEditTextFocus(txtPhone);
            }
            txtPhone.setError(getString(R.string.enter_MobileNum));
            return false;
        }*/ else if (txtBirthday.getText() == null || (txtBirthday.getText().toString().isEmpty())) {
            UIHelper.showShortToastInCenter(getDockActivity(),"Date of birth cannot be Empty");
           // txtBirthday.setError("Date of birth cannot be Empty");
            return false;
        } else if (txtAutoComplete.getText() == null || (txtAutoComplete.getText().toString().isEmpty())) {
            txtAutoComplete.setError("Location cannot be Empty");
            return false;
        } else if (txtCertificateLicense.getText() == null || (txtCertificateLicense.getText().toString().isEmpty())) {
            if (txtCertificateLicense.requestFocus()) {
                setEditTextFocus(txtCertificateLicense);
            }
            txtCertificateLicense.setError("Certificate License cannot be Empty");
            return false;
        } else if (txtTypeOfLicense.getText() == null || (txtTypeOfLicense.getText().toString().isEmpty())) {
            if (txtTypeOfLicense.requestFocus()) {
                setEditTextFocus(txtTypeOfLicense);
            }
            txtTypeOfLicense.setError("Type Of License cannot be Empty");
            return false;
        } else if (txtCompanyName.getText() == null || (txtCompanyName.getText().toString().isEmpty())) {
            if (txtCompanyName.requestFocus()) {
                setEditTextFocus(txtCompanyName);
            }
            txtCompanyName.setError("Company Name cannot be Empty");
            return false;
        } else if (txtCompanyCategory.getText() == null || (txtCompanyCategory.getText().toString().isEmpty())) {
            if (txtCompanyCategory.requestFocus()) {
                setEditTextFocus(txtCompanyCategory);
            }
            txtCompanyCategory.setError("Company Category cannot be Empty");
            return false;
        } else if (txtInsuranceInformation.getText() == null || (txtInsuranceInformation.getText().toString().isEmpty())) {
            if (txtInsuranceInformation.requestFocus()) {
                setEditTextFocus(txtInsuranceInformation);
            }
            txtInsuranceInformation.setError("Insurance Information cannot be Empty");
            return false;
        } /*else if (txtAboutUs.getText() == null || (txtAboutUs.getText().toString().isEmpty())) {

            txtAboutUs.setError("Aboutus cannot be Empty");
            return false;
        }*/ else if (resultIds == null || (resultIds.equals(""))) {
            UIHelper.showShortToastInCenter(getDockActivity(), "Select Categories");
            return false;
        } else {

            return true;
        }

    }

    @OnClick({R.id.btn_UploadProfile, R.id.btn_update, R.id.btn_male, R.id.btn_female, R.id.txt_birthday})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_UploadProfile:
                CameraHelper.uploadPhotoDialog(getMainActivity());
                break;
            case R.id.btn_update:
               /* if(getProfileEnt.getMyServices().size()>0){
                    for(GetServicesEnt item: getProfileEnt.getMyServices()){
                        categoriesIds.add(item.getId()+"");
                    }
                }*/
                resultIds = TextUtils.join(", ", categoriesIds);
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
        serviceHelper.enqueueCall(headerWebService.updateSpProfile(
                RequestBody.create(MediaType.parse("text/plain"), txtName.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtEmail.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtPhone.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), gender),
                RequestBody.create(MediaType.parse("text/plain"), (locationName != null) ? locationName : txtAutoComplete.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), (String.valueOf(locationLatLng.latitude))),
                RequestBody.create(MediaType.parse("text/plain"), String.valueOf(locationLatLng.longitude)),
                RequestBody.create(MediaType.parse("text/plain"), txtBirthday.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), resultIds),
                RequestBody.create(MediaType.parse("text/plain"), txtCertificateLicense.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtTypeOfLicense.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtCompanyName.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), txtCompanyCategory.getText().toString()),
                RequestBody.create(MediaType.parse("text/plain"), (toggleNotification.isChecked()) ? "yes" : "no"),
                RequestBody.create(MediaType.parse("text/plain"), txtInsuranceInformation.getText().toString()),
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
            profilePath = imagePath;
            /*Picasso.with(getDockActivity())
                    .load("file:///" + imagePath)
                    .into(ivProfileImage);*/
            imageLoader.displayImage("file:///" + imagePath, ivProfileImage);

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
                prefHelper.setUserType(getString(R.string.technician));
                prefHelper.set_TOKEN(entity.getToken());

                getDockActivity().popFragment();
                getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(), "SpProfileFragment");

                break;

            case AllServicesCategories:
                ArrayList<GetServicesEnt> ent = (ArrayList<GetServicesEnt>) result;
                setMechanicSpinner(ent);
                break;
        }
    }

    private void setMechanicSpinner(ArrayList<GetServicesEnt> ent) {
        genderCollection = new ArrayList<>();
        categoriesCollection = new ArrayList<>();
        categoriesCollection.add(new GetServicesEnt());
        categoriesCollection.addAll(ent);
        genderCollection.add("Select Category");
        for (GetServicesEnt item : ent) {
            genderCollection.add(item.getName() + "");
        }

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getDockActivity()
                , R.layout.spinner_item, genderCollection) {
            @Override
            public boolean isEnabled(int position) {
                return !(position == 0);
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };

        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnServices.setAdapter(genderAdapter);

    }

    private void setCategoryRecyclerView() {


        lvCategories.BindRecyclerView(new CategoryItemBinder(this, false), userCollections,
                new LinearLayoutManager(getDockActivity(), LinearLayoutManager.HORIZONTAL, false)
                , new DefaultItemAnimator());

        if (getProfileEnt.getMyServices().size() > 0) {
            for (GetServicesEnt item : getProfileEnt.getMyServices()) {
                userCollections.add(item.getName() + "");
            }
        }
        lvCategories.notifyDataSetChanged();
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        userCollections.remove(position);
        categoriesIds.remove(position);
        if (userCollections.size() <= 0) {
            spnServices.setSelection(0);
        }
        lvCategories.notifyDataSetChanged();
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }
}
