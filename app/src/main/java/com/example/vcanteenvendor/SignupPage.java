package com.example.vcanteenvendor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupPage extends AppCompatActivity {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^\\w+@\\w+\\..{2,3}(.{1,})?$");

    private static final Pattern EMAIL_CHARACTER_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.]+\\..{2,3}(.{2,3})?$");

    private final String url = "https://vcanteen.herokuapp.com/";
    private SharedPreferences sharedPref;
    private ProgressDialog progressDialog;

    EditText emailBox;
    TextView emailError;
    ImageView facebook_login_button;
    Button nextButton;

    private String email;
    private String acccountType;

    private FirebaseAuth mAuth;
    private DatabaseReference dbUsers;
    private String firebaseToken;
    private CallbackManager callbackManager;
    private String passwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_page);

        emailBox = findViewById(R.id.emailBox);
        emailError = findViewById(R.id.emailError);
        facebook_login_button = findViewById(R.id.facebook_login_button);
        nextButton = findViewById(R.id.nextButton);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        FirebaseApp.initializeApp(SignupPage.this);
        mAuth = FirebaseAuth.getInstance();


        emailBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailError.setVisibility(View.INVISIBLE);
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailBox.getText().toString().trim();
                System.out.println(" ==================== email EditText:: "+email+" ==================== ");
                if (email.isEmpty()){
                    emailError.setText("Please enter your E-mail.");
                    emailError.setVisibility(View.VISIBLE);

                }/*else if (!(EMAIL_PATTERN.matcher(email).matches())){
                    emailError.setText("Invalid E-mail. Please try again.");
                    emailError.setVisibility(View.VISIBLE);

                }*/else if (!(EMAIL_CHARACTER_PATTERN.matcher(email).matches())){
                    emailError.setText("Invalid E-mail. Please try again.");
                    emailError.setVisibility(View.VISIBLE);

                }else {
                    sendEmailToCheck(email);

                }
            }
        });

        facebook_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin();
            }
        });
    }


    private void sendEmailToCheck(final String email) {

        System.out.println(" ==================== sendEmailToCheck :: "+email+" ==================== ");
        progressDialog = new ProgressDialog(SignupPage.this);
        progressDialog = ProgressDialog.show(SignupPage.this, "",
                "Checking Email. Please wait...", true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<LoginResponse> call = jsonPlaceHolderApi.checkIfEmailExist(email);

        // PUT DATA FOR Email VERIFICATION
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {
                if(response.code() != 200 && response.code() != 404){
                    // SERVER ERROR
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Oops. Something went wrong :( , please try again later...", Toast.LENGTH_SHORT).show();
                    System.out.println(" ==================== Error Code :: "+response.code()+" ==================== ");


                }else if (response.code() == 404){
                    // Code 404 = NOT FOUND IN DB = proceed to password page
                    System.out.println(" ==================== Result Code :: "+response.code()+" ==================== ");
                    System.out.println(" ==================== Email :: "+email+" not found in DB, go sign up ==================== ");
                    progressDialog.dismiss();
                    Intent i = new Intent(SignupPage.this, SignupPasswordPage.class);
                    i.putExtra("input_email",email);
                    i.putExtra("accountType","NORMAL");
                    startActivity(i);

                }else {
                    // Code 200 = FOUND IN DB = receive Account Type
                    System.out.println(" ==================== Result Code :: "+response.code()+" ==================== ");
                    System.out.println(" ==================== Email :: "+email+" FOUND! ==================== ");

                    progressDialog.dismiss();
                    acccountType = response.body().getAccountType();

                    System.out.println(" ==================== Response Email Account Type :: "+acccountType+" ======================= ");

                    if (acccountType.equals("NORMAL")){
                        Intent i = new Intent(SignupPage.this, LoginActivity.class);
                        i.putExtra("emailFromSignUpPage",email);
                        startActivity(i);

                    } else if (acccountType.equals("FACEBOOK")){
                        emailError.setText("This email can only be logged in with Facebook.");
                        emailError.setVisibility(View.VISIBLE);
                        return;
                    }


                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                System.out.println("\n\n\n\n********************"+ t.getMessage() +"********************\n\n\n\n");

            }
        });
    }

    private void facebookLogin() {

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logOut();
        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("Success");

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");

                                            String jsonresult = String.valueOf(json);
                                            System.out.println("JSON Result" + jsonresult);

                                            email = json.optString("email");
                                            String str_id = json.optString("id");
                                            String str_firstname = json.optString("first_name");
                                            String str_lastname = json.optString("last_name");

                                            acccountType = "FACEBOOK";
                                            passwd = "firebaseOnlyNaja";

                                            firebaseCheckExist(email,passwd);

                                            /*mAuth.signInWithEmailAndPassword(email, passwd)
                                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()) {
                                                                System.out.println("SUCCESS");
                                                                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                                dbUsers = FirebaseDatabase.getInstance().getReference("users").child(uid);

                                                                dbUsers.addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        for(DataSnapshot dsUser: dataSnapshot.getChildren())
                                                                            firebaseToken = dsUser.getValue(String.class);
                                                                        System.out.println(firebaseToken);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        sharedPref.edit().putString("firebaseToken", firebaseToken).commit();
                                                                        passwd = null;
                                                                        sendJSONFacebook(email, firebaseToken);

                                                                    }
                                                                }, 3500);


                                                            } else {
                                                                errorMessage.setText("Email or Password is Incorrect");
                                                                errorMessage.setVisibility(View.VISIBLE);
                                                                System.out.println("FIREBASE LOGIN FAIL");
                                                            }
                                                        }
                                                    });*/


                                        }

                                    }

                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name, last_name, email,link, picture.type(large)");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
    }


    private void firebaseCheckExist(final String email, final String password){
        progressDialog = new ProgressDialog(SignupPage.this);
        progressDialog = ProgressDialog.show(SignupPage.this, "",
                "Loading. Please wait...", true);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            registerToFirebase(email);
                            System.out.println("========= Email not found in Firebase, register new to firebase::::"+email+"===Pass::: "+password);

                        } else {
                            progressDialog.dismiss();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                System.out.println("========= Email FOUND in Firebase, login to firebase to get token::::"+email+"===Pass::: "+password);
                                firebaseLogin(email,password);

                            } else {
                                Toast.makeText(SignupPage.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

    }


    private void registerToFirebase(final String email){
        progressDialog = new ProgressDialog(SignupPage.this);
        progressDialog = ProgressDialog.show(SignupPage.this, "",
                "Loading. Please wait...", true);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            firebaseToken = task.getResult().getToken();
                            String emailFromFirebase = mAuth.getCurrentUser().getEmail();
                            User user = new User("VENDOR",email, firebaseToken);

                            DatabaseReference dbUsers = FirebaseDatabase.getInstance().getReference("users");

                            dbUsers.child(mAuth.getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupPage.this, "Firebase Auth Complete", Toast.LENGTH_LONG).show();
                                        sendJSONFacebook(email, firebaseToken);
                                        progressDialog.dismiss();

                                    }
                                    progressDialog.dismiss();
                                }
                            });


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignupPage.this, "Firebase Auth Fail", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }

    private void firebaseLogin(final String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupPage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("firebaseLogin SUCCESS");
                            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            dbUsers = FirebaseDatabase.getInstance().getReference("users").child(uid);

                            dbUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot dsUser: dataSnapshot.getChildren())
                                        firebaseToken = dsUser.getValue(String.class);
                                    System.out.println("==============firebaseLogin receive firebase token"+firebaseToken);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sharedPref.edit().putString("firebaseToken", firebaseToken).commit();
                                    passwd = null;
                                    sendJSONFacebook(email, firebaseToken);

                                }
                            }, 3500);


                        } else {
                            emailError.setText("Email or Password is Incorrect");
                            emailError.setVisibility(View.VISIBLE);
                            System.out.println("FIREBASE LOGIN FAIL");
                        }
                    }
                });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void sendJSONFacebook(final String email, String firebaseToken){

        progressDialog = new ProgressDialog(SignupPage.this);
        progressDialog = ProgressDialog.show(SignupPage.this, "",
                "Checking Email. Please wait...", true);

        LoginVendor loginVendor = new LoginVendor(email, firebaseToken);
        System.out.println("======================sent FACEBOOK loginVendor to string::: "+loginVendor.toString() + " ==sendLoginFacebook=============");

        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<LoginResponse> call = jsonPlaceHolderApi.sendLoginFacebook(email, firebaseToken);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                System.out.println("==================Code :::: "+response.code()+" ==sendLoginFacebook()================");

                if (response.code() != 200 && response.code() != 404 ) {
                    // Error
                    /*if (acccountType.equals("FACEBOOK"))
                        sharedPref.edit().putString("token", "NO TOKEN JA EDOK").commit();*/
                    sharedPref.edit().putString("token", "NO TOKEN JA EDOK").commit();
                    progressDialog.dismiss();
                    emailError.setText("Email or Password is Incorrect");
                    emailError.setVisibility(View.VISIBLE);
                    LoginManager.getInstance().logOut();
                    if (acccountType.equals("FACEBOOK"))
                        LoginManager.getInstance().logOut();
                    progressDialog.dismiss();
                } else if(response.code() == 404){
                    //Email not found in DB, go to sign up
                    System.out.println("================== FACEBOOK EMAIL NOT IN DB, GO SIGN UP ================");
                    Intent i = new Intent(SignupPage.this,SignupInfoPage.class);
                    // passing data to the next activity
                    i.putExtra("input_email",email);
                    i.putExtra("accountType","FACEBOOK");
                    startActivity(i);

                /*} else if(response.code()==409) {
                    progressDialog.dismiss();
                    errorMessage.setText("This account can only be logged into with Facebook");
                    errorMessage.setVisibility(View.VISIBLE);*/

                } else {
                    // Success
                    System.out.println("================== sendLoginFacebook() = SUCCESS! ================");
                    String type = response.body().getAccountType();

                    if(type.equals("FACEBOOK")){
                        // save vendor_id, token
                        sharedPref.edit().putString("token", response.body().getToken()).commit();
                        sharedPref.edit().putInt("vendor_id", response.body().getVendor_id()).commit();
                        sharedPref.edit().putString("email", email).commit();
                        //sharedPref.edit().putString("account_type", account_type).commit();

                        System.out.println("==================Account Type :::: "+response.body().getAccountType()+" ==================");
                        System.out.println("==================VENDOR ID :::: "+response.body().getVendor_id()+" ==================");
                        System.out.println("==================JWT Token :::: "+response.body().getToken()+" ==================");
                        progressDialog.dismiss();
                        startActivity(new Intent(SignupPage.this, MainActivity.class));


                    }else{

                        System.out.println("==================Account Type :::: "+response.body().getAccountType()+" ==================");
                        System.out.println("==================VENDOR ID :::: "+response.body().getVendor_id()+" ==================");
                        System.out.println("==================JWT Token :::: "+response.body().getToken()+" ==================");
                        progressDialog.dismiss();
                        //emailBox.setText(email);
                        Intent i = new Intent(SignupPage.this, LoginActivity.class);
                        i.putExtra("emailFromSignUpPage",email);
                        startActivity(i);

                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                System.out.println("==================send Login Fail :::: "+t.getMessage()+" ==================");

            }
        });

    }

    public void hideKb(View view){ //For hiding soft keyboard when tap outside
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
}
