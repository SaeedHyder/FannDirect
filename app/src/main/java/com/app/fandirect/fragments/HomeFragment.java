package com.app.fandirect.fragments;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.HomeGridEnt;
import com.app.fandirect.entities.TilesCountEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.helpers.InternetHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.HomeGridItemBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.ExpandableGridView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.app.fandirect.global.WebServiceConstants.HomeCount;


public class HomeFragment extends BaseFragment implements RecyclerViewItemListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


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
    @BindView(R.id.pullToRefresh)
    SwipeRefreshLayout pullToRefresh;
    protected BroadcastReceiver broadcastReceiver;


    private ArrayListAdapter<HomeGridEnt> adapter;
    private ArrayList<HomeGridEnt> collection;
    private GoogleApiClient googleApiClient;
    private Location Mylocation;
    private LocationListener listener;
    private Double latitude;
    private Double longitude;

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        getMainActivity().showAdds();

        if (InternetHelper.CheckInternetConectivityandShowToast(getDockActivity())) {
            serviceHelper.enqueueCall(headerWebService.getHomeCount(), HomeCount);
        } else {
            adapter = new ArrayListAdapter<HomeGridEnt>(getDockActivity(), new HomeGridItemBinder(getDockActivity(), prefHelper, this, null));
            setGridViewData();
        }

        //  serviceHelper.enqueueCall(headerWebService.getNotificaitonCount(), NotifcationCount);
        onNotificationReceived();

        gvHome.setExpanded(true);

        googleApiClient = new GoogleApiClient.Builder(getMainActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (!prefHelper.isUserSelected()) {
            requestLocationTechnicianPermission();
        }

        getMainActivity().hideBottomBar();


        prefHelper.setLoginStatus(true);
        prefHelper.setBeforeLoginStatus(false);
        setListner();
        pullToRefreshListner();

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
                        getDockActivity().replaceDockableFragment(ServiceHistoryMainUserFragment.newInstance(), "ServiceHistoryMainUserFragment");

                    } else {
                        getDockActivity().replaceDockableFragment(ServiceHistoryMainSpFragment.newInstance(), "ServiceHistoryMainSpFragment");
                    }

                }
            }
        });
    }


    private void setGridViewData() {


        collection = new ArrayList<>();
        collection.add(new HomeGridEnt(R.drawable.profile3, getResString(R.string.profile)));
        collection.add(new HomeGridEnt(R.drawable.feeds2, getResString(R.string.feeds)));
        collection.add(new HomeGridEnt(R.drawable.message3, getResString(R.string.message)));
        collection.add(new HomeGridEnt(R.drawable.fanns, getResString(R.string.fanns)));
        collection.add(new HomeGridEnt(R.drawable.fan_request1, getResString(R.string.fanns_request)));
        collection.add(new HomeGridEnt(R.drawable.promotions, getResString(R.string.promotions)));
        collection.add(new HomeGridEnt(R.drawable.settings, getResString(R.string.settings)));
        if (prefHelper.isUserSelected()) {
            collection.add(new HomeGridEnt(R.drawable.posts, getResString(R.string.posts)));
        } else {
            collection.add(new HomeGridEnt(R.drawable.posts, getResString(R.string.promotions_posts)));
        }
        collection.add(new HomeGridEnt(R.drawable.services, getResString(R.string.my_services)));


        adapter.clearList();
        gvHome.setAdapter(adapter);
        adapter.addAll(collection);
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {
            case HomeCount:
                TilesCountEnt data = (TilesCountEnt) result;

                if (data.getNotification_count() != null) {
                    prefHelper.setNotificationCount(data.getNotification_count());
                } else {
                    prefHelper.setNotificationCount(0);
                }

                if (getTitleBar() != null) {
                    getTitleBar().showNotification(prefHelper.getNotificationCount());
                    ShortcutBadger.applyCount(getDockActivity(), prefHelper.getNotificationCount());
                }

                adapter = new ArrayListAdapter<HomeGridEnt>(getDockActivity(), new HomeGridItemBinder(getDockActivity(), prefHelper, this, data));
                setGridViewData();
                break;

           /* case NotifcationCount:
                prefHelper.setNotificationCount(((NotificationCount) result).getNotification_count());
                if(getTitleBar()!=null){
                    getTitleBar().showNotification(prefHelper.getNotificationCount());
                    ShortcutBadger.applyCount(getDockActivity(), prefHelper.getNotificationCount());
                }
                break;
                */
        }
    }

    @Override
    public void ResponseFailure(String tag) {
        super.ResponseFailure(tag);
        if (getMainActivity() != null) {
            getMainActivity().onLoadingFinished();
        }
        adapter = new ArrayListAdapter<HomeGridEnt>(getDockActivity(), new HomeGridItemBinder(getDockActivity(), prefHelper, this, null));
        setGridViewData();
    }

    private void pullToRefreshListner() {

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (InternetHelper.CheckInternetConectivityandShowToast(getDockActivity())) {
                    serviceHelper.enqueueCall(headerWebService.getHomeCount(), HomeCount);
                } else {
                    adapter = new ArrayListAdapter<HomeGridEnt>(getDockActivity(), new HomeGridItemBinder(getDockActivity(), prefHelper, HomeFragment.this, null));
                    setGridViewData();
                }
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showNotification(prefHelper.getNotificationCount());

    }


    @OnClick({R.id.txt_searchBox, R.id.ll_searchBox, R.id.btn_searchUser, R.id.btn_searchService})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_searchBox:
                //  getDockActivity().replaceDockableFragment(HomeSearchFragment.newInstance(), "HomeSearchFragment");
                requestLocationPermission();
                break;
            case R.id.ll_searchBox:
                //    getDockActivity().replaceDockableFragment(HomeSearchFragment.newInstance(), "HomeSearchFragment");
                requestLocationPermission();
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

        if (entity.getText().equals(getResString(R.string.profile))) {
            if (prefHelper.isUserSelected()) {
                getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(), "UserProfileFragment");
            } else {
                getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(), "SpProfileFragment");
            }

        } else if (entity.getText().equals(getResString(R.string.feeds))) {
            getDockActivity().replaceDockableFragment(FeedFragment.newInstance(), "FeedFragment");

        } else if (entity.getText().equals(getResString(R.string.message))) {
            getDockActivity().replaceDockableFragment(MessageFragment.newInstance(), "MessageFragment");

        } else if (entity.getText().equals(getResString(R.string.fanns))) {
            getDockActivity().replaceDockableFragment(FannsFragment.newInstance(), "FannsFragment");

        } else if (entity.getText().equals(getResString(R.string.fanns_request))) {
            getDockActivity().replaceDockableFragment(FriendRequestFragment.newInstance(), "FriendRequestFragment");

        } else if (entity.getText().equals(getResString(R.string.promotions))) {
            getDockActivity().replaceDockableFragment(PromotionsFragment.newInstance(), "PromotionsFragment");

        } else if (entity.getText().equals(getResString(R.string.settings))) {
            getDockActivity().replaceDockableFragment(SettingFragment.newInstance(), "SettingFragment");

        } else if (entity.getText().equals(getResString(R.string.posts))) {
            getDockActivity().replaceDockableFragment(WallPostsFragment.newInstance(), "WallPostsFragment");

        } else if (entity.getText().equals(getResString(R.string.my_services))) {
            if (prefHelper.isUserSelected()) {
                getDockActivity().replaceDockableFragment(ServiceHistoryMainUserFragment.newInstance(), "ServiceHistoryMainUserFragment");

            } else {
                getDockActivity().replaceDockableFragment(ServiceHistorySpFragment.newInstance(), "ServiceHistorySpFragment");
            }

        }


    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    private void requestLocationPermission() {
        Dexter.withActivity(getDockActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (getMainActivity() != null && getMainActivity().statusCheck()) {
                                getDockActivity().replaceDockableFragment(HomeSearchFragment.newInstance(), "HomeSearchFragment");
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestLocationPermission();

                        } else if (report.getDeniedPermissionResponses().size() > 0) {
                            requestLocationPermission();
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Grant Location Permission to processed");
                        openSettings();
                    }
                })

                .onSameThread()
                .check();


    }

    private void requestLocationTechnicianPermission() {
        Dexter.withActivity(getDockActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (getMainActivity() != null && getMainActivity().statusCheck()) {

                              /*  SimpleLocation location = new SimpleLocation(getDockActivity());
                                if (!location.hasLocationEnabled()) {
                                    SimpleLocation.openSettings(getDockActivity());
                                }
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();*/

                                if (latitude != null && longitude != null && latitude != 0.0 && !latitude.equals("")) {
                                    serviceHelper.enqueueCall(headerWebService.updateLatlong(latitude + "", longitude + ""), WebServiceConstants.updateLatLng, false);
                                } else {
                                    getCurrentLocation();
                                    //   UIHelper.showShortToastInCenter(getDockActivity(), "Can't get your Location Try getting using Location Button");
                                }
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestLocationPermission();

                        } else if (report.getDeniedPermissionResponses().size() > 0) {
                            requestLocationPermission();
                        }
                    }


                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Grant Location Permission to processed");
                        openSettings();
                    }
                })

                .onSameThread()
                .check();


    }

    private void openSettings() {

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        Uri uri = Uri.fromParts("package", getDockActivity().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void getCurrentLocation() {

        if (getMainActivity() != null) {
            if (ActivityCompat.checkSelfPermission(getMainActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getMainActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
        }
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (Mylocation == null) {
            locationRequest.setInterval(1000);
        }
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location mlocation) {
                if (mlocation != null) {
                    Mylocation = mlocation;
                    try {
                        if (googleApiClient.isConnected() || googleApiClient.isConnecting()) {
                            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (Mylocation != null) {
                        //Getting longitude and latitude
                        latitude = Mylocation.getLatitude();
                        longitude = Mylocation.getLongitude();

                        if (latitude != null && longitude != null && latitude != 0.0 && !latitude.equals("")) {
                            serviceHelper.enqueueCall(headerWebService.updateLatlong(latitude + "", longitude + ""), WebServiceConstants.updateLatLng, false);
                        } else {
                            getCurrentLocation();
                            //UIHelper.showShortToastInCenter(getDockActivity(), "Gps is not working, location not found...");
                        }

                    }
                }
            }
        };
        if (googleApiClient.isConnected() && getActivity() != null)
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener);

            }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (getMainActivity() != null && getMainActivity().statusCheck()) {
            getCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.PUSH_NOTIFICATION));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getDockActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }


    private void onNotificationReceived() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppConstants.REGISTRATION_COMPLETE)) {
                    System.out.println("registration complete");
                    System.out.println(prefHelper.getFirebase_TOKEN());

                } else if (intent.getAction().equals(AppConstants.PUSH_NOTIFICATION)) {
                    serviceHelper.enqueueCall(headerWebService.getHomeCount(), HomeCount, false);
                }
            }

        };
    }

}

