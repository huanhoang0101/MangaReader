package com.example.mangareader.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mangareader.Common.Common;
import com.example.mangareader.Interface.ILanguage;
import com.example.mangareader.Interface.IMenu;
import com.example.mangareader.R;
import com.example.mangareader.data_local.LocaleHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.paperdb.Paper;

public class ProfileActivity extends AppCompatActivity implements IMenu, ILanguage {

    TextView txtName, txtUserName, txtPass, txtEmail, txtChangeLanguage, txtLogout;
    BottomNavigationView bottomNavigationView;

    //Firebase Database
    DatabaseReference table_user;

    boolean lang_selected = true;
    Context context;
    Resources resources;
    TextView txtTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        AnhXa();
        ShowInfo();

        bottomNavigationView.setSelectedItemId(R.id.action_user);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                setMenu(item);
                return true;
            }
        });

        txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowNameChangeDialog();
            }
        });

        txtEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowEmailChangeDialog();
            }
        });

        txtPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPasswordChangeDialog();
            }
        });

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Common.Login = false;
                Common.currentUser = null;
            }
        });

        txtChangeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeLanguage();
            }
        });
    }

    private void ShowInfo() {
        txtName.setText(Common.currentUser.getName());
        txtUserName.setText(Common.currentUser.getUserName());
        txtEmail.setText(Common.currentUser.getEmail());
    }

    private void AnhXa() {
        //Init Database
        table_user = FirebaseDatabase.getInstance().getReference("User");

        txtName = findViewById(R.id.txt_name_profile);
        txtUserName = findViewById(R.id.txt_username_profile);
        txtPass = findViewById(R.id.txt_pass_profile);
        txtEmail = findViewById(R.id.txt_email_profile);
        txtChangeLanguage = findViewById(R.id.txt_change_language);
        txtLogout = findViewById(R.id.txt_logout_profile);
        bottomNavigationView = findViewById(R.id.menu_nav);
        txtTest = findViewById(R.id.txtTest);
    }


    private void ShowNameChangeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View nameChange_layout = inflater.inflate(R.layout.dialog_name_change, null);

        MaterialEditText edtNewName = nameChange_layout.findViewById(R.id.edt_name_change);

        alertDialog.setView(nameChange_layout);

        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("ĐỔI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edtNewName.getText().toString().isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Tên không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                Map<String, Object> nameChange = new HashMap<>();
                nameChange.put("name", edtNewName.getText().toString());

                table_user.child(Common.currentUser.getUserName())
                        .updateChildren(nameChange)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Đổi tên thành công", Toast.LENGTH_SHORT).show();
                                Common.currentUser.setName(edtNewName.getText().toString());
                                txtName.setText(Common.currentUser.getName());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.show();
    }

    private void ShowEmailChangeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View emailChange_layout = inflater.inflate(R.layout.dialog_email_change, null);

        MaterialEditText edtNewEmail = emailChange_layout.findViewById(R.id.edt_email_change);

        alertDialog.setView(emailChange_layout);

        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("ĐỔI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edtNewEmail.getText().toString().isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(edtNewEmail.getText().toString().contains("@gmail.com"))) {
                    Toast.makeText(ProfileActivity.this, "Sai định dạng Email", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> EmailChange = new HashMap<>();
                    EmailChange.put("email", edtNewEmail.getText().toString());

                    table_user.child(Common.currentUser.getUserName())
                            .updateChildren(EmailChange)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(ProfileActivity.this, "Đổi Email thành công", Toast.LENGTH_SHORT).show();
                                    Common.currentUser.setEmail(edtNewEmail.getText().toString());
                                    txtEmail.setText(Common.currentUser.getEmail());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
        alertDialog.show();
    }

    private void ShowPasswordChangeDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View passChange_layout = inflater.inflate(R.layout.dialog_pass_change, null);

        MaterialEditText edtOldPass = passChange_layout.findViewById(R.id.edt_pass_change_old);
        MaterialEditText edtNewPass = passChange_layout.findViewById(R.id.edt_pass_change_new);
        MaterialEditText edtNewPassConf = passChange_layout.findViewById(R.id.edt_pass_change_new_conf);

        alertDialog.setView(passChange_layout);

        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("Thay đổi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (edtNewPass.getText().toString().isEmpty()) {
                    Toast.makeText(ProfileActivity.this, "Mật khẩu không hợp lệ", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Change Pass

                //Check old pass
                if (edtOldPass.getText().toString().equals(Common.currentUser.getPassword())) {
                    //Check pass confirm
                    if (edtNewPass.getText().toString().equals(edtNewPassConf.getText().toString())) {
                        Map<String, Object> passChange = new HashMap<>();
                        passChange.put("password", edtNewPass.getText().toString());

                        table_user.child(Common.currentUser.getUserName())
                                .updateChildren(passChange)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ProfileActivity.this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                                        Common.currentUser.setPassword(edtNewPass.getText().toString());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Mật khẩu hiện tại không chính xác", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    private void ShowDialogLogin() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
        alertDialog.setTitle("Thông báo!");
        alertDialog.setMessage("Vui lòng đăng nhập để thực hiện chức năng này");

        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });
        alertDialog.show();
    }

    private void ChangeLanguage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);

        LayoutInflater inflater = this.getLayoutInflater();
        View languageChange_layout = inflater.inflate(R.layout.dialog_language_change, null);

        RadioButton rdVN = languageChange_layout.findViewById(R.id.rdb_VN);
        RadioButton rdEN = languageChange_layout.findViewById(R.id.rdb_EN);
        RadioButton rdJP = languageChange_layout.findViewById(R.id.rdb_JP);

        String currentLanguage = (String) Paper.book().read("language");

        if(currentLanguage.equals("vi"))
            rdVN.setChecked(true);
        if(currentLanguage.equals("en"))
            rdEN.setChecked(true);
        if(currentLanguage.equals("ja"))
            rdJP.setChecked(true);

        alertDialog.setView(languageChange_layout);

        alertDialog.setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("Xác nhận", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //setLanguageChecked(radioGroup);
                if(rdVN.isChecked()){
                    Paper.book().write("language", "vi");
                    updateView((String) Paper.book().read("language"));
                }
                if(rdEN.isChecked()) {
                    Paper.book().write("language", "en");
                    updateView((String) Paper.book().read("language"));
                }
                if(rdJP.isChecked()) {
                    Paper.book().write("language", "ja");
                    updateView((String) Paper.book().read("language"));
                }
            }
        });
        alertDialog.show();
    }
    //        int checkedItem;
//        String[] language = {"English", "Tiếng Việt"};
//
//        if(lang_selected){
//            checkedItem = 0;
//        }else {
//            checkedItem = 1;
//        }
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileActivity.this);
//
//        alertDialog.setTitle("Select Language")
//                .setSingleChoiceItems(language, checkedItem, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        txtChangeLanguage.setText(language[i]);
//                        if(language[i].equals("English"))
//                        {
//                            context = LocaleHelper.setLocale(ProfileActivity.this, "en");
//                            resources = context.getResources();
//
//                            txtTest.setText(resources.getString(R.string.new_comic));
//                            Common.language = "en";
//                        }
//                        if(language[i].equals("VN"))
//                        {
//                            context = LocaleHelper.setLocale(ProfileActivity.this, "vi");
//                            resources = context.getResources();
//
//                            txtTest.setText(resources.getString(R.string.new_comic));
//                            Common.language = "vi";
//                        }
//                    }
//                })
//                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//        alertDialog.create().show();

    private void setLanguageChecked(RadioGroup radioGroup) {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rdb_VN:
                        Paper.book().write("language", "vi");
                        updateView((String) Paper.book().read("language"));
                        Toast.makeText(ProfileActivity.this, (String) Paper.book().read("language") + "1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rdb_EN:
                        Paper.book().write("language", "en");
                        updateView((String) Paper.book().read("language"));
                        Toast.makeText(ProfileActivity.this, (String) Paper.book().read("language")+ "1", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.rdb_JP:
                        Paper.book().write("language", "ja-rJP");
                        updateView((String) Paper.book().read("language"));
                        Toast.makeText(ProfileActivity.this, (String) Paper.book().read("language"), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void setMenu(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_home:
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                break;
            case R.id.action_favorite:
                if (!Common.Login)
                    ShowDialogLogin();
                else
                    startActivity(new Intent(ProfileActivity.this, FavoriteActivity.class));
                break;
            case R.id.action_category:
                startActivity(new Intent(ProfileActivity.this, CategoryActivity.class));
                break;
            case R.id.action_user:
                //Dang o user
                break;
        }
    }

    @Override
    public void updateView(String language) {
        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();
        txtTest.setText(resources.getString(R.string.new_comic));
    }
}