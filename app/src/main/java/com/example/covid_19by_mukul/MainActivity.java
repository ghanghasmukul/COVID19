package com.example.covid_19by_mukul;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telecom.Call;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Arrays;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    EditText edttxt1,edttxt2;
    Button btn1;
    TextView txtview;
    TextToSpeech t1;//
    // Creating Member variables for Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthSteListener;
    public final int RC_SIGN_IN = 1;
    private String username ="";
    private String singerName = "";
    private String songName = "";

//https://api.lyrics.ovh/v1/Rihanna/Diamonds#
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Find Lyrics");

        mFirebaseAuth = FirebaseAuth.getInstance();
        edttxt1 = findViewById(R.id.edttxt1);
        edttxt2 = findViewById(R.id.edttxt2);
        txtview = findViewById(R.id.txtview);
        btn1  = findViewById(R.id.btn);

        initTextToSpeech();






        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "good work", Toast.LENGTH_SHORT).show();
                if(!singerName.equals(edttxt1.getText().toString()) || !songName.equals(edttxt2.getText().toString())) {
                    singerName = edttxt1.getText().toString();
                    songName = edttxt2.getText().toString();
                    loadLyrics(singerName, songName);
                }


            }
        });
        mAuthSteListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                        username = user.getDisplayName();
                    ttsSPeak("Logged in Successfully");
                }
                else{
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.PhoneBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.callBtn) {

            askPermission(Manifest.permission.CALL_PHONE, 1);

            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED ){
                //ttsSPeak("Waah");
            String uri = "tel:8683937456";
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);

        }
            else{
                Toasty.error(this,"K khaj h",Toasty.LENGTH_SHORT,true).show();
              //  ttsSPeak("k khaj hai");

            }
        }

        if (item.getItemId() == R.id.exit){

            finish();
            System.exit(0);

        }
        if (item.getItemId() == R.id.mukulg){
            Intent intent1 = new Intent(MainActivity.this,MainActivity2.class);
            startActivity(intent1);
            Toasty.info(MainActivity.this, "Welcome", Toast.LENGTH_LONG).show();
            return true;

        }
        if (item.getItemId() == R.id.signout) {
            AuthUI.getInstance().signOut(this);
            Toasty.info(MainActivity.this, "Isme tera ghata!!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

public void ttsSPeak(String toSpeak){
    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void initTextToSpeech(){
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }

    private void askPermission(String permissionName, int reqC){
        if(ContextCompat.checkSelfPermission(MainActivity.this, permissionName) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {permissionName}, reqC);
        }
        else{
            ttsSPeak("Already Granted");
        }

    }



    public void Toasty1(int i , String message) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthSteListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthSteListener);
    }

    public void loadLyrics(String singerName, String songName) {
        ttsSPeak("Lyrics on the way");
        Toasty.info(MainActivity.this, "Lyrics on the way", Toast.LENGTH_SHORT,true).show();


        String url = "https://api.lyrics.ovh/v1/"+singerName + "/" + songName;
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    String lyrics = response.getString("lyrics");
                    if(response.getString("lyrics").equals("")){
                        txtview.setText("Lyrics not found!");
                    }
                    else{
                    txtview.setText(response.getString("lyrics"));
                    ttsSPeak("Lyrics loaded.");
                    txtview.setBackgroundColor(Color.parseColor("#404040"));}


                }catch (JSONException e){
                    e.printStackTrace();
                    Toasty.error(MainActivity.this, "lyrics not found", Toasty.LENGTH_LONG, true).show();

                }


            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toasty.error(MainActivity.this, "Lyrics not found", Toasty.LENGTH_LONG, true).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == Activity.RESULT_OK){
            }
            else{
                finish();
            }
        }
    }

}