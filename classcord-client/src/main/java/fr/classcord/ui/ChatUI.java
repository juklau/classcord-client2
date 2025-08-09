package fr.classcord.ui;

//Frame du TChat
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent; //gestion de la mise en page
import java.awt.event.ActionListener; //gestion des actions utilisateur
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import fr.classcord.controller.ChatController;


import fr.classcord.model.ClientInvite;
import fr.classcord.ui.ChatPersoUI;

public class ChatUI extends JFrame {

    //propriétés
    private final JPanel contentPane;
    private final ChatController controller;
    private final ChatPersoUI chatPersoUI;

    private final JTextField adresseIP;
    private final JTextField adressePort;
    private final JTextField pseudo;
    // private final JTextArea chatArea;  //avant la colorisation des messages
    private final JTextPane chatArea = new JTextPane();
    // private final StyledDocument doc = chatArea.getStyledDocument();
    private final JTextField inputField;
    private final JButton sendButton;
    private ClientInvite clientInvite;

    //pour afficher les pax connectés
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);

    //pour gérer le status des utilisateurs
    private final Map<String, String> userStatuses = new HashMap<>();
    private final JComboBox<String> statusComboBox = new JComboBox<>(new String[] {"En ligne", "Absent", "Invisible", "Indisponible"});


    //méthodes

    private class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { // Appelle la méthode pour envoyer le message
            String messageText = inputField.getText().trim();
            String selectedUser = userList.getSelectedValue();
            controller.sendMessage(messageText, selectedUser);
            inputField.setText(""); // vider le champ après envoi
        }
    }

    //pour mettre à jour dynamiquement les personnes connectés en modifiant l'interface graphique
    public void updateUserList(Map<String, String> userMap){
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            // userStatuses.clear();
            System.out.println("Mise à jour de la liste d'utilisateurs connectés :");
            String localUser = clientInvite.getPseudo();
            for (Map.Entry<String, String> entry : userMap.entrySet()) {
                String pseudo = entry.getKey();
                String statut = entry.getValue();
                boolean isLocalUser = localUser != null && localUser.equals(pseudo);
                if (!statut.equalsIgnoreCase("invisible") || isLocalUser) {

                    // Si c'est le localUser ET qu'on a déjà un statut, on ne remplace pas
                    if (isLocalUser && userStatuses.containsKey(localUser)) {
                        // On conserve l'ancien statut local
                        System.out.println("Conservation du statut local existant : " + userStatuses.get(localUser));
                    } else {
                        // Sinon on met à jour le statut normalement
                        userStatuses.put(pseudo, statut);
                    }
                    if (!userListModel.contains(pseudo)) {
                        userListModel.addElement(pseudo);
                    }
                    System.out.println("✔ " + pseudo + " : " + statut);
                }
                // if("online".equalsIgnoreCase(statut)) { //avant le différantiation de "statut"
                //     userListModel.addElement(pseudo);
                //     System.out.println("Résultat: " + pseudo + " est en ligne."); //ça fonctionne
                // }
            }
            //ajouter le pseudo local s'il n'est pas déjà dans la liste
            if(localUser != null && !userMap.containsKey(localUser)){

                //pour éviter qu'il me remets à chaque evenement en couleur vert en écrasant mon statut existant
                if(!userStatuses.containsKey(localUser)){
                    String currentStatus = (String) statusComboBox.getSelectedItem();
                    String normalizedStatus = switch (currentStatus) {
                        case "Absent" -> "away";
                        case "Indisponible" -> "dnd";
                        case "Invisible" -> "invisible";
                        case "En ligne", "Disponible" -> "online";
                        default -> "online";
                    };
                    userStatuses.put(localUser, normalizedStatus);
                    System.out.println("Statut local déterminé depuis statusComboBox : " + normalizedStatus);
                }else{
                    System.out.println("Statut local conservé : " + userStatuses.get(localUser));
                }
                if (!userListModel.contains(localUser)) {
                    userListModel.addElement(localUser);
                }
                System.out.println("Ajout manuel de l'utilisateur local : " + localUser);
            }
            userList.setCellRenderer(new UserStatusRenderer(userStatuses));
        });
    }


    //Constructeur
    public ChatUI(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
        this.chatPersoUI = new ChatPersoUI(clientInvite);
        this.controller = new ChatController(clientInvite, null, this);

        clientInvite.setChatUI(this);

        setTitle("Tchat de " + clientInvite.getPseudo());
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
        adresseIP = new JTextField("10.0.108.78", 10);
        topPanel.add(adresseIP);

        // Adresse Port
        topPanel.add(new JLabel("Adresse Port: "));
        adressePort = new JTextField("12345", 10);
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
        btnConnexion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                String pseudoText = pseudo.getText().trim();
                String ipText = adresseIP.getText().trim();
                String portText = adressePort.getText().trim();

                controller.handleConnexion(pseudoText, ipText, portText);
            }
        });

        // Zone de chat (non éditable)
        // chatArea = new JTextArea(); //avant la colorisation des messages
        chatArea.setEditable(false); //non modifiable
        chatArea.setPreferredSize(new Dimension(400, 300));
        // chatArea.setLineWrap(true); //retour à la ligne est automatique //avant la colorisation des messages

        JScrollPane scrollPane = new JScrollPane(chatArea); //scroller sur chatArea

        //mettre le chatArea au milieu
        contentPane.add(scrollPane, BorderLayout.CENTER);

        //Panel pour l'entrée de message et le bouton envoyer
        JPanel inputPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        inputField.addActionListener(new ChatUI.SendMessageListener()); //en appuyant sur Entrée =>message est envoyé

        // Bouton envoyer
        sendButton = new JButton("Envoyer");
        sendButton.addActionListener(e -> {
            String messageText = inputField.getText().trim();
            String selectedUser = userList.getSelectedValue();
            controller.sendMessage(messageText, selectedUser);
            inputField.setText(""); // vider le champ
        });

        inputPanel.add(inputField, BorderLayout.CENTER); //champ de saisie au centre
        inputPanel.add(sendButton, BorderLayout.EAST); //mettre le bouton à droite

        // Ajout des composants
        contentPane.add(inputPanel, BorderLayout.SOUTH); //mettre le champ et le bouton en bas
    }


    //Deuxième jour:17 juin 25 =>peut être mettre en commentaire
    // //Méthode principale pour la ChatInterface
     public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //pour lancer l'UI dans le bon thread (thread graphique)

            //Instanciation du clientInvite
            ClientInvite clientInvite = new ClientInvite("invité");
           ChatUI  ui = new ChatUI (clientInvite);
            ui.setVisible(true);
       });
     }


}
