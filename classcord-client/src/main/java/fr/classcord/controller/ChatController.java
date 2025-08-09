package fr.classcord.controller;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

import fr.classcord.model.ClientInvite;
import fr.classcord.ui.ChatPersoUI;
import fr.classcord.ui.ChatUI;
import org.json.JSONArray;
import org.json.JSONObject;


public class ChatController {

    //propriétés
    private final ClientInvite clientInvite;
    private final ChatPersoUI chatPersoUI;
    private final ChatUI chatUI;
    //suivre les utilisateurs et leur état en les stockant
    private final Map<String, String> userStatusMap = new HashMap<>(); //ex.: dodo online

    //constructeur
    public ChatController(ClientInvite clientInvite, ChatPersoUI chatPersoUI, ChatUI chatUI) {
        this.clientInvite = clientInvite;
        this.chatPersoUI = chatPersoUI;
        this.chatUI = chatUI;

        clientInvite.setChatPersoUI(chatPersoUI);   //liaison avec la vue
        clientInvite.setController(this);           //liaison avec le contrôleur
        clientInvite.listenForMessages();           //démarrer l'écoute
        clientInvite.requestUserList();             //demander la liste des utilisateurs
    }

    //méthodes

    // Action Utilisateur

    public void sendMessage(String messageText, String selectedUser) {
        if(clientInvite == null || messageText == null || messageText.trim().isEmpty()){
            chatPersoUI.appendFormattedMessage("Système", "Erreur : Vous devez être connecté avant d'envoyer un message.\n", false);
            return;
        }

        JSONObject json = new JSONObject();
        json.put("type", "message");
        json.put("content", messageText);

        if(selectedUser != null && !selectedUser.isEmpty()){
            //Envoyer un message "privé"
            json.put("subtype", "private");
            json.put("to", selectedUser);

            //Envoie message privé avec préfixe
            // chatArea.append("**[MP à " + selectedUser + "]** " + messageText + "\n"); //avant la colorisation des messages
            chatPersoUI.appendFormattedMessage(clientInvite.getPseudo(), "**[MP à " + selectedUser + "]** " + messageText + "\n", true);
        }else{
            //envoyer un message "global"
            json.put("subtype", "global");
            json.put("to", "global");

            // chatArea.append("Vous: " + messageText + "\n"); //afficher le message //avant la colorisation des messages
            chatPersoUI.appendFormattedMessage(clientInvite.getPseudo(), messageText, false);
        }
        clientInvite.send(json.toString());
    }

    //pour envoyer le "statut" choisi
    public void envoyerStatut(String selection){
        if (clientInvite == null || clientInvite.getPseudo() == null || clientInvite.getPseudo().isEmpty()) {
            System.err.println("Client non initialisé ou pseudo invalide.");
            return;
        }

        //correspondance entre libellé UI et codes de statut =>transformation
        String state = switch (selection) {
            case "Absent" -> "away";  // ou "non joignable" => "not reachable"
            case "Indisponible" -> "do not disturb";    // => "dnd" ou unavailable
            case "Invisible" -> "offline";
            case "En ligne", "Disponible" -> "online"; // pour plus de flexibilité
            default -> "online";                 // sécurité par défaut
        };

        JSONObject json = new JSONObject();
        json.put("type", "status");
        json.put("user", clientInvite.getPseudo()); //pour l'identification côté serveur
        json.put("state", state);

        clientInvite.send(json.toString());

        // Mise à jour immédiate locale (avant que le serveur ne le renvoie)
        chatPersoUI.updateLocalUserStatus(clientInvite.getPseudo(), state);
    }


    // Traitement des messages reçus


    public void handleIncomingMessage(JSONObject json){
        String type = json.optString("type");

        switch (type) {
            case "message" -> SwingUtilities.invokeLater(this::afficherDernierMessage);
            case "status" -> { // kijavitva
                String username = json.optString("user");
                String statut = json.optString("state");
                userStatusMap.put(username, statut);
                handleUserListUpdate(new HashMap<>(userStatusMap));
            }
            case "users" -> { //kijavitva
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
                    handleUserListUpdate(new HashMap<>(userStatusMap));
                }else{
                    System.err.println("Réponse 'users' invalide: pas de champ de 'users'");
                }
            }
            default -> System.out.println("Type de message inconnu : " + type);
        }
    }

    public void handleUserListUpdate(Map<String, String> updateMap){
        if (chatPersoUI !=  null){
            //chatInterfacePerso.updateUserList(new HashMap<>(userStatusMap));
            chatPersoUI.updateUserList(updateMap);
        }else if (chatUI != null){
            //chatInterface.updateUserList(new HashMap<>(userStatusMap));
            chatUI.updateUserList(updateMap);
        }
    }


    // Afficher le dernier message reçu
    public void afficherDernierMessage(){
        if((chatPersoUI != null && chatPersoUI.isVisible()) || (chatUI != null && chatUI.isVisible())){
            String lastMessageJSON = clientInvite.getLastMessage();

            if (lastMessageJSON == null || lastMessageJSON.isEmpty()){
                System.out.println("Aucun message à afficher.");
                return;
            }

            //  AVANT la séparation => individuel ou global
            // Message lastMessageString = Message.fromJson(lastMessageJSON); //convertion en message "normal"
            // chatArea.append("Message reçu de " + lastMessageString.getFrom() + lastMessageString.getContent() + "\n");
            // chatArea.repaint();
            // chatArea.revalidate();

            try {
                JSONObject json = new JSONObject(lastMessageJSON);
                String type = json.optString("type");
                if(!"message".equals(type)) return;

                String subtype = json.optString("subtype"); //MODOSITANI KELL
                String from = json.optString("from");
                String content = json.optString("content");

                boolean isPrivate = "private".equals(subtype);
                chatPersoUI.appendFormattedMessage(from, content, isPrivate);

                // if("private".equals(subtype)){ //avant la colorisation des messages
                //     //affichage de message "privé" avec préfixe
                //     chatArea.append("**[MP de " + from + "]** " + content + "\n");
                // }else{
                //     //affichage de message "global"
                //     chatArea.append(from + " : " + content + "\n");
                // }

                chatPersoUI.scrollToBottom();
            } catch (Exception e) {
                System.out.println("Erreur dans afficherDernierMessage() " + e.getMessage());
            }
        }
    }

    /*//décision quand le modèle doit agir p.ex. lors de la connexion ou de l'ouverture de la session
    public void notifierStatutEnLigne(){
        clientInvite.notifyOnlineStatus();
    }*/

    //
    public void demanderListeUtilisateurs() {
        clientInvite.requestUserList();
    }

    public void handleConnexion(String pseudoText, String ipText, String portText){
        int port;

        //pour éviter que le port invalide plant l'application
        try{
            port = Integer.parseInt(portText.trim());
        }catch (NumberFormatException ex){
            // chatArea.append("⚠ Le port doit être un nombre entier valide.\n"); //avant la colorisation des messages
            chatPersoUI.appendFormattedMessage("Système", "⚠ Le port doit être un nombre entier valide.", false);
            return;
        }

        if(!pseudoText.isEmpty() && !ipText.isEmpty()){
            // chatArea.append("Connexion en cours..."); //avant la colorisation des messages
            chatPersoUI.appendFormattedMessage("Système", "Connexion en cours...", false);

            //Instancier ClientInvite avec le pseudo
            clientInvite.setPseudo(pseudoText);

            //Lancer la connexion
            if(clientInvite.connect(ipText, port)){
                // chatArea.append("Connecté au serveur " + ipText + " : " + port + "\n"); //avant la colorisation des messages
                chatPersoUI.appendFormattedMessage("Système", " Connecté au serveur " + ipText + " : " + port, false);
            }else{
                // chatArea.append("Erreur de connexion. \n"); //avant la colorisation des messages
                chatPersoUI.appendFormattedMessage("Système", " Erreur de connexion.", false);
            }
        }else{
            // chatArea.append("Entrez un pseudo, un adresse IP et un adresse Port \n"); //avant la colorisation des messages
            chatPersoUI.appendFormattedMessage("Système", "Entrez un pseudo, une adresse IP et un port.", false);
        }
    }

}
