package com.example.vcanteenvendor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FourDigitPage extends AppCompatActivity {

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("\\d+");

    private final String url = "https://vcanteen.herokuapp.com/";
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPref;

    EditText fourDigitBox;
    EditText confirmPinBox;
    TextView pinError;
    TextView confirmPinError;
    Button nextButton;

    private String fourDigitPin; // Must Hash
    private String confirmPin;

    //Form Intent
    private String serviceProvider;
    private String accountNumber;
    private String vendorName;
    private String vendorPhoneNumber;
    private String email;
    private String acccountType;
    private String password; //Hashed or Empty(Facebook)

    private FirebaseAuth mAuth;
    private DatabaseReference dbUsers;
    private String firebaseToken;

    private int vendorId;
    private String vendorSessionToken;

    //sharedPref too

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_digit_page);

        FirebaseApp.initializeApp(FourDigitPage.this);
        mAuth = FirebaseAuth.getInstance();

        fourDigitBox = findViewById(R.id.fourDigitBox);
        confirmPinBox = findViewById(R.id.confirmPinBox);
        pinError = findViewById(R.id.pinError);
        confirmPinError = findViewById(R.id.confirmPinError);
        nextButton = findViewById(R.id.nextButton);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        email = getIntent().getStringExtra("input_email");  //Can came from either Login or Sign up, depends on account type.
        acccountType = getIntent().getStringExtra("accountType");
        password = getIntent().getStringExtra("input_password");
        vendorName = getIntent().getStringExtra("input_name");
        vendorPhoneNumber = getIntent().getStringExtra("input_phone");
        serviceProvider = getIntent().getStringExtra("input_service");
        accountNumber = getIntent().getStringExtra("input_account_no");



        System.out.println(" ==================== In FourDigitPage From intent - Email:: "+email+"====Acc::"+acccountType+"====Pass:: "+password+" ==================== ");
        System.out.println(" ==================== In FourDigitPage From intent - Name:: "+vendorName+"====Phone::"+vendorPhoneNumber+" ==================== ");
        System.out.println(" ==================== In FourDigitPage From intent - Service:: "+serviceProvider+"==== Acc No::"+accountNumber+" ==================== ");

        if (acccountType.equals("FACEBOOK")) {
            password = "firebaseOnlyNaja";
            firebaseToken = getIntent().getStringExtra("input_firebase_token");
        }

        fourDigitBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pinError.setVisibility(View.INVISIBLE);  //Hide Error
            }
        });

        confirmPinBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPinError.setVisibility(View.INVISIBLE);  //Hide Error
            }
        });

        confirmPinBox.setOnEditorActionListener(editorListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fourDigitPin = fourDigitBox.getText().toString().trim();
                confirmPin = confirmPinBox.getText().toString().trim();
                System.out.println(" ==================== Pin EditText:: "+fourDigitPin+" ==================== ");
                System.out.println(" ==================== Confirm EditText:: "+confirmPin+" ==================== ");

                if(fourDigitPin.isEmpty()){
                    pinError.setText("Please enter your 4-digit pin.");
                    pinError.setVisibility(View.VISIBLE);

                } else if(confirmPin.isEmpty()){
                    confirmPinError.setText("Please confirm your 4-digit pin.");
                    confirmPinError.setVisibility(View.VISIBLE);

                } else if(!(NUMBER_PATTERN.matcher(fourDigitPin).matches())){
                    pinError.setText("Pin can only contains 0-9.");
                    pinError.setVisibility(View.VISIBLE);

                } else if(!fourDigitPin.equals(confirmPin)){
                    confirmPinError.setText("The pin did not match. Please try again.");
                    confirmPinError.setVisibility(View.VISIBLE);

                } else if(fourDigitPin.length() != 4){
                    pinError.setText("Pin must have 4 digits");
                    pinError.setVisibility(View.VISIBLE);
                } else{
                    pinError.setVisibility(View.INVISIBLE);
                    confirmPinError.setVisibility(View.INVISIBLE);

                    System.out.println(" ==================== Plain Pin :: "+fourDigitPin+" ==================== ");
                    fourDigitPin = new String(Hex.encodeHex(DigestUtils.sha256(fourDigitPin)));
                    System.out.println(" ==================== Hashed Pin :: "+fourDigitPin+" ==================== ");

                    registerFireBase();

                }
            }
        });

    }

    private void sendToSignUp(String FireBaseToken, String accountType) {

        System.out.println(" ==================== sendEmailToSignUp :: "+email+" ==================== ");
        System.out.println(" ==================== Firebasetoken :: "+FireBaseToken+" ==================== ");
        progressDialog = new ProgressDialog(FourDigitPage.this);
        progressDialog = ProgressDialog.show(FourDigitPage.this, "",
                "Verifying. Please wait...", true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<LoginResponse> call = jsonPlaceHolderApi.signUpNewVendor(email,password,acccountType,serviceProvider,accountNumber,FireBaseToken,vendorName,vendorPhoneNumber,fourDigitPin);

        // PUT DATA FOR Email VERIFICATION
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {
                if(response.code() != 200 ){
                    // SERVER ERROR
                    if (acccountType.equals("FACEBOOK"))  sharedPref.edit().putString("token", "NO TOKEN JA EDOK").commit();

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Oops. Something went wrong :( , please try again later...", Toast.LENGTH_SHORT).show();
                    System.out.println(" ==================== Error Code :: "+response.code()+" ==================== ");
                    progressDialog.dismiss();

                }else {
                    // Code 200 = FOUND IN DB = receive Account Type
                    System.out.println(" ==================== Result Code :: "+response.code()+" ==================== ");

                    progressDialog.dismiss();
                    vendorId = response.body().getVendor_id();
                    vendorSessionToken = response.body().getToken();

                    System.out.println(" ==================== Response SignUp Vendor Id:: "+vendorId+" ======================= ");
                    System.out.println(" ==================== Response SignUp token :: "+vendorSessionToken+" ======================= ");

                    sharedPref.edit().putString("token", response.body().getToken()).commit();
                    sharedPref.edit().putInt("vendor_id", response.body().getVendor_id()).commit();
                    sharedPref.edit().putString("email", email).commit();
                    progressDialog.dismiss();

                    startActivity(new Intent(FourDigitPage.this,MainActivity.class));

                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                System.out.println("\n\n\n\n********************"+ t.getMessage() +"********************\n\n\n\n");

            }
        });

    }

    private void registerFireBase(){

        progressDialog = new ProgressDialog(FourDigitPage.this);
        progressDialog = ProgressDialog.show(FourDigitPage.this, "",
                "Loading. Please wait...", true);


        System.out.println("================== mAuth Start -- signInWithEmailAndPassword(email, passwd) ==================");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            registerFireBase2("NORMAL");

                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                progressDialog.dismiss();
                                //Toast.makeText(FourDigitPage.this, "FirebaseAuthUserCollisionException", Toast.LENGTH_LONG).show();
                                System.out.println("============== Firebase Collision because acctype is "+acccountType+"================");
                                if (acccountType.equals("FACEBOOK")){
                                    sendToSignUp(firebaseToken,"FACEBOOK");
                                }
                            } else {
                                progressDialog.dismiss();
                                //Toast.makeText(FourDigitPage.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                System.out.println("============== Firebase Check Exist Error "+task.getException().getMessage()+" ================");
                            }
                        }

                    }
                });
        /*mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(FourDigitPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("================== mAuth SUCCESS! ==================");
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            dbUsers = FirebaseDatabase.getInstance().getReference("users").child(uid);

                            dbUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot dsUser: dataSnapshot.getChildren())
                                        firebaseToken = dsUser.getValue(String.class);
                                    System.out.println("================== FireBase Token From datasnapshot: "+firebaseToken+" ==================");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    System.out.println("================== mAuth CANCELLED! ==================");
                                }
                            });

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    System.out.println("================== FIREBASE TOKEN to sharepref : "+firebaseToken+" ==================");
                                    sharedPref.edit().putString("firebaseToken", firebaseToken).commit();
                                    System.out.println("================== Begin sendJSON by NORMAL LOGIN ==================");
                                    sendToSignUp(firebaseToken);

                                }
                            }, 3500);


                        } else {
                            Toast.makeText(getApplicationContext(), "Cannot register now, please try again later...", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });*/

    }

    private void registerFireBase2(final String acccountType) {
        progressDialog = new ProgressDialog(FourDigitPage.this);
        progressDialog = ProgressDialog.show(FourDigitPage.this, "",
                "Loading. Please wait...", true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            firebaseToken = task.getResult().getToken();
                            //String email = mAuth.getCurrentUser().getEmail();
                            User user = new User("VENDOR",email, firebaseToken);

                            DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("users");

                            dbUsers.child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //Toast.makeText(FourDigitPage.this, "Firebase Auth Complete", Toast.LENGTH_LONG).show();
                                        System.out.println("============== Firebase Auth Complete================");
                                        sendToSignUp(firebaseToken, acccountType);
                                        progressDialog.dismiss();

                                    }
                                    progressDialog.dismiss();
                                }
                            });


                        } else {
                            progressDialog.dismiss();
                            //Toast.makeText(FourDigitPage.this, "Firebase Auth Fail", Toast.LENGTH_LONG).show();
                            System.out.println("============== Firebase Auth Fail ================");

                        }
                    }
                });

    }


    // Do the same things as OnClick of Next Button, but with hitting enter on soft keyboard
    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if(actionId == EditorInfo.IME_ACTION_DONE){
                fourDigitPin = fourDigitBox.getText().toString().trim();
                confirmPin = confirmPinBox.getText().toString().trim();
                System.out.println(" ==================== Pin EditText:: "+fourDigitPin+" ==================== ");
                System.out.println(" ==================== Confirm EditText:: "+confirmPin+" ==================== ");

                if(fourDigitPin.isEmpty()){
                    pinError.setText("Please enter your 4-digit pin.");
                    pinError.setVisibility(View.VISIBLE);

                } else if(confirmPin.isEmpty()){
                    confirmPinError.setText("Please confirm your 4-digit pin.");
                    confirmPinError.setVisibility(View.VISIBLE);

                } else if(!(NUMBER_PATTERN.matcher(fourDigitPin).matches())){
                    pinError.setText("Pin can only contains 0-9.");
                    pinError.setVisibility(View.VISIBLE);

                } else if(!fourDigitPin.equals(confirmPin)){
                    confirmPinError.setText("The pin did not match. Please try again.");
                    confirmPinError.setVisibility(View.VISIBLE);

                } else if(fourDigitPin.length() != 4){
                    pinError.setText("Pin must have 4 digits");
                    pinError.setVisibility(View.VISIBLE);
                } else{
                    pinError.setVisibility(View.INVISIBLE);
                    confirmPinError.setVisibility(View.INVISIBLE);

                    System.out.println(" ==================== Plain Pin :: "+fourDigitPin+" ==================== ");
                    fourDigitPin = new String(Hex.encodeHex(DigestUtils.sha256(fourDigitPin)));
                    System.out.println(" ==================== Hashed Pin :: "+fourDigitPin+" ==================== ");

                    registerFireBase();

                }
            }

            return false;
        }
    };

    public void hideKb(View view){ //For hiding soft keyboard when tap outside
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
