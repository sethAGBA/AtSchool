# Stratégie de Déploiement et Onboarding

Voici l'analyse de l'architecture pour passer d'un projet de développement à un produit fini.

### 1. Le compte `admin@atschool.com`
C'est ce qu'on appelle un **"Bootstrap Account"**. 
- **En production :** Ce compte ne devrait exister que dans l'environnement de test. 
- **La réalité :** Chaque école aura son propre espace (Tenant) et son propre compte administrateur unique (ex: `directeur@ecole-excellence.ci`).

### 2. Le flux d'onboarding (Comment le client arrive ?)
Il y a généralement deux approches selon le modèle économique :

*   **Option A : Self-Service (Modèle SaaS)**
    1. L'école va sur le site web et clique sur "Créer mon école".
    2. Elle remplit un formulaire : Nom de l'école, Email du directeur, Ville.
    3. Le système crée automatiquement une nouvelle entrée dans la table `Tenants` et un utilisateur `ADMIN` lié à ce `tenantId`.
    4. Le directeur reçoit un email pour configurer son mot de passe.

*   **Option B : Installation Manuelle (Modèle Accompagné)**
    1. Vente de la solution à une école.
    2. Le Super-Admin possède une interface cachée pour créer le Tenant "Manuellement".
    3. Communication des premiers identifiants à l'école.

### 3. Comment le client se connecte ?
Le défi du multi-tenant est d'identifier l'école de l'utilisateur lors de la connexion.

*   **Approche par Sous-domaine (la plus pro) :**
    - L'école "Excellence" se connecte sur `excellence.atschool.com`.
    - L'application identifie immédiatement le `tenantId` lié à "Excellence".
*   **Approche par Code École :**
    - Sur la page de login, on demande : "Code École", "Email", "Mot de passe".

### 4. La configuration de l'école (L'écran "Mon École")
Une fois connecté pour la première fois, l'admin arrive sur un assistant de configuration :
1.  **Identité** : Logo, Nom officiel, Slogan, En-tête des bulletins.
2.  **Structure** : "Quels sont vos cycles ?" (Primaire, Collège, etc.).
3.  **Période** : "Quelle est l'année scolaire actuelle ?" (ex: 2024-2025).

### 5. Installation technique
Comment l'application arrive chez le client ?

1.  **Le Serveur** : Hébergé sur le Cloud (ex: AWS, Google Cloud, ou un serveur privé). Les clients n'installent rien côté serveur.
2.  **L'Application (Client)** :
    *   **Desktop (Windows/Mac)** : Fourniture d'un fichier `.exe` ou `.dmg` à télécharger.
    *   **Web** : Utilisation d'un navigateur (Chrome/Firefox).
    *   **Mobile** : Distribution via Play Store / App Store.

---

### Questions d'orientation pour la suite :

1.  **Modèle** : Inscription autonome des écoles (SaaS) ou création manuelle des comptes par l'éditeur ?
2.  **Accès** : Utilisation d'un "Code École" au login ou adresses personnalisées (ex: `ecole1.atschool.com`) ?
3.  **Hébergement** : Un seul serveur pour toutes les écoles (Multi-tenant partagé) ou un serveur dédié par école ? (Le code actuel est optimisé pour un serveur partagé).



1. Le compte admin@atschool.com
C'est ce qu'on appelle un "Bootstrap Account".

En production : Ce compte ne devrait exister que dans ton environnement de test.
La réalité : Chaque école aura son propre espace (Tenant) et son propre compte administrateur unique (ex: directeur@ecole-excellence.ci).
2. Le flux d'onboarding (Comment le client arrive ?)
Il y a généralement deux approches selon ton modèle économique :

Option A : Self-Service (Modèle SaaS)
L'école va sur ton site web et clique sur "Créer mon école".
Elle remplit un formulaire : Nom de l'école, Email du directeur, Ville.
Le système crée automatiquement une nouvelle entrée dans la table Tenants et un utilisateur ADMIN lié à ce tenantId.
Le directeur reçoit un email pour configurer son mot de passe.
Option B : Installation Manuelle (Modèle Accompagné)
Tu vends la solution à une école.
Toi (en tant que Super-Admin), tu as une interface cachée pour créer le Tenant "Manuellement".
Tu leur donnes leurs premiers identifiants.
3. Comment le client se connecte ?
Le plus gros défi du multi-tenant est de savoir à quelle école appartient l'utilisateur qui tente de se connecter.

Approche par Sous-domaine (la plus pro) :
L'école "Excellence" se connecte sur excellence.atschool.com.
L'application sait immédiatement que c'est le tenantId de "Excellence".
Approche par Code École :
Sur la page de login, on demande : "Code École", "Email", "Mot de passe".
4. La configuration de l'école (L'écran "Mon École")
Une fois connecté pour la première fois, l'admin arrive sur un assistant de configuration :

Identité : Logo, Nom officiel, Slogan, En-tête des bulletins.
Structure : "Quels sont vos cycles ?" (Primaire, Collège, etc.).
Période : "Quelle est l'année scolaire actuelle ?" (ex: 2024-2025).
5. Installation technique
Comment l'application arrive chez eux ?

Le Serveur : Il est hébergé sur le Cloud (ex: AWS, Google Cloud, ou un serveur privé). Les clients n'installent rien côté serveur.
L'Application (Client) :
Desktop (Windows/Mac) : Tu leur fournis un fichier .exe ou .dmg qu'ils téléchargent une seule fois.
Web : Ils utilisent simplement Chrome/Firefox.
Mobile : Via le Play Store / App Store.
Mes questions pour toi afin d'orienter la suite :

Ton modèle : Est-ce que tu veux que n'importe quelle école puisse s'inscrire seule sur internet (SaaS), ou est-ce que c'est toi qui vas créer les comptes pour tes clients ?
L'accès : Préfères-tu qu'ils tapent un "Code École" au login, ou qu'ils aient chacun une adresse personnalisée (ex: ecole1.atschool.com) ?
L'hébergement : Est-ce que tu prévois un seul gros serveur pour toutes les écoles (Multi-tenant partagé), ou chaque école aura son propre petit serveur ? (Le code actuel est fait pour un serveur partagé, ce qui est beaucoup plus simple à maintenir).
Veux-tu qu'on rédige une petite "Spécification d'Onboarding" pour mettre cela au clair ?

---

## Choix Validés (2026-01-30)

Voici les décisions finales pour la mise en œuvre :

### 1. Modèle de Distribution
- **Côté Serveur** : Les clients n'installent rien. L'application est hébergée sur un serveur central (Cloud).
- **Côté Client** : Multi-plateforme (Desktop, Web, Mobile).

### 2. Architecture Multi-Tenancy
- **Hébergement** : Un seul serveur centralisé (Multi-tenant partagé) pour toutes les écoles. 

### 3. Système d'Accès : Le "Code École"
- Pour identifier l'école lors de la connexion, nous utiliserons un **Code École**.
- Sur la page de login : **Code École**, **Email**, **Mot de passe**.
- Le serveur utilise le code pour router la requête vers le bon `tenantId`.

### 4. Configuration Initiale ("Mon École")
- L'administrateur pourra personnaliser son espace (Logo, Nom, Structure) via une interface dédiée une fois connecté.