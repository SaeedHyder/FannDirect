package com.app.fandirect.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.fandirect.R;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.EqualSpacingItemDecoration;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.binders.CategoriesBinder;
import com.app.fandirect.ui.binders.NearestPlacesBinder;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRecyclerView;
import com.app.fandirect.ui.views.TitleBar;
import com.bumptech.glide.Glide;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaofeng.flowlayoutmanager.FlowLayoutManager;

import java.util.ArrayList;

import NearbyLocation.NearByPlaces;
import NearbyLocation.PlacesTask;
import NearbyLocation.entities.PlacesEnt;
import NearbyLocation.entities.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by saeedhyder on 4/21/2018.
 */
public class NewPostFragment extends BaseFragment implements RecyclerViewItemListener, NearByPlaces,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {


    private static String imageUpload = "imageUpload";
    private static String isPromotionKey = "isPromotionKey";
    private static String textKey = "textKey";
    @BindView(R.id.iv_image)
    CircleImageView ivImage;
    @BindView(R.id.txt_caption)
    AnyEditTextView txtCaption;
    @BindView(R.id.iv_upload_photo)
    ImageView ivUploadPhoto;
    @BindView(R.id.lv_selecte_category)
    CustomRecyclerView lvSelecteCategory;
    @BindView(R.id.txt_category_name)
    AnyTextView txtCategoryName;
    @BindView(R.id.iv_location_category)
    ImageView ivLocationCategory;
    @BindView(R.id.ll_cross_category)
    LinearLayout llCrossCategory;
    @BindView(R.id.rl_selected_category)
    RelativeLayout rlSelectedCategory;
    @BindView(R.id.ll_select_category)
    LinearLayout llSelectCategory;
    @BindView(R.id.txt_add_location)
    AnyTextView txtAddLocation;
    @BindView(R.id.lv_nearest_locations)
    CustomRecyclerView lvNearestLocations;
    @BindView(R.id.txt_location_name)
    AnyTextView txtLocationName;
    @BindView(R.id.txt_location_vacinity)
    AnyTextView txtLocationVacinity;
    @BindView(R.id.iv_location)
    ImageView ivLocation;
    @BindView(R.id.ll_cross)
    LinearLayout llCross;
    @BindView(R.id.rl_selected_place)
    RelativeLayout rlSelectedPlace;
    @BindView(R.id.ll_add_location)
    LinearLayout llAddLocation;
    Unbinder unbinder;
    @BindView(R.id.txt_select_category)
    AnyTextView txtSelectCategory;
    private ImageLoader imageLoader;
    private LocationListener listener;
    private ArrayList<String> userCollections = new ArrayList<>();
    private PlacesTask placesTask;
    private PlacesEnt nearByPlaces;
    private Location Mylocation;
    //  private LocationListener listener;
    private GoogleApiClient googleApiClient;
    LocationManager mLocationManager;
    View mainFrame;
    boolean isFromPromotion = false;
    private FlowLayoutManager flowLayoutManager;

    public static NewPostFragment newInstance() {
        Bundle args = new Bundle();

        NewPostFragment fragment = new NewPostFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static NewPostFragment newInstance(String image, boolean isPromotion,String text) {
        Bundle args = new Bundle();
        args.putString(imageUpload, image);
        args.putBoolean(isPromotionKey, isPromotion);
        args.putString(textKey, text);
        NewPostFragment fragment = new NewPostFragment();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        if (getArguments() != null) {
            imageUpload = getArguments().getString(imageUpload);
            textKey = getArguments().getString(textKey);
            isFromPromotion = getArguments().getBoolean(isPromotionKey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();

        mainFrame = view;
        mainFrame.setVisibility(View.GONE);
        loadingStarted();

        placesTask = new PlacesTask(this);


        googleApiClient = new GoogleApiClient.Builder(getMainActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        setData();


    }


    public StringBuilder sbMethod(double latitude, double longitude) {

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location=" + latitude + "," + longitude);
        sb.append("&radius=5000");
        sb.append("&sensor=true");
        sb.append("&key=" + getDockActivity().getResources().getString(R.string.api_key));

        Log.d("Map", "api: " + sb.toString());

        return sb;
    }


    private void setData() {

        if (isFromPromotion) {
            llSelectCategory.setVisibility(View.VISIBLE);
        }

        if(textKey!=null && !textKey.equals("")){
            txtCaption.setText(textKey);
        }

        Glide.with(getDockActivity())
                .load("file:///" + imageUpload)
                .into(ivUploadPhoto);


    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showBackButton();
        titleBar.showPostBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainActivity().notImplemented();
            }
        });
        titleBar.setSubHeading(getString(R.string.new_post));
    }




    @Override
    public void places(String result) {

        Gson gson = new Gson();

        nearByPlaces = gson.fromJson(result, PlacesEnt.class);

        setNearestLocationData(nearByPlaces);
        setCategoriesData(nearByPlaces);

        loadingFinished();
        mainFrame.setVisibility(View.VISIBLE);

    }

    private void setCategoriesData(PlacesEnt nearByPlaces) {
        flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        lvSelecteCategory.addItemDecoration(new EqualSpacingItemDecoration(-5));

        lvSelecteCategory.BindRecyclerView(new CategoriesBinder(this),
                nearByPlaces.getResults(), flowLayoutManager, new DefaultItemAnimator());
    }

    private void setNearestLocationData(PlacesEnt nearByPlaces) {

        flowLayoutManager = new FlowLayoutManager();
        flowLayoutManager.setAutoMeasureEnabled(true);
        lvNearestLocations.addItemDecoration(new EqualSpacingItemDecoration(-5));

        lvNearestLocations.BindRecyclerView(new NearestPlacesBinder(this),
                nearByPlaces.getResults(), flowLayoutManager, new DefaultItemAnimator());
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
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener);
                    if (Mylocation != null) {
                        //Getting longitude and latitude
                        Double latitude = Mylocation.getLatitude();
                        Double longitude = Mylocation.getLongitude();

                        StringBuilder sbValue = new StringBuilder(sbMethod(latitude, longitude));
                        placesTask.execute(sbValue.toString());


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
        if (getMainActivity()!=null && getMainActivity().statusCheck()) {
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

    private void setPlaceWithPlaceId(String placeId) {

        PendingResult<PlaceBuffer> placeResult =
                Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(@NonNull PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    places.release();
                    return;
                }
                if (places.getCount() > 0) {
                    // Got place details
                    final Place place = places.get(0);
                    //  setPlace(place.getAddress().toString(), place.getLatLng());
//                        madapter.clear();
//                        madapter.notifyDataSetChanged();
                    // Do your stuff
                } else {
                    // No place details
                    Toast.makeText(getApplicationContext(), "Place details not found.", Toast.LENGTH_LONG).show();
                }

                places.release();
            }

        });
    }


    @Override
    public void onCategoryClick(Object Ent, int position) {

        txtSelectCategory.setVisibility(View.GONE);
        lvSelecteCategory.setVisibility(View.GONE);
        rlSelectedCategory.setVisibility(View.VISIBLE);

        Result entitiy = (Result) Ent;
        txtCategoryName.setText(entitiy.getName());

    }

    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {

        txtAddLocation.setVisibility(View.GONE);
        lvNearestLocations.setVisibility(View.GONE);
        rlSelectedPlace.setVisibility(View.VISIBLE);

        Result entitiy = (Result) Ent;
        txtLocationName.setText(entitiy.getName());
        txtLocationVacinity.setText(entitiy.getVicinity());

    }



    @OnClick({R.id.ll_cross_category, R.id.ll_cross})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_cross_category:
                txtSelectCategory.setVisibility(View.VISIBLE);
                lvSelecteCategory.setVisibility(View.VISIBLE);
                rlSelectedCategory.setVisibility(View.GONE);
                break;
            case R.id.ll_cross:
                txtAddLocation.setVisibility(View.VISIBLE);
                lvNearestLocations.setVisibility(View.VISIBLE);
                rlSelectedPlace.setVisibility(View.GONE);
                break;
        }
    }
}
