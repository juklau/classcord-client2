package fr.classcord.ui;

 //Frame du connexion au serveur
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel; //gestion de la mise en page
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.classcord.network.ClientInvite;



public class ConnectToServeurUI extends JFrame {

    //propriétés
    private final JTextField adresseIPServeur;
    private final JTextField adressePortServeur;
    private final JButton btnConnexionServeur;
    private ClientInvite clientInvite;
    

    //Constructor
    public ConnectToServeurUI(){

        setTitle("Connexion au Serveur");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //fermeture automatique
        setLocationRelativeTo(null); // Centrer la fenêtre

        JPanel parentPanel = new JPanel(new BorderLayout());
        //BorderFactory.createEmptyBorder() !!!! => crée une bordure invisible,un marge intérieure pour le panel.
        parentPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Adresse IP: "));
        adresseIPServeur = new JTextField("10.0.108.52");
        panel.add(adresseIPServeur);

        panel.add(new JLabel("Adresse Port: "));
        adressePortServeur = new JTextField("12345");
        panel.add(adressePortServeur);
       
        btnConnexionServeur = new JButton("Connexion au Serveur");
        panel.add(new JLabel()); //pour espacement
        panel.add(btnConnexionServeur);
        
        parentPanel.add(panel, BorderLayout.CENTER);
        add(parentPanel);

        btnConnexionServeur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 System.out.println("Bouton 'Connexion au Serveur' cliqué !");
                connectToServer();
            }
        });
    }

    //méthodes

    private void connectToServer(){
        String ip = adresseIPServeur.getText().trim();
        int port;

        try {
            //transtypage de String en int
            port = Integer.parseInt(adressePortServeur.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Le port doit être un nombre valide !");
            return;
        }

        if (!ip.isEmpty()) {
            clientInvite = new ClientInvite("invité"); // Utilisation d’un pseudo temporaire
            boolean connected = clientInvite.connect(ip, port);

            if (connected) {
                JOptionPane.showMessageDialog(this, "Connexion réussie au serveur " + ip + " : " + port);
                dispose(); // Fermer ConnexionToServeur

                if (clientInvite != null) {
                        SwingUtilities.invokeLater(() -> new ChoixModeUI(clientInvite).setVisible(true));
                    } else {
                        System.err.println("Erreur : clientInvite est null !");
                    }
            } else {
                JOptionPane.showMessageDialog(this, "Erreur de connexion au serveur.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez entrer une adresse IP valide !");
        }
    }

    //Deuxième et Troisième jour:17-18 juin 25 
    //Méthode principale pour la ConnectToServuerUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            new ConnectToServeurUI().setVisible(true);
        });
    }
}



