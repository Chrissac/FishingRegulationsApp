package com.regulationfishing.csacripante.fishingregulationsapp;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.helper.users;
import prefs.RegisterRequest;
import prefs.UserInfo;
import prefs.UserSession;

import static prefs.CommonFunctions.PassWordMd5;


public class SignupActivity extends AppCompatActivity {
    private String TAG = SignupActivity.class.getSimpleName();
    private EditText username, email, password;
    private Button signup;
    private ProgressDialog progressDialog;
    private UserSession session;
    private UserInfo userInfo;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        db = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db1;
        //db.onUpgrade(db.getWritableDatabase(),1,2);

        final EditText etUsername     = (EditText)findViewById(R.id.input_name);
        final EditText etEmail         = (EditText)findViewById(R.id.input_email);
        final EditText etPassword       = (EditText)findViewById(R.id.input_password);
        final Button signup          = (Button)findViewById(R.id.btn_signup);
        final TextView login = (TextView)findViewById(R.id.link_login);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = etUsername.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = PassWordMd5(etPassword.getText().toString());


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
                                //Bind to Database
                                success = jsonResponse.getBoolean("success");

                            }

                            if (success) {
                                users newUser = new users(username, email,password,0);
                                long user_id = db.createUser(newUser);
                                db.closeDB();


                                Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                                SignupActivity.this.startActivity(intent);
                            }
                            else if(error){
                                String errorMsg = jsonResponse.getString("error_msg");
                                if(errorMsg.contains("email")){
                                    etEmail.setError(errorMsg);
                                }else if(errorMsg.contains("user name")){
                                    etUsername.setError(errorMsg);
                                }

                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setMessage("Register Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(username, email, password,"", responseListener);
                RequestQueue queue = Volley.newRequestQueue(SignupActivity.this);
                queue.add(registerRequest);
            }
        });
        //on click listener for the sign up button -> opens sign up activity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(SignupActivity.this, HomeActivity.class);
                SignupActivity.this.startActivity(registerIntent);
            }
        });

    }
}