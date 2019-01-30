package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.activities.MainActivity;
import com.app.fandirect.entities.HomeGridEnt;
import com.app.fandirect.entities.TilesCountEnt;
import com.app.fandirect.fragments.FannsFragment;
import com.app.fandirect.fragments.FeedFragment;
import com.app.fandirect.fragments.FriendRequestFragment;
import com.app.fandirect.fragments.MessageFragment;
import com.app.fandirect.fragments.PromotionsFragment;
import com.app.fandirect.fragments.ServiceHistorySpFragment;
import com.app.fandirect.fragments.ServiceHistoryUserFragment;
import com.app.fandirect.fragments.SettingFragment;
import com.app.fandirect.fragments.SpProfileFragment;
import com.app.fandirect.fragments.SpPromotionsPostFragment;
import com.app.fandirect.fragments.UserProfileFragment;
import com.app.fandirect.fragments.WallPostsFragment;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saeedhyder on 3/1/2018.
 */

public class HomeGridItemBinder extends ViewBinder<HomeGridEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;
    private Picasso picasso;
    private TilesCountEnt data;


    public HomeGridItemBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner, TilesCountEnt data) {
        super(R.layout.row_item_home_grid);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = clickListner;
        picasso = Picasso.with(dockActivity);
        this.data = data;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final HomeGridEnt entity, final int position, int grpPosition, View view, Activity activity) {

        ViewGroup gridView = (ViewGroup) view;
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(GridView.LayoutParams.MATCH_PARENT, getScreenHeight(activity) / 3); // new GridView.LayoutParams(view.getLayoutParams());
        gridView.setLayoutParams(layoutParams);

        final ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.ivImage.setImageResource(entity.getImage());
       // imageLoader.displayImage(entity.getImage(), viewHolder.ivImage);
       // picasso.load(entity.getImage()).into(viewHolder.ivImage);
        viewHolder.txtCategory.setText(entity.getText() + "");

        if (data != null) {
            if (position == 1) {
                if (data.getFeeds() != null && data.getFeeds() > 0) {
                    viewHolder.txtBadge.setVisibility(View.VISIBLE);
                    viewHolder.txtBadge.setText(data.getFeeds() + "");
                } else {
                    viewHolder.txtBadge.setVisibility(View.GONE);
                }

            } else if (position == 2) {
                if (data.getMessages() != null && data.getMessages() > 0) {
                    viewHolder.txtBadge.setVisibility(View.VISIBLE);
                    viewHolder.txtBadge.setText(data.getMessages() + "");
                } else {
                    viewHolder.txtBadge.setVisibility(View.GONE);
                }

            } else if (position == 4) {
                if (data.getFriendRequest() != null && data.getFriendRequest() > 0) {
                    viewHolder.txtBadge.setVisibility(View.VISIBLE);
                    viewHolder.txtBadge.setText(data.getFriendRequest() + "");
                } else {
                    viewHolder.txtBadge.setVisibility(View.GONE);
                }

            } else if (position == 5) {
                if (data.getPromotions() != null && data.getPromotions() > 0) {
                    viewHolder.txtBadge.setVisibility(View.VISIBLE);
                    viewHolder.txtBadge.setText(data.getPromotions() + "");
                } else {
                    viewHolder.txtBadge.setVisibility(View.GONE);
                }

            } else {
                viewHolder.txtBadge.setVisibility(View.GONE);
            }
        }


    }

    private int getScreenHeight(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        int screenHeight = size.y;
        screenHeight = screenHeight - (int) activity.getResources().getDimension(R.dimen.x150) - (int)
                (((MainActivity) activity).titleBar.getHeight());
        return screenHeight;
    }


    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.txt_category)
        AnyTextView txtCategory;
        @BindView(R.id.ll_item)
        LinearLayout llItem;
        @BindView(R.id.txtBadge)
        AnyTextView txtBadge;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
