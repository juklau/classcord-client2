package fr.classcord.model;

public class CurrentUser {

    //propriétés
    private static String username;

    //setters/ getters
    public static void setUsername(String name) {
        username = name;
    }

    public static String getUsername() {
        return username;
    }
    
}
