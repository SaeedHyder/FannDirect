package com.app.fandirect.fragments;


import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.app.fandirect.R;
import com.app.fandirect.entities.Picture;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.abstracts.BaseFragment;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.helpers.ShareIntentHelper;
import com.app.fandirect.helpers.UIHelper;
import com.app.fandirect.interfaces.ReportPostIntetface;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.TitleBar;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;
import static com.app.fandirect.global.WebServiceConstants.deletePost;
import static com.app.fandirect.global.WebServiceConstants.favoritePost;
import static com.app.fandirect.global.WebServiceConstants.getAllPosts;
import static com.app.fandirect.global.WebServiceConstants.postLike;
import static com.app.fandirect.global.WebServiceConstants.reportPost;
import static com.app.fandirect.global.WebServiceConstants.reportUser;

/**
 * Created by saeedhyder on 4/21/2018.
 */
public class ProfilePictureDetail extends BaseFragment {
    @BindView(R.id.iv_image)
    CircleImageView ivImage;
    @BindView(R.id.txt_name)
    AnyTextView txtName;
    @BindView(R.id.txt_address)
    AnyTextView txtAddress;
    @BindView(R.id.iv_post_image)
    ImageView ivPostImage;
    @BindView(R.id.txt_title)
    AnyTextView txtTitle;
    @BindView(R.id.txt_description)
    AnyTextView txtDescription;
    @BindView(R.id.like)
    AnyTextView like;
    @BindView(R.id.comment)
    AnyTextView comment;
    @BindView(R.id.share)
    AnyTextView share;
    @BindView(R.id.favorite)
    AnyTextView favorite;
    Unbinder unbinder;
    @BindView(R.id.txt_date)
    AnyTextView txtDate;
    @BindView(R.id.menu_btn)
    Button menuBtn;
    @BindView(R.id.ll_name)
    LinearLayout llName;


    private ImageLoader imageLoader;
    private static String imageEntKey = "imageEntKey";
    private String imageEnt = "imageEnt";
    private Picture entitiy;
    private long mLastClickTime = 0;

    public static ProfilePictureDetail newInstance(Picture ent) {
        Bundle args = new Bundle();
        args.putString(imageEntKey, new Gson().toJson(ent));
        ProfilePictureDetail fragment = new ProfilePictureDetail();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLoader = ImageLoader.getInstance();
        if (getArguments() != null) {
            imageEnt = getArguments().getString(imageEntKey);
        }
        if (imageEnt != null) {
            entitiy = new Gson().fromJson(imageEnt, Picture.class);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainActivity().hideBottomBar();
        setData();
    }

    private void setData() {

        if (entitiy != null) {
            imageLoader.displayImage(entitiy.getImageUrl(), ivPostImage);
            if (entitiy.getUserDetail().getImageUrl() != null)
                imageLoader.displayImage(entitiy.getUserDetail().getImageUrl(), ivImage);
            txtName.setText(entitiy.getUserDetail().getUserName() + "");

            txtDescription.setText(entitiy.getDescription());
            txtDate.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss", "dd-MM-yy", entitiy.getCreated_at()));

            if (entitiy.getLocation() != null && !entitiy.getLocation().equals("")) {
                txtAddress.setText(entitiy.getLocation());
                txtAddress.setVisibility(View.VISIBLE);
            } else {
                txtAddress.setVisibility(View.GONE);
            }

            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (entitiy.getUserDetail().getRoleId().equals(UserRoleId)) {
                        getDockActivity().replaceDockableFragment(UserProfileFragment.newInstance(entitiy.getUserDetail().getId() + ""), "UserProfileFragment");
                    } else {
                        getDockActivity().replaceDockableFragment(SpProfileFragment.newInstance(entitiy.getUserDetail().getId() + ""), "SpProfileFragment");
                    }
                }
            });

            if (entitiy.getLikeCount() != null && entitiy.getLikeCount() != 0) {
                like.setText(entitiy.getLikeCount() + "");
            }
            if (entitiy.getCommentCount() != null && entitiy.getCommentCount() != 0) {
                comment.setText(entitiy.getCommentCount() + "");
            }
            if (entitiy.getIsLike() != null && entitiy.getIsLike() == 1) {
                like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_like, 0, 0, 0);
            } else {
                like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_like, 0, 0, 0);
            }

            if (entitiy.getIsFavourite() == 1) {
                favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_fav, 0, 0, 0);
            } else {
                favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_fav, 0, 0, 0);
            }

        }

    }

    @Override
    public void setTitleBar(TitleBar titleBar) {
        super.setTitleBar(titleBar);
        titleBar.hideButtons();
        titleBar.showTitleLogo();
        titleBar.showBackButton();
        titleBar.showNotificationBell(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDockActivity().replaceDockableFragment(NotificationFragment.newInstance(), "NotificationFragment");
            }
        });
    }


    @OnClick({R.id.like, R.id.comment, R.id.share, R.id.favorite,R.id.menu_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.like:
                if (entitiy.getIsLike() == 0) {
                    like.setText(String.valueOf(entitiy.getLikeCount() + 1));
                    like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_like, 0, 0, 0);
                    entitiy.setLikeCount(entitiy.getLikeCount() + 1);
                    entitiy.setIsLike(1);
                } else {
                    like.setText(String.valueOf(entitiy.getLikeCount() - 1));
                    like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_like, 0, 0, 0);
                    entitiy.setIsLike(0);
                    entitiy.setLikeCount(entitiy.getLikeCount() - 1);
                }
                serviceHelper.enqueueCall(headerWebService.likePost(entitiy.getId() + ""), postLike);
                break;
            case R.id.comment:
                getDockActivity().replaceDockableFragment(CommentFragment.newInstance(entitiy.getId() + ""), "CommentFragment");
                break;

            case R.id.share:

                if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                getDockActivity().onLoadingStarted();

                if (entitiy.getImageUrl() != null && !entitiy.getImageUrl().equals("") && entitiy.getDescription() != null && !entitiy.getDescription().equals("")) {
                    ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entitiy.getImageUrl(), entitiy.getDescription());
                } else if (entitiy.getImageUrl() != null && entitiy.getDescription() == null) {
                    ShareIntentHelper.shareImageAndTextResultIntent(getDockActivity(), entitiy.getImageUrl(), "");
                } else if (entitiy.getImageUrl() == null && entitiy.getDescription() != null) {
                    ShareIntentHelper.shareTextIntent(getDockActivity(), entitiy.getDescription());
                } else {
                    UIHelper.showShortToastInCenter(getDockActivity(), "Description is not avaliable");
                }
                break;
            case R.id.favorite:
                if (entitiy.getIsFavourite() != null && entitiy.getIsFavourite() == 0) {
                    favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_fav, 0, 0, 0);
                    entitiy.setIsFavourite(1);
                } else {
                    favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_fav, 0, 0, 0);
                    entitiy.setIsFavourite(0);
                }
                serviceHelper.enqueueCall(headerWebService.favoritePost(entitiy.getId() + ""), favoritePost);
                break;

            case R.id.menu_btn:

                LayoutInflater layoutInflater = (LayoutInflater) getDockActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popup_window_dialoge, null);
                final DialogHelper dialogHelper = new DialogHelper(getDockActivity());

                AnyTextView deletePost = (AnyTextView) customView.findViewById(R.id.txt_delete_post);
                AnyTextView reportPost = (AnyTextView) customView.findViewById(R.id.txt_report_post);
                AnyTextView reportUser = (AnyTextView) customView.findViewById(R.id.txt_repost_user);
                View viewReport = (View) customView.findViewById(R.id.view_report);

                final PopupWindow popupWindow = new PopupWindow(customView, (int) getDockActivity().getResources().getDimension(R.dimen.x130), LinearLayout.LayoutParams.WRAP_CONTENT);
                //  popupWindow.showAtLocation(viewHolder.llMainframe, Gravity.CENTER, 0, 0);


                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);

                popupWindow.showAsDropDown(view);

                if (String.valueOf(entitiy.getUserDetail().getId()).equals(prefHelper.getUser().getId())) {
                    reportPost.setVisibility(View.GONE);
                    reportUser.setVisibility(View.GONE);
                    deletePost.setVisibility(View.VISIBLE);
                    viewReport.setVisibility(View.GONE);
                } else {
                    reportPost.setVisibility(View.VISIBLE);
                    reportUser.setVisibility(View.VISIBLE);
                    viewReport.setVisibility(View.VISIBLE);
                    deletePost.setVisibility(View.GONE);
                }

                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        dialogHelper.initDropDown(R.layout.dialoge_delete_post, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                serviceHelper.enqueueCall(headerWebService.deletePost(entitiy.getId()), WebServiceConstants.deletePost);
                                dialogHelper.hideDialog();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogHelper.hideDialog();
                            }
                        }, getDockActivity().getResources().getString(R.string.delete_post), getDockActivity().getResources().getString(R.string.are_you_sure_you_want_to_delete_post));

                        dialogHelper.showDialog();
                    }
                });

                reportPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        dialogHelper.initDropDown(R.layout.dialoge_delete_post, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                serviceHelper.enqueueCall(headerWebService.reportPost(entitiy.getId(), entitiy.getUserId()), WebServiceConstants.reportPost);
                                dialogHelper.hideDialog();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogHelper.hideDialog();
                            }
                        }, getDockActivity().getResources().getString(R.string.report_post), getDockActivity().getResources().getString(R.string.are_you_sure_you_want_to_report_post));

                        dialogHelper.showDialog();
                    }
                });

                reportUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        dialogHelper.initDropDown(R.layout.dialoge_delete_post, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                serviceHelper.enqueueCall(headerWebService.reportUser(entitiy.getUserId()), WebServiceConstants.reportUser);
                                dialogHelper.hideDialog();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogHelper.hideDialog();
                            }
                        }, getDockActivity().getResources().getString(R.string.report_user), getDockActivity().getResources().getString(R.string.are_you_sure_you_want_to_report_user));

                        dialogHelper.showDialog();
                    }
                });

                break;
        }
    }

    @Override
    public void ResponseSuccess(Object result, String Tag, String message) {
        super.ResponseSuccess(result, Tag, message);
        switch (Tag) {

            case postLike:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case favoritePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case WebServiceConstants.deletePost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
               getDockActivity().replaceDockableFragment(HomeFragment.newInstance(),"HomeFragment");
                break;

            case WebServiceConstants.reportPost:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;

            case WebServiceConstants.reportUser:
                UIHelper.showShortToastInCenter(getDockActivity(), message);
                break;
        }
    }



}
