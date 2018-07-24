package com.app.fandirect.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.app.fandirect.R;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.Device_Type;
import static com.app.fandirect.global.AppConstants.SProviderRoleId;
import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.WebServiceConstants.SPLogin;
import static com.app.fandirect.global.WebServiceConstants.UserLogin;


public class LoginFragment extends BaseFragment {


    @BindView(R.id.txt_email)
    AnyEditTextView txtEmail;
    @BindView(R.id.txt_password)
    AnyEditTextView txtPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_signup)
    AnyTextView btnSignup;
    Unbinder unbinder;
    @BindView(R.id.btn_forgotPassword)
    AnyTextView btnForgotPassword;
    @BindView(R.id.btn_user)
    RadioButton btnUser;
    @BindView(R.id.btn_tech)
    RadioButton btnTech;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        getMainActivity().hideBottomBar();

        txtEmail.getText().clear();
        txtPassword.getText().clear();

        prefHelper.setUserType(getString(R.string.user));

        if (prefHelper.isUserSelected()) {
            btnUser.setChecked(true);
        } else {
            btnTech.setChecked(true);
        }


    }

    private boolean isValidated() {
        if (txtEmail.getText() == null || (txtEmail.getText().toString().isEmpty()) ||
                !(Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches())) {
            txtEmail.setError(getString(R.string.enter_valid_email));
            return false;
        } else if (txtPassword.getText().toString().isEmpty()) {
            txtPassword.setError(getString(R.string.enter_password));
            return false;
        } else if (txtPassword.getText().toString().length() < 6) {
            txtPassword.setError(getString(R.string.passwordLength));
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        // TODO Auto-generated method stub
        super.setTitleBar(titleBar);
        titleBar.hideTitleBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }


    @OnClick({R.id.btn_login, R.id.btn_signup, R.id.btn_forgotPassword, R.id.btn_user, R.id.btn_tech})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (isValidated()) {
                    if (prefHelper.isUserSelected()) {

                        serviceHelper.enqueueCall(webService.loginUser(txtEmail.getText().toString(), txtPassword.getText().toString(), UserRoleId, Device_Type, FirebaseInstanceId.getInstance().getToken()), UserLogin);
                    } else {
                        serviceHelper.enqueueCall(webService.loginUser(txtEmail.getText().toString(), txtPassword.getText().toString(), SProviderRoleId, Device_Type, FirebaseInstanceId.getInstance().getToken()), SPLogin);

                    }
                }
                break;
            case R.id.btn_signup:
                getDockActivity().replaceDockableFragment(UserSignupFragment.newInstance(), "UserSignupFragment");
                break;

            case R.id.btn_forgotPassword:
                getDockActivity().replaceDockableFragment(EmailVerificationFragment.newInstance(), "EmailVerificationFragment");
                break;
            case R.id.btn_user:
                prefHelper.setUserType(getString(R.string.user));
                break;
            case R.id.btn_tech:
                prefHelper.setUserType(getString(R.string.technician));
                break;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case UserLogin:
                UserEnt entity = (UserEnt) result;
                prefHelper.putUser(entity);
                prefHelper.setUserType(getString(R.string.user));
                prefHelper.set_TOKEN(entity.getToken());

                if (entity.getStatus().equals("1")) {
                    if (entity.getIsVerified().equals("1")) {
                        getMainActivity().popBackStackTillEntry(0);
                        getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                    } else {
                        getDockActivity().replaceDockableFragment(CodeVerificationFragment.newInstance(false, entity.getId() + "", entity.getEmail()), "CodeVerificationFragment");
                    }
                } else {
                    final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                    dialogHelper.alertDialoge(R.layout.alert_dialoge, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogHelper.hideDialog();
                        }
                    }, getDockActivity().getResources().getString(R.string.blocked_by_admin));
                    dialogHelper.showDialog();
                }

                break;

            case SPLogin:
                UserEnt ent = (UserEnt) result;
                prefHelper.putUser(ent);
                prefHelper.setUserType(getString(R.string.technician));
                prefHelper.set_TOKEN(ent.getToken());

                if (ent.getStatus().equals("1")) {
                    if (ent.getIsVerified().equals("1")) {
                        getMainActivity().popBackStackTillEntry(0);
                        getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                    } else {
                        getDockActivity().replaceDockableFragment(CodeVerificationFragment.newInstance(false, ent.getId() + "", ent.getEmail()), "CodeVerificationFragment");
                    }
                } else {
                    final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                    dialogHelper.alertDialoge(R.layout.alert_dialoge, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogHelper.hideDialog();
                        }
                    }, getDockActivity().getResources().getString(R.string.blocked_by_admin));

                    dialogHelper.showDialog();
                }

                break;


        }
    }
}
