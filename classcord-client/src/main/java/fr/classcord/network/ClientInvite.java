package fr.classcord.network;

import java.io.BufferedReader; //pour recevoir les messages
import java.io.IOException; //pour envoyer les messages
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import fr.classcord.model.Message;
import fr.classcord.ui.ChatInterface;
import fr.classcord.ui.ChatInterfacePerso;



// import org.json.JSONObject; //maven-ben benne van??? à faire quoi?

public class ClientInvite {

    //propriétés
    private Socket socket;
    
    private PrintWriter writer;
    private BufferedReader reader;
    private String pseudo;
    private String lastMessage = "";
    private ChatInterface chatInterface;
    private ChatInterfacePerso chatInterfacePerso;
    
    //suivre les utilisateurs et leur état en les stockant
    private final Map<String, String> userStatusMap = new HashMap<>(); //ex.: dodo online

    
    

    //Constructeur
    public ClientInvite(String pseudo){
        this.pseudo = pseudo;
    }


    //méthodes

    //Connexion au serveur
    public boolean connect(String ip, int port){
        
        try {
            //création une connexion TCP entre client et serveur
            socket = new Socket(ip, port); 

            //envoyer des messages, des données au serveur
            //autoFlush: true =>immédiaement envoyés sans besoin d'appeler flush() manuellement
            writer = new PrintWriter(socket.getOutputStream(), true);

            //lire les messages envoyés par le serveur (ligne par ligne)
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connexté au serveur " + ip + " : " + port);
            return true;
           
        } catch (IOException e) {
            System.out.println("Promblème pendant la connexion au servuer: " + e.getMessage());

            try {
                //fermeture de la connexion en cas d'échec
                if(socket != null){
                    socket.close(); 
                    //une connexion TCP ouvert consomme la mémoire et bloque les ports de réseau!!
                }
    
            } catch (IOException closException) {
                System.out.println("Erreur pendant la fermeture de la connexion " + closException);
            }
            return false;
        }
    }


    public void setChatInterface(ChatInterface chatInterface){
        this.chatInterface = chatInterface;
        this.chatInterfacePerso = null; //désactiver chatInterfacePerso
    }

    // public void setChatInterfacePerso(ChatInterfacePerso chatInterfacePerso){
    //     this.chatInterfacePerso = chatInterfacePerso;
    //     this.chatInterface = null; // désactiver chatInterface
    // }

    public void setChatInterfacePerso(ChatInterfacePerso ui) {
        this.chatInterfacePerso = ui;
    }
   
    //Gestion de la réception des messages
    public void listenForMessages(){
         new Thread(() -> {
            try {
                String line;
                //!socket.isClosed() => la connexion est encore active
                //line = reader.readLine()) != null =>il y a texte envoyée par le serveur
                while(!socket.isClosed() && socket !=null){
                    line = reader.readLine(); //modositva
                    if( line != null){ //modositva
                        lastMessage = line.trim(); //modositva
                        System.out.println("Message reçu " + line);

                        JSONObject json = new JSONObject(line);
                        System.out.println("JSON reçu = " + json.toString(2));

                        String type = json.optString("type");

                        switch (type) {
                            case "message" -> SwingUtilities.invokeLater(() -> {
                                    if (chatInterfacePerso != null && chatInterfacePerso.isVisible()) {
                                        chatInterfacePerso.afficheMessage();
                                    } else if (chatInterface != null && chatInterface.isVisible()) {
                                        chatInterface.afficheMessage();
                                    }
                                });
                            
                            case "status" -> {
                                String username = json.optString("user");
                                String statut = json.optString("state");

                                userStatusMap.put(username, statut);

                                if (chatInterfacePerso !=  null){
                                    chatInterfacePerso.updateUserList(new HashMap<>(userStatusMap));
                                }else if (chatInterface != null){
                                    chatInterface.updateUserList(new HashMap<>(userStatusMap));
                                }
                            }

                            case "users" -> {
                                System.out.println("Liste complète reçu");
                                JSONArray usersArray = json.optJSONArray("users");

                                if(usersArray != null){
                                    // Vider temporairement la map des utilisateurs (optionnel, selon ton besoin)
                                    userStatusMap.clear();

                                    for(int i = 0; i < usersArray.length(); i++){ //parcourt toutes les clés (pseudos) dans l'objet users
                                        String pseudo = usersArray.optString(i);

                                        // Ajouter chaque utilisateur avec statut "online" (puisque ce sont ceux connectés)
                                        userStatusMap.put(pseudo, "online");
                                    }

                                    if (chatInterfacePerso !=  null){
                                        chatInterfacePerso.updateUserList(new HashMap<>(userStatusMap)); //création des copies pour éviter les conflits
                                    }else if (chatInterface != null){
                                        chatInterface.updateUserList(new HashMap<>(userStatusMap));
                                    }
                                }else{
                                    System.err.println("Réponse 'users' invalide: pas de champ de 'users'");
                                }
                            }
                             case "change_username" -> { ///modifier lehet hogy le kell venni
                                 String newPseudo = json.getString("new_user");
                                 System.out.println("Pseudo mis à jour : " + newPseudo);
                                 this.setPseudo(newPseudo);
                            }

                            

                            default -> System.out.println("Type de message inconnu : " + type);
                        }
                    }else{
                        break; //si le serveur ferme la connexion
                    }
                }   
            } catch (IOException e) {
                // socket.isClosed();
                System.err.println("Connexion interrompu ou erreur de lecture: " + e.getMessage());
            }
        }).start();
    }

     //envoie d'un message au format JSON au serveur
    public void sendMessage(String messageText){

        if (pseudo == null || pseudo.isEmpty()) {
            System.err.println("Erreur : pseudo non défini.");
            return;
        }

        //convertir et envoyer sous forme de JSON
        if(writer != null && socket != null && !socket.isClosed()){
            Message message = new Message("message", "global", pseudo, "global", messageText, "");
            writer.println(message.toJson().toString());
        }else{
            System.err.println("Impossible d'envoyer le message, la connexion est fermée.\n");
        }
    }

    public void notifyOnlineStatus() {
        if (writer != null && pseudo != null && !pseudo.isEmpty()) {
            JSONObject json = new JSONObject();
            json.put("type", "status");
            json.put("user", pseudo);
            json.put("state", "online");
            writer.println(json.toString());
        }
    }

    //envoie la requête de type "users"
    public void requestUserList() {
        if (writer != null && socket != null && !socket.isClosed()) {
            JSONObject request = new JSONObject();
            request.put("type", "users");
            send(request.toString());
        } else {
            System.err.println("Impossible de demander la liste des utilisateurs, connexion non active.");
        }
    }

    public void setPseudo(String pseudo){ 
        this.pseudo = pseudo;
    }

    public String getPseudo(){
        return pseudo;
    }

    public String getLastMessage(){
        return lastMessage;
    }

    public void send(String message) {
        writer.println(message);
    }

    
    public BufferedReader getIn() {
        return reader;
    }


    //Premièr jour:16 juin 25 =>peut être mettre en commentaire
    //Méthode principale pour la console
   public static void main(String[] args) {
        //objet "client" => pour la gestion de la connexion et l'envoir de messages au serveur
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Entrez votre pseudo : ");
            String pseudo = scanner.nextLine();

            ClientInvite client = new ClientInvite(pseudo);

            //à quel adresse IP veut se connecter
            System.out.print("Entrez IP du serveur : ");
            String ip = scanner.nextLine();

            System.out.print("Entrez le port du serveur : ");

            //transtypage string en int
            int port = Integer.parseInt(scanner.nextLine());

            client.connect(ip, port);
        } catch (Exception e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }


     // Interface pour le callback de réception de message
    // public interface MessageListener {
    //     void onMessage(String message);
    // }

    // public void setMessageListener(MessageListener listener) {
    //     this.messageListener = listener;
    // }


}
