# Spécifications des Paramètres Académiques - ÉcoliX

Ce document définit les champs et règles de gestion à implémenter dans la section **Paramètres** de la Gestion Académique.

## 1. Configuration des Périodes
*   **Système de découpage** : `TRIMESTRE` (3), `SEMESTRE` (2), ou `HYBRIDE`.
*   **Nombre de périodes** : Entier (définit le nombre de colonnes dans les tableaux de notes).
*   **Chevauchement autorisé** : Booléen (permet de commencer une période avant la fin de la précédente).

## 2. Règles de Notation et Coefficients
*   **Type de calcul des moyennes** : `ARITHMÉTIQUE_SIMPLE` ou `PONDÉRÉE_COEFFICIENTS`.
*   **Barème par défaut** : Entier (ex: 20 ou 100).
*   **Coefficients standards** :
    *   Interrogations/Devoirs : Défaut `1.0`.
    *   Compositions/Examens : Défaut `2.0` ou `3.0`.
*   **Arrondi des notes** :
    *   `AUCUN` (ex: 12.33)
    *   `PROCHE` (ex: 12.33 -> 12.5)
    *   `ENTIER` (ex: 12.33 -> 12)

## 3. Gestion des Délais (Deadlines)
*   **Délai de saisie (jours)** : Nombre de jours autorisés après une évaluation pour saisir ou modifier les notes.
*   **Verrouillage automatique** : Booléen (bloque toute modification dès que la date de fin de période est atteinte).

## 4. Assiduité et Comportement
*   **Impact sur la moyenne** : Booléen (déduire des points pour absences non justifiées).
*   **Note de conduite** : Booléen (activer un champ spécifique pour l'évaluation comportementale).
*   **Seuil d'alerte absence** : Nombre d'heures avant notification parentale.

## 5. Paramètres de Passage (Promotions)
*   **Moyenne de passage** : Seuil minimal pour la promotion automatique (ex: 10.0).
*   **Seuil de rachat** : Seuil pour l'examen en conseil (ex: 9.5).

## 6. Affichage sur le Bulletin
*   **Appréciations automatiques** : Table de correspondance (ex: 0-7: Médiocre, 14-16: Bien).
*   **Visibilité du rang** : Booléen (afficher ou masquer la position de l'élève).
*   **Statistiques de classe** : Booléen (afficher moyenne min/max et moyenne générale de classe).
