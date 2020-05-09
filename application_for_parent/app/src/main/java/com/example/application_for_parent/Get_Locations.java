package com.example.application_for_parent;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Get_Locations extends AppCompatActivity {

    TextView out;
    Button get_location;
    String answerHTTP;
    final String LOG_TAG = "myLogs";
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get__locations);
        out=(TextView)findViewById(R.id.textView);
        get_location=(Button)findViewById(R.id.button);
        dbHelper= new DBHelper(this);

        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.button:
                        new MyAsyncTask().execute("");
                        break;
                }

            }
        };
        get_location.setOnClickListener(listener);
    }

    class MyAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("text/plain");
            Request request = new Request.Builder()
                    .url("http://192.168.0.175:8080/location/getlocations?idperson=6")
                    .method("GET", null)
                    .build();
            Response response =null;
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


            JsonParser parser=new JsonParser();
            JsonArray jsonArray=parser.parse(answerHTTP).getAsJsonArray();

            SQLiteDatabase database=dbHelper.getWritableDatabase();
            ContentValues contentValues=new ContentValues();
            for (int i=0;i<jsonArray.size();i++){
                JsonObject jsonObject=jsonArray.get(i).getAsJsonObject();
                String latitude=jsonObject.get("latitude").getAsString();
                String longitude=jsonObject.get("longitude").getAsString();
                String time=jsonObject.get("time").getAsString();

                contentValues.put(DBHelper.KEY_Lat,latitude);
                contentValues.put(DBHelper.KEY_Lon,longitude);
                database.insert(DBHelper.TABLE_CONTACTS,null,contentValues);
                Log.d(LOG_TAG,latitude+"  "+longitude+"  "+time);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Log.d(LOG_TAG,answerHTTP);
            out.setText(answerHTTP);
        }

    }

}