# Architecture Backend - AtSchool (ÉcoliX)

Ce document détaille la conception, la sécurité et la structure du backend pour l'application AtSchool.

## 1. Vue d'Ensemble
Le backend est une API REST construite avec **Ktor**, conçue pour être performante, sécurisée et capable de gérer plusieurs établissements scolaires (multi-tenancy) de manière isolée.

### Pile Technologique
- **Langage** : Kotlin
- **Framework** : Ktor 3.x (Asynchrone via Coroutines)
- **Base de données** : PostgreSQL
- **ORM** : JetBrains Exposed
- **Authentification** : JWT (Json Web Token)
- **Migrations** : Flyway
- **Injection de Dépendances** : Koin

## 2. Stratégie de Sécurité
La sécurité est au cœur du système pour protéger les données sensibles des élèves et du personnel.

### Authentification & Accès
- **JWT** : Les tokens sont signés et contiennent le `TenantID` et le `Role`.
- **RBAC (Role-Based Access Control)** : Les accès sont limités selon les rôles (ADMIN, ENSEIGNANT, COMPTABLE, etc.).
- **Hachage** : Utilisation de **BCrypt** pour stocker les mots de passe.

### Défense contre les Attaques
- **Anti-Brute Force** : Limitation du nombre de requêtes par IP (`RateLimiting`).
- **Anti-CSRF** : Validation stricte des origines et des headers pour les opérations de modification.
- **XSS & Sécurité Header** : Configuration de `Content Security Policy` (CSP) et `HSTS`.
- **SQL Injection** : Prévention native via l'utilisation systématique de l'ORM **Exposed**.

## 3. Architecture Multi-Tenant
Pour garantir qu'une école ne puisse jamais accéder aux données d'une autre :
- **Isolation par Schéma** : Chaque établissement possède son propre schéma PostgreSQL.
- **Routage Dynamique** : Le serveur sélectionne le schéma approprié à chaque requête en fonction du `TenantID` présent dans le JWT du client.

## 4. Schéma de Données (Résumé)

### Gestion des Utilisateurs
- `users` : Identifiants de connexion.
- `staff` : Profils professionnels du personnel.

### Structure Scolaire
- `annees_scolaires` : Gestion des périodes actives.
- `classes` : Organisation par niveaux et filières.
- `matieres` : Catalogue des enseignements avec coefficients.

### Gestion des Élèves
- `eleves` : Fiches individuelles et dossiers médicaux.
- `inscriptions` : Suivi des élèves par classe et par année.
- `tuteurs` : Coordonnées des parents/tuteurs.

### Académique & Discipline
- `notes` & `evaluations` : Calcul automatique des moyennes et rangs.
- `absences` & `sanctions` : Suivi disciplinaire en temps réel.

### Finances
- `frais_scolaires` : Configuration des tarifs.
- `paiements` & `recus` : Suivi des encaissements et impayés.

## 5. Roadmap Technique
1.  **Phase 1** : Setup infrastructure (Ktor, DB, Koin).
2.  **Phase 2** : Authentification et Multi-tenancy.
3.  **Phase 3** : Core Modules (Élèves, Classes, Inscriptions).
4.  **Phase 4** : Module Académique (Notes, Bulletins).
5.  **Phase 5** : Module Financier (Paiements).
