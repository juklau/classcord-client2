package fr.classcord.model;

//gestion L'UTILISATEUR connecté
public class CurrentUser {

    //propriétés
    private static String username;


    //méthodes

    //setters/ getters
    public static void setUsername(String name) {
        username = name;
    }

    public static String getUsername() {
        return username;
    }
    
}
