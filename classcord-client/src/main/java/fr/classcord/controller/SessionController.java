package fr.classcord.controller;

import fr.classcord.model.ClientInvite;
import fr.classcord.ui.ChatPersoUI;
import fr.classcord.ui.ChoixModeUI;
import fr.classcord.ui.GuestUI;
import fr.classcord.ui.LoginUI;

import javax.swing.*;

public class SessionController {

    //propriétés
    private ClientInvite clientInvite;

    //constructeur par défaut
    public SessionController() {
        this.clientInvite = new ClientInvite("invité");
    }

    //constructeur avec client parsonnalisé
    public SessionController(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
    }

    //méthodes

    public void connecterAuServeur(String ip, String portStr, JFrame parentFrame){
        int port;

        try {
            //transtypage de String en int
            port = Integer.parseInt(portStr.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, "Le port doit être un nombre valide !");
            return;
        }

        if (ip.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Le adresse IP est vide !");
            return;
        }

        //création d'un modèle
        //ClientInvite clientInvite = new ClientInvite("invité"); // Utilisation d’un pseudo temporaire
        //création un contrôleur
        //ConnectionController connectionController = new ConnectionController(clientInvite);

        //Appel grâce au contrôleur
        boolean connected = clientInvite.connect(ip, port);

        if (connected) {
            JOptionPane.showMessageDialog(parentFrame, "Connexion réussie au serveur " + ip + " : " + port);
            parentFrame.dispose(); // Fermer ConnexionToServeur
            SwingUtilities.invokeLater(() -> new ChoixModeUI(clientInvite).setVisible(true));
        } else {
            JOptionPane.showMessageDialog(parentFrame, "Erreur de connexion au serveur.");
        }
    }

    public void connecterAuChat(String pseudo, JFrame parentFrame){

        if (pseudo == null || pseudo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(parentFrame, "Veuillez entrer un pseudo.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // récupération le pseudo du client
        clientInvite.setPseudo(pseudo);
        clientInvite.listenForMessages();

        // Connexion au serveur
        new Thread(() -> {
            // clientInvite.connect("10.0.108.52", 12345); // Adresse et port à ajuster si besoin

            // Une fois connecté, lancer l'interface de chat dans le thread Swing
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(parentFrame, "Bienvenue " + pseudo + " !");
                new ChatPersoUI(clientInvite).setVisible(true);
                parentFrame.dispose(); // Fermer la fenêtre d'invité
            });
        }).start();
    }

    // décision de quel interface à ouvrir
    public void ouvrirFenetreGuestUI() {
        SwingUtilities.invokeLater(() -> new GuestUI(clientInvite).setVisible(true));
    }

    public void ouvrirLoginUI() {
        SwingUtilities.invokeLater(() -> new LoginUI(clientInvite).setVisible(true));
    }

    public ClientInvite getClientInvite() {
        return clientInvite;
    }
}