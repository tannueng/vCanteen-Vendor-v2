package com.example.vcanteenvendor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {


    Button orderStatusButton; //ORDER STATUS
    Button menuButton; //MENU
    Button salesRecordButton; //SALES RECORD
    Button settingsButton; //SETTINGS


    Button addMenuButton; //+ ADD MENU

    RecyclerView combinationMenuRecyclerView;
    RecyclerView alacarteMenuRecyclerView;


    MenuRecyclerviewAdapter combinationMenuAdapter;
    MenuRecyclerviewAdapter alacarteMenuAdapter;

    //List<Menu> lstMenu;
    CombinationAlacarteList combinationAlacarteList;

    RequestOptions option = new RequestOptions().centerCrop();

    ProgressDialog progressDialog;

    SharedPreferences sharedPref;

    int vendor_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        orderStatusButton = (Button) findViewById(R.id.orderStatusButton);
        menuButton = (Button) findViewById(R.id.menuButton);
        salesRecordButton = (Button) findViewById(R.id.salesRecordButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);
        addMenuButton = (Button) findViewById(R.id.addMenuButton);

        combinationMenuRecyclerView = (RecyclerView) findViewById(R.id.combinationMenuRecyclerView);
        alacarteMenuRecyclerView = (RecyclerView) findViewById(R.id.alacarteMenuRecyclerView);


        //////////////////////////////////////////   Navigation   //////////////////////////////////////

        orderStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

        /*menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMenu();
            }
        });*/

        salesRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSalesRecord();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });

        addMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddEdit();
            }
        });




        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        vendor_id =  sharedPref.getInt("vendor_id", 0);
        System.out.println("LDSKFJSDKLFJLDSJFDLSK VENDORID "+vendor_id);

        menuLoadUp();


    }




    private void menuLoadUp() {

        progressDialog = new ProgressDialog(MenuActivity.this);
        progressDialog = ProgressDialog.show(MenuActivity.this, "",
                "Loading. Please wait...", true);

        String url = "https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<CombinationAlacarteList> call = jsonPlaceHolderApi.getAllMenu(vendor_id); //SET LOGIC TO INSERT ID HERE
        System.out.println("VENDOR ID : "+vendor_id);


        call.enqueue(new Callback<CombinationAlacarteList>() {
            @Override
            public void onResponse(Call<CombinationAlacarteList> call, Response<CombinationAlacarteList> response) {

                if (!response.isSuccessful()) {
                    System.out.println("\n\n\n\n********************" + "Code: " + response.code() + "********************\n\n\n\n");
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "You have 0 menu.",  Toast.LENGTH_SHORT).show();
                    return;
                }

                combinationAlacarteList = response.body();

                combinationMenuAdapter = new MenuRecyclerviewAdapter(MenuActivity.this, combinationAlacarteList.combinationList);
                combinationMenuRecyclerView.setLayoutManager(new GridLayoutManager(MenuActivity.this, 4));
                combinationMenuRecyclerView.setAdapter(combinationMenuAdapter);

                alacarteMenuAdapter = new MenuRecyclerviewAdapter(MenuActivity.this, combinationAlacarteList.alacarteList);
                alacarteMenuRecyclerView.setLayoutManager(new GridLayoutManager(MenuActivity.this, 4));
                alacarteMenuRecyclerView.setAdapter(alacarteMenuAdapter);

                progressDialog.dismiss();

                //combinationMenuAdapter.notifyDataSetChanged();
                //alacarteMenuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<CombinationAlacarteList> call, Throwable t) {
                System.out.println("\n\n\n\n********************" + t.getMessage() + "********************\n\n\n\n");

            }
        });

    }










    /*@Override
    public void onResume(){
        super.onResume();
        menuLoadUp();


    }*/

    @Override
    public void onBackPressed()
    {
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }


//////////////////////////////////////////   Navigation(cont.)   //////////////////////////////////////
public void goToMain(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        }

    /*public void goToMenu(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }*/

public void goToSalesRecord(){
        Intent intent=new Intent(this,SalesRecordActivity.class);
        startActivity(intent);
        }

public void goToSettings(){
        Intent intent=new Intent(this,SettingsActivity.class);
        startActivity(intent);
        }

////////////////////

public void goToAddEdit(){
        Intent intent=new Intent(this,AddEditMenuActivity.class);
        startActivity(intent);
        }


        }
