package fr.classcord.ui;

//Frame du TChat
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; //gestion de la mise en page

import javax.swing.JButton; //gestion des actions utilisateur
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea; //gestion des actions utilisateur
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.classcord.model.Message;
import fr.classcord.network.ClientInvite;



public class ChatInterfacePerso extends JFrame {

    //propriétés
    private final JPanel contentPane;
    private final JTextArea chatArea;
    private final JTextField inputField;
    private final JButton sendButton;
    private final ClientInvite clientInvite;
    

    //Constructeur
    public ChatInterfacePerso(ClientInvite clientInvite) {
        this.clientInvite = clientInvite;
        clientInvite.setChatInterfacePerso(this); //Associer ClientInvite à ChatInterfacePerso

        // Lancer l'écoute des messages (une seule fois !)
        clientInvite.listenForMessages();

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
    }

    // Envoi de message via `ClientInvite`
    private void sendMessage() {
        if(clientInvite != null){
            String messageText = inputField.getText().trim(); //on "lit" le texte du champ
            if (!messageText.isEmpty()) {
                clientInvite.sendMessage(messageText);
                chatArea.append("Vous: " + messageText + "\n"); //afficher le message
                inputField.setText(""); //vider le champ  
           
            }
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

     // Afficher le dernier message reçu
    public void afficheMessage(){
        String lastMessageJSON = clientInvite.getLastMessage();
        if(lastMessageJSON != null && !lastMessageJSON.isEmpty()){
          
            Message lastMessageString = Message.fromJson(lastMessageJSON); //convertion en message "normal"
            chatArea.append("Message reçu de" + lastMessageString.getFrom() + lastMessageString.getContent() + "\n");
            chatArea.repaint();
            chatArea.revalidate();

        }
    }

    //Troisième jour: 18 juin 25 =>peut être mettre en commentaire
    //Méthode principale pour la ChatInterface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { //pour lancer l'UI dans le bon thread (thread graphique)

            //Instanciation du clientInvite
            ClientInvite clientInvite = new ClientInvite(""); 
            ChatInterfacePerso ui = new ChatInterfacePerso(clientInvite);
            ui.setVisible(true); 
        });
    }

    
}
