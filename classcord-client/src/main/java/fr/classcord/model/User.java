package fr.classcord.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.json.JSONObject;

import fr.classcord.network.ClientInvite;


public class User {

    //propriétés
    private String username;
    private String status;


    // Constructeurs
    public User(String username, String status){
        this.username = username;
        this.status = status;
    }


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


    //methodes
    //sauvegarder le username du dernière user
    public static void saveLastUsername(String username){
        Properties properties = new Properties();
        properties.setProperty("lastUsername", username);

        try (FileOutputStream fOutputStream = new FileOutputStream("config.properties")){
            properties.store(fOutputStream, "Dernier pseudo utilisé");
        }catch (IOException e){
            System.out.println("Erreur pendant la sauvegarde du pseudo" + e.getMessage());
        }
    }

    // s'inscrire
	public static String register(ClientInvite client, String username, String password) throws Exception {
		if (client == null || client.getIn() == null) {
            System.err.println("Erreur : le client n'est pas connecté au serveur.");
            throw new IllegalStateException("Le client n'est pas connecté au serveur.");
        }
        
        JSONObject json = new JSONObject(); //peut-être à ramplacer json par request
		json.put("type", "register");
		json.put("username", username);
		json.put("password", password);

        System.out.println("[ENVOI] Requête d'inscription : " + json.toString());

		client.send(json.toString());

		String response = client.getIn().readLine();
        if (response == null) {
            System.err.println("Erreur : Aucune réponse reçue du serveur après l'inscription.");
            throw new IOException("Aucune réponse du serveur après l'inscription.");
        }

        System.out.println("[RÉPONSE] Serveur : " + response);
		return response;
	}

	// se connecter par login
	public static String login(ClientInvite client, String username, String password) throws Exception {
		if (client == null || client.getIn() == null) {
            System.err.println("Erreur : Connexion au serveur requise.");
            throw new IllegalStateException("Le client n'est pas connecté au serveur.");
        }
         
        JSONObject request = new JSONObject();
		request.put("type", "login");
		request.put("username", username);
		request.put("password", password);

        System.out.println("Requête envoyée : " + request.toString());

		client.send(request.toString());

		String response = client.getIn().readLine();
        if (response == null) {
            System.out.println("Erreur pendant la récupération de réponse du serveur");
            throw new IOException("Aucune réponse du serveur après la connexion.");
        }

        System.out.println("Réponse reçue : " + response);
		return response;
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