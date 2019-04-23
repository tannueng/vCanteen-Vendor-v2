package com.example.vcanteenvendor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.regex.Pattern;

public class FirstTimeLinkPayment extends AppCompatActivity {

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("\\d+");

    Spinner paymentSpinner;
    EditText accountNumberBox;
    TextView accountNumberError;
    Button linkButton;

    private String serviceProvider;
    private String accountNumber;

    //Form Intent
    private String vendorName;
    private String vendorPhoneNumber;
    private String email;
    private String acccountType;
    private String password; //Hashed or Empty(Facebook)
    private String firebaseToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time_link_payment);

        paymentSpinner = findViewById(R.id.paymentSpinner);
        accountNumberBox = findViewById(R.id.accountNumberBox);
        accountNumberError = findViewById(R.id.accountNumberError);
        linkButton = findViewById(R.id.linkButton);

        email = getIntent().getStringExtra("input_email");  //Can came from either Login or Sign up, depends on account type.
        acccountType = getIntent().getStringExtra("accountType");
        password = getIntent().getStringExtra("input_password");
        vendorName = getIntent().getStringExtra("input_name");
        vendorPhoneNumber = getIntent().getStringExtra("input_phone");
        firebaseToken = getIntent().getStringExtra("input_firebase_token");

        System.out.println(" ==================== In LinkPayment From intent - Email:: "+email+"====Acc::"+acccountType+"====Pass:: "+password+" ==================== ");
        System.out.println(" ==================== In LinkPayment From intent - Name:: "+vendorName+"====Phone::"+vendorPhoneNumber+" ==================== ");

        ArrayAdapter<CharSequence> serviceProviderAdapter = ArrayAdapter.createFromResource(this,
                R.array.service_provider_array, android.R.layout.simple_spinner_item);
        serviceProviderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        paymentSpinner.setAdapter(serviceProviderAdapter);

        accountNumberBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountNumberError.setVisibility(View.INVISIBLE);  //Hide Error
            }
        });

        linkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountNumber = accountNumberBox.getText().toString().trim();
                System.out.println(" ==================== AccNo EditText:: "+accountNumber+" ==================== ");
                if (accountNumber.isEmpty()){
                    accountNumberError.setText("This field cannot be blank.");
                    accountNumberError.setVisibility(View.VISIBLE);
                    return;
                } else if (!(NUMBER_PATTERN.matcher(accountNumber).matches())){
                    accountNumberError.setText("Only 0-9 is allowed.");
                    accountNumberError.setVisibility(View.VISIBLE);
                    return;
                }

                accountNumberError.setVisibility(View.INVISIBLE);

                String fromSpinner = paymentSpinner.getSelectedItem().toString().trim();
                System.out.println(" ==================== From Spinner:: "+fromSpinner+" ==================== ");

                if (fromSpinner.equals("SCB EASY")){
                    serviceProvider = "SCB_EASY";

                }else if(fromSpinner.equals("CU NEX")){
                    serviceProvider = "CU_NEX";

                }else if (fromSpinner.equals("K PLUS")){
                    serviceProvider = "K_PLUS";

                }else if (fromSpinner.equals("TRUEMONEY WALLET")){
                    serviceProvider = "TRUEMONEY_WALLET";

                }

                System.out.println(" ==================== Service Provider:: "+serviceProvider+" ==================== ");

                Intent i = new Intent(FirstTimeLinkPayment.this, FourDigitPage.class);

                i.putExtra("input_service", serviceProvider);
                i.putExtra("input_account_no",accountNumber);
                System.out.println(" ==================== Put Extra - Service :: "+serviceProvider+"==== AccNo ::"+accountNumber+" ==================== ");

                i.putExtra("input_name",vendorName);
                i.putExtra("input_phone",vendorPhoneNumber);
                i.putExtra("input_password",password);
                i.putExtra("input_email",email);
                i.putExtra("accountType",acccountType);
                i.putExtra("input_firebase_token",firebaseToken);
                startActivity(i);


            }
        });

    }

    public void hideKb(View view){ //For hiding soft keyboard when tap outside
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
