package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.litepal.LitePal;


import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences pref;

    private EditText accountEdit;

    private EditText passwordEdit;

    private Button loginButton;

    private Button registerButton;

    private CheckBox rememberPass;

    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        LitePal.getDatabase();//创建数据库存储账号密码

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        accountEdit = (EditText) findViewById(R.id.account_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);

        //如果点击注册按钮
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//将账号密码保存在数据库中
                if (accountEdit.getText().equals("")||passwordEdit.getText().equals("")) {
                    Toast.makeText(LoginActivity.this, "账号或密码未输入！！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                Account_Password ap = new Account_Password();
                ap.setAccount(account);
                ap.setPassword(password);
                if (rememberPass.isChecked()) {
                    ap.setIsRemember("true");
                } else {
                    ap.setIsRemember("false");
                }
                ap.save();
                //注册之后将输入框清空
                accountEdit.setText("");
                passwordEdit.setText("");
            }
        });
        //如果点击登录按钮
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accountEdit.getText().equals("")) {
                    Toast.makeText(LoginActivity.this, "请输入账号！！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取文本框中的账号密码
                String account = accountEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                //在数据库中匹配账号密码
                List<Account_Password> aps = LitePal.findAll(Account_Password.class);
                for (Account_Password ap : aps) {
                    if (ap.getAccount().equals(account)) {
                        flag = 1;
                        if (!passwordEdit.getText().equals("")&&!accountEdit.getText().equals("")) {
                            if (rememberPass.isChecked()) {//判断是否勾选记住密码(数据更新)
                                Account_Password apUpdate = new Account_Password();
                                apUpdate.setIsRemember("true");
                                apUpdate.updateAll("account = ?", ap.getAccount());//根据账号更新
                            } else {
                                Account_Password apUpdate = new Account_Password();
                                apUpdate.setIsRemember("false");
                                apUpdate.updateAll("account = ?", ap.getAccount());//根据账号更新
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "账号或密码未输入！！！", Toast.LENGTH_SHORT).show();
                        }

                        if (ap.getIsRemember().equals("true")) {//如果此时isRemember为true则直接进入下一个活动
                            passwordEdit.setText(ap.getPassword());//将密码设置到密码框中
                            try {
                                Thread.sleep(1500);//在此等待1.5秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //开启下一个活动
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            break;
                        } else {
                            if (!ap.getPassword().equals(password)) {
                                Toast.makeText(LoginActivity.this, "密码错误！！！请重新输入！！！", Toast.LENGTH_SHORT).show();
                            } else {

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                break;
                            }
                        }
                    }
                }
                if (flag==0) {
                    Toast.makeText(LoginActivity.this, "此账号未注册！！！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button delete = (Button) findViewById(R.id.delete_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LitePal.deleteAll(Account_Password.class);
            }
        });
    }
}