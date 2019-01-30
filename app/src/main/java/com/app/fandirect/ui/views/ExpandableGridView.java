package com.app.fandirect.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;


/**
 * Created by ahsan.majid on 10/26/2015.
 */
public class ExpandableGridView extends GridView {

    boolean expanded = false;

    public ExpandableGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isExpanded() {
        return true || expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            if (isExpanded()) {

                int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
                super.onMeasure(widthMeasureSpec, expandSpec);
                ViewGroup.LayoutParams params = getLayoutParams();
                params.height = getMeasuredHeight();

            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
