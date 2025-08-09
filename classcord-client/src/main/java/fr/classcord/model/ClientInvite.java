package fr.classcord.model;

import java.io.BufferedReader; //pour recevoir les messages
import java.io.IOException; //pour envoyer les messages
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;

import fr.classcord.controller.ChatController;
import fr.classcord.ui.ChatPersoUI;
import fr.classcord.ui.ChatUI;


// import org.json.JSONObject; //maven-ben benne van??? à faire quoi?

public class ClientInvite {

    //propriétés
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String pseudo;
    private String lastMessage = "";
    private ChatPersoUI chatPersoUI;
    private ChatUI chatUI;
    private ChatController controller;
    
    //suivre les utilisateurs et leur état en les stockant
    private final Map<String, String> userStatusMap = new HashMap<>(); //ex.: dodo online


    //Constructeur
    public ClientInvite(String pseudo){
        this.pseudo = pseudo;
    }


    //méthodes

    //Connexion au serveur
    //marad itt/ connectioncontroller l'appel -> puis connecToServeur
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

    public void setChatUI(ChatUI chatUI){
        this.chatUI = chatUI;
        this.chatPersoUI = null; //désactiver chatInterfacePerso
    }

    // public void setChatInterfacePerso(ChatInterfacePerso chatInterfacePerso){
    //     this.chatInterfacePerso = chatInterfacePerso;
    //     this.chatInterface = null; // désactiver chatInterface
    // }

    public void setChatPersoUI(ChatPersoUI ui) {
        this.chatPersoUI = ui;
    }

    public void setController(ChatController controller) {
        this.controller = controller;
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

                        //déléguer le traitement "switch" au contrôleur
                        if(controller != null){
                            controller.handleIncomingMessage(json);
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

   /*  //envoie d'un message au format JSON au serveur
    // était utilisé au début en utilisant un message de type Message
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
    }*/

    // envoie un message JSON via un printwriter => exécution une action réseau
    public void notifyOnlineStatus() {
        if (writer != null && pseudo != null && !pseudo.isEmpty()) {
            JSONObject json = new JSONObject();
            json.put("type", "status");
            json.put("user", pseudo);
            json.put("state", "online");
            writer.println(json.toString());
        }
    }

    //envoie une requête réseau pour obtenir la liste des utilisateurs
    //utilisation des ressources internes: writer, socket
    public void requestUserList() {
        if (writer != null && socket != null && !socket.isClosed()) {
            JSONObject request = new JSONObject();
            request.put("type", "users");
            send(request.toString());
        } else {
            System.err.println("Impossible de demander la liste des utilisateurs, connexion non active.");
        }
    }

    public void send(String message) {
        writer.println(message);
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
