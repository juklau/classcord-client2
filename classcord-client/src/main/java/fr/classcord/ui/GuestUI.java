package fr.classcord.ui;
import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.classcord.network.ClientInvite;



public class GuestUI extends JFrame {

   //propriétés
    private final JTextField pseudoUsername;
    private final JButton btnConnexionChat;
    private ClientInvite clientInvite;
    

    //Contstucteur
    public GuestUI(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
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

        btnConnexionChat.addActionListener(e -> btnConnexionChatClic());

        setVisible(true);
    }


    //méthodes

    private void btnConnexionChatClic() {
        String pseudo = pseudoUsername.getText().trim();
        if (pseudo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un pseudo.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Créer un client et lui attribuer le pseudo
        // clientInvite = new ClientInvite(pseudo);
        clientInvite.setPseudo(pseudo);

        clientInvite.listenForMessages();

        // Connexion au serveur
        new Thread(() -> {
            // clientInvite.connect("10.0.108.52", 12345); // Adresse et port à ajuster si besoin

            // Une fois connecté, lancer l'interface de chat dans le thread Swing
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this, "Bienvenue " + pseudo + " !");
                new ChatInterfacePerso(clientInvite).setVisible(true);
                dispose(); // Fermer la fenêtre d'invité

            });
        }).start();

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
