package com.app.fandirect;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;


import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.utils.L;

import io.fabric.sdk.android.Fabric;

public class BaseApplication extends MultiDexApplication {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		MultiDex.install(this);
		MobileAds.initialize(this, getResources().getString(R.string.adMobId));
		FacebookSdk.sdkInitialize(this);
		FirebaseApp.initializeApp(this);
		initImageLoader();
		Fabric.with(this, new Crashlytics());

	}
	
	public void initImageLoader() {
		
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri( R.color.black )
				.showImageOnFail( R.color.black ).resetViewBeforeLoading( true )
				.cacheInMemory( true ).cacheOnDisc( true )
				
				.imageScaleType( ImageScaleType.IN_SAMPLE_POWER_OF_2 )
				.displayer( new FadeInBitmapDisplayer( 850 ) )
				.bitmapConfig( Bitmap.Config.RGB_565 ).build();
		
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext() ).defaultDisplayImageOptions( options )
				.build();
		
		ImageLoader.getInstance().init( config );
		L.disableLogging();
	}
	
}
