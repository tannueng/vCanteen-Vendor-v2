package com.example.vcanteenvendor;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEditMenuActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1234;

    private static final Pattern VENDOR_NAME_PATTERN =
            Pattern.compile("[a-zA-Z][a-zA-Z ]+[a-zA-Z]$");

    private static final Pattern NUMBER_PATTERN =
            Pattern.compile("\\d+");

    private static final int PICK_IMAGE_REQUEST = 1; //Can be any number
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;
    private String imageUrl;
    private Uri downloadUri;
    private ProgressDialog progressDialog;

    SharedPreferences sharedPref;
    int vendor_id;

    Button orderStatusButton; //ORDER STATUS
    Button menuButton; //MENU
    Button salesRecordButton; //SALES RECORD
    Button settingsButton; //SETTINGS

    Button backButton;
    Button deleteMenuButton;
    Button saveMenuButton;
    Switch toggle;

    RadioGroup foodTypeRadioGroup;
    RadioButton combiMainRadio;
    RadioButton combiBaseRadio;
    RadioButton alacarteRadio;
    TextInputLayout nameInputLayout;
    TextInputEditText nameInput;
    ImageView uploadImage;
    TextInputLayout priceInputLayout;
    TextInputEditText priceInput;
    Spinner categoryInput;
    TextView editMenuPicture;
    TextInputLayout durationInputLayout;
    TextInputEditText durationInput;
    TextView categoryInputLabel;


    RequestOptions option = new RequestOptions().centerCrop();

     Menu selectedMenu;
     String foodName;
     int foodPrice ;
     int foodId;
     String foodImg ;
     String foodStatus ;
     String foodType;
     String categoryName;
     int prepareDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_menu);


        orderStatusButton= (Button) findViewById(R.id.orderStatusButton);
        menuButton= (Button) findViewById(R.id.menuButton);
        salesRecordButton= (Button) findViewById(R.id.salesRecordButton);
        settingsButton= (Button) findViewById(R.id.settingsButton);

        backButton = (Button) findViewById(R.id.backButton);
        deleteMenuButton = (Button) findViewById(R.id.deleteMenuButton);
        saveMenuButton = findViewById(R.id.saveMenuButton);
        toggle = (Switch)findViewById(R.id.availabilityToggle);

        foodTypeRadioGroup = findViewById(R.id.foodTypeRadioGroup);
        combiMainRadio = findViewById(R.id.combiMainRadio);
        combiBaseRadio = findViewById(R.id.combiBaseRadio);
        alacarteRadio = findViewById(R.id.alacarteRadio);
        nameInputLayout = (TextInputLayout) findViewById(R.id.nameInputLayout);
        nameInput = findViewById(R.id.nameInput);
        uploadImage = findViewById(R.id.uploadImage);
        priceInput= findViewById(R.id.priceInput);
        priceInputLayout = findViewById(R.id.priceInputLayout);
        categoryInput = findViewById(R.id.categoryInput);
        categoryInputLabel = findViewById(R.id.categoryInputLabel);


        editMenuPicture = findViewById(R.id.editMenuPicture);
        durationInput = findViewById(R.id.durationInput);
        durationInputLayout = findViewById(R.id.durationInputLayout);

        sharedPref = getSharedPreferences("myPref", MODE_PRIVATE);
        vendor_id =  sharedPref.getInt("vendor_id", 0);



        nameInput.addTextChangedListener(inputTextWatcher);
        priceInput.addTextChangedListener(inputTextWatcher);
        durationInput.addTextChangedListener(inputTextWatcher);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");
        //แก้จาก upload เป็นอย่างอื่นเพื่อจะได้เก็บไว้คนละ folder ทั้ง 2 อันเลย และให้ชื่อเหมือนกัน



        /*if(getIntent().getStringExtra("foodImageUrl") != null)
        Glide.with(this).load(getIntent().getStringExtra("foodImageUrl")).apply(option).into(uploadImage);*/



        //////////////////////////////////////////   Retrieve every info from menu   //////////////////////////////////////

        foodName = getIntent().getStringExtra("foodName");
        foodPrice = getIntent().getIntExtra("price",0);
        foodId = getIntent().getIntExtra("foodId",0);
        foodImg = getIntent().getStringExtra("foodImage");
        foodStatus = getIntent().getStringExtra("foodStatus");
        foodType = getIntent().getStringExtra("foodType");
        categoryName = getIntent().getStringExtra("categoryName");
        prepareDuration = getIntent().getIntExtra("prepareDuration",0);

        selectedMenu = new Menu(foodName,foodPrice, foodId, foodImg,foodStatus,foodType,categoryName,prepareDuration);

        System.out.println("==============================================" + foodImg);


        //Setup Drop-down list
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryInput.setAdapter(categoryAdapter);

        categoryInput.setSelection(categoryAdapter.getPosition(categoryName));

        final int currentPosition = categoryAdapter.getPosition(categoryName);
        categoryInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != currentPosition) enableSave();
                else disableSave();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        nameInput.setText(foodName);


        if(foodImg != null)
            Glide.with(this).load(foodImg).apply(option).into(uploadImage);
            if(foodType != null) {
            foodTypeRadioGroup.clearCheck();
            if(foodType.equals("ALACARTE")){
                alacarteRadio.setChecked(true);

            } else if (foodType.equals("COMBINATION_BASE")){
                combiBaseRadio.setChecked(true);

            } else if (foodType.equals("COMBINATION_MAIN")){
                combiMainRadio.setChecked(true);

            }
        }


        if(foodStatus != null){
            if(foodStatus.equals("AVAILABLE")){
                toggle.setChecked(true);

            } else if(foodStatus.equals("SOLD_OUT")){
                toggle.setChecked(false);

            }
        }

        if( foodId != 0 ){  //Edit Menu -- if foodId == 0 mean Adding new one
            deleteMenuButton.setVisibility(View.VISIBLE);
            durationInput.setText(String.valueOf(prepareDuration));
            priceInput.setText(String.valueOf(foodPrice));

        }



       final int currentChecked = foodTypeRadioGroup.getCheckedRadioButtonId();
       if(currentChecked != R.id.alacarteRadio){
           categoryInputLabel.setVisibility(View.INVISIBLE);
           categoryInput.setVisibility(View.INVISIBLE);
       } else {
           categoryInputLabel.setVisibility(View.VISIBLE);
           categoryInput.setVisibility(View.VISIBLE);
       }

        foodTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if (checkedId != R.id.alacarteRadio){
                    categoryInputLabel.setVisibility(View.INVISIBLE);
                    categoryInput.setVisibility(View.INVISIBLE);
                } else {
                    categoryInputLabel.setVisibility(View.VISIBLE);
                    categoryInput.setVisibility(View.VISIBLE);
                }

                if (checkedId!=currentChecked) enableSave();
                else disableSave();
            }
        });

       nameInput.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               nameInput.setBackgroundResource(R.drawable.bg_input);
               nameInputLayout.setError(null);
               nameInputLayout.setErrorEnabled(false);
           }
       });

       priceInput.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               priceInput.setBackgroundResource(R.drawable.bg_input);
               priceInputLayout.setError(null);
               priceInputLayout.setErrorEnabled(false);
           }
       });

       durationInput.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               durationInput.setBackgroundResource(R.drawable.bg_input);
               durationInputLayout.setError(null);
               durationInputLayout.setErrorEnabled(false);
           }
       });



        //////////////////////////////////////////   Navigation   //////////////////////////////////////

        orderStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

        salesRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSalesRecord();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSettings();
            }
        });



        /////////////////////////////

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(AddEditMenuActivity.this);

                dialog.setContentView(R.layout.dialog_red);

                final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
                final TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
                Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
                Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);


                title.setText("Delete Menu");
                content.setText(nameInput.getText().toString().trim()+"?");
                positiveButton.setText("delete");



                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Toast.makeText(getApplicationContext(), "Deleting menu...",  Toast.LENGTH_SHORT).show();
                        deleteMenuButton.setText("deleting...");
                        deleteMenuButton.setClickable(false);
                        deleteThisMenu();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "MENU DELETED!",  Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddEditMenuActivity.this, MenuActivity.class);
                                startActivity(intent);
                                deleteMenuButton.setText("delete menu");
                                saveMenuButton.setClickable(true);

                            }
                        }, 3000);

                        //Toast.makeText(getApplicationContext(), "MENU DELETED!",  Toast.LENGTH_SHORT).show();
                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });



        

        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableSave();


                if(!toggle.isChecked() && foodId != 0){
                    toggle.setChecked(true);

                    final Dialog dialog = new Dialog(AddEditMenuActivity.this);

                    dialog.setContentView(R.layout.dialog_red);

                    final TextView title = (TextView) dialog.findViewById(R.id.dialogTitle);
                    final TextView content = (TextView) dialog.findViewById(R.id.dialogContent);
                    Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
                    Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);


                    title.setText("Closing Menu");
                    content.setText("Awaiting orders for this menu\n" +
                            "will be cancelled after saving.\n"
                    );
                    positiveButton.setText("close menu");


                    negativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggle.setChecked(true);
                            dialog.dismiss();
                        }
                    });

                    positiveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggle.setChecked(false);

                            Toast.makeText(getApplicationContext(), "MENU CLOSED!",  Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }
                    });

                    dialog.show();

                }

            }
        });



        saveMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(nameInput.getText().toString().equals("") ){
                    nameInputLayout.setErrorEnabled(true);
                    nameInputLayout.setError("Give me a Name please :)");
                    nameInput.setBackgroundResource(R.drawable.bg_input_error);
                    priceInputLayout.setError(null);
                    priceInputLayout.setErrorEnabled(false);
                    durationInputLayout.setError(null);
                    durationInputLayout.setErrorEnabled(false);

                } else if (!(VENDOR_NAME_PATTERN.matcher(nameInput.getText().toString().trim()).matches())){
                    nameInputLayout.setErrorEnabled(true);
                    nameInputLayout.setError("My name must be in a-z or A-Z!");
                    nameInput.setBackgroundResource(R.drawable.bg_input_error);
                    priceInputLayout.setError(null);
                    priceInputLayout.setErrorEnabled(false);
                    durationInputLayout.setError(null);
                    durationInputLayout.setErrorEnabled(false);

                } else if(nameInput.getText().toString().trim().length()>40){
                    nameInputLayout.setErrorEnabled(true);
                    nameInputLayout.setError("Name is too long");
                    nameInput.setBackgroundResource(R.drawable.bg_input_error);
                    priceInputLayout.setError(null);
                    priceInputLayout.setErrorEnabled(false);
                    durationInputLayout.setError(null);
                    durationInputLayout.setErrorEnabled(false);

                } else if(priceInput.getText().toString().isEmpty()){
                    nameInputLayout.setError(null);
                    nameInputLayout.setErrorEnabled(false);
                    priceInputLayout.setErrorEnabled(true);
                    priceInputLayout.setError("Please insert the price.");
                    priceInput.setBackgroundResource(R.drawable.bg_input_error);
                    durationInputLayout.setError(null);
                    durationInputLayout.setErrorEnabled(false);

                } else if(!(NUMBER_PATTERN.matcher(priceInput.getText().toString().trim()).matches())){
                    nameInputLayout.setError(null);
                    nameInputLayout.setErrorEnabled(false);
                    priceInputLayout.setErrorEnabled(true);
                    priceInputLayout.setError("Price must be in number 0-9");
                    priceInput.setBackgroundResource(R.drawable.bg_input_error);
                    durationInputLayout.setError(null);
                    durationInputLayout.setErrorEnabled(false);

                } else if(durationInput.getText().toString().isEmpty()){
                    nameInputLayout.setError(null);
                    nameInputLayout.setErrorEnabled(false);
                    durationInputLayout.setErrorEnabled(true);
                    durationInputLayout.setError("Please insert the prepare duration.");
                    durationInput.setBackgroundResource(R.drawable.bg_input_error);
                    priceInputLayout.setError(null);
                    priceInputLayout.setErrorEnabled(false);

                } else if(!(NUMBER_PATTERN.matcher(durationInput.getText().toString().trim()).matches())){
                    nameInputLayout.setError(null);
                    nameInputLayout.setErrorEnabled(false);
                    durationInputLayout.setErrorEnabled(true);
                    durationInputLayout.setError("Price must be in number 0-9");
                    durationInput.setBackgroundResource(R.drawable.bg_input_error);
                    priceInputLayout.setError(null);
                    priceInputLayout.setErrorEnabled(false);

                } else {

                    nameInputLayout.setError(null);
                    nameInputLayout.setErrorEnabled(false);
                    durationInputLayout.setError(null);
                    durationInputLayout.setErrorEnabled(false);
                    priceInputLayout.setError(null);
                    priceInputLayout.setErrorEnabled(false);


                    if( foodId == 0){

                        addThisMenu();

                        Toast.makeText(getApplicationContext(), "Saving new menu...",  Toast.LENGTH_SHORT).show();
                        saveMenuButton.setBackgroundResource(R.drawable.button_grey_rounded);
                        saveMenuButton.setText("saving...");
                        saveMenuButton.setClickable(false);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "SAVED!",  Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddEditMenuActivity.this, MenuActivity.class);
                                startActivity(intent);
                                saveMenuButton.setBackgroundResource(R.drawable.pink_round_btn);
                                saveMenuButton.setText("save");
                                saveMenuButton.setClickable(true);

                            }
                        }, 3000);

                    }else{

                        saveThisMenu(foodId, selectedMenu);

                        Toast.makeText(getApplicationContext(), "Saving...",  Toast.LENGTH_SHORT).show();
                        saveMenuButton.setBackgroundResource(R.drawable.button_grey_rounded);
                        saveMenuButton.setText("saving...");
                        saveMenuButton.setClickable(false);
                        //finish();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(getApplicationContext(), "SAVED!",  Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddEditMenuActivity.this, MenuActivity.class);
                                startActivity(intent);
                                saveMenuButton.setBackgroundResource(R.drawable.pink_round_btn);
                                saveMenuButton.setText("save");
                                saveMenuButton.setClickable(true);

                            }
                        }, 3000);

                    }
                }
            }
        });


        editMenuPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });


        disableSave();
    }

    private void deleteThisMenu() {

        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<Void> call = jsonPlaceHolderApi.deleteMenu(vendor_id, selectedMenu.getFoodId());


        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    System.out.println("\n\n\n\n********************"+ "Code: " + response.code() +"********************\n\n\n\n");
                    return;
                }

                System.out.println("\n\n\n\n********************"+ "MENU DELETED" +"********************\n\n\n\n");


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n********************"+ t.getMessage() +"********************\n\n\n\n");

            }
        });
    }

    private void addThisMenu() {

        String mName = nameInput.getText().toString().trim();
        int mPrice = Integer.parseInt(priceInput.getText().toString().trim());
        String mStatus;
        String mType;
        String mCat = categoryInput.getSelectedItem().toString().trim();
        int mDura =Integer.parseInt( durationInput.getText().toString().trim());


        String mUrlImage = "";
        if (imageUrl != null) mUrlImage = imageUrl;

        if(toggle.isChecked()){
            mStatus =  "AVAILABLE";
        }else{
            mStatus =  "SOLD_OUT";
        }

        if(alacarteRadio.isChecked()){
            mType = "ALACARTE";

        } else if (combiBaseRadio.isChecked()){
            mType = "COMBINATION_BASE";

        } else if (combiMainRadio.isChecked()){
            mType = "COMBINATION_MAIN";

        } else {
            mType = "COMBINATION_MAIN"; //Just for Default
        }


        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<Integer> call = jsonPlaceHolderApi.addMenuV2(vendor_id,mName,mPrice,mStatus,mType,mUrlImage,mCat,mDura);
        System.out.println("\n\n\n\n********************"+ vendor_id+"--"+mName+"--"+mPrice+"--"+mStatus+"--"+mType+"--"+mUrlImage+"--"+mCat+"--"+mDura +"********************\n\n\n\n");


        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {

                if (!response.isSuccessful()) {
                    System.out.println("\n\n\n\n********************"+ "Code: " + response.code() +"********************\n\n\n\n");
                    return;
                }

                Menu menu = new Menu();
                menu.setFoodId(response.body());


            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n********************"+ t.getMessage() +"********************\n\n\n\n");

            }
        });

    }

    private void saveThisMenu(int foodId, Menu menu) {

        menu.setFoodName(nameInput.getText().toString().trim());
        menu.setFoodPrice(Integer.parseInt(priceInput.getText().toString().trim()));
        if(toggle.isChecked()){
            menu.setFoodStatus(toggle.getTextOn().toString());
        }else{
            menu.setFoodStatus(toggle.getTextOff().toString());
        }

        if(alacarteRadio.isChecked()){
            menu.setFoodType("ALACARTE");

        } else if (combiBaseRadio.isChecked()){
            menu.setFoodType("COMBINATION_BASE");

        } else if (combiMainRadio.isChecked()){
            menu.setFoodType("COMBINATION_MAIN");

        }

        menu.setCategoryName(categoryInput.getSelectedItem().toString());
        menu.setPrepareDuration(Integer.parseInt(durationInput.getText().toString().trim()));


        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<Void> call = jsonPlaceHolderApi.editMenuV2(vendor_id, foodId,
                                                        menu.getFoodName(),
                                                        menu.getFoodPrice(),
                                                        menu.getFoodStatus(),
                                                        menu.getFoodType(),
                                                        menu.getFoodImg(),
                                                        menu.getCategoryName(),
                                                        menu.getPrepareDuration());


        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (!response.isSuccessful()) {
                    System.out.println("\n\n\n\n********************"+ "Code: " + response.code() +"********************\n\n\n\n");
                    return;
                }


            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                //vendorProfile.setText(t.getMessage());
                System.out.println("\n\n\n\n********************"+ t.getMessage() +"********************\n\n\n\n");

            }
        });



    }


    private void openFileChooser() {
        if (ContextCompat.checkSelfPermission(AddEditMenuActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            /*Toast.makeText(MainActivity.this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();*/

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);

        } else {
            requestStoragePermission();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { //มีไว้คู่กับ open file chooser เฉยๆ
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            //Picasso.with(this).load(mImageUri).into(mImageView);
            Glide.with(this).load(mImageUri).apply(option).into(uploadImage);
            enableSave();
            uploadFile(); //เริ่มอัพโหลดเมื่อไหร่ก็ใส่ตรงนั้น ในกรณีนี้คือเราให้เลือกไฟล์เสร็จ อัพเลยทันที
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

            progressDialog = new ProgressDialog(AddEditMenuActivity.this);
            progressDialog = ProgressDialog.show(AddEditMenuActivity.this, "",
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
                                    selectedMenu.setFoodImg(imageUrl); //เอาไปเก็บไว้ เตรียมส่งให้ MySQL ด้วย retrofit

                                    Upload upload = new Upload(nameInput.getText().toString().trim(), downloadUri.toString());
                                    //สร้าง class ชื่อ Upload ด้วย

                                    String uploadId = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(uploadId).setValue(upload);
                                    Toast.makeText(AddEditMenuActivity.this, "Upload successful", Toast.LENGTH_LONG).show();

                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) { // อัพไม่สำเร็จ ทำอะไร

                            progressDialog.dismiss();
                            Toast.makeText(AddEditMenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) { //ระหว่างกำลังอัพ ทำอะไร

                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private TextWatcher EditMenuTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            saveMenuButton.setBackgroundResource(R.drawable.pink_round_btn);
            saveMenuButton.setEnabled(true);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(AddEditMenuActivity.this,
                                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }



    //////////////////////////////////////////   Navigation(cont.)   //////////////////////////////////////
    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /*public void goToMenu(){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }*/

    public void goToSalesRecord() {
        Intent intent = new Intent(this, SalesRecordActivity.class);
        startActivity(intent);
    }

    public void goToSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    ////////////////////

    public void goToAddEdit() {
        Intent intent = new Intent(this, AddEditMenuActivity.class);
        startActivity(intent);
    }

    public void hideKb(View view){ //For hiding soft keyboard when tap outside
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    private void enableSave(){
        saveMenuButton.setEnabled(true);
        saveMenuButton.setBackgroundResource(R.drawable.pink_round_btn);
    }

    private void disableSave(){
        saveMenuButton.setBackgroundResource(R.drawable.button_grey_rounded);
        saveMenuButton.setEnabled(false);
    }

    private TextWatcher inputTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            disableSave();

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            enableSave();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
