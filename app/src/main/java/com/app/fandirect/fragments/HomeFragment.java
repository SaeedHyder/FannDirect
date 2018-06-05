package com.app.fandirect.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.HomeGridEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.InternetHelper;
import com.app.fandirect.helpers.ShareIntentHelper;
import com.app.fandirect.helpers.TokenUpdater;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.HomeGridItemBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.ExpandableGridView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class HomeFragment extends BaseFragment implements RecyclerViewItemListener {


    @BindView(R.id.txt_searchBox)
    AnyTextView txtSearchBox;
    @BindView(R.id.ll_search_btn)
    LinearLayout llSearchBtn;
    @BindView(R.id.ll_searchBox)
    LinearLayout llSearchBox;
    Unbinder unbinder;
    @BindView(R.id.gv_home)
    ExpandableGridView gvHome;
    @BindView(R.id.btn_searchUser)
    Button btnSearchUser;
    @BindView(R.id.btn_searchService)
    Button btnSearchService;
    @BindView(R.id.iv_image)
    ImageView ivImage;

    private ArrayListAdapter<HomeGridEnt> adapter;
    private ArrayList<HomeGridEnt> collection;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<HomeGridEnt>(getDockActivity(), new HomeGridItemBinder(getDockActivity(), prefHelper, this));


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (InternetHelper.CheckInternetConectivityandShowToast(getDockActivity())) {
            TokenUpdater.getInstance().UpdateToken(getDockActivity(),
                    AppConstants.Device_Type,
                    FirebaseInstanceId.getInstance().getToken());
        }

        getMainActivity().hideBottomBar();

        prefHelper.setLoginStatus(true);
        setGridViewData();
        setListner();

    }

    private void setListner() {

        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (prefHelper.isUserSelected()) {
                        getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(), "UserProfileFragment");
                    } else {
                        getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(), "SpProfileFragment");
                    }

                } else if (position == 1) {
                    getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");

                } else if (position == 2) {
                    getDockActivity().replaceDockableFragment(MessageFragment.newInstance(), "MessageFragment");

                } else if (position == 3) {
                    getDockActivity().replaceDockableFragment(FannsFragment.newInstance(), "FannsFragment");

                } else if (position == 4) {
                    getDockActivity().replaceDockableFragment(FriendRequestFragment.newInstance(), "FriendRequestFragment");

                } else if (position == 5) {
                    getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");

                } else if (position == 6) {
                    getDockActivity().replaceDockableFragment(SettingFragment.newInstance(), "SettingFragment");

                } else if (position == 7) {
                    if (prefHelper.isUserSelected()) {
                        getDockActivity().replaceDockableFragment(WallPostsFragment.newInstance(), "WallPostsFragment");
                    } else {
                        getDockActivity().replaceDockableFragment(SpPromotionsPostFragment.newInstance(), "SpPromotionsPostFragment");
                    }

                } else if (position == 8) {
                    if (prefHelper.isUserSelected()) {
                        getDockActivity().replaceDockableFragment(ServiceHistoryUserFragment.newInstance(), "ServiceHistoryUserFragment");

                    } else {
                        getDockActivity().replaceDockableFragment(ServiceHistorySpFragment.newInstance(), "ServiceHistorySpFragment");
                    }

                }
            }
        });
    }


    private void setGridViewData() {

        collection = new ArrayList<>();
        collection.add(new HomeGridEnt(R.drawable.profile3, getString(R.string.profile)));
        collection.add(new HomeGridEnt(R.drawable.feeds2, getString(R.string.feeds)));
        collection.add(new HomeGridEnt(R.drawable.message3, getString(R.string.message)));
        collection.add(new HomeGridEnt(R.drawable.fanns, getString(R.string.fanns)));
        collection.add(new HomeGridEnt(R.drawable.fan_request1, getString(R.string.fanns_request)));
        collection.add(new HomeGridEnt(R.drawable.promotions, getString(R.string.promotions)));
        collection.add(new HomeGridEnt(R.drawable.settings, getString(R.string.settings)));
        if (prefHelper.isUserSelected()) {
            collection.add(new HomeGridEnt(R.drawable.posts, getString(R.string.posts)));
        } else {
            collection.add(new HomeGridEnt(R.drawable.posts, getString(R.string.promotions_posts)));
        }
        collection.add(new HomeGridEnt(R.drawable.services, getString(R.string.my_services)));


        adapter.clearList();
        gvHome.setAdapter(adapter);
        adapter.addAll(collection);
        adapter.notifyDataSetChanged();


    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showNotificationBell(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDockActivity().replaceDockableFragment(NotificationFragment.newInstance(), "NotificationFragment");
            }
        });


    }


    @OnClick({R.id.txt_searchBox, R.id.ll_searchBox, R.id.btn_searchUser, R.id.btn_searchService})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_searchBox:
                getDockActivity().replaceDockableFragment(HomeSearchFragment.newInstance(), "HomeSearchFragment");
                break;
            case R.id.ll_searchBox:
                getDockActivity().replaceDockableFragment(HomeSearchFragment.newInstance(), "HomeSearchFragment");
                break;
            case R.id.btn_searchUser:
                btnSearchUser.setTextColor(getResources().getColor(R.color.white));
                btnSearchUser.setBackgroundColor(getResources().getColor(R.color.app_blue_dark_2));

                btnSearchService.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSearchService.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case R.id.btn_searchService:
                btnSearchUser.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSearchUser.setBackgroundColor(getResources().getColor(R.color.white));

                btnSearchService.setTextColor(getResources().getColor(R.color.white));
                btnSearchService.setBackgroundColor(getResources().getColor(R.color.app_blue_dark_2));
                break;
        }
    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {

        HomeGridEnt entity = (HomeGridEnt) Ent;

        if (entity.getText().equals(getResources().getString(R.string.profile))) {
            if (prefHelper.isUserSelected()) {
                getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(), "UserProfileFragment");
            } else {
                getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(), "SpProfileFragment");
            }

        } else if (entity.getText().equals(getResources().getString(R.string.feeds))) {
            getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");

        } else if (entity.getText().equals(getResources().getString(R.string.message))) {
            getDockActivity().replaceDockableFragment(MessageFragment.newInstance(), "MessageFragment");

        } else if (entity.getText().equals(getResources().getString(R.string.fanns))) {
            getDockActivity().replaceDockableFragment(FannsFragment.newInstance(), "FannsFragment");

        } else if (entity.getText().equals(getResources().getString(R.string.fanns_request))) {
            getDockActivity().replaceDockableFragment(FriendRequestFragment.newInstance(), "FriendRequestFragment");

        } else if (entity.getText().equals(getResources().getString(R.string.promotions))) {
            getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");

        } else if (entity.getText().equals(getResources().getString(R.string.settings))) {
            getDockActivity().replaceDockableFragment(SettingFragment.newInstance(), "SettingFragment");

        } else if (entity.getText().equals(getResources().getString(R.string.posts))) {
            getDockActivity().replaceDockableFragment(WallPostsFragment.newInstance(), "WallPostsFragment");

        } else if (entity.getText().equals(getResources().getString(R.string.my_services))) {
            if (prefHelper.isUserSelected()) {
                getDockActivity().replaceDockableFragment(ServiceHistoryUserFragment.newInstance(), "ServiceHistoryUserFragment");

            } else {
                getDockActivity().replaceDockableFragment(ServiceHistorySpFragment.newInstance(), "ServiceHistorySpFragment");
            }

        }


    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }



}

