package fr.classcord.controller;

import org.json.JSONObject;

import javax.swing.*;

import fr.classcord.model.ClientInvite;

public class LoginController {

    //propriétés
    private final ClientInvite clientInvite;
    //private final ChatController chatController;

    //Constructeur
    public LoginController(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
    }


    //méthodes

    public void login(String username, String password, JFrame frame, Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            // toute interaction avec l'interface utilisateur Swing doit être exécutée dans le thread de l'UI →
            // et c’est exactement le rôle de SwingUtilities.invokeLater.
            try {
                String response = AuthController.login(clientInvite, username, password);
                JSONObject resp = new JSONObject(response);
                if(resp.optString("status").equals("ok")){
                    clientInvite.setPseudo(username);

                    clientInvite.notifyOnlineStatus(); //je dis au serveur que je suis "online"

                    // Ajoute l'utilisateur local à la liste onlineUsers
                    // onlineUsers.add(username);
                    // refreshUserList();

                    // préparation de la requête pour récupérer la lise des utilisateus en ligne
                    JSONObject requestUserList = new JSONObject();
                    requestUserList.put("type", "users");

                    //envoie la requête au serveur
                    clientInvite.send(requestUserList.toString());

                   SwingUtilities.invokeLater(onSuccess);
                }else{
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(frame, "Échec pendant l'authentification");
                        onFailure.run(); // réactiver les boutons
                    });

                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Erreur : " + e.getMessage());
                    onFailure.run(); // réactiver les boutons
                });
            }
        }).start();
    }

    public void registerThenLogin(String username, String password, JFrame frame, Runnable onSuccess,  Runnable onFailure) {
        new Thread(() -> {
            // toute interaction avec l'interface utilisateur Swing doit être exécutée dans le thread de l'UI →
            // et c’est exactement le rôle de SwingUtilities.invokeLater.
            try {
                String registerResponse = AuthController.register(clientInvite, username, password);
                JSONObject registerResp = new JSONObject(registerResponse);

                if(registerResp.optString("status").equals("ok")){

                    //après registration on fait le login
                    JOptionPane.showMessageDialog(frame, "Inscription réussie \n Veuillez-vous connectez!");
                    //String loginResponse = AuthController.login(clientInvite, username, password);
                    //JSONObject loginResp = new JSONObject(loginResponse);
                    login(username, password, frame, onSuccess, onFailure);

                }else{
                    //Erreur pendant l'inscription
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(frame, "Erreur pendant l'inscription");
                        onFailure.run(); // réactiver les boutons
                    });

                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, "Erreur : " + e.getMessage());
                    onFailure.run(); // réactiver les boutons
                });

            }
        }).start();
    }

    public ClientInvite getClientInvite() {
        return clientInvite;
    }
}
