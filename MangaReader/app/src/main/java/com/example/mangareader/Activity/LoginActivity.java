package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Common.Common;
import com.example.mangareader.Model.User;
import com.example.mangareader.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText edtUserName, edtPass;
    Button btnLogin;
    TextView txtSignUp;
    Toolbar toolbar;

    //Firebase Database
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AnhXa();

        toolbar.setNavigationIcon(R.drawable.ic_left_24);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Kiểm tra tài khoản
                        if(snapshot.child(edtUserName.getText().toString()).exists()) {
                            //Kiểm tra mật khẩu
                            User user = snapshot.child(edtUserName.getText().toString()).getValue(User.class);
                            user.setUserName(edtUserName.getText().toString());
                            if (user.getPassword().equals(edtPass.getText().toString()))
                            {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Common.Login = true;
                                Common.currentUser = user;
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                                table_user.removeEventListener(this);
                            } else {
                                Toast.makeText(LoginActivity.this, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

    private void AnhXa() {
        edtUserName = findViewById(R.id.edt_username);
        edtPass = findViewById(R.id.edt_pass);
        btnLogin = findViewById(R.id.btn_login);
        txtSignUp = findViewById(R.id.txt_sign_up);
        toolbar = findViewById(R.id.toolbar);

        //Init Database
        table_user = FirebaseDatabase.getInstance().getReference("User");
    }
}