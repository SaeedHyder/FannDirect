package com.app.fandirect.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.FanStatus;
import com.app.fandirect.entities.GetMyFannsEnt;
import com.app.fandirect.entities.GetProfileEnt;
import com.app.fandirect.entities.NotificationCount;
import com.app.fandirect.entities.Picture;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.InternetHelper;
import com.app.fandirect.helpers.ShareIntentHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.PostClicksInterface;
import com.app.fandirect.interfaces.ReportPostIntetface;
import com.app.fandirect.interfaces.UserItemClickInterface;
import com.app.fandirect.ui.adapters.ArrayListAdapter;
import com.app.fandirect.ui.binders.PostItemBinder;
import com.app.fandirect.ui.binders.ProfileFannsBinder;
import com.app.fandirect.ui.binders.ProfilePicturesGridItemBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.ExpandableGridView;
import com.app.fandirect.ui.views.ExpandedListView;
import com.app.fandirect.ui.views.TitleBar;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import me.leolin.shortcutbadger.ShortcutBadger;

import static com.app.fandirect.global.AppConstants.REQUEST_ACCEPTED;
import static com.app.fandirect.global.AppConstants.REQUEST_CANCELLED;
import static com.app.fandirect.global.AppConstants.REQUEST_PENDING;
import static com.app.fandirect.global.AppConstants.REQUEST_UNFRIEND;
import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.AppConstants.accept_request;
import static com.app.fandirect.global.AppConstants.accepted;
import static com.app.fandirect.global.AppConstants.cancel_request;
import static com.app.fandirect.global.AppConstants.declined_request;
import static com.app.fandirect.global.AppConstants.friend_request;
import static com.app.fandirect.global.AppConstants.unfriend;
import static com.app.fandirect.global.WebServiceConstants.AddFann;
import static com.app.fandirect.global.WebServiceConstants.AddFannProfile;
import static com.app.fandirect.global.WebServiceConstants.GetProfile;
import static com.app.fandirect.global.WebServiceConstants.GetProfilePushNotification;
import static com.app.fandirect.global.WebServiceConstants.NotifcationCount;
import static com.app.fandirect.global.WebServiceConstants.PostDeleteProfile;
import static com.app.fandirect.global.WebServiceConstants.Unfriend;
import static com.app.fandirect.global.WebServiceConstants.UnfriendProfile;
import static com.app.fandirect.global.WebServiceConstants.UpdateProfile;
import static com.app.fandirect.global.WebServiceConstants.cancelled;
import static com.app.fandirect.global.WebServiceConstants.cancelledProfile;
import static com.app.fandirect.global.WebServiceConstants.confirmRequest;
import static com.app.fandirect.global.WebServiceConstants.confirmRequestProfile;
import static com.app.fandirect.global.WebServiceConstants.deletePost;
import static com.app.fandirect.global.WebServiceConstants.favoritePost;
import static com.app.fandirect.global.WebServiceConstants.postLike;
import static com.app.fandirect.global.WebServiceConstants.reportPost;
import static com.app.fandirect.global.WebServiceConstants.reportUser;
import static com.app.fandirect.global.WebServiceConstants.showProfile;

/**
 * Created by saeedhyder on 3/1/2018.
 */
public class UserProfileFragment extends BaseFragment implements PostClicksInterface, UserItemClickInterface, ReportPostIntetface {
    @BindView(R.id.iv_profile_image)
    CircleImageView ivProfileImage;
    @BindView(R.id.txt_profileName)
    AnyTextView txtProfileName;
    @BindView(R.id.txt_work_at)
    AnyTextView txtWorkAt;
    @BindView(R.id.txt_about)
    AnyTextView txtAbout;
    @BindView(R.id.txt_birthday)
    AnyTextView txtBirthday;
    @BindView(R.id.txt_location)
    AnyTextView txtLocation;
    @BindView(R.id.txt_profession)
    AnyTextView txtProfession;
    @BindView(R.id.txt_hobbies)
    AnyTextView txtHobbies;
    @BindView(R.id.btn_edit_profile)
    Button btnEditProfile;
    @BindView(R.id.txt_posts)
    AnyTextView txtPosts;
    @BindView(R.id.txt_pictures)
    AnyTextView txtPictures;
    @BindView(R.id.txt_fanns)
    AnyTextView txtFanns;
    @BindView(R.id.gv_pictures)
    ExpandableGridView gvPictures;
    Unbinder unbinder;
    @BindView(R.id.lv_fanns)
    ExpandedListView lvFanns;
    @BindView(R.id.lv_posts)
    ExpandedListView lvPosts;
    @BindView(R.id.btn_fanns)
    AnyTextView btnFanns;
    @BindView(R.id.btn_send_message)
    AnyTextView btnSendMessage;
    @BindView(R.id.ll_two_button)
    LinearLayout llTwoButton;
    @BindView(R.id.rl_fann)
    RelativeLayout rlFann;
    ImageLoader imageLoader;
    @BindView(R.id.txt_no_data)
    AnyTextView txtNoData;
    @BindView(R.id.mainFrameLayout)
    LinearLayout mainFrameLayout;
    @BindView(R.id.btn_request)
    AnyTextView btnRequest;

    protected BroadcastReceiver broadcastReceiver;
    @BindView(R.id.txt_mutual_fanns)
    AnyTextView txtMutualFanns;
    @BindView(R.id.txt_education)
    AnyTextView txtEducation;
    @BindView(R.id.ll_user_information)
    LinearLayout llUserInformation;
    @BindView(R.id.user_deleted)
    AnyTextView userDeleted;

    private ArrayListAdapter<Picture> pictureAdapter;
    private ArrayList<Picture> picturesCollection;

    private ArrayListAdapter<GetMyFannsEnt> fannsAdapter;
    private ArrayList<GetMyFannsEnt> fannsCollection;

    private ArrayListAdapter<Post> postAdapter;
    private ArrayList<Post> postCollection;
    private GetProfileEnt entity;

    private static String userIdKey = "userIdKey";
    private String userId;


    private String postCount = "";
    private String picturesCount = "";
    private String fannCount = "";
    private String mutualfannCount = "";
    private int pos = 0;
    private int friendPosition;
    private DialogHelper dialogHelper;

    private boolean isMutual = false;

    public static UserProfileFragment newInstance() {
        Bundle args = new Bundle();

        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public static UserProfileFragment newInstance(String userId) {
        Bundle args = new Bundle();
        args.putString(userIdKey, userId);
        UserProfileFragment fragment = new UserProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();

        pictureAdapter = new ArrayListAdapter<Picture>(getDockActivity(), new ProfilePicturesGridItemBinder(getDockActivity(), prefHelper));
        postAdapter = new ArrayListAdapter<Post>(getDockActivity(), new PostItemBinder(getDockActivity(), prefHelper, this, this));

        if (getArguments() != null) {

            userId = getArguments().getString(userIdKey);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getMainActivity().hideAdds();
        mainFrameLayout.setVisibility(View.GONE);


        if (prefHelper.getUser() != null && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
            llTwoButton.setVisibility(View.VISIBLE);
            btnEditProfile.setVisibility(View.GONE);
        } else {
            llTwoButton.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.VISIBLE);
        }

        btnFanns.setClickable(false);
        selectFirstTab();
        setData();

        onNotificationReceived();

        getMainActivity().showBottomBar(AppConstants.profile);
        // getDockActivity().getSupportFragmentManager().addOnBackStackChangedListener(getListener());
    }

   /* private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {

                FragmentManager manager = getDockActivity().getSupportFragmentManager();
                if (manager != null) {
                    Fragment currFrag = manager.findFragmentById(getDockActivity().getDockFrameLayoutId());
                    if (currFrag != null) {
                        if (currFrag instanceof UserProfileFragment) {
                            updateOnBackStack();

                        }
                    }
                }
            }
        };

        return result;
    }*/

    public void updateOnBackStack() {
        if (getMainActivity() != null) {
            getMainActivity().showBottomBar(AppConstants.profile);

            if (userId != null) {
                serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), UpdateProfile, true, true);
            } else {
                serviceHelper.enqueueCall(headerWebService.getProfile(prefHelper.getUser().getId() + ""), UpdateProfile, true, true);
            }

        }
    }


    private void setData() {

        if (InternetHelper.CheckInternetConectivityandShowToast(getDockActivity())) {
            getDockActivity().onLoadingStarted();
        }

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                getDockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (userId != null) {
                            if (userId.equals(prefHelper.getUser().getId() + "")) {
                                txtMutualFanns.setVisibility(View.GONE);
                            } else {
                                txtMutualFanns.setVisibility(View.VISIBLE);
                            }
                        } else {
                            txtMutualFanns.setVisibility(View.GONE);
                        }
                    }
                });

                if (userId != null) {
                   /* if (userId.equals(prefHelper.getUser().getId() + "")) {
                        txtMutualFanns.setVisibility(View.GONE);
                    } else {
                        txtMutualFanns.setVisibility(View.VISIBLE);
                    }*/

                    serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), GetProfile, true, true);
                } else {
                    //  txtMutualFanns.setVisibility(View.GONE);
                    serviceHelper.enqueueCall(headerWebService.getProfile(prefHelper.getUser().getId() + ""), GetProfile, true, true);
                }
            }
        });
        t.start();


    }

    private void selectFirstTab() {
        txtPosts.setBackgroundColor(getResources().getColor(R.color.app_blue));
        txtPosts.setTextColor(getResources().getColor(R.color.white));
    }

    private void setPostData(ArrayList<Post> posts) {

        if (posts.size() > 0) {
            lvPosts.setVisibility(View.VISIBLE);
            txtNoData.setVisibility(View.GONE);
        } else {
            lvPosts.setVisibility(View.GONE);
            txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
            txtNoData.setVisibility(View.VISIBLE);
        }


        postAdapter.clearList();
        lvPosts.setAdapter(postAdapter);
        postAdapter.addAll(posts);
    }

    private void setFannsData(ArrayList<GetMyFannsEnt> fans) {

        fannsAdapter = new ArrayListAdapter<GetMyFannsEnt>(getDockActivity(), new ProfileFannsBinder(getDockActivity(), prefHelper, this, userId, false));

        fannsAdapter.clearList();
        lvFanns.setAdapter(fannsAdapter);
        fannsAdapter.addAll(fans);
    }

    private void setMutualFannsData(ArrayList<GetMyFannsEnt> mutualFanns) {
        fannsAdapter = new ArrayListAdapter<GetMyFannsEnt>(getDockActivity(), new ProfileFannsBinder(getDockActivity(), prefHelper, this, userId, true));

        fannsAdapter.clearList();
        lvFanns.setAdapter(fannsAdapter);
        fannsAdapter.addAll(mutualFanns);
    }


    private void setPicturesData(ArrayList<Picture> pictures) {

        pictureAdapter.clearList();
        gvPictures.setAdapter(pictureAdapter);
        pictureAdapter.addAll(pictures);
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


    @OnClick({R.id.btn_edit_profile, R.id.txt_posts, R.id.txt_pictures, R.id.txt_fanns, R.id.btn_fanns, R.id.btn_send_message, R.id.rl_fann, R.id.btn_request, R.id.txt_mutual_fanns})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_profile:
                if (entity != null) {
                    //    getDockActivity().popFragment();
                    getDockActivity().replaceDockableFragment(UserEditProfile.newInstance(entity), "UserEditProfile");
                }
                break;
            case R.id.txt_posts:

                isMutual = false;

                lvPosts.setVisibility(View.VISIBLE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.GONE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtPosts.setTextColor(getResources().getColor(R.color.white));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                txtMutualFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtMutualFanns.setTextColor(getResources().getColor(R.color.gray_dark));


                if (entity.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                    lvPosts.setVisibility(View.GONE);
                    txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                    txtNoData.setVisibility(View.VISIBLE);

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("0")) {
                    if (entity.getPosts().size() > 0) {
                        lvPosts.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvPosts.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("1")) {

                    if (prefHelper.getUser() != null && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                        lvPosts.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (entity.getPosts().size() > 0) {
                            lvPosts.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);
                        } else {
                            lvPosts.setVisibility(View.GONE);
                            txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                            txtNoData.setVisibility(View.VISIBLE);
                        }

                    }

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("2")) {
                    if (entity.getFanStatus().getStatus() == null && userId != null && !(prefHelper.getUser().getId()).equals(userId)) {
                        lvPosts.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                        txtNoData.setVisibility(View.VISIBLE);

                    } else {
                        if (entity.getPosts().size() > 0) {
                            lvPosts.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);
                        } else {
                            lvPosts.setVisibility(View.GONE);
                            txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                            txtNoData.setVisibility(View.VISIBLE);
                        }

                    }
                } else {
                    if (entity.getPosts().size() > 0) {
                        lvPosts.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvPosts.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }

                }


                break;
            case R.id.txt_pictures:

                isMutual = false;

                lvPosts.setVisibility(View.GONE);
                gvPictures.setVisibility(View.VISIBLE);
                lvFanns.setVisibility(View.GONE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPosts.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtPictures.setTextColor(getResources().getColor(R.color.white));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                txtMutualFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtMutualFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                if (entity.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                    gvPictures.setVisibility(View.GONE);
                    txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                    txtNoData.setVisibility(View.VISIBLE);

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("0")) {
                    if (entity.getPictures().size() > 0) {
                        gvPictures.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        gvPictures.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("1")) {

                    if (prefHelper.getUser() != null && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                        gvPictures.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    } else {
                        if (entity.getPictures().size() > 0) {
                            gvPictures.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);
                        } else {
                            gvPictures.setVisibility(View.GONE);
                            txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                            txtNoData.setVisibility(View.VISIBLE);
                        }

                    }

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("2")) {
                    if (entity.getFanStatus().getStatus() == null && userId != null && !(prefHelper.getUser().getId()).equals(userId)) {
                        gvPictures.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                        txtNoData.setVisibility(View.VISIBLE);

                    } else {
                        if (entity.getPictures().size() > 0) {
                            gvPictures.setVisibility(View.VISIBLE);
                            txtNoData.setVisibility(View.GONE);
                        } else {
                            gvPictures.setVisibility(View.GONE);
                            txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                            txtNoData.setVisibility(View.VISIBLE);
                        }

                    }
                } else {
                    if (entity.getPictures().size() > 0) {
                        gvPictures.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        gvPictures.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }

                }


                break;
            case R.id.txt_fanns:

                isMutual = false;

                lvPosts.setVisibility(View.GONE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.VISIBLE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPosts.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtFanns.setTextColor(getResources().getColor(R.color.white));

                txtMutualFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtMutualFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                if (entity.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                    lvFanns.setVisibility(View.GONE);
                    txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                    txtNoData.setVisibility(View.VISIBLE);

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("0")) {
                    if (entity.getFans().size() > 0) {
                        lvFanns.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvFanns.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.fann_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }

                } else {
                    if (entity.getFans().size() > 0) {
                        lvFanns.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvFanns.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.fann_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }

                }
                setFannsData(entity.getFans());


                break;

            case R.id.txt_mutual_fanns:

                isMutual = true;

                lvPosts.setVisibility(View.GONE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.VISIBLE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPosts.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                txtMutualFanns.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtMutualFanns.setTextColor(getResources().getColor(R.color.white));


                if (entity.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                    lvFanns.setVisibility(View.GONE);
                    txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                    txtNoData.setVisibility(View.VISIBLE);

                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("0")) {
                    if (entity.getMutualFanns().size() > 0) {
                        lvFanns.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvFanns.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.mutual_fann_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }


                } else {
                    if (entity.getMutualFanns().size() > 0) {
                        lvFanns.setVisibility(View.VISIBLE);
                        txtNoData.setVisibility(View.GONE);
                    } else {
                        lvFanns.setVisibility(View.GONE);
                        txtNoData.setText(getDockActivity().getResources().getString(R.string.mutual_fann_error));
                        txtNoData.setVisibility(View.VISIBLE);
                    }


                }

                setMutualFannsData(entity.getMutualFanns());

                break;


            case R.id.btn_send_message:
                getDockActivity().addDockableFragment(InboxChatFragment.newInstance(entity.getId() + "", entity.getUserName(), true), "InboxChatFragment");
                break;


            case R.id.btn_request:
                if (btnRequest.getText().toString().equals(getDockActivity().getResources().getString(R.string.add_fann))) {
                    btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                    btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                    btnRequest.setText(getString(R.string.request_sent));
                    serviceHelper.enqueueCall(headerWebService.addFann(entity.getId() + "", prefHelper.getUser().getRoleId() + ""), AddFann);
                } else if (btnRequest.getText().toString().equals(getDockActivity().getResources().getString(R.string.request_sent))) {
                    dialogHelper = new DialogHelper(getDockActivity());
                    dialogHelper.initCancelDialoge(R.layout.cancel_request_dialoge, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_CANCELLED, prefHelper.getUser().getRoleId() + ""), cancelled);
                            dialogHelper.hideDialog();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogHelper.hideDialog();
                        }
                    });
                    dialogHelper.showDialog();

                } else if (btnRequest.getText().toString().equals(getDockActivity().getResources().getString(R.string.fann))) {
                    dialogHelper = new DialogHelper(getDockActivity());
                    dialogHelper.initUnfriendDialoge(R.layout.unfriend_dialoge, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_UNFRIEND, prefHelper.getUser().getRoleId() + ""), Unfriend);
                            dialogHelper.hideDialog();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            dialogHelper.hideDialog();
                        }
                    });
                    dialogHelper.showDialog();

                } else if (btnRequest.getText().toString().equals(getDockActivity().getResources().getString(R.string.confirm))) {
                    serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", accepted, prefHelper.getUser().getRoleId() + ""), confirmRequest);

                }
                break;

        }
    }


    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case GetProfile:
                entity = (GetProfileEnt) result;

                // serviceHelper.enqueueCall(headerWebService.getNotificaitonCount(), NotifcationCount);
                if (entity != null && entity.getUserName() != null) {


                    getDockActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getDockActivity().onLoadingFinished();
                            mainFrameLayout.setVisibility(View.VISIBLE);
                            userDeleted.setVisibility(View.GONE);

                            if (getTitleBar() != null) {
                                getTitleBar().hideNotificationBell();
                            }

                            if (entity != null && entity.getUserName() != null) {
                                if (entity.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                                    llUserInformation.setVisibility(View.GONE);
                                } else {
                                    llUserInformation.setVisibility(View.VISIBLE);
                                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) txtNoData.getLayoutParams();
                                    params.height = getDockActivity().getResources().getDimensionPixelOffset(R.dimen.x150);
                                    txtNoData.setLayoutParams(params);
                                }

                                if (entity.getImageUrl() != null)
                                    imageLoader.displayImage(entity.getImageUrl() + "", ivProfileImage);
                                if (entity.getUserName() != null && !entity.getUserName().equals("") && !entity.getUserName().equals("null"))
                                    txtProfileName.setText(entity.getUserName() + "");
                                if (entity.getWorkAt() != null && !entity.getWorkAt().equals("") && !entity.getWorkAt().equals("null")) {
                                    String sourceString = "<b>" + getDockActivity().getString(R.string.works_at) + "</b> " + entity.getWorkAt() + "";
                                    txtWorkAt.setText(Html.fromHtml(sourceString));
                                }
                                if (entity.getAbout() != null && !entity.getAbout().equals("") && !entity.getAbout().equals("null"))
                                    txtAbout.setText(entity.getAbout() + "");
                                if (entity.getDob() != null && !entity.getDob().equals("") && !entity.getDob().equals("null")) {
                                    txtBirthday.setText(entity.getDob() + "");
                                }
                                if (entity.getLocation() != null && !entity.getLocation().equals("") && !entity.getLocation().equals("null")) {
                                    txtLocation.setText(entity.getLocation() != null ? entity.getLocation() : "");
                                }
                                if (entity.getProfession() != null && !entity.getProfession().equals("") && !entity.getProfession().equals("null")) {
                                    txtProfession.setText(entity.getProfession() != null ? entity.getProfession() : "");
                                }
                                if (entity.getHobbies() != null && !entity.getHobbies().equals("") && !entity.getHobbies().equals("null")) {
                                    txtHobbies.setText(entity.getHobbies() != null ? entity.getHobbies() : "");
                                }

                                if (entity.getEducation() != null && !entity.getEducation().equals("") && !entity.getEducation().equals("null")) {
                                    txtEducation.setText(entity.getEducation() != null ? entity.getEducation() : "");
                                }

                                postCount = String.valueOf(entity.getPosts().size());
                                picturesCount = String.valueOf(entity.getPictures().size());
                                fannCount = String.valueOf(entity.getFans().size());
                                mutualfannCount = String.valueOf(entity.getMutualFanns().size());

                                if (postCount.equals("1")) {
                                    txtPosts.setText(postCount + " Post");
                                } else {
                                    txtPosts.setText(postCount + " Posts");
                                }
                                if (picturesCount.equals("1")) {
                                    txtPictures.setText(picturesCount + " Picture");
                                } else {
                                    txtPictures.setText(picturesCount + " Pictures");
                                }
                                if (fannCount.equals("1")) {
                                    txtFanns.setText(fannCount + " Fann");
                                } else {
                                    txtFanns.setText(fannCount + " Fanns");
                                }
                                if (mutualfannCount.equals("1")) {
                                    txtMutualFanns.setText(mutualfannCount + " " + getDockActivity().getResources().getString(R.string.mutual_fann));
                                } else {
                                    txtMutualFanns.setText(mutualfannCount + " " + getDockActivity().getResources().getString(R.string.mutual_fanns));
                                }


                                if (entity.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {

                                    entity.setPosts(new ArrayList<Post>());
                                    entity.setPictures(new ArrayList<Picture>());
                                    entity.setFans(new ArrayList<GetMyFannsEnt>());
                                    entity.setMutualFanns(new ArrayList<GetMyFannsEnt>());

                                    lvPosts.setVisibility(View.GONE);
                                    txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                                    txtNoData.setVisibility(View.VISIBLE);

                                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("0")) {
                                    setPostData(entity.getPosts());
                                    setPicturesData(entity.getPictures());
                                    //  setFannsData(entity.getFans());

                                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("1")) {

                                    if (prefHelper.getUser() != null && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {

                                        entity.setPosts(new ArrayList<Post>());
                                        entity.setPictures(new ArrayList<Picture>());

                                        lvPosts.setVisibility(View.GONE);
                                        txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                                        txtNoData.setVisibility(View.VISIBLE);

                                    } else {
                                        setPicturesData(entity.getPictures());
                                        setPostData(entity.getPosts());


                                    }

                                    //setFannsData(entity.getFans());

                                } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("2")) {
                                    if (entity.getFanStatus().getStatus() == null && userId != null && !(prefHelper.getUser().getId()).equals(userId)) {


                                        entity.setPosts(new ArrayList<Post>());
                                        entity.setPictures(new ArrayList<Picture>());

                                        lvPosts.setVisibility(View.GONE);
                                        txtNoData.setText(getDockActivity().getResources().getString(R.string.privacy_error));
                                        txtNoData.setVisibility(View.VISIBLE);

                                        //   setFannsData(entity.getFans());

                                    } else if (entity.getFanStatus().getStatus() != null && entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                                        setPostData(entity.getPosts());
                                        setPicturesData(entity.getPictures());
                                        setFannsData(entity.getFans());
                                    } else {
                                        setPostData(entity.getPosts());
                                        setPicturesData(entity.getPictures());
                                        // setFannsData(entity.getFans());
                                    }
                                } else {
                                    setPostData(entity.getPosts());
                                    setPicturesData(entity.getPictures());
                                    // setFannsData(entity.getFans());
                                }

                                if (entity.getFanStatus() != null && entity.getFanStatus().getSenderId() != null && entity.getFanStatus().getStatus() != null) {
                                    if (entity.getFanStatus().getSenderId().equals(prefHelper.getUser().getId())) {
                                        if (entity.getFanStatus().getStatus().equals(REQUEST_PENDING)) {
                                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                                            btnRequest.setText(R.string.request_sent);
                                        } else if (entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                                            btnRequest.setText(R.string.fann);
                                        } else {
                                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                                            btnRequest.setText(R.string.add_fann);
                                        }
                                    } else if (entity.getFanStatus().getReceiverId().equals(prefHelper.getUser().getId())) {
                                        if (entity.getFanStatus().getStatus().equals(REQUEST_PENDING)) {
                                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                                            btnRequest.setText(R.string.confirm);
                                        } else if (entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                                            btnRequest.setText(R.string.fann);
                                        } else {
                                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                                            btnRequest.setText(R.string.add_fann);
                                        }
                                    } else {
                                        btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                                        btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                                        btnRequest.setText(R.string.add_fann);
                                    }
                                } else {
                                    btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                                    btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                                    btnRequest.setText(R.string.add_fann);
                                }
                            }
                        }
                    });
                } else {
                    getDockActivity().onLoadingFinished();
                    mainFrameLayout.setVisibility(View.GONE);
                    userDeleted.setVisibility(View.VISIBLE);

                }

                break;

            case UpdateProfile:

                GetProfileEnt profile = (GetProfileEnt) result;

                if (entity != null) {
                    entity.setFans(profile.getFans());
                    entity.setMutualFanns(profile.getMutualFanns());
                    //    entity.setPosts(profile.getPosts());
                    //  entity.setPictures(profile.getPictures());

                    if (profile != null && profile.getProfileStaus() != null && profile.getProfileStaus().equals("0")) {
                        if (fannsAdapter != null) {
                            fannsAdapter.clearList();
                            if (isMutual) {
                                fannsAdapter.addAll(profile.getMutualFanns());
                                if (profile.getMutualFanns().size() > 0) {
                                    lvFanns.setVisibility(View.VISIBLE);
                                    txtNoData.setVisibility(View.GONE);
                                } else {
                                    lvFanns.setVisibility(View.GONE);
                                    txtNoData.setText(getDockActivity().getResources().getString(R.string.mutual_fann_error));
                                    txtNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                fannsAdapter.addAll(profile.getFans());
                                if (profile.getFans().size() > 0) {
                                    lvFanns.setVisibility(View.VISIBLE);
                                    txtNoData.setVisibility(View.GONE);
                                } else {
                                    lvFanns.setVisibility(View.GONE);
                                    txtNoData.setText(getDockActivity().getResources().getString(R.string.fann_error));
                                    txtNoData.setVisibility(View.VISIBLE);
                                }
                            }
                            fannsAdapter.notifyDataSetChanged();
                        }
                    }
                }

                if (profile != null) {

                    fannCount = String.valueOf(profile.getFans().size());
                    mutualfannCount = String.valueOf(profile.getMutualFanns().size());

                    if (fannCount.equals("1")) {
                        txtFanns.setText(fannCount + " Fann");
                    } else {
                        txtFanns.setText(fannCount + " Fanns");
                    }

                    if (mutualfannCount.equals("1")) {
                        txtMutualFanns.setText(mutualfannCount + " " + getDockActivity().getResources().getString(R.string.mutual_fann));
                    } else {
                        txtMutualFanns.setText(mutualfannCount + " " + getDockActivity().getResources().getString(R.string.mutual_fanns));
                    }
                }

                break;

            case GetProfilePushNotification:

                GetProfileEnt profileEnt = (GetProfileEnt) result;

                if (entity != null) {
                    entity.setFanStatus(profileEnt.getFanStatus());
                }

                if (profileEnt!=null && profileEnt.getProfileStaus()!=null && profileEnt.getProfileStaus().equals("0")) {
                    if (fannsAdapter != null) {
                        fannsAdapter.clearList();
                        if (isMutual) {
                            fannsAdapter.addAll(profileEnt.getMutualFanns());
                            if (profileEnt.getMutualFanns().size() > 0) {
                                lvFanns.setVisibility(View.VISIBLE);
                                txtNoData.setVisibility(View.GONE);
                            } else {
                                lvFanns.setVisibility(View.GONE);
                                txtNoData.setText(getDockActivity().getResources().getString(R.string.mutual_fann_error));
                                txtNoData.setVisibility(View.VISIBLE);
                            }
                        } else {
                            fannsAdapter.addAll(profileEnt.getFans());
                            if (profileEnt.getMutualFanns().size() > 0) {
                                lvFanns.setVisibility(View.VISIBLE);
                                txtNoData.setVisibility(View.GONE);
                            } else {
                                lvFanns.setVisibility(View.GONE);
                                txtNoData.setText(getDockActivity().getResources().getString(R.string.fann_error));
                                txtNoData.setVisibility(View.VISIBLE);
                            }
                        }
                        fannsAdapter.notifyDataSetChanged();
                    }


                postCount = String.valueOf(profileEnt.getPosts().size());
                picturesCount = String.valueOf(profileEnt.getPictures().size());
                fannCount = String.valueOf(profileEnt.getFans().size());
                mutualfannCount = String.valueOf(profileEnt.getMutualFanns().size());

                if (postCount.equals("1")) {
                    txtPosts.setText(postCount + " Post");
                } else {
                    txtPosts.setText(postCount + " Posts");
                }
                if (picturesCount.equals("1")) {
                    txtPictures.setText(picturesCount + " Picture");
                } else {
                    txtPictures.setText(picturesCount + " Pictures");
                }
                if (fannCount.equals("1")) {
                    txtFanns.setText(fannCount + " Fann");
                } else {
                    txtFanns.setText(fannCount + " Fanns");
                }
                if (mutualfannCount.equals("1")) {
                    txtMutualFanns.setText(mutualfannCount + " " + getDockActivity().getResources().getString(R.string.mutual_fann));
                } else {
                    txtMutualFanns.setText(mutualfannCount + " " + getDockActivity().getResources().getString(R.string.mutual_fanns));
                }

                }
                /*
                if (profileEnt.getProfileStaus().equals("0") && profileEnt.getPostStatus().equals("0")) {
                    setFannsData(profileEnt.getFans());

                } else if (profileEnt.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {

                    txtFanns.setText(String.valueOf(profileEnt.getFans().size()) + " Fanns");
                    txtMutualFanns.setText(String.valueOf(profileEnt.getMutualFanns().size()) + " " + getDockActivity().getResources().getString(R.string.mutual_fanns));

                    profileEnt.setFans(new ArrayList<GetMyFannsEnt>());
                    profileEnt.setMutualFanns(new ArrayList<GetMyFannsEnt>());


                }

                lvPosts.setVisibility(View.GONE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.VISIBLE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPosts.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtMutualFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtMutualFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtFanns.setTextColor(getResources().getColor(R.color.white));


                if (profileEnt.getFans().size() > 0) {
                    lvFanns.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    lvFanns.setVisibility(View.GONE);
                    txtNoData.setText(getDockActivity().getResources().getString(R.string.fann_error));
                    txtNoData.setVisibility(View.VISIBLE);
                }*/


                break;

            case postLike:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case NotifcationCount:
                prefHelper.setNotificationCount(((NotificationCount) result).getNotification_count());
              /*  if(getTitleBar()!=null){
                    getTitleBar().showNotification(prefHelper.getNotificationCount());
                    ShortcutBadger.applyCount(getDockActivity(), prefHelper.getNotificationCount());
                }*/
                break;
            case favoritePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case AddFann:
                FanStatus ent = (FanStatus) result;
                entity.setFanStatus(ent);
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case cancelled:
                FanStatus entCancelled = (FanStatus) result;
                entity.setFanStatus(entCancelled);
                btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                btnRequest.setText(R.string.add_fann);
                UIHelper.showShortToastInCenter(getDockActivity(), message);


                break;

            case Unfriend:
                FanStatus entUnfriend = (FanStatus) result;
                entity.setFanStatus(entUnfriend);
                btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                btnRequest.setText(R.string.add_fann);
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case confirmRequest:
                FanStatus entconfirm = (FanStatus) result;
                entity.setFanStatus(entconfirm);
                btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                btnRequest.setText(R.string.fann);
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case AddFannProfile:

                GetMyFannsEnt ent1 = entity.getFans().get(friendPosition);
                /*  FanStatus fanStatus =  entity.getFans().get(friendPosition).getFanStatus();*/
                FanStatus fanStatus = (FanStatus) result;
                fanStatus.setStatus(REQUEST_PENDING);
                ent1.setFanStatus(fanStatus);
                entity.getFans().remove(friendPosition);
                entity.getFans().add(friendPosition, ent1);
//                entity.getFans().get(friendPosition).setStatus(getResString(R.string.request_sent));
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case cancelledProfile:
                GetMyFannsEnt ent2 = entity.getFans().get(friendPosition);
                //   FanStatus fanStatus2 =  entity.getFans().get(friendPosition).getFanStatus(); //
                FanStatus fanStatus2 = (FanStatus) result;
                fanStatus2.setStatus(getString(R.string.add_fann));
                ent2.setFanStatus(fanStatus2);
                entity.getFans().remove(friendPosition);
                entity.getFans().add(friendPosition, ent2);
                fannsAdapter.notifyDataSetChanged();
                // entity.getFans().get(friendPosition).setStatus(getResString(R.string.add_fann));
                UIHelper.showShortToastInCenter(getDockActivity(), message);

                break;

            case UnfriendProfile:
                GetMyFannsEnt ent3 = entity.getFans().get(friendPosition);
                /*    FanStatus fanStatus3 =  entity.getFans().get(friendPosition).getFanStatus();*/
                FanStatus fanStatus3 = (FanStatus) result;
                fanStatus3.setStatus(getString(R.string.add_fann));
                ent3.setFanStatus(fanStatus3);
                entity.getFans().remove(friendPosition);
                entity.getFans().add(friendPosition, ent3);
                UIHelper.showShortToastInCenter(getDockActivity(), message);

                break;

            case confirmRequestProfile:
                GetMyFannsEnt ent4 = entity.getFans().get(friendPosition);
                //  FanStatus fanStatus4 =  entity.getFans().get(friendPosition).getFanStatus();
                FanStatus fanStatus4 = (FanStatus) result;
                fanStatus4.setStatus(REQUEST_ACCEPTED);
                ent4.setFanStatus(fanStatus4);
                entity.getFans().remove(friendPosition);
                entity.getFans().add(friendPosition, ent4);
                UIHelper.showShortToastInCenter(getDockActivity(), message);

                break;

            case deletePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);

                if (userId != null) {
                    serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), PostDeleteProfile, true, true);
                } else {
                    serviceHelper.enqueueCall(headerWebService.getProfile(prefHelper.getUser().getId() + ""), PostDeleteProfile, true, true);
                }

                break;

            case reportPost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case reportUser:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case PostDeleteProfile:

                GetProfileEnt postDelete = (GetProfileEnt) result;


                if (postDelete.getProfileStaus().equals("0") && postDelete.getPostStatus().equals("0")) {

                    postCount = String.valueOf(postDelete.getPosts().size());
                    if (postCount.equals("1")) {
                        txtPosts.setText(postCount + " Post");
                    } else {
                        txtPosts.setText(postCount + " Posts");
                    }

                    setPostData(postDelete.getPosts());

                } else if (postDelete.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {

                    postDelete.setPosts(new ArrayList<Post>());
                    if (String.valueOf(postDelete.getPosts().size()).equals("1")) {
                        txtPosts.setText(String.valueOf(postDelete.getPosts().size()) + " Post");
                    } else {
                        txtPosts.setText(String.valueOf(postDelete.getPosts().size()) + " Posts");
                    }
                }

                lvPosts.setVisibility(View.VISIBLE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.GONE);

                txtFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                txtMutualFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtMutualFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPosts.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtPosts.setTextColor(getResources().getColor(R.color.white));


                if (postDelete.getPosts().size() > 0) {
                    lvPosts.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    lvPosts.setVisibility(View.GONE);
                    txtNoData.setText(getDockActivity().getResources().getString(R.string.post_error));
                    txtNoData.setVisibility(View.VISIBLE);
                }


                break;


        }
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
        getDockActivity().onLoadingStarted();
        if (entity.getImageUrl() != null && !entity.getImageUrl().equals("") && entity.getDescription() != null && !entity.getDescription().equals("")) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), entity.getDescription());
        } else if (entity.getImageUrl() != null && entity.getDescription() == null) {
            ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entity.getImageUrl(), "");
        } else if (entity.getImageUrl() == null && entity.getDescription() != null) {
            ShareIntentHelper.shareTextIntent(getDockActivity(), entity.getDescription());
        } else {
            UIHelper.showShortToastInCenter(getDockActivity(), "Description is not avaliable");
        }
    }

    @Override
    public void comment(Post entity, int position) {
        getDockActivity().addDockableFragment(CommentFragment.newInstance(entity.getId() + ""), "CommentFragment");
    }


    @Override
    public void addFriend(GetMyFannsEnt entity, int position, String Key, String id) {

        String userId = "";
        String roleId = "";
        if (entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
            if (entity.getSenderDetail().getId().equals(id)) {
                userId = entity.getReceiverDetail().getId() + "";
                roleId = entity.getReceiverDetail().getRoleId() + "";
            } else {
                userId = entity.getSenderDetail().getId() + "";
                roleId = entity.getSenderDetail().getRoleId() + "";
            }
        }
        friendPosition = position;
        if (Key.equals(AddFann) && !userId.equals("")) {
            serviceHelper.enqueueCall(headerWebService.addFann(userId, prefHelper.getUser().getRoleId() + ""), AddFannProfile);
        } else if (Key.equals(cancelled)) {
            if (entity.getSenderId().equals(prefHelper.getUser().getId()) || entity.getReceiverId().equals(prefHelper.getUser().getId())) {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", REQUEST_CANCELLED, prefHelper.getUser().getRoleId() + "", getUserId(entity.getSenderId() + "", entity.getReceiverId() + "", prefHelper.getUser().getId() + "")), cancelledProfile);
            } else {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_CANCELLED, prefHelper.getUser().getRoleId() + "", getUserId(entity.getFanStatus().getSenderId() + "", entity.getFanStatus().getReceiverId() + "", prefHelper.getUser().getId() + "")), cancelledProfile);
            }

        } else if (Key.equals(Unfriend)) {
            if (entity.getSenderId().equals(prefHelper.getUser().getId()) || entity.getReceiverId().equals(prefHelper.getUser().getId())) {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", REQUEST_UNFRIEND, prefHelper.getUser().getRoleId() + "", getUserId(entity.getSenderId() + "", entity.getReceiverId() + "", prefHelper.getUser().getId() + "")), UnfriendProfile);
            } else {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_UNFRIEND, prefHelper.getUser().getRoleId() + "", getUserId(entity.getFanStatus().getSenderId() + "", entity.getFanStatus().getReceiverId() + "", prefHelper.getUser().getId() + "")), UnfriendProfile);
            }

        } else if (Key.equals(confirmRequest)) {
            if (entity.getSenderId().equals(prefHelper.getUser().getId()) || entity.getReceiverId().equals(prefHelper.getUser().getId())) {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", accepted, prefHelper.getUser().getRoleId() + "", getUserId(entity.getSenderId() + "", entity.getReceiverId() + "", prefHelper.getUser().getId() + "")), confirmRequestProfile);
            } else {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", accepted, prefHelper.getUser().getRoleId() + "", getUserId(entity.getFanStatus().getSenderId() + "", entity.getFanStatus().getReceiverId() + "", prefHelper.getUser().getId() + "")), confirmRequestProfile);
            }

        } else if (Key.equals(showProfile)) {
            String userIdKey = "";
            String roleIdKey = "";

            if (id != null && entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
                if (entity.getSenderDetail().getId().equals(id)) {

                    userIdKey = entity.getReceiverDetail().getId() + "";
                    roleIdKey = entity.getReceiverDetail().getRoleId() + "";

                } else {
                    userIdKey = entity.getSenderDetail().getId() + "";
                    roleIdKey = entity.getSenderDetail().getRoleId() + "";
                }
            }

            if (roleIdKey.equals(UserRoleId)) {
                getDockActivity().addDockableFragment(UserProfileFragment.newInstance(userIdKey), "UserProfileFragment");
            } else {
                getDockActivity().addDockableFragment(SpProfileFragment.newInstance(userIdKey), "SpProfileFragment");
            }
        }

    }

    @Override
    public void profileClick(GetMyFannsEnt entity, int position, String id) {


        String userId = "";
        String roleId = "";

        if (id != null && entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
            if (entity.getSenderDetail().getId().equals(id)) {

                userId = entity.getReceiverDetail().getId() + "";
                roleId = entity.getReceiverDetail().getRoleId() + "";

            } else {
                userId = entity.getSenderDetail().getId() + "";
                roleId = entity.getSenderDetail().getRoleId() + "";
            }
        } else if(entity != null && entity.getSenderDetail() != null){
            if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {

                userId = entity.getReceiverDetail().getId() + "";
                roleId = entity.getReceiverDetail().getRoleId() + "";

            } else {
                userId = entity.getSenderDetail().getId() + "";
                roleId = entity.getSenderDetail().getRoleId() + "";
            }
        }
        if (roleId.equals(UserRoleId)) {
            getDockActivity().addDockableFragment(UserProfileFragment.newInstance(userId), "UserProfileFragment");
        } else {
            getDockActivity().addDockableFragment(SpProfileFragment.newInstance(userId), "SpProfileFragment");
        }

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getDockActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDockActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.REGISTRATION_COMPLETE));

        LocalBroadcastManager.getInstance(getDockActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter(AppConstants.PUSH_NOTIFICATION));


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
                   /* if (getTitleBar() != null) {
                        getTitleBar().showNotification(prefHelper.getNotificationCount());
                        ShortcutBadger.applyCount(getDockActivity(), prefHelper.getNotificationCount());
                    }*/
                    if (bundle != null) {
                        String Type = bundle.getString("pushtype");
                        String SenderId = bundle.getString("sender_id");
                        String ReceiverId = bundle.getString("receiver_id");

                        if (Type != null && (Type.equals(cancel_request) || Type.equals(declined_request) || Type.equals(unfriend))) {
                            if (dialogHelper != null) {
                                dialogHelper.hideDialog();
                            }
                            if (userId != null) {
                                if (SenderId.equals(userId) || ReceiverId.equals(userId)) {
                                    btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                                    btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                                    btnRequest.setText(R.string.add_fann);
                                }

                                serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), GetProfilePushNotification, true, true);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.getProfile(prefHelper.getUser().getId() + ""), GetProfilePushNotification, true, true);
                            }

                        } else if (Type != null && Type.equals(accept_request)) {
                            if (dialogHelper != null) {
                                dialogHelper.hideDialog();
                            }
                            if (userId != null) {
                                if (SenderId.equals(userId) || ReceiverId.equals(userId)) {
                                    btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                                    btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                                    btnRequest.setText(R.string.fann);
                                }


                                serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), GetProfilePushNotification, true, true);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.getProfile(prefHelper.getUser().getId() + ""), GetProfilePushNotification, true, true);
                            }
                        } else if (Type != null && Type.equals(friend_request)) {
                            if (userId != null) {
                                if (SenderId.equals(userId) || ReceiverId.equals(userId)) {
                                    btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                                    btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                                    if (SenderId.equals(prefHelper.getUser().getId())) {
                                        btnRequest.setText(R.string.request_sent);
                                    } else if (ReceiverId.equals(prefHelper.getUser().getId())) {
                                        btnRequest.setText(R.string.confirm);
                                    }
                                }

                                serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), GetProfilePushNotification, true, true);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.getProfile(prefHelper.getUser().getId() + ""), GetProfilePushNotification, true, true);
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
    public void ResponseFailure(String tag) {
        super.ResponseFailure(tag);

        getDockActivity().onLoadingFinished();
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


}
