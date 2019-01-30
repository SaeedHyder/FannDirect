package com.app.fandirect.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.app.fandirect.R;
import com.app.fandirect.ui.views.AnyEditTextView;
import com.app.fandirect.ui.views.AnyTextView;
import com.app.fandirect.ui.views.CustomRatingBar;

import com.jsibbold.zoomage.ZoomageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xw.repo.XEditText;


/**
 * Created on 5/24/2017.
 */

public class DialogHelper {
    private Dialog dialog;
    private Context context;
    private ImageLoader imageLoader;
    private RadioGroup rg;


    public DialogHelper(Context context) {
        this.context = context;
        this.dialog = new Dialog(context);
    }


    public Dialog initlogout(int layoutID, View.OnClickListener onokclicklistener, View.OnClickListener oncancelclicklistener) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        Button okbutton = (Button) dialog.findViewById(R.id.btn_yes);
        okbutton.setOnClickListener(onokclicklistener);
        Button cancelbutton = (Button) dialog.findViewById(R.id.btn_No);
        cancelbutton.setOnClickListener(oncancelclicklistener);
        return this.dialog;
    }

    public Dialog facebookLogin(View.OnClickListener userlistener, View.OnClickListener spListner) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(R.layout.dialoge_facebook_signup);
        Button userButton = (Button) dialog.findViewById(R.id.btn_User);
        userButton.setOnClickListener(userlistener);
        Button spButton = (Button) dialog.findViewById(R.id.btn_Sp);
        spButton.setOnClickListener(spListner);
        return this.dialog;
    }

    public Dialog initDropDown(int layoutID, View.OnClickListener onokclicklistener, View.OnClickListener oncancelclicklistener,String titleString,String textString) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        Button okbutton = (Button) dialog.findViewById(R.id.btn_yes);
        okbutton.setOnClickListener(onokclicklistener);
        Button cancelbutton = (Button) dialog.findViewById(R.id.btn_No);
        cancelbutton.setOnClickListener(oncancelclicklistener);
        AnyTextView title=(AnyTextView)dialog.findViewById(R.id.txt_title);
        AnyTextView textDetail=(AnyTextView)dialog.findViewById(R.id.txt_text);
        title.setText(titleString);
        textDetail.setText(textString);
        return this.dialog;
    }

    public Dialog alertDialoge(int layoutID, View.OnClickListener onokclicklistener,String text) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        Button okbutton = (Button) dialog.findViewById(R.id.btn_yes);
        AnyTextView textView = (AnyTextView) dialog.findViewById(R.id.txt_text);
        okbutton.setOnClickListener(onokclicklistener);
        textView.setText(text);
        return this.dialog;
    }

    public Dialog initCancelDialoge(int layoutID, View.OnClickListener Yesclicklistener, View.OnClickListener onNoclicklistener) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(Yesclicklistener);
        Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
        btnNo.setOnClickListener(onNoclicklistener);
        return this.dialog;
    }

    public Dialog initDeleteDialoge(View.OnClickListener Yesclicklistener, View.OnClickListener onNoclicklistener,String title,String Description) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(R.layout.dialoge_delete_message);
        AnyTextView txtTitle = (AnyTextView) dialog.findViewById(R.id.txt_title);
        txtTitle.setText(title);
        AnyTextView txtDescription = (AnyTextView) dialog.findViewById(R.id.txt_text);
        txtDescription.setText(Description);
        Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(Yesclicklistener);
        Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
        btnNo.setOnClickListener(onNoclicklistener);
        return this.dialog;
    }

    public Dialog initDeleteCommentDialoge(View.OnClickListener Yesclicklistener, View.OnClickListener onNoclicklistener,String title,String Description) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(R.layout.delete_diaoge);
        AnyTextView txtTitle = (AnyTextView) dialog.findViewById(R.id.txt_title);
        txtTitle.setText(title);
        AnyTextView txtDescription = (AnyTextView) dialog.findViewById(R.id.txt_text);
        txtDescription.setText(Description);
        Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(Yesclicklistener);
        Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
        btnNo.setOnClickListener(onNoclicklistener);
        return this.dialog;
    }

    public Dialog initFullImage(String linkImage) {
        this.dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        imageLoader=ImageLoader.getInstance();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(R.layout.fragment_fullscreen);
        final ZoomageView imageview = (ZoomageView) dialog.findViewById(R.id.imageFull);
        ImageView cross = (ImageView) dialog.findViewById(R.id.cross);
        imageLoader.displayImage(linkImage,imageview);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        return this.dialog;
    }

    public Dialog initUnfriendDialoge(int layoutID, View.OnClickListener Yesclicklistener, View.OnClickListener onNoclicklistener) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        Button btnYes = (Button) dialog.findViewById(R.id.btn_yes);
        btnYes.setOnClickListener(Yesclicklistener);
        Button btnNo = (Button) dialog.findViewById(R.id.btn_No);
        btnNo.setOnClickListener(onNoclicklistener);
        return this.dialog;
    }

    public Dialog addService( View.OnClickListener Yesclicklistener) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(R.layout.dialoge_suggest_service);
        Button btnDone = (Button) dialog.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(Yesclicklistener);
        return this.dialog;
    }

    public XEditText getSuggestionText() {
        XEditText text = (XEditText) dialog.findViewById(R.id.txt_service);
        return text;
    }

    public Dialog initCompleteService(int layoutID, View.OnClickListener onokclicklistener, View.OnClickListener oncancelclicklistener) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        Button okbutton = (Button) dialog.findViewById(R.id.btn_yes);
        okbutton.setOnClickListener(onokclicklistener);
        Button cancelbutton = (Button) dialog.findViewById(R.id.btn_No);
        cancelbutton.setOnClickListener(oncancelclicklistener);
        return this.dialog;
    }


    public Dialog initRating(int layoutID, View.OnClickListener onokclicklistener) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        Button okbutton = (Button) dialog.findViewById(R.id.btn_submit);

        okbutton.setOnClickListener(onokclicklistener);
        final CustomRatingBar rbAddRating = (CustomRatingBar) dialog.findViewById(R.id.rbAddRating);
        rbAddRating.setOnScoreChanged(new CustomRatingBar.IRatingBarCallbacks() {
            @Override
            public void scoreChanged(float score) {
                if (score < 1.0f)
                    rbAddRating.setScore(1.0f);
            }
        });

        return this.dialog;
    }

   public AnyEditTextView getRatingTextView() {
       AnyEditTextView text = (AnyEditTextView) dialog.findViewById(R.id.txt_feedback);
        return text;
    }



   public Float getRatingScore() {
        CustomRatingBar rbAddRating = (CustomRatingBar) dialog.findViewById(R.id.rbAddRating);
        return rbAddRating.getScore();
    }


    public Dialog intiImageDialoge(int layoutID, String imagePath) {
        imageLoader = ImageLoader.getInstance();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.dialog.setContentView(layoutID);
        ImageView iv_image = (ImageView) dialog.findViewById(R.id.imagedialoge);
        imageLoader.displayImage(imagePath, iv_image);


        return this.dialog;
    }


    public void showDialog() {

        dialog.show();
    }

    public void setCancelable(boolean isCancelable) {
        dialog.setCancelable(isCancelable);
        dialog.setCanceledOnTouchOutside(isCancelable);
    }

    public void hideDialog() {
        dialog.dismiss();
    }
}
