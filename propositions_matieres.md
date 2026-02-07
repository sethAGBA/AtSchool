# Propositions d'Améliorations Fonctionnelles : Gestion des Matières

Voici un résumé des pistes d'amélioration pour la logique et le fonctionnement du module des matières :

### 1. Système de Propagation (Héritage)
- **Logique** : Lier le "Coefficient par défaut" de la matière maître aux configurations de classes.
- **Action** : Ajouter une option pour répercuter les changements de coefficients ou volumes horaires sur toutes les classes existantes ou seulement celles sans dérogations.

### 2. Duplication de Configuration de Classe
- **Logique** : Éviter la saisie répétitive pour des classes de même niveau.
- **Action** : Créer une fonction "Cloner la configuration" (matières, coefficients, profs) d'une classe vers une autre ou vers tout un niveau (ex: de 6ème A vers toutes les 6èmes).

### 3. Contrôle de la Charge Horaire des Professeurs
- **Logique** : Suivi du temps de travail des enseignants lors de l'affectation.
- **Action** : Afficher un indicateur de charge (ex: "15h/18h") lors de l'affectation d'un prof pour éviter les surcharges ou les conflits.

### 4. Groupement Pédagogique (Matières & Sous-matières)
- **Logique** : Gérer les disciplines complexes (ex: Français = Dictée + Grammaire).
- **Action** : Implémenter une structure parent/enfant permettant la saisie de notes détaillées avec calcul automatique de la moyenne pondérée pour la matière principale.

### 5. Import / Export en Masse
- **Logique** : Initialisation rapide des données.
- **Action** : Support des fichiers CSV/Excel pour charger la liste complète des matières, codes et coefficients en une seule opération.

### 6. Règle d'Unicité et d'Intégrité
- **Logique** : Fiabilité des données.
- **Action** : 
    - Validation stricte des codes uniques (ex: un seul code "MATH").
    - Vérification du total des coefficients par classe selon les normes de l'établissement.
