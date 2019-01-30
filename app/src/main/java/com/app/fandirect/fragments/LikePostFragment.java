package com.app.fandirect.fragments;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fandirect.R;
import com.app.fandirect.entities.LikeDetailEnt;
import com.app.fandirect.entities.ReviewsEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.binders.LikesBinder;
import com.app.fandirect.ui.binders.ReviewsBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.WebServiceConstants.LikeDetails;

public class LikePostFragment extends BaseFragment implements RecyclerViewItemListener {
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.rv_likes)
    CustomRecyclerView rvLikes;
    Unbinder unbinder;

    private static String postid="";

    public static LikePostFragment newInstance(String id) {
        Bundle args = new Bundle();
        postid=id;
        LikePostFragment fragment = new LikePostFragment();
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
        View view = inflater.inflate(R.layout.fragment_like_post, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(postid!=null && !postid.equals("")) {
            serviceHelper.enqueueCall(headerWebService.getLikesDetail(postid), LikeDetails);
        }




    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag){
            case LikeDetails:
                ArrayList<LikeDetailEnt> collection=(ArrayList<LikeDetailEnt>)result;
                bindData(collection);
                break;
        }
    }

    private void bindData(ArrayList<LikeDetailEnt> collection) {

        if (collection != null && collection.size() > 0) {

            txtNoData.setVisibility(View.GONE);
            rvLikes.setVisibility(View.VISIBLE);

            rvLikes.BindRecyclerView(new LikesBinder(this), collection,
                    new LinearLayoutManager(getDockActivity(), LinearLayoutManager.VERTICAL, false)
                    , new DefaultItemAnimator());
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            rvLikes.setVisibility(View.GONE);
        }


    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.setSubHeading(getDockActivity().getResources().getString(R.string.likes));
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {

        LikeDetailEnt entity=(LikeDetailEnt)Ent;

        if (entity.getUserDetail()!=null && entity.getUserDetail().getRoleId().equals(UserRoleId)) {
            getDockActivity().addDockableFragment(UserProfileFragment.newInstance(entity.getUserDetail().getId()+""), "UserProfileFragment");
        } else if(entity.getUserDetail()!=null) {
            getDockActivity().addDockableFragment(SpProfileFragment.newInstance(entity.getUserDetail().getId()+""), "SpProfileFragment");
        }
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }
}
