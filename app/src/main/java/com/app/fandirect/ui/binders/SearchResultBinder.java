package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.UserEnt;
import com.app.fandirect.global.AppConstants;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.interfaces.userProfileClick;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRatingBar;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.math.RoundingMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saeedhyder on 3/7/2018.
 */

public class SearchResultBinder extends ViewBinder<UserEnt> {

    private DockActivity dockActivity;
    private BasePreferenceHelper prefHelper;
    private ImageLoader imageLoader;
    private RecyclerViewItemListener clickListner;
    private userProfileClick profileClick;


    public SearchResultBinder(DockActivity dockActivity, BasePreferenceHelper prefHelper, RecyclerViewItemListener clickListner, userProfileClick profileClick) {
        super(R.layout.row_item_search_result);
        this.dockActivity = dockActivity;
        this.prefHelper = prefHelper;
        this.imageLoader = ImageLoader.getInstance();
        this.clickListner = clickListner;
        this.profileClick = profileClick;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final UserEnt entity, final int position, int grpPosition, View view, Activity activity) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.image.setImageResource(R.drawable.placeholder);
        if (entity.getImageUrl() != null) {
            Picasso.with(dockActivity).load(entity.getImageUrl()).placeholder(R.drawable.placeholder).into(viewHolder.image);
          //  imageLoader.displayImage(entity.getImageUrl(), viewHolder.image);
        }
        viewHolder.txtName.setText(entity.getUserName() + "");

        if (entity.getLocation() != null && !entity.getLocation().equals("")) {
            viewHolder.txtLocation.setVisibility(View.VISIBLE);
            viewHolder.txtLocation.setText(entity.getLocation());
        } else {
            viewHolder.txtLocation.setVisibility(View.GONE);
        }

        Double distance = 0.00;
        try {
            distance = round(Double.valueOf(entity.getDistance()), 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewHolder.txt_distance.setText(distance != null ? distance + " " + AppConstants.DistanceUnit : "-");
        if (entity.getRating() != null && entity.getRating() > 0) {
            viewHolder.rbAddRating.setScore(entity.getRating());
        } else {
            viewHolder.rbAddRating.setScore(0);
        }

        viewHolder.btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileClick.onSendRequestBtn(entity, position);
            }
        });
        viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileClick.onUserProfileClick(entity, position);
            }
        });


    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.image)
        CircleImageView image;
        @BindView(R.id.txt_name)
        AnyTextView txtName;
        @BindView(R.id.txt_location)
        AnyTextView txtLocation;
        @BindView(R.id.txt_distance)
        AnyTextView txt_distance;
        @BindView(R.id.rbAddRating)
        CustomRatingBar rbAddRating;
        @BindView(R.id.btn_request)
        Button btnRequest;
        @BindView(R.id.ll_item)
        LinearLayout llItem;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
