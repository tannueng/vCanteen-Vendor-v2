package com.example.vcanteenvendor;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Splash extends AppCompatActivity {


    private SharedPreferences sharedPref;
    private final String url = "https://vcanteen.herokuapp.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        System.out.println("CURRENT VENDOR TOKEN ========== "+sharedPref.getString("token", "empty token"));
        System.out.println("CURRENT VENDOR TOKEN EMAIL ========== "+sharedPref.getString("email", "empty email"));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        final boolean first = sharedPref.getBoolean("firstRun", true);


        final Handler handlerFirst = new Handler();
        handlerFirst.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (first){
                    startTutorial();
                    sharedPref.edit().putBoolean("firstRun", false).commit();
                } else {
                    Token token = new Token(sharedPref.getString("email", "empty email"), sharedPref.getString("token", "empty token"));
                    Call<TokenVerification> call = jsonPlaceHolderApi.verifyToken(token);

                    // POST DATA FOR TOKEN VERIFICATION
                    call.enqueue(new Callback<TokenVerification>() {
                        @Override
                        public void onResponse(Call<TokenVerification> call, final Response<TokenVerification> response) {
                            if(!response.isSuccessful())
                                Toast.makeText(getApplicationContext(), "Error Occured, please try again.", Toast.LENGTH_SHORT);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // yourMethod();
                                    boolean isExpired = response.body().isExpired();
                                    if(isExpired)
                                        startActivity(new Intent(Splash.this, LoginActivity.class));
                                    else
                                        startActivity(new Intent(Splash.this, MainActivity.class));
                                }
                            }, 1000);

                        }

                        @Override
                        public void onFailure(Call<TokenVerification> call, Throwable t) {

                        }
                    });
                }

            }
        },3000);
        Log.e("myTag", "firstrun");


    }

    public void startTutorial(){
        Intent intent = new Intent(this, TutorialPageOne.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
