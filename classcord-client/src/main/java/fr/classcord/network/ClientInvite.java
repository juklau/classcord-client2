package fr.classcord.network;

import java.io.BufferedReader; //pour recevoir les messages
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter; //pour envoyer les messages
import java.net.Socket;
import java.util.Scanner;

import javax.swing.SwingUtilities;

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
    // private MessageListener messageListener;

    
    

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


    // public void setChatInterface(ChatInterface chatInterface){
    //     this.chatInterface = chatInterface;
    //     this.chatInterfacePerso = null; //désactiver chatInterfacePerso
    // }

    // public void setChatInterfacePerso(ChatInterfacePerso chatInterfacePerso){
    //     this.chatInterfacePerso = chatInterfacePerso;
    //     this.chatInterface = null; // désactiver chatInterface
    // }
   
    //Gestion de la réception des messages
    public void listenForMessages(){
         new Thread(() -> {
            try {
                String line;
                //!socket.isClosed() => la connexion est encore active
                //line = reader.readLine()) != null =>il y a texte envoyée par le serveur
                while(!socket.isClosed() && socket !=null){
                    if((line = reader.readLine()) != null){
                        lastMessage = line;
                        System.out.println("Message reçu " + line);

                        SwingUtilities.invokeLater(() -> {
                            if (chatInterfacePerso != null && chatInterfacePerso.isVisible()) {
                                chatInterfacePerso.afficheMessage();
                            } else if (chatInterface != null && chatInterface.isVisible()) {
                                chatInterface.afficheMessage();
                            }
                        });
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
            System.err.println("Impossible d'envoyer le message : connexion fermée.\n");
        }
       
    }


    // public boolean sendAuthRequest(String type, String username, String password){
    //     if (writer == null || socket == null || socket.isClosed()) {
    //         System.err.println("Erreur : Connexion au serveur requise.");
    //         return false;
    //     }

    //     //création du requete en forme JSON
    //     JSONObject request = new JSONObject();
    //     request.put("type", type); //"login" ou "register"
    //     request.put("username", username);
    //     request.put("password", password);

    //     //envoie la requête au serveur
    //     writer.println(request.toString());
    //     System.out.println("Requête envoyé: " + request.toString());

    //     try {
    //         //lire la réponse du serveur
    //         String reponse = reader.readLine();
    //         System.out.println("Réponse du serveur : " + reponse); // Debug

    //         //vérification si le "status = OK"
    //         JSONObject jsonReponse = new JSONObject(reponse);
    //         boolean authReussi = jsonReponse.has("status") && jsonReponse.getString("status").equals("ok");

    //         if(authReussi){
    //             System.out.println("Authentification réussi, démarrage du chat...");
               
    //         }

    //         return authReussi;
    //         // return reponse != null && reponse.contains("success"); //ha van vàlasz => true ???
    //     } catch (IOException e) {
    //         System.out.println("Erreur pendant la récupération de réponse du serveur");
    //         return false;
    //     }
    // };

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
