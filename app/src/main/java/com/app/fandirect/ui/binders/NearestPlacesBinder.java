package com.app.fandirect.ui.binders;

import android.content.Context;
import android.view.View;

import com.app.fandirect.R;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.RecyclerViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import NearbyLocation.entities.PlacesEnt;
import NearbyLocation.entities.Result;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saeedhyder on 4/21/2018.
 */

public class NearestPlacesBinder extends RecyclerViewBinder<Result> {

    private ImageLoader imageLoader;
    private RecyclerViewItemListener recyclerViewItemListener;

    public NearestPlacesBinder(RecyclerViewItemListener recyclerViewItemListener) {
        super(R.layout.row_item_nearest_location);
        imageLoader = ImageLoader.getInstance();
        this.recyclerViewItemListener = recyclerViewItemListener;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final Result entity, final int position, Object viewHolder, Context context) {
        ViewHolder holder = (ViewHolder) viewHolder;
        if (entity != null )
            holder.serviceType.setText(entity.getName() + "");

        holder.serviceType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemListener.onRecyclerItemClicked(entity,position);
            }
        });

    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.serviceType)
        AnyTextView serviceType;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
