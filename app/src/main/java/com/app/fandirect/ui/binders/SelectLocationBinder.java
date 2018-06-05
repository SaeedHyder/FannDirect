package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import NearbyLocation.entities.PlacesEnt;
import NearbyLocation.entities.Result;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by saeedhyder on 5/10/2018.
 */

public class SelectLocationBinder extends ViewBinder<Result> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;



    public SelectLocationBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper) {
        super(R.layout.row_item_location);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();

    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final Result entity, final int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.txtPlaceName.setText(entity.getName());
        viewHolder.txtDescription.setText(entity.getVicinity());


    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.txt_place_name)
        TextView txtPlaceName;
        @BindView(R.id.txt_description)
        TextView txtDescription;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
