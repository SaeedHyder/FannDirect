package com.app.fandirect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.FanStatus;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.userProfileClick;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.HomeSearchBinder;
import com.app.fandirect.ui.binders.SearchResultBinder;
import com.app.fandirect.ui.binders.SearchResultUserBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.app.fandirect.global.AppConstants.REQUEST_CANCELLED;
import static com.app.fandirect.global.AppConstants.REQUEST_UNFRIEND;
import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.AppConstants.accept_request;
import static com.app.fandirect.global.AppConstants.accepted;
import static com.app.fandirect.global.AppConstants.cancel_request;
import static com.app.fandirect.global.AppConstants.declined_request;
import static com.app.fandirect.global.AppConstants.unfriend;
import static com.app.fandirect.global.WebServiceConstants.AddFann;
import static com.app.fandirect.global.WebServiceConstants.AllServicesCategories;
import static com.app.fandirect.global.WebServiceConstants.SearchServiceP;
import static com.app.fandirect.global.WebServiceConstants.SearchUser;
import static com.app.fandirect.global.WebServiceConstants.Unfriend;
import static com.app.fandirect.global.WebServiceConstants.cancelled;
import static com.app.fandirect.global.WebServiceConstants.confirmRequest;
import static com.app.fandirect.global.WebServiceConstants.getServiceProvider;

/**
 * Created by saeedhyder on 3/5/2018.
 */
public class HomeSearchFragment extends BaseFragment implements RecyclerViewItemListener, userProfileClick {
    @BindView(R.id.txt_searchBox)
    AnyEditTextView txtSearchBox;
    @BindView(R.id.ll_search_btn)
    LinearLayout llSearchBtn;
    @BindView(R.id.ll_searchBox)
    LinearLayout llSearchBox;
    @BindView(R.id.gv_home_search)
    GridView gvHomeSearch;
    Unbinder unbinder;
    @BindView(R.id.btn_searchUser)
    Button btnSearchUser;
    @BindView(R.id.btn_searchService)
    Button btnSearchService;
    protected BroadcastReceiver broadcastReceiver;
    int roleId = 3;
    int pos = 0;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;

    private ArrayListAdapter<GetServicesEnt> CategoriesAdapter;
    private ArrayList<GetServicesEnt> CategoriesCollection;

    private ArrayListAdapter<UserEnt> searchSpAdapter;


    private ArrayListAdapter<UserEnt> SearchUserAdapter;
    private ArrayList<UserEnt> searchUserCollection;


    public static HomeSearchFragment newInstance() {
        Bundle args = new Bundle();

        HomeSearchFragment fragment = new HomeSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CategoriesAdapter = new ArrayListAdapter<GetServicesEnt>(getDockActivity(), new HomeSearchBinder(getDockActivity(), prefHelper, this));
        SearchUserAdapter = new ArrayListAdapter<UserEnt>(getDockActivity(), new SearchResultUserBinder(getDockActivity(), prefHelper, this));
        searchSpAdapter = new ArrayListAdapter<UserEnt>(getDockActivity(), new SearchResultBinder(getDockActivity(), prefHelper, this, this));

        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_search, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().showBottomBar(AppConstants.search);


        txtSearchBox.getText().clear();
        roleId = 3;

        serviceHelper.enqueueCall(headerWebService.getServices(), AllServicesCategories);

        onNotificationReceived();

    }


    private void setGridViewData(ArrayList<GetServicesEnt> entity) {

        CategoriesCollection = new ArrayList<>();
        CategoriesCollection.addAll(entity);

        CategoriesAdapter.clearList();
        gvHomeSearch.setAdapter(CategoriesAdapter);
        gvHomeSearch.setNumColumns(1);
        CategoriesAdapter.addAll(CategoriesCollection);
        CategoriesAdapter.notifyDataSetChanged();


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


    @OnClick({R.id.btn_searchUser, R.id.btn_searchService, R.id.ll_searchBox})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_searchUser:
                roleId = 3;
                btnSearchUser.setTextColor(getResources().getColor(R.color.white));
                btnSearchUser.setBackgroundColor(getResources().getColor(R.color.app_blue_dark_2));

                btnSearchService.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSearchService.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case R.id.btn_searchService:
                roleId = 4;
                btnSearchUser.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSearchUser.setBackgroundColor(getResources().getColor(R.color.white));

                btnSearchService.setTextColor(getResources().getColor(R.color.white));
                btnSearchService.setBackgroundColor(getResources().getColor(R.color.app_blue_dark_2));

                break;

            case R.id.ll_searchBox:
                UIHelper.hideSoftKeyboard(getDockActivity(), txtSearchBox);
                if (roleId == 3) {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchUser);
                } else {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchServiceP);
                }
                break;
        }
    }


    @Override
    public void onUserProfileClick(Object Ent, int position) {

        UserEnt ent = (UserEnt) Ent;

        if (ent.getRoleId().equals(UserRoleId)) {
            getDockActivity().addDockableFragment(UserProfileFragment.newInstance(ent.getId() + ""), "UserProfileFragment");
        } else {
            getDockActivity().addDockableFragment(SpProfileFragment.newInstance(ent.getId() + ""), "SpProfileFragment");
        }


    }

    @Override
    public void onUserAddFriendBtn(Object Ent, int position, String Key) {

        UserEnt ent = (UserEnt) Ent;
        pos = position;

        if (Key.equals(AddFann)) {
            serviceHelper.enqueueCall(headerWebService.addFann(ent.getId() + ""), AddFann);
        } else if (Key.equals(cancelled)) {
            serviceHelper.enqueueCall(headerWebService.markFannRequset(ent.getFanStatus().getId() + "", REQUEST_CANCELLED), cancelled);
        } else if (Key.equals(Unfriend)) {
            serviceHelper.enqueueCall(headerWebService.markFannRequset(ent.getFanStatus().getId() + "", REQUEST_UNFRIEND), Unfriend);
        } else if (Key.equals(confirmRequest)) {
            serviceHelper.enqueueCall(headerWebService.markFannRequset(ent.getFanStatus().getId() + "", accepted), confirmRequest);
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case AllServicesCategories:

                ArrayList<GetServicesEnt> ent = (ArrayList<GetServicesEnt>) result;

                setGridViewData(ent);
                break;

            case SearchUser:

                ArrayList<UserEnt> userEnt = (ArrayList<UserEnt>) result;

                searchUserCollection = new ArrayList<>();
                searchUserCollection.addAll(userEnt);

                if (userEnt.size() > 0) {
                    gvHomeSearch.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    gvHomeSearch.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }

                SearchUserAdapter.clearList();
                gvHomeSearch.setAdapter(SearchUserAdapter);
                SearchUserAdapter.addAll(searchUserCollection);

                if (pos > 0) {
                    gvHomeSearch.setSelection(pos);
                }

                //  SearchUserAdapter.notifyDataSetChanged();

                //   getDockActivity().replaceDockableFragment(SearchResultFragment.newInstance(userEnt,roleId+""), "SearchResultFragment");
                break;

            case SearchServiceP:

                ArrayList<UserEnt> userEntity = (ArrayList<UserEnt>) result;

                if (userEntity.size() > 0) {
                    gvHomeSearch.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    gvHomeSearch.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }

                searchSpAdapter.clearList();
                gvHomeSearch.setAdapter(searchSpAdapter);
                searchSpAdapter.addAll(userEntity);

                break;

            case getServiceProvider:

                ArrayList<UserEnt> spEntity = (ArrayList<UserEnt>) result;

                if (spEntity.size() > 0) {
                    gvHomeSearch.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    gvHomeSearch.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }

                searchSpAdapter.clearList();
                gvHomeSearch.setAdapter(searchSpAdapter);
                searchSpAdapter.addAll(spEntity);

                break;


            case AddFann:
                FanStatus ResultEnt = (FanStatus) result;
                if (roleId == 3) {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchUser);
                } else {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchServiceP);
                }

                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case cancelled:

                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case Unfriend:

                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case confirmRequest:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;


        }
    }

    @Override
    public void onSendRequestBtn(Object Ent, int position) {

        UserEnt ent = (UserEnt) Ent;
        getDockActivity().replaceDockableFragment(BookAServiceFragment.newInstance(ent.getId() + ""), "BookAServiceFragment");

    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        GetServicesEnt ent = (GetServicesEnt) Ent;
        serviceHelper.enqueueCall(headerWebService.getServiceProvider(ent.getId() + ""), getServiceProvider);

    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    private void onNotificationReceived() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(AppConstants.REGISTRATION_COMPLETE)) {
                    System.out.println("registration complete");
                    System.out.println(prefHelper.getFirebase_TOKEN());

                } else if (intent.getAction().equals(AppConstants.PUSH_NOTIFICATION)) {
                    Bundle bundle = intent.getExtras();
                    if (bundle != null) {
                        String Type = bundle.getString("pushtype");

                        if (Type != null && (Type.equals(cancel_request) || Type.equals(declined_request) || Type.equals(unfriend))) {
                            if (roleId == 3) {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchUser);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchServiceP);
                            }
                        } else if (Type != null && Type.equals(accept_request)) {
                            if (roleId == 3) {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchUser);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString()), SearchServiceP);
                            }
                        }
                    }

                } else {
                    UIHelper.showShortToastInCenter(getDockActivity(), "Notification Data is Empty");
                }
            }

        };
    }


    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getDockActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        txtSearchBox.getText().clear();
        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.PUSH_NOTIFICATION));


    }


}
