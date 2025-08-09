package fr.classcord.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.classcord.controller.SessionController;
import fr.classcord.model.ClientInvite;


public class GuestUI extends JFrame {

   //propriétés
    private final JTextField pseudoUsername;
    private final JButton btnConnexionChat;
    private final SessionController controller;


    //Contstucteur
    public GuestUI(ClientInvite clientInvite) {
        this.controller = new SessionController(clientInvite);
        // initComponents();

        setTitle("Connexion au Chat");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre

        JPanel parentPanel = new JPanel(new BorderLayout());
        //BorderFactory.createEmptyBorder() !!!! => crée une bordure invisible,un marge intérieure pour le panel.
        parentPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Votre Pseudo: "));
        pseudoUsername = new JTextField("");
        panel.add(pseudoUsername);

        btnConnexionChat = new JButton("Connexion au Chat");
        panel.add(new JLabel()); //pour espacement
        panel.add(btnConnexionChat);

        parentPanel.add(panel, BorderLayout.CENTER);
        add(parentPanel);

        btnConnexionChat.addActionListener(e -> {
            controller.connecterAuChat(pseudoUsername.getText(), this);
        });

        setVisible(true);
    }


    //Troisième et Quatrième jour: 18-19 juin 25 
    //Méthode principale pour la GuestUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientInvite tempClient = new ClientInvite("invité");
            new GuestUI(tempClient).setVisible(true);
        });
    }
}
