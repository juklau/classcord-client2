package fr.classcord.model;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class UserColorManager {

    //propriétés
    private final Map<String, Color> userColors = new HashMap<>();

    //méthodes

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
}
