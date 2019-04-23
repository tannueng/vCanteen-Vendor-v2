package com.example.vcanteenvendor;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class LoginActivity extends AppCompatActivity /*implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener*/{

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.]+\\..{2,3}(.{2,3})?$");
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^[a-zA-Z0-9@!#$%^&+-=](?=\\S+$).{7,}$");

    private EditText emailField;
    private EditText passField;
    private Button loginBtn;
    private Button fbBtn;
    private Button forgotPass;
    private TextView errorMessage;
    private Button showPass;
    private CallbackManager callbackManager;

    private Button signup_button;

    private Dialog recoverPassDialog;
    private Button sendRecoverBtn;
    private TextView backRecoverBtn;
    private EditText emailRecoverField;
    private TextView errorEmailRecover;
    private TextView errorEmpty;

    private Dialog confirmRecoverPass;
    private Button cancelConfirmRecover;
    private Button sendRecoverPass;

    private String email;
    private String passwd;
    private String account_type;

    private SharedPreferences sharedPref;
    private ProgressDialog progressDialog;

    private final String URL = "https://vcanteen.herokuapp.com/";
    private boolean isHidden = true;

    private FirebaseAuth mAuth;
    private DatabaseReference dbUsers;
    private String firebaseToken;

    private String emailFromSignUpPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LinearLayout layout = (LinearLayout) findViewById(R.id.linearParent);
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                hideKeyboard(view);
                return false;
            }
        });


        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);

        emailField = findViewById(R.id.email_field);
        passField = findViewById(R.id.password_field);
        loginBtn = findViewById(R.id.login_button);
        fbBtn = findViewById(R.id.facebook_button);
        forgotPass = findViewById(R.id.forgot_pw_button);
        errorMessage = findViewById(R.id.errorLogin);
        showPass = findViewById(R.id.show_pw_btn);
        signup_button = findViewById(R.id.signup_button);

        emailFromSignUpPage = getIntent().getStringExtra("emailFromSignUpPage");
        System.out.println("================== Email Form Sign Up = "+emailFromSignUpPage+" =================");
        if (emailFromSignUpPage!=null && !emailFromSignUpPage.equals("") && emailFromSignUpPage.contains("@")){
            emailField.setText(emailFromSignUpPage);
        }


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("================== login button pressed! ==================");
                if (emailField.getText().toString().trim().equals("") && passField.getText().toString().trim().equals("")) {
                    errorMessage.setText("Please fill both Email and Password.");
                    errorMessage.setVisibility(View.VISIBLE);
                    return;
                } else if (!(EMAIL_PATTERN.matcher(emailField.getText().toString().trim()).matches())){
                    errorMessage.setText("Invalid E-mail. Please try again.");
                    errorMessage.setVisibility(View.VISIBLE);
                    return;

                }

                email = emailField.getText().toString();
                passwd = passField.getText().toString();

                System.out.println("==================Login Email :::: "+email+" ==================");
                System.out.println("==================Login Plain Pass :::: "+passwd+" ==================");

//                passwd = DigestUtils.sha256Hex(passwd);
                passwd = new String(Hex.encodeHex(DigestUtils.sha256(passwd)));
                account_type = "NORMAL";


                System.out.println("==================Login Hash Pass :::: "+passwd+" ==================");

                progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog = ProgressDialog.show(LoginActivity.this, "",
                        "Loading. Please wait...", true);


                System.out.println("================== mAuth Start -- signInWithEmailAndPassword(email, passwd) ==================");

                mAuth.signInWithEmailAndPassword(email, passwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
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
                                            sendJSON(email, passwd, firebaseToken);

                                        }
                                    }, 3500);


                                } else {
                                    errorMessage.setText("Email or Password is Incorrect");
                                    errorMessage.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                }
                            }
                        });
            }
        });

        fbBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fblogin();
            }
        });

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupPage.class));
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassDialog = new Dialog(LoginActivity.this);
                recoverPassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                recoverPassDialog.setContentView(R.layout.dialog_forgot_pass);

                sendRecoverBtn = recoverPassDialog.findViewById(R.id.send_recover_btn);
                backRecoverBtn = recoverPassDialog.findViewById(R.id.back_recover);
                emailRecoverField = recoverPassDialog.findViewById(R.id.recover_email);
                errorEmailRecover = recoverPassDialog.findViewById(R.id.error_recover);
                errorEmpty = recoverPassDialog.findViewById(R.id.error_email_notfound);

                sendRecoverBtn.setEnabled(true);
                backRecoverBtn.setEnabled(true);
                emailRecoverField.setEnabled(true);

                emailRecoverField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.toString().length() > 0)
                            errorEmpty.setVisibility(View.GONE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                sendRecoverBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (emailRecoverField.getText().toString().equals("")) {
                            errorEmpty.setVisibility(View.VISIBLE);
                            return;
                        }
                        confirmRecoverPass = new Dialog(LoginActivity.this);
                        confirmRecoverPass.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        confirmRecoverPass.setContentView(R.layout.dialog_confirm_forgot_pass);

                        cancelConfirmRecover = confirmRecoverPass.findViewById(R.id.confirm_cancel_recover);
                        sendRecoverPass = confirmRecoverPass.findViewById(R.id.confirm_recover_pass);

                        cancelConfirmRecover.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmRecoverPass.dismiss();
                            }
                        });

                        sendRecoverPass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                progressDialog = new ProgressDialog(LoginActivity.this);
                                progressDialog = ProgressDialog.show(LoginActivity.this, "",
                                        "Loading. Please wait...", true);
                                // HTTP PUT email
                                String emailSent = emailRecoverField.getText().toString();
                                Gson gson = new GsonBuilder().serializeNulls().create();
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(URL)
                                        .addConverterFactory(GsonConverterFactory.create(gson))
                                        .build();
                                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                                RecoverPass postData = new RecoverPass(emailSent);
                                System.out.println(postData.toString());
                                System.out.println(URL);
                                Call<Void> call = jsonPlaceHolderApi.recoverPass(postData);
                                System.out.println(jsonPlaceHolderApi.recoverPass(postData).toString());
                                System.out.println(call.request().body().toString()
                                );
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        System.out.println(response.code());
                                        if (response.code() != 200) {
                                            // Error
                                            progressDialog.dismiss();
                                            confirmRecoverPass.dismiss();
                                            errorEmailRecover.setVisibility(View.VISIBLE);
                                        } else {
                                            // Success
                                            Toast.makeText(getApplicationContext(), "A new Password has been sent to your Email.", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            confirmRecoverPass.dismiss();
                                            recoverPassDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {

                                    }
                                });


                            }
                        });

                        confirmRecoverPass.show();
                    }


                });

                backRecoverBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recoverPassDialog.dismiss();
                    }
                });

                recoverPassDialog.show();
            }
        });

        showPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isHidden) {
                    passField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    isHidden = false;
                } else {
                    passField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    isHidden = true;
                }
            }
        });



        /*emailField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        passField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });*/
        closeKeyboard();




    }



    private void Fblogin() {
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

                                            account_type = "FACEBOOK";
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
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog = ProgressDialog.show(LoginActivity.this, "",
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
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

    }


    private void registerToFirebase(final String email){
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog = ProgressDialog.show(LoginActivity.this, "",
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
                                        Toast.makeText(LoginActivity.this, "Firebase Auth Complete", Toast.LENGTH_LONG).show();
                                        sendJSONFacebook(email, firebaseToken);
                                        progressDialog.dismiss();

                                    }
                                    progressDialog.dismiss();
                                }
                            });


                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Firebase Auth Fail", Toast.LENGTH_LONG).show();

                        }
                    }
                });

    }

    private void firebaseLogin(final String email, String password){

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
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
                            errorMessage.setText("Email or Password is Incorrect");
                            errorMessage.setVisibility(View.VISIBLE);
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

    private void sendJSON(final String email, String pass, String firebaseToken) {

//        LoginVendor loginVendor = new LoginVendor(email, passwd, firebaseToken, account_type);      //V1

        LoginVendor loginVendor = new LoginVendor(email, passwd, firebaseToken);
        System.out.println("======================sent loginVendor to string::: "+loginVendor.toString() + " ==sendLogin=============");

        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

//        Call<LoginResponse> call = jsonPlaceHolderApi.sendLogin(loginVendor);
        Call<LoginResponse> call = jsonPlaceHolderApi.sendLoginV2(email, passwd, firebaseToken);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                System.out.println("==================Code :::: "+response.code()+" ==sendLogin()================");

                if (response.code() != 200 ) {
                    // Error
                    /*if (account_type.equals("FACEBOOK"))
                        sharedPref.edit().putString("token", "NO TOKEN JA EDOK").commit();*/
                    progressDialog.dismiss();
                    errorMessage.setText("Email or Password is Incorrect");
                    errorMessage.setVisibility(View.VISIBLE);
                    /*if (account_type.equals("FACEBOOK"))
                        LoginManager.getInstance().logOut();
                    progressDialog.dismiss();*/

                /*} else if(response.code()==409) {
                    progressDialog.dismiss();
                    errorMessage.setText("This account can only be logged into with Facebook");
                    errorMessage.setVisibility(View.VISIBLE);*/

                } else {
                    // Success

                    // save vendor_id, token
                    sharedPref.edit().putString("token", response.body().getToken()).commit();
                    sharedPref.edit().putInt("vendor_id", response.body().getVendor_id()).commit();
                    sharedPref.edit().putString("email", email).commit();
                    //sharedPref.edit().putString("account_type", account_type).commit();

                    System.out.println("==================VENDOR ID :::: "+response.body().getVendor_id()+" ==================");
                    System.out.println("==================JWT Token :::: "+response.body().getToken()+" ==================");
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("==================send Login Fail :::: "+t.getMessage()+" ==================");

            }
        });

        /*ImageView chefpic = findViewById(R.id.chefpic);
        RequestOptions option = new RequestOptions().centerCrop();
        Glide.with(LoginActivity.this).load(R.drawable.hero_image).apply(option).into(chefpic);*/
    }




    private void sendJSONFacebook(final String email, final String firebaseToken){
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog = ProgressDialog.show(LoginActivity.this, "",
                "Loading. Please wait...", true);

        LoginVendor loginVendor = new LoginVendor(email, firebaseToken);
        System.out.println("======================sent FACEBOOK loginVendor to string::: "+loginVendor.toString() + " ==sendLoginFacebook=============");

        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
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
                    /*if (account_type.equals("FACEBOOK"))
                        sharedPref.edit().putString("token", "NO TOKEN JA EDOK").commit();*/
                    sharedPref.edit().putString("token", "NO TOKEN JA EDOK").commit();
                    progressDialog.dismiss();
                    errorMessage.setText("Email or Password is Incorrect");
                    errorMessage.setVisibility(View.VISIBLE);
                    LoginManager.getInstance().logOut();
                    /*if (account_type.equals("FACEBOOK"))
                        LoginManager.getInstance().logOut();
                    progressDialog.dismiss();*/
                } else if(response.code() == 404){
                    //Email not found in DB, go to sign up
                    System.out.println("================== FACEBOOK EMAIL NOT IN DB, GO SIGN UP ================");
                    progressDialog.dismiss();
                    Intent i = new Intent(LoginActivity.this,SignupInfoPage.class);
                    // passing data to the next activity
                    i.putExtra("input_email",email);
                    i.putExtra("accountType","FACEBOOK");
                    i.putExtra("input_firebase_token",firebaseToken);
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
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));


                    }else{

                        System.out.println("==================Account Type :::: "+response.body().getAccountType()+" ==================");
                        System.out.println("==================VENDOR ID :::: "+response.body().getVendor_id()+" ==================");
                        System.out.println("==================JWT Token :::: "+response.body().getToken()+" ==================");
                        progressDialog.dismiss();
                        emailField.setText(email);

                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                System.out.println("==================send Login Fail :::: "+t.getMessage()+" ==================");

            }
        });

    }

    public void hideKb(View view){ //For hiding soft keyboard when tap outside
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    protected void hideKeyboard(View view)    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /*@Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        System.out.println("single tap detected");
        closeKeyboard();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        System.out.println("single tap confirmed detected");
        closeKeyboard();
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        System.out.println("double tap detected");
        closeKeyboard();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        System.out.println("double tap detected");
        closeKeyboard();
        return true;
    }*/
}
