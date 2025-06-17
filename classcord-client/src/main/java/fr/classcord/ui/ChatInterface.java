package fr.classcord.ui;


 //Frame du TChat
import javax.swing.*; 
import java.awt.*; //gestion de la mise en page
import java.awt.event.ActionEvent; //gestion des actions utilisateur
import java.awt.event.ActionListener; //gestion des actions utilisateur


public class ChatInterface extends JFrame {

    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;

    //Constructeur
    public ChatInterface() {

        setTitle("Tchat Simple");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre

        initUI(); //appel de la construction l'interface
    }

    private void initUI() {
        // Zone de chat (non éditable)
        chatArea = new JTextArea();
        chatArea.setEditable(false); //non modifiable
        chatArea.setLineWrap(true); //retour à la ligne est automatique

        JScrollPane scrollPane = new JScrollPane(chatArea); //scroller sur chatArea

        // Champ de texte pour écrire les messages
        inputField = new JTextField();
        inputField.addActionListener(new SendMessageListener()); //en appuyant sur Entrée =>message est envoyé

        // Bouton envoyer
        sendButton = new JButton("Envoyer");
        sendButton.addActionListener(new SendMessageListener());

        // Layout
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER); //champ de saisie au centre
        inputPanel.add(sendButton, BorderLayout.EAST); //mettre le bouton à droite

        // Ajout des composants
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane, BorderLayout.CENTER); //mettre le zone de message au centre 
        getContentPane().add(inputPanel, BorderLayout.SOUTH); //mettre le champ et le bouton en bas
    }

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


