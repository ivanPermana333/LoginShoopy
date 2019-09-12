package com.example.loginshoopy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AndroidClarified";
    GoogleSignInClient GoogleSignInClient;
    private Task completedTask;
    public static final String GOOGLE_ACCOUNT = "google_account";
    LoginButton loginButton;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Shopee");
        setContentView(R.layout.activity_main);

          loginButton = (LoginButton)findViewById(R.id.login_button);
          loginButton.setReadPermissions(Arrays.asList(EMAIL));
          callbackManager = CallbackManager.Factory.create();
          LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                String userDetil = response.getRawResponse();
                                try {
                                    JSONObject jsonObject = new JSONObject(userDetil);
                                    String fbId = jsonObject.getString("id");
                                    String datanama = jsonObject.optString("name", "");
                                    //String username = jsonObject.optString("first_name", "") + jsonObject.optString("last_name", "") + jsonObject.getString("id");
                                    String dataemail = jsonObject.optString("email", "");
                                    String datagambar = "https://graph.facebook.com/" + fbId + "/picture?type=large";
                                    Log.d("gambar", "onCompleted: "+datagambar);

                                    //save to share preference
                                    SharedPreferences mSettings = getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = mSettings.edit();
                                    editor.putString("dataemail", dataemail);
                                    editor.putString("datanama", datanama);
                                    editor.putString("datagambar", datagambar);
                                    editor.apply();

                                    Intent in =new Intent(getApplicationContext(), AboutMe.class);
                                    Toast.makeText(getApplicationContext(),"makanan",Toast.LENGTH_LONG).show();
                                    startActivity(in);

                                } catch (JSONException ignored) {
                                    Toast.makeText(getApplicationContext(), "error : "+ignored.toString(),Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "name,email");
                        graphRequest.setParameters(parameters);
                        graphRequest.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.loginshoopy",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = GoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 100);
            }
        });

    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case 100:
                    try {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                        // The Task returned from this call is always completed, no need to attach
                        // a listener.
//                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        String idToken = account.getIdToken();

                        onLoggedIn(account);
                    } catch (ApiException e) {
                        // The ApiException status code indicates the detailed failure reason.
                        Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                    }
                    break;
            }

    }



    private void onLoggedIn(GoogleSignInAccount googleSignInAccount) {
        Intent intent = new Intent(this, AboutMe.class);
        intent.putExtra(AboutMe.GOOGLE_ACCOUNT, googleSignInAccount);
        startActivity(intent);
        finish();
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String dataemail = account.getEmail();
            String datanama = account.getDisplayName();
            Toast.makeText(getApplicationContext(), "email : " + dataemail, Toast.LENGTH_LONG).show();
            String datagambar;
            if (account.getPhotoUrl() != null) {
                datagambar = account.getPhotoUrl().toString();
            } else {
                datagambar = "avatar";
            }
            //save to share preference
            SharedPreferences mSettings = getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putString("dataemail", dataemail);
            editor.putString("datanama", datanama);
            editor.putString("datagambar", datagambar);
            editor.apply();

            Intent in = new Intent(getApplicationContext(), AboutMe.class);
            startActivity(in);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("makan", "signInResult:failed code=" + e.getStatusCode());
        }

//    public void onStart() {
//        super.onStart();
//        AccessToken accessToken = AccessToken.getCurrentAccessToken();
//
//        if (accessToken != null) {
//            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
//            useLoginInformation(accessToken);
//
//        } else {
//            Log.d(TAG, "Not logged in");
//
//        }
//
//        GoogleSignInAccount alreadyloggedAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (alreadyloggedAccount != null) {
//            Toast.makeText(this, "Already Logged In", Toast.LENGTH_SHORT).show();
//            onLoggedIn(alreadyloggedAccount);
//        } else {
//            Log.d(TAG, "Not logged in");
//        }
//    }



    }

}




