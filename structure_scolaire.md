# Refonte : Gestion de la Structure Scolaire & Classes

Ce document spécifie la refonte de l'écran **"Élèves & Classes"** (`StudentsScreen`) pour y intégrer la gestion structurelle de l'établissement (Cycles et Niveaux).

## 1. Vision & Objectifs
L'objectif est de transformer cet écran en un véritable **Centre de Pilotage Scolaire**.
Nous passons d'une gestion "à plat" (Classes isolées) à une gestion "hiérarchique" :
**Structure (Cycles > Niveaux) --> Classes --> Élèves**

## 2. Nouvelle Architecture de l'Écran
L'écran sera accessible via le menu latéral "Élèves".
Il disposera d'une navigation supérieure à 3 onglets (ou ViewToggle) :

| Onglet | Rôle | Description |
| :--- | :--- | :--- |
| **1. STRUCTURE** (Nouveau) | *L'Architecte* | Définition de l'arborescence (Cycles et Niveaux). |
| **2. CLASSES** | *Le Constructeur* | Création des classes physiques basées sur les Niveaux définis. |
| **3. ÉLÈVES** | *L'Occupant* | Gestion des inscriptions dans les classes. |

---

## 3. Détail des Fonctionnalités

### Onglet A : STRUCTURE (Nouveau)
C'est ici que l'administration configure l'offre pédagogique.

**Interface :** Vue hiérarchique (Liste ou Arbre).

1.  **Cycles** (Le Conteneur)
    *   *Exemples :* "Maternelle", "Primaire", "Collège", "Lycée".
    *   Permet de regrouper les niveaux.
    *   Possède un ordre de tri (ex: Maternelle avant Primaire).

2.  **Niveaux Scolaires** (L'Étape)
    *   *Exemples :* "Petite Section", "CP", "6ème", "Terminale".
    *   **Règle d'or :** Un niveau appartient obligatoirement à un Cycle.
    *   **Configuration par défaut :** On peut définir ici la capacité standard ou les frais de scolarité par défaut (futur).

**Actions Utilisateur :**
*   [+] Ajouter un Cycle.
*   [+] Ajouter un Niveau dans un Cycle.
*   Modifier / Supprimer / Réordonner.

### Onglet B : CLASSES (Visualisation et Création)
L'interface de gestion des classes évolue pour tirer parti de la structure.

**Formulaire de Classe (`ClassForm`) - Changements :**
*   **Avant :** Le champ "Niveau" était un texte libre. (Risque : "6eme", "6è", "Sixième" coexistent).
*   **Après :** Le champ "Niveau" devient une **Liste Déroulante (Dropdown)**.
    *   Elle affiche l'arborescence définie dans l'onglet Structure.
    *   Exemple : "Collège > 6ème".
    *   Conséquence : Toutes les classes de 6ème sont maintenant reliées par un ID commun.

### Onglet C : ÉLÈVES
Pas de changement structurel majeur, mais les filtres "Par Niveau" deviennent dynamiques et précis grâce à la nouvelle structure backend.

---

## 4. Modèle de Données (Backend)

Cette refonte s'appuie sur de nouvelles tables en base de données :

### Table `school_cycles`
*   `id` (PK)
*   `name` (String, ex: "Collège")
*   `sort_order` (Int)
*   `tenant_id`

### Table `school_levels`
*   `id` (PK)
*   `name` (String, ex: "6ème")
*   `cycle_id` (FK -> school_cycles)
*   `tenant_id`

### Table `classrooms` (Modification)
*   Ajout de `school_level_id` (FK -> school_levels).
*   *Migration :* On conserve temporairement le champ `level` (String) le temps de migrer les données existantes.

---

## 5. Plan d'Implémentation

1.  **Phase 1 : Backend & API (Fondations)**
    *   Création des tables et des repos Ktor (`StructureRepository`).
    *   Endpoints API pour le CRUD des Cycles et Niveaux.

2.  **Phase 2 : UI Onglet Structure**
    *   Implémentation de `StructureView` dans `StudentsScreen`.
    *   Logique de navigation et State Management (`StudentsScreenModel`).

3.  **Phase 3 : Connexion Classes**
    *   Mise à jour du `ClassForm` avec le nouveau sélecteur.
    *   Mise à jour de la sauvegarde pour lier la classe au `school_level_id`.
