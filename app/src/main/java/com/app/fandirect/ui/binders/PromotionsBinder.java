package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.Post;
import com.app.fandirect.entities.TagName;
import com.app.fandirect.fragments.LikePostFragment;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.interfaces.PostClicksInterface;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.ReportPostIntetface;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;

/**
 * Created by saeedhyder on 3/14/2018.
 */

public class PromotionsBinder extends ViewBinder<Post> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;
    private PostClicksInterface postClicksInterface;
    private PopupMenu popup;
    private ReportPostIntetface reportPostIntetface;
    private long mLastClickTime = 0;


    public PromotionsBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner, PostClicksInterface postClicksInterface, ReportPostIntetface reportPostIntetface) {
        super(R.layout.row_item_feeds);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = clickListner;
        this.postClicksInterface = postClicksInterface;
        this.reportPostIntetface = reportPostIntetface;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);

    }

    @Override
    public void bindView(final Post entity, final int position, int grpPosition, View view, Activity activity) {

        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (entity.getFeedType().toString().equals("image")) {
            viewHolder.ivPostImage.setVisibility(View.VISIBLE);
            if (entity.getImageUrl() != null) {
                imageLoader.displayImage(entity.getImageUrl(), viewHolder.ivPostImage);
            }
        } else {
            viewHolder.ivPostImage.setVisibility(View.GONE);
        }

        if (entity.getUserDetail() != null && entity.getUserDetail().getImageUrl() != null)
            imageLoader.displayImage(entity.getUserDetail().getImageUrl(), viewHolder.ivImage);

        if(entity.getUserDetail()!=null && entity.getUserDetail().getUserName()!=null) {
            viewHolder.txtName.setText(entity.getUserDetail().getUserName());
        }
        viewHolder.txtDate.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss", "MM-dd-yy", entity.getCreated_at()));

        if (entity.getTagPersonName()!=null && entity.getTagPersonName().size() > 0) {
            makeLinks(viewHolder.txtDescription, entity.getTagPersonName(), entity.getPostText().replace("@", ""), entity);

        } else {
            viewHolder.txtDescription.setText(entity.getPostText());
        }

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

        viewHolder.txtName.setOnClickListener(new View.OnClickListener() {
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

                  /*     Intent intent=new Intent(dockActivity,FullscreenActivity.class);
                intent.putExtra("Image",entity.getImageUrl());
                dockActivity.startActivity(intent);*/
                //   dockActivity.replaceDockableFragment(FullScreenImageFragment.newInstance(entity.getImageUrl()),"FullScreenImageFragment");
            }
        });

        viewHolder.llLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entity.getLikeCount() > 0) {
                    dockActivity.addDockableFragment(LikePostFragment.newInstance(entity.getId()+""), "LikePostFragment");
                }
            }
        });

       /* if (String.valueOf(entity.getUserDetail().getId()).equals(prefHelper.getUser().getId())) {
            viewHolder.menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup = new PopupMenu(dockActivity, viewHolder.menuBtn);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            final DialogHelper dialogHelper = new DialogHelper(dockActivity);

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


                            return true;
                        }
                    });

                    popup.show();
                }
            });
        } else {
            viewHolder.menuBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popup = new PopupMenu(dockActivity, viewHolder.menuBtn);
                    //Inflating the Popup using xml file
                    popup.getMenuInflater()
                            .inflate(R.menu.popup_report, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            final DialogHelper dialogHelper = new DialogHelper(dockActivity);

                            if(item.getTitle().toString().equals(dockActivity.getResources().getString(R.string.report_post))) {
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
                            }else{
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

                            return true;
                        }
                    });

                    popup.show();
                }
            });
        }*/
    }

    public void makeLinks(TextView textView, ArrayList<TagName> links, String text, Post entity) {

        SpannableString spannableString = new SpannableString(text);
        for (TagName item : links) {
            int startIndexOfLink = text.indexOf(item.getUserName());
            if (item.getDeletedAt() != null && !item.getDeletedAt().equals("") && !item.getDeletedAt().equals("null")) {
                if (startIndexOfLink != -1) {
                    spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), startIndexOfLink, startIndexOfLink + item.getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }else{
                if (startIndexOfLink != -1) {
                    spannableString.setSpan(getClickableSpan(item, item.getId() + ""), startIndexOfLink, startIndexOfLink + item.getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

            }
        }

        textView.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
    }


    private ClickableSpan getClickableSpan(final TagName entity, final String id) {
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                if (entity!=null && entity.getRoleId()!=null && entity.getRoleId().equals(UserRoleId)) {
                    ( (DockActivity)dockActivity).addDockableFragment(UserProfileFragment.newInstance(id), "UserProfileFragment");
                } else {
                    ( (DockActivity)dockActivity).addDockableFragment(SpProfileFragment.newInstance(id), "SpProfileFragment");
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        return clickableSpan;
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.txt_date)
        AnyTextView txtDate;
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
        @BindView(R.id.menu_btn)
        Button menuBtn;
        @BindView(R.id.ll_likeBtn)
        LinearLayout llLikeBtn;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
