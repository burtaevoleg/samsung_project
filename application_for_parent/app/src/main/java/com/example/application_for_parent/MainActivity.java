package com.example.application_for_parent;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

    final String LOG_TAG = "myLogs";
    SharedPreferences sharedPreferences;
    String url_server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences("preference", MODE_PRIVATE);
        url_server=getString(R.string.url_server);
        if(!sharedPreferences.contains("unique_code")){
            showRegisterWindow(); //окно для регистрации
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent=new Intent(MainActivity.this, DistributeActivity.class);
                    startActivity(intent);
                    finish();
                }
            },5000);

        }
    }

    private void showRegisterWindow() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle("Зарегистрироваться");
        dialog.setMessage("Введите все данные для регистрации");

        LayoutInflater inflater=LayoutInflater.from(this);
        final View register_window=inflater.inflate(R.layout.registret_window,null);
        dialog.setView(register_window);

        final MaterialEditText child_phone=register_window.findViewById(R.id.child_phone);
        final MaterialEditText parent_phone=register_window.findViewById(R.id.parent_phone);
        final TextView exception=(TextView) register_window.findViewById(R.id.exception);
        Button register=(Button) register_window.findViewById(R.id.register);

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.register:
                        if(TextUtils.isEmpty(parent_phone.getText().toString()) || TextUtils.isEmpty(child_phone.getText().toString())){
                            exception.setTextColor(Color.WHITE);
                            exception.setBackgroundColor(Color.parseColor("#ABCDEF"));
                            exception.setText("Пожалуйста, введите все данные");
                            return;
                        }
                        String Parent_phone=parent_phone.getText().toString();
                        String Child_phone=child_phone.getText().toString();
                        String unique_code= generateUniqueCode();
                        new MyAsyncTask(Parent_phone,Child_phone,unique_code).execute("");
                        showUniqueCode(unique_code);
                        dialog.dismiss();
                        break;
                }
            }
        };
        register.setOnClickListener(listener);
        dialog.show();
    }


    private void showUniqueCode(String unique_code) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);
        View show = inflater.inflate(R.layout.show_unique_code,null);
        TextView textView=(TextView)show.findViewById(R.id.show_code);
        Button next=(Button)show.findViewById(R.id.next);
        textView.setText(unique_code);
        Log.d(LOG_TAG,textView.toString());
        builder.setView(show);
        builder.setTitle("Получение кода");
        builder.show();
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
                Intent intent=new Intent(MainActivity.this, DistributeActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    public String generateUniqueCode() {
        int size=6;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"+ "0123456789";
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG,"destroy main");
    }



    class MyAsyncTask extends AsyncTask<String, String, String> {

        String child_phone,parent_phone,unique_code;
        int parent_id;

        public MyAsyncTask(String parent_phone, String child_phone,String unique_code) {
            this.child_phone=child_phone;
            this.parent_phone=parent_phone;
            this.unique_code=unique_code;
        }

        @Override
        protected String doInBackground(String... params) {
            saveUSER();
            //showUSER();
            //activate_user();
            //getParent_id(unique_code);
            return null;
        }

        private void activate_user() {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("text/plain");

            RequestBody body = RequestBody.create("{\n\t\"parent_phone_number\":\""+parent_phone+"\",\n\t\"chile_phone_number\":\""+child_phone+"\",\n\t\"unique_code\":\""+unique_code+"\"\n}", mediaType);
            Request request = new Request.Builder()
                    .url(url_server+"/users/create")
                    .method("PUT", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            Response response = null;

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String answerHTTP=null;
            if (response != null && response.code() == 200) {
                try {
                    answerHTTP = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        private void getParent_id(String unique_code) {
            String answerHTTP=null;
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url_server+"/users/getuser?unique_code="+unique_code)
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
            else{
                return;
            }
            save_to_share_preference(answerHTTP,unique_code);
            Log.d(LOG_TAG, child_phone+" "+ parent_phone+" "+ parent_id);
        }

        private void save_to_share_preference(String answerHTTP, String unique_code) {

            JsonParser parser = new JsonParser();
            JsonObject jsonObject=parser.parse(answerHTTP).getAsJsonObject();
            parent_id=jsonObject.get("parent_id").getAsInt();

            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt("parent_id",parent_id);
            editor.commit();

        }

        private void showUSER() {
            sharedPreferences=getSharedPreferences("preference", MODE_PRIVATE);
            String s1=sharedPreferences.getString("parent_phone","");
            String s2=sharedPreferences.getString("child_phone","");
            String s3=sharedPreferences.getString("unique_code","");
            Log.d(LOG_TAG,s1+" "+s2+" "+s3);
        }

        private void saveUSER() {
            sharedPreferences=getSharedPreferences("preference", MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("parent_phone",parent_phone);
            editor.putString("child_phone",child_phone);
            editor.putString("unique_code",unique_code);
            editor.commit();

        }

        @Override
        protected void onPostExecute (String result){
            super.onPostExecute(result);
        }
    }

}
