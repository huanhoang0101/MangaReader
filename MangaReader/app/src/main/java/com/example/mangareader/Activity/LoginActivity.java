package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Common.Common;
import com.example.mangareader.Interface.ILanguage;
import com.example.mangareader.Model.User;
import com.example.mangareader.R;
import com.example.mangareader.data_local.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements ILanguage {

    EditText edtUserName, edtPass;
    Button btnLogin;
    TextView txtSignUp;
    Toolbar toolbar;
    TextView txtLogin;

    //Firebase Database
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        AnhXa();

        updateView(Paper.book().read("language"));

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
                ActiveLogin();
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
        txtLogin = findViewById(R.id.txt_login);

        //Init Database
        table_user = FirebaseDatabase.getInstance().getReference("User");
    }

    private void ActiveLogin(){
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
                        Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                        Common.Login = true;
                        Common.currentUser = user;
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        table_user.removeEventListener(this);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.password_wrong), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, getString(R.string.username_wrong), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "ERROR" + error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources =  context.getResources();

        edtUserName.setHint(resources.getString(R.string.input_username_hint));
        edtPass.setHint(resources.getString(R.string.input_password_hint));
        btnLogin.setText(resources.getString(R.string.login_btn));
        txtSignUp.setText(resources.getString(R.string.signup_new_account));
        txtLogin.setText(resources.getString(R.string.slogan));
    }
}