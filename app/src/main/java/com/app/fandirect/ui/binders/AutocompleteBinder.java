package com.app.fandirect.ui.binders;

import android.app.Activity;
import android.graphics.Typeface;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.fandirect.R;
import com.app.fandirect.ui.viewbinders.abstracts.ViewBinder;
import com.google.android.gms.location.places.AutocompletePrediction;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 6/23/2017.
 */

public class AutocompleteBinder extends ViewBinder<AutocompletePrediction> {

    private String places;
    private String places2;
    @Override
    public BaseViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    public AutocompleteBinder() {
        super(R.layout.row_item_autocomplete);
    }

    @Override
    public void bindView(AutocompletePrediction entity, int position, int grpPosition, View view, Activity activity) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();
        places=entity.getPrimaryText(new StyleSpan(Typeface.BOLD)).toString();
        places2=entity.getSecondaryText(new StyleSpan(Typeface.BOLD)).toString();
        if(places.contains("null")){
            places = places.replace("null,", "");
        }
        if(places2.contains("null")){
            places2 = places2.replace("null,", "");
        }

       /* viewHolder. text1.setText(entity.getPrimaryText(new StyleSpan(Typeface.BOLD)));
        viewHolder.  text2.setText(entity.getSecondaryText(new StyleSpan(Typeface.BOLD)));*/

        viewHolder. text1.setText(places);
        viewHolder.  text2.setText(places2);

       // viewHolder.imgIcon.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.set_location));
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.img_icon)
        ImageView imgIcon;
        @BindView(R.id.text1)
        TextView text1;
        @BindView(R.id.text2)
        TextView text2;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
