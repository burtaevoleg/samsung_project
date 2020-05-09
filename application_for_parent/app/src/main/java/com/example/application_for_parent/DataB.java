package com.example.application_for_parent;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class DataB extends AppCompatActivity {

    Button saveB, showB,clearB;
    EditText lon,lat;
    DBHelper dbHelper;
    final String LOG_TAG = "myLogs";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_b);
        saveB=(Button)findViewById(R.id.Save);
        showB=(Button)findViewById(R.id.Show);
        lon=(EditText)findViewById(R.id.Lon);
        lat=(EditText)findViewById(R.id.Lat);
        clearB=(Button)findViewById(R.id.clear);

        dbHelper= new DBHelper(this);


        View.OnClickListener listener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Lat=lat.getText().toString();
                String Lon=lon.getText().toString();
                SQLiteDatabase database=dbHelper.getWritableDatabase();
                ContentValues contentValues=new ContentValues();
                switch (v.getId()){
                    case R.id.Save:
                        contentValues.put(DBHelper.KEY_Lat,Lat);
                        contentValues.put(DBHelper.KEY_Lon,Lon);
                        database.insert(DBHelper.TABLE_CONTACTS,null,contentValues);
                        break;
                    case R.id.Show:
                        Cursor cursor=database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);

                        if (cursor.moveToFirst()){
                            int idColIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
                            int latColIndex = cursor.getColumnIndex(DBHelper.KEY_Lat);
                            int lonColIndex = cursor.getColumnIndex(DBHelper.KEY_Lon);
                            do {
                                // получаем значения по номерам столбцов и пишем все в лог
                                Log.d(LOG_TAG,
                                        "ID = " + cursor.getInt(idColIndex) +
                                                ", lon = " + cursor.getString(lonColIndex) +
                                                ", lat = " + cursor.getString(latColIndex));
                                // переход на следующую строку
                                // а если следующей нет (текущая - последняя), то false - выходим из цикла
                            } while (cursor.moveToNext());
                        } else
                            Log.d(LOG_TAG, "0 rows");
                        cursor.close();
                        break;

                    case R.id.clear:
                        Log.d(LOG_TAG, "clear data");
                        database.delete(DBHelper.TABLE_CONTACTS,null,null);
                        break;
                }
            }
        };
        saveB.setOnClickListener(listener);
        showB.setOnClickListener(listener);
        clearB.setOnClickListener(listener);

    }


}
