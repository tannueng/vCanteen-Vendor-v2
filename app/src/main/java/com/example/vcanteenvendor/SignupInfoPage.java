package com.example.vcanteenvendor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Pattern;

public class SignupInfoPage extends AppCompatActivity {

    private static final Pattern VENDOR_NAME_PATTERN =
            Pattern.compile("[a-zA-Z][a-zA-Z ]+[a-zA-Z]$");

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("\\d+");

    EditText restaurantBox;
    EditText phoneNumberBox;
    TextView restaurantError;
    TextView phoneNumberError;
    Button nextButton;

    private String vendorName;
    private String vendorPhoneNumber;

    //From Intent
    private String email;
    private String acccountType;
    private String password; //Hashed or Empty(Facebook)
    private String firebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_info_page);

        restaurantBox = findViewById(R.id.restaurantBox);
        phoneNumberBox = findViewById(R.id.phoneNumberBox);
        restaurantError = findViewById(R.id.restaurantError);
        phoneNumberError = findViewById(R.id.phoneNumberError);
        nextButton = findViewById(R.id.nextButton);

        email = getIntent().getStringExtra("input_email");  //Can came from either Login or Sign up, depends on account type.
        acccountType = getIntent().getStringExtra("accountType");
        password = getIntent().getStringExtra("input_password");
        firebaseToken = getIntent().getStringExtra("input_firebase_token");

        System.out.println(" ==================== In SignupInfoPage From intent - Email:: "+email+"====Acc::"+acccountType+"====Pass:: "+password+" ==================== ");


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(restaurantBox.getText().toString().trim().isEmpty()){
                    restaurantError.setText("Please enter your restaurant name.");
                    restaurantError.setVisibility(View.VISIBLE);
                    phoneNumberError.setVisibility(View.INVISIBLE);

                } else if(!(VENDOR_NAME_PATTERN.matcher(restaurantBox.getText().toString().trim()).matches())){
                    restaurantError.setText("Name must contain only a-z or A-Z.");
                    restaurantError.setVisibility(View.VISIBLE);
                    phoneNumberError.setVisibility(View.INVISIBLE);

                } else if(phoneNumberBox.getText().toString().trim().isEmpty()){
                    phoneNumberError.setText("Please enter your phone number.");
                    phoneNumberError.setVisibility(View.VISIBLE);
                    restaurantError.setVisibility(View.INVISIBLE);

                }else if (!(NUMBER_PATTERN.matcher(phoneNumberBox.getText().toString().trim()).matches())){
                    phoneNumberError.setText("Phone number can only contain 0-9.");
                    phoneNumberError.setVisibility(View.VISIBLE);
                    restaurantError.setVisibility(View.INVISIBLE);

                } else if( phoneNumberBox.length() != 10){
                    phoneNumberError.setText("Phone number is too short.");
                    phoneNumberError.setVisibility(View.VISIBLE);
                    restaurantError.setVisibility(View.INVISIBLE);

                } /*else if(phoneNumberBox.length() != 9 ){
                    phoneNumberError.setText("Phone number is too short.");
                    phoneNumberError.setVisibility(View.VISIBLE);
                    restaurantError.setVisibility(View.INVISIBLE);

                }*/ else {
                    vendorName = restaurantBox.getText().toString().trim();
                    vendorPhoneNumber = phoneNumberBox.getText().toString().trim();

                    Intent i = new Intent(SignupInfoPage.this,FirstTimeLinkPayment.class);
                    i.putExtra("input_name",vendorName);
                    i.putExtra("input_phone",vendorPhoneNumber);
                    System.out.println(" ==================== Put Extra - Name:: "+email+"====Phone::"+acccountType+" ==================== ");

                    i.putExtra("input_password",password);
                    i.putExtra("input_email",email);
                    i.putExtra("accountType",acccountType);
                    i.putExtra("input_firebase_token",firebaseToken);
                    startActivity(i);

                }

            }
        });

    }

    public void hideKb(View view){ //For hiding soft keyboard when tap outside
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
