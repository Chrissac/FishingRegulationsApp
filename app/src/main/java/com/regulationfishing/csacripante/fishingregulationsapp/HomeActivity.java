package com.regulationfishing.csacripante.fishingregulationsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static prefs.CommonFunctions.PassWordMd5;
import static prefs.CommonFunctions.generatePassword;
import static prefs.CommonFunctions.validate;

import info.android.sqlite.helper.DatabaseHelper;
import info.android.sqlite.helper.users;
import prefs.LoginRequest;

import prefs.RegisterRequest;
import prefs.UserInfo;
import prefs.UserSession;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_SIGNUP = 0;

    private EditText email, password;
    private Button login;
    private TextView signup;
    private ProgressDialog progressDialog;
    private UserSession session;
    private UserInfo userInfo ;

    DatabaseHelper db;

    private static final String TAG = "FacebookLogin";

    // [START declare_auth]
    private FirebaseAuth mAuth;

    // [END declare_auth]

    private CallbackManager mCallbackManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_home);
        FirebaseApp.initializeApp(this);
        db = new DatabaseHelper(getApplicationContext());
        db.onCreate(db.getWritableDatabase());
        //db.onUpgrade(db.getWritableDatabase(),2,3);
/*        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.regulationfishing.csacripante.fishingregulationsapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }*/
         userInfo = new UserInfo(getApplicationContext());

        // binds buttons and text to unchaneable variables.
        final EditText etEmail = (EditText)findViewById(R.id.input_email);
        final EditText etPassword = (EditText)findViewById(R.id.input_password);
        final Button login  = (Button)findViewById(R.id.btn_login);
        final TextView signup = (TextView)findViewById(R.id.link_signup);
        final TextView forgotPassword = (TextView)findViewById(R.id.lnk_Forgot_passowrd);
        final LoginButton  facebookLoginButton = (LoginButton)findViewById(R.id.btn_facebookLogin);

        //on click listener for the sign up button -> opens sign up activity
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(HomeActivity.this, SignupActivity.class);
                HomeActivity.this.startActivity(registerIntent);
            }
        });

        //on click listener for login button. allows authentication for database.
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = etEmail.getText().toString();
                String password = null;

                //checks to see if email and password are in the valid formats
                int isVald = validate(etEmail.getText().toString(),etPassword.getText().toString());

                //if it is valid: 0 then we do and do a database check for email and passwor    d
                switch(isVald){
                    case 0:
                        //converts strong to the hashset for checking database password against itself.
                        password = PassWordMd5(etPassword.getText().toString());
                        // Response received from the server
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    //gets the response back from the server
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean error = false;
                                    boolean success = false;
                                    if(jsonResponse.has("error")) {
                                        error  = jsonResponse.getBoolean("error");
                                    }
                                    if(jsonResponse.has("success")) {
                                        success = jsonResponse.getBoolean("success");
                                    }
                                    if (success) {;

                                        JSONObject user = jsonResponse.getJSONObject("user");
                                        String email = user.getString("email");
                                        String name = user.getString("username");
                                        String password = user.getString("password");


                                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                                        intent.putExtra("email", email);
                                        intent.putExtra("username", name);
                                        HomeActivity.this.startActivity(intent);
                                    }else if(error){
                                        String errorMsg = jsonResponse.getString("error_msg");
                                        if(errorMsg.contains("Account")){
                                            etEmail.setError(errorMsg);
                                        }else if(errorMsg.contains("Password")){
                                            etPassword.setError(errorMsg);
                                        }

                                    }
                                    else {
                                        //if authentication fails we send this error back to the user.
                                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                        builder.setMessage("Email or password does not match")
                                                .setNegativeButton("Retry", null)
                                                .create()
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };

                        LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                        queue.add(loginRequest);
                        break;
                    case 1:
                        etEmail.setError("Email must a valid address and not empty.");
                        return;
                    case 2:
                        etPassword.setError("Password not be empty and between 4 and 10 characters.");
                        return;
                    default:
                        break;
                }
            }
        });

        //on click listener for the sign up button -> opens sign up activity
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(HomeActivity.this, ForgotPasswordActivity.class);
                HomeActivity.this.startActivity(registerIntent);
            }
        });

        //Set up authentication for facebook login.
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginFacebookButton = (LoginButton) findViewById(R.id.btn_facebookLogin);
        loginFacebookButton.setReadPermissions("public_profile", "email");
        loginFacebookButton.setReadPermissions("email", "public_profile");
        loginFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override

            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
                  GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject,
                                                    GraphResponse response) {
                                Bundle facebookData = getFacebookData(jsonObject);
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
                                            if(jsonResponse.has("success") && jsonResponse.has("userexistsfb")) {
                                                success = jsonResponse.getBoolean("success");
                                                //add user to sqlLiteDatabase

                                                JSONObject user = jsonResponse.getJSONObject("user");
                                                int id = user.getInt("id");
                                                String email = user.getString("email");
                                                String username = user.getString("username");
                                                String password = user.getString("password");

                                                users newUser = new users(username, email,password,id);
                                                long user_id = db.createUser(newUser);
                                                db.closeDB();

                                                //we start a new intent to go to the main activity where we bind the returned results.
                                                Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                                                HomeActivity.this.startActivity(intent);
                                            }
                                            else if(jsonResponse.has("success")) {
                                                success = jsonResponse.getBoolean("success");
                                                //add user to sqlLiteDatabase

                                                JSONObject user = jsonResponse.getJSONObject("user");
                                                int id = user.getInt("id");
                                                String email = user.getString("email");
                                                String username = user.getString("username");
                                                String password = user.getString("password");

                                                users newUser = new users(username, email,password,id);
                                                long user_id = db.createUser(newUser);
                                                db.closeDB();

                                                //we start a new intent to go to the main activity where we bind the returned results.
                                                Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                                                intent.putExtra("email", email);
                                                intent.putExtra("username", username);
                                                HomeActivity.this.startActivity(intent);
                                            }
                                            else if(error){
                                                String errorMsg = jsonResponse.getString("error_msg");
                                                if(errorMsg.contains("email")){


                                                }else if(errorMsg.contains("user name")){
                                                    Toast.makeText(getApplicationContext(),
                                                            "User name already exists.", Toast.LENGTH_LONG).show();
                                                }

                                                Toast.makeText(getApplicationContext(),
                                                        errorMsg, Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
                                RegisterRequest registerRequest = new RegisterRequest(userInfo.getKeyUsername(), userInfo.getKeyEmail(), userInfo.getKeypassowrd(),"FB", responseListener);
                                RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
                                queue.add(registerRequest);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
                //Insert User into PHPMyAdmin

                userInfo.setPassword(PassWordMd5(generatePassword()));


                //Insert the same user into SQLlite Database.
            }


            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }


    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }

                }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String test = e.toString();
            }
        });


    }
    // [END auth_with_facebook]

    public void signOut() {
        mAuth.signOut();
        LoginManager.getInstance().logOut();

        updateUI(null);
    }

    public void updateUI(FirebaseUser user) {
        //Set up a new user within PhpMyadmin


        List<users> allUsers = db.getAllUsers();
        for (users userObj : allUsers) {
            Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
            intent.putExtra("email", userObj.getEmail());
            intent.putExtra("username", userObj.getUserName());
            HomeActivity.this.startActivity(intent);
        }

        if(user!=null) {
            String test = user.getEmail();

        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }

    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            String userName = "";
            //Get the sign in information from facebook.
            bundle.putString("idFacebook", id);
            if (object.has("first_name")) {
                bundle.putString("first_name", object.getString("first_name"));
                userName = object.getString("first_name");
            }else {
                bundle.putString("first_name", id);
            }
            if (object.has("last_name")){
                bundle.putString("last_name", object.getString("last_name"));
                userName += " " + object.getString("last_name");
            }
            if (object.has("email")) {
                bundle.putString("email", object.getString("email"));
            }else{
                bundle.putString("email", id + "@FishingRegulations.com");
            }

            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));

            userInfo.setLoginType("FB");
            userInfo.setEmail(bundle.getString("email"));
            if(userName==""){

            }else {
                userName += "-" + id.toString();
            }
            userInfo.setUsername(userName);

        } catch (Exception e) {
            Log.d(TAG, "BUNDLE Exception : "+e.toString());
        }
        return bundle;
    }
}