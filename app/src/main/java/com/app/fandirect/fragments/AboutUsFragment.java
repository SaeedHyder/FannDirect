package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fandirect.R;
import com.app.fandirect.entities.CmsEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.about_us;
import static com.app.fandirect.global.WebServiceConstants.terms_condition;

/**
 * Created by saeedhyder on 3/9/2018.
 */
public class AboutUsFragment extends BaseFragment {
    @BindView(R.id.txt_aboutUs)
    AnyTextView txtAboutUs;
    Unbinder unbinder;

    public static AboutUsFragment newInstance() {
        Bundle args = new Bundle();

        AboutUsFragment fragment = new AboutUsFragment();
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
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();

        serviceHelper.enqueueCall(webService.cms("about_us"),about_us);
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getString(R.string.about));
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case about_us:
                CmsEnt ent=(CmsEnt)result;
                txtAboutUs.setText(ent.getText()+"");
                break;




        }
    }


}
