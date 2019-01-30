package com.app.fandirect.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fandirect.R;
import com.app.fandirect.entities.ReviewsEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.binders.ReviewsBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.WebServiceConstants.GetReviews;

public class ReviewsFragment extends BaseFragment implements RecyclerViewItemListener {
    private static String userId = "";
    @BindView(R.id.rv_reviews)
    CustomRecyclerView rvReviews;
    Unbinder unbinder;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;

    public static ReviewsFragment newInstance() {
        Bundle args = new Bundle();

        ReviewsFragment fragment = new ReviewsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static ReviewsFragment newInstance(String UserIdKey) {
        Bundle args = new Bundle();
        userId = UserIdKey;
        ReviewsFragment fragment = new ReviewsFragment();
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
        View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serviceHelper.enqueueCall(headerWebService.getReviews(userId), GetReviews);

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {
            case GetReviews:
                ArrayList<ReviewsEnt> collection = (ArrayList<ReviewsEnt>) result;
                bindData(collection);
                break;
        }
    }

    private void bindData(ArrayList<ReviewsEnt> collection) {

        if (collection != null && collection.size() > 0) {

            txtNoData.setVisibility(View.GONE);
            rvReviews.setVisibility(View.VISIBLE);

            rvReviews.BindRecyclerView(new ReviewsBinder(this,true), collection,
                    new LinearLayoutManager(getDockActivity(), LinearLayoutManager.VERTICAL, false)
                    , new DefaultItemAnimator());
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            rvReviews.setVisibility(View.GONE);
        }


    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getDockActivity().getResources().getString(R.string.reviews));
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {

        ReviewsEnt entity=(ReviewsEnt)Ent;

        if (entity.getSenderDetail().getRoleId().equals(UserRoleId)) {
            getDockActivity().addDockableFragment(UserProfileFragment.newInstance(entity.getSenderDetail().getId()+""), "UserProfileFragment");
        } else {
            getDockActivity().addDockableFragment(SpProfileFragment.newInstance(entity.getSenderDetail().getId()+""), "SpProfileFragment");
        }

    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }


}
