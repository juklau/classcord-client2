package fr.classcord.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import fr.classcord.model.Message;



// import org.json.JSONObject; //maven-ben benne van??? à faire quoi?

public class ClientInvite {

    //propriétés
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String pseudo;



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

            //envoyer des messages au serveur
            //autoFlush: true =>immédiaement envoyés sans besoin d'appeler flush() manuellement
            writer = new PrintWriter(socket.getOutputStream(), true);

            //lire les messages envoyés par le serveur
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connexté au serveur" + ip + ":" + port);

            //démarrer le thread de réception des messages en parallele, en arrière plan
            //écoute les messages entrant envoyés par le servuer
            new Thread(this::listenForMessages).start();
            return true;

            //lire les messages depuis la console
            // startChat();
            
        } catch (IOException e) {
            System.out.println("Promblème pendant la connexion au servuer: " + e.getMessage());
            return false; //échec de la connexion
        }
    }

   
    //Gestion de la réception des messages
    private void listenForMessages(){
        
        try {
            String line;
            //!socket.isClosed() => la connexion est encore active
            //line = reader.readLine()) != null =>il y a texte envoyée par le serveur
            while(!socket.isClosed() && (line = reader.readLine()) != null && socket !=null){
                System.out.println("Message reçu " + line);
            }   
        } catch (IOException e) {
            // socket.isClosed();
            System.err.println("Connexion interrompu ou erreur de lecture: " + e.getMessage());
        }
    }

     //envoie d'un message au format JSON au serveur
    public void sendMessage(String messageText){
        //PREMIER EXERCICE
        // JSONObject message = new JSONObject();
        // message.put("type", "message");
        // message.put("subtype", "global");
        // message.put("to", "global");
        // message.put("from", pseudo);
        // message.put("content", messageText);

        //convertir et envoyer sous forme de JSON
        if(writer != null && socket != null && !socket.isClosed()){
            Message message = new Message("message", "global", pseudo, "global", messageText, "");
            writer.println(message.toJson().toString());
        }else{
            System.err.println("Impossible d'envoyer le message : connexion fermée.\n");
        }
       
    }

    //pour envoyer plusieurs messages sans bloquer l'application
    // private void startChat(){
    //     //permet de lire les entrées utilisateur depuis la console
    //     //à la fin de l'exécution de la methode, le scanner fermera automatiquement
    //     try (Scanner scanner = new Scanner(System.in)) {
    //         while (true) { 
    //             System.out.println("Entrez le message :");
    //             String messageText = scanner.nextLine();

    //             //en tapant "exit" la connexion au serveur est fermée
    //             //equalsIgnoreCase() =>comparation de la chaîne sans tenir compte des majuscules et minuscules
    //             if("exit".equalsIgnoreCase(messageText)){
    //                 System.out.println("Déconnexion");
    //                 socket.close();
    //                 break;
    //             }
    //             send(messageText);
    //         }
    //     } catch (IOException e) {
    //         System.out.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
    //     }

    // }

    //Méthode principale (console)
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


    //IP Nedj: 10.0.108.81
}
