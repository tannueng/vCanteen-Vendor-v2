package com.example.vcanteenvendor;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class MainActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1234;
    private long backPressedTime;
    private Toast backToast;


    Button orderStatusButton; //ORDER STATUS
    Button menuButton; //MENU
    Button salesRecordButton; //SALES RECORD
    Button settingsButton; //SETTINGS

    Button doneButton;
    Button cancelButton;
    TextView orderId;
    TextView foodName;
    TextView foodExtra;

    Button refreshBtn;

    ListView orderListListView;

    OrderList List;
    List<Order> orderList;
    private ProgressDialog progressDialog;
    Order orderMain;

    SharedPreferences sharedPref;

    int vendor_id;
    int orderId1;
    int orderId2;

    Handler handler;

    String url = "https://vcanteen.herokuapp.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        orderStatusButton = (Button) findViewById(R.id.orderStatusButton);
        menuButton = (Button) findViewById(R.id.menuButton);
        salesRecordButton = (Button) findViewById(R.id.salesRecordButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        doneButton = (Button) findViewById(R.id.doneButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        orderId = (TextView) findViewById(R.id.orderId);
        foodName = (TextView) findViewById(R.id.foodName);
        foodExtra = (TextView) findViewById(R.id.foodExtra);

        orderListListView = findViewById(R.id.orderlist);
        refreshBtn = findViewById(R.id.refreshBtn);


        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        vendor_id = sharedPref.getInt("vendor_id", 0);


        //////////////////////////////////////////   Navigation   //////////////////////////////////////

        /*orderStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });*/

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                goToMenu();
            }
        });

        salesRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                goToSalesRecord();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                goToSettings();
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacksAndMessages(null);
                refreshOrder();
            }
        });

        /*doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            /*Toast.makeText(MainActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();*/
        } else {
            requestStoragePermission();
        }



        //////////////////////////////////////////   Order Adapter   //////////////////////////////////////


        List = new OrderList(orderList);



        /*ListAdapter testAdapter = new OrderAdapter(this, List); //Put the arraylist here
        orderListListView.setAdapter(testAdapter);*/


    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onPause() {
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    @Override
    protected void onResume() {
        orderLoadUp(); //GET DATA FROM JSON
        realTimeOrder();
        super.onResume();
    }

    //FIRST LOAD UP
    private void orderLoadUp() {

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog = ProgressDialog.show(MainActivity.this, "",
                "Loading. Please wait...", true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<OrderList> call = jsonPlaceHolderApi.getOrder(vendor_id); //SET LOGIC TO INSERT ID HERE


        call.enqueue(new Callback<OrderList>() {
            @Override
            public void onResponse(Call<OrderList> call, Response<OrderList> response) {

                if (!response.isSuccessful()) {
                    System.out.println("\n\n\n\n********************" + "Code: " + response.code() + "********************\n\n\n\n");
                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "You have 0 order.", Toast.LENGTH_SHORT).show();
                    orderListListView.setAdapter(null);
                    return;
                }

                List = response.body();

                orderId1 = List.orderList.get(List.getOrderList().size()-1).getOrderId();
//                System.out.println(orderId1);
                Log.i("checkID", "orderID1 = " + orderId1);

                if (List != null) {
                    ListAdapter testAdapter = new OrderAdapter(MainActivity.this, List); //Put the arraylist here
                    orderListListView.setAdapter(testAdapter);
                    progressDialog.dismiss();

                }

            }

            @Override
            public void onFailure(Call<OrderList> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n********************" + t.getMessage() + "********************\n\n\n\n");
                progressDialog.dismiss();
            }
        });

    }


    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast.cancel();
            //super.onBackPressed();
            handler.removeCallbacksAndMessages(null);

            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory( Intent.CATEGORY_HOME );
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);


            return;
        } else {
            backToast = Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT);
            backToast.show();

        }

        backPressedTime = System.currentTimeMillis();
        /*new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Closing Activity")
                .setMessage("Are you sure you want to close this activity?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();*/
    }


    //////////////////////////////////////////   Navigation(cont.)   //////////////////////////////////////
    /*public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }*/

    public void goToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void goToSalesRecord() {
        Intent intent = new Intent(this, SalesRecordActivity.class);
        startActivity(intent);
    }

    public void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void refreshOrder() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog = ProgressDialog.show(MainActivity.this, "",
                "Loading. Please wait...", true);


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<OrderList> call = jsonPlaceHolderApi.getOrder(vendor_id); //SET LOGIC TO INSERT ID HERE


        call.enqueue(new Callback<OrderList>() {
            @Override
            public void onResponse(Call<OrderList> call, Response<OrderList> response) {

                if (!response.isSuccessful()) {
                    System.out.println("\n\n\n\n********************" + "Code: " + response.code() + "********************\n\n\n\n");
                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "You have 0 order.", Toast.LENGTH_SHORT).show();
                    orderListListView.setAdapter(null);
                    return;
                }

                List = response.body();

                if (List != null) {
                    orderId2 = List.orderList.get(List.getOrderList().size() - 1).getOrderId();
                    Log.i("checkID", "orderID2 = " + orderId2);
                    ListAdapter testAdapter = new OrderAdapter(MainActivity.this, List); //Put the arraylist here
                    orderListListView.setAdapter(testAdapter);
                    progressDialog.dismiss();
                    if (orderId2 > orderId1) {
                        orderId1 = orderId2;
                        Toast.makeText(getApplicationContext(), "New Orders Have Arrived", Toast.LENGTH_SHORT).show();
                        Log.i("checkID", "updatedOrderID1 = " + orderId1);
                        return;
                    }

                }

            }

            @Override
            public void onFailure(Call<OrderList> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n********************" + t.getMessage() + "********************\n\n\n\n");
                progressDialog.dismiss();
            }
        });
    }


    public void realTimeOrder(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        final Call<OrderList> call = jsonPlaceHolderApi.getOrder(vendor_id); //SET LOGIC TO INSERT ID HERE


        handler = new Handler();
        final int delay = 10000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                call.clone().enqueue(new Callback<OrderList>() {
                    @Override
                    public void onResponse(Call<OrderList> call, Response<OrderList> response) {
//                        int orderId1 = (List.getOrderList().get(List.getOrderList().size())).getOrderId();
//                        Log.e("order", String.valueOf((List.getOrderList().get(List.getOrderList().size())).getOrderId()));
                        Log.e("realtime", "run pass this already");
                        if (!response.isSuccessful()) {
                            System.out.println("\n\n\n\n********************"+ "Code: " + response.code() +"********************\n\n\n\n");
//                            Toast.makeText(getApplicationContext(), "You have 0 order.",  Toast.LENGTH_SHORT).show();
                            orderListListView.setAdapter(null);
                            return;
                        }

                        List = response.body();
                        if(List !=null){
                            orderId2 = List.orderList.get(List.getOrderList().size()-1).getOrderId();
                            ListAdapter testAdapter = new OrderAdapter(MainActivity.this, List); //Put the arraylist here
                            orderListListView.setAdapter(testAdapter);
                            progressDialog.dismiss();
                            if (orderId2 > orderId1) {
                                orderId1 = orderId2;
                                Toast.makeText(getApplicationContext(), "New Orders Have Arrived", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<OrderList> call, Throwable t) {
                        //vendorProfile.setText(t.getMessage());
                        System.out.println("\n\n\n\n********************"+ t.getMessage() +"********************\n\n\n\n");
                    }
                });
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

}
