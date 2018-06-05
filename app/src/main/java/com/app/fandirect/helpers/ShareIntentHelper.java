package com.app.fandirect.helpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.app.fandirect.activities.DockActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//import org.apache.commons.io.FileUtils;

/**
 * Created by saeedhyder on 5/22/2018.
 */

public class ShareIntentHelper {


    private ShareIntentHelper() {
    }

    public static void shareTextIntent(DockActivity context, String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.onLoadingFinished();
        try {
          //  context.startActivity(intent);
            context.startActivity(Intent.createChooser(intent, "send"));
        } catch (android.content.ActivityNotFoundException ex) {
            UIHelper.showShortToastInCenter(context, "App have not been installed.");
        }
    }


    public static void shareImageAndTextResultIntent(DockActivity context, String image, String text) {
        try {

          getBitmap(image, context,text);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getBitmap(String image, final DockActivity context, final String text) {
        Picasso.with(context)
                .load(image)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Uri imageUri = getImageUri(context,bitmap);
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                        shareIntent.setType("image/jpeg");
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                       context.startActivity(Intent.createChooser(shareIntent, "send"));

                       context.onLoadingFinished();
                     //  context.startActivity(shareIntent);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        context.onLoadingFinished();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }



    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);

    }
}
