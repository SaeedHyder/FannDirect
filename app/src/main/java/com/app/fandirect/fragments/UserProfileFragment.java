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
import com.app.fandirect.entities.Picture;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.InternetHelper;
import com.app.fandirect.helpers.ShareIntentHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.PostClicksInterface;
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

import static com.app.fandirect.global.AppConstants.REQUEST_ACCEPTED;
import static com.app.fandirect.global.AppConstants.REQUEST_CANCELLED;
import static com.app.fandirect.global.AppConstants.REQUEST_PENDING;
import static com.app.fandirect.global.AppConstants.REQUEST_UNFRIEND;
import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.AppConstants.accept_request;
import static com.app.fandirect.global.AppConstants.accepted;
import static com.app.fandirect.global.AppConstants.cancel_request;
import static com.app.fandirect.global.AppConstants.declined_request;
import static com.app.fandirect.global.AppConstants.unfriend;
import static com.app.fandirect.global.WebServiceConstants.AddFann;
import static com.app.fandirect.global.WebServiceConstants.AddFannProfile;
import static com.app.fandirect.global.WebServiceConstants.GetProfile;
import static com.app.fandirect.global.WebServiceConstants.GetProfilePushNotification;
import static com.app.fandirect.global.WebServiceConstants.Unfriend;
import static com.app.fandirect.global.WebServiceConstants.UnfriendProfile;
import static com.app.fandirect.global.WebServiceConstants.cancelled;
import static com.app.fandirect.global.WebServiceConstants.cancelledProfile;
import static com.app.fandirect.global.WebServiceConstants.confirmRequest;
import static com.app.fandirect.global.WebServiceConstants.confirmRequestProfile;
import static com.app.fandirect.global.WebServiceConstants.favoritePost;
import static com.app.fandirect.global.WebServiceConstants.postLike;
import static com.app.fandirect.global.WebServiceConstants.showProfile;

/**
 * Created by saeedhyder on 3/1/2018.
 */
public class UserProfileFragment extends BaseFragment implements PostClicksInterface, UserItemClickInterface {
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
    private int pos = 0;


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
        postAdapter = new ArrayListAdapter<Post>(getDockActivity(), new PostItemBinder(getDockActivity(), prefHelper, this));

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
    }


    private void setData() {

        if (InternetHelper.CheckInternetConectivityandShowToast(getDockActivity())) {
            getDockActivity().onLoadingStarted();
        }

        final Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (userId != null) {
                    serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), GetProfile, true, true);
                } else {
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
            txtNoData.setVisibility(View.VISIBLE);
        }


        postAdapter.clearList();
        lvPosts.setAdapter(postAdapter);
        postAdapter.addAll(posts);
    }

    private void setFannsData(ArrayList<GetMyFannsEnt> fans) {

        fannsAdapter = new ArrayListAdapter<GetMyFannsEnt>(getDockActivity(), new ProfileFannsBinder(getDockActivity(), prefHelper, this, userId));

        fannsAdapter.clearList();
        lvFanns.setAdapter(fannsAdapter);
        fannsAdapter.addAll(fans);
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
        titleBar.showNotificationBell(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDockActivity().replaceDockableFragment(NotificationFragment.newInstance(), "NotificationFragment");
            }
        });

    }


    @OnClick({R.id.btn_edit_profile, R.id.txt_posts, R.id.txt_pictures, R.id.txt_fanns, R.id.btn_fanns, R.id.btn_send_message, R.id.rl_fann, R.id.btn_request})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_profile:
                if (entity != null) {
                    getDockActivity().popFragment();
                    getDockActivity().replaceDockableFragment(UserEditProfile.newInstance(entity), "UserEditProfile");
                }
                break;
            case R.id.txt_posts:

                lvPosts.setVisibility(View.VISIBLE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.GONE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtPosts.setTextColor(getResources().getColor(R.color.white));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                if (entity.getPosts().size() > 0) {
                    lvPosts.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    lvPosts.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }


                break;
            case R.id.txt_pictures:

                lvPosts.setVisibility(View.GONE);
                gvPictures.setVisibility(View.VISIBLE);
                lvFanns.setVisibility(View.GONE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPosts.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtPictures.setTextColor(getResources().getColor(R.color.white));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtFanns.setTextColor(getResources().getColor(R.color.gray_dark));

                if (entity.getPictures().size() > 0) {
                    gvPictures.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    gvPictures.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.txt_fanns:

                lvPosts.setVisibility(View.GONE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.VISIBLE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPosts.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtFanns.setTextColor(getResources().getColor(R.color.white));

                if (entity.getFans().size() > 0) {
                    lvFanns.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    lvFanns.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }

                break;


            case R.id.btn_send_message:
                getDockActivity().replaceDockableFragment(InboxChatFragment.newInstance(entity.getId() + "", true), "InboxChatFragment");
                break;


            case R.id.btn_request:
                if (btnRequest.getText().toString().equals(getDockActivity().getResources().getString(R.string.add_fann))) {
                    btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                    btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                    btnRequest.setText(getString(R.string.request_sent));
                    serviceHelper.enqueueCall(headerWebService.addFann(entity.getId() + ""), AddFann);
                } else if (btnRequest.getText().toString().equals(getDockActivity().getResources().getString(R.string.request_sent))) {
                    final DialogHelper dialogHelper = new DialogHelper(getDockActivity());
                    dialogHelper.initCancelDialoge(R.layout.cancel_request_dialoge, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_CANCELLED), cancelled);
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
                    final DialogHelper dialogHelper = new DialogHelper(getDockActivity());

                    dialogHelper.initUnfriendDialoge(R.layout.unfriend_dialoge, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_UNFRIEND), Unfriend);
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
                    serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", accepted), confirmRequest);

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
                mainFrameLayout.setVisibility(View.VISIBLE);

                getDockActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getDockActivity().onLoadingFinished();
                        if (entity.getImageUrl() != null)
                            imageLoader.displayImage(entity.getImageUrl() + "", ivProfileImage);
                        txtProfileName.setText(entity.getUserName() + "");
                        if (entity.getWorkAt() != null && !entity.getWorkAt().equals("")) {
                            String sourceString = "<b>" + getDockActivity().getString(R.string.works_at) + "</b> " + entity.getWorkAt() + "";
                            txtWorkAt.setText(Html.fromHtml(sourceString));
                        }
                        if (entity.getAbout() != null)
                            txtAbout.setText(entity.getAbout() + "");
                        if (entity.getDob() != null) {
                            txtBirthday.setText(entity.getDob() + "");
                        }
                        txtLocation.setText(entity.getLocation() + "");
                        txtProfession.setText(entity.getProfession() + "");
                        txtHobbies.setText(entity.getHobbies() + "");

                        postCount = String.valueOf(entity.getPosts().size());
                        picturesCount = String.valueOf(entity.getPictures().size());
                        fannCount = String.valueOf(entity.getFans().size());


                        txtPosts.setText(postCount + " Posts");
                        txtPictures.setText(picturesCount + " Pictures");
                        txtFanns.setText(fannCount + " Fanns");


                        if (entity.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                            entity.setPosts(new ArrayList<Post>());
                            entity.setPictures(new ArrayList<Picture>());
                            entity.setFans(new ArrayList<GetMyFannsEnt>());

                            txtPosts.setText(String.valueOf(entity.getPosts().size()) + " Posts");
                            txtPictures.setText(String.valueOf(entity.getPictures().size()) + " Pictures");
                            txtFanns.setText(String.valueOf(entity.getFans().size()) + " Fanns");

                            if (entity.getPosts().size() > 0) {
                                lvPosts.setVisibility(View.VISIBLE);
                                txtNoData.setVisibility(View.GONE);
                            } else {
                                lvPosts.setVisibility(View.GONE);
                                //  txtNoData.setText("Privacy has been applied");
                                txtNoData.setVisibility(View.VISIBLE);
                            }
                        } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("0")) {
                            setPostData(entity.getPosts());
                            setPicturesData(entity.getPictures());
                            setFannsData(entity.getFans());

                        } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("1")) {

                            if (prefHelper.getUser() != null && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {
                                entity.setPosts(new ArrayList<Post>());
                                entity.setPictures(new ArrayList<Picture>());
                                txtPosts.setText(String.valueOf(entity.getPosts().size()) + " Posts");
                                txtPictures.setText(String.valueOf(entity.getPictures().size()) + " Pictures");
                                if (entity.getPosts().size() > 0) {
                                    lvPosts.setVisibility(View.VISIBLE);
                                    txtNoData.setVisibility(View.GONE);
                                } else {
                                    lvPosts.setVisibility(View.GONE);
                                    //   txtNoData.setText("Privacy has been applied");
                                    txtNoData.setVisibility(View.VISIBLE);
                                }
                            } else {
                                setPicturesData(entity.getPictures());
                                setPostData(entity.getPosts());
                                txtPosts.setText(String.valueOf(entity.getPosts().size()) + " Posts");
                            }

                            setFannsData(entity.getFans());

                        } else if (entity.getProfileStaus().equals("0") && entity.getPostStatus().equals("2")) {
                            if (entity.getFanStatus().getStatus() == null && userId != null && !(prefHelper.getUser().getId()).equals(userId)) {

                                entity.setPosts(new ArrayList<Post>());
                                entity.setPictures(new ArrayList<Picture>());
                                txtPosts.setText(String.valueOf(entity.getPosts().size()) + " Posts");
                                txtPictures.setText(String.valueOf(entity.getPictures().size()) + " Pictures");
                                if (entity.getPosts().size() > 0) {
                                    lvPosts.setVisibility(View.VISIBLE);
                                    txtNoData.setVisibility(View.GONE);
                                } else {
                                    lvPosts.setVisibility(View.GONE);
                                    //   txtNoData.setText("Privacy has been applied");
                                    txtNoData.setVisibility(View.VISIBLE);
                                }
                                //  setPicturesData(entity.getPictures());
                                setFannsData(entity.getFans());
                            } else if (entity.getFanStatus().getStatus() != null && entity.getFanStatus().getStatus().equals(REQUEST_ACCEPTED)) {
                                setPostData(entity.getPosts());
                                setPicturesData(entity.getPictures());
                                setFannsData(entity.getFans());
                            } else {
                                setPostData(entity.getPosts());
                                setPicturesData(entity.getPictures());
                                setFannsData(entity.getFans());
                            }
                        } else {
                            setPostData(entity.getPosts());
                            setPicturesData(entity.getPictures());
                            setFannsData(entity.getFans());
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
                });

                break;

            case GetProfilePushNotification:

                GetProfileEnt profileEnt = (GetProfileEnt) result;


                if (profileEnt.getProfileStaus().equals("0") && profileEnt.getPostStatus().equals("0")) {

                    fannCount = String.valueOf(profileEnt.getFans().size());
                    txtFanns.setText(fannCount + " Fanns");
                    setFannsData(profileEnt.getFans());

                } else if (profileEnt.getProfileStaus().equals("1") && userId != null && !(String.valueOf(prefHelper.getUser().getId()).equals(userId))) {

                    profileEnt.setFans(new ArrayList<GetMyFannsEnt>());
                    txtFanns.setText(String.valueOf(profileEnt.getFans().size()) + " Fanns");
                }

                lvPosts.setVisibility(View.GONE);
                gvPictures.setVisibility(View.GONE);
                lvFanns.setVisibility(View.VISIBLE);

                txtPosts.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPosts.setTextColor(getResources().getColor(R.color.gray_dark));

                txtPictures.setBackgroundColor(getResources().getColor(R.color.transparent));
                txtPictures.setTextColor(getResources().getColor(R.color.gray_dark));

                txtFanns.setBackgroundColor(getResources().getColor(R.color.app_blue));
                txtFanns.setTextColor(getResources().getColor(R.color.white));


                if (profileEnt.getFans().size() > 0) {
                    lvFanns.setVisibility(View.VISIBLE);
                    txtNoData.setVisibility(View.GONE);
                } else {
                    lvFanns.setVisibility(View.GONE);
                    txtNoData.setVisibility(View.VISIBLE);
                }


                break;

            case postLike:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
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
                btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                btnRequest.setText(R.string.add_fann);
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case Unfriend:
                btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                btnRequest.setText(R.string.add_fann);
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case confirmRequest:
                btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                btnRequest.setText(R.string.fann);
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case AddFannProfile:
                FanStatus resultEnt = (FanStatus) result;
                //   entity.getFans().get(pos).
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case cancelledProfile:

                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case UnfriendProfile:

                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case confirmRequestProfile:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
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
        getDockActivity().replaceDockableFragment(CommentFragment.newInstance(entity.getId() + ""), "CommentFragment");
    }


    @Override
    public void addFriend(GetMyFannsEnt entity, int position, String Key, String id) {

        String userId = "";
        if (entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
            if (entity.getSenderDetail().getId().equals(id)) {
                userId = entity.getReceiverDetail().getId() + "";
            } else {
                userId = entity.getSenderDetail().getId() + "";
            }
        }

        if (Key.equals(AddFann) && !userId.equals("")) {
            serviceHelper.enqueueCall(headerWebService.addFann(userId), AddFannProfile);
        } else if (Key.equals(cancelled)) {
            if (entity.getSenderId().equals(prefHelper.getUser().getId()) || entity.getReceiverId().equals(prefHelper.getUser().getId())) {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", REQUEST_CANCELLED), cancelledProfile);
            } else {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_CANCELLED), cancelledProfile);
            }

        } else if (Key.equals(Unfriend)) {
            if (entity.getSenderId().equals(prefHelper.getUser().getId()) || entity.getReceiverId().equals(prefHelper.getUser().getId())) {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", REQUEST_UNFRIEND), UnfriendProfile);
            } else {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", REQUEST_UNFRIEND), UnfriendProfile);
            }

        } else if (Key.equals(confirmRequest)) {
            if (entity.getSenderId().equals(prefHelper.getUser().getId()) || entity.getReceiverId().equals(prefHelper.getUser().getId())) {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getId() + "", accepted), confirmRequestProfile);
            } else {
                serviceHelper.enqueueCall(headerWebService.markFannRequset(entity.getFanStatus().getId() + "", accepted), confirmRequestProfile);
            }

        } else if (Key.equals(showProfile)) {
            String userIdKey = "";
            String roleId = "";

            if (id != null && entity != null && entity.getSenderDetail() != null && entity.getReceiverDetail() != null) {
                if (entity.getSenderDetail().getId().equals(id)) {

                    userIdKey = entity.getReceiverDetail().getId() + "";
                    roleId = entity.getReceiverDetail().getRoleId() + "";

                } else {
                    userIdKey = entity.getSenderDetail().getId() + "";
                    roleId = entity.getSenderDetail().getRoleId() + "";
                }
            }

            if (roleId.equals(UserRoleId)) {
                getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(userIdKey), "UserProfileFragment");
            } else {
                getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(userIdKey), "SpProfileFragment");
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
        } else {
            if (entity.getSenderDetail().getId().equals(prefHelper.getUser().getId())) {

                userId = entity.getReceiverDetail().getId() + "";
                roleId = entity.getReceiverDetail().getRoleId() + "";

            } else {
                userId = entity.getSenderDetail().getId() + "";
                roleId = entity.getSenderDetail().getRoleId() + "";
            }
        }
        if (roleId.equals(UserRoleId)) {
            getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(userId), "UserProfileFragment");
        } else {
            getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(userId), "SpProfileFragment");
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
                    if (bundle != null) {
                        String Type = bundle.getString("pushtype");

                        if (Type != null && (Type.equals(cancel_request) || Type.equals(declined_request) || Type.equals(unfriend))) {
                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangle_box_white));
                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.app_blue));
                            btnRequest.setText(R.string.add_fann);

                            if (userId != null) {
                                serviceHelper.enqueueCall(headerWebService.getProfile(userId + ""), GetProfilePushNotification, true, true);
                            } else {
                                serviceHelper.enqueueCall(headerWebService.getProfile(prefHelper.getUser().getId() + ""), GetProfilePushNotification, true, true);
                            }

                        } else if (Type != null && Type.equals(accept_request)) {
                            btnRequest.setBackground(getDockActivity().getResources().getDrawable(R.drawable.rectangular_box_blue));
                            btnRequest.setTextColor(getDockActivity().getResources().getColor(R.color.white));
                            btnRequest.setText(R.string.fann);

                            if (userId != null) {
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


}
