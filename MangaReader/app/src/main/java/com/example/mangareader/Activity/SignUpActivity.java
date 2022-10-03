package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.mangareader.Interface.ILanguage;
import com.example.mangareader.Model.Comic;
import com.example.mangareader.Model.User;
import com.example.mangareader.R;
import com.example.mangareader.data_local.LocaleHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SignUpActivity extends AppCompatActivity implements ILanguage {

    EditText edtUserName, edtPass, edtPassConf, edtEmail;
    Button btnSignUp;
    Toolbar toolbar;
    TextView txtSignUp;

    //Firebase Database
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        AnhXa();

        updateView(Paper.book().read("language"));

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
                table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //Check Format Exception
                        if (!FormatException()) {
                            //Kiem tra user da ton tai
                            if (snapshot.child(edtUserName.getText().toString()).exists()) {
                                Toast.makeText(SignUpActivity.this, getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                            } else {
                                User user = new User(edtUserName.getText().toString(),
                                        edtPass.getText().toString(), edtEmail.getText().toString());

                                table_user.child(edtUserName.getText().toString()).setValue(user);
                                Toast.makeText(SignUpActivity.this, getString(R.string.sign_up_success), Toast.LENGTH_SHORT).show();
                                finish();

                                table_user.removeEventListener(this);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignUpActivity.this, "ERROR" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean FormatException() {
        String userName = edtUserName.getText().toString();
        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();
        String passConf = edtPassConf.getText().toString();

        if (edtUserName.getText().toString().isEmpty()) {
            Toast.makeText(SignUpActivity.this, getString(R.string.username_not_fill), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!(email.isEmpty()) && !(email.contains("@gmail.com"))) {
            Toast.makeText(SignUpActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (pass.isEmpty()) {
            Toast.makeText(SignUpActivity.this, getString(R.string.password_not_fill), Toast.LENGTH_SHORT).show();
            return true;
        }
        if (!(pass.equals(passConf))) {
            Toast.makeText(SignUpActivity.this, getString(R.string.wrong_passwordconf), Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
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
        txtSignUp = findViewById(R.id.txt_sign_up);
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this,language);
        Resources resources =  context.getResources();

        edtUserName.setHint(resources.getString(R.string.input_username_hint));
        edtPass.setHint(resources.getString(R.string.input_password_hint));
        edtPass.setHint(resources.getString(R.string.input_password_hint));
        edtPassConf.setHint(resources.getString(R.string.input_passwordconf_hint));
        edtEmail.setHint(resources.getString(R.string.input_email_hint));
        btnSignUp.setText(resources.getString(R.string.signup_btn));
        txtSignUp.setText(resources.getString(R.string.slogan_signup));
    }
}