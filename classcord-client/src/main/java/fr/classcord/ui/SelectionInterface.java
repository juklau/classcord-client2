package fr.classcord.ui;

 //Frame du SelectionInterface
import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import fr.classcord.network.ClientInvite;


public class SelectionInterface extends JFrame {

    //propriétés
    private final ClientInvite clientInvite;
    private final JButton btnInvite;
    private final JButton btnLogin;

    //constructeur
    public SelectionInterface(ClientInvite clientInvite){
        this.clientInvite = clientInvite;

        setTitle("Choisissez une option");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel parentPanel = new JPanel(new BorderLayout());
        //BorderFactory.createEmptyBorder() !!!! => crée une bordure invisible,un marge intérieure pour le panel.
        parentPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        btnInvite = new JButton("Invité");
        btnLogin = new JButton("Connexion");

        panel.add(Box.createHorizontalStrut(50)); //Espacement 
        panel.add(btnInvite);
        panel.add(Box.createHorizontalStrut(100)); //Espacement 
        panel.add(btnLogin);

        parentPanel.add(panel, BorderLayout.CENTER);
        add(parentPanel);

        btnInvite.addActionListener(e -> ouvrirFenetrePseudoInvite());
        btnLogin.addActionListener(e -> ouvrirLoginInterface());
    }

    private void ouvrirFenetrePseudoInvite() {
        dispose();
        SwingUtilities.invokeLater(() -> new ChatInterface(clientInvite).setVisible(true));
        //lehet hogy nem kell : SwingUtilities.invokeLater(() ->   ) ?????
    }

    private void ouvrirLoginInterface() {
        SwingUtilities.invokeLater(() -> new LoginInterface(clientInvite).setVisible(true));
         //lehet hogy nem kell : SwingUtilities.invokeLater(() ->   ) ?????
        dispose();
    }

    //Troisième jour: 18 juin 25 
    //Méthode principale pour la SelectionInterface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            ClientInvite clientInvite = new ClientInvite("invite");
            new SelectionInterface(clientInvite).setVisible(true);
        });
    }
}
