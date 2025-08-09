package fr.classcord.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONObject;

public class User {

    //propriétés
    private String username;
    private String status;


    // Constructeurs
    public User(String username, String status){
        this.username = username;
        this.status = status;
    }


    //methodes

    // getters/setters
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //toujours faire toString
    @Override
    public String toString(){
        return "User{" +
            "username = '" + username + '\'' +
            ",status = '" + status + '\'' +
        '}';
    }  
}