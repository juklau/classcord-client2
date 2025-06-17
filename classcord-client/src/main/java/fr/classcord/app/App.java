package fr.classcord.app;

import fr.classcord.model.User;

//main => permet de lancer le program

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // Message text = new Message("Hello Classcord!");

        //automatiquement toString() qui va s'affcher!!
        // System.out.println(text);

        User klaudia = new User("Klaudia","away");
        System.out.println(klaudia);



    }

    
}
