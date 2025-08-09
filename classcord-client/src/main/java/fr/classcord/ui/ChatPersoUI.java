package fr.classcord.ui;

//Frame du TChat
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; //gestion de la mise en page
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList; //gestion des actions utilisateur
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities; //gestion des actions utilisateur
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import fr.classcord.controller.ChatController;
import fr.classcord.model.ClientInvite;
import fr.classcord.model.UserColorManager;

public class ChatPersoUI extends JFrame {

    //propriétés
    //final => l’instancier une seule fois dans le constructeur && plus modifier après
    private final JPanel contentPane;
    private final ChatController controller;

    // private final JTextArea chatArea;
    private final JTextPane chatArea = new JTextPane();
    private final StyledDocument doc = chatArea.getStyledDocument();

    private final JTextField inputField;
    private final JButton sendButton;
    private final ClientInvite clientInvite;

    //pour afficher les pax connectés
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    private final UserColorManager colorManager = new UserColorManager();

    //pour gérer le status des utilisateurs
    private final Map<String, String> userStatuses = new HashMap<>();
    private final JComboBox<String> statusComboBox = new JComboBox<>(new String[] {"En ligne", "Absent", "Invisible", "Indisponible"});

    private final JButton globalButton = new JButton("Global");


    //méthodes

    //afin d'afficher l'écriture des utilisatuers en couleur
    public void appendFormattedMessage(String from, String content, boolean isPrivate){
        try {
            //Message coloré pour l'utilisateur
            Style fullStyle = chatArea.addStyle("full_" + from, null);
            StyleConstants.setForeground(fullStyle, colorManager.getColorForUser(from));
            StyleConstants.setBold(fullStyle, true); //font Bold

            String textToInsert;
            if(isPrivate){
                textToInsert = "**[MP de " + from + "]** " + content + "\n";
            }else{
                textToInsert = from + " : " + content + "\n";
            }

            doc.insertString(doc.getLength(), textToInsert, fullStyle);
            chatArea.setCaretPosition(doc.getLength());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { // Appelle la méthode pour envoyer le message
            String messageText = inputField.getText().trim();
            String selectedUser = userList.getSelectedValue();
            controller.sendMessage(messageText, selectedUser);
            inputField.setText(""); // vider le champ après envoi
        }
    }

    // placer le curseur de texte (caret) à la fin du contenu du chatArea afin de voir le dernier message
    public void scrollToBottom() {
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
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

    public void updateLocalUserStatus(String pseudo, String state){
        //mettre à jour le map "userStatuses" => stocke les statuts de tous les utilisateurs
        userStatuses.put(pseudo, state);

        //retirer user local de la liste graphique de tous les utilisateurs connectés affichée dans la JList
        userListModel.removeElement(pseudo);

        //déclenche une actualisation de l'interface graphique.
        userListModel.addElement(pseudo);

        // rafraîchit complètement le rendu (comme un repaint)
        userList.setCellRenderer(new UserStatusRenderer(userStatuses));  // Reforçage du rendu
    }


    //Constructeur
    public ChatPersoUI(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
        this.controller = new ChatController(clientInvite, this, null);

        clientInvite.setChatPersoUI(this); //Associer ClientInvite à ChatPersoUI

        // Lancer l'écoute des messages (une seule fois !)
        clientInvite.listenForMessages();
        controller.demanderListeUtilisateurs();

        System.out.println("Pseudo dans ChatPersoUI : " + clientInvite.getPseudo());

        setTitle("Tchat de " + clientInvite.getPseudo());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre


        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        // contentPane.setLayout(null);

        // Zone de chat (non éditable)
        // chatArea = new JTextArea(); //avant la colorisation des messages
        chatArea.setEditable(false); //non modifiable
        // chatArea.setLineWrap(true); //retour à la ligne est automatique //avant la colorisation des messages
        chatArea.setPreferredSize(new Dimension(400, 300));

        JScrollPane scrollPane = new JScrollPane(chatArea); //scroller sur chatArea

        //mettre le chatArea au milieu
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Panel pour l'entrée de message et le bouton envoyer
        JPanel inputPanel = new JPanel(new BorderLayout());

        inputField = new JTextField();
        inputField.addActionListener(new ChatPersoUI.SendMessageListener()); //en appuyant sur Entrée =>message est envoyé

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

        //pour afficher les pax connectés
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setPreferredSize(new Dimension(150, 0));
        add(userScroll, BorderLayout.EAST); // pour mettre à droite

        JPanel eastPanel = new JPanel(new BorderLayout());
        eastPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
        eastPanel.add(globalButton, BorderLayout.SOUTH);
        eastPanel.setPreferredSize(new Dimension(150, 0));
        add(eastPanel, BorderLayout.EAST);

        globalButton.addActionListener(e -> userList.clearSelection());

        userList.addListSelectionListener(e -> {
            String selected = userList.getSelectedValue();
            if (selected != null) {
                globalButton.setText("↩ Global");
            } else {
                globalButton.setText("Global");
            }
        });

        // Panel en haut pour le statut
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Statut : "), BorderLayout.WEST);
        topPanel.add(statusComboBox, BorderLayout.CENTER);

        // Ajouter un listener pour envoyer le statut lorsqu'il est changé

        statusComboBox.addActionListener(e -> {
            String selection = (String) statusComboBox.getSelectedItem();
            controller.envoyerStatut(selection);
        });
        // Ajoute ce panel en haut de la fenêtre
        contentPane.add(topPanel, BorderLayout.NORTH);

        //pour tester s'il s'affiche dans le constructeur => répoons: oui
        // SwingUtilities.invokeLater(() -> {
        //     Map<String, String> testMap = new HashMap<>();
        //     testMap.put("Alice", "online");
        //     testMap.put("Bob", "online");
        //     updateUserList(testMap);
        // });
    }


    //Troisième jour: 18 juin 25 =>peut être mettre en commentaire
    //Méthode principale pour la ChatPersoUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //pour lancer l'UI dans le bon thread (thread graphique)

            //Instanciation du clientInvite
            ClientInvite clientInvite = new ClientInvite("invité");
            ChatPersoUI ui = new ChatPersoUI(clientInvite);
            ui.setVisible(true);
        });
    }


}












