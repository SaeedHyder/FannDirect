package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.fandirect.R;
import com.app.fandirect.entities.ServiceHistoryEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.ServiceHistory;
import static com.app.fandirect.global.WebServiceConstants.markRequestService;

/**
 * Created by saeedhyder on 3/13/2018.
 */
public class ServiceDspViewFragment extends BaseFragment {
    @BindView(R.id.txt_service)
    AnyTextView txtService;
    @BindView(R.id.txt_location)
    AnyTextView txtLocation;
    @BindView(R.id.txt_date)
    AnyTextView txtDate;
    @BindView(R.id.txt_time)
    AnyTextView txtTime;
    @BindView(R.id.txt_message)
    AnyTextView txtMessage;
    @BindView(R.id.btn_accept)
    Button btnAccept;
    @BindView(R.id.btn_reject)
    Button btnReject;
    Unbinder unbinder;

    private static String ServiceHistoryEntKey = "ServiceHistoryEntKey";
    private String ServiceHistoryString;
    private ServiceHistoryEnt serviceHistoryEnt;

    public static ServiceDspViewFragment newInstance() {
        Bundle args = new Bundle();

        ServiceDspViewFragment fragment = new ServiceDspViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ServiceDspViewFragment newInstance(ServiceHistoryEnt ent) {
        Bundle args = new Bundle();
        args.putString(ServiceHistoryEntKey, new Gson().toJson(ent));
        ServiceDspViewFragment fragment = new ServiceDspViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ServiceHistoryString = getArguments().getString(ServiceHistoryEntKey);
        }
        if (ServiceHistoryString != null) {
            serviceHistoryEnt = new Gson().fromJson(ServiceHistoryString, ServiceHistoryEnt.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dsp_view, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
        setData();
    }

    private void setData() {

        txtService.setText(serviceHistoryEnt.getServiceDetail().getName()+"");
        txtLocation.setText(serviceHistoryEnt.getLocation()+"");
        txtMessage.setText(serviceHistoryEnt.getDescription()+"");
        txtDate.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss","dd-MMM-yyyy",serviceHistoryEnt.getDatetime()+""));
        txtTime.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss","hh:mm a",serviceHistoryEnt.getDatetime()+""));
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.showTitleLogo();
    }


    @OnClick({R.id.btn_accept, R.id.btn_reject})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                if(serviceHistoryEnt.getSenderId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                    serviceHelper.enqueueCall(headerWebService.markRequestService(serviceHistoryEnt.getReceiverId() + "", serviceHistoryEnt.getId() + "", AppConstants.accepted), markRequestService);
                }else{
                    serviceHelper.enqueueCall(headerWebService.markRequestService(serviceHistoryEnt.getSenderId() + "", serviceHistoryEnt.getId() + "", AppConstants.accepted), markRequestService);

                }
                break;
            case R.id.btn_reject:
                 if(serviceHistoryEnt.getSenderId().equals(String.valueOf(prefHelper.getUser().getId()))) {
                    serviceHelper.enqueueCall(headerWebService.markRequestService(serviceHistoryEnt.getReceiverId() + "", serviceHistoryEnt.getId() + "", AppConstants.rejected), markRequestService);
                }else{
                    serviceHelper.enqueueCall(headerWebService.markRequestService(serviceHistoryEnt.getSenderId() + "", serviceHistoryEnt.getId() + "", AppConstants.rejected), markRequestService);

                }
                break;
        }
    }



    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case markRequestService:
                UIHelper.showShortToastInCenter(getDockActivity(),message);
                getDockActivity().popFragment();
                getDockActivity().replaceDockableFragment(ServiceHistorySpFragment.newInstance(),"ServiceHistorySpFragment");
                break;

        }
    }
}
