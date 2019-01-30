package com.app.fandirect.fragments;

import android.os.Bundle;
import android.text.Html;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.AutoCompleteLocation;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.Device_Type;
import static com.app.fandirect.global.WebServiceConstants.RegisterUser;

/**
 * Created by saeedhyder on 2/26/2018.
 */
public class UserSignupFragment extends BaseFragment {
    @BindView(R.id.backButton)
    LinearLayout backButton;
    @BindView(R.id.txt_name)
    AnyEditTextView txtName;
    @BindView(R.id.txt_email)
    AnyEditTextView txtEmail;
    @BindView(R.id.txt_password)
    AnyEditTextView txtPassword;
    @BindView(R.id.txt_confpassword)
    AnyEditTextView txtConfpassword;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.btn_signup_service_provider)
    Button btnSignupServiceProvider;
    @BindView(R.id.btn_terms_and_condition)
    AnyTextView btnTermsAndCondition;
    @BindView(R.id.txt_autoComplete)
    AutoCompleteLocation txtAutoComplete;
    Unbinder unbinder;
    @BindView(R.id.cb_term)
    CheckBox cbTerm;
    private String locationName = "";
    private LatLng locationLatLng = new LatLng(0, 0);

    public static UserSignupFragment newInstance() {
        Bundle args = new Bundle();

        UserSignupFragment fragment = new UserSignupFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_signup, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
        autoCompleteLocationListner();
        btnTermsAndCondition.setText(Html.fromHtml("<a href=''>"+getResources().getString(R.string.by_registering_you_agree_to_terms_condition)+"</a>"));
    }

    private void autoCompleteLocationListner() {

        txtAutoComplete.setAutoCompleteTextListener(new AutoCompleteLocation.AutoCompleteLocationListener() {
            @Override
            public void onTextClear() {

            }

            @Override
            public void onItemSelected(Place selectedPlace) {
                locationName = selectedPlace.getName().toString();
                locationLatLng = selectedPlace.getLatLng();
            }
        });
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideTitleBar();
    }


    private boolean isValidated() {
        if (txtName.getText() == null || (txtName.getText().toString().isEmpty())) {
            if (txtName.requestFocus()) {
                setEditTextFocus(txtName);
            }
            txtName.setError(getString(R.string.enter_FullName));
            return false;
        } else if (txtEmail.getText() == null || (txtEmail.getText().toString().isEmpty()) ||
                (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches())) {
            if (txtEmail.requestFocus()) {
                setEditTextFocus(txtEmail);
            }
            txtEmail.setError(getString(R.string.enter_email));
            return false;
        } else if (txtAutoComplete.getText() == null || (txtAutoComplete.getText().toString().isEmpty())) {
            txtAutoComplete.setError("Location cannot be Empty");
            return false;
        } else if (txtPassword.getText().toString().isEmpty()) {
            txtPassword.setError(getString(R.string.enter_password));
            if (txtPassword.requestFocus()) {
                setEditTextFocus(txtPassword);
            }
            return false;
        } else if (txtPassword.getText().toString().length() < 6) {
            txtPassword.setError(getString(R.string.enter_valid_password));
            if (txtPassword.requestFocus()) {
                setEditTextFocus(txtPassword);
            }
            return false;
        } else if (!txtConfpassword.getText().toString().equals(txtPassword.getText().toString())) {
            txtConfpassword.setError(getString(R.string.confirm_password_error));
            if (txtConfpassword.requestFocus()) {
                setEditTextFocus(txtConfpassword);
            }
            return false;
        } else if (!cbTerm.isChecked()) {
            UIHelper.showShortToastInCenter(getDockActivity(),getDockActivity().getResources().getString(R.string.select_term_cond));
            return false;
        } else {
            return true;
        }

    }

    @OnClick({R.id.backButton, R.id.btn_signup, R.id.btn_signup_service_provider, R.id.btn_terms_and_condition})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backButton:
                getMainActivity().popFragment();
                break;
            case R.id.btn_signup:
                if (isValidated())
                    // getDockActivity().replaceDockableFragment(CodeVerificationFragment.newInstance(false,txtEmail.getText().toString()), "CodeVerificationFragment");
                    serviceHelper.enqueueCall(webService.registerUser(txtName.getText().toString(),
                            txtEmail.getText().toString(),
                            String.valueOf(locationLatLng.latitude),
                            String.valueOf(locationLatLng.longitude),
                            (locationName != null) ? locationName : txtAutoComplete.getText().toString(),
                            txtPassword.getText().toString(), Device_Type, FirebaseInstanceId.getInstance().getToken()), RegisterUser);

                break;
            case R.id.btn_signup_service_provider:
                getDockActivity().replaceDockableFragment(TechnicianSignUp.newInstance(), "TechnicianSignUp");
                break;
            case R.id.btn_terms_and_condition:
                getDockActivity().replaceDockableFragment(TermsAndConditionFragment.newInstance(), "TermsAndConditionFragment");
                break;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case RegisterUser:
                UserEnt entity = (UserEnt) result;
                prefHelper.putUser(entity);
                prefHelper.setUserType(getString(R.string.user));
                prefHelper.set_TOKEN(entity.getToken());
                /*TokenUpdater.getInstance().UpdateToken(getDockActivity(),
                        AppConstants.Device_Type,
                        FirebaseInstanceId.getInstance().getToken());*/

                getDockActivity().replaceDockableFragment(CodeVerificationFragment.newInstance(false, entity.getId() + "", entity.getEmail()), "CodeVerificationFragment");


                break;
        }
    }


}
