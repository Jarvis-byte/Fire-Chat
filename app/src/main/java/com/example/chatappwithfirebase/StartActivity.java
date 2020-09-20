package com.example.chatappwithfirebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {
Button login,register;
FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null)
        {
            Intent intent=new Intent(StartActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

              login=findViewById(R.id.btn_login);
              register=findViewById(R.id.btn_register);

              login.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      startActivity(new Intent(StartActivity.this,LoginActivity.class));
                  }
              });
            register.setOnClickListener(new View.OnClickListener() {
       @Override
          public void onClick(View v) {
          startActivity(new Intent(StartActivity.this, RegistryActivity.class));
        }
         });
    }
}