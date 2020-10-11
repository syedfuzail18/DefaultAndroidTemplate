package com.app.mybaseapplication.networking;

import android.content.Context;

import com.app.mybaseapplication.BuildConfig;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created on 11-Oct-2020
 * author: Syed Fuzail
 * email: fuzail@imobisoft.co.uk
 */

public class RetrofitInstance {

    private static Retrofit mInstance;
    private static OkHttpClient client;

    public static Retrofit getInstance(final Context context) {

        if (mInstance == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    Request request = original.newBuilder()
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                }
            });

            client = httpClient
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .build();

            mInstance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BaseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return mInstance;
    }

}
