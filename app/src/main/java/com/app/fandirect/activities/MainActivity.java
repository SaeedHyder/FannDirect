package com.app.fandirect.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.fandirect.R;
import com.app.fandirect.fragments.FannsFragment;
import com.app.fandirect.fragments.FavoriteFragment;
import com.app.fandirect.fragments.FriendRequestFragment;
import com.app.fandirect.fragments.HomeFragment;
import com.app.fandirect.fragments.HomeSearchFragment;
import com.app.fandirect.fragments.InboxChatFragment;
import com.app.fandirect.fragments.LoginFragment;
import com.app.fandirect.fragments.MessageFragment;
import com.app.fandirect.fragments.NotificationsFragment;
import com.app.fandirect.fragments.ServiceHistorySpFragment;
import com.app.fandirect.fragments.ServiceHistoryUserFragment;
import com.app.fandirect.fragments.SideMenuFragment;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.global.SideMenuChooser;
import com.app.fandirect.global.SideMenuDirection;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.ScreenHelper;
import com.app.fandirect.helpers.ServiceHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ImageSetter;
import com.app.fandirect.interfaces.webServiceResponseLisener;
import com.app.fandirect.residemenu.ResideMenu;
import com.app.fandirect.retrofit.WebService;
import com.app.fandirect.retrofit.WebServiceFactory;
import com.app.fandirect.ui.views.TitleBar;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.FirebaseApp;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ChosenImages;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.fandirect.global.AppConstants.Accept_Request;
import static com.app.fandirect.global.AppConstants.Complete_Request;
import static com.app.fandirect.global.AppConstants.New_Request;
import static com.app.fandirect.global.AppConstants.Rejected_Request;
import static com.app.fandirect.global.AppConstants.accept_request;
import static com.app.fandirect.global.AppConstants.block_user;
import static com.app.fandirect.global.AppConstants.delete_user;
import static com.app.fandirect.global.AppConstants.friend_request;
import static com.app.fandirect.global.AppConstants.messagePush;
import static com.app.fandirect.global.WebServiceConstants.feedback;


public class MainActivity extends DockActivity implements webServiceResponseLisener, OnClickListener, ImageChooserListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public TitleBar titleBar;
    @BindView(R.id.sideMneuFragmentContainer)
    public FrameLayout sideMneuFragmentContainer;
    @BindView(R.id.header_main)
    TitleBar header_main;
    @BindView(R.id.mainFrameLayout)
    FrameLayout mainFrameLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.iv_home)
    ImageView ivHome;
    @BindView(R.id.ll_home)
    LinearLayout llHome;
    @BindView(R.id.iv_message)
    ImageView ivMessage;
    @BindView(R.id.ll_message)
    LinearLayout llMessage;
    @BindView(R.id.iv_search)
    ImageView ivSearch;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.iv_favorite)
    ImageView ivFavorite;
    @BindView(R.id.ll_favorite)
    LinearLayout llFavorite;
    @BindView(R.id.iv_profile)
    ImageView ivProfile;
    @BindView(R.id.ll_profile)
    LinearLayout llProfile;
    @BindView(R.id.ll_bottomtab_layout)
    LinearLayout llBottomtabLayout;
    @BindView(R.id.content_frame)
    RelativeLayout contentFrame;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private MainActivity mContext;
    private boolean loading;

    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String filePath;
    private String originalFilePath;
    private final static String TAG = "ICA";
    private String thumbnailFilePath;
    private String thumbnailSmallFilePath;
    private boolean isActivityResultOver = false;
    private ImageSetter imageSetter;

    private ResideMenu resideMenu;

    private float lastTranslate = 0.0f;

    private String sideMenuType;
    private String sideMenuDirection;

    protected ServiceHelper serviceHelper;

    private AlertDialog alert;
    public final int WifiResultCode = 2;
    public final int LocationResultCode = 1;
    protected BroadcastReceiver broadcastReceiver;
    protected WebService webService;
    protected WebService headerWebService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_dock);
        ButterKnife.bind(this);
        titleBar = header_main;
        // setBehindContentView(R.layout.fragment_frame);
        mContext = this;
        Log.i("Screen Density", ScreenHelper.getDensity(this) + "");

        sideMenuType = SideMenuChooser.DRAWER.getValue();
        sideMenuDirection = SideMenuDirection.LEFT.getValue();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        settingSideMenu(sideMenuType, sideMenuDirection);
        onNotificationReceived();

        if (webService == null) {
            webService = WebServiceFactory.getWebServiceInstanceWithCustomInterceptor(getDockActivity(), WebServiceConstants.Local_SERVICE_URL);
        }
        if (headerWebService == null) {
            headerWebService = WebServiceFactory.getWebServiceInstanceWithCustomInterceptorandheader(getDockActivity(), WebServiceConstants.Local_SERVICE_URL);
        }
        if (serviceHelper == null) {
            serviceHelper = new ServiceHelper(this, getDockActivity(), webService);
        }

        titleBar.setMenuButtonListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sideMenuType.equals(SideMenuChooser.DRAWER.getValue()) && getDrawerLayout() != null) {
                    if (sideMenuDirection.equals(SideMenuDirection.LEFT.getValue())) {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    } else {
                        drawerLayout.openDrawer(Gravity.RIGHT);
                    }
                } else {
                    //    resideMenu.openMenu(sideMenuDirection);
                }

            }
        });


        titleBar.setBackButtonListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (loading) {
                    UIHelper.showLongToastInCenter(getApplicationContext(),
                            R.string.message_wait);
                } else {

                    popFragment();
                    UIHelper.hideSoftKeyboard(getApplicationContext(),
                            titleBar);
                }
            }
        });

        titleBar.setNotificationButtonListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceDockableFragment(NotificationsFragment.newInstance(), "NotificationsFragment");
            }
        });

        if (savedInstanceState == null)
            initFragment();


    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(getDockActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.PUSH_NOTIFICATION));

        if (!prefHelper.isLogin()) {
            replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");
        }
      //  initFragment();

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
                        final String RequestId = bundle.getString("request_id");
                        final String SenderId = bundle.getString("sender_id");
                        final String ReceiverId = bundle.getString("receiver_id");
                        if (Type != null && Type.equals(New_Request)) {
                            replaceDockableFragment(ServiceHistorySpFragment.newInstance(), "ServiceHistorySpFragment");
                        } else if (Type != null && Type.equals(Rejected_Request)) {
                            replaceDockableFragment(ServiceHistoryUserFragment.newInstance(), "ServiceHistoryUserFragment");
                        } else if (Type != null && Type.equals(Accept_Request)) {
                            replaceDockableFragment(ServiceHistoryUserFragment.newInstance(), "ServiceHistoryUserFragment");
                        } else if (Type != null && Type.equals(friend_request)) {
                            replaceDockableFragment(FriendRequestFragment.newInstance(), "FriendRequestFragment");
                        }/*else if (Type != null && Type.equals(accept_request)) {
                            replaceDockableFragment(FannsFragment.newInstance(), "FannsFragment");
                        }else if (Type != null && Type.equals(messagePush)) {
                            replaceDockableFragment(InboxChatFragment.newInstance(), "InboxChatFragment");
                        }*/ else if (Type != null && Type.equals(Complete_Request)) {
                            final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                            dialogHelper.initRating(R.layout.submit_rating_dialoge, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    feedbackService(RequestId, SenderId, ReceiverId, dialogHelper.getRatingTextView().getText().toString(), dialogHelper.getRatingScore(), dialogHelper);

                                }
                            });
                            dialogHelper.setCancelable(false);
                            dialogHelper.showDialog();
                        } else if (Type != null && Type.equals(block_user)) {

                            final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                            dialogHelper.alertDialoge(R.layout.alert_dialoge, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogHelper.hideDialog();
                                }
                            }, getDockActivity().getResources().getString(R.string.blocked_by_admin));

                            dialogHelper.showDialog();
                            getDockActivity().popBackStackTillEntry(0);
                            prefHelper.setLoginStatus(false);
                            replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");

                        } else if (Type != null && Type.equals(delete_user)) {
                            final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                            dialogHelper.alertDialoge(R.layout.alert_dialoge, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialogHelper.hideDialog();
                                }
                            }, getString(R.string.account_deleted_by_admin));

                            dialogHelper.showDialog();
                            prefHelper.setLoginStatus(false);
                            getDockActivity().popBackStackTillEntry(0);
                            replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");
                        }
                    }

                } else {
                    UIHelper.showShortToastInCenter(getDockActivity(), "Notification Data is Empty");
                }
            }

        };
    }

    private void feedbackService(String requestId, String senderId, String receiverId, String text, Float ratingScore, DialogHelper dialogHelper) {

        String userId = "";
        if (senderId.equals(String.valueOf(prefHelper.getUser().getId()))) {
            userId = receiverId;
        } else {
            userId = senderId;
        }

        if (text != null && !text.equals("")) {
            dialogHelper.hideDialog();
            serviceHelper.enqueueCall(headerWebService.feedback(userId, requestId, String.valueOf(Math.round(ratingScore)), text), feedback);
        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), "Write Feedback to proceed");
        }


    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        switch (Tag) {

            case feedback:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                getDockActivity().popBackStackTillEntry(0);
                getDockActivity().replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                break;
        }
    }


    public void setImageSetter(ImageSetter imageSetter) {
        this.imageSetter = imageSetter;
    }

    public View getDrawerView() {
        return getLayoutInflater().inflate(getSideMenuFrameLayoutId(), null);
    }

    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public void closeDrawer() {
        drawerLayout.closeDrawers();

    }

    public void lockDrawer() {
        try {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void releaseDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void settingSideMenu(String type, String direction) {
        if (type.equals(SideMenuChooser.DRAWER.getValue())) {


            DrawerLayout.LayoutParams params = new DrawerLayout.LayoutParams((int) getResources().getDimension(R.dimen.x300), (int) DrawerLayout.LayoutParams.MATCH_PARENT);


            if (direction.equals(SideMenuDirection.LEFT.getValue())) {
                params.gravity = Gravity.LEFT;
                sideMneuFragmentContainer.setLayoutParams(params);
            } else {
                params.gravity = Gravity.RIGHT;
                sideMneuFragmentContainer.setLayoutParams(params);
            }


            sideMenuFragment = SideMenuFragment.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.replace(getSideMenuFrameLayoutId(), sideMenuFragment).commit();

            drawerLayout.closeDrawers();
        } else {
            resideMenu = new ResideMenu(this);
            resideMenu.attachToActivity(this);
            resideMenu.setMenuListener(getMenuListener());
            resideMenu.setScaleValue(0.52f);

            setMenuItemDirection(direction);
        }
    }

    private void setMenuItemDirection(String direction) {

        if (direction.equals(SideMenuDirection.LEFT.getValue())) {

            SideMenuFragment leftSideMenuFragment = SideMenuFragment.newInstance();
            resideMenu.addMenuItem(leftSideMenuFragment, "LeftSideMenuFragment", direction);

        } else if (direction.equals(SideMenuDirection.RIGHT.getValue())) {

            SideMenuFragment rightSideMenuFragment = SideMenuFragment.newInstance();
            resideMenu.addMenuItem(rightSideMenuFragment, "RightSideMenuFragment", direction);

        }

    }

    private int getSideMenuFrameLayoutId() {
        return R.id.sideMneuFragmentContainer;

    }


    public void initFragment() {
        getSupportFragmentManager().addOnBackStackChangedListener(getListener());
        if (prefHelper.isLogin()) {
            getDockActivity().popBackStackTillEntry(0);
            replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
        } else {
            replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String Type = bundle.getString("pushtype");
            final String RequestId = bundle.getString("request_id");
            final String SenderId = bundle.getString("sender_id");
            final String ReceiverId = bundle.getString("receiver_id");

            if (Type != null && Type.equals(messagePush)) {
                inboxChat(SenderId, ReceiverId);
            } else if (Type != null && Type.equals(New_Request)) {
                replaceDockableFragment(ServiceHistorySpFragment.newInstance(), "ServiceHistorySpFragment");
            } else if (Type != null && Type.equals(Rejected_Request)) {
                replaceDockableFragment(HomeFragment.newInstance(), "HomeMapFragment");
            } else if (Type != null && Type.equals(Accept_Request)) {
                replaceDockableFragment(ServiceHistoryUserFragment.newInstance(), "ServiceHistoryUserFragment");
            } else if (Type != null && Type.equals(friend_request)) {
                replaceDockableFragment(FriendRequestFragment.newInstance(), "FriendRequestFragment");
            } else if (Type != null && Type.equals(accept_request)) {
                replaceDockableFragment(FannsFragment.newInstance(), "FannsFragment");
            } else if (Type != null && Type.equals(Complete_Request)) {
                final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                dialogHelper.initRating(R.layout.submit_rating_dialoge, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogHelper.hideDialog();
                    }
                });
                dialogHelper.showDialog();
            } else if (Type != null && Type.equals(block_user)) {

               /* final DialogHelper dialogHelper=new DialogHelper(getDockActivity());
                dialogHelper.alertDialoge(R.layout.alert_dialoge, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogHelper.hideDialog();
                    }
                },getDockActivity().getResources().getString(R.string.blocked_by_admin));

                dialogHelper.showDialog();*/
                prefHelper.setLoginStatus(false);
                getDockActivity().popBackStackTillEntry(0);
                replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");

            } else if (Type != null && Type.equals(delete_user)) {
              /*  final DialogHelper dialogHelper=new DialogHelper(getDockActivity());
                dialogHelper.alertDialoge(R.layout.alert_dialoge, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogHelper.hideDialog();
                    }
                },"Account has been deleted by admin");

                dialogHelper.showDialog();*/
                prefHelper.setLoginStatus(false);
                getDockActivity().popBackStackTillEntry(0);
                replaceDockableFragment(LoginFragment.newInstance(), "LoginFragment");
            }
        }
    }

    private void inboxChat(String senderId, String receiverId) {

        String userId = "";
        if (senderId.equals(prefHelper.getUser().getId())) {
            userId = receiverId;
        } else {
            userId = senderId;
        }

        replaceDockableFragment(InboxChatFragment.newInstance(userId, true), "InboxChatFragment");
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();

                if (manager != null) {
                    BaseFragment currFrag = (BaseFragment) manager.findFragmentById(getDockFrameLayoutId());
                    if (currFrag != null) {
                        currFrag.fragmentResume();
                    }
                }
            }
        };

        return result;
    }


    @Override
    public void onLoadingStarted() {

        if (mainFrameLayout != null) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            mainFrameLayout.setVisibility(View.VISIBLE);
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
            loading = true;
        }
    }

    @Override
    public void onLoadingFinished() {
        mainFrameLayout.setVisibility(View.VISIBLE);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        if (progressBar != null) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        loading = false;

    }

    @Override
    public void onProgressUpdated(int percentLoaded) {

    }

    @Override
    public int getDockFrameLayoutId() {
        return R.id.mainFrameLayout;
    }

    @Override
    public void onMenuItemActionCalled(int actionId, String data) {

    }

    @Override
    public void setSubHeading(String subHeadText) {

    }

    @Override
    public boolean isLoggedIn() {
        return false;
    }

    @Override
    public void hideHeaderButtons(boolean leftBtn, boolean rightBtn) {
    }

    @Override
    public void onBackPressed() {
        if (loading) {
            UIHelper.showLongToastInCenter(getApplicationContext(),
                    R.string.message_wait);
        } else
            super.onBackPressed();

    }

    @Override
    public void onClick(View view) {

    }

    public void notImplemented() {
        UIHelper.showLongToastInCenter(this, "will be implemented in future version");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
            if (imageChooserManager == null) {
                reinitializeImageChooser();
            }
            imageChooserManager.submit(requestCode, data);
        } else {
            // progressBar.setVisibility(View.GONE);
        }

    }

    private void reinitializeImageChooser() {
        imageChooserManager = new ImageChooserManager(this, chooserType, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.reinitialize(filePath);
    }

    public void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageChooserManager.setExtras(bundle);
        imageChooserManager.setImageChooserListener(this);
        imageChooserManager.clearOldFiles();
        try {
            //pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            //pbar.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Chosen Image: O - " + image.getFilePathOriginal());
                Log.i(TAG, "Chosen Image: T - " + image.getFileThumbnail());
                Log.i(TAG, "Chosen Image: Ts - " + image.getFileThumbnailSmall());
                isActivityResultOver = true;
                originalFilePath = image.getFilePathOriginal();
                thumbnailFilePath = image.getFileThumbnail();
                thumbnailSmallFilePath = image.getFileThumbnailSmall();
                //pbar.setVisibility(View.GONE);
                if (image != null) {
                    Log.i(TAG, "Chosen Image: Is not null");

                    // Toast.makeText(getApplication(),thumbnailFilePath,Toast.LENGTH_LONG).show();
                    imageSetter.setImage(originalFilePath);

                    //loadImage(imageViewThumbnail, image.getFileThumbnail());
                } else {
                    Log.i(TAG, "Chosen Image: Is null");
                }

            }
        });

    }

    @Override
    public void onError(final String reason) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "OnError: " + reason);
                // pbar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, reason,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onImagesChosen(final ChosenImages images) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "On Images Chosen: " + images.size());
                onImageChosen(images.getImage(0));
            }
        });

    }


    public void showBottomBar(String type) {
        setBottomTabStyle(type);
        llBottomtabLayout.setVisibility(View.VISIBLE);
    }

    public void hideBottomBar() {
        llBottomtabLayout.setVisibility(View.GONE);
    }

    private void setBottomTabStyle(String type) {

        if (type.equals(AppConstants.home)) {
            ivHome.setImageResource(R.drawable.home2);
            ivMessage.setImageResource(R.drawable.news_feed);
            ivSearch.setImageResource(R.drawable.search2);
            ivFavorite.setImageResource(R.drawable.bottomheart);
            ivProfile.setImageResource(R.drawable.profile);

            llHome.setBackgroundColor(getResources().getColor(R.color.app_blue));
            llMessage.setBackgroundColor(getResources().getColor(R.color.transparent));
            llSearch.setBackgroundColor(getResources().getColor(R.color.transparent));
            llFavorite.setBackgroundColor(getResources().getColor(R.color.transparent));
            llProfile.setBackgroundColor(getResources().getColor(R.color.transparent));
        } else if (type.equals(AppConstants.message)) {
            ivHome.setImageResource(R.drawable.home);
            ivMessage.setImageResource(R.drawable.message5);
            ivSearch.setImageResource(R.drawable.search2);
            ivFavorite.setImageResource(R.drawable.bottomheart);
            ivProfile.setImageResource(R.drawable.profile);

            llHome.setBackgroundColor(getResources().getColor(R.color.transparent));
            llMessage.setBackgroundColor(getResources().getColor(R.color.app_blue));
            llSearch.setBackgroundColor(getResources().getColor(R.color.transparent));
            llFavorite.setBackgroundColor(getResources().getColor(R.color.transparent));
            llProfile.setBackgroundColor(getResources().getColor(R.color.transparent));

        } else if (type.equals(AppConstants.search)) {
            ivHome.setImageResource(R.drawable.home);
            ivMessage.setImageResource(R.drawable.news_feed);
            ivSearch.setImageResource(R.drawable.search1);
            ivFavorite.setImageResource(R.drawable.bottomheart);
            ivProfile.setImageResource(R.drawable.profile);

            llHome.setBackgroundColor(getResources().getColor(R.color.transparent));
            llMessage.setBackgroundColor(getResources().getColor(R.color.transparent));
            llSearch.setBackgroundColor(getResources().getColor(R.color.app_blue));
            llFavorite.setBackgroundColor(getResources().getColor(R.color.transparent));
            llProfile.setBackgroundColor(getResources().getColor(R.color.transparent));
        } else if (type.equals(AppConstants.favorite)) {
            ivHome.setImageResource(R.drawable.home);
            ivMessage.setImageResource(R.drawable.news_feed);
            ivSearch.setImageResource(R.drawable.search2);
            ivFavorite.setImageResource(R.drawable.bottomheart2);
            ivProfile.setImageResource(R.drawable.profile);

            llHome.setBackgroundColor(getResources().getColor(R.color.transparent));
            llMessage.setBackgroundColor(getResources().getColor(R.color.transparent));
            llSearch.setBackgroundColor(getResources().getColor(R.color.transparent));
            llFavorite.setBackgroundColor(getResources().getColor(R.color.app_blue));
            llProfile.setBackgroundColor(getResources().getColor(R.color.transparent));

        } else if (type.equals(AppConstants.profile)) {
            ivHome.setImageResource(R.drawable.home);
            ivMessage.setImageResource(R.drawable.news_feed);
            ivSearch.setImageResource(R.drawable.search2);
            ivFavorite.setImageResource(R.drawable.bottomheart);
            ivProfile.setImageResource(R.drawable.profile5);

            llHome.setBackgroundColor(getResources().getColor(R.color.transparent));
            llMessage.setBackgroundColor(getResources().getColor(R.color.transparent));
            llSearch.setBackgroundColor(getResources().getColor(R.color.transparent));
            llFavorite.setBackgroundColor(getResources().getColor(R.color.transparent));
            llProfile.setBackgroundColor(getResources().getColor(R.color.app_blue));

        }

    }


    @OnClick({R.id.ll_home, R.id.ll_message, R.id.ll_search, R.id.ll_favorite, R.id.ll_profile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_home:
                setBottomTabStyle(AppConstants.home);
                getDockActivity().popBackStackTillEntry(0);
                replaceDockableFragment(HomeFragment.newInstance(), "HomeFragment");
                break;
            case R.id.ll_message:
                setBottomTabStyle(AppConstants.message);
                popFragment();
                replaceDockableFragment(MessageFragment.newInstance(), "MessageFragment");
                break;
            case R.id.ll_search:
                setBottomTabStyle(AppConstants.search);
                popFragment();
                replaceDockableFragment(HomeSearchFragment.newInstance(), "HomeSearchFragment");
                break;
            case R.id.ll_favorite:
                setBottomTabStyle(AppConstants.favorite);
                popFragment();
                replaceDockableFragment(FavoriteFragment.newInstance(), "FavoriteFragment");
                break;
            case R.id.ll_profile:
                setBottomTabStyle(AppConstants.profile);
                popFragment();
                if (prefHelper.isUserSelected()) {
                    replaceDockableFragment(UserProfileFragment.newInstance(), "UserProfileFragment");
                } else {
                    replaceDockableFragment(SpProfileFragment.newInstance(), "SpProfileFragment");
                }
                break;
        }
    }

    public void hideSoftKeyboard(Context context, EditText editText) {

        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public boolean statusCheck() {
        if (isConnected(getApplicationContext())) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                turnLocationOn(null);
                buildAlertMessageNoGps(R.string.gps_question, Settings.ACTION_LOCATION_SOURCE_SETTINGS, LocationResultCode);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMob = cm.getNetworkInfo(cm.TYPE_MOBILE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(cm.TYPE_WIFI);
        if (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) {
            Log.v("TAG", "Mobile Internet connected");
            return true;
        }
        if (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting()) {
            Log.v("TAG", "Wifi Internet connected");
            return true;
        }
        buildAlertMessageNoGps(R.string.wifi_question, Settings.ACTION_WIFI_SETTINGS, WifiResultCode);
        return false;
      /*  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo drive_mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((drive_mobile != null && drive_mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else {
                //isConnected(getApplicationContext());
                return false;
            }
        } else {
            buildAlertMessageNoGps(R.string.wifi_question, Settings.ACTION_WIFI_SETTINGS, WifiResultCode);
            return false;
        }*/
    }

    private void buildAlertMessageNoGps(final int StringResourceID, final String IntentType, final int requestCode) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getDockActivity());
        builder
                .setMessage(getString(StringResourceID))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.gps_yes), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        if (StringResourceID == R.string.gps_question) {
                            dialog.cancel();
                            turnLocationOn(null);
                            dialog.dismiss();
                        } else {
                            dialog.cancel();
                            startImpIntent(dialog, IntentType, requestCode);
                            dialog.dismiss();
                        }

                        // startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),LocationResultCode);
                    }
                })
                .setNegativeButton(getResources().getString(R.string.gps_no), new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.dismiss();
                        dialog.cancel();

                    }
                });
        if (alert == null) {
            alert = builder.create();

        }

        if (!alert.isShowing())
            alert.show();

    }

    public void turnLocationOn(GoogleApiClient apiClient) {
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            apiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(apiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All drive_location drive_settings are satisfied. The client can initialize drive_location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location drive_settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(
                                        getDockActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location drive_settings are not satisfied. However, we have no way to fix the
                            // drive_settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }


    private void startImpIntent(DialogInterface dialog, String IntentType, int requestCode) {
        dialog.dismiss();
        Intent i = new Intent(IntentType);
        i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(i, requestCode);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void ResponseFailure(String tag) {

    }
}
