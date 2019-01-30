package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.entities.HomeSearchEnt;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saeedhyder on 3/5/2018.
 */

public class HomeSearchBinder extends ViewBinder<GetServicesEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;


    public HomeSearchBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner) {
        super(R.layout.row_item_home_search);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = clickListner;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final GetServicesEnt entity, final int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.txtCategory.setText(entity.getName());
        if (entity.getBannerUrl() != null) {
            Picasso.with(dockActivity).load(entity.getBannerUrl()).placeholder(R.drawable.placeholder_thumb).into(viewHolder.ivCategory);
         //   imageLoader.displayImage(entity.getBannerUrl(), viewHolder.ivCategory);
        }
        viewHolder.rlHomeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListner.onRecyclerItemClicked(entity, position);
            }
        });


    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_category)
        ImageView ivCategory;
        @BindView(R.id.txt_category)
        AnyTextView txtCategory;
        @BindView(R.id.rl_home_search)
        RelativeLayout rlHomeSearch;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
