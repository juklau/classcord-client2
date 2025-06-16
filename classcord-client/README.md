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

A la fin du premier journée le projet est livrable:
    *Projet Maven fonctionnel dans VSCode.
    *Fichier pom.xml configuré avec la dépendance JSON.
    *Packages Java créés et classes User et Message valides.
    *Compilation sans erreur.
