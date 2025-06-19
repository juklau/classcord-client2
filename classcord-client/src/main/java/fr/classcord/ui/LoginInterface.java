package fr.classcord.ui;

 //Frame du Login
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel; //gestion de la mise en page
import javax.swing.JPasswordField; //gestion des actions utilisateur
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONObject;

import fr.classcord.model.User;
import fr.classcord.network.ClientInvite;

public class LoginInterface extends JFrame{

    //propriétés
    private final JTextField usernameField;
    
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;
    private ClientInvite clientInvite;
    private final JLabel loaderLabel;
   

    //Constructor
    public LoginInterface(ClientInvite clientInvite){
        this.clientInvite=clientInvite;

        setTitle("Connexion au Chat");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre

        JPanel parentPanel = new JPanel(new BorderLayout());
        //BorderFactory.createEmptyBorder() !!!! => crée une bordure invisible,un marge intérieure pour le panel.
        parentPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Nom d'utilisateur :"));
        usernameField = new JTextField();
        
        //pour afficher le dernier username
        // String lastUsername = User.loadLastUsername();
        // usernameField.setText(lastUsername);
        panel.add(usernameField);

        panel.add(new JLabel("Mot de passe :"));
        passwordField = new JPasswordField(); //masquer le MPD
        panel.add(passwordField);

        loginButton = new JButton("Se connecter");
        registerButton = new JButton("S'inscrire");

        panel.add(loginButton);
        panel.add(registerButton);

        loaderLabel = new JLabel(new ImageIcon("image/spinner-loading.gif"));//????????
        //https://pixabay.com/fr/gifs/
        loaderLabel.setVisible(false);
        // loaderLabel.setPreferredSize(new Dimension(50, 50)); // Ajuste selon la taille du GIF

        parentPanel.add(loaderLabel, BorderLayout.NORTH);

        parentPanel.add(panel, BorderLayout.SOUTH);
        add(parentPanel);

        String lastUser = readLastUsername();
        if (!lastUser.isEmpty()){
            usernameField.setText(lastUser);
        }

        loginButton.addActionListener(e -> authenticateUser("login"));
        registerButton.addActionListener(e -> loginApresRegistration());
    }

    //authentification de user par LOGIN
    private void authenticateUser(String type){
        // loaderLabel.setVisible(true);

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

            
        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        loaderLabel.setVisible(true);
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);

        new Thread(() -> {

             // toute interaction avec l'interface utilisateur Swing doit être exécutée dans le thread de l'UI → 
            // et c’est exactement le rôle de SwingUtilities.invokeLater.
            try {
                String response = User.login(clientInvite, username, password);
                JSONObject resp = new JSONObject(response);
                if(resp.optString("status").equals("ok")){
                    saveLastUsername(username);// megnezni mi van benne nalam loadLastName??
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Bienvenue " + username);
                        openChatWindow();
                        dispose();
                    });
                }else{
                     SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Échec pendant l'authentification");
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                        loaderLabel.setVisible(false);
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage()); 
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    loaderLabel.setVisible(false);
                });
            }
        }).start();
    }

    // Registration puis en cas de succes => login automatiquement
    private void loginApresRegistration(){

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if(username.isEmpty() || password.isEmpty()){
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.");
            return;
        }

        loaderLabel.setVisible(true);
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);

        new Thread(() -> {
            // toute interaction avec l'interface utilisateur Swing doit être exécutée dans le thread de l'UI → 
            // et c’est exactement le rôle de SwingUtilities.invokeLater.
            try {
                String registerResponse = User.register(clientInvite, username, password);
                JSONObject registerResp = new JSONObject(registerResponse);

                if(registerResp.optString("status").equals("ok")){

                    //après registration on fait le login
                    JOptionPane.showMessageDialog(this, "Inscription réussie \n Veuillez-vous connectez!");
                    String loginResponse = User.login(clientInvite, username, password);
                    JSONObject loginResp = new JSONObject(loginResponse);
                    if(loginResp.optString("status").equals("ok")){
                        saveLastUsername(username);
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Bienvenue " + username);
                            openChatWindow();
                            dispose();
                        });
                    }else{
                         SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Échec pendant l'authentification");
                            loginButton.setEnabled(true);
                            registerButton.setEnabled(true);
                            loaderLabel.setVisible(false);
                        });
                    }
                }else{
                    //Erreur pendant l'inscription
                     SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Erreur pendant l'inscription");
                        loginButton.setEnabled(true);
                        registerButton.setEnabled(true);
                        loaderLabel.setVisible(false); // n'oublie pas le loader au passage
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage()); 
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    loaderLabel.setVisible(false);
                });
            }
        }).start();
    }


    private void openChatWindow() {
         new ChatInterfacePerso(clientInvite).setVisible(true);
    }

    //Mémoriser
     private void saveLastUsername(String username) {
        try (FileWriter writerUserName = new FileWriter("lastuser.txt")) {
            writerUserName.write(username);
        } catch (IOException ignored) {
        }
    }

    private String readLastUsername() {
        try (BufferedReader readerUserName = new BufferedReader(new FileReader("lastuser.txt"))) {
            return readerUserName.readLine();
        } catch (IOException e) {
            return "";
        }
    }


    //Deuxième et Troisième jour:17-18 juin 25 =>peut être mettre en commentaire??
    //Méthode principale pour la LoginInterface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            ClientInvite clientInvite = new ClientInvite("null");
            new LoginInterface(clientInvite).setVisible(true);
        });
    }
    
}
