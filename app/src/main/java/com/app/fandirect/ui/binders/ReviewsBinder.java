package com.app.fandirect.ui.binders;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.entities.ReviewsEnt;
import com.app.fandirect.helpers.BasePreferenceHelper;
import com.app.fandirect.helpers.DateHelper;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.RecyclerViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewsBinder extends RecyclerViewBinder<ReviewsEnt> {

    private RecyclerViewItemListener clickListner;

    private ImageLoader imageLoader;
    private RecyclerViewItemListener recyclerViewItemListener;
    private BasePreferenceHelper preferenceHelper;
    private boolean isBold = false;

    public ReviewsBinder(RecyclerViewItemListener recyclerViewItemListener, boolean isBold) {
        super(R.layout.row_item_reviews);
        imageLoader = ImageLoader.getInstance();
        this.clickListner = recyclerViewItemListener;
        this.isBold = isBold;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final ReviewsEnt entity, final int position, Object viewHolder, Context context) {

        final ViewHolder holder = (ViewHolder) viewHolder;
        imageLoader.displayImage(entity.getSenderDetail().getImageUrl(), holder.ivImage);

        holder.txtName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
       /* if(isBold){
            holder.txtName.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        }else{
            holder.txtName.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }*/

        if (entity != null) {
            if (entity.getSenderDetail() != null) {
                holder.txtName.setText(entity.getSenderDetail().getUserName() + "");
            }
            holder.txtDescription.setText(entity.getFeedback() + "");
            holder.txtTimeDate.setText(DateHelper.getLocalDateTime(entity.getCreatedAt()));
        }

        holder.mainFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListner.onRecyclerItemClicked(entity, position);
            }
        });
    }


    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_image)
        CircleImageView ivImage;
        @BindView(R.id.txt_name)
        TextView txtName;
        @BindView(R.id.txt_description)
        AnyTextView txtDescription;
        @BindView(R.id.txt_time_date)
        AnyTextView txtTimeDate;
        @BindView(R.id.mainFrameLayout)
        LinearLayout mainFrameLayout;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
