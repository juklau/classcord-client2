

## Informations Personnelles
- **Klaudia Juhasz**

# ClassCord Client

### Lancement du Chat

1. Lancer l’application via la classe `ConnectToServeur`.
2. Se connecter au serveur (IP/port).
3. S’authentifier (inscription + connexion automatique, ou connexion directe, ou mode invité).
4. Accéder à la fenêtre de chat.


# 📖 Jour 1 - Mise en place du projet et modélisation

Pendant cette journée, on configure les IDEs afin de pouvoir travailler en bon condition, on organise le package et on commence découvrir l'utilisation de VSCode en Java

## Fonctionnalités Dévelopées
- Création du projet Maven dans VSCode, puis dans IntelliJ IDEA.
- Configuration du fichier `pom.xml` avec la dépendance JSON.
- Mise en place de la structure de packages selon le modèle MVC.
- Implémentation des classes métier `User` et `Message`.

### Fonctionnalités ajoutées

- Création de la classe `User` avec les attributs `username` et `status`
- Création de la classe `Message` avec les attributs `type`, `subtype`, `from`, `to`, `content`, `timestamp`
- Ajout des constructeurs pour chaque classe
- Implémentation de la méthode **toString()** pour faciliter l’affichage des objets
- Réalisation de premiers tests simples pour vérifier le bon fonctionnement des méthodes **toString()**


## Instructions pour Lancer le Projet

### Pour les prérequis
- Java 11 ou plus
- J'ai installé le Maven sur mon ordinateur puis j'ai crée une variable d'environnement en  pointant vers le dossier Maven
- J'ai installé dans VSCode les extensions liés à Java et à Maven
- J'ai installé l'IntelliJ IDEA en Août 2025

### Étapes pour Configurer le Projet

**Forker le dépôt** :

- J'ai forké le dépôt original sur mon compte GitHub.

**Clone du dépôt** :

   ```bash
        git clone https://github.com/votre-identifiant/classcord-client.git
        cd classcord-client
   ```

**Configuration du projet Maven** :

    J'ai ajouté la dépendance JSON dans le fichier pom.xml:
      <dependency>
          <groupId>org.json</groupId>
          <artifactId>json</artifactId>
          <version>20231013</version>
      </dependency>

    Puis j'ai rechargé le projet Maven par MAJ+ALT+U

**Organisation des packages dans VSCode** :

    J'ai créé la structure suivante:
    ```
      classcord-client/
        ├── src/
        │   ├── main/
        │   │   ├── java/
        │   │   │   ├── fr/
        │   │   │   │   ├── classcord/
        │   │   │   │   │   ├── model/
        │   │   │   │   │   │   ├── User.java
        │   │   │   │   │   │   ├── Message.java
        │   │   │   │   │   ├── network/
        │   │   │   │   │   ├── ui/
        │   │   │   │   │   ├── app/
        │   │   │   │   │   │   ├── Main.java
        ├── pom.xml
    ```
        Puis j'ai créé les constructors, les getters et les setters dans les classes Message, et User


**Compilation du projet** :

    j'ai compilé le projet en utilisant le "mvn compile" pour voir si ça fonctionne... et ça fonctionnait

**BONUS**
**Test du projet** :

    Dans la classe Main j'ai testé le projet avec "System.out.println("Hello ClassCord")"


**Résumé de la première journée du projet**

- Projet Maven fonctionnel dans VSCode.
- Fichier pom.xml configuré avec la dépendance JSON.
- Packages Java créés et classes User et Message valides.
- Compilation sans erreur.


# 📖 Jour 2 - Connexion au serveur et tchat en mode invité

Le but de cet étape pendant le dévéloppement du Tchat:
En entrant le pseudo, adresse IP et port du serveur, l'utilisateur peut connecter au Serveur grâce à une socket TCP comme "invité", et avec le bon gestion des messages, il peut communiquer avec des autres personnes connectés comme "invité".

## Fonctionnalités Dévelopées
- Permission afin que l'utilisateur puisse de **se connecter à un serveur** via une adresse IP et un port.
- Envoie **des messages en tant qu'invité** (sans authentification).
- Reçoit **des messages en temps réel** depuis le serveur.
- Affichage asynchrone des messages entrants dans **la console ou une interface Swing**.

---

##  Architecture du projet
**Packages** :
- `fr.classcord.controller` → Contient la classe `ClientInvite`.
- `fr.classcord.model` → Gestion des messages JSON.
- `fr.classcord.ui` → Interface Swing pour le chat qui contient la classe `ChatInterface`

---

###  Étapes de développement:
Mes méthodes contiennent des messages qui sont affiché dans la console, afin de savoir ce qui se passe pendant l'exécution de la méthode.


1. **Création de la classe ClientInvite**

**Connexion au serveur via une socket TCP**

    ```
    java
        socket = new Socket(ip, port);
    ```

**Ouverture des flux pour envoyer et recevoir des messages**

    ```
    writer = new PrintWriter(socket.getOutputStream(), true);
    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    ```

**Gestion des messages JSON avec org.json.JSONObject**

    ```
    JSONObject message = new JSONObject();
    message.put("type", "message");
    message.put("subtype", "global");
    message.put("to", "global");
    message.put("from", pseudo);
    message.put("content", messageText);
    writer.println(message.toString());
    ```

2. **Réception et affichage des messages**

**Création d’un thread secondaire écouter les messages reçus**

Pour gérer la réception des messages, j'ai créé un Thread dans la méthode **listenForMessages()** de la classe `ClientInvite`

    ```
    new Thread(() -> { 
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("Message reçu : " + line);
            }
        } catch (IOException e) {
            System.err.println("Erreur de réception");
        }
    }).start();
    ```

3. **Intégration avec l'interface Swing ChatInterface**

**Entrée utilisateur (pseudo, IP, port)**

Création des propriétés:

    ```
    pseudo = new JTextField(10);
    adresseIP = new JTextField(10);
    adressePort = new JTextField(10);
    ```

**Connexion au Serveur via la méthodes connect(ip, port)**

Cette méthode est appelée par le bouton de "Connexion"

    ```
    btnConnexion.addActionListener(e -> btnConnexion_clic());
    ```

    ```
     public boolean connect(String ip, int port){
        
        try {
            //création une connexion TCP entre client et serveur
            socket = new Socket(ip, port); 

            //envoyer des messages, des données au serveur
            writer = new PrintWriter(socket.getOutputStream(), true);

            //lire les messages envoyés par le serveur (ligne par ligne)
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Connexté au serveur " + ip + " : " + port);
            return true;
        } catch (IOException e) {
            System.out.println("Promblème pendant la connexion au servuer: " + e.getMessage());
            try {
                //fermeture de la connexion en cas d'échec
                if(socket != null){
                    socket.close(); 
                    //une connexion TCP ouvert consomme la mémoire et bloque les ports de réseau!!
                }
            } catch (IOException closException) {
                System.out.println("Erreur pendant la fermeture de la connexion " + closException);
            }
            return false;
        }
    }
    ```

**Envoi des messages via la méthode send(String message)**

Cette méthode de la classe ClientInvite peut être appelée par le bouton d'"Envoyer" ou sur appuyant sur "Entrée"

    ```
    sendButton.addActionListener(e -> sendMessage());
    ```


    ```
    //envoie d'un message au format JSON au serveur 
    public void sendMessage(String messageText){

        if (pseudo == null || pseudo.isEmpty()) {
            System.err.println("Erreur : pseudo non défini.");
            return;
        }
        //convertir et envoyer sous forme de JSON
        if(writer != null && socket != null && !socket.isClosed()){
            Message message = new Message("message", "global", pseudo, "global", messageText, "");
            writer.println(message.toJson().toString());
        }else{
            System.err.println("Impossible d'envoyer le message, la connexion est fermée.\n");
        }
    }

    ```
**Reçoit des messages via la méthode sendMessage() de la Classe ChatInterface**

    ```
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
    ```
**BONUS**

**Encapsulation de la logique JSON dans la classe Message**

    ```
        //convertir l'objet message en JSONObject
        public JSONObject toJson(){
            JSONObject json = new JSONObject();
            json.put("type" , type);
            json.put("subtype" , subtype);
            json.put("from", from);
            json.put("to", to);
            json.put("content", content);
            json.put("timestamp", timestamp);
            return json;
        }
    ```

    ```
        //Convertir une chaîne JSON en objet Message
        public static Message fromJson(String jsonString){
            JSONObject json = new JSONObject(jsonString);
            return new Message(
                json.getString("type"),
                json.optString("subtype", "global"), //gestion de l'absence de subtype
                json.getString("from"),
                json.optString("to", "global"), //gestion de l'absence de to
                json.getString("content"),
                json.getString("timestamp")
            );
        }
    ```

**Interface graphique Swing de base**

## L'image de cette interface se trouve dans le dossier image sous le nom `ChatInterface`

Résumé de la deuxième journée:
- Classe ClientInvite fonctionnelle capable de communiquer avec un serveur.
- Envoi et réception de messages en mode invité.
- Interface Swing ou console affichant le chat.
- Encapsulation de la logique JSON dans une classe Message.
- Interface graphique avancée avec design amélioré et gestion des utilisateurs connectés.


# 📖 Jour 3 – Authentification et Gestion des Comptes Utilisateurs

## Fonctionnalités Dévelopées
- Implémentation de l’**inscription** et de la **connexion** via identifiants utilisateur.
- Communication avec le serveur via des **messages JSON**.
- Affichage d'un message de **confirmation ou d’erreur** selon la réponse du serveur.
- Réutiliser le **canal d’échange de messages** après authentification.


##  Architecture du projet
- `fr.classcord.controller` → Contient la classe `ClientInvite`.
- `fr.classcord.model` → Ajoute de la classe `CurrentUser`
- `fr.classcord.ui` → Ajoute des classes: `ConnectToServeur`, `SelectionneInterface`, `LoginUI`, `ChatInterfacePerso`, `GuestUI`

###  Étapes de développement:

## Fonctionnalités Implémentées

**Connexion au serveur**

L'utilisateur peut connecter au Serveur en saissant l'adresse IP et le port du serveur.
Une fois connecté, il accéde à l'interface d'authentification.

Pour cette connexion j'ai créé la méthode suivant dans la classe ConnectToServeur:
```
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
dispose(); // Fermer ConnectToServeur

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
    ```

## L'image de l'interface de connexion au Serveur se trouve dans le dossier image sous le nom `ConnectToServeur`

**Interface qui permet choisir le mode de la connexion**
Pendant cet étape user peut choisir s'il voudrait se connecter comme "Invité" ou comme "Utilisateur".
En appuyant sur un des boutons, il est amené sur l'interface correspondant de son choix

## L'image de l'interface de connexion au Serveur se trouve dans le dossier image sous le nom `ChoixModeUI`


**Interface de Connexion (Swing) en tant que l'Utilisateur'**

- Fenêtre avec :
    - Champ `username`
    - Champ `password` (masqué via `JPasswordField`)
    - Boutons **Se connecter** / **S’inscrire**

Il y a 2 possibilités:
1. Le nouveau utilisateur s'enregistre via le bouton "S'inscrire" et puis il accéde automatiquement au Tchat via
   la méthode **loginApresRegistration()** de la classe `LoginUI`.
   Cette méthode crée un Thread dans lequel elle fait appel de la méthode **register()** et **login()** de la classe `User`.
   Pendant cette méthode j'utilise **SwingUtilities.invokeLater()** qui permet exécuter du code sur le thread de l’interface graphique.


2.L'utilisateur déjà enregistré dans la base de donnée du serveur, il se connect en entrant son nom d'utilisateur et son mot de passe via la méthode **authenticateUser()** qui crée également un Thread dans lequel il fait appel de la méthode **login()** de la classe `User` et pendant cette méthode j'utilise aussi **SwingUtilities.invokeLater()**.

## L'image de l'interface de connexion au Serveur se trouve dans le dossier image sous le nom `LoginUI`


**Interface de Connexion (Swing) en tant que l'Invité'**
Il saisit son nom de pseudo, et en appuyant sur le bouton "Connexion au Chat", il peut accéder au Tchat sans compte, en "Mode invité"
J'ai créé la méthode **btnConnexionChatClic()**, qui permet réaliser cette tâche.

## L'image de l'interface de connexion au Serveur se trouve dans le dossier image sous le nom `GuestUI`



**Communication avec le Serveur (Socket + JSON)**

## L'image de l'interface de Chat se trouve dans le dossier image sous le nom `ChatIterfacePerso`

Lors de l’inscription, les informations sont envoyées au serveur sous forme de message JSON :

    ```json
    {
      "type": "register",
      "username": "alice",
      "password": "azerty"
    }
    ```

Après une inscription réussie, le client effectue automatiquement la connexion :
```json
{
  "type": "login",
  "username": "alice",
  "password": "azerty"
}
```

**Réception de la Réponse du Serveur**

En cas de connexion réussite :
- J'affichage un message de bienvenue.
```
JOptionPane.showMessageDialog(this, "Bienvenue " + pseudo + " !");
```
- L'utilisateur passe à la fenêtre principale de **chat**.

En cas d'échec de connexion
-J'affichage un message d’erreur retourné par le serveur.
```
JOptionPane.showMessageDialog(this, "Erreur : " + e.getMessage());
```

**BONUS**
**Gestion de l’Utilisateur**

Pour conserver et mémoriser le dernier pseudo j'utilise la méthode **saveLastUsername(String username)**.
Affiche en **Auto-complétion** du champ `username` avec le dernier pseudo sur `LoginUI`:

    ```
    String lastUser = readLastUsername();
            if (!lastUser.isEmpty()){
                usernameField.setText(lastUser);
            }
    ```

**Masquer le mot de passe dans le champ JPasswordField**

J'ai instancié "JpasswordField":

    ```
     passwordField = new JPasswordField(); 
    ```


**Ajoute d'une icône de chargement pendant la tentative de connexion**
Je l'ai téléchargé sur le site https://pixabay.com/fr/gifs/
Pendant l'attente de connexion je l'ai mis en "visible":

    ```
    loaderLabel.setVisible(true);
    ```

En cas contraire, je l'ai mis en mode "invisible"

    ```
    loaderLabel.setVisible(false);
    ```

Actuellement je n'ai pas pu vérifier son bon fonctionnement...
## Cet icon se trouve dans le dossier image sous le nom `spinner-loading`


Résumé de la troisième journée:
- Fenêtre Swing d’**inscription/connexion** fonctionnelle.
- Envoi des identifiants via **socket** avec format JSON valide.
- Affichage des **messages de succès ou d’erreur**.
- Accès automatique à la **fenêtre de tchat** après connexion réussie.
- Navigation fluide entre fenêtres (sans redémarrage complet).
- Respect du **protocole JSON** établi avec le serveur.
- Interface graphique Swing claire et intuitive.
- Séparation propre des responsabilités (UI / logique / communication réseau) ......


# 📖 Jour 4 – Messages privés et liste des utilisateurs connectés

Pendant cette journée afin de comprendre comment les utilisateurs sont identifiés et suivis par le serveur il a fallu développer différentes fonctionnalités du Tchat


## Fonctionnalités Dévelopées
- Récupération de la liste des utilisateurs connectés à tout moment (via les messages de statut).
- Affichage dynamique de cette liste dans l'interface utilisateur (Swing).
- Possibilité d'envoyer un message privé à un utilisateur précis.
- Différenciation visuelle des messages globaux des messages privés.


##  Architecture du projet
- `fr.classcord.controller` → Contient la classe `ClientInvite`.
- `fr.classcord.model` → Contient la classe `CurrentUser`, `Message`, `User`
- `fr.classcord.ui` → Contient les classes: `ConnectToServeur`, `SelectionneInterface`, `LoginUI`, `ChatInterfacePerso`, `GuestUI`, `ChatInterface`

###  Étapes de développement:

## Fonctionnalités Implémentées

**Afficher la liste des utilisateurs connectés**

Dans la classe `ChatInterface` et `ChatInterfacePerso` j'ai instancié ces classes afin de pouvoir sauvegarder les personnes connectés
```
    private final DefaultListModel<String> userListModel = new DefaultListModel<>();
    private final JList<String> userList = new JList<>(userListModel);
```

Dans la méthode **listenForMessages()** de la classe `ClientInvite` j'intercepte les messages de type "status":

    ```
    case "status" -> {
            String username = json.optString("user");
            String statut = json.optString("state");

            userStatusMap.put(username, statut);

            if (chatInterfacePerso !=  null){
                chatInterfacePerso.updateUserList(new HashMap<>(userStatusMap));
            }else if (chatInterface != null){
                chatInterface.updateUserList(new HashMap<>(userStatusMap));
            }
        }
    ```
En format JSON

    ```
    { "type": "status", "user": "bob", "state": "online" }
    ```
Je maintiens une HashMap<String, String> (pseudo → statut) dans la méthode **listenForMessages()** afin de savoir qui est connecté:

    ```
    case "users" -> {
        System.out.println("Liste complète reçu");
        JSONArray usersArray = json.optJSONArray("users");

        if(usersArray != null){
            // Vider temporairement la map des utilisateurs (optionnel, selon ton besoin)
            userStatusMap.clear();

            for(int i = 0; i < usersArray.length(); i++){ //parcourt toutes les clés (pseudos) dans l'objet users
                String pseudo = usersArray.optString(i);

                // Ajouter chaque utilisateur avec statut "online" (puisque ce sont ceux connectés)
                userStatusMap.put(pseudo, "online");
            }

            if (chatInterfacePerso !=  null){
                chatInterfacePerso.updateUserList(new HashMap<>(userStatusMap)); //création des copies pour éviter les conflits
            }else if (chatInterface != null){
                chatInterface.updateUserList(new HashMap<>(userStatusMap));
            }
        }else{
            System.err.println("Réponse 'users' invalide: pas de champ de 'users'");
        }
    }
    ```
Afin de mettre à jour dynamiquement la liste affichée j'utilise la méthode **updateUserList(Map<String, String> userMap)** de la classe `ChatInterfacePerso`

    ```
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
    ```
**Possibilité de l'envoi de messages privés**

Quand l'utilisateur est sélectionné, on peut envoyer en message privé invisible par les autres utilisateurs.
J'ai modifié la méthode **sendMessage()** de la classe `ChatInterfacePerso` en différenciant le "subtype":
J'ai ajouté un attribut to : le destinataire si MP, ou "global" sinon.
```
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
            json.put("to", "global");
            
            chatArea.append("Vous: " + messageText + "\n"); //afficher le message
        }
        clientInvite.send(json.toString());
        inputField.setText("");
    ```

J'ai adapté le message JSON à envoyer:
```
{
"type": "message",
"subtype": "private",
"to": "pseudo_destinataire",
"content": "Message confidentiel"
}
```

**Afficher les messages entrants selon leur type**

Il a fallu différencier l'affichage des messages selon leur types!!
J'ai modifié la méthode **afficheMessage()** dans la classe `ChatInterfacePerso`
Dans l'affichage, j'ai utilisé "subtype" pour distinguer les types de messages.

    ```
        try {
            JSONObject json = new JSONObject(lastMessageJSON);

            String type = json.optString("type");
            if(!"message".equals(type)){
                return;
            }
            
            String subtype = json.optString("subtype");
            String from = json.optString("from");
            String content = json.optString("content");

            if("private".equals(subtype)){

                //affichage de message "privé" avec préfixe
                chatArea.append("**[MP de " + from + "]** " + content + "\n");
            }else{
                //affichage de message "global"
                chatArea.append(from + " : " + content + "\n");
            }
            // placer le curseur de texte (caret) à la fin du contenu du chatArea afin de voir le dernier message
            chatArea.setCaretPosition(chatArea.getDocument().getLength());

        } catch (Exception e) {
            System.out.println("Erreur dans afficheMessage() " + e.getMessage());
        }
    ```

**Mettre à jour le modèle objet**

Dans la classe `Message`, j'ai ajouté un attribut subtype:

    ```
        public Message(String type, String subtype, String from, String to, String content, String timestamp){
            this.type = type;
            this.subtype = subtype;
            this.from = from;
            this.to = to;
            this.content = content;
            this.timestamp = timestamp;
        }
    ```
**Différencier l'envoie du message "privé" et "global"**

Pour donner la possibilité d'envoyer un MP ou message global j'ai ajouté un bouton "Global" qui permet accomplir cette tâche

    ```
        globalButton.addActionListener(e -> userList.clearSelection());

        userList.addListSelectionListener(e -> {
            String selected = userList.getSelectedValue();
            if (selected != null) {
                globalButton.setText("↩ Global");
            } else {
                globalButton.setText("Global");
            }
        });
    ```

**BONUS**

**Ajout d'une couleur personnalisée pour chaque utilisateur connecté**

J'ai crée dans la classe `ChatInterfacePerso` 2 méthodes:

la méthode **getColorForUser(String user)**
```
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
    ```


la méthode **appendFormattedMessage(String from, String content, boolean isPrivate)**
    ```
    //afin d'afficher l'écriture des utilisatuers en couleur
    public void appendFormattedMessage(String from, String content, boolean isPrivate){
    try {
    //Message coloré pour l'utilisateur
    Style fullStyle = chatArea.addStyle("full_" + from, null);
    StyleConstants.setForeground(fullStyle, getColorForUser(from));
    StyleConstants.setBold(fullStyle, true);
    
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
    ```

Résumé de la quatrième journée:
- Inclusion dans l'interface Swing une liste déroulante des utilisateurs connectés.
- Possibilité de choisir entre un message global et un MP.
- Traitement des messages correct selon le subtype.
- Les messages privés invisibles par les autres utilisateurs.
- Une interface Swing avec affichage de la liste des utilisateurs connectés
- Fonctionnalité d'envoi de messages privés fonctionnelle
- Affichage clair des messages selon leur nature (global ou privé).



# 📖 Jour 5 – Gestion des statuts et finalisation du projet

La dernière journée, il a fallu gérer les statuts des utilisateurs, améliorer l'affichage graphique de l'application (interface Swing), vérifier toutes les fonctionnalités du projet et corriger les bugs et préparer les livrables techniques pour finaliser la réalisation professionnelle.


## Fonctionnalités Dévelopées
- Possibilité de choisir le statut des utilisateurs (disponible, absent, invisible).
- Envoie de ce statut au serveur et affichage de ce statut dans la liste des connectés.

##  Architecture du projet
- `fr.classcord.controller` → Contient la classe `ClientInvite`.
- `fr.classcord.model` → Contient la classe `CurrentUser`, `Message`, `User`
- `fr.classcord.ui` → Contient les classes: `ConnectToServeur`, `SelectionneInterface`, `LoginUI`, `ChatInterfacePerso`, `GuestUI`, `ChatInterface`

###  Étapes de développement

## Fonctionnalités Implémentées

J'ai travaillé dans la classe `ChatInterfacePerso` pendant cette journée

**Ajout de la gestion des statuts utilisateur**

J'ai ajouteé un menu déroulant (JComboBox) pour choisir le statut par utilisateur:

j'ai déclaré ces proptiétés et ajoutés dans le constucteur

    ```
        //pour gérer le status des utilisateurs
        private final Map<String, String> userStatuses = new HashMap<>();
        private final JComboBox<String> statusComboBox = new JComboBox<>(new String[] {"En ligne", "Absent", "Invisible", "Indisponible"});
    ```
    ```
        // Panel en haut pour le statut
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Statut : "), BorderLayout.WEST);
        topPanel.add(statusComboBox, BorderLayout.CENTER);

        // Ajouter un listener pour envoyer le statut lorsqu'il est changé
        statusComboBox.addActionListener(e -> envoyerStatut());

        // Ajoute ce panel en haut de la fenêtre
        contentPane.add(topPanel, BorderLayout.NORTH);
    ```

**J'ai envoyé un message JSON au serveur afin de savoir le status changé**

Dans la méthode **envoyerStatut()** j'envoie ma statut choisi au serveur afin qu'il soit au courant du changement de mon statut

    ````
        JSONObject json = new JSONObject();
        json.put("type", "status");
        json.put("user", clientInvite.getPseudo()); //pour l'identification côté serveur
        json.put("state", state);
    ````

**Affichage des utilisateurs selon leur statut**

J'ai fait un mise à jour dans l'affichage des autres utilisateurs dans la liste connectée en utilisant du text et icône (ex : point vert, gris, orange) coloré.

Pour cela j'ai créé une classe interne `UserStatusRenderer`:

la méthode **getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus)** permet de coloré l'affichage des utilisatuers selon leur statut

la méthode **createStatusDot(Color color)** crée et affiche le "point" à côté des noms d'utilisateurs selon leur statut


**Finalisation graphique de l'interface Swing**

- Vérification de tous les composants afin qu'ils soient alignés et lisibles.
- Ajout des bordures, marges, icônes ou couleurs pour améliorer l'expérience utilisateur.
- Gestion de la redimension du composant et la réactivité de l'application.

## L'image de l'interface de Chat se trouve dans le dossier image sous le nom `Interface Swing final`

**Tests croisés et débogage**

**Problème trouvé: il remets mon statut en "online" après chaque évenement dans le chat**

Pour éviter ça il a fallu que je corrige le méthode **updateUserList(Map<String, String> userMap)**

J'ai ajouté une condition: si mon statut existe déjà dans "userStatuses", il ne écrasera pas mon "statut"
Voici la modification:

    ```
        if(localUser != null && !userMap.containsKey(localUser)){
            
            //pour éviter qu'il me remets à chaque evenement en couleur vert en écrasant mon statut existant
            if(!userStatuses.containsKey(localUser)){

                String currentStatus = (String) statusComboBox.getSelectedItem();
                String normalizedStatus = switch (currentStatus) {
                case "Absent" -> "away";
                case "Indisponible" -> "dnd";
                case "Invisible" -> "invisible";
                case "En ligne", "Disponible" -> "online";
                default -> "online";
            };
            userStatuses.put(localUser, normalizedStatus);
            
            System.out.println("Statut local déterminé depuis statusComboBox : " + normalizedStatus);
            }else{
                    System.out.println("Statut local conservé : " + userStatuses.get(localUser));
            }

            if (!userListModel.contains(localUser)) {
                userListModel.addElement(localUser);
            }
            
            System.out.println("Ajout manuel de l'utilisateur local : " + localUser);
        }
    ```



Résumé de la quatrième journée:
- Application complète, testée, stable et fonctionnelle
- Interface Swing finale intégrant la gestion des statuts
- Dossier de documentation (PDF ou README + captures)
- Projet Maven archivable (zip ou Git)


**source de code et documentation**
- aide de IA
- https://stackoverflow.com/questions/34719923/how-do-i-load-an-animated-gif-within-my-jframe-while-a-long-process-is-running
- https://stackoverflow.com/questions/34262447/java-applet-setforeground-what-exactly-it-does-and-how-to-see-its-effect
- Projet du Jeu en Java (Cours SLAM)


# 📖 Jour EXTRA – Restructuration du projet

**Réorganisation des packages dans IntelliJ IDEA afin de respecter l’architecture MVC, en repartant sur une nouvelle base de développement pour ce projet.** :



    Voici la nouvelle structure:
    ```
      classcord-client/
        ├── src/
        │   ├── main/
        │   │   ├── java/
        │   │   │   ├── fr/
        │   │   │   │   ├── classcord/
        │   │   │   │   │   ├── controller/
        │   │   │   │   │   │   ├── AuthController
        │   │   │   │   │   │   ├── ChatController
        │   │   │   │   │   │   ├── LoginController
        │   │   │   │   │   │   ├── SessionController
        │   │   │   │   │   ├── model/
        │   │   │   │   │   │   ├── ClientInvite
        │   │   │   │   │   │   ├── CurrentUser (inactif)
        │   │   │   │   │   │   ├── User.java (inactif)
        │   │   │   │   │   │   ├── Message.java (inactif)
        │   │   │   │   │   │   ├── UserColorManager
        │   │   │   │   │   ├── ui/
        │   │   │   │   │   │   ├── ChatPersoUI
        │   │   │   │   │   │   ├── ChatUI
        │   │   │   │   │   │   ├── ChoixModeUI
        │   │   │   │   │   │   ├── ConnectToServeurUI
        │   │   │   │   │   │   ├── GuestUI
        │   │   │   │   │   │   ├── LoginUI
        │   │   │   │   │   │   ├── UserStatusRenderer
        │   │   │   │   │   ├── app/
        │   │   │   │   │   │   ├── App (inactif)
        ├── pom.xml
    ```
    Puis j'ai créé les constructors, les getters et les setters dans les classes Message, et User


Vérification de l’application en effectuant différents tests sur l’ensemble de ses fonctionnalités.
(reste à vérifier son fonctionnalité multiclient en Septembre)