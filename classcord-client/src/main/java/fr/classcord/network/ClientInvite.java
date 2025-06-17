package fr.classcord.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.json.JSONObject; //??????????

// import org.json.JSONObject; //maven-ben benne van??? à faire quoi?

public class ClientInvite {

    //propriétés
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String pseudo;



    //Constructeur
    // public ClientInvite(String pseudo){
    //     this.pseudo = pseudo;

    // }

    public ClientInvite(){
        
    }


    //méthodes

    //Connexion au serveur
    public void connect(String ip, int port){
        
        try {
            //création une connexion TCP entre client et serveur
            socket = new Socket(ip, port); 

            //envoyer des messages au serveur
            //autoFlush: true =>immédiaement envoyés sans besoin d'appeler flush() manuellement
            writer = new PrintWriter(socket.getOutputStream(), true);

            //lire les messages envoyés par le serveur
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connextez au serveur" + ip + ":" + port);

            //démarrer le thread de réception des messages en parallele, en arrière plan
            //écoute les messages entrant envoyés par le servuer
            new Thread(this::listenForMessages).start();

            //lire les messages depuis la console
            startChat();
            
        } catch (IOException e) {
            System.out.println("Promblème pendant la connexion au servuer: " + e.getMessage());
        }
        
    }

    //envoie d'un message au format JSON depuis la console
    public void send(String messageText){
        JSONObject message = new JSONObject();
        message.put("type", "message");
        message.put("subtype", "global");
        message.put("to", "global");
        message.put("from", pseudo);
        message.put("content", messageText);

        //envoyer le message => transformer l'objet JSON en String
        writer.println(message.toString());
    }

    //Gestion de la réception des messages
    private void listenForMessages(){
        String line;
        try {
            //!socket.isClosed() => la connexion est encore active
            //line = reader.readLine()) != null =>il y a texte envoyée par le serveur
            while(!socket.isClosed() && (line = reader.readLine()) != null){
                System.out.println("Message reçu " + line);
            }   
        } catch (IOException e) {
            // socket.isClosed();
            System.err.println("Connexion interrompu ou erreur de lecture: " + e.getMessage());
        }
    }

    //pour envoyer plusieurs messages sans bloquer l'application
    private void startChat(){
        //permet de lire les entrées utilisateur depuis la console
        //à la fin de l'exécution de la methode, le scanner fermera automatiquement
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) { 
                System.out.println("Entrez le message :");
                String messageText = scanner.nextLine();

                //en tapant "exit" la connexion au serveur est fermée
                //equalsIgnoreCase() =>comparation de la chaîne sans tenir compte des majuscules et minuscules
                if("exit".equalsIgnoreCase(messageText)){
                    System.out.println("Déconnexion");
                    socket.close();
                    break;
                }
                send(messageText);
            }
        } catch (IOException e) {
            System.out.println("Erreur lors de la fermeture de la connexion: " + e.getMessage());
        }

    }

    //Méthode principale (console)
   public static void main(String[] args) {
        //objet "client" => pour la gestion de la connexion et l'envoir de messages au serveur
        ClientInvite client = new ClientInvite();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Entrez votre pseudo : ");
            client.pseudo = scanner.nextLine();

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
