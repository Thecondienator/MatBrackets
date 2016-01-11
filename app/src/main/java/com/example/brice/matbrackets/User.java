package com.example.brice.matbrackets;

/**
 * Created by Brice on 1/8/2016.
 */
public class User {

    private String email;
    private String password;
    private String mobileToken;

    public User(){

    }

    public User(String email, String token){
        this.email = email;
        this.mobileToken = token;
    }

    public boolean checkToken(){
        if(email.equals("thecondienator@gmail.com")){
            return true;
        } else{
            return false;
        }
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setMobileToken(String token){
        this.mobileToken = token;
    }
}
