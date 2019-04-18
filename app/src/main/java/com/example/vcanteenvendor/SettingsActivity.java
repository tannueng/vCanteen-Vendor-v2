package com.example.vcanteenvendor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SettingsActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^[a-zA-Z0-9@!#$%^&+-=](?=\\S+$).{7,}$");

    Button orderStatusButton; //ORDER STATUS
    Button menuButton; //MENU
    Button salesRecordButton; //SALES RECORD
    Button settingsButton; //SETTINGS
    Button changePasswordButton;

    Button signOutButton;
    Switch vendorStatusToggle;
    TextView statusText;
    TextView vendorProfile;

    EditText vendorNameInput;
    EditText vendorEmailInput;

    TextView checkCUNex;
    TextView checkScb;
    TextView checkKplus;
    TextView checkTrueMoney;

    ImageView vendorProfilePicture;

    private Dialog confirmChangePassDialog;
    private TextView cancelChangePass;
    private Button confirmConfirmChangePass;


    Button changePass;
    Dialog changePassDialog;

    /// FOR CHANGEPASS DIALOG ///
    private EditText currPassBox;
    private EditText newPassBox;
    private EditText confirmNewPassBox;
    private Button confirmChangePass;

    private Button clearCurrPass;
    private Button clearNewPass;
    private Button clearConfirmPass;

    private TextView errorCurrPass;
    private TextView errorNewPass;
    private TextView errorConfirmPass;

    private Button closeDialog;
    private SharedPreferences sharedPref;


    private RequestQueue mQueue;
    private String url = "FROM ENDPOINTS";

    String email;

    Dialog dialog;

    Vendor vendor;
    VendorInfoArray vendorInfoArray;

    ProgressDialog progressDialog;

    RequestOptions option = new RequestOptions().centerCrop();
    int vendor_id;

    Button viewReviewsButton;
    TextView reviewValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        orderStatusButton = (Button) findViewById(R.id.orderStatusButton);
        menuButton = (Button) findViewById(R.id.menuButton);
        salesRecordButton = (Button) findViewById(R.id.salesRecordButton);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        signOutButton = (Button) findViewById(R.id.signOutButton);
        vendorStatusToggle = (Switch) findViewById(R.id.vendorStatusToggle);
        statusText = (TextView) findViewById(R.id.statusText);

        vendorNameInput = (EditText) findViewById(R.id.vendorNameInput);
        vendorEmailInput = (EditText) findViewById(R.id.vendorEmailInput);

        vendorProfile = (TextView) findViewById(R.id.vendorProfile);

        checkCUNex = (TextView) findViewById(R.id.checkCUNex);
        checkScb = (TextView) findViewById(R.id.checkScb);
        checkKplus = (TextView) findViewById(R.id.checkKplus);
        checkTrueMoney = (TextView) findViewById(R.id.checkTrueMoney);

        vendorProfilePicture = findViewById(R.id.vendorProfilePicture);

        changePass = findViewById(R.id.changePasswordButton);

        viewReviewsButton = findViewById(R.id.viewReviewsButton);
        reviewValue = findViewById(R.id.reviewValue);

        //////////////////////////////////////////   JSON START UP   //////////////////////////////////////

        //Glide.with(SettingsActivity.this).load(getDrawable(R.drawable.img_loading)).apply(option).into(vendorProfilePicture);


        accountJSONLoadUp();


        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        vendor_id = sharedPref.getInt("vendor_id", 0);


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

        salesRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSalesRecord();
            }
        });

        /*settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });*/


        signOutButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(SettingsActivity.this);
                //dialog.setTitle("Devahoy");
                dialog.setContentView(R.layout.dialog_red);

                final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
                final TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
                Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
                Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);


                content.setText("Log out of vCanteen?");
                content.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/SF-Pro-Text-Bold.otf"));
                positiveButton.setText("LOG OUT");
                title.setVisibility(View.GONE);


                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        logOut();
                        Toast.makeText(getApplicationContext(), "LOG OUT SUCCESS!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });


        vendorStatusToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!vendorStatusToggle.isChecked()) {
                    vendorStatusToggle.setChecked(true);


                    final Dialog dialog = new Dialog(SettingsActivity.this);

                    dialog.setContentView(R.layout.dialog_red);

                    final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
                    final TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
                    Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
                    final Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);


                    title.setText("Closing Restaurant");
                    content.setText(R.string.closing_vendor);
                    positiveButton.setText("close vendor");
                    //content.setGravity(Gravity.LEFT);


                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            vendorStatusToggle.setChecked(true);
                            dialog.dismiss();
                        }
                    });

                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            positiveButton.setBackgroundResource(R.drawable.button_grey_rounded);
                            openCloseVendor("CLOSED");


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    vendorStatusToggle.setChecked(false);
                                    cancelAllCookingOrders();

                                    Toast.makeText(getApplicationContext(), "VENDOR CLOSED!", Toast.LENGTH_SHORT).show();
                                    positiveButton.setBackgroundResource(R.drawable.button_red_rounded);
                                    dialog.dismiss();
                                    statusText.setText("CLOSED");
                                    statusText.setTextColor(Color.parseColor("#828282"));

                                }
                            }, 2000);

                        }
                    });

                    dialog.show();

                } else {

                    openCloseVendor("OPEN");
                    statusText.setText("OPEN");
                    statusText.setTextColor(getResources().getColor(R.color.pinkPrimary));
                }

            }
        });

        ///////////////////// CHANGE PASSWORD ////////////////////////
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassDialog = new Dialog(SettingsActivity.this);
                changePassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                changePassDialog.setContentView(R.layout.change_password_dialog);

                currPassBox = changePassDialog.findViewById(R.id.currentPasswordBox);
                newPassBox = changePassDialog.findViewById(R.id.newPasswordBox);
                confirmNewPassBox = changePassDialog.findViewById(R.id.confirmNewPasswordBox);
                confirmChangePass = changePassDialog.findViewById(R.id.confirmChangePasswordButton);

                clearCurrPass = changePassDialog.findViewById(R.id.clearTextButtonCurrentPW);
                clearNewPass = changePassDialog.findViewById(R.id.clearTextButtonNewPW);
                clearConfirmPass = changePassDialog.findViewById(R.id.clearTextButtonConfirmNewPW);

                errorCurrPass = changePassDialog.findViewById(R.id.errorCurrPass);
                errorNewPass = changePassDialog.findViewById(R.id.errorNewPass);
                errorConfirmPass = changePassDialog.findViewById(R.id.errorConfirmPass);

                closeDialog = changePassDialog.findViewById(R.id.close_dialog);

                confirmChangePass.setEnabled(true);
                clearCurrPass.setEnabled(true);
                clearNewPass.setEnabled(true);
                clearConfirmPass.setEnabled(true);

                changePassDialog.show();

                confirmChangePass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String currPass = currPassBox.getText().toString();
                        final String newPass = newPassBox.getText().toString();
                        final String confirmPass = confirmNewPassBox.getText().toString();

                        if (currPass.matches("") || newPass.matches("") || confirmPass.matches("")) { //pin just add
                            errorCurrPass.setText("You must fill out all the fields.");
                            errorCurrPass.setVisibility(View.VISIBLE);
                            errorConfirmPass.setVisibility(View.INVISIBLE);
                            errorNewPass.setVisibility(View.INVISIBLE);
                            currPassBox.setText("");
                            newPassBox.setText("");
                            confirmNewPassBox.setText("");
                        } else if (!PASSWORD_PATTERN.matcher(newPass).matches() || !PASSWORD_PATTERN.matcher(confirmPass).matches()) {
                            errorNewPass.setText("Must be letter, number or these characters _ - * ' \" # & () @");
                            errorNewPass.setVisibility(View.VISIBLE);
                            errorConfirmPass.setVisibility(View.INVISIBLE);
                            errorCurrPass.setVisibility(View.INVISIBLE);
                            //currentPassword.setText("");
                            newPassBox.setText("");
                            confirmNewPassBox.setText("");
                        } else if (!(newPass.equals(confirmPass))) {
                            errorNewPass.setText("Password doesn't match. Please try again.");
                            errorNewPass.setVisibility(View.VISIBLE);
                            errorConfirmPass.setVisibility(View.INVISIBLE);
                            errorCurrPass.setVisibility(View.INVISIBLE);
                            //currentPassword.setText("");
                            newPassBox.setText("");
                            confirmNewPassBox.setText("");
                        } else if (newPass.length() < 8 || newPass.length() > 20) {
                            errorNewPass.setText("Invalid Password. Please try again.");
                            errorNewPass.setVisibility(View.VISIBLE);
                            errorConfirmPass.setVisibility(View.INVISIBLE);
                            errorCurrPass.setVisibility(View.INVISIBLE);
                            //currentPassword.setText("");
                            newPassBox.setText("");
                            confirmNewPassBox.setText("");
                        } else if (currPass.equals(newPass)) {
                            errorCurrPass.setText("Your new password can't be the same as your current passaword.");
                            errorCurrPass.setVisibility(View.VISIBLE);
                            errorConfirmPass.setVisibility(View.INVISIBLE);
                            errorNewPass.setVisibility(View.INVISIBLE);
                            currPassBox.setText("");
                            newPassBox.setText("");
                            confirmNewPassBox.setText("");
                        } else {
                            confirmChangePassDialog = new Dialog(SettingsActivity.this);
                            confirmChangePassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            confirmChangePassDialog.setContentView(R.layout.change_password_popup);

                            confirmConfirmChangePass = confirmChangePassDialog.findViewById(R.id.confirmButtonPassword);
                            cancelChangePass = confirmChangePassDialog.findViewById(R.id.cancelButtonPassword);

                            confirmConfirmChangePass.setEnabled(true);
                            cancelChangePass.setEnabled(true);

                            confirmChangePassDialog.show();

                            confirmConfirmChangePass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    url = "https://vcanteen.herokuapp.com/";

                                    Gson gson = new GsonBuilder().serializeNulls().create();
                                    Retrofit retrofit = new Retrofit.Builder()
                                            .baseUrl(url)
                                            .addConverterFactory(GsonConverterFactory.create(gson))
                                            .build();
                                    final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

                                    String hashedNewPass = new String(Hex.encodeHex(DigestUtils.sha256(newPass)));
                                    ChangePass postData = new ChangePass(hashedNewPass, email);
                                    String hashedCurrPass = new String(Hex.encodeHex(DigestUtils.sha256(currPass)));

                                    String firebaseToken = sharedPref.getString("firebaseToken", "default no token");
                                    String account_type = sharedPref.getString("account_type", "DEFAULT");
                                    LoginVendor loginVendor = new LoginVendor(email, hashedCurrPass, firebaseToken, account_type);
                                    System.out.println(loginVendor.toString());
                                    Call<LoginResponse> call = jsonPlaceHolderApi.sendLogin(loginVendor);
                                    progressDialog = new ProgressDialog(SettingsActivity.this);
                                    progressDialog = ProgressDialog.show(SettingsActivity.this
                                            , "",
                                            "Loading. Please wait...", true);
                                    call.enqueue(new Callback<LoginResponse>() {
                                        @Override
                                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                                            if (response.code() != 200) {
                                                errorCurrPass.setText("Current Password is incorrect.");
                                                errorCurrPass.setVisibility(View.VISIBLE);
                                                errorNewPass.setVisibility(View.INVISIBLE);
                                                errorConfirmPass.setVisibility(View.INVISIBLE);
                                                confirmChangePassDialog.cancel();
                                                progressDialog.dismiss();
                                            } else {
                                                changePass(newPass);
                                                confirmChangePassDialog.cancel();
                                                changePassDialog.cancel();
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Password successfully changed.", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<LoginResponse> call, Throwable t) {

                                        }
                                    });


                                }
                            });

                            cancelChangePass.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmChangePassDialog.cancel();
                                }
                            });
                        }

                    }
                });

                clearCurrPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currPassBox.setText("");
                    }
                });

                clearNewPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        newPassBox.setText("");
                    }
                });

                clearConfirmPass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmNewPassBox.setText("");
                    }
                });

                closeDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePassDialog.dismiss();
                    }
                });
            }
        });


        viewReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, VendorReviewActivity.class);
                startActivity(intent);
            }
        });

    }

    private void cancelAllCookingOrders() {

        url = "https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.cancelAllOrder(vendor_id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    //vendorNameInput.setText("Code: " + response.code());
                    System.out.println("\n\n\n\n********************" + "Code: " + response.code() + "********************\n\n\n\n");
                    return;
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n********************" + t.getMessage() + "********************\n\n\n\n");


            }
        });

    }

    private void openCloseVendor(String vendorStatus) {

        url = "https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.editVendorStatus(vendor_id, vendorStatus);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    //vendorNameInput.setText("Code: " + response.code());
                    return;
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());


            }
        });

    }


    private void accountJSONLoadUp() {

        progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog = ProgressDialog.show(SettingsActivity.this, "",
                "Loading. Please wait...", true);

        url = "https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        String token = sharedPref.getString("token", "empty token");
        int vendor_id = sharedPref.getInt("vendor_id", 0);
        System.out.println(vendor_id);
        Call<VendorInfoArray> call = jsonPlaceHolderApi.getVendorInfoV2(vendor_id);

        progressDialog.dismiss();

        call.enqueue(new Callback<VendorInfoArray>() {
            @Override
            public void onResponse(Call<VendorInfoArray> call, Response<VendorInfoArray> response) {

                if (!response.isSuccessful()) {
                    //vendorNameInput.setText("Code: " + response.code());
                    //vendorNameInput.setText("");
                    return;
                }


                vendorInfoArray = response.body();

                if (vendorInfoArray != null) {

                    vendor = vendorInfoArray.vendorInfo.get(0);
                    vendorNameInput.setText(vendor.getVendorName());
                    email = vendor.getVendorEmail();
                    System.out.println(email);
                    vendorEmailInput.setText(vendor.getVendorEmail());

                    if(vendor.getScore() != 0.0){
                        reviewValue.setText(String.valueOf(vendor.getScore())+" ✭");
                    }


                    if (vendor.getVendorImage() != null)
                        Glide.with(SettingsActivity.this).load(vendor.getVendorImage()).apply(option).into(vendorProfilePicture);
                    //This array always have 1 member, so use get(1).

                } else {
                    vendorNameInput.setText("Receive Null");
                }


                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "CU_NEX")) {
                    checkCUNex.setVisibility(View.VISIBLE);
                }

                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "SCB_EASY")) {
                    checkScb.setVisibility(View.VISIBLE);
                }

                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "K_PLUS")) {
                    checkKplus.setVisibility(View.VISIBLE);
                }

                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "TRUEMONEY_WALLET")) {
                    checkTrueMoney.setVisibility(View.VISIBLE);
                }


                if (vendor.getVendorStatus().equals("OPEN")) {
                    vendorStatusToggle.setChecked(true);
                    statusText.setText("OPEN");
                    statusText.setTextColor(getResources().getColor(R.color.pinkPrimary));
                } else {
                    vendorStatusToggle.setChecked(false);
                    statusText.setText("CLOSED");
                    statusText.setTextColor(Color.parseColor("#828282"));
                }


            }

            @Override
            public void onFailure(Call<VendorInfoArray> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());

                progressDialog.dismiss();
            }
        });


    }


    //////////////////////////////////////////   Navigation(cont.)   //////////////////////////////////////
    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    public void goToSalesRecord() {
        Intent intent = new Intent(this, SalesRecordActivity.class);
        startActivity(intent);
    }

    public void logOut() {
        LoginManager.getInstance().logOut();
        sharedPref.edit().putString("token", "NO TOKEN JA EDOK").commit();
        sharedPref.edit().putInt("vendor_id", 0).commit();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void changePass(String newPass) {

        errorNewPass.setVisibility(View.GONE);
        errorConfirmPass.setVisibility(View.GONE);
        errorCurrPass.setVisibility(View.GONE);

        url = "https://vcanteen.herokuapp.com/";

        Gson gson = new GsonBuilder().serializeNulls().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        String hashedNewPass = new String(Hex.encodeHex(DigestUtils.sha256(newPass)));
        ChangePass postData = new ChangePass(hashedNewPass, email);
        System.out.println(postData.toString());
        Call<Void> call = jsonPlaceHolderApi.resetPass(postData);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() != 200) {
                    // ERROR
                    errorConfirmPass.setText("Current password is incorrect. Please try again.");
                    errorConfirmPass.setVisibility(View.VISIBLE);
                    confirmNewPassBox.setText("");
                    currPassBox.setText("");
                    newPassBox.setText("");
                    progressDialog.dismiss();
                } else {
                    // SUCCESS
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

}
