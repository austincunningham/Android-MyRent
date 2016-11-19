package org.wit.myrent.app;

import android.app.Application;
import android.util.Log;
import org.wit.myrent.models.Portfolio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import org.wit.myrent.retrofit.ResidenceServiceProxy;
import java.util.concurrent.TimeUnit;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;


public class MyRentApp extends Application
{
    static final String TAG = "MyRentApp";
    public Portfolio portfolio;
    public String service_url = "http://10.0.2.2:9000"; //Android Emulator
    //public String service_url = "https://myrent-service-2016.herokuapp.com/";
    public ResidenceServiceProxy residenceService;

    protected static MyRentApp app;
    @Override
    public void onCreate()
    {
        super.onCreate();
        portfolio = new Portfolio(getApplicationContext());
        Log.d(TAG, "MyRent app launched");
        app = this;

        Gson gson = new GsonBuilder().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(service_url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        residenceService = retrofit.create(ResidenceServiceProxy.class);
    }

    public static MyRentApp getApp(){
        return app;
    }
}