package fr.classcord.ui;

//Frame du TChat
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; //gestion de la mise en page
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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

import org.json.JSONObject;

import fr.classcord.network.ClientInvite;



public class ChatInterfacePerso extends JFrame {

    //propriétés
    private final JPanel contentPane;

    // private final JTextArea chatArea;
    private final JTextPane chatArea = new JTextPane();
    private final StyledDocument doc = chatArea.getStyledDocument();

    private final JTextField inputField;
    private final JButton sendButton;
    private final ClientInvite clientInvite;

    //pour afficher les pax connectés
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
    private final Map<String, Color> userColors = new HashMap<>();

    //pour gérer le status des utilisateurs
    private final Map<String, String> userStatuses = new HashMap<>();
    private final JComboBox<String> statusComboBox = new JComboBox<>(new String[] {"En ligne", "Absent", "Invisible", "Indisponible"});


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

        // Panel en haut pour le statut
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Statut : "), BorderLayout.WEST);
        topPanel.add(statusComboBox, BorderLayout.CENTER);

        // Ajouter un listener pour envoyer le statut lorsqu'il est changé
        statusComboBox.addActionListener(e -> envoyerStatut());

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

    //méthodes

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
                //Envoyer un message "privé"
                json.put("subtype", "private");
                json.put("to", selectedUser);

                //Envoie message privé avec préfixe
                // chatArea.append("**[MP à " + selectedUser + "]** " + messageText + "\n"); //avant la colorisation des messages
                appendFormattedMessage(clientInvite.getPseudo(), "**[MP à " + selectedUser + "]** " + messageText + "\n", true);
            }else{
                //envoyer un message "global"
                json.put("subtype", "global");
                json.put("to", "global");

                // chatArea.append("Vous: " + messageText + "\n"); //afficher le message //avant la colorisation des messages
                appendFormattedMessage(clientInvite.getPseudo(), messageText, false);
            }
            clientInvite.send(json.toString());
            inputField.setText("");
        }else{
            // chatArea.append("Erreur : Vous devez être connecté avant d'envoyer un message.\n"); //avant la colorisation des messages
            appendFormattedMessage("Système", "Erreur : Vous devez être connecté avant d'envoyer un message.\n", false);
        }        
    }

    private class SendMessageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage(); // Appelle la méthode pour envoyer le message
        }
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

                appendFormattedMessage(from, content, "private".equals(subtype));

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

    //pour mettre à jour dynamiquement les personnes connectés
    public void updateUserList(Map<String, String> userMap){
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            userStatuses.clear();
            System.out.println("🧾 Mise à jour de la liste d'utilisateurs connectés :");

            String localUser = clientInvite.getPseudo();
            for (Map.Entry<String, String> entry : userMap.entrySet()) {
                String pseudo = entry.getKey();
                String statut = entry.getValue();

                boolean isLocalUser = localUser != null && localUser.equals(pseudo);

                if (!statut.equalsIgnoreCase("invisible") || isLocalUser) {
                    userStatuses.put(pseudo, statut);

                    if (!userListModel.contains(pseudo)) {
                        userListModel.addElement(pseudo);
                    }
                }

                // if("online".equalsIgnoreCase(statut)) { //avant le différantiation de "statut"
                //     userListModel.addElement(pseudo);
                //     System.out.println("Résultat: " + pseudo + " est en ligne."); //ça fonctionne 
                // }
            }

            //ajouter le pseudo local s'il n'est pas déjà dans la liste
           if(localUser != null && !localUser.isEmpty() && !userListModel.contains(localUser)){
                userListModel.addElement(localUser);
                userStatuses.put(localUser, "online");
                System.out.println("Ajoute de l'utilisateur local : " + localUser);
            }

            userList.setCellRenderer(new UserStatusRenderer());
        });
    }

    //définir la couleur de chaque utilisateur
    public Color getColorForUser(String user){
        if (userColors.containsKey(user)) {
            return userColors.get(user);
        }

        int hash = Math.abs(user.hashCode()); //garantit que chaque pseudo aura une base différente
        // >> => décale les bits vers la droit
        // & 0xFF => pour garder que 8 bits (entre 0 et 255)
        //                                  => r bits 16 à 23
        //                                  => g bits 8 à 15
        //                                  => b bits 0 à 7
        int r = (hash >> 16) & 0xFF; 
        int g = (hash >> 8) & 0xFF;
        int b = hash & 0xFF;

        // afin d'empêcher d’avoir du noir ou du blanc
        r = (r + 100) % 256;
        g = (g + 100) % 256;
        b = (b + 100) % 256;

        Color color = new Color(r, g, b);
        userColors.put(user, color);
        return color;
    }

    //afin d'afficher l'écriture des utilisatuers en couleur
    public void appendFormattedMessage(String from, String content, boolean isPrivate){
        try {
            //Message coloré pour l'utilisateur
            Style fullStyle = chatArea.addStyle("full_" + from, null);
            StyleConstants.setForeground(fullStyle, getColorForUser(from));
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

    //pour envoyer le "statut" choisi
    private void envoyerStatut(){

        if (clientInvite == null || clientInvite.getPseudo() == null || clientInvite.getPseudo().isEmpty()) {
            System.err.println("Client non initialisé ou pseudo invalide.");
            return;
        }

        String selection = (String) statusComboBox.getSelectedItem();

        //correspondance entre libellé UI et codes de statut
         String state = switch (selection) {
            case "Absent" -> "away";
            case "Indisponible" -> "dnd";        // Do Not Disturb
            case "Invisible" -> "invisible";
            case "En ligne", "Disponible" -> "online"; // pour plus de flexibilité
            default -> "online";                 // sécurité par défaut
        };

        JSONObject json = new JSONObject();
        json.put("type", "status");
        json.put("user", clientInvite.getPseudo()); //pour l'identification côté serveur
        json.put("state", state);

        
        clientInvite.send(json.toString());

        // Mise à jour immédiate locale (avant que le serveur ne le renvoie)

        //mettre à jour le map "userStatuses" => stocke les statuts de tous les utilisateurs
        userStatuses.put(clientInvite.getPseudo(), state); 

        //retirer user local de la liste graphique de tous les utilisateurs connectés affichée dans la JList
        userListModel.removeElement(clientInvite.getPseudo());

        //déclenche une actualisation de l'interface graphique.
        userListModel.addElement(clientInvite.getPseudo());

        // rafraîchit complètement le rendu (comme un repaint)
        userList.setCellRenderer(new UserStatusRenderer());  // Reforçage du rendu
    }

    // === Classe interne pour personnaliser l'affichage des utilisateurs dans une JList (liste graphique). ===
    private class UserStatusRenderer extends DefaultListCellRenderer { 
        //extends DefaultListCellRenderer => permet de surcharger le rendu visuel des éléments dans une JList

        @Override
        // pour personnaliser l'apparence d'un élémnent de la liste
        public java.awt.Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String user = (String) value; //caster l'objet "value" en String
            String status = userStatuses.getOrDefault(user, "online"); //si le statut n'est pas trouvé => "online" par défault

            Color color;
            switch (status) {
                case "away" -> color = Color.ORANGE;
                case "dnd" -> color = Color.RED;
                case "invisible" -> color = Color.GRAY;
                default -> color = new Color(0, 153, 0); // vert foncé =>online
            }

            // label.setText(user + " (" + status + ")"); //affichage de statut avec text
            label.setText(user);
            label.setForeground(color); //application de la couleur chosie au texte du label
            label.setIcon(createStatusDot(color));
            label.setIconTextGap(8);
            label.setOpaque(true);
            label.setBackground(isSelected ? new Color(220, 220, 220) : Color.WHITE);
           
            return label; //sera affiché dans la JList
        }

        private Icon createStatusDot(Color color){
            int size = 14; //=>14 pixels
            BufferedImage image=  new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB); //création une image vide
            Graphics2D g2 = image.createGraphics(); //récupèration du "pinceau" (Graphics2D) pour dessiner dans l’image.

            //activation de l’antialiasing pour des bords lisses afin d'éviter le cercle pixélisé
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); 
            g2.setColor(color); //choix la couleur du cercel
            g2.fillOval(0, 0, size, size); //dessine un ovale rempli
            g2.dispose(); //libèration les ressources graphiques utilisées (bonne pratique avec Graphics2D).
            return new ImageIcon(image);
        }
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
