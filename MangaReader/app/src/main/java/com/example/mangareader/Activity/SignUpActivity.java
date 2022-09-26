package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mangareader.Model.User;
import com.example.mangareader.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {

    EditText edtUserName, edtPass, edtPassConf, edtEmail;
    Button btnSignUp;
    Toolbar toolbar;

    //Firebase Database
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        AnhXa();

        toolbar.setNavigationIcon(R.drawable.ic_left_24);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //Check Format Exception
                        if (!FormatException()) {
                            //Kiem tra user da ton tai
                            if (snapshot.child(edtUserName.getText().toString()).exists()) {
                                Toast.makeText(SignUpActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                            } else {
                                User user = new User(edtPass.getText().toString(), edtEmail.getText().toString());
                                user.setUserName(edtUserName.getText().toString());
                                user.setName(edtUserName.getText().toString());

                                table_user.child(edtUserName.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private boolean FormatException() {
        if(edtUserName.getText().toString().isEmpty()){
            Toast.makeText(SignUpActivity.this, "Chưa nhập tài khoản", Toast.LENGTH_SHORT).show();
            return true;
        }
        if(!(edtEmail.getText().toString().isEmpty()) && !(edtEmail.getText().toString().equals("@gmail.com"))) {
            Toast.makeText(SignUpActivity.this, "Sai định dạng Email", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (edtPass.getText().toString().equals(edtPassConf.getText().toString()))
            return false;
        else{
            Toast.makeText(SignUpActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    private void AnhXa() {
        //Init Database
        table_user = FirebaseDatabase.getInstance().getReference("User");

        edtUserName = findViewById(R.id.edt_username);
        edtPass = findViewById(R.id.edt_pass);
        edtPassConf = findViewById(R.id.edt_pass_xacnhan);
        edtEmail = findViewById(R.id.edt_email);
        btnSignUp = findViewById(R.id.btn_sign_up);
        toolbar = findViewById(R.id.toolbar);
    }
}