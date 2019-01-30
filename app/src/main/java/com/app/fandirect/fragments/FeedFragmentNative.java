package com.app.fandirect.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.fandirect.R;
import com.app.fandirect.entities.NotificationCount;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.ShareIntentHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ImageSetter;
import com.app.fandirect.interfaces.PostClicksInterface;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.ReportPostIntetface;
import com.app.fandirect.ui.adapters.AdapterRecyclerviewAdMob;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.NativeExpressAdView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.ITEM_PER_AD;
import static com.app.fandirect.global.WebServiceConstants.NotifcationCount;
import static com.app.fandirect.global.WebServiceConstants.deletePost;
import static com.app.fandirect.global.WebServiceConstants.favoritePost;
import static com.app.fandirect.global.WebServiceConstants.getAllPosts;
import static com.app.fandirect.global.WebServiceConstants.postLike;
import static com.app.fandirect.global.WebServiceConstants.reportPost;
import static com.app.fandirect.global.WebServiceConstants.reportUser;


/**
 * Created by saeedhyder on 3/13/2018.
 */
public class FeedFragmentNative extends BaseFragment implements RecyclerViewItemListener, ImageSetter, PostClicksInterface, ReportPostIntetface {


    @BindView(R.id.txt_search)
    AnyEditTextView txtSearch;
    @BindView(R.id.iv_search_icon)
    ImageView ivSearchIcon;
    @BindView(R.id.ll_search)
    LinearLayout llSearch;
    @BindView(R.id.ll_search_box)
    LinearLayout llSearchBox;
    @BindView(R.id.image)
    CircleImageView image;
    @BindView(R.id.txt_update_post)
    AnyTextView txtUpdatePost;
    @BindView(R.id.ll_post)
    LinearLayout llPost;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_feeds)
    RecyclerView lvFeeds;
    Unbinder unbinder;
    private ImageLoader imageLoader;

    private Post postEntity;
    private ArrayListAdapter<Post> Adapter;
    private ArrayList<Object> collection;
    private InterstitialAd interstitialAd;
    private AdapterRecyclerviewAdMob mAdapter;


    public static final int NATIVE_EXPRESS_AD_HEIGHT = 150;


    public static FeedFragmentNative newInstance() {
        Bundle args = new Bundle();

        FeedFragmentNative fragment = new FeedFragmentNative();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        interstitialAd = new InterstitialAd(getDockActivity());
        interstitialAd.setAdUnitId(getDockActivity().getResources().getString(R.string.ad_unit_id_interstitial));
        final AdRequest adRequest = new AdRequest.Builder()
                .build();
        interstitialAd.loadAd(adRequest);

        imageLoader = ImageLoader.getInstance();
        // Adapter = new ArrayListAdapter<Post>(getDockActivity(), new FeedsBinder(getDockActivity(), prefHelper, this, this, this));

        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feeds, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().setImageSetter(this);
        getMainActivity().showBottomBar(AppConstants.search);
      //  onNotificationReceived();
        adMobListner();

        if (prefHelper.getUser().getImageUrl() != null) {
            imageLoader.displayImage(prefHelper.getUser().getImageUrl() + "", image);
        }
        lvFeeds.setNestedScrollingEnabled(false);

        serviceHelper.enqueueCall(headerWebService.getAllPosts(), getAllPosts);
        //serviceHelper.enqueueCall(headerWebService.getNotificaitonCount(), NotifcationCount);

        setTextWatecher();

    }


    private void adMobListner() {

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                } else {
                    Toast.makeText(getDockActivity(), "Ad did not load", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });
    }

    private void setTextWatecher() {
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                bindData(getSearchedArray(s.toString()));
            }
        });
    }

    public ArrayList<Object> getSearchedArray(String keyword) {
        if (collection.isEmpty()) {
            return new ArrayList<>();
        }

        ArrayList<Object> arrayList = new ArrayList<>();

        String UserName = "";
        for (Object item : collection) {
            UserName = ((Post) item).getUserDetail().getUserName();
          /*  if (UserName.contains(keyword)) {
                arrayList.add(item);
            }*/
            if (Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE).matcher(UserName).find()) {
                arrayList.add(item);
            }
        }
        return arrayList;

    }


    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showBackButton();
       /* titleBar.showNotificationBell(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDockActivity().replaceDockableFragment(NotificationFragment.newInstance(), "NotificationFragment");
            }
        },prefHelper.getNotificationCount());*/
       /* titleBar.showMessageBtn(new View.OnClickListener() {
            @Override
            public void onUserProfileClick(View v) {
                getDockActivity().replaceDockableFragment(MessageFragment.newInstance(), "MessageFragment");
            }
        });*/
    }


    @Override
    public void onRecyclerItemClicked(Object Ent, int position) {
        getMainActivity().notImplemented();
    }

    @Override
    public void onCategoryClick(Object Ent, int position) {

    }

    @OnClick({R.id.ll_post})
    public void onViewClicked(View view) {

        switch (view.getId()) {

            case R.id.ll_post:
              //  getMainActivity().popFragment();
                getDockActivity().replaceDockableFragment(AddPostFragment.newInstance(false), "NewPostFragment");
                break;
        }
    }


    @Override
    public void setImage(String imagePath) {
       /* if (imagePath != null) {
            getDockActivity().replaceDockableFragment(AddPostFragment.newInstance(imagePath, false, txtPost.getText().toString()), "NewPostFragment");
        }*/
    }

    @Override
    public void setFilePath(String filePath) {

    }

    @Override
    public void setVideo(String videoPath, String VideoThumbail) {

    }


    private void setFeedsListData(ArrayList<Post> entity) {

        collection = new ArrayList<>();
        collection.addAll(entity);
        addNativeExpressAds();
        setUpAndLoadNativeExpressAds();
        bindData(collection);

    }

    private void bindData(ArrayList<Object> collection) {

        if (collection.size() > 0) {
            lvFeeds.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvFeeds.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

        mAdapter = new AdapterRecyclerviewAdMob(this.collection, getDockActivity(), getMainActivity(), prefHelper, this, this, this);
        lvFeeds.setLayoutManager(new LinearLayoutManager(getDockActivity(), LinearLayoutManager.VERTICAL, false));
        lvFeeds.setItemAnimator(new DefaultItemAnimator());
        lvFeeds.setAdapter(mAdapter);

      /*  lvFeeds.BindRecyclerView(new FeedsRecyclerBinder(getDockActivity(), prefHelper, this, this, this), collection,
                new LinearLayoutManager(getDockActivity(), LinearLayoutManager.VERTICAL, false)
                , new DefaultItemAnimator());*/

      /*  Adapter.clearList();
        lvFeeds.setAdapter(Adapter);
        Adapter.addAll(collection);*/
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

        postEntity = entity;
        requestStoragePermission();

      /*  getDockActivity().onLoadingStarted();

        if (entity.getImageUrl() != null && !entity.getImageUrl().equals("") && entity.getDescription() != null && !entity.getDescription().equals("")) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), entity.getDescription());
        } else if (entity.getImageUrl() != null && entity.getDescription() == null) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), "");
        } else if (entity.getImageUrl() == null && entity.getDescription() != null) {
            ShareIntentHelper.shareTextIntent(getDockActivity(), entity.getDescription());
        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), "Description is not avaliable");
        }*/

    }

    @Override
    public void comment(Post entity, int position) {
        getDockActivity().replaceDockableFragment(CommentFragment.newInstance(entity.getId() + ""), "CommentFragment");
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

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {


            case getAllPosts:
                ArrayList<Post> entity = (ArrayList<Post>) result;
                setFeedsListData(entity);
                break;

            case NotifcationCount:
                prefHelper.setNotificationCount(((NotificationCount) result).getNotification_count());
                if(getTitleBar()!=null){
                //    getTitleBar().showNotification(prefHelper.getNotificationCount());
                }
                break;

            case postLike:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case favoritePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case deletePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                serviceHelper.enqueueCall(headerWebService.getAllPosts(), getAllPosts);
                break;

            case reportPost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case reportUser:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;


        }
    }


    private void requestStoragePermission() {
        Dexter.withActivity(getDockActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            getDockActivity().onLoadingStarted();

                            if (postEntity.getImageUrl() != null && !postEntity.getImageUrl().equals("") && postEntity.getDescription() != null && !postEntity.getDescription().equals("")) {
                                ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), postEntity.getImageUrl(), postEntity.getDescription());
                            } else if (postEntity.getImageUrl() != null && postEntity.getDescription() == null) {
                                ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), postEntity.getImageUrl(), "");
                            } else if (postEntity.getImageUrl() == null && postEntity.getDescription() != null) {
                                ShareIntentHelper.shareTextIntent(getDockActivity(), postEntity.getDescription());
                            } else {
                                UIHelper.showShortToastInCenter(getDockActivity(), "Description is not avaliable");
                            }
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            requestStoragePermission();

                        } else if (report.getDeniedPermissionResponses().size() > 0) {
                            requestStoragePermission();
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
                        UIHelper.showShortToastInCenter(getDockActivity(), "Grant Storage Permission to processed");
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

    private void addNativeExpressAds() {

        for (int i = 0; i <= collection.size(); i += ITEM_PER_AD) {
            final NativeExpressAdView adView = new NativeExpressAdView(getDockActivity());
            collection.add(i, adView);
        }
    }

    private void setUpAndLoadNativeExpressAds() {
        lvFeeds.post(new Runnable() {
            @Override
            public void run() {
                final float scale = getDockActivity().getResources().getDisplayMetrics().density;

                for (int i = 0; i <= collection.size(); i += ITEM_PER_AD) {
                    final NativeExpressAdView adView = (NativeExpressAdView) collection.get(i);
                    AdSize adSize = new AdSize((int) (300 / scale), NATIVE_EXPRESS_AD_HEIGHT);
                    adView.setAdSize(adSize);
                    adView.setAdUnitId(getDockActivity().getResources().getString(R.string.ad_unit_id_native));

                    loadNativeExpressAd(0);
                }
            }
        });
    }

    private void loadNativeExpressAd(final int index) {

        if (index >= collection.size()) {
            return;
        }

        Object item = collection.get(index);
        if (!(item instanceof NativeExpressAdView)) {
            throw new ClassCastException("Expected item at index " + index + "to be a Native");
        }

        final NativeExpressAdView adView = (NativeExpressAdView) item;

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                loadNativeExpressAd(index + ITEM_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                loadNativeExpressAd(index + ITEM_PER_AD);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*  @Override
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

    protected BroadcastReceiver broadcastReceiver;

    private void onNotificationReceived() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(AppConstants.PUSH_NOTIFICATION)) {
                    if(getTitleBar()!=null){
                        getTitleBar().showNotification(prefHelper.getNotificationCount());
                    }
                }
            }
        };
    }*/


}


