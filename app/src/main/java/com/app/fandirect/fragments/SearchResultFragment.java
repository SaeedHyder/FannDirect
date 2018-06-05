package com.app.fandirect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.app.fandirect.R;
import com.app.fandirect.entities.SearchResultEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.userProfileClick;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.SearchResultBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by saeedhyder on 3/7/2018.
 */
public class SearchResultFragment extends BaseFragment implements RecyclerViewItemListener, userProfileClick {
    @BindView(R.id.txt_searchBox)
    AnyEditTextView txtSearchBox;
    @BindView(R.id.ll_search_btn)
    LinearLayout llSearchBtn;
    @BindView(R.id.ll_searchBox)
    LinearLayout llSearchBox;
    @BindView(R.id.lv_search)
    ListView lvSearch;
    Unbinder unbinder;
    private static String searchedArrayKey="searchedArrayKey";
    private static String roleIdKey="roleId";
    private String searchedArray;

    private ArrayListAdapter<UserEnt> adapter;
    private ArrayList<UserEnt> collection;
    private ArrayList<UserEnt> searchCollection=new ArrayList<>();

    public static SearchResultFragment newInstance() {
        Bundle args = new Bundle();

        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static SearchResultFragment newInstance(ArrayList<UserEnt> searchArray,String roleId) {
        Bundle args = new Bundle();
        args.putString(searchedArrayKey,new Gson().toJson(searchArray));
        args.putString(roleIdKey,roleId);
        SearchResultFragment fragment = new SearchResultFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<UserEnt>(getDockActivity(), new SearchResultBinder(getDockActivity(), prefHelper, this, this));
        if (getArguments() != null) {
            searchedArray=getArguments().getString(searchedArrayKey);
            roleIdKey=getArguments().getString(roleIdKey);

        }
        if(searchedArray!=null){
            searchCollection=new Gson().fromJson(searchedArray, new TypeToken<ArrayList<UserEnt>>(){}.getType());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().showBottomBar(AppConstants.search);

        setListViewData();
    }


    private void setListViewData() {

        collection = new ArrayList<>();
       /* collection.add(new SearchResultEnt("drawable://" + R.drawable.image3, "Olivilia Marchant", "San fransicso", 4));
        collection.add(new SearchResultEnt("drawable://" + R.drawable.image4, "Olivilia Marchant", "San fransicso", 3));
        collection.add(new SearchResultEnt("drawable://" + R.drawable.image1, "Olivilia Marchant", "San fransicso", 5));
        collection.add(new SearchResultEnt("drawable://" + R.drawable.image2, "Olivilia Marchant", "San fransicso", 1));
        collection.add(new SearchResultEnt("drawable://" + R.drawable.image6, "Olivilia Marchant", "San fransicso", 2));*/


        adapter.clearList();
        lvSearch.setAdapter(adapter);
       // adapter.addAll(collection);
        adapter.notifyDataSetChanged();


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



    @OnClick(R.id.ll_searchBox)
    public void onViewClicked() {
    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        SearchResultEnt entity = (SearchResultEnt) Ent;
        getDockActivity().replaceDockableFragment(BookAServiceFragment.newInstance(), "BookAServiceFragment");
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    @Override
    public void onUserProfileClick(Object Ent, int position) {

        SearchResultEnt entity = (SearchResultEnt) Ent;

        if (prefHelper.isUserSelected()) {
            getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(), "UserProfileFragment");
        }
        else {
            getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(), "SpProfileFragment");

        }
    }

    @Override
    public void onUserAddFriendBtn(Object Ent, int position, String Key) {

    }


    @Override
    public void onSendRequestBtn(Object Ent, int position) {

    }
}
