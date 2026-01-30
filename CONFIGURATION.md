# Guide de Configuration ÉcoliX

Ce document explique comment l'application est configurée pour le développement local et le déploiement en production.

## 1. Serveur (Backend)

Le serveur utilise **Ktor** et un fichier de configuration HOCON standard situé dans :
`server/src/main/resources/application.conf`

### Structure de la Config

```hocon
ktor {
    deployment {
        port = 8080
        # Peut être surchargé par la variable d'environnement PORT
        port = ${?PORT}
    }
}

jwt {
    # Secret par défaut pour le dev. EN PROD, IL FAUT DÉFINIR LA VAR D'ENVIRONNEMENT !
    secret = "secret-key-atschool-2026"
    secret = ${?JWT_SECRET}
    
    issuer = "http://0.0.0.0:8080/"
    audience = "atschool-users"
    realm = "Access to atschool"
}

database {
    driverClassName = "org.postgresql.Driver"
    jdbcUrl = "jdbc:postgresql://localhost:5432/atschool"
    jdbcUrl = ${?JDBC_URL}
    
    user = "postgres"
    user = ${?DB_USER}
    
    password = "postgres"
    password = ${?DB_PASSWORD}
}
```

### Variables d'Environnement (Production)

Lorsque vous déployez le serveur (sur un VPS, Docker, etc.), vous **devez** définir ces variables d'environnement pour surcharger les valeurs par défaut :

| Variable | Description | Exemple |
| :--- | :--- | :--- |
| `PORT` | Port d'écoute du serveur | `80` ou `443` |
| `JWT_SECRET` | Clé secrète pour signer les tokens | `une-chaine-tres-longue-et-aleatoire` |
| `JDBC_URL` | URL de connexion PostgreSQL | `jdbc:postgresql://db.prod.com:5432/atschool` |
| `DB_USER` | Utilisateur de la base de données | `atschool_user` |
| `DB_PASSWORD` | Mot de passe de la base de données | `db-password` |

---

## 2. Client (Application Mobile/Shared)

Le client utilise le plugin `buildConfig` pour gérer l'URL de l'API.

### Configuration

Le fichier `shared/build.gradle.kts` définit l'URL de base :

```kotlin
buildConfig {
   // ...
   buildConfigField("String", "BASE_URL", "\"http://localhost:8080/\"")
}
```

### Gestion Automatique Android (Localhost)

Une logique spéciale a été ajoutée dans `ApiClient.kt` pour faciliter le développement sur Android.

*   **iOS / Desktop** : Si `BASE_URL` est `localhost`, le système essaie de joindre `localhost` (la machine elle-même).
*   **Android (Émulateur)** : L'émulateur Android ne considère pas `localhost` comme la machine hôte.
    *   Le code détecte automatiquement si l'app tourne sur Android **ET** si l'URL contient "localhost".
    *   Si oui, il remplace automatiquement par `10.0.2.2` (l'alias spécial d'Android pour joindre l'ordinateur).

**Vous n'avez donc rien à faire : lancez le serveur, lancez l'émulateur, et ça marche.**

### Changer pour la Production

Pour préparer une version de production de l'application mobile :

1.  Ouvrez `shared/build.gradle.kts`.
2.  Changez la valeur de `BASE_URL` pour votre domaine réel :
    ```kotlin
    buildConfigField("String", "BASE_URL", "\"https://api.votre-domaine.com/\"")
    ```
3.  Synchronisez Gradle et compilez l'application.




Ce que j'ai fait :
Serveur (Backend) :
Création de server/src/main/resources/application.conf : C'est désormais le fichier central pour vos configs (Port, Base de données, JWT).
Le serveur charge maintenant ce fichier au démarrage.
Sécurité : AuthService.kt ne contient plus de secrets en dur. Les secrets sont injectés via le fichier de config (qui peut lui-même être surchargé par des variables d'environnement comme JWT_SECRET pour la prod).
Base de Données : DatabaseFactory lit désormais la config pour se connecter.
Client (Shared/Mobile) :
J'ai ajouté le plugin BuildConfig à votre projet shared.
J'ai défini une variable BASE_URL = "http://localhost:8080/".
Magie Android : Dans ApiClient.kt, j'ai ajouté une petite logique qui détecte si on est sur Android ET si l'URL est localhost. Si c'est le cas, il remplace automatiquement par 10.0.2.2.
Ça veut dire que vous n'avez rien à toucher. Ça marche sur iOS (localhost) et Android (10.0.2.2) sans changer de code !
Votre application est maintenant prête pour le déploiement (en changeant les variables d'env) et pour le dévelopement local sans maux de tête.