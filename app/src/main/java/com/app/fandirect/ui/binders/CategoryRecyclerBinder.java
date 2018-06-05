package com.app.fandirect.ui.binders;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.app.fandirect.R;
import com.app.fandirect.entities.CategoryEnt;
import com.app.fandirect.entities.GetServicesEnt;
import com.app.fandirect.interfaces.RecyclerViewItemListener;
import com.app.fandirect.ui.viewbinders.abstracts.RecyclerViewBinder;
import com.app.fandirect.ui.views.AnyTextView;
import com.google.android.gms.vision.text.Line;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by saeedhyder on 3/14/2018.
 */

public class CategoryRecyclerBinder extends RecyclerViewBinder<GetServicesEnt> {

    private ImageLoader imageLoader;
    private RecyclerViewItemListener recyclerViewItemListener;

    public CategoryRecyclerBinder(RecyclerViewItemListener recyclerViewItemListener) {
        super(R.layout.row_item_promotion_category);
        imageLoader = ImageLoader.getInstance();
        this.recyclerViewItemListener = recyclerViewItemListener;
    }

    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindView(final GetServicesEnt entity, final int position, final Object viewHolder, Context context) {

        ViewHolder holder = (ViewHolder) viewHolder;
        holder.txtCategoryName.setText(entity.getName() + "");
        imageLoader.displayImage(entity.getLogoUrl(),holder.ivCategoryImage);

        holder.llCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewItemListener.onRecyclerItemClicked(entity,position);
            }
        });
    }

    static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.iv_category_image)
        CircleImageView ivCategoryImage;
        @BindView(R.id.txt_category_name)
        AnyTextView txtCategoryName;
        @BindView(R.id.ll_category)
        LinearLayout llCategory;



        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
