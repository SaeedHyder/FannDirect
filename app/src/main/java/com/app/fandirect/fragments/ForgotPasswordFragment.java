package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.ResetForgotPassword;
import static com.app.fandirect.global.WebServiceConstants.UserLogin;

/**
 * Created by saeedhyder on 3/9/2018.
 */
public class ForgotPasswordFragment extends BaseFragment {
    @BindView(R.id.backButton)
    LinearLayout backButton;
    @BindView(R.id.txt_password)
    AnyEditTextView txtPassword;
    @BindView(R.id.txt_confpassword)
    AnyEditTextView txtConfpassword;
    @BindView(R.id.btn_reset_password)
    Button btnResetPassword;
    Unbinder unbinder;

    private static String userIdKey = "userIdKey";

    public static ForgotPasswordFragment newInstance() {
        Bundle args = new Bundle();

        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ForgotPasswordFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(userIdKey, userId);
        ForgotPasswordFragment fragment = new ForgotPasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userIdKey = getArguments().getString(userIdKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
    }

    private boolean isvalidate() {
        if (txtPassword.getText() == null || (txtPassword.getText().toString().isEmpty())) {
            txtPassword.setError(getString(R.string.enter_password));
            return false;
        } else if (txtPassword.getText().toString().length() < 6) {
            txtPassword.setError(getString(R.string.passwordLength));
            return false;
        } else if (txtConfpassword.getText() == null || (txtConfpassword.getText().toString().isEmpty()) || txtConfpassword.getText().toString().length() < 6) {
            txtConfpassword.setError(getString(R.string.enter_password));
            return false;
        } else if (!txtConfpassword.getText().toString().equals(txtPassword.getText().toString())) {
            txtConfpassword.setError(getString(R.string.conform_password_error));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideTitleBar();
    }


    @OnClick(R.id.btn_reset_password)
    public void onViewClicked() {

    }

    @OnClick({R.id.backButton, R.id.btn_reset_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                getMainActivity().popFragment();
                break;
            case R.id.btn_reset_password:
                if (isvalidate()) {
                    serviceHelper.enqueueCall(webService.resetForgotPassword(txtPassword.getText().toString(), userIdKey), ResetForgotPassword);
                }

                break;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case ResetForgotPassword:

                UIHelper.showShortToastInCenter(getDockActivity(),message);
                getMainActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");

                break;
        }
    }
}
