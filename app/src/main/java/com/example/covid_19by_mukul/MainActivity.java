package com.example.covid_19by_mukul;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    EditText edttxt1,edttxt2;
    Button btn1;
    TextView txtview;
    TextToSpeech t1;
//https://api.lyrics.ovh/v1/Rihanna/Diamonds#
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edttxt1 = findViewById(R.id.edttxt1);
        edttxt2 = findViewById(R.id.edttxt2);
        txtview = findViewById(R.id.txtview);
        btn1  = findViewById(R.id.btn);

        initTextToSpeech();






        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "good work", Toast.LENGTH_SHORT).show();
                ttsSPeak("Lyrics on the way");
                Toasty.info(MainActivity.this, "Lyrics on the way", Toast.LENGTH_SHORT,true).show();


                String url = "https://api.lyrics.ovh/v1/"+edttxt1.getText().toString() + "/" + edttxt2.getText().toString();
                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            txtview.setText(response.getString("lyrics"));
                            ttsSPeak("Lyrics loaded.");


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
        });

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
            Toasty.info(MainActivity.this, "Isme tera ghata!!", Toast.LENGTH_LONG).show();
        }
        if (item.getItemId() == R.id.mukulg){
            Intent intent1 = new Intent(MainActivity.this,MainActivity2.class);
            startActivity(intent1);
            Toasty.info(MainActivity.this, "Welcome", Toast.LENGTH_LONG).show();
            return true;

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
}