package com.example.application_for_parent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class User_account extends AppCompatActivity {

    TextView child_phone,parent_phone,unique_code;
    String ch_phone,pr_phone,un_code;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);
        sharedPreferences=getSharedPreferences("preference",MODE_PRIVATE);
        child_phone=(TextView)findViewById(R.id.child_phone);
        parent_phone=(TextView)findViewById(R.id.parent_phone);
        unique_code=(TextView)findViewById(R.id.unique_code);
        pr_phone=sharedPreferences.getString("parent_phone","");
        ch_phone=sharedPreferences.getString("child_phone","");
        un_code=sharedPreferences.getString("unique_code","");
        if(!pr_phone.contains("+")){
            pr_phone="+"+pr_phone;
        }
        if(!ch_phone.contains("+")){
            ch_phone="+"+ch_phone;
        }
        child_phone.setText(child_phone.getText()+"  "+ch_phone);
        parent_phone.setText(parent_phone.getText()+"  "+pr_phone);
        unique_code.setText(un_code);
    }
}
