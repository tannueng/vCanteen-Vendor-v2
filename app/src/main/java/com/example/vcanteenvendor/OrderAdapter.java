package com.example.vcanteenvendor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderAdapter extends ArrayAdapter {

    private OrderList mOrderList;
    private List<Order> mOrderArrayList;
    View customView;
    TextView foodname;
    TextView foodextra;
    Button cancelButton;
    Button doneButton;
    Order singleOrder;
    ProgressDialog progressDialog;

    int mPosition;




    OrderAdapter(Context context, OrderList List){
        super(context, R.layout.order_row_relative , List.orderList);
        mOrderList=List;
        mOrderArrayList = List.orderList;

    }

    /*OrderAdapter(Context context, String[] orderList){

        super(context, R.layout.order_row_relative , orderList);
    }*/

    @Override

    public View getView(final int position, View convertView, ViewGroup parent){

        LayoutInflater orderInflater = LayoutInflater.from(getContext());
        customView = orderInflater.inflate(R.layout.order_row_relative, parent, false);



        singleOrder = (Order) getItem(position);

        final int singleOrderId = singleOrder.getOrderId();

        foodname = (TextView) customView.findViewById(R.id.foodName);
        foodextra = (TextView) customView.findViewById(R.id.foodExtra);
        cancelButton = (Button) customView.findViewById(R.id.cancelButton);
        doneButton = (Button) customView.findViewById(R.id.doneButton);



        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(getContext());
                //dialog.setTitle("Devahoy");
                dialog.setContentView(R.layout.dialog_cancel_order);

                final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
                //final TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
                Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
                Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);
                final CheckBox dialogCheckbox = (CheckBox) dialog.findViewById(R.id.dialogCheckbox);
                final EditText reasonInput = (EditText) dialog.findViewById(R.id.reasonInput);


                positiveButton.setText("CONFIRM");
                //negativeButton.setVisibility(View.GONE);






                dialogCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(dialogCheckbox.isChecked()){
                            dialogCheckbox.setBackgroundResource(R.drawable.button_gradient_rounded);
                            dialogCheckbox.setTextColor(Color.WHITE);
                        }
                        if(!dialogCheckbox.isChecked()){
                            dialogCheckbox.setBackgroundResource(R.drawable.button_gradient_rounded_unchecked);
                            dialogCheckbox.setTextColor(Color.BLACK);
                        }

                    }
                });
                
                reasonInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if(hasFocus & dialogCheckbox.isChecked()){

                            dialogCheckbox.setBackgroundResource(R.drawable.button_gradient_rounded_unchecked);
                            dialogCheckbox.setTextColor(Color.BLACK);
                            dialogCheckbox.setChecked(false);
                        }
                    }
                });

                /*System.out.println(dialogCheckbox.isChecked());
                if (dialogCheckbox.isChecked()) {

                    dialogCheckbox.setBackgroundResource(R.drawable.button_gradient_rounded_unchecked);
                    dialogCheckbox.setTextColor(Color.BLACK);
                    dialogCheckbox.setChecked(false);
                }*/


                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        orderCancel(singleOrderId);
                        //another put
                        mOrderArrayList.remove(position);
                        OrderAdapter.super.notifyDataSetChanged();


                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderArrayList.remove(position);
                OrderAdapter.super.notifyDataSetChanged();

                orderDone(singleOrderId);
                //sent put here
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



        foodname.setText(singleOrder.getOrderName());
        foodextra.setText(singleOrder.getOrderNameExtra());
        return customView;
    }




    private void orderCancel(int orderId) {

        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.editOrderStatus(orderId, "CANCELLED");

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {

                    System.out.println("---------------**********---------------"+"Code: "+response.code()+"---------------**********---------------");
                    return;
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                System.out.println("---------------**********---------------"+t.getMessage()+"---------------**********---------------");
            }
        });

    }




    private void orderDone(int orderId) {

        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.editOrderStatus(orderId, "DONE");

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {

                    System.out.println("---------------**********---------------"+"Code: "+response.code()+"---------------**********---------------");
                    return;
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                System.out.println("---------------**********---------------"+t.getMessage()+"---------------**********---------------");
            }
        });

    }


}
