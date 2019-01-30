package com.app.fandirect.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.PinEntryEditText;
import com.app.fandirect.ui.views.TitleBar;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.ResendCode;
import static com.app.fandirect.global.WebServiceConstants.VerifyCode;
import static com.app.fandirect.global.WebServiceConstants.VerifyCodeForgot;

/**
 * Created by saeedhyder on 3/9/2018.
 */
public class CodeVerificationFragment extends BaseFragment {
    @BindView(R.id.txt_pin_entry)
    PinEntryEditText txtPinEntry;
    @BindView(R.id.tv_counter)
    AnyTextView tvCounter;
    @BindView(R.id.btn_login)
    Button btnLogin;
    Unbinder unbinder;
    CountDownTimer timer;

    private static String isForgotKey = "isForgotPass";
    @BindView(R.id.resend_code_btn)
    AnyTextView resendCodeBtn;
    @BindView(R.id.ll_timer)
    LinearLayout llTimer;
    private boolean isForgot = false;
    private static String userIdKey = "userIdKey";
    private static String emailIdKey = "emailIdKey";

    public static CodeVerificationFragment newInstance() {
        Bundle args = new Bundle();

        CodeVerificationFragment fragment = new CodeVerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static CodeVerificationFragment newInstance(boolean isForgotPass, String userId, String email) {
        Bundle args = new Bundle();
        args.putBoolean(isForgotKey, isForgotPass);
        args.putString(userIdKey, userId);
        args.putString(emailIdKey, email);
        CodeVerificationFragment fragment = new CodeVerificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isForgot = getArguments().getBoolean(isForgotKey);
            userIdKey = getArguments().getString(userIdKey);
            emailIdKey = getArguments().getString(emailIdKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_verification, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
        txtPinEntry.getText().clear();
        counter();

    }

    public void counter() {
        timer = new CountDownTimer(120000, 1000) {

            public void onTick(long millisUntilFinished) {

                String text = String.format(Locale.getDefault(), "%2d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                if (tvCounter != null) {
                    tvCounter.setText(text + "");

                }
            }

            public void onFinish() {
                if (tvCounter != null){
                   llTimer.setVisibility(View.GONE);
                   resendCodeBtn.setVisibility(View.VISIBLE);
                }
            }
        }.start();

    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.email_verification));
    }

    @OnClick({R.id.resend_code_btn, R.id.btn_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.resend_code_btn:

                serviceHelper.enqueueCall(webService.resendCode(emailIdKey), ResendCode);

                break;
            case R.id.btn_login:
                if (txtPinEntry.getText().toString().length() < 5) {
                    UIHelper.showShortToastInCenter(getDockActivity(), getString(R.string.correct_verification_code));
                } else {
                    if (isForgot) {
                        serviceHelper.enqueueCall(webService.VerifyCode(userIdKey, txtPinEntry.getText().toString()), VerifyCodeForgot);

                    } else {
                        //
                        serviceHelper.enqueueCall(webService.VerifyCode(userIdKey, txtPinEntry.getText().toString()), VerifyCode);
                    }
                }
                break;
        }
    }


    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case VerifyCode:
                if (timer != null) {
                    timer.cancel();
                }

                UserEnt entity = (UserEnt) result;
                prefHelper.putUser(entity);
                prefHelper.set_TOKEN(entity.getToken());
                getMainActivity().popBackStackTillEntry(0);
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");



                break;

            case VerifyCodeForgot:
                if (timer != null) {
                    timer.cancel();
                }

                UserEnt ent = (UserEnt) result;
                prefHelper.putUser(ent);
                prefHelper.set_TOKEN(ent.getToken());
                //   UIHelper.showShortToastInCenter(getDockActivity(),message);
                getDockActivity().replaceDockableFragment(ForgotPasswordFragment.newInstance(ent.getId() + ""), "ForgotPasswordFragment");

                break;

            case ResendCode:
                counter();
                llTimer.setVisibility(View.VISIBLE);
                resendCodeBtn.setVisibility(View.GONE);
                UIHelper.showShortToastInCenter(getDockActivity(), message);

                break;


        }
    }

    @Override
    public void ResponseFailure(String tag) {
        super.ResponseFailure(tag);

        switch (tag) {

            case VerifyCode:
                txtPinEntry.getText().clear();
                break;

            case VerifyCodeForgot:
                txtPinEntry.getText().clear();
                break;
        }
    }


}
