package fr.classcord.model;

public class CurrentUser {

     private static String username;

    public static void setUsername(String name) {
        username = name;
    }

    public static String getUsername() {
        return username;
    }
    
}
