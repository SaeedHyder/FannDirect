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


    public HomeGridItemBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner) {
        super(R.layout.row_item_home_grid);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = clickListner;
        picasso = Picasso.with(dockActivity);
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
     //   imageLoader.displayImage(entity.getImage(), viewHolder.ivImage);
        picasso.load(entity.getImage()).into(viewHolder.ivImage);
        viewHolder.txtCategory.setText(entity.getText() + "");

        //
/*        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onUserProfileClick(View v) {
                clickListner.onRecyclerItemClicked(entity, position);
            }
        });*/


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


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
