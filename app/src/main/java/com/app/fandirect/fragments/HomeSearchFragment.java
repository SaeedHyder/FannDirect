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
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.FanStatus;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.LocationModel;
import com.app.fandirect.entities.NotificationCount;
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
import im.delight.android.location.SimpleLocation;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.app.fandirect.global.AppConstants.REQUEST_CANCELLED;
import static com.app.fandirect.global.AppConstants.REQUEST_UNFRIEND;
import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.AppConstants.accept_request;
import static com.app.fandirect.global.AppConstants.accepted;
import static com.app.fandirect.global.AppConstants.cancel_request;
import static com.app.fandirect.global.AppConstants.declined_request;
import static com.app.fandirect.global.AppConstants.friend_request;
import static com.app.fandirect.global.AppConstants.unfriend;
import static com.app.fandirect.global.WebServiceConstants.AddFann;
import static com.app.fandirect.global.WebServiceConstants.AllServicesCategories;
import static com.app.fandirect.global.WebServiceConstants.NotifcationCount;
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
    private GoogleApiClient googleApiClient;
    private Location Mylocation;
    private LocationListener listener;

    private ArrayListAdapter<GetServicesEnt> CategoriesAdapter;
    private ArrayList<GetServicesEnt> CategoriesCollection;
    private ArrayListAdapter<UserEnt> searchSpAdapter;

    private ArrayListAdapter<UserEnt> SearchUserAdapter;
    private ArrayList<UserEnt> searchUserCollection;
    private Double latitude;
    private Double longitude;


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

        getMainActivity().hideAdds();
        requestOnCreatLocationPermission();

      /*  googleApiClient = new GoogleApiClient.Builder(getMainActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/

        txtSearchBox.getText().clear();
        roleId = 3;

        serviceHelper.enqueueCall(headerWebService.getServices(), AllServicesCategories);
    //    serviceHelper.enqueueCall(headerWebService.getNotificaitonCount(), NotifcationCount);


        onNotificationReceived();

        txtSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                UIHelper.hideSoftKeyboard(getDockActivity(), txtSearchBox);
                requestLocationPermission();
                /*if (roleId == 3) {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchUser);
                } else {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchServiceP);
                }*/

                return false;
            }
        });

        getDockActivity().getSupportFragmentManager().addOnBackStackChangedListener(getListener());

    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getDockActivity().getSupportFragmentManager();
                if (manager != null) {
                    Fragment currFrag = manager.findFragmentById(getDockActivity().getDockFrameLayoutId());
                    if (currFrag != null) {
                        if (currFrag instanceof HomeSearchFragment) {
                            if (getMainActivity() != null)
                                getMainActivity().showBottomBar(AppConstants.search);
                        }
                    }
                }
            }
        };

        return result;
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
       /* titleBar.showNotificationBell(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDockActivity().addDockableFragment(NotificationFragment.newInstance(), "NotificationFragment");
            }
        }, prefHelper.getNotificationCount());*/
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

                txtSearchBox.getText().clear();
                txtSearchBox.setHint(getDockActivity().getResources().getString(R.string.search_fann));

                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(), latitude != null ? latitude + "" : "", longitude != null ? longitude + "" : ""), SearchUser);

                break;
            case R.id.btn_searchService:
                roleId = 4;
                btnSearchUser.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSearchUser.setBackgroundColor(getResources().getColor(R.color.white));

                btnSearchService.setTextColor(getResources().getColor(R.color.white));
                btnSearchService.setBackgroundColor(getResources().getColor(R.color.app_blue_dark_2));

                txtSearchBox.getText().clear();
                txtSearchBox.setHint(getDockActivity().getResources().getString(R.string.search_sp));

                requestLocationPermission();
                break;

            case R.id.ll_searchBox:
                UIHelper.hideSoftKeyboard(getDockActivity(), txtSearchBox);
                requestLocationPermission();

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
            serviceHelper.enqueueCall(headerWebService.addFann(ent.getId() + "", prefHelper.getUser().getRoleId() + ""), AddFann);
        } else if (Key.equals(cancelled)) {
            serviceHelper.enqueueCall(headerWebService.markFannRequset(ent.getFanStatus().getId() + "", REQUEST_CANCELLED, prefHelper.getUser().getRoleId() + ""), cancelled);
        } else if (Key.equals(Unfriend)) {
            serviceHelper.enqueueCall(headerWebService.markFannRequset(ent.getFanStatus().getId() + "", REQUEST_UNFRIEND, prefHelper.getUser().getRoleId() + ""), Unfriend);
        } else if (Key.equals(confirmRequest)) {
            serviceHelper.enqueueCall(headerWebService.markFannRequset(ent.getFanStatus().getId() + "", accepted, prefHelper.getUser().getRoleId() + ""), confirmRequest);
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case AllServicesCategories:
                if (getTitleBar() != null) {
                    getTitleBar().hideNotificationBell();
                }
                ArrayList<GetServicesEnt> ent = (ArrayList<GetServicesEnt>) result;
                setGridViewData(ent);
                break;


            case NotifcationCount:
                prefHelper.setNotificationCount(((NotificationCount) result).getNotification_count());
              /*  if(getTitleBar()!=null){
                    getTitleBar().showNotification(prefHelper.getNotificationCount());
                    ShortcutBadger.applyCount(getDockActivity(), prefHelper.getNotificationCount());
                }*/
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

                roleId = 4;
                btnSearchUser.setTextColor(getResources().getColor(R.color.gray_dark));
                btnSearchUser.setBackgroundColor(getResources().getColor(R.color.white));

                btnSearchService.setTextColor(getResources().getColor(R.color.white));
                btnSearchService.setBackgroundColor(getResources().getColor(R.color.app_blue_dark_2));

                txtSearchBox.getText().clear();
                txtSearchBox.setHint(getDockActivity().getResources().getString(R.string.search_sp));


                break;


            case AddFann:
                FanStatus ResultEnt = (FanStatus) result;
                requestLocationPermission();
              /*  if (roleId == 3) {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchUser);
                } else {
                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchServiceP);
                }*/

                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case cancelled:
                FanStatus entCancelled = (FanStatus) result;
                if (searchUserCollection != null && searchUserCollection.size() > 0 && searchUserCollection.size() <= pos) {
                    searchUserCollection.get(pos).setFanStatus(entCancelled);
                }
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case Unfriend:
                FanStatus entUnfriend = (FanStatus) result;
                if (searchUserCollection != null && searchUserCollection.size() > 0 && searchUserCollection.size() <= pos) {
                    searchUserCollection.get(pos).setFanStatus(entUnfriend);
                }
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case confirmRequest:
                FanStatus entconfirm = (FanStatus) result;
                if (searchUserCollection != null && searchUserCollection.size() > 0 && searchUserCollection.size() <= pos) {
                    searchUserCollection.get(pos).setFanStatus(entconfirm);
                }
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;


        }
    }

    @Override
    public void onSendRequestBtn(Object Ent, int position) {

        UserEnt ent = (UserEnt) Ent;
        getDockActivity().addDockableFragment(BookAServiceFragment.newInstance(ent.getId() + ""), "BookAServiceFragment");

    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        GetServicesEnt ent = (GetServicesEnt) Ent;
        if (latitude != null && longitude != null && latitude != 0.0 && !latitude.equals("")) {
            serviceHelper.enqueueCall(headerWebService.getServiceProvider(ent.getId() + "", latitude + "", longitude + ""), getServiceProvider);
        } else {
            getLocation();
            UIHelper.showShortToastInCenter(getDockActivity(), "Gps is not working, location not found, try again....");
        }


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
               /*     if (getTitleBar() != null) {
                        getTitleBar().showNotification(prefHelper.getNotificationCount());
                        ShortcutBadger.applyCount(getDockActivity(), prefHelper.getNotificationCount());
                    }
*/
                    if (bundle != null) {
                        String Type = bundle.getString("pushtype");

                        if (Type != null && (Type.equals(cancel_request) || Type.equals(declined_request) || Type.equals(unfriend))) {
                           /* if (roleId == 3) {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchUser);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchServiceP);
                            }*/
                            requestLocationPermission();
                        } else if (Type != null && Type.equals(accept_request)) {
                          /*  if (roleId == 3) {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchUser);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(),latitude!=null?latitude+"":"",longitude!=null?longitude+"":""), SearchServiceP);
                            }*/
                            requestLocationPermission();
                        } else if (Type != null && Type.equals(friend_request)) {
                            if (roleId == 3) {
                                serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(), latitude != null ? latitude + "" : "", longitude != null ? longitude + "" : ""), SearchUser);
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
                                if (roleId == 3) {
                                    serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(), latitude != null ? latitude + "" : "", longitude != null ? longitude + "" : ""), SearchUser);
                                } else {
                                    if (latitude != null && longitude != null && latitude != 0.0 && !latitude.equals("")) {
                                        serviceHelper.enqueueCall(headerWebService.SearchPeople(roleId + "", txtSearchBox.getText().toString(), latitude + "", longitude + ""), SearchServiceP);
                                    } else {
                                        UIHelper.showShortToastInCenter(getDockActivity(), "Gps is not working, location not found, try again....");
                                        getLocation();
                                    }
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

    private void requestOnCreatLocationPermission() {
        Dexter.withActivity(getDockActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            if (getMainActivity() != null && getMainActivity().statusCheck()) {
                                getLocation();
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestOnCreatLocationPermission();

                        } else if (report.getDeniedPermissionResponses().size() > 0) {
                            requestOnCreatLocationPermission();
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


    private void getLocation() {
        if (getMainActivity() != null && getMainActivity().statusCheck()) {
            LocationModel locationModel = getMainActivity().getMyCurrentLocation();
            if (locationModel != null) {
                latitude = locationModel.getLat();
                longitude = locationModel.getLng();

            } else {
                getLocation();
            }
        }
    }

  /*
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


                    } else {
                        UIHelper.showShortToastInCenter(getDockActivity(), "Can't get your Location Try getting using Location Button");
                    }
                }
            }
        };
        if (googleApiClient.isConnected() && getActivity() != null)
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener);

                //drive_location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            }
    }

   @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (getMainActivity() != null && getMainActivity().statusCheck()) {
          //  getCurrentLocation();
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
    }*/

}
