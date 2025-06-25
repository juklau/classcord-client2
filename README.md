## Informations Personnelles
- **Klaudia Juhasz**

# 📇 Mini CRM – Gestion des Contacts

Mini CRM est une application web intuitive dédiée à la gestion des contacts clients, prospects et partenaires. Elle permet d’ajouter, rechercher, modifier, supprimer et classer facilement tous les profils liés à votre activité.

### Lancement du Mini CRM
Dans cette application, plusieurs contacts sont déjà enregistrés. Pour les consulter, il suffit de lancer l’application depuis la page `home.html`

## 🚀 Fonctionnalités

- 🔍 **Recherche dynamique** par nom ou entreprise
- ➕ **Ajout de nouveaux contacts** avec photo, informations complètes et statut
- 📝 **Modification des données** de contact via une interface ergonomique
- 🗑️ **Suppression sécurisée** avec confirmation utilisateur
- 📂 **Classement alphabétique** des contacts avec interface en accordéon
- 🌟 **Favoris** : marquer un contact comme important
- 📅 **Date de relance et statut de suivi**
- 📋 **Filtres** par type de contact et statut de relance
- 👤 **Système de connexion** sécurisé => qui pour l'instant ne fonctionne pas...
- 📞 **Formulaire de contact**


## 🖼️ Interfaces disponibles

- **Page d’accueil (Home)** avec affichage de tous les contacts par ordre alphabétique:
En cliquant sur un contact, vous pouvez le modifier ou le supprimer.
- **Page Ajouter un contact**
- **Page de Recherche** avec filtres multicritères
- **Pages statiques** : Connexion, À propos, Contact

## 🧰 Technologies utilisées

- HTML5, CSS3, JavaScript
- Framework Bootstrap pour la mise en page responsive
- Postman pour vérifier le fonctionnement de mes requêtes 
- API Airtable pour la gestion des données
- API Cloudinary pour héberger les images et leur attribuer une URL accessible.
- Font Awesome pour les icônes

## 📁 Structure principale

    ```
   MINI_CRM/
		├── html/
		│   ├── A_propos.html              # Présentation de l'application
		│   ├── ajouter.html               # Formulaire d'ajout de contact
		│   ├── connexion.html             # Page de connexion (non fonctionnelle pour le moment)
		│   ├── home.html                  # Page d'accueil affichant tous les contacts
		│   ├── nous_contacter.html        # Formulaire de contact
		│   ├── plan_du_site.html          # Plan du site pour faciliter la navigation
		│   ├── rechercher.html            # Recherche avancée multicritères
		│   └── new-style.css              # Feuille de style centralisée
		├── js/
		│   ├── affiche_contacts.js        # Affichage dynamique des contacts depuis l’API Airtable
		│   ├── ajouter.js                 # Script pour l’ajout d’un contact
		│   ├── modifier.js                # Script de modification de contact
		│   ├── nous_contacter.js          # Gestion du formulaire de contact
		│   ├── rechercher.js              # Fonction de recherche générale
		│   ├── rechercher_par_nom_entreprise.js # Recherche spécifique par entreprise
		│   └── se-connecter.js            # Script prévu pour la connexion (non finalisé)

    ```


## 🔒 Sécurité et données

Toutes les requêtes vers la base de données sont actuellement sécurisées par un jeton d’authentification API unique et visible. 
La prochaine étape consistera à implémenter un système de connexion utilisateur avec un jeton d’authentification API propre à chaque compte, afin d’améliorer la sécurité et le fonctionnement global de l’application.
La gestion des données personnelles respecte le RGPD.

## 📬 Contact

Pour toute question, suggestion ou demande de support, utilisez le [formulaire de contact](#) intégré à l'application.

---

📌 *Projet conçu dans le cadre de l’application Mini CRM – Gestion simplifiée de vos relations professionnelles.*

**source de code et documentation**

## 🧩 Code source

- HTML / CSS / JavaScript : l’ensemble des fichiers se trouvent probablement dans un dossier projet local (ou sur un dépôt GitHub si tu en as un).
- Scripts JavaScript : pour l’interaction avec Airtable, la recherche, la gestion des favoris, etc.
- API Airtable : utilisée pour récupérer et manipuler les données de contact.
- API Cloudinary : pour héberger les images de profil.
- Frameworks : Bootstrap pour le design, Font Awesome pour les icônes.

## 📘 Documentation

- Voici le line de mon fichier Figma:
    https://www.figma.com/design/9ogs0Hg8QeLXew9DdNP9AC/Klaudia-Juhasz-s-team-library?node-id=3314-2&p=f&t=ice7l1j8TohSXNEv-0 

    (Cette maquette a connu plusieurs améliorations depuis sa conception.)
- README.md
- Commentaires dans mes codes

- Documentation officielles externes :
        *Airtable API Docs
        *Cloudinary Documentation
        *Bootstrap Docs
        *Font Awesome Docs
