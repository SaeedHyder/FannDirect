package com.app.fandirect.retrofit;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WebServiceFactory {

    private static WebService webService;

    public static WebService getWebServiceInstanceWithCustomInterceptor(Context context, String endPoint) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OKHttpClientCreator.createCustomInterceptorClient(context))
                .build();

        webService = retrofit.create(WebService.class);

        return webService;

    }

    public static WebService getWebServiceInstanceWithCustomInterceptorandheader(Context context, String endPoint) {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endPoint)
                //.addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(OKHttpClientCreator.createCustomInterceptorClientwithHeader(context))
                .build();

        webService = retrofit.create(WebService.class);

        return webService;

    }

    public static WebService getWebServiceInstanceWithDefaultInterceptor(Context context, String endPoint) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(endPoint)
                .addConverterFactory(GsonConverterFactory.create())
                .client(OKHttpClientCreator.createDefaultInterceptorClient(context))
                .build();

        webService = retrofit.create(WebService.class);

        return webService;

    }

}
