package com.example.loginshoopy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.time.Instant;

import de.hdodenhof.circleimageview.CircleImageView;


public class AboutMe extends AppCompatActivity {
    public static final String GOOGLE_ACCOUNT = "google_account";
    private Button signOut;
    private GoogleSignInClient googleSignInClient;
    private CircleImageView circleImageView;
    TextView nama;
    TextView email;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        nama = (TextView) findViewById(R.id.txtNAma);
        email = (TextView) findViewById(R.id.txtEmail);
        logo = (ImageView) findViewById(R.id.profile_image);

        SharedPreferences mSettings = getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String cookieName = mSettings.getString("datanama", "datanama");
        String cookieEmail = mSettings.getString("dataemail", "dataemail");
        String cookiegambar = mSettings.getString("datagambar", "datagambar");
        Log.d("datagambar", "onCreate: "+cookiegambar);
        //set to UI
        nama.setText(cookieName);
        email.setText(cookieEmail);
//        Glide.with(getApplicationContext())
//                .load(cookiegambar)
//                .centerCrop()
//                .placeholder(R.mipmap.ic_launcher)
//                .into(circleImageView);


        circleImageView = (CircleImageView)findViewById(R.id.profile_image);
        signOut=findViewById(R.id.sign_out);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent=new Intent(AboutMe.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
            }
        });


        this.setTitle("About me");
    }
    private void setDataOnView() {
        GoogleSignInAccount googleSignInAccount = getIntent().getParcelableExtra(GOOGLE_ACCOUNT);

    }
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
