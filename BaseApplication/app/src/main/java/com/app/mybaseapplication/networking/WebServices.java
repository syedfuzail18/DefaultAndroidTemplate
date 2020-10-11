package com.app.mybaseapplication.networking;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created on 11-Oct-2020
 * author: Syed Fuzail
 * email: fuzail@imobisoft.co.uk
 */
public class WebServices {

    private static final String TAG = WebServices.class.getSimpleName();

    //General Variables
    private Context mContext;
    private ProgressDialog dialog;
    private APIListener mApiListener;
    public ApiService mApiService;
    private static String device_type = "android";


    /**
     * This constructor is executed before any api call
     */
    public WebServices(Context mContext, APIListener mApiListener, String msg) {

        //Initialize Context
        this.mContext = mContext;
        this.mApiListener = mApiListener;
        dialog = new ProgressDialog(mContext);
        dialog.setMessage(msg);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mApiService = RetrofitInstance.getInstance(mContext).create(ApiService.class);
    }


    public <T> void sendData(Call<T> call, final int api) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                Object mObject = response.body();
                if (response.code() == 401) {
                    mApiListener.onApiSuccess(mObject, -1);
                } else {
                    mApiListener.onApiSuccess(mObject, api);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                mApiListener.onApiFailure(t);
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });

    }

}
