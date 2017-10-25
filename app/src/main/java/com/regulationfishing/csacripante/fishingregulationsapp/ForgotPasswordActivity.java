package com.regulationfishing.csacripante.fishingregulationsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import static prefs.CommonFunctions.PassWordMd5;
import static prefs.CommonFunctions.validate;
import static prefs.CommonFunctions.generatePassword;

import org.json.JSONException;
import org.json.JSONObject;

import prefs.PHPRequests;
import prefs.SendEmail;
import prefs.Utils;

import prefs.Utils.*;

public class ForgotPasswordActivity extends AppCompatActivity {
    public static Context context;
    String GeneratedPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        context = getApplicationContext();



        final EditText etEmail = (EditText)findViewById(R.id.input_frogotEmail);
        final EditText etRecoveryCode = (EditText)findViewById(R.id.input_RecoveryCode);
        final Button SendRecoveryCode  = (Button)findViewById(R.id.btn_sendEmail);
        final Button SendPassword = (Button)findViewById(R.id.btn_sendEmailPassowrd);

        //get random letters up to 7 characters
        GeneratedPassword = generatePassword();
        SendRecoveryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                int isValidated = validate(email.toString());

                switch (isValidated){
                    case 0:
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean error = false;
                                    boolean success = false;
                                    if(jsonResponse.has("error")) {
                                        error  = jsonResponse.getBoolean("error");
                                    }
                                    if(jsonResponse.has("success")) {
                                        success = jsonResponse.getBoolean("success");
                                    }
                                    if (success) {
                                        JSONObject user = jsonResponse.getJSONObject("user");
                                        String email = user.getString("email");

                                        SendRecoveryEmail(email);

                                        Toast.makeText(getApplicationContext(),
                                                "A recovery code has been send to your email!", Toast.LENGTH_LONG).show();

   /*                                   Intent intent = new Intent(ForgotPasswordActivity.this, HomeActivity.class);
                                        ForgotPasswordActivity.this.startActivity(intent);*/
                                    } else if(error){
                                        String errorMsg = jsonResponse.getString("error_msg");
                                        if(errorMsg.contains("email")){
                                            etEmail.setError(errorMsg);
                                        }else if(errorMsg.contains("Recovery")){
                                            etRecoveryCode.setError(errorMsg);
                                        }

                                        Toast.makeText(getApplicationContext(),
                                                errorMsg, Toast.LENGTH_LONG).show();
                                    }else {

                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        finish();
                                                        Intent intent = new Intent(ForgotPasswordActivity.this, SignupActivity.class);
                                                        ForgotPasswordActivity.this.startActivity(intent);
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        //No button clicked
                                                        break;
                                                }
                                            }
                                        };
                                        AlertDialog.Builder ab = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                        ab.setMessage("Email doe snot exist. Would you like to sign up?").setPositiveButton("Yes", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        PHPRequests registerRequest = new PHPRequests(email,"",GeneratedPassword, Utils.RECOVERY_REQUEST_URL,responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                        queue.add(registerRequest);
                        break;
                    case 1:
                            etEmail.setError("Email must a valid address and not empty.");
                        break;
                    default:
                        break;
                }
            }
        });
        //update password recovery
        SendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = etEmail.getText().toString();
                final String recoveryCode = etRecoveryCode.getText().toString();
                int isValidated = validate(email.toString());
                GeneratedPassword = generatePassword();
                String GeneratedPasswordMd5 = PassWordMd5(GeneratedPassword);
                switch (isValidated){
                    case 0:
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success) {

                                        JSONObject user = jsonResponse.getJSONObject("user");
                                        String email = user.getString("email");

                                        SendPasswordEmail(email,GeneratedPassword);

                                        Toast.makeText(getApplicationContext(),
                                                "A new password has been sent to your email!", Toast.LENGTH_LONG).show();

/*                                      Intent intent = new Intent(ForgotPasswordActivity.this, HomeActivity.class);
                                        ForgotPasswordActivity.this.startActivity(intent);*/
                                    } else {

                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which){
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        finish();
                                                        Intent intent = new Intent(ForgotPasswordActivity.this, SignupActivity.class);
                                                        ForgotPasswordActivity.this.startActivity(intent);
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        //No button clicked
                                                        break;
                                                }
                                            }
                                        };
                                        AlertDialog.Builder ab = new AlertDialog.Builder(ForgotPasswordActivity.this);
                                        ab.setMessage("Email doe snot exist. Would you like to sign up?").setPositiveButton("Yes", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        PHPRequests registerRequest = new PHPRequests(email,recoveryCode,GeneratedPasswordMd5,Utils.PASSWORD_UPDATE_REQUEST_URL,responseListener);
                        RequestQueue queue = Volley.newRequestQueue(ForgotPasswordActivity.this);
                        queue.add(registerRequest);
                        break;
                    case 1:
                        etEmail.setError("Email must a valid address and not empty.");
                        break;
                    default:
                        break;
                }
            }
        });
    }
    public void SendRecoveryEmail(String email){


        //Sends Email to user.
        String subject = "Fishing App Password Recovery";
        String message = "Your recovery code is: " + GeneratedPassword ;

        //Creating SendMail object
        SendEmail sm = new SendEmail(this, email, subject, message);

        sm.execute();


    }
    public void SendPasswordEmail(String email,String password){


        //Sends Email to user.
        String subject = "Fishing App Password Recovery";
        String message = "Your new password is: " + password;

        //Creating SendMail object
        SendEmail sm = new SendEmail(this, email, subject, message);

        sm.execute();


    }
}
