package com.app.fandirect.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.app.fandirect.R;
import com.app.fandirect.entities.HomeGridEnt;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.helpers.InternetHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.helpers.Utils;
import com.app.fandirect.interfaces.OnReceivePlaceListener;
import com.app.fandirect.interfaces.PostInterface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.adapters.AutoCompleteListAdapter;
import com.app.fandirect.ui.binders.AutocompleteBinder;
import com.app.fandirect.ui.binders.HomeGridItemBinder;
import com.app.fandirect.ui.binders.SelectLocationBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.AutoCompleteLocation;
import com.app.fandirect.ui.views.ExpandedListView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import java.util.ArrayList;

import NearbyLocation.NearByPlaces;
import NearbyLocation.PlacesTask;
import NearbyLocation.entities.PlacesEnt;
import NearbyLocation.entities.Result;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.HomeCount;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by saeedhyder on 5/10/2018.
 */
public class SelectLocationTagFragment extends BaseFragment implements NearByPlaces,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnReceivePlaceListener {
    @BindView(R.id.autoComplete)
    AutoCompleteLocation autoComplete;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_nearestPLaces)
    ExpandedListView lvNearestPlaces;
    Unbinder unbinder;
    @BindView(R.id.edt_destination)
    EditText edtDestination;
    @BindView(R.id.new_places)
    ExpandedListView newPlaces;


    private PlacesTask placesTask;
    private PlacesEnt nearByPlaces;
    private Location Mylocation;
    private GoogleApiClient googleApiClient;
    LocationManager mLocationManager;
    private LocationListener listener;
    private AutoCompleteListAdapter madapter;
    View mainFrame;
    PostInterface postInterface;

    private ArrayListAdapter<Result> adapter;
    private ArrayList<Result> collection;

    public static SelectLocationTagFragment newInstance() {
        Bundle args = new Bundle();

        SelectLocationTagFragment fragment = new SelectLocationTagFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ArrayListAdapter<Result>(getDockActivity(), new SelectLocationBinder(getDockActivity(), prefHelper));

        if (getArguments() != null) {
        }

    }

    void setInterfaceListner(PostInterface postInterface) {
        this.postInterface = postInterface;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_location_tag, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainFrame = view;
        mainFrame.setVisibility(View.GONE);
        loadingStarted();

        placesTask = new PlacesTask(this);


        if (getMainActivity() != null) {
            googleApiClient = new GoogleApiClient.Builder(getMainActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();

        }

        autoCOmpleteListner();
        setAutocomplete();
        setListner();


    }


    private void setListner() {

        newPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(Utils.doubleClickCheck2Seconds()) {
                getPlace(position);
            }
            }
        });

        lvNearestPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Result entity = (Result) adapter.getItem(position);
                if(Utils.doubleClickCheck2Seconds()) {
                    setPlaceWithPlaceId(entity.getPlaceId());
                }
            }
        });

    }

    private void getPlace(int position) {
        UIHelper.hideSoftKeyboard(getApplicationContext(), getDockActivity().getWindow().getDecorView());
        final AutocompletePrediction item = madapter.getItem(position);
        if (item != null) {
            final String placeId = item.getPlaceId();

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
                        postInterface.getSelectedPlaceData(place.getName().toString(), place.getAddress().toString(), place.getLatLng().latitude + "", place.getLatLng().longitude + "");
                        if (getMainActivity() != null) {
                            getMainActivity().popFragment();
                        }
                        //getDockActivity().addDockableFragment(AddPostFragment.newInstance(place.getName().toString(),place.getAddress().toString(), place.getLatLng().latitude + "", place.getLatLng().longitude + ""),"AddPostFragment");
                        // setPlace(place.getAddress().toString(), place.getLatLng());
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

    }

    private void setPlaceWithPlaceId(String placeId) {

        try {


            if (placeId != null && !placeId.equals("")) {
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
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
                            postInterface.getSelectedPlaceData(place.getName().toString(), place.getAddress().toString(), place.getLatLng().latitude + "", place.getLatLng().longitude + "");
                            if (getMainActivity() != null) {
                                getMainActivity().popFragment();
                            }
                            //  getDockActivity().addDockableFragment(AddPostFragment.newInstance(place.getName().toString(),place.getAddress().toString(), place.getLatLng().latitude + "", place.getLatLng().longitude + ""),"AddPostFragment");

                        } else {
                            Toast.makeText(getApplicationContext(), "Place details not found.", Toast.LENGTH_LONG).show();
                        }

                        places.release();
                    }

                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAutocomplete() {

        madapter = new AutoCompleteListAdapter(getDockActivity(), googleApiClient, null, null, new AutocompleteBinder(), this);
        newPlaces.setAdapter(madapter);

        //enables filtering for the contents of the given ListView
        newPlaces.setTextFilterEnabled(true);

    }

    private void autoCOmpleteListner() {

        edtDestination.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                madapter.getFilter().filter(s);
            }
        });
    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showBackButton();
    }


    @Override
    public void places(String result) {
        Gson gson = new Gson();

        nearByPlaces = gson.fromJson(result, PlacesEnt.class);

        setNearestLocationData(nearByPlaces);


        loadingFinished();
        mainFrame.setVisibility(View.VISIBLE);
    }

    private void setNearestLocationData(PlacesEnt nearByPlaces) {

        adapter.clearList();
        lvNearestPlaces.setAdapter(adapter);
        adapter.addAll(nearByPlaces.getResults());

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
                        try {
                            placesTask = new PlacesTask(SelectLocationTagFragment.this);
                            placesTask.execute(sbValue.toString());

                        } catch (Exception e) {
                            mainFrame.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }

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
            getCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
    public void OnPlaceReceive() {

    }

}
