package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.postKey;
import static com.app.fandirect.global.WebServiceConstants.postPrivacySetting;
import static com.app.fandirect.global.WebServiceConstants.profilePrivacySetting;

/**
 * Created by saeedhyder on 3/12/2018.
 */
public class PricavySettingSpFragment extends BaseFragment {
    @BindView(R.id.btn_tickEveryOne)
    LinearLayout btnTickEveryOne;
    @BindView(R.id.btn_tickNetworkOnly)
    LinearLayout btnTickNetworkOnly;
    @BindView(R.id.btn_tickFannsOnly)
    LinearLayout btnTickFannsOnly;
    @BindView(R.id.btn_tickPrivate)
    LinearLayout btnTickPrivate;
    Unbinder unbinder;
    @BindView(R.id.ll_everyOne)
    LinearLayout llEveryOne;
    @BindView(R.id.ll_networkOnly)
    LinearLayout llNetworkOnly;
    @BindView(R.id.ll_fannsOnly)
    LinearLayout llFannsOnly;
    @BindView(R.id.ll_private)
    LinearLayout llPrivate;

    public static PricavySettingSpFragment newInstance() {
        Bundle args = new Bundle();

        PricavySettingSpFragment fragment = new PricavySettingSpFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy_setting_sp, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();

        setStatus();
    }

    private void setStatus() {


        if (prefHelper.getUser().getPostStatus().equals("0")) {
            btnTickEveryOne.setVisibility(View.VISIBLE);
            btnTickFannsOnly.setVisibility(View.GONE);
            btnTickPrivate.setVisibility(View.GONE);
        } else if (prefHelper.getUser().getPostStatus().equals("1")) {
            btnTickEveryOne.setVisibility(View.GONE);
            btnTickFannsOnly.setVisibility(View.GONE);
            btnTickPrivate.setVisibility(View.VISIBLE);
        } else {
            btnTickEveryOne.setVisibility(View.GONE);
            btnTickFannsOnly.setVisibility(View.VISIBLE);
            btnTickPrivate.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.privacy_setting));
    }



    @OnClick({R.id.btn_tickEveryOne, R.id.btn_tickNetworkOnly, R.id.btn_tickFannsOnly, R.id.btn_tickPrivate,R.id.ll_everyOne, R.id.ll_networkOnly, R.id.ll_fannsOnly, R.id.ll_private})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_tickEveryOne:
                break;
            case R.id.btn_tickNetworkOnly:
                break;
            case R.id.btn_tickFannsOnly:
                break;
            case R.id.btn_tickPrivate:
                break;

            case R.id.ll_everyOne:
                btnTickEveryOne.setVisibility(View.VISIBLE);
                btnTickNetworkOnly.setVisibility(View.GONE);
                btnTickFannsOnly.setVisibility(View.GONE);
                btnTickPrivate.setVisibility(View.GONE);
                UserEnt PublicsEnt=prefHelper.getUser();
                PublicsEnt.setPostStatus("0");
                prefHelper.putUser(PublicsEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(postKey, 0), postPrivacySetting);
                break;
            case R.id.ll_networkOnly:
                btnTickEveryOne.setVisibility(View.GONE);
                btnTickNetworkOnly.setVisibility(View.VISIBLE);
                btnTickFannsOnly.setVisibility(View.GONE);
                btnTickPrivate.setVisibility(View.GONE);
                break;
            case R.id.ll_fannsOnly:
                btnTickEveryOne.setVisibility(View.GONE);
                btnTickNetworkOnly.setVisibility(View.GONE);
                btnTickFannsOnly.setVisibility(View.VISIBLE);
                btnTickPrivate.setVisibility(View.GONE);
                UserEnt FannsEnt=prefHelper.getUser();
                FannsEnt.setPostStatus("2");
                prefHelper.putUser(FannsEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(postKey, 2), postPrivacySetting);
                break;
            case R.id.ll_private:
                btnTickEveryOne.setVisibility(View.GONE);
                btnTickNetworkOnly.setVisibility(View.GONE);
                btnTickFannsOnly.setVisibility(View.GONE);
                btnTickPrivate.setVisibility(View.VISIBLE);
                UserEnt PrivateEnt=prefHelper.getUser();
                PrivateEnt.setPostStatus("1");
                prefHelper.putUser(PrivateEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(postKey, 1), postPrivacySetting);
                break;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case postPrivacySetting:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;


        }
    }


}
