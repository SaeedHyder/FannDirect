package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.app.fandirect.R;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.ShareIntentHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.PostClicksInterface;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.ReportPostIntetface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.FeedsBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.ExpandedListView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.allMyPromotionsPosts;
import static com.app.fandirect.global.WebServiceConstants.deletePost;
import static com.app.fandirect.global.WebServiceConstants.favoritePost;
import static com.app.fandirect.global.WebServiceConstants.getAllPosts;
import static com.app.fandirect.global.WebServiceConstants.postLike;
import static com.app.fandirect.global.WebServiceConstants.reportPost;
import static com.app.fandirect.global.WebServiceConstants.reportUser;

/**
 * Created by saeedhyder on 5/10/2018.
 */
public class SpPromotionsPostFragment extends BaseFragment implements RecyclerViewItemListener ,PostClicksInterface,ReportPostIntetface{
    @BindView(R.id.txt_name)
    AnyTextView txtName;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_promotions_posts)
    ExpandedListView lvPromotionsPosts;
    Unbinder unbinder;
    private ArrayListAdapter<Post> Adapter;
    private ArrayList<Post> Collection;

    public static SpPromotionsPostFragment newInstance() {
        Bundle args = new Bundle();

        SpPromotionsPostFragment fragment = new SpPromotionsPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Adapter = new ArrayListAdapter<Post>(getDockActivity(), new FeedsBinder(getDockActivity(), prefHelper, this,this,this));
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sp_promotions, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().showBottomBar(AppConstants.search);

        serviceHelper.enqueueCall(headerWebService.getMyPromotionsPosts(),allMyPromotionsPosts);

    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case allMyPromotionsPosts:
                ArrayList<Post> entity=(ArrayList<Post>)result;
                setPromotionsPostListData(entity);
                break;

            case postLike:
                UIHelper.showShortToastInCenter(getDockActivity(),message);
                break;

            case favoritePost:
                UIHelper.showShortToastInCenter(getDockActivity(),message);
                break;

            case deletePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                serviceHelper.enqueueCall(headerWebService.getMyPromotionsPosts(),allMyPromotionsPosts);
                break;

            case reportPost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case reportUser:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

        }
    }

    private void setPromotionsPostListData(ArrayList<Post> entity) {

        if(entity.size()>0){
            lvPromotionsPosts.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        }
        else {
            lvPromotionsPosts.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

        Adapter.clearList();
        lvPromotionsPosts.setAdapter(Adapter);
        Adapter.addAll(entity);

    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.showTitleLogo();
        titleBar.showNotificationBell(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDockActivity().replaceDockableFragment(NotificationFragment.newInstance(), "NotificationFragment");
            }
        });
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        getMainActivity().notImplemented();
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    @Override
    public void like(Post entity, int position) {
        serviceHelper.enqueueCall(headerWebService.likePost(entity.getId() + ""), postLike);

    }

    @Override
    public void favorite(Post entity, int position) {
        serviceHelper.enqueueCall(headerWebService.favoritePost(entity.getId() + ""), favoritePost);
    }

    @Override
    public void share(Post entity, int position) {
        getDockActivity().onLoadingStarted();
        if (entity.getImageUrl() != null && !entity.getImageUrl().equals("") && entity.getDescription() != null && !entity.getDescription().equals("")) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), entity.getDescription());
        } else if (entity.getImageUrl() != null && entity.getDescription() == null) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), "");
        } else if (entity.getImageUrl() == null && entity.getDescription() != null) {
            ShareIntentHelper.shareTextIntent(getDockActivity(), entity.getDescription());
        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), "Description is not avaliable");
        }
    }

    @Override
    public void comment(Post entity, int position) {
        getDockActivity().replaceDockableFragment(CommentFragment.newInstance(entity.getId()+""),"CommentFragment");
    }

    @Override
    public void onResume() {
        super.onResume();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void reportPost(Post entity, int position) {
        serviceHelper.enqueueCall(headerWebService.reportPost(entity.getId(), entity.getUserId()), reportPost);
    }

    @Override
    public void reportUser(Post entity, int position) {
        serviceHelper.enqueueCall(headerWebService.reportUser(entity.getUserId()), reportUser);

    }

    @Override
    public void DeletePost(Post entity, int position) {
        serviceHelper.enqueueCall(headerWebService.deletePost(entity.getId()), deletePost);
    }
}
