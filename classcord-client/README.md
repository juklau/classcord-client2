# **Projet Semaine Intensive SLAM - BTS SIO 2024**

### **Nom du projet : ClassCord - Client de messagerie interopérable**

### **Public concerné : Étudiants option SLAM (Solutions Logicielles et Applications Métiers)**

---

## ✨ Contexte du projet

Vous intégrez une équipe de développeurs chargée de créer une application cliente pour une plateforme de messagerie instantanée utilisée en réseau local dans un établissement. Baptisée **ClassCord**, cette solution a pour objectif de permettre aux utilisateurs (professeurs, étudiants) de communiquer en temps réel, de manière fluide et sécurisée.

Le serveur de tchat, déjà en place et géré par les étudiants SISR, fonctionne sur le réseau local. Chaque utilisateur se connecte à l'adresse IP de son choix pour interagir avec le serveur d'un camarade ou avec le serveur central de la salle. Votre formateur vous proposera dès le début du projet son propre serveur opérationnel afin que vous ayez toujours un serveur sur lequel vous pourrez vous connecter.

---

## 📆 Objectif pédagogique de la semaine

Réaliser un **client Java Swing complet** capable de se connecter à un serveur de tchat et d'offrir à l'utilisateur une interface fonctionnelle, réactive et évolutive.

Vous devez mobiliser vos compétences en modélisation, architecture logicielle, programmation réseau, interface graphique et documentation technique. Le tout en suivant une méthodologie de projet itérative, par étapes. Vous serez guidé sur chacune de ces étapes.

---

## 🚀 Démarrage obligatoire via GitHub

> 🎯 **Chaque étudiant doit travailler dans un dépôt GitHub personnel à partir de ce projet.**

### Étapes à suivre :

1. **Forkez** ce dépôt sur votre propre compte GitHub (bouton “Fork” en haut à droite).
2. Sur votre fork, cliquez sur **Code > HTTPS** et copiez l’URL.
3. Ouvrez VSCode ou votre terminal et tapez :
   ```bash
   git clone https://github.com/votre-identifiant/classcord-client.git
   cd classcord-client
   ```

4. Enregistrez votre travail régulièrement :
   ```bash
   git add .
   git commit -m "ex: ajout interface de login"
   git push origin main
   ```
---

## 📌 Contraintes GitHub pour la validation

* Travail **exclusivement sur votre fork GitHub**
* Projet avec **au moins 1 commit par jour clair et cohérent**
* Un fichier `README.md` personnel avec :

  * votre **nom et prénom**,
  * les **fonctionnalités développées**,
  * les **instructions pour lancer le projet**

Pour l'occasion, vous apprendrez la syntaxe markdown (md) pour rédiger la documentation.

---

## 📊 Cahier des charges fonctionnel

L'application cliente doit permettre :

1. **Connexion à un serveur distant** (IP + port à saisir)
2. **Connexion en mode invité** (pseudo temporaire)
3. **Connexion avec compte utilisateur** (login / mot de passe)
4. **Affichage des messages du canal global**
5. **Envoi de messages dans le canal global**
6. **Affichage de la liste des utilisateurs connectés**
7. **Envoi de messages privés**
8. **Affichage de l'état des utilisateurs (disponible / absent / invisible)**
9. **Changement de statut personnel**
10. **Interface conviviale et fluide avec Swing**

---

## 🪨 Cahier des charges technique

### Langage & environnement

* Java 11 ou plus
* Interface Swing (JavaFX en option en bonus)
* IDE recommandé : VSCode avec extension Java + Maven

### Dépendance

* Bibliothèque JSON : `org.json:json:20231013`

### Architecture recommandée

* **MVC** (Modèle-Vue-Contrôleur) qu'on mettra en oeuvre dans le sgrandes lignes nous mêmes, sans framework
* Thread réseau séparé (pour la réception)
* Communication par **Socket TCP** (pas HTTP)

---

## 📝 Protocole de communication

Tous les échanges entre client et serveur se font en JSON, terminés par un saut de ligne (`\n`).

### Exemple de message global envoyé au serveur :

```json
{
  "type": "message",
  "subtype": "global",
  "to": "global",
  "content": "Bonjour à tous !"
}
```

### Exemple de message privé :

```json
{
  "type": "message",
  "subtype": "private",
  "to": "bob",
  "content": "Salut Bob, dispo ?"
}
```

### Exemple de connexion :

```json
{
  "type": "login",
  "username": "alice",
  "password": "1234"
}
```

### Exemple de statut :

```json
{
  "type": "status",
  "state": "invisible"
}
```

Les réponses du serveur suivent le même format, avec `type`, `status`, `message`, `from`, `timestamp`, etc.

---

## 🔑 Fonctionnement attendus

* Les messages entrants sont affichés en temps réel (Thread écouteur)
* L'utilisateur peut envoyer des messages via une zone de saisie
* L'application doit être réactive et ne jamais figer l'IHM
* Toute déconnexion doit être gérée proprement (sans crash)

---

## 📕 **Jour 1 - Lundi : Mise en place du projet et modélisation**

### Objectifs de la journée :

* Créer un projet Java fonctionnel dans VSCode avec Maven.
* Mettre en place la dépendance `org.json`.
* Créer la structure du projet (dossiers `model`, `network`, `ui`, etc.) qui respecte dans le principe la séparation des préoccupations propre au MVC.
* Implémenter les classes métier `User`, `Message`.
* S'assurer que le projet compile et peut être exécuté (définissez une classe de test, avec quelques instructions intéressantes).

### Etapes à suivre :

1. **Création du projet Maven dans VSCode**

   * Ouvrir VSCode > Créer un dossier `classcord-client`
   * Ouvrir le terminal VSCode (raccourci : Ctrl + \`), et si Maven n'est pas installé sur la machine, commencer par :
   * Vérifier Maven avec `mvn -v`.
   * Si la commande est inconnue, suivre le tutoriel d'installation Maven (voir ressource partagée ou demander au formateur).

Une fois Maven installé, exécuter la commande suivante dans le terminal **selon votre système d'exploitation** :

* **Windows** (CMD ou PowerShell) :

  ```cmd
  mvn archetype:generate -DgroupId=fr.classcord -DartifactId=classcord-client -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
  ```
* **Linux / macOS** (Terminal Bash ou Zsh) :

  ```bash
  mvn archetype:generate -DgroupId=fr.classcord -DartifactId=classcord-client -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false
  ```
  
  - Ouvrir le projet nouvellement créé.


2. **Configurer le fichier pom.xml**

   * Ajouter la dépendance JSON dans `<dependencies>` :

     ```xml
     <dependency>
       <groupId>org.json</groupId>
       <artifactId>json</artifactId>
       <version>20231013</version>
     </dependency>
     ```

3. **Organiser les packages**

   * `fr.classcord.model`
   * `fr.classcord.network`
   * `fr.classcord.ui`
   * `fr.classcord.app`

4. **Créer les classes de base**

   Toutes les classes suivantes sont à placer dans le package `fr.classcord.model`.

   * `Message` :

     ```java
     public class Message {
         public String type;
         public String subtype;
         public String from;
         public String to;
         public String content;
         public String timestamp;

         // Constructeurs, getters/setters
     }
     ```
   * `User` :

     ```java
     public class User {
         private String username;
         private String status;

         // Constructeurs, getters/setters
     }
     ```

     Essayez de bien penser à tout et d'anticiper, dès maintenant, tous les futurs besoins.

5. **Test de compilation**

   * Lancer : `mvn compile` (vous pouvez aussi compiler sans passer par maven)
   * Vérifier qu’il n’y a pas d’erreur

6. **(Bonus) Créer un Main minimal**

   * Créer `Main.java` avec un `System.out.println("Hello ClassCord");`

### Livrables attendus en fin de journée

* Projet Maven fonctionnel dans VSCode
* `pom.xml` configuré
* Packages Java créés et classes `User` / `Message` valides
* Compilation sans erreur

---

## 📖 **Jour 2 - Mardi : Connexion au serveur et tchat en mode invité**

### Objectifs de la journée :

* Permettre à l'utilisateur de se connecter au serveur en entrant une IP et un port.
* Envoyer des messages au serveur en tant qu'invité (sans authentification).
* Recevoir des messages en temps réel depuis le serveur.
* Afficher les messages entrants dans la console ou une fenêtre Swing simple.

---

### Étapes à suivre :

1. **Créer une classe `ClientInvite` dans le package `fr.classcord.network`**

   * Utiliser une socket TCP pour se connecter au serveur.
   * Ouvrir un `PrintWriter` pour envoyer des messages.
   * Ouvrir un `BufferedReader` pour recevoir les messages.
   * Créer une méthode `connect(String ip, int port)` pour initier la connexion.
   * Créer une méthode `send(String message)` pour envoyer une ligne JSON.

2. **Gérer l'envoi d'un message JSON depuis la console**

   * Créer un objet JSON avec `org.json.JSONObject`.

   * Remplir les champs requis :

     ```java
     JSONObject message = new JSONObject();
     message.put("type", "message");
     message.put("subtype", "global");
     message.put("to", "global");
     message.put("from", pseudo);
     message.put("content", messageText);
     ```

   * Envoyer le message via le `PrintWriter`, suivi d’un `\n`.

3. **Gérer la réception des messages**

   * Créer un thread secondaire qui écoute les messages en continu avec `BufferedReader.readLine()`.
   * Afficher chaque message dés qu’il est reçu dans la console (ou future UI).

4. **Interaction avec l'utilisateur dans la console**

   * Saisir le pseudo en console
   * Boucle de lecture de message à envoyer
   * Affichage asynchrone des messages reçus

---

### Exigences techniques

* Le thread réseau de réception doit être indépendant du thread principal.
* Aucun blocage ne doit figer l'application.
* Affichage clair de chaque message reçu (pseudo + contenu).

---

### Bonus

* Encapsulation de la logique JSON dans la classe `Message` (méthodes `toJson()` et `fromJson(String)`)
* Interface graphique Swing de base (champ de texte + zone d'affichage)

---

### Livrables attendus en fin de journée

* Classe `ClientInvite` fonctionnelle capable de se connecter à un serveur
* Envoi et réception de messages JSON en mode invité
* Affichage console ou fenêtre Swing basique

---

## 📗 **Jour 3 - Mercredi : Authentification et gestion des comptes utilisateurs**

### Objectifs de la journée :

* Implémenter l'inscription et la connexion via identifiants utilisateur.
* Envoyer les données de connexion au serveur selon le protocole JSON.
* Afficher un message de confirmation ou d'erreur selon la réponse.
* Réutiliser le canal d'échange de messages une fois authentifié.

### Étapes à suivre :

1. **Ajout de l'interface de login**

   * Créer une fenêtre Swing avec deux champs : `username`, `password` + boutons "Se connecter" / "S'inscrire"
   * Afficher cette interface au démarrage

2. **Envoyer un message JSON au serveur selon l'action choisie**

   * Pour l'inscription :

     ```json
     {
       "type": "register",
       "username": "alice",
       "password": "azerty"
     }
     ```
   * Pour la connexion :

     ```json
     {
       "type": "login",
       "username": "alice",
       "password": "azerty"
     }
     ```

3. **Recevoir et interpréter la réponse du serveur**

   * Succès : afficher un message de bienvenue, puis accès à la fenêtre de tchat
   * Échec : afficher l'erreur renvoyée par le serveur

4. **Mettre à jour l'identité de l'utilisateur localement**

   * Conserver le pseudo dans un objet `CurrentUser` ou une variable centrale

### Exigences techniques

* Les messages JSON doivent être conformes au protocole
* La fenêtre de connexion doit être fonctionnelle
* Le passage entre fenêtres doit être fluide (pas de redémarrage complet)

### Bonus

* Masquer le mot de passe dans le champ `JPasswordField`
* Ajouter une icône de chargement pendant la tentative de connexion
* Mémoriser le dernier pseudo utilisé

### Livrables attendus en fin de journée

* Fenêtre Swing d'inscription/connexion
* Envoi des identifiants via socket
* Affichage des erreurs ou du succès
* Démarrage du tchat une fois connecté

## 📘 **Jour 4 - Jeudi : Messages privés et liste des utilisateurs connectés**

### Objectifs de la journée :

* Comprendre comment les utilisateurs sont identifiés et suivis par le serveur.
* Récupérer la liste des utilisateurs connectés à tout moment (via les messages de statut).
* Afficher dynamiquement cette liste dans l'interface utilisateur (Swing).
* Permettre d'envoyer un message privé à un utilisateur précis.
* Distinguer visuellement les messages globaux des messages privés.

---

### Étapes à suivre en détail :

#### 1. **Afficher la liste des utilisateurs connectés**

* Créer un composant `JList<String>` (ou `JTable`) dans votre UI.
* Dans le thread de réception, intercepter les messages de type `"status"` :

  ```json
  { "type": "status", "user": "bob", "state": "online" }
  ```
* Maintenir une `HashMap<String, String>` (pseudo → statut) pour savoir qui est connecté.
* Mettre à jour dynamiquement la liste affichée.

#### 2. **Permettre l'envoi de messages privés**

* Lorsqu'un utilisateur est sélectionné dans la liste, permettre d'envoyer un message uniquement à cette personne.
* Adapter le message JSON à envoyer :

  ```json
  {
    "type": "message",
    "subtype": "private",
    "to": "pseudo_destinataire",
    "content": "Message confidentiel"
  }
  ```
* Créer un champ d'envoi ou un bouton qui prend en compte le pseudo sélectionné.

#### 3. **Afficher les messages entrants selon leur type**

* Lorsque vous recevez un message :

  ```json
  { "type": "message", "subtype": "private", "from": "alice", "content": "yo" }
  ```
* Afficher les MP avec un préfixe visuel clair (ex : `**[MP de Alice]** yo` ou fond coloré).
* Les messages globaux doivent conserver leur affichage habituel.

#### 4. **Mettre à jour le modèle objet**

* Dans la classe `Message`, ajouter un attribut `subtype` si ce n'est pas encore fait.
* Ajouter un attribut `to` : le destinataire si MP, ou "global" sinon.
* Dans l'affichage, utiliser `subtype` pour distinguer les types de messages.

---

### Exigences techniques

* L'interface Swing doit inclure une liste déroulante ou cliquable des utilisateurs connectés.
* Il doit être possible de choisir entre un message global et un MP.
* Le traitement des messages reçus doit être correct selon le `subtype`.
* Les messages privés ne doivent pas être visibles par les autres utilisateurs.

---

### Bonus (si vous avancez bien)

* Mettre les messages privés dans un panneau ou un onglet à part.
* Créer des canaux de discussion dynamiques (ex : par groupe ou projet).
* Ajout d'une couleur ou icône personnalisée pour chaque utilisateur connecté.
* Notification sonore ou visuelle pour les nouveaux MP.

---

### Livrables attendus en fin de journée

* Une interface Swing avec affichage de la liste des utilisateurs connectés.
* Fonctionnalité d'envoi de messages privés fonctionnelle.
* Affichage clair des messages selon leur nature (global ou privé).

## 📙 **Jour 5 - Vendredi : Gestion des statuts et finalisation du projet**

### Objectifs de la journée :

* Permettre aux utilisateurs de choisir leur statut (disponible, absent, invisible).
* Envoyer ce statut au serveur et l'afficher dans la liste des connectés.
* Améliorer l'affichage graphique de l'application (interface Swing).
* Vérifier toutes les fonctionnalités du projet et corriger les bugs.
* Préparer les livrables techniques pour finaliser la réalisation professionnelle.

---

### Étapes à suivre :

#### 1. **Ajout de la gestion des statuts utilisateur**

* Ajouter un menu déroulant (`JComboBox`) ou un bouton avec options pour choisir son statut.
* Envoyer un message JSON au serveur dès que le statut est changé :

  ```json
  {
    "type": "status",
    "state": "invisible"
  }
  ```
* Mettre à jour l'affichage des autres utilisateurs dans la liste connectée : afficher leur statut en texte ou icône (ex : point vert, gris, orange).

#### 2. **Finalisation graphique de l'interface Swing**

* Vérifier que tous les composants sont alignés et lisibles.
* Ajouter des bordures, marges, icônes ou couleurs pour améliorer l'expérience utilisateur.
* Gérer la redimension du composant et la réactivité de l'application.

#### 3. **Tests croisés et débogage**

* Tester les interactions entre clients (serveurs différents si besoin).
* Vérifier les cas limites : déconnexion, reconnexion, mauvais identifiants, envoi vide, etc.
* Corriger les bugs restants.

#### 4. **Documentation de la réalisation professionnelle**

* Préparer une documentation claire comprenant :

  * Description du projet
  * Architecture du code (MVC)
  * Capture d’écran de l’application
  * Fichier `README.md` dans le projet avec les instructions de lancement
  * Commentaires dans le code source si ce n'est pas déjà fait

---

### Livrables attendus en fin de journée

* Application complète, testée, stable et fonctionnelle
* Interface Swing finale intégrant la gestion des statuts
* Dossier de documentation (PDF ou README + captures)
* Projet Maven archivable (zip ou Git)

### 🌟 Démonstration finale et validation de la réalisation professionnelle

Pour valider le bon fonctionnement de votre application ClassCord, vous devez procéder à une démonstration opérationnelle de bout en bout. Voici le protocole recommandé :

#### Protocole de démonstration :

1. **Lancer le serveur** (local ou celui d’un binôme SISR).
2. **Lancer un premier client** (avec compte ou en mode invité).
3. **Lancer un second client** sur une autre instance (autre IP ou autre port si besoin).
4. **Effectuer les actions suivantes devant le formateur :**

   * Connexion avec identifiant OU en tant qu'invité
   * Envoi d'un message global
   * Envoi d'un message privé à un autre client
   * Changement de statut et propagation visible (ex : en ligne > invisible)
   * Affichage dynamique de la liste des connectés avec statut
   * Bonne distinction entre message global et MP
   * Aucune erreur lors de la déconnexion

---

### ✨ Liste des BONUS possibles (facultatifs mais valorisés) :

| Catégorie            | Idée de bonus                         | Description                                                                                      |
| -------------------- | ------------------------------------- | ------------------------------------------------------------------------------------------------ |
| **UI / UX**          | Passage à JavaFX                      | Interface plus moderne avec transitions, onglets, effets visuels                                 |
|                      | Thèmes personnalisables               | Choix entre mode clair/sombre ou autre thème                                                     |
|                      | Notifications / sons                  | Popup ou son lors de nouveaux messages (globaux ou MP)                                           |
|                      | Multi-fenêtres MP                     | Ouverture de fenêtre dédiée pour chaque discussion privée                                        |
| **Fonctionnel**      | Création de canaux                    | Permet de rejoindre un groupe thématique (ex : #SISR, #jeu-video)                                |
|                      | Rôles spéciaux (admin, modo)          | Un modérateur peut supprimer des messages, expulser un utilisateur, envoyer un message système   |
|                      | Historique local                      | Enregistrement des discussions dans un fichier texte ou base locale                              |
|                      | Statuts personnalisés                 | Message d’humeur, lien, emoji...                                                                 |
| **Technique**        | Intégration WebSocket client          | Utilisation d’un client WebSocket au lieu de socket brut                                         |
|                      | Version Web (HTML/JS)                 | Interface web avec fetch ou WebSocket, connectée au serveur Java                                 |
|                      | Export logs JSON                      | Sauvegarde automatique des messages ou activités sous forme de fichier JSON                      |
| **Interopérabilité** | Compatibilité avec plusieurs serveurs | Capacité à se connecter à différents serveurs du réseau et changer dynamiquement l'adresse cible |
| **Accessibilité**    | Navigation clavier / raccourcis       | Utilisation des flèches, tabulation, raccourcis pour les commandes principales                   |

N’hésitez pas à innover et à aller au-delà du cahier des charges si le temps le permet : tout ajout utile, stable, pertinent sera valorisé !


---

## 🎓 Compétences mobilisées (Référentiel BTS SIO – SLAM)

A condition d'avoir tout très bien réalisé, voici a maxima les compétences que vous pouvez définir comme couvertes par le projet.

| Bloc de compétences | Référence | Intitulé                                                                                   |
|---------------------|-----------|---------------------------------------------------------------------------------------------|
| Concevoir et développer une solution applicative |
|                     | ✔         | Analyser un besoin exprimé et son contexte juridique                                        |
|                     | ✔         | Participer à la conception de l’architecture d’une solution applicative                    |
|                     | ✔         | Modéliser une solution applicative                                                         |
|                     | ✔         | Identifier, développer, utiliser ou adapter des composants logiciels                       |
|                     | ✔         | Utiliser des composants d’accès aux données                                                |
|                     | ✔         | Réaliser les tests nécessaires à la validation ou à la mise en production                  |
|                     | ✔         | Rédiger des documentations technique et d’utilisation d’une solution applicative          |
|                     | ✔         | Exploiter les fonctionnalités d’un environnement de développement et de tests             |
| Assurer la maintenance corrective ou évolutive d’une solution applicative |
|                     | ✔         | Recueillir, analyser et mettre à jour les informations sur une version d’une application   |
|                     | ✔         | Évaluer la qualité d’une solution applicative                                              |
|                     | ✔         | Analyser et corriger un dysfonctionnement                                                  |
|                     | ✔         | Mettre à jour des documentations technique et d’utilisation d’une solution applicative     |
| Gérer les données |
|                     | ✔         | Développer des fonctionnalités applicatives au sein d’un système de gestion de base de données |
|                     | ✔         | Concevoir ou adapter une base de données                                                   |
|                     | ✔         | Administrer et déployer une base de données                                                |




