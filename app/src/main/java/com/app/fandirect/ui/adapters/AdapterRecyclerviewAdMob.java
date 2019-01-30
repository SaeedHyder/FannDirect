package com.app.fandirect.ui.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.activities.MainActivity;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.interfaces.PostClicksInterface;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.ReportPostIntetface;
import com.app.fandirect.ui.views.AnyTextView;
import com.google.android.gms.ads.NativeExpressAdView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;

public class AdapterRecyclerviewAdMob extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Object> posts;
    private DockActivity dockActivity;
    private MainActivity mainActivity;
    private ImageLoader imageLoader;
    private PostClicksInterface postClicksInterface;
    private ReportPostIntetface reportPostIntetface;
    private RecyclerViewItemListener clickListner;
    private BasePreferenceHelper prefHelper;
    private long mLastClickTime = 0;

    public static final int MENU_ITME_VIEW_TYPE = 0;
    public static final int NATIVE_EXPRESS_AD_VIEW_TYPE = 1;

    public AdapterRecyclerviewAdMob(ArrayList<Object> posts, DockActivity context, MainActivity mainActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner, PostClicksInterface postClicksInterface, ReportPostIntetface reportPostIntetface) {
        this.posts = posts;
        this.dockActivity = context;
        this.mainActivity = mainActivity;
        this.prefHelper = prefHelper;
        this.clickListner = clickListner;
        this.postClicksInterface = postClicksInterface;
        this.reportPostIntetface = reportPostIntetface;
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MENU_ITME_VIEW_TYPE) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_item_feeds, parent, false);
            return new MyViewHolder(itemView);
        } else {
            View nativeExpressLayoutView = LayoutInflater.from(
                    parent.getContext()).inflate(R.layout.row_item_native_ad, parent, false);
            return new NativeExpressAdViewHolder(nativeExpressLayoutView);
        }

    }


    @Override
    public int getItemViewType(int position) {
        return (position % AppConstants.ITEM_PER_AD == 0 ? NATIVE_EXPRESS_AD_VIEW_TYPE : MENU_ITME_VIEW_TYPE);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case MENU_ITME_VIEW_TYPE:
                final MyViewHolder viewHolder = (MyViewHolder) holder;
                final Post entity = (Post)posts.get(position);

                if (entity.getFeedType().toString().equals("image")) {
                    viewHolder.ivPostImage.setVisibility(View.VISIBLE);
                    if (entity.getImageUrl() != null) {
                        imageLoader.displayImage(entity.getImageUrl(), viewHolder.ivPostImage);
                    }
                } else {
                    viewHolder.ivPostImage.setVisibility(View.GONE);
                }

                if (entity.getUserDetail().getImageUrl() != null)
                    imageLoader.displayImage(entity.getUserDetail().getImageUrl(), viewHolder.ivImage);

                viewHolder.txtName.setText(entity.getUserDetail().getUserName());
                viewHolder.txtDate.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss", "MM-dd-yy", entity.getCreated_at()));
                viewHolder.txtDescription.setText(entity.getDescription());

                if (entity.getLocation() != null && !entity.getLocation().equals("")) {
                    viewHolder.txtAddress.setText(entity.getLocation());
                    viewHolder.txtAddress.setVisibility(View.VISIBLE);
                } else {

                    viewHolder.txtAddress.setVisibility(View.GONE);
                }


                if (entity.getLikeCount() != 0) {
                    viewHolder.like.setText(entity.getLikeCount() + "");
                } else {
                    viewHolder.like.setText("");
                }
                if (entity.getCommentCount() != 0) {
                    viewHolder.comment.setText(entity.getCommentCount() + "");
                } else {
                    viewHolder.comment.setText("");
                }
                if (entity.getIsLike() == 1) {
                    viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_like, 0, 0, 0);
                } else {
                    viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_like, 0, 0, 0);
                }

                if (entity.getIsFavourite() == 1) {
                    viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_fav, 0, 0, 0);
                } else {
                    viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_fav, 0, 0, 0);
                }

                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (entity.getIsLike() == 0) {
                            viewHolder.like.setText(String.valueOf(entity.getLikeCount() + 1));
                            viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_like, 0, 0, 0);
                            entity.setLikeCount(entity.getLikeCount() + 1);
                            entity.setIsLike(1);
                        } else {
                            viewHolder.like.setText(String.valueOf(entity.getLikeCount() - 1));
                            viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_like, 0, 0, 0);
                            entity.setIsLike(0);
                            entity.setLikeCount(entity.getLikeCount() - 1);
                        }

                        postClicksInterface.like(entity, position);
                    }
                });

                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (entity.getIsFavourite() == 0) {
                            viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.selected_fav, 0, 0, 0);
                            entity.setIsFavourite(1);

                        } else {
                            viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.unselected_fav, 0, 0, 0);
                            entity.setIsFavourite(0);
                        }
                        postClicksInterface.favorite(entity, position);
                    }
                });
                viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postClicksInterface.comment(entity, position);
                    }
                });

                viewHolder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 3000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        postClicksInterface.share(entity, position);
                    }
                });


                viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (entity.getUserDetail().getRoleId().equals(UserRoleId)) {
                            dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(entity.getUserDetail().getId() + ""), "UserProfileFragment");
                        } else {
                            dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(entity.getUserDetail().getId() + ""), "SpProfileFragment");
                        }
                    }
                });

                viewHolder.menuBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater layoutInflater = (LayoutInflater) dockActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View customView = layoutInflater.inflate(R.layout.popup_window_dialoge, null);
                        final DialogHelper dialogHelper = new DialogHelper(dockActivity);

                        AnyTextView deletePost = (AnyTextView) customView.findViewById(R.id.txt_delete_post);
                        AnyTextView reportPost = (AnyTextView) customView.findViewById(R.id.txt_report_post);
                        AnyTextView reportUser = (AnyTextView) customView.findViewById(R.id.txt_repost_user);
                        View viewReport = (View) customView.findViewById(R.id.view_report);

                        final PopupWindow popupWindow = new PopupWindow(customView, (int) dockActivity.getResources().getDimension(R.dimen.x130), LinearLayout.LayoutParams.WRAP_CONTENT);
                        //  popupWindow.showAtLocation(viewHolder.llMainframe, Gravity.CENTER, 0, 0);


                        popupWindow.setBackgroundDrawable(new BitmapDrawable());
                        popupWindow.setOutsideTouchable(true);
                        popupWindow.setTouchable(true);

                        popupWindow.showAsDropDown(v);

                        if (String.valueOf(entity.getUserDetail().getId()).equals(prefHelper.getUser().getId())) {
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
                                        reportPostIntetface.DeletePost(entity, position);
                                        dialogHelper.hideDialog();
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogHelper.hideDialog();
                                    }
                                }, dockActivity.getResources().getString(R.string.delete_post), dockActivity.getResources().getString(R.string.are_you_sure_you_want_to_delete_post));

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
                                        reportPostIntetface.reportPost(entity, position);
                                        dialogHelper.hideDialog();
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogHelper.hideDialog();
                                    }
                                }, dockActivity.getResources().getString(R.string.report_post), dockActivity.getResources().getString(R.string.are_you_sure_you_want_to_report_post));

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
                                        reportPostIntetface.reportUser(entity, position);
                                        dialogHelper.hideDialog();
                                    }
                                }, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogHelper.hideDialog();
                                    }
                                }, dockActivity.getResources().getString(R.string.report_user), dockActivity.getResources().getString(R.string.are_you_sure_you_want_to_report_user));

                                dialogHelper.showDialog();
                            }
                        });


                    }
                });

                viewHolder.ivPostImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DialogHelper dialoge = new DialogHelper(dockActivity);
                        dialoge.initFullImage(entity.getImageUrl());
                        dialoge.showDialog();

                    }
                });
                break;

            case NATIVE_EXPRESS_AD_VIEW_TYPE:

                NativeExpressAdViewHolder nativeExpressAdViewHolder=(NativeExpressAdViewHolder)holder;
                NativeExpressAdView adView=(NativeExpressAdView)posts.get(position);
                ViewGroup adCardView=(ViewGroup)nativeExpressAdViewHolder.itemView;

                if(adCardView.getChildCount()>0){
                    adCardView.removeAllViews();
                }
                if(adView.getParent()!=null){
                    ((ViewGroup)adView.getParent()).removeView(adView);
                }
                adCardView.addView(adView);

                break;
        }


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.txt_date)
        AnyTextView txtDate;
        @BindView(R.id.menu_btn)
        Button menuBtn;
        @BindView(R.id.ll_name)
        LinearLayout llName;
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
        @BindView(R.id.ll_mainframe)
        LinearLayout llMainframe;

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}



