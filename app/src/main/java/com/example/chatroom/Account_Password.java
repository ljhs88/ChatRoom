package com.example.chatroom;


import org.litepal.crud.LitePalSupport;

public class Account_Password extends LitePalSupport {//继承DataSupport才能执行CRUD操作

    private String account;

    private String password;

    private String isRemember;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsRemember() {
        return isRemember;
    }

    public void setIsRemember(String isRemember) {
        this.isRemember = isRemember;
    }
}
