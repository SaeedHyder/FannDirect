package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.TagName;
import com.app.fandirect.entities.commentEnt;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.helpers.DialogHelper;
import com.app.fandirect.interfaces.DeleteComment;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;

/**
 * Created by saeedhyder on 5/17/2018.
 */

public class CommentBinder extends ViewBinder<commentEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private DeleteComment clickListner;
    //  private String[] nameArray;
    private ArrayList<String> nameArray = new ArrayList<>();


    public CommentBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, DeleteComment listner) {
        super(R.layout.row_item_comment);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = listner;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final commentEnt entity, final int position, int grpPosition, View view, Activity activity) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (entity != null) {
            if (entity.getUserDetail() != null && entity.getUserDetail().getImageUrl() != null)
                imageLoader.displayImage(entity.getUserDetail().getImageUrl(), viewHolder.ivImage);

            if (entity.getTagPersonName() != null && entity.getTagPersonName().size() > 0) {
                makeLinks(viewHolder.tvCommentBox, entity.getTagPersonName(), entity.getCommentText().replace("@", ""), entity);

            } else {
                viewHolder.tvCommentBox.setText(entity.getCommentText());
            }

            if (entity.getUserDetail() != null && entity.getUserDetail().getId() != null && String.valueOf(entity.getUserDetail().getId()).equals(prefHelper.getUser().getId())) {
                viewHolder.menuBtn.setVisibility(View.VISIBLE);
            } else {
                viewHolder.menuBtn.setVisibility(View.GONE);
            }

            if (entity.getUserDetail() != null) {
                viewHolder.tvNameCommentor.setText(entity.getUserDetail().getUserName());
            }
            viewHolder.tvDate.setText(DateHelper.getLocalDateTime(entity.getCreatedAt()));

        }
        viewHolder.menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) dockActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View customView = layoutInflater.inflate(R.layout.popup_comment, null);
                final DialogHelper dialogHelper = new DialogHelper(dockActivity);

                AnyTextView deletePost = (AnyTextView) customView.findViewById(R.id.txt_delete_comment);

                final PopupWindow popupWindow = new PopupWindow(customView, (int) dockActivity.getResources().getDimension(R.dimen.x130), LinearLayout.LayoutParams.WRAP_CONTENT);
                //  popupWindow.showAtLocation(viewHolder.llMainframe, Gravity.CENTER, 0, 0);
                popupWindow.setBackgroundDrawable(new BitmapDrawable());
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);

                popupWindow.showAsDropDown(view);

                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                        dialogHelper.initDropDown(R.layout.dialoge_delete_post, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickListner.DeleteComment(entity, position);
                                dialogHelper.hideDialog();
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogHelper.hideDialog();
                            }
                        }, dockActivity.getResources().getString(R.string.delete_comment), dockActivity.getResources().getString(R.string.are_you_sure_you_want_to_delete_comment));

                        dialogHelper.showDialog();
                    }
                });

            }
        });

        viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getUserDetail()!=null && entity.getUserDetail().getRoleId().equals(UserRoleId)) {
                    dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(entity.getUserDetail().getId() + ""), "UserProfileFragment");
                } else {
                    dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(entity.getUserDetail().getId() + ""), "UserProfileFragment");
                }
            }
        });
    }

    public void makeLinks(TextView textView, ArrayList<TagName> links, String text, commentEnt entity) {
        SpannableString spannableString = new SpannableString(text);

        for (TagName item : links) {
            int startIndexOfLink = text.indexOf(item.getUserName());
            if (item.getDeletedAt() != null && !item.getDeletedAt().equals("") && !item.getDeletedAt().equals("null")) {
                if (startIndexOfLink != -1) {
                    spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), startIndexOfLink, startIndexOfLink + item.getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } else {
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
                if (entity != null && entity.getRoleId() != null && entity.getRoleId().equals(UserRoleId)) {
                    dockActivity.addDockableFragment(UserProfileFragment.newInstance(id), "UserProfileFragment");
                } else {
                    dockActivity.addDockableFragment(SpProfileFragment.newInstance(id), "SpProfileFragment");
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
        @BindView(R.id.tv_NameCommentor)
        AnyTextView tvNameCommentor;
        @BindView(R.id.tv_CommentBox)
        AnyTextView tvCommentBox;
        @BindView(R.id.tv_date)
        AnyTextView tvDate;
        @BindView(R.id.mainFrameLayout)
        LinearLayout mainFrameLayout;
        @BindView(R.id.menu_btn)
        Button menuBtn;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
