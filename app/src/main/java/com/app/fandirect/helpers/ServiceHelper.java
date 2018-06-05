package com.app.fandirect.helpers;

import android.util.Log;

import com.app.fandirect.activities.DockActivity;
import com.app.fandirect.entities.ResponseWrapper;
import com.app.fandirect.global.WebServiceConstants;
import com.app.fandirect.interfaces.webServiceResponseLisener;
import com.app.fandirect.retrofit.WebService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 7/17/2017.
 */

public class ServiceHelper<T> {
    private webServiceResponseLisener serviceResponseLisener;
    private DockActivity context;
    private WebService webService;

    public ServiceHelper(webServiceResponseLisener serviceResponseLisener, DockActivity conttext, WebService webService) {
        this.serviceResponseLisener = serviceResponseLisener;
        this.context = conttext;
        this.webService = webService;
    }

    public void enqueueCall(Call<ResponseWrapper<T>> call, final String tag) {
        if (InternetHelper.CheckInternetConectivityandShowToast(context)) {
            context.onLoadingStarted();
            call.enqueue(new Callback<ResponseWrapper<T>>() {
                @Override
                public void onResponse(Call<ResponseWrapper<T>> call, Response<ResponseWrapper<T>> response) {
                    context.onLoadingFinished();
                    if (response != null && response.body() != null) {
                        if (response.body().getCode().equals(WebServiceConstants.SUCCESS_RESPONSE_CODE)){
                            serviceResponseLisener.ResponseSuccess(response.body().getResult(), tag,response.body().getMessage());
                        } else {
                            UIHelper.showShortToastInCenter(context, response.body().getMessage());
                        }
                    }
                    else {
                        UIHelper.showShortToastInCenter(context, "No Service Response");
                    }

                }

                @Override
                public void onFailure(Call<ResponseWrapper<T>> call, Throwable t) {
                    context.onLoadingFinished();
                    t.printStackTrace();
                    Log.e(ServiceHelper.class.getSimpleName() + " by tag: " + tag, t.toString());
                }
            });
        }
    }

    public void enqueueCall(Call<ResponseWrapper<T>> call, final String tag,boolean loading) {
        if (InternetHelper.CheckInternetConectivityandShowToast(context)) {
            call.enqueue(new Callback<ResponseWrapper<T>>() {
                @Override
                public void onResponse(Call<ResponseWrapper<T>> call, Response<ResponseWrapper<T>> response) {

                    if (response != null && response.body() != null) {
                        if (response.body().getCode().equals(WebServiceConstants.SUCCESS_RESPONSE_CODE)){
                            serviceResponseLisener.ResponseSuccess(response.body().getResult(), tag,response.body().getMessage());
                        } else {
                            UIHelper.showShortToastInCenter(context, response.body().getMessage());
                        }
                    }
                    else {
                        serviceResponseLisener.ResponseFailure(tag);
                        UIHelper.showShortToastInCenter(context, "No Service Response");
                    }

                }

                @Override
                public void onFailure(Call<ResponseWrapper<T>> call, Throwable t) {

                    t.printStackTrace();
                    Log.e(ServiceHelper.class.getSimpleName() + " by tag: " + tag, t.toString());
                }
            });
        }
        }

    public void enqueueCall(Call<ResponseWrapper<T>> call, final String tag,boolean loading,boolean isProfile) {
        // if (InternetHelper.CheckInternetConectivityandShowToast(context)) {
        call.enqueue(new Callback<ResponseWrapper<T>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<T>> call, Response<ResponseWrapper<T>> response) {

                if (response != null && response.body() != null) {
                    if (response.body().getCode().equals(WebServiceConstants.SUCCESS_RESPONSE_CODE)){
                        serviceResponseLisener.ResponseSuccess(response.body().getResult(), tag,response.body().getMessage());
                    } else {
                        UIHelper.showShortToastInCenter(context, response.body().getMessage());
                    }
                }
                else {
                    serviceResponseLisener.ResponseFailure(tag);
                    UIHelper.showShortToastInCenter(context, "No Service Response");
                }

            }

            @Override
            public void onFailure(Call<ResponseWrapper<T>> call, Throwable t) {

                t.printStackTrace();
                Log.e(ServiceHelper.class.getSimpleName() + " by tag: " + tag, t.toString());
            }
        });
    }


}
