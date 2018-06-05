package com.app.fandirect.fragments;

import android.app.DatePickerDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.ServiceHistoryEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.DatePickerHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.ServiceHistoryBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.ServiceHistory;
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
    private Date FromDateSelected;
    private Date ToDateSelected;

    private ArrayListAdapter<ServiceHistoryEnt> adapter;
    private ArrayList<ServiceHistoryEnt> collection;


    public static ServiceHistoryUserFragment newInstance() {
        Bundle args = new Bundle();

        ServiceHistoryUserFragment fragment = new ServiceHistoryUserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<ServiceHistoryEnt>(getDockActivity(), new ServiceHistoryBinder(getDockActivity(), prefHelper, this));
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

        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd"); //this format changeable
        String currentDate = dateFormatter.format(date);

        FromDateSelected=new Date();
        txtDateTo.setText(currentDate);
        txtDateFrom.setText(currentDate);

        serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(currentDate, currentDate), ServiceHistory);
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case ServiceHistory:
                ArrayList<ServiceHistoryEnt> ent = (ArrayList<ServiceHistoryEnt>) result;
                setListViewData(ent);
                break;

            case markRequestService:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getMainActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
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

                            String predate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
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
                            String predate = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
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
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.service_history));
    }


    @OnClick({R.id.txt_date_from, R.id.txt_date_to,R.id.ll_search_btn})
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
                serviceHelper.enqueueCall(headerWebService.getUserServiceRequestHistory(txtDateFrom.getText().toString(), txtDateTo.getText().toString()), ServiceHistory);
                break;
        }
    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        // getMainActivity().notImplemented();
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }


}
