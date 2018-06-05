package com.app.fandirect.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.fandirect.R;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.ForgotPassword;

/**
 * Created by saeedhyder on 3/9/2018.
 */
public class EmailVerificationFragment extends BaseFragment {
    @BindView(R.id.txt_email)
    AnyEditTextView txtEmail;
    @BindView(R.id.btn_login)
    Button btnLogin;
    Unbinder unbinder;

    public static EmailVerificationFragment newInstance() {
        Bundle args = new Bundle();

        EmailVerificationFragment fragment = new EmailVerificationFragment();
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
        View view = inflater.inflate(R.layout.fragment_email_verification, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();

        txtEmail.getText().clear();
    }

    private boolean isValidated() {
        if (txtEmail.getText() == null || (txtEmail.getText().toString().isEmpty()) ||
                !(Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches())) {
            txtEmail.setError(getString(R.string.enter_valid_email));
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
        titleBar.setSubHeading(getString(R.string.email_verification));
    }



    @OnClick(R.id.btn_login)
    public void onViewClicked() {
        if (isValidated())
        {
            serviceHelper.enqueueCall(webService.forgotPassword(txtEmail.getText().toString()),ForgotPassword);
        }

    }
    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case ForgotPassword:
                UserEnt entity = (UserEnt) result;
                prefHelper.putUser(entity);
                prefHelper.set_TOKEN(entity.getToken());

                getDockActivity().replaceDockableFragment(CodeVerificationFragment.newInstance(true,entity.getId()+"",entity.getEmail()), "CodeVerificationFragment");


                break;
        }
    }
}
