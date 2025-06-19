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
import javax.swing.JFrame; //gestion des actions utilisateur
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField; //gestion des actions utilisateur
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import fr.classcord.network.ClientInvite;



public class ChatInterfacePerso extends JFrame {

    //propriétés
    private final JPanel contentPane;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final ClientInvite clientInvite;

    //pour afficher les pax connectés
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    private final Map<String, Color> userColors = new HashMap<>();


    private final JButton globalButton = new JButton("Global");

    

    //Constructeur
    public ChatInterfacePerso(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
        clientInvite.setChatInterfacePerso(this); //Associer ClientInvite à ChatInterfacePerso

        // Lancer l'écoute des messages (une seule fois !)
        clientInvite.listenForMessages();
        clientInvite.requestUserList();

        System.out.println("Pseudo dans ChatInterfacePerso : " + clientInvite.getPseudo());

        setTitle("Tchat de " + clientInvite.getPseudo());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre


        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        // contentPane.setLayout(null);

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
        sendButton.addActionListener(e -> sendMessage());


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

        //pour tester s'il s'affiche
        // SwingUtilities.invokeLater(() -> {
        //     Map<String, String> testMap = new HashMap<>();
        //     testMap.put("Alice", "online");
        //     testMap.put("Bob", "online");
        //     updateUserList(testMap);
        // });
    }

    // Envoi de message via `ClientInvite`
    private void sendMessage() {
        if(clientInvite != null){
            String messageText = inputField.getText().trim(); //on "lit" le texte du champ
            //  AVANT la séparation => individuel ou global
            // if (!messageText.isEmpty()) {
            //     clientInvite.sendMessage(messageText);
            //     chatArea.append("Vous: " + messageText + "\n"); //afficher le message
            //     inputField.setText(""); //vider le champ  
            // }

            if(messageText.isEmpty()){
                return;
            }

            //sélectionner utilisateur
            String selectedUser= userList.getSelectedValue();

            JSONObject json = new JSONObject();
            json.put("type", "message");
            json.put("content", messageText);

            if(selectedUser != null && !selectedUser.isEmpty()){
                //Envoyer un message privé
                json.put("subtype", "private");
                json.put("to", selectedUser);

                //affichage message privé avec préfixe
                chatArea.append("**[MP à " + selectedUser + "]** " + messageText + "\n");
            }else{
                //envoyer un message global
                json.put("subtype", "global");
                chatArea.append("Vous: " + messageText + "\n"); //afficher le message
            }
            clientInvite.send(json.toString());
            inputField.setText("");
        }else{
            chatArea.append("Erreur : Vous devez être connecté avant d'envoyer un message.\n");
        }        
    }

    private class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage(); // Appelle la méthode pour envoyer le message
        }
    }

   
    private Color getColorForUser(String user){
        int hash = user.hashCode();
        int r = (hash >> 16) & 0xFF;
        int g = (hash >> 8) & 0xFF;
        int b = hash & 0xFF;

        Color base = new Color(r, g, b);
        return base.brighter(); //pour pas être trop foncé le couleur
    }


    // Afficher le dernier message reçu
    public void afficheMessage(){
        String lastMessageJSON = clientInvite.getLastMessage();
        if(lastMessageJSON != null && !lastMessageJSON.isEmpty()){
          
            //  AVANT la séparation => individuel ou global
            // Message lastMessageString = Message.fromJson(lastMessageJSON); //convertion en message "normal"
            // chatArea.append("Message reçu de " + lastMessageString.getFrom() + lastMessageString.getContent() + "\n");
            // chatArea.repaint();
            // chatArea.revalidate();

            try {
                JSONObject json = new JSONObject(lastMessageJSON);

                String type = json.optString("type");
                if(!"message".equals(type)){
                    return;
                }
                
                String subtype = json.optString("subtype"); //MODOSITANI KELL
                String from = json.optString("from");
                String content = json.optString("content");

                if("private".equals(subtype)){

                    //affichage message privé avec préfixe
                    chatArea.append("**[MP à " + from + "]** " + content + "\n");
                }else{
                    chatArea.append("Message reçu de " + from + " : " + content + "\n");
                }
                // placer le curseur de texte (caret) à la fin du contenu du chatArea afin de voir le dernier message
                chatArea.setCaretPosition(chatArea.getDocument().getLength());

            } catch (Exception e) {
                System.out.println("Erreur dans afficheMessage() " + e.getMessage());
            }
        }
    }

    //pour mettre à jour dynamiquement les personnes connectés
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

    //Troisième jour: 18 juin 25 =>peut être mettre en commentaire
    //Méthode principale pour la ChatInterface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //pour lancer l'UI dans le bon thread (thread graphique)

            //Instanciation du clientInvite
            ClientInvite clientInvite = new ClientInvite("invité"); 
            ChatInterfacePerso ui = new ChatInterfacePerso(clientInvite);
            ui.setVisible(true); 
        });
    }

    
}
