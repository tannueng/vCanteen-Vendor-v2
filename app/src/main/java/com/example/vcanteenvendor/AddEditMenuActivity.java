package com.example.vcanteenvendor;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddEditMenuActivity extends AppCompatActivity {

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
    EditText priceInput;
    Spinner categoryInput;

    RequestOptions option = new RequestOptions().centerCrop();

    Menu selectedMenu;


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
        categoryInput = findViewById(R.id.categoryInput);



        //////////////////////////////////////////   Retrieve every info from menu   //////////////////////////////////////

        nameInput.setText(getIntent().getStringExtra("foodName"));
        priceInput.setText(String.valueOf(getIntent().getIntExtra("price",0)));

        if(getIntent().getStringExtra("foodImageUrl") != null)
        Glide.with(this).load(getIntent().getStringExtra("foodImageUrl")).apply(option).into(uploadImage);





        //////////////////////////////////////////   Retrieve every info from menu   //////////////////////////////////////

        final String foodName = getIntent().getStringExtra("foodName");
        int foodPrice = getIntent().getIntExtra("price",0);
        final int foodId = getIntent().getIntExtra("foodId",0);
        String foodImg = getIntent().getStringExtra("foodImage");
        String foodStatus = getIntent().getStringExtra("foodStatus");
        String foodType = getIntent().getStringExtra("foodType");

        selectedMenu = new Menu(foodId,foodName,foodPrice,foodStatus,foodImg,foodType);


        nameInput.setText(foodName);
        priceInput.setText(String.valueOf(foodPrice));

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

        }




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
                content.setText("\"insert menu name here\"");
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

                        Toast.makeText(getApplicationContext(), "Deleting new menu...",  Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "Please insert name",  Toast.LENGTH_SHORT).show();
                    /*nameInputLayout.setErrorEnabled(true);
                    nameInputLayout.setError("name");*/
                } else {

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



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryInput.setAdapter(adapter);




    }

    private void deleteThisMenu() {

        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<Void> call = jsonPlaceHolderApi.deleteMenu(1, selectedMenu.getFoodId());


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

        String mName = nameInput.getText().toString();
        int mPrice = Integer.parseInt(priceInput.getText().toString());
        String mStatus;
        String mType;

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

        Call<Integer> call = jsonPlaceHolderApi.addMenu(1,mName,mPrice,mStatus,mType,"");


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

        menu.setFoodName(nameInput.getText().toString());
        menu.setFoodPrice(Integer.parseInt(priceInput.getText().toString()));
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


        String url="https://vcanteen.herokuapp.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Call<Void> call = jsonPlaceHolderApi.editMenu(1, foodId,
                                                        menu.getFoodName(),
                                                        menu.getFoodPrice(),
                                                        menu.getFoodStatus(),
                                                        menu.getFoodType(),
                                                        menu.getFoodImg()); //SET LOGIC TO INSERT ID HERE


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
}
