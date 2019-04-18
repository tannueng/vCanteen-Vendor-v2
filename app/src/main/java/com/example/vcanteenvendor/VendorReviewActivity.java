package com.example.vcanteenvendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VendorReviewActivity extends AppCompatActivity {


    Button orderStatusButton; //ORDER STATUS
    Button menuButton; //MENU
    Button salesRecordButton; //SALES RECORD
    Button settingsButton; //SETTINGS

    Button backButton;

    ListView reviewListListView;

    TextView overallScore;


    SharedPreferences sharedPref;
    int vendor_id;

    ReviewList reviewList;
    List<Review> reviewArrayList;

    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_review);


        orderStatusButton = (Button) findViewById(R.id.orderStatusButton);
        menuButton = (Button) findViewById(R.id.menuButton);
        salesRecordButton = (Button) findViewById(R.id.salesRecordButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        backButton = (Button) findViewById(R.id.backButton);
        reviewListListView = findViewById(R.id.reviewlist);

        overallScore = findViewById(R.id.overallScore);


        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        vendor_id =  sharedPref.getInt("vendor_id", 0);



        //////////////////////////////////////////   Navigation   //////////////////////////////////////
        orderStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        loadAllReviews();

    }

    private void loadAllReviews() {

        progressDialog = new ProgressDialog(VendorReviewActivity.this);
        progressDialog = ProgressDialog.show(VendorReviewActivity.this, "",
                "Loading. Please wait...", true);

        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<ReviewList> call = jsonPlaceHolderApi.getReviews(vendor_id); //SET LOGIC TO INSERT ID HERE


        call.enqueue(new Callback<ReviewList>() {
            @Override
            public void onResponse(Call<ReviewList> call, Response<ReviewList> response) {

                if (!response.isSuccessful()) {
                    System.out.println("\n\n\n\n********************"+ "Code: " + response.code() +"********************\n\n\n\n");
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "You have 0 review.",  Toast.LENGTH_SHORT).show();
                    return;
                }


                reviewList = response.body();
                if(reviewList !=null){

                    ListAdapter reviewAdapter = new ReviewListAdapter(VendorReviewActivity.this, reviewList); //Put the arraylist here
                    reviewListListView.setAdapter(reviewAdapter);
                    overallScore.setText(String.valueOf(reviewList.getVendorScore()));

                    progressDialog.dismiss();
                }

            }

            @Override
            public void onFailure(Call<ReviewList> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n********************"+ t.getMessage() +"********************\n\n\n\n");

            }
        });

    }


    //////////////////////////////////////////   Navigation(cont.)   //////////////////////////////////////
    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToMenu(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }




}
