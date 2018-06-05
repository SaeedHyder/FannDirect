package com.app.fandirect.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
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
import android.widget.TimePicker;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.helpers.DatePickerHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.AutoCompleteLocation;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.location.places.Place;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    AnyEditTextView txtAboutUs;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    Unbinder unbinder;
    @BindView(R.id.autoComplete)
    AutoCompleteLocation autoComplete;
    private double locationLat = 0;
    private double locationLng = 0;
    private String location = "";
    private String selectedCategoryId = "";

    private Date DateSelected;
    private Date TimeSelected;

    private static String UserIdKey = "UserIdKey";
    private String UserId;


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

        setListners();
        setLatLngOnAutoComplete();
        serviceHelper.enqueueCall(headerWebService.getMyServices(UserId), getMyServices);

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
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day, hourOfDay, minute + 15);
                        String preTime = new SimpleDateFormat("HH:mm").format(c.getTime());
                        textView.setVisibility(View.VISIBLE);
                        textView.setText(preTime);
                        textView.setPaintFlags(Typeface.BOLD);
                    }
                }
            }, DateSelected.getHours(), DateSelected.getMinutes(), true);

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
                            String predate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());

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
        if (selectedCategoryId==null || selectedCategoryId.equals("")) {
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


    @OnClick({R.id.txt_date, R.id.txt_time, R.id.btn_submit})
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
                    serviceHelper.enqueueCall(headerWebService.serviceRequest(UserId, selectedCategoryId, txtDate.getText().toString(),
                            txtTime.getText().toString(), location, String.valueOf(locationLat),
                            String.valueOf(locationLng), txtAboutUs.getText().toString()), SendRequest);
                }

                break;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getMyServices:
                ArrayList<GetServicesEnt> entity = (ArrayList<GetServicesEnt>) result;
                setSpinnerData(entity);
                break;

            case SendRequest:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getMainActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                break;
        }
    }

    private void setSpinnerData(ArrayList<GetServicesEnt> entity) {

        final ArrayList<String> Collection = new ArrayList<>();
        final ArrayList<String> CollectionIds = new ArrayList<>();

        selectedCategoryId=entity.get(0).getId()+"";

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
}
