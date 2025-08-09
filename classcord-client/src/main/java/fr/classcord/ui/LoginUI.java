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

import fr.classcord.controller.LoginController;
import org.json.JSONObject;

import fr.classcord.model.ClientInvite;
import fr.classcord.controller.ChatController;
import fr.classcord.controller.AuthController;

public class LoginUI extends JFrame{

    //propriétés
    private final JTextField usernameField;
    
    private final JPasswordField passwordField;
    private final JButton loginButton;
    private final JButton registerButton;

    private final JLabel loaderLabel;
    private ChatController chatController;
    private LoginController loginController;


    //méthodes

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

        loginController.login(username, password, this,
                () -> {
                    saveLastUsername(username);
                    JOptionPane.showMessageDialog(this, "Bienvenue " + username);
                    openChatWindow();
                    dispose();
                },
                () -> {
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    loaderLabel.setVisible(false);
                }
        );
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

        loginController.registerThenLogin(username, password, this,
                () -> {
                    saveLastUsername(username);
                    JOptionPane.showMessageDialog(this, "Bienvenue " + username);
                    openChatWindow();
                    dispose();
                },
                () -> {
                    loginButton.setEnabled(true);
                    registerButton.setEnabled(true);
                    loaderLabel.setVisible(false);
                }
                );
    }

    /*private void openChatWindow() { //régi
        new ChatPersoUI(clientInvite).setVisible(true);
    }*/

    private void openChatWindow() {
        new ChatPersoUI(loginController.getClientInvite()).setVisible(true);
    }

    //Mémoriser le nom du dernière utilisateur
     private void saveLastUsername(String username) {
        try (FileWriter writerUserName = new FileWriter("lastuser.txt")) {
            writerUserName.write(username);
        } catch (IOException ignored) {
        }
    }

    //lire le nom du dernière utilisateur
    private String readLastUsername() {
        try (BufferedReader readerUserName = new BufferedReader(new FileReader("lastuser.txt"))) {
            return readerUserName.readLine();
        } catch (IOException e) {
            return "";
        }
    }

    //Constructeur
    public LoginUI(ClientInvite clientInvite){
        this.loginController = new LoginController(clientInvite);

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


    //Deuxième et Troisième jour:17-18 juin 25 =>peut être mettre en commentaire??
    //Méthode principale pour la LoginUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            ClientInvite clientInvite = new ClientInvite("null");
            new LoginUI(clientInvite).setVisible(true);
        });
    }
}
