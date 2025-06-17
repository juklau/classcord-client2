package fr.classcord.ui;


 //Frame du TChat
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent; //gestion de la mise en page
import java.awt.event.ActionListener; //gestion des actions utilisateur

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel; //gestion des actions utilisateur
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


public class ChatInterface extends JFrame {

    private final JPanel contentPane;
    private final JTextField adresseIP;
    private final JTextField adressePort;
    private final JTextField pseudo;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;

    //Constructeur
    public ChatInterface() {

        setTitle("Tchat Simple");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre


        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        // contentPane.setLayout(null);

        //création d'un panel superieur
       // Création d'un panel supérieur pour l'IP, le Port et le Pseudo
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Aligner à gauche
        topPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Espacement

        // Adresse IP
        topPanel.add(new JLabel("Adresse IP: "));
        adresseIP = new JTextField(10);
        topPanel.add(adresseIP);

        // Adresse Port
        topPanel.add(new JLabel("Adresse Port: "));
        adressePort = new JTextField(10);
        topPanel.add(adressePort);

        // Pseudo
        topPanel.add(new JLabel("Pseudo: "));
        pseudo = new JTextField(10);
        topPanel.add(pseudo);

        // Bouton de connexion
        JButton btnConnexion = new JButton("Connexion");
        topPanel.add(btnConnexion);

        //mettre le topPanel en haut
        contentPane.add(topPanel, BorderLayout.NORTH);

        //Connexion en cliquant sur le bouton
        // btnConnexion.addMouseListener(new MouseAdapter() {
        // @Override
        // public void mouseClicked(MouseEvent e){
        //     btnConnexion_clic();
        // }
        // });
         

        // Zone de chat (non éditable)
        chatArea = new JTextArea();
        chatArea.setEditable(false); //non modifiable
        chatArea.setLineWrap(true); //retour à la ligne est automatique

        JScrollPane scrollPane = new JScrollPane(chatArea); //scroller sur chatArea

        //mettre le chatArea au milieu
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Panel pour l'entrée de message et le bouton envoyer
        JPanel inputPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        inputField.addActionListener(new SendMessageListener()); //en appuyant sur Entrée =>message est envoyé

        // Bouton envoyer
        sendButton = new JButton("Envoyer");

        inputPanel.add(inputField, BorderLayout.CENTER); //champ de saisie au centre
        inputPanel.add(sendButton, BorderLayout.EAST); //mettre le bouton à droite

        // Ajout des composants
        contentPane.add(inputPanel, BorderLayout.SOUTH); //mettre le champ et le bouton en bas
    }


    // public ChatInterface() {
    //     setTitle("Tchat Simple");
    //     setSize(700, 500);
    //     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Fermeture automatique
    //     setLocationRelativeTo(null); // Centrer la fenêtre

    //     // Définir un BorderLayout pour le contentPane
    //     contentPane = new JPanel(new BorderLayout());
    //     setContentPane(contentPane);

    //     // Création d'un panel supérieur pour l'IP, le Port et le Pseudo
    //     JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Aligner à gauche
    //     topPanel.setBorder(new EmptyBorder(5, 5, 5, 5)); // Espacement

    //     // Adresse IP
    //     topPanel.add(new JLabel("Adresse IP: "));
    //     adresseIP = new JTextField(10);
    //     topPanel.add(adresseIP);

    //     // Adresse Port
    //     topPanel.add(new JLabel("Adresse Port: "));
    //     adressePort = new JTextField(10);
    //     topPanel.add(adressePort);

    //     // Pseudo
    //     topPanel.add(new JLabel("Pseudo: "));
    //     pseudo = new JTextField(10);
    //     topPanel.add(pseudo);

    //     // Bouton de connexion
    //     JButton btnConnexion = new JButton("Connexion");
    //     topPanel.add(btnConnexion);

    //     // Ajouter topPanel en haut du contentPane
    //     contentPane.add(topPanel, BorderLayout.NORTH);

    //     // Zone de chat (non éditable)
    //     chatArea = new JTextArea();
    //     chatArea.setEditable(false); // Non modifiable
    //     chatArea.setLineWrap(true); // Retour à la ligne automatique

    //     JScrollPane scrollPane = new JScrollPane(chatArea); // Ajouter une barre de défilement
    //     contentPane.add(scrollPane, BorderLayout.CENTER); // Ajouter la zone de chat au centre

    //     // Panel pour l'entrée de message et le bouton envoyer
    //     JPanel inputPanel = new JPanel(new BorderLayout());
    //     inputField = new JTextField();
    //     inputField.addActionListener(new SendMessageListener()); // En appuyant sur Entrée => message envoyé

    //     sendButton = new JButton("Envoyer");
        
    //     inputPanel.add(inputField, BorderLayout.CENTER); // Champ de saisie au centre
    //     inputPanel.add(sendButton, BorderLayout.EAST); // Mettre le bouton à droite

    //     contentPane.add(inputPanel, BorderLayout.SOUTH); // Ajouter le champ et le bouton en bas
    // }



    // Action quand on envoie un message
    private class SendMessageListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String message = inputField.getText().trim(); //on "lit" le texte du champ
            if (!message.isEmpty()) {
                chatArea.append("Vous: " + message + "\n"); //afficher le message
                inputField.setText(""); //vider le champ
                // Ici, on pourrait envoyer le message à un serveur
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //pour lancer l'UI dans le bon thread (thread graphique)
            ChatInterface ui = new ChatInterface();
            ui.setVisible(true); 
        });
    }
}


