package com.regulationfishing.csacripante.fishingregulationsapp;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;



public class AndroidLoginController extends Application {
    private RequestQueue requestQueue;
    private static Context context;

    private static AndroidLoginController ourInstance = new AndroidLoginController();

    public static AndroidLoginController getInstance() {
        return ourInstance;
    }

    private AndroidLoginController() {
    }

    public RequestQueue getRequestQueue(){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }

}