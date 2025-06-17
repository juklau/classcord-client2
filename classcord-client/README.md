# ClassCord Client - Jour 1

## Informations Personnelles
- **Klaudia et Juhasz** 

## Fonctionnalités Dévelopées
- Création du projet Maven dans VSCode.
- Configuration du fichier `pom.xml` avec la dépendance JSON.
- Mise en place de la structure de packages selon le modèle MVC.
- Implémentation des classes métier `User` et `Message`.

## Instructions pour Lancer le Projet

### Prérequis
- Java 11 ou plus
- J'ai installé le Maven sur mon ordinateur puis j'ai crée une variable d'environnement en  pointant vers le dossier Maven
- J'ai installé dans VSCode les extensions liés à Java et à Maven

### Étapes pour Configurer le Projet

1. **Forker le dépôt** :
   - Forkez le dépôt original sur votre compte GitHub.

2. **Cloner le dépôt** :
   ```bash
   git clone https://github.com/votre-identifiant/classcord-client.git
   cd classcord-client

3. **Configurer le projet Maven** :
    J'ai ajouté la dépendance JSON dans le fichier pom.xml:
      <dependency>
          <groupId>org.json</groupId>
          <artifactId>json</artifactId>
          <version>20231013</version>
      </dependency>

    Puis j'ai rechargé le projet Maven par MAJ+ALT+U

4. **Organisation des packages dans VSCode** :

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
        Puis j'ai créé les constructors, les getters et les setters dans les classe Message, et User


5. **Compilation du projet** :

    j'ai compilé le projet en utilisant le "mvn compile"

6. **Test du projet** :

    Dans la classe Main j'ai testé le projet avec "System.out.println("Hello ClassCord")"

A la fin de la première journée le projet est livrable:
    *Projet Maven fonctionnel dans VSCode.
    *Fichier pom.xml configuré avec la dépendance JSON.
    *Packages Java créés et classes User et Message valides.
    *Compilation sans erreur.


# 📖 Jour 2 - Connexion au serveur et tchat en mode invité

## 🎯 Objectifs de la journée
- Permettre à l'utilisateur de **se connecter à un serveur** via une adresse IP et un port.
- Envoyer **des messages en tant qu'invité** (sans authentification).
- Recevoir **les messages en temps réel** depuis le serveur.
- Afficher les messages entrants dans **la console ou une interface Swing**.

---

## 🏗️ Architecture du projet
📂 **Packages** :
- `fr.classcord.network` → Contient la classe `ClientInvite`.
- `fr.classcord.model` → Gestion des messages JSON.
- `fr.classcord.ui` → Interface Swing pour le chat.

---

###  Étapes de développement:

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

**Création d’un thread secondaire pour écouter les messages reçus**
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
```
pseudo = new JTextField(10);
adresseIP = new JTextField(10);
adressePort = new JTextField(10);
```

**Connexion via un bouton**
```
btnConnexion.addActionListener(e -> btnConnexion_clic());
```

**Envoi des messages**
```
sendButton.addActionListener(e -> sendMessage());
```

A la fin de la première journée le projet est livrable:
    *Classe ClientInvite fonctionnelle capable de communiquer avec un serveur. 
    *Envoi et réception de messages en mode invité. 
    *Interface Swing ou console affichant le chat.
    *Encapsulation de la logique JSON dans une classe Message.
    *Interface graphique avancée avec design amélioré et gestion des utilisateurs connectés.