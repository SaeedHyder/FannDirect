package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.Post;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.interfaces.PostClicksInterface;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.fandirect.global.AppConstants.UserRoleId;

/**
 * Created by saeedhyder on 3/14/2018.
 */

public class FeedsBinder extends ViewBinder<Post> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;
    private PostClicksInterface postClicksInterface;


    public FeedsBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner, PostClicksInterface postClicksInterface) {
        super(R.layout.row_item_feeds);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = clickListner;
        this.postClicksInterface = postClicksInterface;
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

        if (entity.getUserDetail().getImageUrl() != null)
            imageLoader.displayImage(entity.getUserDetail().getImageUrl(), viewHolder.ivImage);

        viewHolder.txtName.setText(entity.getUserDetail().getUserName());
        viewHolder.txtDate.setText(DateHelper.getFormatedDate("yyyy-MM-dd HH:mm:ss", "dd-MM-yyyy", entity.getCreated_at()));
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
            viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like4, 0, 0, 0);
        } else {
            viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like3, 0, 0, 0);
        }

        if (entity.getIsFavourite() == 1) {
            viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fav, 0, 0, 0);
        } else {
            viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fav3, 0, 0, 0);
        }

        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getIsLike() == 0) {
                    viewHolder.like.setText(String.valueOf(entity.getLikeCount() + 1));
                    viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like4, 0, 0, 0);
                    entity.setLikeCount(entity.getLikeCount() + 1);
                    entity.setIsLike(1);
                } else {
                    viewHolder.like.setText(String.valueOf(entity.getLikeCount() - 1));
                    viewHolder.like.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like3, 0, 0, 0);
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
                    viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fav, 0, 0, 0);
                    entity.setIsFavourite(1);

                } else {
                    viewHolder.favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.fav3, 0, 0, 0);
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


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
