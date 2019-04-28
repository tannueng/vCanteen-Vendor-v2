package com.example.vcanteenvendor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.regex.Pattern;

import javax.crypto.spec.IvParameterSpec;

public class SignupPasswordPage extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^[a-zA-Z0-9_@#&-*'()\"](?=\\S+$).{7,}$");

    EditText passwordBox;
    EditText confirmPasswordBox;
    TextView passwordError;
    TextView confirmPasswordError;
    Button nextButton;

    private String password;
    private String confirmPassword;

    //From Intent
    private String email;
    private String acccountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_password_page);

        passwordBox = findViewById(R.id.passwordBox);
        confirmPasswordBox = findViewById(R.id.confirmPasswordBox);
        passwordError = findViewById(R.id.passwordError);
        confirmPasswordError = findViewById(R.id.confirmPasswordError);
        nextButton = findViewById(R.id.nextButton);

        email = getIntent().getStringExtra("input_email");  //Can came from either Login or Sign up, depends on account type.
        acccountType = getIntent().getStringExtra("accountType");

        passwordBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordError.setVisibility(View.INVISIBLE);
            }
        });

        confirmPasswordBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPasswordError.setVisibility(View.INVISIBLE);
            }
        });

        confirmPasswordBox.setOnEditorActionListener(editorListener);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = passwordBox.getText().toString().trim();
                confirmPassword = confirmPasswordBox.getText().toString().trim();

                System.out.println(" ==================== Password EditText:: "+password+" ==================== ");
                System.out.println(" ==================== Confirm EditText:: "+confirmPassword+" ==================== ");

                if(password.isEmpty()){
                    passwordError.setText("Please enter your password.");
                    passwordError.setVisibility(View.VISIBLE);

                } else if(confirmPassword.isEmpty()){
                    confirmPasswordError.setText("Please confirm your password.");
                    confirmPasswordError.setVisibility(View.VISIBLE);

                } else if(!(PASSWORD_PATTERN.matcher(password).matches())){
                    passwordError.setText("Password must be at least 8 characters and \ncan only contain a-z A-Z 0-9 _ - * ‘ “ # & () @.");
                    passwordError.setVisibility(View.VISIBLE);

                } else if(!password.equals(confirmPassword)){
                    confirmPasswordError.setText("Password did not match. Please try again.");
                    confirmPasswordError.setVisibility(View.VISIBLE);

                } else{
                    passwordError.setVisibility(View.INVISIBLE);
                    confirmPasswordError.setVisibility(View.INVISIBLE);

                    System.out.println(" ==================== Plain Password :: "+password+" ==================== ");
                    password = new String(Hex.encodeHex(DigestUtils.sha256(password)));
                    System.out.println(" ==================== Hashed Password :: "+password+" ==================== ");

                    Intent i = new Intent(SignupPasswordPage.this, SignupInfoPage.class);
                    i.putExtra("input_password",password);
                    i.putExtra("input_email",email);
                    i.putExtra("accountType",acccountType);
                    startActivity(i);

                }
            }
        });
    }


    // Do the same things as OnClick of Next Button, but with hitting enter on soft keyboard
    private TextView.OnEditorActionListener editorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if(actionId == EditorInfo.IME_ACTION_DONE){
                password = passwordBox.getText().toString().trim();
                confirmPassword = confirmPasswordBox.getText().toString().trim();

                System.out.println(" ==================== Password EditText:: "+password+" ==================== ");
                System.out.println(" ==================== Confirm EditText:: "+confirmPassword+" ==================== ");

                if(password.isEmpty()){
                    passwordError.setText("Please enter your password.");
                    passwordError.setVisibility(View.VISIBLE);

                } else if(confirmPassword.isEmpty()){
                    confirmPasswordError.setText("Please confirm your password.");
                    confirmPasswordError.setVisibility(View.VISIBLE);

                } else if(!(PASSWORD_PATTERN.matcher(password).matches())){
                    passwordError.setText("Password must be at least 8 characters and \ncan only contain a-z A-Z 0-9 _ - * ‘ “ # & () @.");
                    passwordError.setVisibility(View.VISIBLE);

                } else if(!password.equals(confirmPassword)){
                    confirmPasswordError.setText("Password did not match. Please try again.");
                    confirmPasswordError.setVisibility(View.VISIBLE);

                } else{
                    passwordError.setVisibility(View.INVISIBLE);
                    confirmPasswordError.setVisibility(View.INVISIBLE);

                    System.out.println(" ==================== Plain Password :: "+password+" ==================== ");
                    password = new String(Hex.encodeHex(DigestUtils.sha256(password)));
                    System.out.println(" ==================== Hashed Password :: "+password+" ==================== ");

                    Intent i = new Intent(SignupPasswordPage.this, SignupInfoPage.class);
                    i.putExtra("input_password",password);
                    i.putExtra("input_email",email);
                    i.putExtra("accountType",acccountType);
                    startActivity(i);

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
