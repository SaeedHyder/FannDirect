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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.fandirect.R;
import com.app.fandirect.entities.NotificationCount;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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
import butterknife.Unbinder;

import static com.app.fandirect.global.WebServiceConstants.NotifcationCount;
import static com.app.fandirect.global.WebServiceConstants.deletePost;
import static com.app.fandirect.global.WebServiceConstants.favoritePost;
import static com.app.fandirect.global.WebServiceConstants.getAllPosts;
import static com.app.fandirect.global.WebServiceConstants.getMyPosts;
import static com.app.fandirect.global.WebServiceConstants.postLike;
import static com.app.fandirect.global.WebServiceConstants.reportPost;
import static com.app.fandirect.global.WebServiceConstants.reportUser;

/**
 * Created by saeedhyder on 3/14/2018.
 */
public class WallPostsFragment extends BaseFragment implements RecyclerViewItemListener ,PostClicksInterface,ReportPostIntetface{
    @BindView(R.id.txt_name)
    AnyTextView txtName;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.lv_feeds)
    ExpandedListView lvFeeds;
    Unbinder unbinder;
    private ArrayListAdapter<Post> Adapter;
    private ArrayList<Post> Collection;
    private Post postEntity;
    private InterstitialAd interstitialAd;



    public static WallPostsFragment newInstance() {
        Bundle args = new Bundle();

        WallPostsFragment fragment = new WallPostsFragment();
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
        Adapter = new ArrayListAdapter<Post>(getDockActivity(), new FeedsBinder(getDockActivity(), prefHelper, this,this,this));
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall_posts, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().showBottomBar(AppConstants.search);
        adMobListner();
       // onNotificationReceived();
        serviceHelper.enqueueCall(headerWebService.getMyPosts(),getMyPosts);
       // serviceHelper.enqueueCall(headerWebService.getNotificaitonCount(), NotifcationCount);
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

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case getMyPosts:
                ArrayList<Post> entity=(ArrayList<Post>)result;
                setWallPostListData(entity);
                break;

            case NotifcationCount:
                prefHelper.setNotificationCount(((NotificationCount) result).getNotification_count());
                if(getTitleBar()!=null){
               //     getTitleBar().showNotification(prefHelper.getNotificationCount());
                }
                break;

            case postLike:
                UIHelper.showShortToastInCenter(getDockActivity(),message);
                break;

            case favoritePost:
                UIHelper.showShortToastInCenter(getDockActivity(),message);
                break;

            case deletePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                serviceHelper.enqueueCall(headerWebService.getMyPosts(),getMyPosts);
                break;

            case reportPost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case reportUser:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

        }
    }

    private void setWallPostListData(ArrayList<Post> entity) {

        if(entity.size()>0){
            lvFeeds.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        }
        else {
            lvFeeds.setVisibility(View.GONE);
            txtNoData.setVisibility(View.VISIBLE);
        }

        Adapter.clearList();
        lvFeeds.setAdapter(Adapter);
        Adapter.addAll(entity);
        Adapter.notifyDataSetChanged();
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

       /* getDockActivity().onLoadingStarted();
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

       getDockActivity().replaceDockableFragment(CommentFragment.newInstance(entity.getId()+""),"CommentFragment");
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

    @Override
    public void onResume() {
        super.onResume();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

   /* @Override
    public void onResume() {
        super.onResume();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
