package com.example.application_for_children;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.Polyline;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    final String LOG_TAG = "myLogs";
    SharedPreferences sharedPreferences;
    RelativeLayout relativeLayout;
    String answer = "";
    Button activate;
    String child_phone, parent_phone;
    Integer parent_id;
    String url_server;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relativeLayout=findViewById(R.id.activity_main);
        setContentView(R.layout.activity_main);
        url_server=getString(R.string.url_server);
        sharedPreferences=getSharedPreferences("child",MODE_PRIVATE);
        if(!sharedPreferences.contains("parent_id")) {
            showRegisterWindow();

        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                }
            },5000);
        }





    }
    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        while(true) {
            if ((ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
                finish();
                break;
            }
        }
    }


    private void showRegisterWindow() {
        final AlertDialog dialog=new AlertDialog.Builder(this).create();
        dialog.setTitle("Зарегистрироваться");

        LayoutInflater inflater=LayoutInflater.from(this);
        final View register_window=inflater.inflate(R.layout.register_window,null);
        dialog.setView(register_window);
        activate=(Button)register_window.findViewById(R.id.activate);

        final MaterialEditText unique_code=register_window.findViewById(R.id.unique_code);

        activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(unique_code.getText().toString())){
                    TextView unique_code=(TextView)register_window.findViewById(R.id.unique_code);
                    TextView exception=(TextView)register_window.findViewById(R.id.exception);
                    exception.setTextColor(Color.WHITE);
                    exception.setBackgroundColor(Color.parseColor("#ABCDEF"));
                    exception.setText("Пожалуйста, введите уникальный код");
                    return;
                }
                String Unique_code=unique_code.getText().toString();
                new MyAsyncTask(Unique_code).execute("");

                try {
                    Thread.sleep(500); //Приостанавливает поток на 0.5 секунду
                } catch (Exception e) {
                }

                if(answer.equals("0")){
                    TextView unique_code=(TextView)register_window.findViewById(R.id.unique_code);
                    TextView exception=(TextView)register_window.findViewById(R.id.exception);
                    unique_code.setText("");
                    exception.setTextColor(Color.WHITE);
                    exception.setBackgroundColor(Color.RED);
                    exception.setText("Такого кода не существует.\nПопробуйте ввести код еще раз.");
                    return;
                }
                else {
                    dialog.dismiss();
                    getLocationPermission();

                }
            }
        });
        dialog.show();
    }


    class MyAsyncTask extends AsyncTask<String, String, String> {

        String unique_code;
        String answerHTTP=null;

        public MyAsyncTask(String unique_code) {
            this.unique_code=unique_code;
        }

        @Override
        protected String doInBackground(String... params) {
            activateUnique_code(unique_code);
            if (answerHTTP.equals("1")) {
                getParent_id(unique_code);
            }
            return null;
        }

        private void getParent_id(String unique_code) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url_server+"/users/getuser?unique_code="+unique_code)//"http://192.168.0.175:8080/users/getuser?unique_code="+unique_code)
                    .method("GET", null)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (response != null && response.code() == 200) {
                try {
                    answerHTTP = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            save_to_share_preference(answerHTTP,unique_code);
            Log.d(LOG_TAG, child_phone+" "+ parent_phone+" "+ parent_id);
        }

        private void save_to_share_preference(String answerHTTP, String unique_code) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject=parser.parse(answerHTTP).getAsJsonObject();
            child_phone=jsonObject.get("child_phone").getAsString();
            parent_phone=jsonObject.get("parent_phone").getAsString();
            parent_id=jsonObject.get("parent_id").getAsInt();

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("unique_code", unique_code);
            editor.putString("child_phone",child_phone);
            editor.putString("parent_phone",parent_phone);
            editor.putInt("parent_id",parent_id);
            editor.commit();

        }

        private void activateUnique_code(String unique_code) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("text/plain");

            RequestBody body = RequestBody.create( "{\n\t\"unique_code\":\"" + unique_code + "\"\n}", mediaType);
            Request request = new Request.Builder()
                    .url(url_server+"/users/activate")//"http://192.168.0.175:8080/users/activate")
                    .method("PUT", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            Response response = null;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (response != null && response.code() == 200) {
                try {
                    answerHTTP = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            answer=answerHTTP;
        }

        private void checkAnswer(String answerHTTP) {
            if (answerHTTP.equals("0")){
                showRegisterWindow();
            }
        }

        private void showUSER() {
            String s1=sharedPreferences.getString("parent_phone","");
            String s2=sharedPreferences.getString("child_phone","");
            String s3=sharedPreferences.getString("unique_code","");
            Log.d(LOG_TAG,s1+" "+s2+" "+s3);
        }

        @Override
        protected void onPostExecute (String result){
            super.onPostExecute(result);
            answer=answerHTTP;
        }

    }
}