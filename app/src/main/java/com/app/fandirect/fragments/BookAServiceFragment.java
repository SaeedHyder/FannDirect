package com.app.fandirect.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.LocationModel;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.helpers.DatePickerHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.AutoCompleteLocation;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.location.places.Place;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.xw.repo.XEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.SendRequest;
import static com.app.fandirect.global.WebServiceConstants.getMyServices;

/**
 * Created by saeedhyder on 3/7/2018.
 */
public class BookAServiceFragment extends BaseFragment {
    @BindView(R.id.spn_categories)
    Spinner spnCategories;
    @BindView(R.id.txt_date)
    AnyTextView txtDate;
    @BindView(R.id.txt_time)
    AnyTextView txtTime;
    @BindView(R.id.txt_location)
    AnyEditTextView txtLocation;
    @BindView(R.id.txt_aboutUs)
    XEditText txtAboutUs;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.img_gps)
    ImageView imgGps;
    Unbinder unbinder;
    @BindView(R.id.autoComplete)
    AutoCompleteLocation autoComplete;
    private double locationLat = 0;
    private double locationLng = 0;
    private String location = "";
    private String selectedCategoryId = "";

    private Date DateSelected;
    private String DateSelectedString="";
    private Date TimeSelected;
    private String TimeSelectedString="";

    private static String UserIdKey = "UserIdKey";
    private String UserId;

    private InterstitialAd interstitialAd;


    public static BookAServiceFragment newInstance() {
        Bundle args = new Bundle();

        BookAServiceFragment fragment = new BookAServiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static BookAServiceFragment newInstance(String id) {
        Bundle args = new Bundle();
        args.putString(UserIdKey, id);
        BookAServiceFragment fragment = new BookAServiceFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        interstitialAd = new InterstitialAd(getDockActivity());
        interstitialAd.setAdUnitId(getDockActivity().getResources().getString(R.string.ad_unit_id_interstitial));
        final AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(adRequest);

        if (getArguments() != null) {
            UserId = getArguments().getString(UserIdKey);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_a_service, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
        getMainActivity().hideAdds();

        imgGps.setVisibility(View.GONE);
        requestLocationPermission();
        setListners();
        adMobListner();
        setLatLngOnAutoComplete();
        setGpsIcon();
        serviceHelper.enqueueCall(headerWebService.getMyServices(UserId), getMyServices);

    }

    private void adMobListner() {

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Toast.makeText(getDockActivity(), "Ad did not load", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });
    }

    private void setLatLngOnAutoComplete() {

        autoComplete.setAutoCompleteTextListener(new AutoCompleteLocation.AutoCompleteLocationListener() {
            @Override
            public void onTextClear() {

            }

            @Override
            public void onItemSelected(Place selectedPlace) {
                locationLat = selectedPlace.getLatLng().latitude;
                locationLng = selectedPlace.getLatLng().longitude;
                location = selectedPlace.getName() + "";

            }
        });
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


    private void initTimePicker(final TextView textView) {
        if (DateSelected != null) {
            TimePickerDialog dialog = new TimePickerDialog(getDockActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    c.set(year, month, day, hourOfDay, minute);
                    Calendar calcurrent = Calendar.getInstance();
                    calcurrent.setTime(date);
                    int currentHour = calcurrent.get(Calendar.HOUR_OF_DAY);
                    int currentMinute = calcurrent.get(Calendar.MINUTE);
                    calcurrent.set(Calendar.MINUTE, currentMinute + 30);
                    int addedHalfhour = calcurrent.get(Calendar.HOUR_OF_DAY);
                    int addedHalfhourMinute = calcurrent.get(Calendar.MINUTE);


                    if (DateHelper.isSameDay(DateSelected, date) && !DateHelper.isTimeAfter(currentHour, currentMinute, hourOfDay, minute)) {
                        UIHelper.showShortToastInCenter(getDockActivity(), getString(R.string.less_time_error));

                    } /*else if (DateHelper.isSameDay(DateSelected, date) && !DateHelper.isTimeAfter(addedHalfhour, addedHalfhourMinute, hourOfDay, minute)) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Select Time 30 min after current time");

                    }*/ else {

                        TimeSelected = c.getTime();
                        String preTime = new SimpleDateFormat("hh:mm a").format(c.getTime());
                        TimeSelectedString = new SimpleDateFormat("HH:mm").format(c.getTime());

                        textView.setVisibility(View.VISIBLE);
                        textView.setText(preTime);
                        textView.setPaintFlags(Typeface.BOLD);
                    }
                }
            }, DateSelected.getHours(), DateSelected.getMinutes(), false);

            dialog.show();

        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), getResources().getString(R.string.select_pickup_date_first));
        }
    }

    private void openFromTimePickerDialog(final AnyTextView txtTime) {

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getDockActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                c.set(year, month, day, selectedHour, selectedMinute);

                // String preTime = new SimpleDateFormat("HH:mm a").format(c.getTime());
                String preTime = new SimpleDateFormat("HH:mm").format(c.getTime());
                txtTime.setText(preTime);
                txtTime.setPaintFlags(Typeface.BOLD);

            }
        }, hour, minute, true);//Yes 24 hour timeChecker


        mTimePicker.setTitle("Select Time");
        mTimePicker.show();


    }


    private void initFromPickerValidated(final AnyTextView textView) {

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

                        Date dateSpecified = c.getTime();
                        if (dateSpecified.before(date)) {
                            UIHelper.showShortToastInCenter(getDockActivity(), getString(R.string.date_after_error));
                        } else {
                            DateSelected = dateSpecified;
                            String predate = new SimpleDateFormat("MM-dd-yyyy").format(c.getTime());
                            DateSelectedString= new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
                            textView.setText(predate);
                            textView.setPaintFlags(Typeface.BOLD);
                        }
                        /*Date dateSpecified = c.getTime();
                        DateSelected = dateSpecified;
                        String predate = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
                        textView.setText(predate);
                        textView.setPaintFlags(Typeface.BOLD);*/


                    }
                }, "PreferredDate", new Date());

        datePickerHelper.showDate();
    }


    private boolean isValidated() {
        if (selectedCategoryId == null || selectedCategoryId.equals("")) {
            UIHelper.showShortToastInCenter(getDockActivity(), "Select Category ");
            return false;
        } else if (txtDate.getText() == null || (txtDate.getText().toString().isEmpty())) {
            txtDate.setError("Enter Date ");
            return false;
        } else if (txtTime.getText().toString().isEmpty()) {
            txtTime.setError("Enter Time");
            return false;
        } else if (autoComplete.getText() == null || (autoComplete.getText().toString().isEmpty())) {
            // autoComplete.setError(getString(R.string.enter_Address));
            UIHelper.showShortToastInCenter(getDockActivity(), getString(R.string.enter_Address));
            return false;
        } else if (txtAboutUs.getText().toString().isEmpty()) {
            txtAboutUs.setError("Enter Message here");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.book_a_service));
    }


    @OnClick({R.id.txt_date, R.id.txt_time, R.id.btn_submit,R.id.img_gps})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_date:
                initFromPickerValidated(txtDate);
                break;
            case R.id.txt_time:
                initTimePicker(txtTime);
                break;
            case R.id.btn_submit:
                if (isValidated()) {
                    serviceHelper.enqueueCall(headerWebService.serviceRequest(UserId, selectedCategoryId, DateSelectedString,
                            TimeSelectedString, location, String.valueOf(locationLat),
                            String.valueOf(locationLng), txtAboutUs.getText().toString()), SendRequest);
                }
                break;
            case R.id.img_gps:
                UIHelper.hideSoftKeyboard(getDockActivity(), imgGps);
                requestLocationPermission();
                break;
        }
    }

    private void setGpsIcon() {

        autoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().equals("")) {
                    imgGps.setVisibility(View.VISIBLE);
                } else {
                    imgGps.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getLocation(AutoCompleteTextView textView) {
        if (getMainActivity()!=null && getMainActivity().statusCheck()) {
            LocationModel locationModel = getMainActivity().getMyCurrentLocation();
            if (locationModel != null) {
                textView.setText(locationModel.getAddress());
               // textView.setSelection(textView.getText().length());
                locationLat = locationModel.getLat();
                locationLng = locationModel.getLng();
                location =locationModel.getAddress()+"";

            } else {
                getLocation(autoComplete);
            }
        }
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
                            getLocation(autoComplete);
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
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getMyServices:
                ArrayList<GetServicesEnt> entity = (ArrayList<GetServicesEnt>) result;

                if (entity != null && entity.size() > 0) {
                    setSpinnerData(entity);
                }
                break;

            case SendRequest:
                UIHelper.showShortToastInCenter(getDockActivity(), "Request sent successfully");
                getMainActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                break;
        }
    }

    private void setSpinnerData(ArrayList<GetServicesEnt> entity) {

        final ArrayList<String> Collection = new ArrayList<>();
        final ArrayList<String> CollectionIds = new ArrayList<>();

        selectedCategoryId = entity.get(0).getId() + "";

        for (GetServicesEnt item : entity) {
            Collection.add(item.getName());
            CollectionIds.add(item.getId() + "");
        }

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getDockActivity()
                , R.layout.spinner_item, Collection); /*{
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
        };*/

        // ArrayAdapter<String> carWashType = new ArrayAdapter<String>(getDockActivity(), android.R.layout.simple_spinner_item, CarWashTypeArray);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategories.setAdapter(genderAdapter);

        spnCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //  if (position != 0) {
                  /*  userCollections.add(Collection.get(position).toString());
                    lvCategories.notifyDataSetChanged();*/
                // }
                selectedCategoryId = CollectionIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getMainActivity().showAdds();
    }
}
