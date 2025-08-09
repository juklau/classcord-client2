package fr.classcord.ui;

 //Frame du connexion au serveur
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel; //gestion de la mise en page
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.classcord.controller.SessionController;
//import fr.classcord.model.ClientInvite;


public class ConnectToServeurUI extends JFrame {

    //propriétés
    private final JTextField adresseIPServeur;
    private final JTextField adressePortServeur;
    private final JButton btnConnexionServeur;
    private final SessionController controller;


    //Constructor
    public ConnectToServeurUI(){
        //ClientInvite clientInvite = new ClientInvite("invité");
        controller = new SessionController();

        setTitle("Connexion au Serveur");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre

        JPanel parentPanel = new JPanel(new BorderLayout());
        //BorderFactory.createEmptyBorder() !!!! => crée une bordure invisible,un marge intérieure pour le panel.
        parentPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Adresse IP: "));
        adresseIPServeur = new JTextField("127.0.0.1");
        panel.add(adresseIPServeur);

        panel.add(new JLabel("Adresse Port: "));
        adressePortServeur = new JTextField("12345");
        panel.add(adressePortServeur);

        btnConnexionServeur = new JButton("Connexion au Serveur");
        panel.add(new JLabel()); //pour espacement
        panel.add(btnConnexionServeur);

        parentPanel.add(panel, BorderLayout.CENTER);
        add(parentPanel);

        btnConnexionServeur.addActionListener(e -> {
            System.out.println("Bouton 'Connexion au Serveur' cliqué !");
            controller.connecterAuServeur(adresseIPServeur.getText(), adressePortServeur.getText(), this);
        });
    }


    //Deuxième et Troisième jour:17-18 juin 25 
    //Méthode principale pour la ConnectToServuerUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            new ConnectToServeurUI().setVisible(true);
        });
    }
}
