package com.app.fandirect.fragments;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fandirect.R;
import com.app.fandirect.entities.ServiceHistoryEnt;
import com.app.fandirect.entities.ServiceHistoryMainEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.DatePickerHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.ServiceHistoryBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.ServiceHistory;
import static com.app.fandirect.global.WebServiceConstants.feedback;
import static com.app.fandirect.global.WebServiceConstants.markRequestService;

/**
 * Created by saeedhyder on 3/13/2018.
 */
public class ServiceHistoryUserFragment extends BaseFragment implements RecyclerViewItemListener {
    @BindView(R.id.txt_date_from)
    AnyTextView txtDateFrom;
    @BindView(R.id.txt_date_to)
    AnyTextView txtDateTo;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_service_history)
    ListView lvServiceHistory;
    Unbinder unbinder;
    @BindView(R.id.ll_search_btn)
    LinearLayout llSearchBtn;
    @BindView(R.id.btnDay)
    AnyTextView btnDay;
    @BindView(R.id.btnWeek)
    AnyTextView btnWeek;
    @BindView(R.id.btnMonth)
    AnyTextView btnMonth;
    @BindView(R.id.btnYear)
    AnyTextView btnYear;
    private Date FromDateSelected;
    private Date ToDateSelected;
    private String FromDateSelectedString = "";
    private String ToDateSelectedString = "";

    private ArrayListAdapter<ServiceHistoryEnt> adapter;
    private ArrayList<ServiceHistoryEnt> collection;
    private InterstitialAd interstitialAd;

    private static boolean isCompleted = false;
    private Date date, fromDate;
    private String currentDate, fromDateString;

    public static ServiceHistoryUserFragment newInstance() {
        Bundle args = new Bundle();
        isCompleted = false;
        ServiceHistoryUserFragment fragment = new ServiceHistoryUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ServiceHistoryUserFragment newInstance(boolean completedKey) {
        Bundle args = new Bundle();
        isCompleted = completedKey;
        ServiceHistoryUserFragment fragment = new ServiceHistoryUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        interstitialAd = new InterstitialAd(getDockActivity());
        interstitialAd.setAdUnitId(getDockActivity().getResources().getString(R.string.ad_unit_id_interstitial));
        final AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(adRequest);
        adapter = new ArrayListAdapter<ServiceHistoryEnt>(getDockActivity(), new ServiceHistoryBinder(getDockActivity(), prefHelper, this, isCompleted));
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().showBottomBar(AppConstants.search);
        adMobListner();

        date=new Date();
        fromDate = new Date();
       /* if(isCompleted){
            date = new Date();
            Calendar calendarMonth = Calendar.getInstance();
            calendarMonth.add(Calendar.MONTH, -1);
            fromDate = calendarMonth.getTime();
        }else{
            fromDate = new Date();
            Calendar calendarMonth = Calendar.getInstance();
            calendarMonth.add(Calendar.MONTH, +1);
            date = calendarMonth.getTime();
        }*/

      /*  date = new Date();
        Calendar calendarYear = Calendar.getInstance();
        calendarYear.add(Calendar.MONTH, +1);
        date = calendarYear.getTime();

        fromDate = new Date();*/

        currentDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
        ToDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(date);

        fromDateString = new SimpleDateFormat("MM-dd-yyyy").format(fromDate);
        FromDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);

        FromDateSelected = new Date();
        txtDateTo.setText(currentDate);
        txtDateFrom.setText(fromDateString);

        serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(FromDateSelectedString, ToDateSelectedString), ServiceHistory);
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

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case ServiceHistory:
                if (getTitleBar() != null) {
                    getTitleBar().hideNotificationBell();
                }
                ServiceHistoryMainEnt ent = (ServiceHistoryMainEnt) result;
                if (isCompleted) {
                    setListViewData(ent.getCompleted());
                } else {
                    setListViewData(ent.getPending());
                }

                break;

            case markRequestService:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getMainActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                break;

            case feedback:
                UIHelper.showShortToastInCenter(getDockActivity(), "Rating submitted successfully");
                serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(FromDateSelectedString, ToDateSelectedString), ServiceHistory);
                break;

        }
    }

    private void setListViewData(ArrayList<ServiceHistoryEnt> ent) {

        collection = new ArrayList<>();
        collection.addAll(ent);

        if (collection.size() > 0) {
            lvServiceHistory.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvServiceHistory.setVisibility(View.GONE);
            if (isCompleted) {
                txtNoData.setText(R.string.no_history_record);
            } else {
                txtNoData.setText(R.string.no_inprogress_record);
            }
            txtNoData.setVisibility(View.VISIBLE);
        }

        adapter.clearList();
        lvServiceHistory.setAdapter(adapter);
        adapter.addAll(collection);


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
                        if (dateSpecified.before(date)) {
                            UIHelper.showShortToastInCenter(getDockActivity(), getString(R.string.date_before_error));
                        } else {
                            //   DateSelected = dateSpecified;
                            String predate = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());

                            textView.setText(predate);
                            textView.setPaintFlags(Typeface.BOLD);
                        }

                    }
                }, "PreferredDate");

        datePickerHelper.showDate();
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
                        FromDateSelected = dateSpecified;

                        if (dateSpecified.after(date)) {
                            UIHelper.showShortToastInCenter(getDockActivity(), "You can not select date in future");
                        } else {

                            String predate = new SimpleDateFormat("MM-dd-yyyy").format(c.getTime());
                            FromDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
                            textView.setText(predate);
                            textView.setPaintFlags(Typeface.BOLD);
                        }
                    }
                }, "PreferredDate", new Date());

        datePickerHelper.showDate();
    }

    private void initToPickerValidated(final AnyTextView textView) {

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
                        ToDateSelected = dateSpecified;

                        if (ToDateSelected.before(FromDateSelected)) {
                            UIHelper.showShortToastInCenter(getDockActivity(), "Selected Date should not be lesser than the previously selected date");
                        } else {
                            String predate = new SimpleDateFormat("MM-dd-yyyy").format(c.getTime());
                            ToDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
                            textView.setText(predate);
                            textView.setPaintFlags(Typeface.BOLD);
                        }
                    }
                }, "PreferredDate", new Date());

        datePickerHelper.showDate();
    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideTitleBar();
    }


    @OnClick({R.id.txt_date_from, R.id.txt_date_to, R.id.ll_search_btn, R.id.btnDay, R.id.btnWeek, R.id.btnMonth, R.id.btnYear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_date_from:
                initFromPickerValidated(txtDateFrom);
                break;
            case R.id.txt_date_to:
                if (FromDateSelected != null) {
                    initToPickerValidated(txtDateTo);
                } else {
                    UIHelper.showShortToastInCenter(getDockActivity(), "Select From Date First");
                }
                break;

            case R.id.ll_search_btn:
                //    serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(txtDateFrom.getText().toString(), txtDateTo.getText().toString()), ServiceHistory);
                serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(FromDateSelectedString, ToDateSelectedString), ServiceHistory);
                break;

            case R.id.btnDay:
                date = new Date();
                fromDate = new Date();

                currentDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
                ToDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(date);

                fromDateString = new SimpleDateFormat("MM-dd-yyyy").format(fromDate);
                FromDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);

                txtDateTo.setText(currentDate);
                txtDateFrom.setText(fromDateString);

                serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(FromDateSelectedString, ToDateSelectedString), ServiceHistory);

                btnDay.setBackgroundColor(getResources().getColor(R.color.app_blue));
                btnDay.setTextColor(getResources().getColor(R.color.white));

                btnWeek.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnWeek.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnMonth.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnMonth.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnYear.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnYear.setTextColor(getResources().getColor(R.color.app_dark_gray_2));


                break;
            case R.id.btnWeek:

                if(isCompleted){
                    date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, -7);
                    fromDate = calendar.getTime();
                }else{
                    fromDate = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, +7);
                    date = calendar.getTime();
                }


                currentDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
                ToDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(date);

                fromDateString = new SimpleDateFormat("MM-dd-yyyy").format(fromDate);
                FromDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);

                txtDateTo.setText(currentDate);
                txtDateFrom.setText(fromDateString);

                serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(FromDateSelectedString, ToDateSelectedString), ServiceHistory);

                btnDay.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnDay.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnWeek.setBackgroundColor(getResources().getColor(R.color.app_blue));
                btnWeek.setTextColor(getResources().getColor(R.color.white));

                btnMonth.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnMonth.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnYear.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnYear.setTextColor(getResources().getColor(R.color.app_dark_gray_2));
                break;
            case R.id.btnMonth:

                if(isCompleted){
                    date = new Date();
                    Calendar calendarMonth = Calendar.getInstance();
                    calendarMonth.add(Calendar.MONTH, -1);
                    fromDate = calendarMonth.getTime();
                }else{
                    fromDate = new Date();
                    Calendar calendarMonth = Calendar.getInstance();
                    calendarMonth.add(Calendar.MONTH, +1);
                    date = calendarMonth.getTime();
                }

                currentDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
                ToDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(date);

                fromDateString = new SimpleDateFormat("MM-dd-yyyy").format(fromDate);
                FromDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);

                txtDateTo.setText(currentDate);
                txtDateFrom.setText(fromDateString);

                serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(FromDateSelectedString, ToDateSelectedString), ServiceHistory);

                btnDay.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnDay.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnWeek.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnWeek.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnMonth.setBackgroundColor(getResources().getColor(R.color.app_blue));
                btnMonth.setTextColor(getResources().getColor(R.color.white));

                btnYear.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnYear.setTextColor(getResources().getColor(R.color.app_dark_gray_2));
                break;
            case R.id.btnYear:

                if(isCompleted){
                    date = new Date();
                    Calendar calendarYear = Calendar.getInstance();
                    calendarYear.add(Calendar.YEAR, -1);
                    fromDate = calendarYear.getTime();
                }else{
                    fromDate = new Date();
                    Calendar calendarYear = Calendar.getInstance();
                    calendarYear.add(Calendar.YEAR, +1);
                    date = calendarYear.getTime();
                }



                currentDate = new SimpleDateFormat("MM-dd-yyyy").format(date);
                ToDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(date);

                fromDateString = new SimpleDateFormat("MM-dd-yyyy").format(fromDate);
                FromDateSelectedString = new SimpleDateFormat("yyyy-MM-dd").format(fromDate);

                txtDateTo.setText(currentDate);
                txtDateFrom.setText(fromDateString);

                serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(FromDateSelectedString, ToDateSelectedString), ServiceHistory);

                btnDay.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnDay.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnWeek.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnWeek.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnMonth.setBackgroundColor(getResources().getColor(R.color.transparent));
                btnMonth.setTextColor(getResources().getColor(R.color.app_dark_gray_2));

                btnYear.setBackgroundColor(getResources().getColor(R.color.app_blue));
                btnYear.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        // getMainActivity().notImplemented();
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

        final ServiceHistoryEnt data = (ServiceHistoryEnt) Ent;
        final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
        dialogHelper.initRating(R.layout.submit_rating_dialoge, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackService(data.getId() + "", data.getSenderId(), data.getReceiverId(), dialogHelper.getRatingTextView().getText().toString(), dialogHelper.getRatingScore(), dialogHelper);
            }
        });
        dialogHelper.showDialog();

    }

    private void feedbackService(String requestId, String senderId, String receiverId, String text, Float ratingScore, DialogHelper dialogHelper) {

        String userId = "";
        if (senderId.equals(String.valueOf(prefHelper.getUser().getId()))) {
            userId = receiverId;
        } else {
            userId = senderId;
        }

        if (text != null && !text.trim().equals("")) {
            dialogHelper.hideDialog();
            serviceHelper.enqueueCall(headerWebService.feedback(userId, requestId, String.valueOf(Math.round(ratingScore)), text), feedback);
        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), "Write Feedback to proceed");
        }


    }



    @Override
    public void onResume() {
        super.onResume();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


}
