package com.example.vcanteenvendor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.vcanteenvendor.POJO.BugReport;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
            Pattern.compile("^[a-zA-Z0-9_@#&-*'()\"](?=\\S+$).{7,}$");

    private static final Pattern VENDOR_NAME_PATTERN =
            Pattern.compile("[a-zA-Z][a-zA-Z ]+[a-zA-Z]$");

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^\\w+@\\w+\\..{2,3}(.{1,})?$");

    private static final Pattern EMAIL_CHARACTER_PATTERN =
            Pattern.compile("^[a-zA-Z0-9.]+@[a-zA-Z0-9.]+\\..{2,3}(.{2,3})?$");

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

    EditText accountNumber;
    TextView inline_error;

    TextView linkCUNex,linkScb,linkKplus,linkTrueMoney;

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

    // FOR CHANGE 4 DIGIT PIN //
    Button changePinButton;
    private Dialog changePinDialog;
    private EditText currentPinBox, newPinBox, confirmNewPinBox;
    private Button closeChangePinButton, confirmChangePinButton;
    private TextView currentPinError, newPinError, confirmNewPinError;
    private TextView currentPinCount, newPinCount, confirmNewPinCount;
    private String currentPinHash, newPinHash;

    // FOR EDIT PROFILE //
    private Button editProfileButton, closeEditProfileButton, saveEditProfileButton;
    private Dialog editProfileDialog;
    private EditText vendorNameBox, emailBox;
    private TextView vendorNameError, emailError;
    VendorSingleton vendorSingleton;

    // FOR CHANGE PROFILE PICTURE //
    private Button changePictureButton, openGalleryButton, savePictureButton, closeChangePicButton;
    private Dialog changePictureDialog;
    private ImageView uploadPicture;
    Bitmap bitmap;
    // DAVE CODE FOR CHANGE PICTURE //
    private static final int PICK_IMAGE_REQUEST = 1; //Can be any number
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private String imageUrl;
    private Uri downloadUri;
    private ProgressDialog progressDialog2;

    private Button report;
    private EditText reportText;
    private TextView counter;
    private TextView bugInline;

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

        // DAVE CODE FOR CHAGNE PICTURE //
        mStorageRef = FirebaseStorage.getInstance().getReference("uploadsVendorImage");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploadsVendorImage");
        orderStatusButton = findViewById(R.id.orderStatusButton);
        menuButton =  findViewById(R.id.menuButton);
        salesRecordButton = findViewById(R.id.salesRecordButton);
        settingsButton = findViewById(R.id.settingsButton);

        signOutButton = findViewById(R.id.signOutButton);
        vendorStatusToggle = findViewById(R.id.vendorStatusToggle);
        statusText = findViewById(R.id.statusText);

        vendorNameInput = findViewById(R.id.vendorNameInput);
        vendorEmailInput = findViewById(R.id.vendorEmailInput);

        vendorProfile = findViewById(R.id.vendorProfile);

        checkCUNex = findViewById(R.id.checkCUNex);
        checkScb = findViewById(R.id.checkScb);
        checkKplus = findViewById(R.id.checkKplus);
        checkTrueMoney = findViewById(R.id.checkTrueMoney);

        linkCUNex = findViewById(R.id.linkCUNex);
        linkScb = findViewById(R.id.linkScb);
        linkKplus = findViewById(R.id.linkKplus);
        linkTrueMoney = findViewById(R.id.linkTrueMoney);

        vendorProfilePicture = findViewById(R.id.vendorProfilePicture);

        changePass = findViewById(R.id.changePasswordButton);

        viewReviewsButton = findViewById(R.id.viewReviewsButton);
        reviewValue = findViewById(R.id.reviewValue);

        changePinButton = findViewById(R.id.changePinButton);

        changePictureButton = findViewById(R.id.changePictureButton);

        editProfileButton = findViewById(R.id.editProfileButton);
        vendorSingleton = com.example.vcanteenvendor.VendorSingleton.getInstance();

        //////////////////////////////////////////   JSON START UP   //////////////////////////////////////

        //Glide.with(SettingsActivity.this).load(getDrawable(R.drawable.img_loading)).apply(option).into(vendorProfilePicture);


        accountJSONLoadUp();

        onClickLink();
        viewReviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this,VendorReviewActivity.class));
            }
        });


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

        ///////////////////// CHANGE 4 DIGIT PIN ////////////////////////
        changePinDialog = new Dialog(SettingsActivity.this);
        currentPinHash = "";
        newPinHash = "";
        changePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePinDialog();
            }
        });

        ///////////////////// CHANGE PROFILE PICTURE ////////////////////////
        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePictureDialog = new Dialog(SettingsActivity.this);
                changePictureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                changePictureDialog.setContentView(R.layout.edit_profile_image_pop_up);

                uploadPicture = changePictureDialog.findViewById(R.id.uploadPicture);
                openGalleryButton = changePictureDialog.findViewById(R.id.openGalleryButton);
                savePictureButton = changePictureDialog.findViewById(R.id.savePictureButton);
                closeChangePicButton = changePictureDialog.findViewById(R.id.closeButton);

                if(vendorSingleton.getVendorImage() != null){
                    //Glide.with(SettingsActivity.this).load(vendorSingleton.getVendorImage()).apply(option).into(uploadPicture);

                }

                changePictureDialog.show();

                closeChangePicButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePictureDialog.dismiss();
                    }
                });

                openGalleryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openFileChooser();
                    }
                });

                savePictureButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        uploadFile(); //เริ่มอัพโหลดเมื่อไหร่ก็ใส่ตรงนั้น ในกรณีนี้คือเราให้เลือกไฟล์เสร็จ อัพเลยทันที
                    }
                });

            }
        });

        ///////////////////// EDIT PROFILE PAGE ////////////////////////
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileDialog = new Dialog(SettingsActivity.this);
                editProfileDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                editProfileDialog.setContentView(R.layout.edit_profile_pop_up);

                closeEditProfileButton = editProfileDialog.findViewById(R.id.closeButton);
                saveEditProfileButton = editProfileDialog.findViewById(R.id.saveButton);

                vendorNameBox = editProfileDialog.findViewById(R.id.vendorNameBox);
                emailBox = editProfileDialog.findViewById(R.id.emailBox);
                vendorNameError = editProfileDialog.findViewById(R.id.vendorNameError);
                emailError = editProfileDialog.findViewById(R.id.emailError);

                vendorNameBox.setText(vendorSingleton.getVendorName());
                emailBox.setText(vendorSingleton.getEmail());

                vendorNameError.setText("");
                emailError.setText("");

                System.out.println(vendorSingleton.getVendorName());
                System.out.println(vendorSingleton.getEmail());

                saveEditProfileButton.setEnabled(false);
                saveEditProfileButton.setAlpha(0.4f);

                editProfileDialog.show();

                vendorNameBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!(vendorNameBox.getText().toString().equals(vendorSingleton.getVendorName()))){
                            saveEditProfileButton.setEnabled(true);
                            saveEditProfileButton.setAlpha(1f);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                emailBox.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!(emailBox.getText().toString().equals(vendorSingleton.getEmail()))){
                            saveEditProfileButton.setEnabled(true);
                            saveEditProfileButton.setAlpha(1f);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                closeEditProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editProfileDialog.dismiss();
                    }
                });

                saveEditProfileButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Case 1 : If either field is blank
                        if(vendorNameBox.getText().toString().length() == 0 || emailBox.getText().toString().length() == 0){
                            if(vendorNameBox.getText().toString().length() == 0){ vendorNameError.setText("This field cannot be blank."); }
                            if(emailBox.getText().toString().length() == 0){ emailError.setText("This field cannot be blank."); }
                        }
                        // Case 2 : Wrong name format (a-z, A-Z) for vendor name
                        else if(!(VENDOR_NAME_PATTERN.matcher(vendorNameBox.getText().toString()).matches())){
                            vendorNameError.setText("Only a-z and A-Z is allowed.");
                            emailError.setText("");
                        }
                        // Case 3 : Wrong email format
                        else if(!(EMAIL_PATTERN.matcher(emailBox.getText().toString()).matches())){
                            emailError.setText("Invalid email. Please try again.");
                            vendorNameError.setText("");
                        }
                        // Case 4 : Contains other characters than a-z, A-Z, 0-9, or a period in email
                        else if(!(EMAIL_CHARACTER_PATTERN.matcher(emailBox.getText().toString()).matches())){
                            emailError.setText("Only a-z, A-Z, 0-9, or a period is allowed.");
                            vendorNameError.setText("");
                        }
                        // If the above condition are met
                        else{
                            System.out.println("the above condition are met!!!");
                            vendorNameError.setText("");
                            emailError.setText("");

                            final String vendorName = vendorNameBox.getText().toString();
                            final String email = emailBox.getText().toString();

                            url = "https://vcanteen.herokuapp.com/";
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                            Call<Void> callToEditProfile = jsonPlaceHolderApi.changeNameAndEmail(vendor_id, vendorName, email);

                            callToEditProfile.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if(!response.isSuccessful()){
                                        Toast.makeText(SettingsActivity.this, "CODE: "+response.code(), Toast.LENGTH_LONG).show();
                                        System.out.println("CODE: "+response.code());
                                        return;
                                    }

                                    if(response.code()==200) {

                                        vendorSingleton.setVendorName(vendorName);
                                        vendorSingleton.setEmail(email);

                                        vendorNameInput.setText(vendorSingleton.getVendorName());
                                        vendorEmailInput.setText(vendorSingleton.getEmail());

                                        editProfileDialog.dismiss();
                                    }

                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    System.out.println("Error to edit profile");
                                }
                            });

                        }
                    }
                });

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

        ///////////////BUG REPORT//////////
        report = findViewById(R.id.reportButton);
        reportText = findViewById(R.id.bugReportBox);
        bugInline = findViewById(R.id.bugInline);
        counter = findViewById(R.id.wordCountReportBox);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(reportText.getText());

                if (reportText.getText().toString().trim().isEmpty()) {
                    bugInline.setText("This field cannot be blank.");
                    bugInline.setVisibility(View.VISIBLE);
                    return;
                }

                //TODO Check Emoji
                sharedPref = SettingsActivity.this.getSharedPreferences("myPref", MODE_PRIVATE);
                BugReport report = new BugReport();
                report.setVendorId(sharedPref.getInt("vendor_id", 0));
                report.setMessage(reportText.getText().toString().trim());

                //TODO Include retrofit here for sending
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://vcanteen.herokuapp.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<Void> call = jsonPlaceHolderApi.postBugReport(report);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "CODE: " + response.code(), Toast.LENGTH_LONG).show();
                            return;

                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });

                reportText.setText("");
                Toast.makeText(SettingsActivity.this, "Bug report successfully submitted", Toast.LENGTH_LONG).show();
            }
        });

        reportText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                counter.setText(s.toString().length()+"/300");

                if(s.toString().length()>0) {
                    bugInline.setVisibility(View.INVISIBLE);
                }
            }
        });
        reportText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reportText.setCursorVisible(true);
                reportText.setFocusable(true);
            }
        });
        findViewById(R.id.linearLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                }catch (Exception e){
                    return false;
                }
                reportText.setCursorVisible(false);
//                reportText.setFocusable(false);
                return true;

            }
        });

    }

    /////////////EDIT EPAYMENT//////////
    private void onClickLink() {
        linkCUNex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK CUNEX pressed");
                showLinkPopup("CU_NEX");
            }
        });
        linkScb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK SCB EASY pressed");
                showLinkPopup("SCB_EASY");
            }
        });
        linkKplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK K PLUS pressed");
                showLinkPopup("K_PLUS");
            }
        });
        linkTrueMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK TrueMoney Wallet pressed");
                showLinkPopup("TRUEMONEY_WALLET");
            }
        });
    }

    private void showLinkPopup(final String serviceProvider) {
        dialog = new Dialog(SettingsActivity.this);
        dialog.setContentView(R.layout.popup_payment_link);
        dialog.setCancelable(true);
        TextView serviceProviderText = dialog.findViewById(R.id.serviceProviderText);
        ImageView close_x = dialog.findViewById(R.id.close_x);
        Button linkBtn = dialog.findViewById(R.id.link_btn);
        accountNumber = dialog.findViewById(R.id.accountNumber);
        inline_error = dialog.findViewById(R.id.inline_error);
        String shownServiceProviderName = "";
        if(serviceProvider.equals("CU_NEX")) {
            shownServiceProviderName = "CU NEX";
        }
        if(serviceProvider.equals("SCB_EASY")) {
            shownServiceProviderName = "SCB EASY";
        }
        if(serviceProvider.equals("K_PLUS")) {
            shownServiceProviderName = "K PLUS";
        }
        if(serviceProvider.equals("TRUEMONEY_WALLET")) {
            shownServiceProviderName = "TrueMoney Wallet";
        }
        serviceProviderText.setText(shownServiceProviderName);

        close_x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        linkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("LINK Pressed");
                if(accountNumber.getText().toString().equals("")) {
                    inline_error.setVisibility(View.VISIBLE);
                    return;
                }
                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog = ProgressDialog.show(SettingsActivity.this
                        , "",
                        "Loading. Please wait...", true);
                //Retrofit
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://vcanteen.herokuapp.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                Call<Void> call =  jsonPlaceHolderApi.postLinkPayment(vendor_id, serviceProvider, accountNumber.getText().toString());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if(!response.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "CODE: "+response.code(), Toast.LENGTH_LONG).show();
                            System.out.println("error onResponse post link");
                            progressDialog.dismiss();
                            dialog.dismiss();
                            return;
                        }
                        progressDialog.dismiss();
                        System.out.println("Successfully Linked Account and reloading page");
                        accountJSONLoadUp();
                        Toast.makeText(SettingsActivity.this, "Successfully Linked Account", Toast.LENGTH_LONG).show();

                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        System.out.println("error onFailure post link");
                        progressDialog.dismiss();
                        dialog.dismiss();
                    }
                });
//                progressDialog.dismiss();
            }
        });

        accountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length()>0) {
                    inline_error.setVisibility(View.INVISIBLE);
                }
            }
        });

        dialog.show();

    }


    private void cancelAllCookingOrders() {

        url = "https://vcanteen.herokuapp.com/";
        String reason = "The vendor has closed their restaurant.";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.cancelAllOrder(vendor_id,reason);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    //vendorNameInput.setText("Code: " + response.code());
                    System.out.println("\n\n\n\n********************" + "Code: " + response.code() + "********************\n\n\n\n");
                    return;
                }

                System.out.println("\n\n\n\n********************"+ " ALL COOKING of vendor id "+ vendor_id +"********************\n\n\n\n");


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n******************** Fail::: " + t.getMessage() + "********************\n\n\n\n");


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
//        Call<VendorInfoArray> call = jsonPlaceHolderApi.getVendorInfo(vendor_id); //add to try



        call.enqueue(new Callback<VendorInfoArray>() {
            @Override
            public void onResponse(Call<VendorInfoArray> call, Response<VendorInfoArray> response) {

                if (!response.isSuccessful()) {
                    //vendorNameInput.setText("Code: " + response.code());
                    //vendorNameInput.setText("");
                    progressDialog.dismiss();
                    return;
                }



                vendorInfoArray = response.body();

                if (vendorInfoArray != null) {

                    vendor = vendorInfoArray.vendorInfo.get(0);
                    vendorNameInput.setText(vendor.getVendorName());
                    email = vendor.getVendorEmail();
                    System.out.println(email);
                    vendorEmailInput.setText(vendor.getVendorEmail());

                    // FOR EDIT PROFILE //
                    vendorSingleton.setVendorName(vendor.getVendorName());
                    vendorSingleton.setEmail(vendor.getVendorEmail());

                    /*if(vendor.getScore() != 0.0){
                        reviewValue.setText(String.valueOf(vendor.getScore())+" ✭");
                    }*/
                    reviewValue.setText(String.valueOf(vendor.getScore())+" ✭");


                    if (vendor.getVendorImage() != null) {
                        Glide.with(SettingsActivity.this).load(vendor.getVendorImage()).apply(option).into(vendorProfilePicture);
                        vendorSingleton.setVendorImage(vendor.getVendorImage());
                    }
                    //This array always have 1 member, so use get(1).
                    progressDialog.dismiss();

                } else {
                    vendorNameInput.setText("Receive Null");
                    progressDialog.dismiss();
                }

                checkCUNex.setVisibility(View.INVISIBLE);
                checkScb.setVisibility(View.INVISIBLE);
                checkKplus.setVisibility(View.INVISIBLE);
                checkTrueMoney.setVisibility(View.INVISIBLE);

                linkCUNex.setVisibility(View.VISIBLE);
                linkScb.setVisibility(View.VISIBLE);
                linkKplus.setVisibility(View.VISIBLE);
                linkTrueMoney.setVisibility(View.VISIBLE);


                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "CU_NEX")) {
                    checkCUNex.setVisibility(View.VISIBLE);
                    linkCUNex.setVisibility(View.INVISIBLE);
                }

                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "SCB_EASY")) {
                    checkScb.setVisibility(View.VISIBLE);
                    linkScb.setVisibility(View.INVISIBLE);
                }

                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "K_PLUS")) {
                    checkKplus.setVisibility(View.VISIBLE);
                    linkKplus.setVisibility(View.INVISIBLE);
                }

                if (vendorInfoArray.findServiceProviderFromList(vendorInfoArray.getVendorPaymentMethod(), "TRUEMONEY_WALLET")) {
                    checkTrueMoney.setVisibility(View.VISIBLE);
                    linkTrueMoney.setVisibility(View.INVISIBLE);
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
                progressDialog.dismiss();


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
        sharedPref.edit().putString("token", "NO TOKEN").commit();
        sharedPref.edit().putInt("vendor_id", 0).commit();
        sharedPref.edit().putString("email", "empty email").commit();
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

    public void showChangePinDialog(){
        changePinDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        changePinDialog.setContentView(R.layout.change_pin_dialog);
        currentPinBox = changePinDialog.findViewById(R.id.currentPinBox);
        newPinBox=changePinDialog.findViewById(R.id.newPinBox);
        confirmNewPinBox = changePinDialog.findViewById(R.id.confirmNewPinBox);

        closeChangePinButton = (Button) changePinDialog.findViewById(R.id.closeButton);
        confirmChangePinButton = (Button) changePinDialog.findViewById(R.id.confirmButton);

        currentPinError = (TextView) changePinDialog.findViewById(R.id.currentPinError);
        newPinError= (TextView) changePinDialog.findViewById(R.id.newPinError);
        confirmNewPinError = (TextView) changePinDialog.findViewById(R.id.confirmNewPinError);

        currentPinError.setText("");
        newPinError.setText("");
        confirmNewPinError.setText("");

        currentPinCount = changePinDialog.findViewById(R.id.currentPinCount);
        newPinCount = changePinDialog.findViewById(R.id.newPinCount);
        confirmNewPinCount = changePinDialog.findViewById(R.id.confirmNewPinCount);

        changePinDialog.show();

        final String currentPin = currentPinBox.getText().toString();
        final String newPin = newPinBox.getText().toString();

        currentPinBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int numCurrent = currentPinBox.getText().toString().length();
                currentPinCount.setText(""+numCurrent+"/4");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPinBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int numNew = newPinBox.getText().toString().length();
                newPinCount.setText(""+numNew+"/4");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmNewPinBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int numConfirmNew = confirmNewPinBox.getText().toString().length();
                confirmNewPinCount.setText(""+numConfirmNew+"/4");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        closeChangePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePinDialog.dismiss();
            }
        });

        confirmChangePinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentPinBox.getText().toString().length()==0 || newPinBox.getText().toString().length()==0 || confirmNewPinBox.getText().toString().length()==0){
                    // If blank
                    if(currentPinBox.getText().toString().length()==0) {currentPinError.setText("This field cannot be blank.");}
                    if(newPinBox.getText().toString().length()==0){newPinError.setText("This field cannot be blank.");}
                    if(confirmNewPinBox.getText().toString().length()==0){confirmNewPinError.setText("This field cannot be blank.");}
                }else if(!(newPinBox.getText().toString().equals(confirmNewPinBox.getText().toString()))){
                    // If both fields are not match
                    currentPinError.setText("");
                    newPinError.setText("");
                    confirmNewPinError.setText("PIN don’t match.");
                }else if(newPinBox.getText().toString().length() < 4 || confirmNewPinBox.getText().toString().length() < 4 ){
                    // If less than 4 digit
                    currentPinError.setText("");
                    newPinError.setText("Please fill in all 4 digits of your pin.");
                    confirmNewPinError.setText("Please fill in all 4 digits of your pin.");
                }else if(currentPinBox.getText().toString().equals(newPinBox.getText().toString())){
                    // If Current PIN and New PIN are the same
                    currentPinError.setText("");
                    newPinError.setText("New and old PIN are identical.");
                    confirmNewPinError.setText("");
                }else{
                    currentPinHash = new String(Hex.encodeHex(DigestUtils.sha256(currentPinBox.getText().toString()))); // just add
                    newPinHash = new String(Hex.encodeHex(DigestUtils.sha256(newPinBox.getText().toString()))); // just add

                    System.out.println("Hash current : "+ currentPinHash);
                    System.out.println("Hash new : "+ newPinHash);
                    // If all condition are met...
                    url = "https://vcanteen.herokuapp.com/";
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                    Call<Void> call = jsonPlaceHolderApi.checkPin(vendor_id, currentPinHash); //currentPinBox.getText().toString());

                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if(!response.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "CODE: "+response.code(), Toast.LENGTH_LONG).show();
                                newPinBox.setText("CODE: "+response.code());
                                currentPinBox.setText("CODE: "+response.code());
                                currentPinError.setText("CODE: "+response.code());
                                confirmNewPinBox.setText("CODE: "+response.code());
                                if(response.code()==404) {
                                    currentPinError.setText("Incorrect PIN");
                                    newPinError.setText("");
                                    confirmNewPinError.setText("");
                                }
                                return;
                            }
                            int code = response.code();
                            if(code==200) { // If current PIN match with BE PIN

                                url = "https://vcanteen.herokuapp.com/";
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(url)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
                                Call<Void> call2 = jsonPlaceHolderApi.changePin(vendor_id, newPinHash);//newPinBox.getText().toString());

                                call2.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(Call<Void> call, Response<Void> response) {
                                        if(!response.isSuccessful()){
                                            Toast.makeText(SettingsActivity.this, "CODE: "+response.code(), Toast.LENGTH_LONG).show();
                                            newPinError.setText("CODE: "+response.code());
                                            confirmNewPinBox.setText("CODE: "+response.code());
                                            return;
                                        }
                                        if(response.code()==200) {
                                            newPinBox.setText("");
                                            currentPinBox.setText("");
                                            confirmNewPinBox.setText("");
                                            changePinDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Your 4-digit pin is changed.", Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<Void> call, Throwable t) {
                                        System.out.println("Error change pin");
                                    }
                                });


                            } else if(code==404) {
                                currentPinError.setText("Incorrect PIN");
                                newPinError.setText("");
                                confirmNewPinError.setText("");
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

    }

    // DAVE CODE FOR UPLOADING PHOTO //
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //มีไว้คู่กับ open file chooser เฉยๆ
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            //Picasso.with(this).load(mImageUri).into(mImageView);
            Glide.with(this).load(mImageUri).apply(option).into(uploadPicture);


        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) {
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));  //สร้างที่ไฟล์ ชื่อจาก System.currentTimeMillis() เพื่อไม่ให้มันชื่อซ้ำ

            progressDialog2 = new ProgressDialog(SettingsActivity.this);
            progressDialog2 = ProgressDialog.show(SettingsActivity.this, "",
                    "Uploading ...", true);

            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) { // เมื่ออัพโหลดเสร็จ ทำอะไร

                                    progressDialog.dismiss();

                                    downloadUri = uri;
                                    imageUrl = downloadUri.toString(); //ได้ URL ของรูปนั้นมาเป็น string
                                    //selectedMenu.setFoodImg(imageUrl); //เอาไปเก็บไว้ เตรียมส่งให้ MySQL ด้วย retrofit
                                    vendorSingleton.setVendorImage(imageUrl);
                                    System.out.println("URL of image :"+imageUrl);
                                    UploadVendorProfileImage upload = new UploadVendorProfileImage(vendorSingleton.getVendorName().trim(), downloadUri.toString());
                                    //สร้าง class ชื่อ Upload ด้วย

                                    String uploadId = mDatabaseRef.push().getKey();

                                    mDatabaseRef.child(uploadId).setValue(upload);

                                    Toast.makeText(SettingsActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                                    // call endpoint?
                                    callToUpdateProfileImage();



                                    //bitmap = getBitmapFromURL(vendorSingleton.getVendorImage());
                                   // vendorProfilePicture.setImageBitmap(bitmap);

                                    progressDialog2.dismiss();
                                    changePictureDialog.dismiss();
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { // อัพไม่สำเร็จ ทำอะไร
                            System.out.println("Fail to upload photo");
                            progressDialog.dismiss();
                            Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) { //ระหว่างกำลังอัพ ทำอะไร
                            System.out.println("Try to upload image");
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    public void callToUpdateProfileImage(){
        url = "https://vcanteen.herokuapp.com/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> callToEditProfileImage = jsonPlaceHolderApi.updateProfileImage(vendor_id, vendorSingleton.getVendorImage());

        callToEditProfileImage.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(!response.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "CODE: "+response.code(), Toast.LENGTH_LONG).show();
                    System.out.println("CODE: "+response.code());
                    return;
                }

                if(response.code()==200) {
                    Glide.with(SettingsActivity.this).load(vendorSingleton.getVendorImage()).apply(option).into(vendorProfilePicture);

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error in updating profile image");
            }
        });
    }

    public Bitmap getBitmapFromURL(String src){
        try{
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}
