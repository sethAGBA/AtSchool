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

---

## 3. Déploiement avec Docker (Recommandé)

Le projet inclut une configuration Docker prête à l'emploi qui lance le serveur et une base de données PostgreSQL.

### Pré-requis
*   Docker Desktop installé et lancé.

### Lancement Rapide (Dev)
Pour lancer tout l'environnement (Serveur + Base de données) :

```bash
docker compose up --build
```

L'API sera accessible sur `http://localhost:8080`.

### Mode Hybride (Dév recommandé)

Pour développer le serveur confortablement (avec `./gradlew run` pour profiter du rechargement à chaud) tout en utilisant la vraie base de données Docker :

1.  Coupez tout autre conteneur : `docker compose down`
2.  Lancez **juste la DB** : `docker compose up -d db`
3.  Lancez le serveur via Gradle : `./gradlew run`

Cela évite le conflit de port sur le `8080`.
L'application se connectera automatiquement à la DB Docker grâce à la config par défaut (`atschool`/`atschool_pass`).

### Déploiement en Production

Pour un déploiement sécurisé, **ne modifiez pas** `docker-compose.yml`. Créez plutôt un fichier `.env` à la racine du projet (ce fichier est ignoré par git pour la sécurité) :

**Fichier `.env` :**
```ini
# Configuration Base de Données
POSTGRES_USER=mon_admin_prod
POSTGRES_PASSWORD=mon_mot_de_passe_secret_prod
POSTGRES_DB=atschool_prod

# Configuration Serveur
# Doit correspondre à la config DB ci-dessus
DB_USER=mon_admin_prod
DB_PASSWORD=mon_mot_de_passe_secret_prod
JDBC_URL=jdbc:postgresql://db:5432/atschool_prod

# Sécurité API
JWT_SECRET=une_phrase_secrete_tres_longue_et_aleatoire_pour_signer_les_tokens
```

Une fois ce fichier créé, lancez simplement Docker normalement. Il utilisera automatiquement vos valeurs secrètes au lieu des valeurs par défaut.

```bash
docker compose up -d --build
```
(L'option `-d` lance les conteneurs en arrière-plan).



<!-- 

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
Votre application est maintenant prête pour le déploiement (en changeant les variables d'env) et pour le dévelopement local sans maux de tête. -->