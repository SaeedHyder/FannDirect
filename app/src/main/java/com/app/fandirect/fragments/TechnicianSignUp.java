package com.app.fandirect.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.binders.CategoryItemBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.AutoCompleteLocation;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.AllServicesCategories;
import static com.app.fandirect.global.WebServiceConstants.SuggestService;
import static com.app.fandirect.global.WebServiceConstants.registerSP;

/**
 * Created by saeedhyder on 2/26/2018.
 */
public class TechnicianSignUp extends BaseFragment implements RecyclerViewItemListener {
    @BindView(R.id.spn_gender)
    Spinner spnGender;
    @BindView(R.id.txt_name)
    AnyEditTextView txtName;
    @BindView(R.id.txt_email)
    AnyEditTextView txtEmail;
    @BindView(R.id.txt_phone)
    AnyEditTextView txtPhone;
    @BindView(R.id.txt_city)
    AnyEditTextView txtCity;
    @BindView(R.id.txt_state)
    AnyEditTextView txtState;
    @BindView(R.id.btn_signup)
    Button btnSignup;
    @BindView(R.id.txt_password)
    AnyEditTextView txtPassword;
    @BindView(R.id.txt_confpassword)
    AnyEditTextView txtConfpassword;
    Unbinder unbinder;
    @BindView(R.id.lv_categories)
    CustomRecyclerView lvCategories;
    @BindView(R.id.txt_autoComplete)
    AutoCompleteLocation txtAutoComplete;
    @BindView(R.id.cb_term)
    CheckBox cbTerm;
    @BindView(R.id.btn_terms_and_condition)
    AnyTextView btnTermsAndCondition;
    @BindView(R.id.txt_new_service)
    AnyTextView txtNewService;
    @BindView(R.id.ll_password)
    LinearLayout llPassword;
    private ArrayList<String> userCollections = new ArrayList<>();
    private ArrayList<String> categoriesIds = new ArrayList<>();
    private ArrayList<String> genderCollection;
    private ArrayList<GetServicesEnt> categoriesCollection;
    private String result = "";
    private boolean isSpinnerFirstTime = true;
    private String locationName = "";
    private LatLng locationLatLng = new LatLng(0, 0);
    private DialogHelper dialogHelper;

    public static TechnicianSignUp newInstance() {
        Bundle args = new Bundle();

        TechnicianSignUp fragment = new TechnicianSignUp();
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
        View view = inflater.inflate(R.layout.fragment_technician_signup, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
        txtNewService.setText(Html.fromHtml("<a href=''>" + getResources().getString(R.string.don_t_see_your_profession_or_service) + "</a>"));
        btnTermsAndCondition.setText(Html.fromHtml("<a href=''>" + getResources().getString(R.string.by_registering_you_agree_to_terms_condition) + "</a>"));
        serviceHelper.enqueueCall(webService.getServices(), AllServicesCategories);

        setCategoryRecyclerView();
        autoCompleteLocationListner();

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

    private void setMechanicSpinner(ArrayList<GetServicesEnt> ent) {
        genderCollection = new ArrayList<>();
        categoriesCollection = new ArrayList<>();
        categoriesCollection.add(new GetServicesEnt());
        categoriesCollection.addAll(ent);
        genderCollection.add("Select Category");
        for (GetServicesEnt item : ent) {
            genderCollection.add(item.getName() + "");
        }

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getDockActivity()
                , R.layout.spinner_item, genderCollection) {
            @Override
            public boolean isEnabled(int position) {
                return !(position == 0);
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };

        // ArrayAdapter<String> carWashType = new ArrayAdapter<String>(getDockActivity(), android.R.layout.simple_spinner_item, CarWashTypeArray);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGender.setAdapter(genderAdapter);

        spnGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    //   if (!isSpinnerFirstTime) {
                    categoriesIds.add(categoriesCollection.get(position).getId().toString());
                    userCollections.add(genderCollection.get(position).toString());
                    lvCategories.notifyDataSetChanged();

                   /* } else {
                        isSpinnerFirstTime = false;
                    }*/
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setCategoryRecyclerView() {

        lvCategories.BindRecyclerView(new CategoryItemBinder(this, true), userCollections,
                new LinearLayoutManager(getDockActivity(), LinearLayoutManager.HORIZONTAL, false)
                , new DefaultItemAnimator());
    }

    private boolean isValidated() {
        if (result == null || (result.equals(""))) {
            UIHelper.showShortToastInCenter(getDockActivity(), "Select Categories");
            return false;
        } else if (txtName.getText() == null || (txtName.getText().toString().isEmpty())) {
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
        } else if (txtPhone.getText() == null || (txtPhone.getText().toString().isEmpty())) {
            if (txtPhone.requestFocus()) {
                setEditTextFocus(txtPhone);
            }
            txtPhone.setError(getString(R.string.enter_MobileNum));
            return false;
        } else if (txtAutoComplete.getText() == null || (txtAutoComplete.getText().toString().isEmpty())) {
            txtAutoComplete.setError("Location cannot be Empty");
            return false;
        } else if (txtCity.getText() == null || (txtCity.getText().toString().isEmpty())) {
            if (txtCity.requestFocus()) {
                setEditTextFocus(txtCity);
            }
            txtCity.setError("City cannot be Empty");
            return false;
        } else if (txtState.getText() == null || (txtState.getText().toString().isEmpty())) {
            if (txtState.requestFocus()) {
                setEditTextFocus(txtState);
            }
            txtState.setError("State cannot be Empty");
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
            UIHelper.showShortToastInCenter(getDockActivity(), getDockActivity().getResources().getString(R.string.select_term_cond));
            return false;
        } else {
            return true;
        }

    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showBackButton();
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {

        userCollections.remove(position);
        categoriesIds.remove(position);
        if (userCollections.size() <= 0) {
            spnGender.setSelection(0);
        }
        lvCategories.notifyDataSetChanged();


    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case registerSP:
                UserEnt entity = (UserEnt) result;
                prefHelper.putUser(entity);
                prefHelper.setUserType(getString(R.string.technician));
                prefHelper.set_TOKEN(entity.getToken());

                getDockActivity().replaceDockableFragment(CodeVerificationFragment.newInstance(false, entity.getId() + "", entity.getEmail()), "CodeVerificationFragment");
                break;

            case AllServicesCategories:
                ArrayList<GetServicesEnt> ent = (ArrayList<GetServicesEnt>) result;

                Collections.sort(ent, new Comparator<GetServicesEnt>() {
                    @Override
                    public int compare(GetServicesEnt getServicesEnt, GetServicesEnt t1) {
                        return getServicesEnt.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
                    }
                });

                setMechanicSpinner(ent);
                break;

            case SuggestService:
                UIHelper.showShortToastInCenter(getDockActivity(), getResString(R.string.suggestion_submitted));

                break;
        }
    }


    @OnClick({R.id.btn_terms_and_condition, R.id.btn_signup, R.id.txt_new_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_terms_and_condition:
                getDockActivity().addDockableFragment(TermsAndConditionFragment.newInstance(), "TermsAndConditionFragment");
                break;
            case R.id.txt_new_service:
                final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                dialogHelper.addService(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (dialogHelper.getSuggestionText() != null && !dialogHelper.getSuggestionText().getText().toString().trim().equals("")) {
                            serviceHelper.enqueueCall(webService.suggestService(dialogHelper.getSuggestionText().getText().toString()), SuggestService);
                            getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                            dialogHelper.hideDialog();
                        } else {
                            dialogHelper.getSuggestionText().setError(getResString(R.string.write_service_error));
                        }

                    }
                });
                dialogHelper.showDialog();

                break;
            case R.id.btn_signup:
                result = TextUtils.join(", ", categoriesIds);
                if (isValidated()) {

                    serviceHelper.enqueueCall(webService.registerSp(
                            txtName.getText().toString(),
                            txtEmail.getText().toString(),
                            txtPhone.getText().toString(),
                            txtPassword.getText().toString(),
                            txtCity.getText().toString(),
                            txtState.getText().toString(),
                            String.valueOf(locationLatLng.latitude),
                            String.valueOf(locationLatLng.longitude),
                            locationName,
                            result,
                            AppConstants.Device_Type,
                            FirebaseInstanceId.getInstance().getToken()
                    ), registerSP);
                }
                break;
        }
    }

}
