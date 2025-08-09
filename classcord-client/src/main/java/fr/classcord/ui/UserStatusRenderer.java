package fr.classcord.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

public class UserStatusRenderer extends DefaultListCellRenderer {
    //extends DefaultListCellRenderer => permet de surcharger le rendu visuel des éléments dans une JList

    //propriétés
    private final Map<String,String> userStatuses;


    //Constructeur
    public UserStatusRenderer(Map<String,String> userStatuses){
        this.userStatuses = userStatuses;
    }

    //méthodes

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
        label.setBorder(new EmptyBorder(2, 4, 2, 4));

        return label; //sera affiché dans la JList
    }

    private Icon createStatusDot(Color color){
        int size = 14; //=>14 pixels
        BufferedImage image=  new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB); //création une image vide
        Graphics2D g2 = image.createGraphics(); //récupèration du "pinceau" (Graphics2D) pour dessiner dans l’image.

        //activation de l’antialiasing pour des bords lisses afin d'éviter le cercle pixélisé
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color); //choix la couleur du cercle
        g2.fillOval(0, 0, size, size); //dessine un ovale rempli
        g2.dispose(); //libèration les ressources graphiques utilisées (bonne pratique avec Graphics2D).
        return new ImageIcon(image);
    }
}
