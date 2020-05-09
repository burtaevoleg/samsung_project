package com.example.application_for_children;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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
    RelativeLayout relativeLayout;
    String answer = "";
    Button activate;
    String child_phone, parent_phone;
    Integer parent_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        relativeLayout=findViewById(R.id.activity_main);
        showRegisterWindow();
        setContentView(R.layout.activity_main);
        sharedPreferences=getSharedPreferences("preference",MODE_PRIVATE);

        if (!isMyServiceRunning(MyService.class) && sharedPreferences.contains("parent_id")) {
            Log.d(LOG_TAG, "end Service");
            Intent intent=new Intent(MainActivity.this, MyService.class).setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
            int parent_id=sharedPreferences.getInt("parent_id",0);
            intent.putExtra("parent_id",parent_id);
            startService(intent);

        }

    }

    private void showRegisterWindow() {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Зарегестрироваться");
        //dialog.setMessage("введите все данные для регистрации");

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
                    Thread.sleep(500); //Приостанавливает поток на 1 секунду
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

                    dialog.setCancelable(true);
                }
            }
        });
        dialog.show();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
                    .url("http://192.168.0.175:8080/users/getuser?unique_code="+unique_code)//+unique_code)
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

            //sharedPreferences=getPreferences(MODE_PRIVATE);
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
                    .url("http://192.168.0.175:8080/users/activate")
                    .method("PUT", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            Response response = null;// client.newCall(request).execute();

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
            //Log.d(LOG_TAG,answerHTTP);
        }

        private void checkAnswer(String answerHTTP) {
            if (answerHTTP.equals("0")){
                showRegisterWindow();
                //Toast.makeText(MainActivity.this,"Проблемы с запросом", Toast.LENGTH_LONG);
            }
        }

        private void showUSER() {

            //sharedPreferences=getPreferences(MODE_PRIVATE);
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