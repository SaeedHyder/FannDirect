package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.commentEnt;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

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


    public CommentBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper) {
        super(R.layout.row_item_comment);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final commentEnt entity, int position, int grpPosition, View view, Activity activity) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (entity.getUserDetail().getImageUrl() != null)
            imageLoader.displayImage(entity.getUserDetail().getImageUrl(), viewHolder.ivImage);

        viewHolder.tvNameCommentor.setText(entity.getUserDetail().getUserName());
        viewHolder.tvCommentBox.setText(entity.getComment() + "");
        viewHolder.tvDate.setText(DateHelper.getLocalDateTime(entity.getCreatedAt()));


        viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(entity.getUserDetail().getRoleId().equals(UserRoleId)) {
                    dockActivity.replaceDockableFragment(UserProfileFragment.newInstance(entity.getUserDetail().getId() + ""), "UserProfileFragment");
                }else{
                    dockActivity.replaceDockableFragment(SpProfileFragment.newInstance(entity.getUserDetail().getId() + ""), "UserProfileFragment");
                }
            }
        });
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
