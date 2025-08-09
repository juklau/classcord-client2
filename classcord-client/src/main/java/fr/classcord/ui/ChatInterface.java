/*
package fr.classcord.ui;

//CETTE INTERFACE NE FONCTIONNE PLUS

 //Frame du TChat
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent; //gestion de la mise en page
import java.awt.event.ActionListener; //gestion des actions utilisateur
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame; //gestion des actions utilisateur
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.json.JSONObject;

import fr.classcord.model.ClientInvite;

public class ChatInterface extends JFrame {

    //propriétés
    private final JPanel contentPane;
    private final JTextField adresseIP;
    private final JTextField adressePort;
    private final JTextField pseudo;
    // private final JTextArea chatArea;  //avant la colorisation des messages
    private final JTextPane chatArea = new JTextPane();
    // private final StyledDocument doc = chatArea.getStyledDocument();
    private final JTextField inputField;
    private final JButton sendButton;
    private ClientInvite clientInvite;
    private ChatInterfacePerso chatInterfacePerso;

    //pour afficher les pax connectés
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);

    //Constructeur
    public ChatInterface(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
        clientInvite.setChatInterface(this);
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
                btnConnexion_clic();
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
        inputField.addActionListener(new SendMessageListener()); //en appuyant sur Entrée =>message est envoyé
        // Bouton envoyer
        sendButton = new JButton("Envoyer");
        sendButton.addActionListener(e -> sendMessage());
        inputPanel.add(inputField, BorderLayout.CENTER); //champ de saisie au centre
        inputPanel.add(sendButton, BorderLayout.EAST); //mettre le bouton à droite
        // Ajout des composants
        contentPane.add(inputPanel, BorderLayout.SOUTH); //mettre le champ et le bouton en bas
    }

    //méthodes

    //connexion au serveur en cliquant sur le "connexion" et en entrant tous les paramètres
    private void btnConnexion_clic(){
       String pseudoText = pseudo.getText().trim();
       String ipText = adresseIP.getText().trim();
       int port;

       //pour éviter que le port invalide plant l'application
       try{
            port = Integer.parseInt(adressePort.getText().trim());
       }catch (NumberFormatException ex){
            // chatArea.append("⚠ Le port doit être un nombre entier valide.\n"); //avant la colorisation des messages
            chatInterfacePerso.appendFormattedMessage("Système", "⚠ Le port doit être un nombre entier valide.", false);
            return;
       }
       if(!pseudoText.isEmpty() && !ipText.isEmpty()){
            // chatArea.append("Connexion en cours..."); //avant la colorisation des messages
            chatInterfacePerso.appendFormattedMessage("Système", "Connexion en cours...", false);
            //Instancier ClientInvite avec le pseudo
            clientInvite = new ClientInvite(pseudoText);
            //Lancer la connexion
            if(clientInvite.connect(ipText, port)){
                // chatArea.append("Connecté au serveur " + ipText + " : " + port + "\n"); //avant la colorisation des messages
                 chatInterfacePerso.appendFormattedMessage("Système", "✅ Connecté au serveur " + ipText + " : " + port, false);
            }else{
                // chatArea.append("Erreur de connexion. \n"); //avant la colorisation des messages
                chatInterfacePerso.appendFormattedMessage("Système", "❌ Erreur de connexion.", false);
            } 
       }else{
            // chatArea.append("Entrez un pseudo, un adresse IP et un adresse Port \n"); //avant la colorisation des messages
           chatInterfacePerso.appendFormattedMessage("Système", "Entrez un pseudo, une adresse IP et un port.", false);
       }
    }

    // Envoi de message via `ClientInvite`
    //atrakva chatcontrolleer et chatui
    private void sendMessage() {
        if(clientInvite != null){
            String messageText = inputField.getText().trim(); //on "lit" le texte du champ
            if(messageText.isEmpty()){
                return;
            }
            //sélectionner utilisateur
            String selectedUser= userList.getSelectedValue();
            JSONObject json = new JSONObject();
            json.put("type", "message");
            json.put("content", messageText);
            if(selectedUser != null && !selectedUser.isEmpty()){
                //Envoyer un message "privé"
                json.put("subtype", "private");
                json.put("to", selectedUser);
                //Envoie message privé avec préfixe
                // chatArea.append("**[MP à " + selectedUser + "]** " + messageText + "\n"); //avant la colorisation des messages
                chatInterfacePerso.appendFormattedMessage(clientInvite.getPseudo(), "**[MP à " + selectedUser + "]** " + messageText + "\n", true);
            }else{
                //envoyer un message "global"
                json.put("subtype", "global");
                json.put("to", "global");
                // chatArea.append("Vous: " + messageText + "\n"); //avant la colorisation des messages
                chatInterfacePerso.appendFormattedMessage(clientInvite.getPseudo(), messageText, false);
            }
            clientInvite.send(json.toString());
            inputField.setText("");
        }else{
            // chatArea.append("Erreur : Vous devez être connecté avant d'envoyer un message.\n"); //avant la colorisation des messages
            chatInterfacePerso.appendFormattedMessage("Système", "Erreur : Vous devez être connecté avant d'envoyer un message.\n", false);
        }        
    }

    // atrakva chatui
    private class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage(); // Appelle la méthode pour envoyer le message
        }
    }

   //pour mettre à jour dynamiquement les personnes connectés
   // atrakva chatui => updateUserList(Map<String, String> userMap) => modositva!!
   // atrakva chatcontroller =>handleUserListUpdate(Map<String, String> userMap)
   // es a clientinvite-ban hivom
    public void updateUserList(Map<String, String> userMap){
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            System.out.println("🧾 Mise à jour de la liste d'utilisateurs connectés :");
            for (Map.Entry<String, String> entry: userMap.entrySet()){ //ex: dodo, online
                String pseudo = entry.getKey();
                String statut = entry.getValue();
                if("online".equalsIgnoreCase(statut)) {
                    userListModel.addElement(pseudo);
                    System.out.println("Résultat: " + pseudo + " est en ligne."); //ça fonctionne 
                }
            }
            //ajouter le pseudo local s'il n'est pas déjà dans la liste
            String localUser = clientInvite.getPseudo();
            if(localUser != null && !localUser.isEmpty() && !userListModel.contains(localUser)){
                userListModel.addElement(localUser);
                System.out.println("Ajoute de l'utilisateur local : " + localUser);
            }
        });
    }

    // Afficher le dernier message reçu
    // atrakva chatController sous afficherDernierMessage()
    public void afficheMessage(){
        String lastMessageJSON = clientInvite.getLastMessage();
        if(lastMessageJSON != null && !lastMessageJSON.isEmpty()){
            try {
                JSONObject json = new JSONObject(lastMessageJSON);
                String type = json.optString("type");
                if(!"message".equals(type)){
                    return;
                }
                String subtype = json.optString("subtype");
                String from = json.optString("from");
                String content = json.optString("content");

                chatInterfacePerso.appendFormattedMessage(from, content, "private".equals(subtype));
                // if("private".equals(subtype)){ //avant la colorisation des messages
                //     //affichage de message "privé" avec préfixe
                //     chatArea.append("**[MP de " + from + "]** " + content + "\n");
                // }else{
                //     //affichage de message "global"
                //     chatArea.append(from + " : " + content + "\n");
                // }
                // placer le curseur de texte (caret) à la fin du contenu du chatArea afin de voir le dernier message
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            } catch (Exception e) {
                System.out.println("Erreur dans afficheMessage() " + e.getMessage());
            }
        }
    }
    

    //Deuxième jour:17 juin 25 =>peut être mettre en commentaire
    // //Méthode principale pour la ChatInterface
    // atrakva chatui
    // public static void main(String[] args) {
    //     SwingUtilities.invokeLater(() -> { //pour lancer l'UI dans le bon thread (thread graphique)

    //         //Instanciation du clientInvite
    //         ClientInvite clientInvite = new ClientInvite("invité"); 
    //         ChatInterface ui = new ChatInterface(clientInvite);
    //         ui.setVisible(true); 
    //     });
    // }
}
*/


