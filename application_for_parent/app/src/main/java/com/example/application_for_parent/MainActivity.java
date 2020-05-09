package com.example.application_for_parent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button locationCoordinates;
    Button mainService;
    Button Google_Maps;
    Button get_locations;
    final String LOG_TAG = "myLogs";
    RelativeLayout root;
    SharedPreferences sharedPreferences;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //root=findViewById(R.id.activity_main);

        setContentView(R.layout.activity_main);
        locationCoordinates=(Button)findViewById(R.id.coordinates);
        mainService=(Button)findViewById(R.id.service);
        Google_Maps=(Button)findViewById(R.id.google_map);
        get_locations=(Button)findViewById(R.id.get_locations);
        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_massage); //начально выбраная кнопка (при включении приложения)

       // showRegisterWindow();


        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                switch (v.getId()){
                    case  R.id.coordinates:
                        intent=new Intent(MainActivity.this,Coordinates.class);
                        startActivity(intent);
                        break;
                    case  R.id.service:
                        intent=new Intent(MainActivity.this,MainService.class);
                        startActivity(intent);
                        break;
                    case R.id.google_map:
                        intent=new Intent(MainActivity.this,Google_map.class);
                        startActivity(intent);
                        break;
                    case R.id.get_locations:
                        intent=new Intent(MainActivity.this,Get_Locations.class);
                        startActivity(intent);
                        break;
                }
            }
        };

        locationCoordinates.setOnClickListener(listener);
        mainService.setOnClickListener(listener);
        Google_Maps.setOnClickListener(listener);
        get_locations.setOnClickListener(listener);

    }






    private void showRegisterWindow() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Зарегестрироваться");
        dialog.setMessage("введите все данные для регистрации");

        LayoutInflater inflater=LayoutInflater.from(this);
        View register_window=inflater.inflate(R.layout.registret_window,null);
        dialog.setView(register_window);

        final MaterialEditText child_phone=register_window.findViewById(R.id.child_phone);
        final MaterialEditText parent_phone=register_window.findViewById(R.id.parent_phone);

        dialog.setNegativeButton("Отменить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(TextUtils.isEmpty(child_phone.getText().toString())){
                    Snackbar.make(root,"Введите номер телефона ребенка",Snackbar.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(parent_phone.getText().toString())){
                    Snackbar.make(root,"Введите свой номер телефона",Snackbar.LENGTH_LONG).show();
                    return;
                }
                String Parent_phone=parent_phone.getText().toString();
                String Child_phone=child_phone.getText().toString();
                String unique_code=getAlphaNumericString();
                new MyAsyncTask(Parent_phone,Child_phone,unique_code).execute("");
                showUniqueCode(unique_code);
            }
        });
        dialog.show();
    }

    private void showUniqueCode(String unique_code) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LayoutInflater inflater=LayoutInflater.from(this);

        View show = inflater.inflate(R.layout.show_unique_code,null);
        TextView textView=(TextView)show.findViewById(R.id.show_code);

        textView.setText(unique_code);
        Log.d(LOG_TAG,textView.toString());
        builder.setView(show);
        builder.setTitle("Получение кода");
        //dialog.setMessage("введите все данные для регистрации");
        //builder.setView(R.layout.show_unique_code);

        builder.setPositiveButton("Дальше",null);
        builder.show();


    }

    public String getAlphaNumericString()
    {
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


        public MyAsyncTask(String parent_phone, String child_phone,String unique_code) {
            this.child_phone=child_phone;
            this.parent_phone=parent_phone;
            this.unique_code=unique_code;
        }

        @Override
        protected String doInBackground(String... params) {
            saveUSER();
            //showUSER();
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("text/plain");

            RequestBody body = RequestBody.create("{\n\t\"parent_phone_number\":\""+parent_phone+"\",\n\t\"chile_phone_number\":\""+child_phone+"\",\n\t\"unique_code\":\""+unique_code+"\"\n}", mediaType);
            Request request = new Request.Builder()
                    .url("http://192.168.0.175:8080/users/create")
                    .method("PUT", body)
                    .addHeader("Content-Type", "text/plain")
                    .build();
            Response response = null;// client.newCall(request).execute();


            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                //  Toast.makeText(getApplicationContext(),"Проблемы с запросом", Toast.LENGTH_LONG);
            }
            String answerHTTP="123";
            if (response != null && response.code() == 200) {
                try {
                    answerHTTP = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Log.d(LOG_TAG,answerHTTP);
            return null;
        }

        private void showUSER() {
            sharedPreferences=getPreferences(MODE_PRIVATE);
            String s1=sharedPreferences.getString("parent_phone","");
            String s2=sharedPreferences.getString("child_phone","");
            String s3=sharedPreferences.getString("unique_code","");
            Log.d(LOG_TAG,s1+" "+s2+" "+s3);

        }

        private void saveUSER() {
            sharedPreferences=getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putString("parent_phone",parent_phone);
            editor.putString("child_phone",child_phone);
            editor.putString("unique_code",unique_code);
            editor.commit();

        }

        @Override
        protected void onPostExecute (String result){
            super.onPostExecute(result);
            //Log.d(LOG_TAG,result);
            //lastnameF.setText(answerHTTP);
        }

    }

}
