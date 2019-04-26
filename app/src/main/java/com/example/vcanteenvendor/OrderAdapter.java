package com.example.vcanteenvendor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;

import java.sql.SQLOutput;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class OrderAdapter extends ArrayAdapter {

    private OrderList mOrderList;
    private List<Order> mOrderArrayList;
    View customView;
    TextView idNumber;
    TextView foodname;
    TextView foodextra;
    TextView idNumber;
    Button cancelButton;
    Button doneButton;
    Button confirmButtonForCancel;
    Order singleOrder;
    ProgressDialog progressDialog;
    String inputReason;
    int mPosition;

    Dialog dialog;


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

        idNumber = (TextView) customView.findViewById(R.id.idNumber);
        foodname = (TextView) customView.findViewById(R.id.foodName);
        foodextra = (TextView) customView.findViewById(R.id.foodExtra);
        cancelButton = (Button) customView.findViewById(R.id.cancelButton);
        doneButton = (Button) customView.findViewById(R.id.doneButton);
        idNumber.setText(String.valueOf(singleOrder.getOrderId()));
        foodname.setText(singleOrder.getOrderName());
        foodextra.setText(singleOrder.getOrderNameExtra());
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.vendor_list_cancel_popup);
                TextView orderName = (TextView) dialog.findViewById(R.id.orderName);
                Button ingredientsButton = (Button) dialog.findViewById(R.id.ingredientsButton);
                ingredientsButton.setPaintFlags(ingredientsButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

                final EditText reasonBox = (EditText) dialog.findViewById(R.id.reasonBox);
                final TextView error = (TextView) dialog.findViewById(R.id.error);
                confirmButtonForCancel = (Button) dialog.findViewById(R.id.confirmButton);
                Button dismissButton = (Button) dialog.findViewById(R.id.dismissButton);
                Button closeDialog = (Button) dialog.findViewById(R.id.close_dialog);
                orderName.setText(singleOrder.getOrderName() + "?");
                dialog.show();

                reasonBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reasonBox.setCursorVisible(true);
                        System.out.println("REASONBOX TAPPED");
                    }
                });
                reasonBox.addTextChangedListener(inputTextWatcher);

                ingredientsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputReason = "Ran out of ingredients";
                        orderCancel(singleOrderId);
                        mOrderArrayList.remove(position);
                        OrderAdapter.super.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                confirmButtonForCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        inputReason = reasonBox.getText().toString().trim();
                        if(inputReason.isEmpty()) {
                            error.setText("Please type your cancellation reasons.");
                            error.setVisibility(View.VISIBLE);
                        } else {
                            orderCancel(singleOrderId);
                            mOrderArrayList.remove(position);
                            OrderAdapter.super.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });

                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.vendor_list_done_popup);
                TextView orderName = (TextView) dialog.findViewById(R.id.orderName);
                Button confirmButton = (Button) dialog.findViewById(R.id.confirmButton);
                Button dismissButton = (Button) dialog.findViewById(R.id.dismissButton);
                Button closeDialog = (Button) dialog.findViewById(R.id.close_dialog);
                orderName.setText(singleOrder.getOrderName() + "?");
                dialog.show();
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        orderDone(singleOrderId);
                        mOrderArrayList.remove(position);
                        OrderAdapter.super.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
        return customView;
    }




    private void orderCancel(int orderId) {

        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.editOrderStatus(orderId, "CANCELLED", inputReason);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {

                    System.out.println("Code: "+response.code());
                    return;
                } else {
                    System.out.println("CANCELLED SUCCEEDED");
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                System.out.println("onFailure at orderCancel " +t.getMessage());
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
        Call<Void> call = jsonPlaceHolderApi.editOrderStatus(orderId, "DONE", null);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {

                    System.out.println("Code: "+response.code());
                    return;
                } else {
                    System.out.println("DONE SUCCEEDED");
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

                System.out.println("onFailure at orderDone " + t.getMessage());
            }
        });

    }
    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            confirmButtonForCancel.setBackgroundResource(R.drawable.pink_round_btn);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
