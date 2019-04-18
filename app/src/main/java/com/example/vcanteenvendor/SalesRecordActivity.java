package com.example.vcanteenvendor;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;*/

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;

public class SalesRecordActivity extends AppCompatActivity {

    private String url = "json url from endpoint";
    private ListView salesrecord;
    Button orderStatusButton; //ORDER STATUS
    Button menuButton; //MENU
    Button salesRecordButton; //SALES RECORD
    Button settingsButton; //SETTINGS

    TextView foodId;
    TextView foodName;
    TextView foodExtra;
    TextView foodPrice;
    TextView total_sales;
    TextView best_seller_dish;
    TextView number_sold;
    int resource;
    private SharedPreferences sharedPref;
    int vendor_id;

    TextInputEditText pinInput;
    TextView errorText;

    ListView salesRecordListListView;

    ProgressDialog progressDialog;

//    List<SalesRecord> salesRecordOrder;
  //  SalesRecord List;
    private ArrayList<SalesRecordOrder> order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_record);



        orderStatusButton = (Button) findViewById(R.id.orderStatusButton);
        menuButton = (Button) findViewById(R.id.menuButton);
        salesRecordButton = (Button) findViewById(R.id.salesRecordButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        foodId = (TextView) findViewById(R.id.orderNo);
        foodName = (TextView) findViewById(R.id.foodName);
        foodExtra = (TextView) findViewById(R.id.foodExtra);
        foodPrice = (TextView) findViewById(R.id.foodPrice);

        salesRecordListListView = (ListView) findViewById(R.id.salesRecordList);

        total_sales = (TextView) findViewById(R.id.total_sales);
        best_seller_dish = (TextView) findViewById(R.id.best_seller_dish);
        number_sold = (TextView) findViewById(R.id.number_sold);



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

        /*salesRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSalesRecord();
            }
        });*/

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });


        //////////////////////////////////////////  SalesRecordAdapter   //////////////////////////////////////

       //error List = new SalesRecordList(order);
        //getSalesRecordArrayList();


        //////////////////////////////////////////  Dialog   //////////////////////////////////////

        final Dialog dialog = new Dialog(SalesRecordActivity.this);

        dialog.setContentView(R.layout.dialog_unlock_pin);


        //dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        //dialog.getWindow().setDimAmount(20); //to mask background

        final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
        final TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
        Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
        Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);

        pinInput = dialog.findViewById(R.id.pinInput);
        errorText = dialog.findViewById(R.id.errorText);
        errorText.setText("");

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Case if blank
                if(pinInput.getText().toString().length()==0){
                    errorText.setText("Please fill in your 4-digit pin.");
                }
                // Case less than 4
                else if(pinInput.getText().toString().length()<4){
                    errorText.setText("Please fill in all 4 digits of your pin.");
                }
                // If all condition are met
                else{
                    String pin = pinInput.getText().toString();
                    url = "https://vcanteen.herokuapp.com/";
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                    Call<Void> callCheckPin = jsonPlaceHolderApi.checkPin(vendor_id, pin);

                    callCheckPin.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(!response.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "CODE: "+response.code(), Toast.LENGTH_SHORT).show();
                                if(response.code()==404){
                                    errorText.setText("Incorrect Pin");
                                }
                                return;
                            }
                            if(response.code()==200) {
                                getSalesRecordArrayList();
                                errorText.setText("");
                                Toast.makeText(getApplicationContext(), "UNLOCKED!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else if(response.code()==404){
                                errorText.setText("Incorrect Pin");
                            }

                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            System.out.println("Error verify pin");
                        }
                    });

                }

            }
        });

        dialog.show();

    }

    private void getSalesRecordArrayList() {

        progressDialog = new ProgressDialog(SalesRecordActivity.this);
        progressDialog = ProgressDialog.show(SalesRecordActivity.this, "",
                "Loading. Please wait...", true);

        String url = "https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SalesRecordApi jsonPlaceHolderApi = retrofit.create(SalesRecordApi.class);

        Call<SalesRecord> call = jsonPlaceHolderApi.getSalesRecord(1); //SET LOGIC TO INSERT ID HERE

        call.enqueue(new Callback<SalesRecord>() {
            @Override
            public void onResponse(Call<SalesRecord> call, Response<SalesRecord> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(),"cannot connect error code: "+response.code(),Toast.LENGTH_LONG).show();
                    //System.out.println("\n\n\n\n********************"+ "Code: " + response.code() +"********************\n\n\n\n");
                    return;
                }

                //put it in array list here
                //String[] name = {"Fried Chicken with Sticky RiceFried Chicken with Sticky RiceFried Chicken with Sticky Rice","Sticky RiceFried Chicken with Sticky Rice","Food3","Food4","Fried Chicken with Sticky RiceFried Chicken with Sticky RiceFried Chicken with Sticky RiceFried Chicken with Sticky Rice","Food6"};
                //String[] price = {"20.-", "30.-", "40.-", "20.-", "30.-", "50.-" };
                /*ArrayList<String> price = new ArrayList<String>();
                price.add("20.-");
                price.add("90.-");
                price.add("2045.-");
                price.add("42.-");
                price.add("18.-");
                price.add("200.-");
                price.add("30.-");
                String[] extra = {"extra1","extra2", "extra3", "extra4", "extra5","extra6"};
                ArrayList<String> name = new ArrayList<String>();
                name.add("A");
                name.add("B");
                name.add("C");
                name.add("D");
                name.add("E");
                name.add("F");
                ListAdapter testAdapter = new SalesRecordAdapter(this, food);
                ListView salesRecordList = findViewById(R.id.salesRecordList);
                salesRecordList.setAdapter(testAdapter);*/
                // Construct the data source
                // Create the adapter to convert the array to views
                //UsersAdapter adapter = new UsersAdapter(this, arrayOfUsers);
                // Attach the adapter to a ListView
                // ListView listView = (ListView) findViewById(R.id.lvItems);
                //listView.setAdapter(adapter);

                //ArrayAdapter<SalesRecordArrayList> itemsAdapter = new ArrayAdapter<String>(this, R.layout.sales_record_row, item);
                if (response.body().getTotalDailySales().getSum() == 0){
                    best_seller_dish.setText("No sales today" );
                    number_sold.setText( "0 DISHES");
                    total_sales.setText( "NO DATA");
                    ArrayList<SalesRecordOrder> temp = new ArrayList<SalesRecordOrder>();
                    SalesRecordOrder newOrderList = new SalesRecordOrder(0, "Null","Null", 0);
                    temp.add(newOrderList);
                    SalesRecordAdapter adapter = new SalesRecordAdapter(SalesRecordActivity.this, temp);
                    salesRecordListListView.setAdapter(adapter);
                    Log.d("Test if", "if");
                    progressDialog.dismiss();
                    return;
                } else {

                    String bestSellingName = response.body().getBestSeller().getOrdername();
                    int bestSellingAmount = response.body().getBestSeller().getAmount();
                    int totalDailySales = response.body().getTotalDailySales().getSum();
                    //Toast.makeText(getApplicationContext(), bestSellingName, Toast.LENGTH_LONG).show();
                    best_seller_dish.setText(bestSellingName);
                    number_sold.setText(bestSellingAmount + " DISHES");
                    total_sales.setText(totalDailySales + " ฿ ");

                    //ArrayList<SalesRecordOrder> salesRecordOrders = new ArrayList<SalesRecordOrder>();
                    // Create the adapter to convert the array to views

                    Log.w("gson response :", new Gson().toJson(response.body().getSalesRecordOrders())); //มาแล้ว เกินตรงนี้ระเบิด

                    ArrayList<SalesRecordOrder> order = response.body().getSalesRecordOrders();
                    ArrayList<SalesRecordOrder> temp = new ArrayList<SalesRecordOrder>();
                    for (int i = 0; i < order.size(); i++) {
                        int orderId = order.get(i).getOrderIdSales();
                        String orderName = order.get(i).getOrderNameSales();
                        String orderNameExtra = order.get(i).getOrderNameExtraSales();
                        int orderPrice = order.get(i).getOrderPriceSales();
                        SalesRecordOrder newOrderList = new SalesRecordOrder(orderId, orderName, orderNameExtra, orderPrice);
                        temp.add(newOrderList);
                    }


                    SalesRecordAdapter adapter = new SalesRecordAdapter(SalesRecordActivity.this, temp);

                    salesRecordListListView.setAdapter(adapter);
                    progressDialog.dismiss();
                }



                // Attach the adapter to a ListView
                //ListView listView = (ListView) findViewById(R.id.salesRecordList);
                //listView.setAdapter(adapter);



               /* for (int i=0;i<order.size() ; i++){
                    int orderId = order.get(i).getOrderId();
                    String orderName  = order.get(i).getOrderName();
                    String orderNameExtra = order.get(i).getOrderNameExtra();
                    int orderPrice =  order.get(i).getOrderPrice();*/

                    // SalesRecordOrder newOrderList = new SalesRecordOrder(orderId, orderName ,orderNameExtra ,orderPrice);
                    //adapter.add(newOrderList);
                    //itemsAdapter.add(newOrderList);
                    //Toast.makeText(getApplicationContext(), orderId+orderName+orderNameExtra+orderPrice, Toast.LENGTH_LONG).show();
               // }


            }

            @Override
            public void onFailure(Call<SalesRecord> call, Throwable t) {

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

    /*public void goToSalesRecord() {
        Intent intent = new Intent(this, SalesRecordActivity.class);
        startActivity(intent);
    }*/

    public void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    
    

}




