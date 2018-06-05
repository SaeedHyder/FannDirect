package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.FannsEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.ProfileKey;
import static com.app.fandirect.global.AppConstants.postKey;
import static com.app.fandirect.global.WebServiceConstants.postPrivacySetting;
import static com.app.fandirect.global.WebServiceConstants.profilePrivacySetting;

/**
 * Created by saeedhyder on 3/12/2018.
 */
public class PrivacySettingUserFragment extends BaseFragment {
    @BindView(R.id.btn_tickPublic)
    LinearLayout btnTickPublic;
    @BindView(R.id.btn_tickFannsOnly)
    LinearLayout btnTickFannsOnly;
    @BindView(R.id.btn_tickPrivate)
    LinearLayout btnTickPrivate;
    @BindView(R.id.btn_yes)
    LinearLayout btnYes;
    @BindView(R.id.btn_tickNo)
    LinearLayout btnTickNo;
    Unbinder unbinder;
    @BindView(R.id.ll_public)
    LinearLayout llPublic;
    @BindView(R.id.ll_fanns)
    LinearLayout llFanns;
    @BindView(R.id.ll_private)
    LinearLayout llPrivate;
    @BindView(R.id.ll_yes)
    LinearLayout llYes;
    @BindView(R.id.ll_no)
    LinearLayout llNo;

    private int post = 0;
    private int profile = 0;

    public static PrivacySettingUserFragment newInstance() {
        Bundle args = new Bundle();

        PrivacySettingUserFragment fragment = new PrivacySettingUserFragment();
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
        View view = inflater.inflate(R.layout.fragment_privacy_setting_user, container, false);
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

        if (prefHelper.getUser().getProfileStaus().equals("1")) {
            btnYes.setVisibility(View.VISIBLE);
            btnTickNo.setVisibility(View.GONE);
        } else {
            btnYes.setVisibility(View.GONE);
            btnTickNo.setVisibility(View.VISIBLE);
        }

        if (prefHelper.getUser().getPostStatus().equals("0")) {
            btnTickPublic.setVisibility(View.VISIBLE);
            btnTickFannsOnly.setVisibility(View.GONE);
            btnTickPrivate.setVisibility(View.GONE);
        } else if (prefHelper.getUser().getPostStatus().equals("1")) {
            btnTickPublic.setVisibility(View.GONE);
            btnTickFannsOnly.setVisibility(View.GONE);
            btnTickPrivate.setVisibility(View.VISIBLE);
        } else {
            btnTickPublic.setVisibility(View.GONE);
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


    @OnClick({R.id.ll_public, R.id.ll_fanns, R.id.ll_private, R.id.ll_yes, R.id.ll_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_public:
                btnTickPublic.setVisibility(View.VISIBLE);
                btnTickFannsOnly.setVisibility(View.GONE);
                btnTickPrivate.setVisibility(View.GONE);
                UserEnt PublicsEnt=prefHelper.getUser();
                PublicsEnt.setPostStatus("0");
                prefHelper.putUser(PublicsEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(postKey, 0), postPrivacySetting);
                break;
            case R.id.ll_fanns:
                btnTickPublic.setVisibility(View.GONE);
                btnTickFannsOnly.setVisibility(View.VISIBLE);
                btnTickPrivate.setVisibility(View.GONE);
                UserEnt FannsEnt=prefHelper.getUser();
                FannsEnt.setPostStatus("2");
                prefHelper.putUser(FannsEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(postKey, 2), postPrivacySetting);
                break;
            case R.id.ll_private:
                btnTickPublic.setVisibility(View.GONE);
                btnTickFannsOnly.setVisibility(View.GONE);
                btnTickPrivate.setVisibility(View.VISIBLE);
                UserEnt PrivateEnt=prefHelper.getUser();
                PrivateEnt.setPostStatus("1");
                prefHelper.putUser(PrivateEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(postKey, 1), postPrivacySetting);
                break;
            case R.id.ll_yes:
                btnYes.setVisibility(View.VISIBLE);
                btnTickNo.setVisibility(View.GONE);
                UserEnt YesEnt=prefHelper.getUser();
                YesEnt.setProfileStaus("1");
                prefHelper.putUser(YesEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(ProfileKey, 1), profilePrivacySetting);
                break;
            case R.id.ll_no:
                btnYes.setVisibility(View.GONE);
                btnTickNo.setVisibility(View.VISIBLE);
                UserEnt NoEnt=prefHelper.getUser();
                NoEnt.setProfileStaus("0");
                prefHelper.putUser(NoEnt);
                serviceHelper.enqueueCall(headerWebService.changePrivacySetting(ProfileKey, 0), profilePrivacySetting);
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

            case profilePrivacySetting:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

        }
    }
}
