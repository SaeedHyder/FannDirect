package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.app.fandirect.R;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.PushOnOff;
import static com.app.fandirect.global.WebServiceConstants.UserLogout;

/**
 * Created by saeedhyder on 3/12/2018.
 */
public class SettingFragment extends BaseFragment {
    @BindView(R.id.txt_changePassword)
    AnyTextView txtChangePassword;
    @BindView(R.id.txt_privacy_settings)
    AnyTextView txtPrivacySettings;
    @BindView(R.id.toggleNotification)
    ToggleButton cbNotifications;
    @BindView(R.id.txt_about_us)
    AnyTextView txtAboutUs;
    @BindView(R.id.txt_logout)
    AnyTextView txtLogout;
    Unbinder unbinder;

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();

        SettingFragment fragment = new SettingFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().showBottomBar(AppConstants.search);
        setListners();

    }

    private void setListners() {

        cbNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                serviceHelper.enqueueCall(headerWebService.pushOnOff(isChecked ? 1 : 0), PushOnOff);
            }
        });
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.settings));
    }


    @OnClick({R.id.txt_changePassword, R.id.txt_privacy_settings, R.id.txt_about_us, R.id.txt_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_changePassword:
                getDockActivity().replaceDockableFragment(ChangePasswordFragment.newInstance(), "ChangePasswordFragment");
                break;
            case R.id.txt_privacy_settings:
                if (prefHelper.isUserSelected()) {
                    getDockActivity().replaceDockableFragment(PrivacySettingUserFragment.newInstance(), "PrivacySettingUserFragment");
                } else {
                    getDockActivity().replaceDockableFragment(PricavySettingSpFragment.newInstance(), "PricavySettingSpFragment");
                }

                break;
            case R.id.txt_about_us:
                getDockActivity().replaceDockableFragment(AboutUsFragment.newInstance(), "AboutUsFragment");
                break;
            case R.id.txt_logout:
                final DialogHelper dialoge = new DialogHelper(getDockActivity());
                dialoge.initlogout(R.layout.logout_dialog, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialoge.hideDialog();
                        serviceHelper.enqueueCall(headerWebService.logout(FirebaseInstanceId.getInstance().getToken()),UserLogout);


                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialoge.hideDialog();

                    }
                });
                dialoge.showDialog();
                break;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case UserLogout:
                UIHelper.showShortToastInCenter(getDockActivity(),message);
                prefHelper.setLoginStatus(false);
                getMainActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");
                break;

            case PushOnOff:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;


        }
    }
}
