Application SaaS de Gestion Scolaire
Écoles Primaires, Collèges & Lycées
Architecture Multi-Plateforme (Desktop + Web + Mobile)

🏗️ Architecture Technique
Stack Technologique
Backend (API REST/GraphQL)

Framework : Ktor (Kotlin) / Spring Boot
Base de données : PostgreSQL (données relationnelles) + Redis (cache)
Authentification : JWT + OAuth2
Storage : AWS S3 / MinIO (documents, photos, bulletins)
Queue : RabbitMQ / Kafka (tâches asynchrones)

Frontend Multi-Plateforme
├── 🖥️ Desktop : Kotlin Compose Multiplatform (Windows, macOS, Linux) - PRIORITAIRE
├── 💻 Web : Kotlin/JS + Compose for Web
├── 📱 Mobile : Kotlin Compose Multiplatform (iOS + Android)
└── 📊 Kiosque : PWA tactile pour bibliothèque/cantine
Base de Données PostgreSQL
sql-- Tables principales
- tenants (multi-tenant architecture)
- etablissements
- annees_scolaires
- classes
- niveaux
- filieres
- eleves
- tuteurs
- inscriptions
- notes
- evaluations
- bulletins
- matieres
- coefficients
- emplois_du_temps
- seances
- enseignants
- personnel_administratif
- absences_eleves
- absences_personnel
- retards
- sanctions
- comportements
- paiements
- frais_scolaires
- factures
- bibliotheque_livres
- bibliotheque_emprunts
- materiel_scolaire
- inventaire
- salles
- logs_activites
- parametres_etablissement
```

### Architecture Multi-Tenant
```
Tenant 1 (École A) ─┐
Tenant 2 (École B) ─┼─→ API Gateway → Backend → Base données isolée
Tenant 3 (École C) ─┘
```

**Isolation des données :**
- Schéma séparé par tenant
- Sécurité au niveau rang (Row-Level Security)
- Backup indépendant par établissement

---

## 📱 Modules & Écrans Détaillés

### 🔹 1. TABLEAU DE BORD

**Vue d'ensemble temps réel**

**Widgets principaux :**
- **Statistiques établissement** : 
  - Effectif total élèves (par sexe, par niveau)
  - Taux de présence du jour
  - Personnel présent/absent
  - Taux de paiement des frais
  
- **Graphiques** :
  - Évolution des inscriptions sur 5 ans
  - Répartition élèves par classe
  - Performance académique globale (moyennes générales)
  - Courbe des paiements mensuels
  
- **Alertes prioritaires** :
  - Élèves absents non justifiés
  - Personnel absent sans remplacement
  - Retards en cours
  - Impayés supérieurs à 3 mois
  - Bulletins non générés
  - Emplois du temps incomplets
  - Matériel en rupture de stock

**Actions rapides :**
- Bouton FAB : "Nouvelle inscription"
- Saisie rapide d'absence
- Enregistrement paiement express
- Recherche globale (élève, enseignant, classe)
- Centre de notifications (temps réel)

**Vue synoptique classes :**
```
En cours | Terminée | Libre | Examen | Sortie pédagogique
   🟢        🔵        ⚪       🟡            🟣
```

---

### 🔹 2. GESTION DES ÉLÈVES

**Écran principal : Registre des élèves**

**Vue principale :**
- DataTable avec colonnes : Photo, Matricule, Nom complet, Classe, Sexe, Date naissance, Tuteur, Statut (Actif/Inactif/Transféré/Diplômé)
- Filtres : Classe, Sexe, Statut, Année d'inscription
- Recherche multi-critères : Nom, matricule, tuteur
- Actions groupées : Export Excel/PDF, envoi SMS/Email groupé

**Fiche élève détaillée**

Tabs:
```
├── 📋 Profil élève
│   ├── Informations personnelles
│   │   ├── Matricule auto-généré
│   │   ├── Nom, prénoms
│   │   ├── Date et lieu de naissance
│   │   ├── Sexe
│   │   ├── Nationalité
│   │   ├── Photo d'identité
│   │   ├── Adresse complète
│   │   └── Groupe sanguin
│   ├── Documents
│   │   ├── Acte de naissance (scan)
│   │   ├── Certificat de scolarité précédent
│   │   ├── Certificat médical
│   │   ├── Photos d'identité
│   │   └── Autres documents
│   └── Informations médicales
│       ├── Allergies
│       ├── Maladies chroniques
│       └── Contacts urgence
│
├── 👨‍👩‍👧 Tuteurs/Parents
│   ├── Tuteur principal
│   │   ├── Nom complet
│   │   ├── Lien de parenté
│   │   ├── Téléphone (principal + secondaire)
│   │   ├── Email
│   │   ├── Profession
│   │   ├── Employeur
│   │   └── Adresse professionnelle
│   └── Tuteur secondaire (mêmes infos)
│
├── 🎓 Parcours scolaire
│   ├── Historique des inscriptions
│   │   ├── Année scolaire
│   │   ├── Classe
│   │   ├── Statut (admis, redoublant, transféré)
│   │   └── Décision de fin d'année
│   ├── Résultats académiques
│   │   ├── Moyennes par année
│   │   ├── Rangs obtenus
│   │   └── Mention aux examens
│   └── Transferts/Changements
│       ├── Date et motif
│       ├── École d'origine/destination
│       └── Documents de transfert
│
├── 📊 Performance académique
│   ├── Notes actuelles (toutes matières)
│   ├── Graphique évolution moyennes
│   ├── Comparaison avec moyenne classe
│   ├── Points forts/faibles par matière
│   └── Prédiction résultats fin d'année
│
├── 📅 Assiduité & Discipline
│   ├── Taux de présence (%)
│   ├── Liste absences (justifiées/non justifiées)
│   ├── Retards
│   ├── Sanctions reçues
│   │   ├── Date et type (avertissement, blâme, exclusion)
│   │   ├── Motif détaillé
│   │   ├── Enseignant/Autorité
│   │   └── Mesures prises
│   └── Comportements positifs (félicitations, mentions)
│
├── 💰 Situation financière
│   ├── Frais de scolarité annuels
│   ├── Montant payé
│   ├── Solde restant
│   ├── Historique des paiements
│   │   ├── Date, montant, mode paiement
│   │   ├── Reçu (numéro + PDF)
│   │   └── Agent encaisseur
│   ├── Échéancier de paiement
│   └── Alertes impayés
│
├── 📚 Bibliothèque & Matériel
│   ├── Livres empruntés (en cours)
│   ├── Historique emprunts/retours
│   ├── Retards de retour
│   ├── Amendes éventuelles
│   └── Matériel scolaire reçu
│
└── 📞 Communications
    ├── SMS envoyés (résultats, absences, paiements)
    ├── Emails envoyés
    ├── Convocations tuteurs
    ├── Notes internes
    └── Observations enseignants
```

**Formulaire nouvelle inscription**

**Wizard en étapes :**
1. **Informations élève** : Identité complète + photo
2. **Informations tuteurs** : 1 ou 2 tuteurs avec contacts
3. **Choix classe** : Niveau + filière (si applicable)
4. **Documents** : Upload acte naissance, certificats
5. **Frais d'inscription** : Montant + paiement premier acompte
6. **Confirmation** : Génération matricule + impression fiche d'inscription

**Fonctionnalités avancées :**
- Import Excel massif d'élèves (pour rentrée)
- Génération matricules auto (format personnalisable : EC2024-001)
- Détection doublons (nom + date naissance)
- Impression fiches individuelles ou listes de classe
- Export statistiques effectifs (par sexe, âge, classe)

---

### 🔹 3. GESTION DES CLASSES

**Écran catalogue classes**

**Structure hiérarchique :**
```
Établissement
├── Cycles
│   ├── Primaire
│   │   ├── CP (Cours Préparatoire)
│   │   ├── CE1, CE2 (Cours Élémentaire)
│   │   ├── CM1, CM2 (Cours Moyen)
│   ├── Collège
│   │   ├── 6ème, 5ème, 4ème, 3ème
│   ├── Lycée
│       ├── Seconde
│       ├── Première (par filière : L, S, ES, etc.)
│       └── Terminale (par filière)
```

**Informations par classe :**
- Code classe (ex: 6ème A, Terminale S1)
- Niveau/Filière
- Effectif actuel / Capacité maximale
- Enseignant titulaire/principal
- Salle principale affectée
- Frais de scolarité associés
- Liste des matières enseignées
- Emploi du temps
- Délégués de classe

**Fiche classe détaillée**

Tabs:
```
├── 📋 Informations générales
│   ├── Code et nom classe
│   ├── Niveau et filière
│   ├── Année scolaire
│   ├── Enseignant principal
│   ├── Effectif (Garçons/Filles)
│   └── Salle attitrée
│
├── 👥 Liste des élèves
│   ├── Tableau élèves inscrits
│   ├── Tri par nom, matricule, moyenne
│   ├── Actions : Ajouter/Retirer élève
│   └── Export liste classe (PDF/Excel)
│
├── 📚 Matières enseignées
│   ├── Liste matières avec coefficients
│   ├── Enseignant par matière
│   ├── Volume horaire hebdomadaire
│   └── Salles spécialisées (si labo, salle info)
│
├── 📅 Emploi du temps
│   ├── Planning hebdomadaire
│   ├── Vue grille (Lundi-Samedi)
│   ├── Modification drag & drop
│   └── Export/Impression
│
├── 📊 Statistiques classe
│   ├── Moyenne générale classe
│   ├── Taux de réussite
│   ├── Taux de présence
│   ├── Graphiques performance par matière
│   └── Comparaison avec autres classes
│
└── 💰 Situation financière
    ├── Total frais de scolarité
    ├── Montant collecté
    ├── Taux de recouvrement
    └── Liste élèves avec impayés
```

**Gestion des niveaux et filières**

**Configuration :**
- Création niveaux personnalisés
- Définition filières par niveau
- Attribution coefficients par matière/niveau
- Barèmes de notation (sur 10, 20, 100)
- Système d'évaluation (semestre, trimestre, contrôle continu)

---

### 🔹 4. GESTION DES NOTES & ÉVALUATIONS

**Écran saisie des notes**

**Interface optimisée (DataTable) :**
- Sélection : Année scolaire, Classe, Matière, Période (Trimestre/Semestre)
- Tableau avec colonnes :
  - Matricule, Nom élève, Note Devoir 1, Note Devoir 2, Note Examen, Moyenne, Rang
- Saisie rapide (touche Tab pour passer)
- Calcul automatique moyennes avec pondération
- Validation par l'enseignant (signature électronique)
- Verrouillage après validation

**Types d'évaluations :**
- Interrogations orales
- Devoirs surveillés
- Devoirs de maison
- Compositions/Examens
- Travaux pratiques (TP)
- Exposés/Projets
- Contrôle continu

**Configuration par matière :**
- Coefficient matière
- Pondération types évaluation (ex: Devoir 30%, Examen 70%)
- Nombre d'évaluations minimales par période
- Barème notation (sur 10 ou 20)

**Calculs automatiques :**

**Formule moyenne élève :**
```
Moyenne Matière = (ΣNotes × Pondération) / 100
Moyenne Générale = Σ(Moyenne Matière × Coefficient) / ΣCoefficients
```

**Calcul du rang :**
- Classement automatique par moyenne décroissante
- Gestion des ex-aequo (même rang)
- Rang par classe et par matière

**Appréciations automatiques :**
```
≥ 18/20 : Excellent
16-17.99 : Très bien
14-15.99 : Bien
12-13.99 : Assez bien
10-11.99 : Passable
< 10 : Insuffisant
```

**Mentions examens officiels :**
```
≥ 16 : Très bien
14-15.99 : Bien
12-13.99 : Assez bien
10-11.99 : Passable
< 10 : Ajourné
```

---

### 🔹 5. GÉNÉRATION DES BULLETINS

**Processus automatisé**

**Étapes de génération :**
1. **Sélection** : Année scolaire, Classe, Période
2. **Vérification** : Toutes les notes saisies et validées
3. **Calculs** : Moyennes, rangs, appréciations automatiques
4. **Génération PDF** : Bulletins personnalisés
5. **Validation direction** : Signature numérique
6. **Distribution** : Impression ou envoi email/SMS aux tuteurs

**Template bulletin personnalisable**

**Structure standard :**
```
┌─────────────────────────────────────────┐
│  [LOGO ÉCOLE]    BULLETIN DE NOTES      │
│  École [Nom]                            │
│  Année scolaire : 2024-2025             │
│  Période : 1er Trimestre                │
├─────────────────────────────────────────┤
│  ÉLÈVE                                  │
│  Nom : [Nom Prénom]                     │
│  Matricule : [MAT-001]                  │
│  Classe : 6ème A                        │
│  Né(e) le : [Date]                      │
├─────────────────────────────────────────┤
│  RÉSULTATS                              │
│  ┌────────────┬──────┬──────┬─────┐    │
│  │  Matière   │ Note │ Coef │ Moy │    │
│  ├────────────┼──────┼──────┼─────┤    │
│  │ Français   │ 15.5 │  4   │ 62  │    │
│  │ Maths      │ 12.0 │  4   │ 48  │    │
│  │ Anglais    │ 14.0 │  2   │ 28  │    │
│  │ ...        │      │      │     │    │
│  ├────────────┴──────┴──────┴─────┤    │
│  │ MOYENNE GÉNÉRALE : 13.85/20    │    │
│  │ RANG : 5ème sur 42 élèves      │    │
│  └────────────────────────────────┘    │
├─────────────────────────────────────────┤
│  APPRÉCIATION GÉNÉRALE                  │
│  Travail satisfaisant. Peut mieux       │
│  faire en mathématiques.                │
│                                         │
│  Conseil de classe : Encouragements     │
├─────────────────────────────────────────┤
│  ABSENCES & DISCIPLINE                  │
│  Absences justifiées : 2                │
│  Absences non justifiées : 0            │
│  Retards : 1                            │
│  Sanctions : Aucune                     │
├─────────────────────────────────────────┤
│  SIGNATURES                             │
│  Enseignant : [Signature]               │
│  Direction : [Signature + Cachet]       │
│  Tuteur : _______________               │
└─────────────────────────────────────────┘
```

**Options de génération :**
- Bulletin individuel (1 élève)
- Bulletins par classe (génération batch)
- Bulletins par niveau complet
- Format PDF (couleur ou noir & blanc)
- Langue (Français, Anglais, Arabe selon région)

**Fonctionnalités avancées :**
- Graphiques performance (courbes évolution)
- Comparaison avec moyenne classe
- Historique résultats années précédentes
- Code QR pour vérification authenticité
- Envoi automatique email aux tuteurs avec pièce jointe
- SMS notification "Bulletin disponible"
- Archivage automatique (7 ans minimum)

---

### 🔹 6. GESTION DES EMPLOIS DU TEMPS

**Écran planning général**

**Vue principale :**
- Planning type Gantt hebdomadaire (Lundi-Samedi)
- Filtres : Par classe, par enseignant, par salle
- Drag & drop pour créer/modifier séances
- Color-coding par matière
- Détection conflits automatique (enseignant/salle occupé)

**Création séance**

**Formulaire :**
- Classe concernée
- Matière
- Enseignant
- Salle
- Jour et horaire (début-fin)
- Type (cours, TP, évaluation, sortie)
- Récurrence (tous les [jour] de [heure] à [heure])

**Gestion des contraintes :**
- Disponibilité enseignants (temps partiel, congés)
- Capacité salles
- Équipements requis (laboratoire, salle informatique)
- Pause déjeuner obligatoire
- Nombre d'heures max par jour/semaine
- Matières à répartir équitablement dans la semaine

**Templates emplois du temps :**
- Modèles pré-configurés par niveau
- Duplication d'une année à l'autre
- Ajustements par classe

**Exports & Impressions :**
- Planning par classe (format A4)
- Planning par enseignant (avec salles)
- Planning par salle (occupation)
- Export Excel/PDF
- Affichage public (écrans salle des profs)

---

### 🔹 7. GESTION DU PERSONNEL

**Écran liste du personnel**

**Catégories :**
```
Personnel enseignant
├── Enseignants permanents
├── Enseignants vacataires
└── Surveillants

Personnel administratif
├── Direction
├── Comptabilité/Caisse
├── Secrétariat
├── Bibliothécaire
├── Infirmier(e)
└── Personnel d'entretien
```

**DataTable avec colonnes :**
- Photo, Matricule, Nom complet, Fonction/Poste, Statut (Actif/Congé/Démissionné), Téléphone, Email

**Fiche personnel détaillée**

Tabs:
```
├── 📋 Profil professionnel
│   ├── Informations personnelles
│   │   ├── Matricule
│   │   ├── Nom complet
│   │   ├── Date et lieu de naissance
│   │   ├── Sexe, nationalité
│   │   ├── Photo
│   │   ├── Adresse
│   │   ├── Téléphone, email
│   │   └── Situation familiale
│   ├── Fonction et statut
│   │   ├── Poste occupé
│   │   ├── Spécialité/Discipline (enseignants)
│   │   ├── Date d'embauche
│   │   ├── Type contrat (CDI, CDD, Vacataire)
│   │   ├── Statut actuel
│   │   └── Supérieur hiérarchique
│   └── Diplômes et formations
│       ├── Niveau d'études
│       ├── Diplômes obtenus (scans)
│       ├── Formations continues
│       └── Certifications
│
├── 👨‍🏫 Affectations (Enseignants)
│   ├── Classes assignées
│   ├── Matières enseignées
│   ├── Volume horaire hebdomadaire
│   ├── Salles de cours
│   └── Responsabilités (prof principal, etc.)
│
├── 📅 Planning & Emploi du temps
│   ├── Emploi du temps personnel
│   ├── Disponibilités
│   ├── Heures supplémentaires
│   └── Heures non effectuées
│
├── 🕐 Assiduité
│   ├── Taux de présence (%)
│   ├── Pointage entrée/sortie (si système)
│   ├── Absences
│   │   ├── Date, durée
│   │   ├── Motif (maladie, congé, personnel)
│   │   ├── Justificatif (certificat médical)
│   │   └── Remplacement effectué
│   ├── Retards
│   └── Congés annuels (pris/restants)
│
├── 💰 Rémunération
│   ├── Salaire de base
│   ├── Primes et indemnités
│   ├── Historique paiements
│   ├── Retenues (absences, avances)
│   ├── Charges sociales
│   └── Mode de paiement (virement, espèces)
│
├── 📊 Évaluations
│   ├── Évaluations annuelles
│   ├── Objectifs fixés/atteints
│   ├── Points forts/axes amélioration
│   ├── Formations recommandées
│   └── Sanctions éventuelles
│
└── 📄 Documents
    ├── Contrat de travail
    ├── Fiche de paie
    ├── Diplômes
    ├── Certificats médicaux
    ├── Demandes de congé
    └── Correspondances
```

**Rôles et permissions système :**
```
├── 👔 Directeur (accès total)
├── 🧑‍💼 Adjoint direction (gestion opérationnelle)
├── 👨‍🏫 Enseignant (notes, absences, emploi du temps)
├── 💰 Caissier/Comptable (paiements, facturation)
├── 📚 Bibliothécaire (gestion bibliothèque)
├── 🏥 Infirmier (dossiers médicaux élèves)
├── 📝 Secrétaire (inscriptions, communications)
└── 👁️ Surveillant (discipline, absences)
```

**Planning de travail**

**Écran planning :**
- Calendrier hebdomadaire/mensuel
- Gestion des shifts/horaires
- Pointage entrée/sortie (optionnel)
- Demandes de congés
  - Formulaire en ligne
  - Workflow validation (hiérarchie)
  - Solde congés mis à jour auto
- Remplacement automatique
  - Alerte si enseignant absent
  - Suggestion remplaçants disponibles
- Export paie (heures travaillées, supplémentaires)

---

### 🔹 8. SUIVI DE LA DISCIPLINE

**Gestion des absences élèves**

**Écran principal :**
- Prise d'absence rapide :
  - Sélection classe + date
  - Cocher élèves absents
  - Motif (maladie, familial, non justifié)
  - Enregistrement groupé
- Liste absences du jour/semaine/mois
- Filtres : Classe, élève, justifiée/non justifiée
- Statistiques absences par élève

**Fiche absence :**
- Élève concerné
- Date(s) d'absence
- Durée (heures, jours)
- Motif déclaré
- Justificatif (certificat médical uploadé)
- Statut (justifiée/non justifiée/en attente)
- Notification tuteur (automatique)
- Décision (acceptée, refusée)

**Gestion des retards**

**Enregistrement retard :**
- Élève, date, heure arrivée
- Motif
- Récurrence (alerte si > 3 retards/mois)
- Sanction éventuelle

**Gestion des sanctions**

**Types de sanctions :**
```
Niveau 1 : Avertissements
├── Avertissement oral
├── Observation écrite
└── Avertissement officiel (au dossier)

Niveau 2 : Sanctions disciplinaires
├── Retenue (heure, durée)
├── Exclusion temporaire de cours
├── Travaux d'intérêt général
└── Blâme

Niveau 3 : Sanctions graves
├── Exclusion temporaire (jours)
├── Conseil de discipline
├── Exclusion définitive
└── Renvoi
```

**Fiche sanction :**
- Élève concerné
- Date et type sanction
- Motif détaillé (incident)
- Enseignant/Autorité ayant sanctionné
- Témoins éventuels
- Mesures prises
- Convocation tuteurs (si grave)
- Signature tuteur (prise de connaissance)
- Voies de recours
- Historique sanctions élève (récidive)

**Comportements positifs**

**Encouragements :**
- Félicitations
- Tableau d'honneur
- Mention spéciale
- Prix d'excellence
- Délégué de classe

**Workflow alerte tuteurs :**
```
Absence/Retard enregistré
        ↓
SMS automatique tuteur ("Votre enfant [Nom] est absent/en retard ce [Date]")
        ↓
Si absence > 3 jours non justifiée
        ↓
Convocation tuteur automatique
        ↓
Entretien avec direction
        ↓
Mesures d'accompagnement ou sanctions
```

---

### 🔹 9. SUIVI DES PAIEMENTS

**Écran gestion des frais scolaires**

**Configuration frais par niveau/classe :**
- Frais d'inscription (une fois)
- Scolarité annuelle (ou par trimestre/mois)
- Fournitures scolaires
- Uniforme
- Cantine
- Transport
- Activités extra-scolaires
- Assurance
- Bibliothèque (caution + abonnement)

**Exemple structure tarifaire :**
```
6ème A
├── Inscription : 25 000 FCFA
├── Scolarité annuelle : 150 000 FCFA (ou 50k × 3 trimestres)
├── Fournitures : 30 000 FCFA
├── Cantine : 5 000 FCFA/mois
└── Total : 205 000 FCFA + Cantine variable
Écran suivi paiements élève
Vue synthétique :

Total frais annuels
Montant payé
Solde restant
Statut (À jour, En retard, Impayé)
Échéancier

Enregistrement paiement
Formulaire :

Élève (recherche par nom ou matricule)
Type de frais (inscription, scolarité, cantine, etc.)
Montant
Mode de paiement :

Espèces (calcul monnaie automatique)
Chèque (numéro, banque)
Virement bancaire (référence)
Mobile Money (MTN, Moov, Orange) - API
Carte bancaire (TPE intégré)
Paiement mixte


Date de paiement
Période concernée (si scolarité : Trimestre 1, 2, 3)
Observation (réduction, bourse, etc.)

Génération reçu automatique
Template reçu :
┌─────────────────────────────────────┐
│   [LOGO ÉCOLE]  REÇU DE PAIEMENT    │
│   École [Nom]                       │
│   N° Reçu : REC-2024-00123          │
├─────────────────────────────────────┤
│   Date : 15/09/2024                 │
│   Reçu de : Mr/Mme [Tuteur]        │
│   Pour : [Nom Élève] - [Classe]    │
│   Matricule : [MAT-001]             │
├─────────────────────────────────────┤
│   DÉTAIL PAIEMENT                   │
│   Frais de scolarité - Trimestre 1 │
│   Montant : 50 000 FCFA             │
│   Mode : Espèces                    │
├─────────────────────────────────────┤
│   Reste à payer : 100 000 FCFA      │
│   Prochain échéance : 15/12/2024    │
├─────────────────────────────────────┤
│   Signature et cachet               │
│   [Signature Agent] [Cachet École]  │
└─────────────────────────────────────┘
```

**Reçu avec :**
- Numéro unique auto-généré
- Date et heure
- Informations élève et tuteur
- Détail paiement
- Solde restant
- QR code (vérification authenticité)
- Impression thermique (caisse) ou A4
- Envoi email/SMS optionnel

**Gestion de caisse**

**Module encaissement :**
- Ouverture/Fermeture caisse
- Fond de caisse initial
- Encaissements du jour (liste détaillée)
- Dépôts intermédiaires
- Décaissements (achats, remboursements)
- Rapport de caisse (attendu vs réel)
- Gestion des écarts (justification)
- Versement en banque

**Alertes automatiques :**
- SMS/Email tuteur si impayé > 1 mois
- Alerte direction si taux recouvrement < 80%
- Blocage accès (optionnel) si impayé > 3 mois
- Relances automatiques avant échéances

**Rapports financiers**

**Rapports standards :**
- Rapport journalier encaissements
- Rapport mensuel par classe
- Rapport annuel global
- Liste impayés (par classe, par montant)
- Prévisionnel encaissements
- Taux de recouvrement (%)
- Répartition paiements par mode
- Statistiques bourses/réductions

**Échéancier & Relances**

**Gestion échéancier :**
- Définition plan de paiement personnalisé
- Échéances par trimestre ou mensuel
- Rappels automatiques (J-7, J-1, J+7 après échéance)
- Historique relances par élève

---

### 🔹 10. GESTION DE LA BIBLIOTHÈQUE

**Catalogue de livres**

**Fiche livre :**
- Code ISBN/Référence interne
- Titre
- Auteur(s)
- Éditeur, année édition
- Catégorie (Roman, Science, Histoire, etc.)
- Niveau recommandé (CP-CM2, Collège, Lycée)
- Nombre d'exemplaires
  - Total
  - Disponibles
  - Empruntés
  - Perdus/Endommagés
- Photo couverture
- Emplacement rayonnage
- Résumé

**Gestion des emprunts**

**Processus emprunt :**
1. Recherche élève (matricule ou nom)
2. Scan code-barre livre ou saisie référence
3. Vérification éligibilité :
   - Pas d'emprunt en retard
   - Quota non dépassé (ex: max 3 livres simultanément)
   - Pas d'amende impayée
4. Enregistrement emprunt
   - Date emprunt
   - Date retour prévue (ex: +14 jours)
5. Impression ticket emprunt

**Gestion retours :**
1. Scan livre retourné
2. Vérification état (bon état, dégradé, perdu)
3. Calcul retard éventuel
4. Amende si retard (ex: 100 FCFA/jour)
5. Clôture emprunt

**Historique emprunts :**
- Par élève : Liste livres empruntés (dates, retours)
- Par livre : Historique circulation
- Statistiques : Livres les plus empruntés, durée moyenne emprunt

**Inventaire bibliothèque :**
- Liste complète des ouvrages
- État du stock
- Alertes livres manquants
- Planification achats nouveaux livres
- Export inventaire (Excel/PDF)

**Amendes et Pénalités :**
- Retard : Calcul automatique (jours × tarif)
- Livre perdu : Valeur de remplacement
- Livre dégradé : Évaluation dommages
- Paiement amendes (intégré module paiements)
- Historique amendes par élève

---

### 🔹 11. GESTION DU MATÉRIEL SCOLAIRE

**Inventaire matériel**

**Catégories :**
```
Fournitures scolaires
├── Cahiers, copies
├── Stylos, crayons
├── Règles, compas
├── Cartables
└── Uniformes

Équipements pédagogiques
├── Matériel scientifique (labo)
├── Ordinateurs/Tablettes
├── Projecteurs, vidéoprojecteurs
├── Livres scolaires
└── Cartes géographiques

Mobilier
├── Tables, chaises
├── Tableaux
├── Armoires
└── Bureaux

Matériel sportif
├── Ballons
├── Filets
├── Tapis de gym
└── Chronomètres
```

**Fiche matériel :**
- Référence/Code
- Désignation
- Catégorie
- Quantité en stock
- Seuil d'alerte
- Prix unitaire
- Fournisseur
- Date d'achat
- État (Neuf, Bon, Usé, HS)
- Localisation (salle, magasin)

**Distribution matériel**

**Processus :**
- Sélection élève/classe
- Choix matériel à distribuer
- Quantité
- Signature élève/tuteur (prise en charge)
- Caution éventuelle (ex: livres)
- Retour fin d'année (vérification état)

**Gestion des achats :**
- Demandes d'achat (enseignants, direction)
- Validation budgétaire
- Bon de commande
- Réception et enregistrement stock
- Suivi fournisseurs

**Alertes réapprovisionnement :**
- Notification si stock < seuil
- Liste articles à commander
- Estimation budget nécessaire

---

### 🔹 12. REPORTING & STATISTIQUES

**Tableau de bord statistiques**

**KPIs principaux :**
```
├── 📊 Indicateurs académiques
│   ├── Taux de réussite global (%)
│   ├── Moyenne générale établissement
│   ├── Taux de redoublement (%)
│   ├── Taux d'abandon scolaire (%)
│   ├── Performance par matière
│   └── Évolution résultats sur 5 ans
│
├── 👥 Indicateurs effectifs
│   ├── Effectif total (par sexe, par niveau)
│   ├── Ratio élèves/enseignant
│   ├── Taux de remplissage classes (%)
│   ├── Évolution inscriptions (YoY)
│   └── Répartition par âge
│
├── 🕐 Indicateurs assiduité
│   ├── Taux de présence élèves (%)
│   ├── Taux de présence personnel (%)
│   ├── Nombre absences justifiées/non justifiées
│   ├── Élèves avec absences critiques (> 10%)
│   └── Taux de retard (%)
│
├── 💰 Indicateurs financiers
│   ├── Total frais de scolarité facturés
│   ├── Montant collecté
│   ├── Taux de recouvrement (%)
│   ├── Total impayés
│   ├── Répartition paiements par mode
│   └── Budget vs Réalisé
│
└── 📚 Indicateurs bibliothèque
    ├── Nombre d'emprunts (mois/année)
    ├── Taux de retour dans les délais (%)
    ├── Livres les plus empruntés
    └── Amendes collectées
```

**Rapports standards**

**Rapports académiques :**
- Procès-verbal conseil de classe (par classe, par trimestre)
- Palmarès fin d'année (meilleurs élèves)
- Liste admis/redoublants
- Statistiques examens officiels (BEPC, BAC)
- Analyse comparative classes

**Rapports administratifs :**
- Liste élèves par classe (avec photos)
- Effectif global (par sexe, âge, nationalité)
- Fiches individuelles élèves
- Attestations de scolarité
- Certificats de radiation/transfert
- Registre matricule

**Rapports financiers :**
- Journal des encaissements
- État des impayés (par classe)
- Prévisionnel budgétaire
- Rapport caisse mensuel
- Bilan financier annuel

**Rapports RH :**
- Liste du personnel (avec contacts)
- Planning enseignants
- Heures supplémentaires
- Absences personnel
- Masse salariale

**Rapports discipline :**
- Absences par classe/élève
- Sanctions prononcées
- Convocations tuteurs

**Exports personnalisés :**
- Générateur de requêtes SQL visuelles (no-code)
- Filtres multiples
- Graphiques (barres, courbes, camemberts)
- Export Excel, PDF, CSV
- Envoi automatique programmé (email)

**Business Intelligence**

**Analyses avancées :**
- Prédiction taux de réussite (Machine Learning)
- Détection élèves à risque d'échec/abandon
- Analyse corrélation assiduité/résultats
- Benchmark avec autres établissements (anonymisé)
- Optimisation répartition classes (équilibrage niveau)
- Analyse rentabilité par niveau

---

### 🔹 13. COMMUNICATION

**Messagerie automatisée**

**SMS automatiques :**
- Confirmation inscription
- Convocation réunion de rentrée
- Absence élève (envoi tuteur immédiat)
- Retard élève
- Sanction disciplinaire
- Rappel échéance paiement (J-7, J+7)
- Résultats disponibles
- Convocation tuteur
- Alerte urgence (fermeture école, etc.)

**Emails automatiques :**
- Bulletin de notes (PDF joint)
- Relevés de paiements
- Calendrier scolaire
- Convocations officielles
- Newsletters école

**Envois groupés :**
- Par classe
- Par niveau
- Tous les tuteurs
- Message personnalisé avec variables ({nom_eleve}, {classe}, etc.)

**CRM Tuteurs**

**Historique communications :**
- Liste SMS/Emails envoyés
- Accusés de réception
- Taux de lecture
- Réponses reçues

**Campagnes ciblées :**
- Invitation portes ouvertes
- Réinscriptions (campagne avant fin d'année)
- Événements (kermesse, journée sportive)
- Appel aux dons/contributions
- Enquêtes satisfaction

**Portail tuteurs (optionnel Web)**

**Espace personnel sécurisé :**
- Login tuteur (matricule élève + mot de passe)
- Consultation notes en temps réel
- Téléchargement bulletins
- Suivi assiduité (absences, retards)
- Relevé paiements
- Emploi du temps élève
- Messagerie avec administration/enseignants
- Demandes (certificat, rendez-vous)

---

### 🔹 14. SÉCURITÉ & CONFORMITÉ

**Gestion des accès**

**Système de permissions granulaire :**
- Authentification par identifiant + mot de passe
- 2FA optionnel (SMS, email)
- Rôles prédéfinis (voir Module 7)
- Permissions par module/fonctionnalité
- Logs d'accès complets (qui, quand, quelle action)

**Registre obligatoire**

**Conformité réglementaire :**
- Registre matricule (obligatoire certains pays)
- Registre des inscriptions annuelles
- Archivage légal (durée selon législation locale)
- Export format requis par autorités (PDF/Excel)
- Transmission automatique inspection académique (si API)

**Protection des données (RGPD/équivalent local)**

**Conformité :**
- Consentement explicite collecte données
- Information tuteurs usage données
- Droit d'accès, rectification, suppression
- Portabilité des données (export)
- Registre des traitements
- Chiffrement base de données (AES-256)
- Sécurisation connexions (TLS/SSL)

**Sauvegarde et restauration**

**Backup automatique :**
- Sauvegarde quotidienne (3h du matin)
- Sauvegarde incrémentielle
- Stockage local + cloud redondant
- Rétention 1 an minimum
- Test restauration trimestriel
- Export manuel on-demand

**Plan de reprise d'activité :**
- Procédure en cas de panne
- Serveur de secours
- Synchronisation données temps réel (si SaaS)

---

### 🔹 15. PARAMÈTRES & ADMINISTRATION

**Configuration établissement**

**Informations générales :**
- Nom complet établissement
- Sigle/Acronyme
- Logo (upload)
- Type (Primaire, Collège, Lycée, Complexe)
- Statut juridique (Public, Privé laïc, Privé confessionnel)
- Numéro d'agrément/Autorisation
- Adresse complète
- Téléphone, email, site web
- Réseaux sociaux

**Année scolaire active :**
- Début et fin année scolaire (dates)
- Périodes d'évaluation :
  - Trimestres (3) ou Semestres (2)
  - Dates de chaque période
  - Dates conseils de classe
  - Dates remise bulletins
- Vacances scolaires (calendrier)
- Jours fériés

**Paramètres académiques :**
- Barème notation (sur 10 ou 20)
- Grille appréciations (seuils)
- Moyenne de passage (ex: 10/20)
- Système calcul moyennes (arithmétique, pondérée)
- Arrondi notes (au supérieur, inférieur, 0.5)
- Nombre d'évaluations minimum par matière/période

**Paramètres financiers :**
- Devise (FCFA, EUR, etc.)
- Frais par niveau/classe
- Modes de paiement acceptés
- Échéanciers standards
- Pénalités retard paiement
- Politique bourses/réductions

**Intégrations**

**APIs tierces :**
- SMS Gateway (Twilio, Africa's Talking, infobip)
- Email service (SendGrid, Mailgun, SMTP)
- Mobile Money (API MTN, Moov, Orange Money)
- Passerelles paiement (Stripe, Fedapay, CinetPay)
- Cloud storage (AWS S3, Google Drive, OneDrive)
- Comptabilité (export vers Sage, Ciel, etc.)
- Inspection académique (si API gouvernementale)

**Personnalisation interface :**
- Thème couleur établissement
- Mode sombre/clair
- Langue (Français, Anglais, Arabe, etc.)
- Format date/heure selon région
- Devise et symbole monétaire

---

### 🔹 16. MODULES COMPLÉMENTAIRES (Extensions)

**Cantine scolaire**

**Gestion cantine :**
- Inscription cantine (par élève)
- Tarif mensuel/journalier
- Menu hebdomadaire
- Pointage repas consommés
- Facturation
- Statistiques fréquentation
- Gestion stock denrées

**Transport scolaire**

**Gestion transport :**
- Inscription transport
- Attribution ligne/arrêt
- Tarification
- Suivi paiements
- Planning itinéraires
- Liste élèves par bus
- Suivi chauffeurs/accompagnateurs

**Infirmerie**

**Dossier médical élèves :**
- Fiche sanitaire
- Antécédents médicaux
- Allergies, traitements en cours
- Vaccinations
- Consultations infirmerie
- Soins dispensés
- Accidents scolaires
- Stock médicaments

**Activités extra-scolaires**

**Gestion activités :**
- Clubs (théâtre, musique, sport, sciences)
- Inscription élèves par club
- Planning activités
- Encadrants
- Facturation
- Événements (concours, spectacles)

**Gestion examens officiels**

**Examens nationaux (BEPC, BAC, etc.) :**
- Inscription élèves candidats
- Génération listes nominatives
- Paiement frais d'examen
- Suivi convocations
- Saisie résultats officiels
- Génération attestations réussite
- Statistiques taux de réussite

---

## 🎨 Interface Utilisateur

### Design System

**Framework UI :**
- **Compose Desktop** avec Material Design 3
- Thème personnalisable par établissement (couleurs primaires/secondaires)
- Mode sombre/clair (auto selon système ou manuel)
- Responsive (desktop prioritaire, puis tablette)
- Support multilingue (FR, EN, AR, ES)
- Polices lisibles (Roboto, Inter)

**Composants réutilisables Compose :**
- DataTables avancées (tri, filtres, pagination, export)
- Calendriers interactifs (emploi du temps)
- Drag & drop (planning)
- Charts dynamiques (Canvas API / Charting library)
- Dialogs et wizards multi-étapes
- Notifications toast
- Signature électronique (Canvas)
- Upload documents (Drag & drop files)
- Recherche autocomplete
- Filtres avancés (multi-critères)

**Navigation :**
- Menu latéral rétractable (rail navigation)
- Top app bar avec recherche globale
- Breadcrumb (fil d'Ariane)
- Shortcuts clavier (Ctrl+N = Nouvelle inscription, etc.)

### UX optimisée par rôle

**Directeur :**
- Dashboard KPIs en première page
- Accès rapide statistiques
- Alertes critiques visibles
- Rapports financiers en un clic

**Enseignant :**
- Emploi du temps personnel en accueil
- Saisie notes simplifiée
- Liste élèves par classe
- Suivi absences rapide

**Caissier :**
- Module paiements en focus
- Encaissement express
- Génération reçus instantanée
- Rapport caisse journalier

**Secrétaire :**
- Inscriptions en priorité
- Gestion documents
- Communications (SMS/Email)
- Édition certificats

---

## ⚡ Fonctionnalités Avancées

### Performance & Scalabilité

**Optimisations :**
- Cache Redis pour requêtes fréquentes (listes classes, élèves)
- Compression images automatique (photos élèves)
- Pagination intelligente (lazy loading tables)
- Indexation base de données optimisée (index sur matricule, classe, année)
- Connection pooling PostgreSQL
- Export asynchrone (rapports lourds en background)

### Mode Offline (Desktop)

**Fonctionnement hors ligne :**
- Base de données locale SQLite en cache
- Synchronisation automatique au retour connexion
- Queue de transactions (inscriptions, paiements, notes)
- Indicateur statut connexion
- Alertes resync
- Résolution conflits (last-write-wins ou manuel)

### Automatisations intelligentes

**IA et Machine Learning :**
- Prédiction risque décrochage scolaire (absences + notes)
- Recommandations orientation (profil élève)
- Optimisation répartition élèves par classe (équilibrage niveau)
- Détection anomalies paiements (fraude)
- Analyse sentiment (enquêtes satisfaction)
- Génération automatique appréciations personnalisées

**Automatisations métier :**
- Passage automatique année supérieure (si admis)
- Génération matricules auto
- Calcul automatique rangs et moyennes
- Alertes absence répétée (> 3 jours)
- Relances paiements programmées
- Archivage fin d'année automatique

### Intégrations avancées

**Export comptabilité :**
- Format compatible Sage, Ciel, QuickBooks
- Export écritures comptables (encaissements)
- Balance comptable

**API REST publique :**
- Documentation OpenAPI/Swagger
- Webhooks (événements : nouvelle inscription, paiement reçu)
- Intégration tierce (site web école, apps mobiles custom)

---

## 🔄 Workflows Types

### Inscription → Année scolaire → Diplôme

```
Demande d'inscription (tuteur)
        ↓
Vérification dossier (documents)
        ↓
Paiement frais d'inscription
        ↓
Génération matricule + Attribution classe
        ↓
Enregistrement base de données
        ↓
Impression fiche inscription + reçu
        ↓
Envoi SMS/Email confirmation
        ↓
===== Début année scolaire =====
        ↓
Saisie notes périodiques (enseignants)
        ↓
Calcul moyennes + rangs automatique
        ↓
Génération bulletins
        ↓
Distribution bulletins (parents)
        ↓
===== Fin trimestre × 3 =====
        ↓
Conseil de classe fin d'année
        ↓
Décision : Admis / Redoublant
        ↓
Si Admis → Passage classe supérieure
        ↓
Si dernière année (Terminale) → Diplômé
        ↓
Archivage dossier complet
```

### Suivi quotidien élève

```
Arrivée école (matin)
        ↓
Pointage présence (par classe ou biométrique)
        ↓
Si absent → SMS automatique tuteur
        ↓
Cours de la journée (selon emploi du temps)
        ↓
Si retard → Enregistrement + motif
        ↓
Si incident discipline → Sanction enregistrée
        ↓
Fin journée
        ↓
Synthèse assiduité mise à jour
        ↓
Dashboard tuteur (portail web) actualisé
```

### Cycle de paiement

```
Début année scolaire
        ↓
Facturation frais annuels (inscription + scolarité)
        ↓
Génération échéancier (ex: 3 trimestres)
        ↓
J-7 échéance → SMS rappel tuteur
        ↓
Réception paiement (caisse/mobile money)
        ↓
Génération reçu automatique + mise à jour solde
        ↓
Envoi reçu email/SMS
        ↓
Si impayé J+7 → Relance automatique
        ↓
Si impayé > 1 mois → Convocation tuteur
        ↓
Si impayé > 3 mois → Alerte direction (mesures exceptionnelles)
```

### Gestion incident élève

```
Signalement incident (enseignant/surveillant)
        ↓
Création fiche incident (détails, témoins)
        ↓
Évaluation gravité (Direction/Vie scolaire)
        ↓
        ├── Léger → Avertissement oral + observation cahier
        ├── Moyen → Sanction (retenue, exclusion cours)
        └── Grave → Conseil de discipline
                ↓
                Convocation tuteur + élève
                ↓
                Audition des parties
                ↓
                Décision (blâme, exclusion temporaire/définitive)
                ↓
                Notification officielle tuteur
                ↓
                Enregistrement dossier disciplinaire élève
                ↓
                Suivi mesures éducatives (si applicable)
```

---

## 📊 Modèle de Tarification SaaS

### Abonnement par nombre d'élèves

**Plans proposés :**

- **Starter** (1-100 élèves) : **15 000 FCFA/mois** (≈ 25 USD)
  - 1 établissement
  - Modules de base (élèves, notes, paiements)
  - Support email
  - 10 Go stockage

- **Business** (101-500 élèves) : **35 000 FCFA/mois** (≈ 60 USD)
  - 1 établissement
  - Tous modules
  - Support prioritaire (email + WhatsApp)
  - 50 Go stockage
  - SMS (500/mois inclus)

- **Professional** (501-1500 élèves) : **75 000 FCFA/mois** (≈ 125 USD)
  - Jusqu'à 3 établissements (complexe scolaire)
  - Tous modules + IA
  - Support dédié (phone + WhatsApp)
  - 200 Go stockage
  - SMS (2000/mois inclus)
  - Portail tuteurs

- **Enterprise** (1500+ élèves) : **Sur devis**
  - Établissements illimités
  - Personnalisations
  - Support 24/7
  - Stockage illimité
  - SMS illimités
  - Formation sur site

**Inclus dans tous les plans :**
- Hébergement cloud sécurisé
- Mises à jour automatiques
- Backup quotidien (30 jours rétention)
- Support technique
- SSL/Sécurité
- 1 formation en ligne (webinar)

**Options additionnelles :**
- **SMS supplémentaires** : +5 000 FCFA / 1000 SMS
- **Portail tuteurs web** : +10 000 FCFA/mois
- **Module cantine** : +5 000 FCFA/mois
- **Module transport** : +5 000 FCFA/mois
- **Module infirmerie** : +3 000 FCFA/mois
- **Mobile Money intégration** : +8 000 FCFA/mois (commission opérateurs)
- **Formation sur site** : 50 000 FCFA/jour
- **Stockage additionnel** : +2 000 FCFA / 10 Go/mois

**Offre de lancement :**
- 🎁 **3 premiers mois gratuits** (plan Starter)
- 🎁 **50% réduction 1ère année** (plans Business/Professional)
- 🎁 **Migration gratuite** depuis ancien système

---

## 🚀 Roadmap de Développement

### Phase 1 (4 mois) - MVP Desktop

**Modules prioritaires :**
- ✅ Gestion élèves (CRUD complet)
- ✅ Gestion classes et niveaux
- ✅ Inscriptions/Réinscriptions
- ✅ Gestion notes et moyennes
- ✅ Génération bulletins (PDF)
- ✅ Gestion paiements (caisse)
- ✅ Génération reçus
- ✅ Emplois du temps basique
- ✅ Gestion personnel (fiche simple)
- ✅ Absences élèves
- ✅ Rapports de base (effectifs, paiements)
- ✅ Authentification et permissions
- ✅ Mode offline (SQLite local)

**Livrable :** Application Desktop Windows/macOS/Linux fonctionnelle, prête pour beta test dans 5-10 écoles pilotes.

### Phase 2 (2 mois) - Fonctionnalités avancées

**Ajouts :**
- ✅ Discipline complète (sanctions, comportements)
- ✅ Bibliothèque (emprunts, inventaire)
- ✅ Matériel scolaire (distribution, stock)
- ✅ Communications SMS/Email (intégration APIs)
- ✅ Emplois du temps avancé (drag & drop, conflits)
- ✅ Portail tuteurs web (consultation en ligne)
- ✅ Reporting avancé (BI, graphiques)
- ✅ Export Excel/PDF tous modules

**Livrable :** Version Desktop enrichie + Portail Web tuteurs.

### Phase 3 (3 mois) - Version Web & Mobile

**Multi-plateforme :**
- ✅ Version Web complète (Kotlin/JS + Compose for Web)
- ✅ App Mobile enseignants (notes, absences)
- ✅ App Mobile tuteurs (consultation)
- ✅ Synchronisation cloud temps réel
- ✅ Mode offline mobile (cache local)

**Livrable :** Écosystème complet Desktop + Web + Mobile.

### Phase 4 (2 mois) - Modules complémentaires

**Extensions :**
- ✅ Cantine scolaire
- ✅ Transport scolaire
- ✅ Infirmerie
- ✅ Activités extra-scolaires
- ✅ Gestion examens officiels
- ✅ Mobile Money intégration (API MTN, Moov, Orange)

**Livrable :** Solution tout-en-un complète.

### Phase 5 (Continu) - Optimisation & IA

**Améliorations :**
- ✅ IA prédictive (décrochage, performance)
- ✅ Chatbot support tuteurs
- ✅ Recommandations personnalisées
- ✅ Analytics avancés
- ✅ Optimisation performance (cache, queries)
- ✅ Intégrations tierces (comptabilité, inspection)
- ✅ Expansion internationale (autres pays africains)
- ✅ Support plus de langues (Swahili, Wolof, etc.)

---

## 📱 Applications Mobiles Spécifiques

### App Enseignant (iOS/Android)

**Fonctionnalités :**
- 📅 Mon emploi du temps
- 📝 Saisie notes rapide (par classe)
- ✅ Pointage absences (liste élèves)
- 📊 Consultation moyennes classe
- 📢 Envoi messages tuteurs
- 📋 Consultation fiches élèves
- 🔔 Notifications (conseils de classe, événements)

**Interface :**
- Login sécurisé (email + mot de passe)
- Dashboard : Cours du jour + actions rapides
- Mode hors ligne (sync auto)

### App Tuteur (iOS/Android)

**Fonctionnalités :**
- 👤 Profil élève(s)
- 📊 Notes et bulletins (téléchargement PDF)
- 📅 Emploi du temps enfant
- ✅ Suivi assiduité (absences, retards)
- 💰 Situation financière (solde, paiements)
- 📚 Emprunts bibliothèque
- 📢 Messagerie avec école
- 🔔 Notifications push (absences, résultats, paiements)

**Interface :**
- Login : Matricule élève + code PIN
- Dashboard : Vue synthétique enfant(s)
- Multi-enfants (si plusieurs dans même école)

### App Administration (iOS/Android - Tablette)

**Pour direction/secrétariat :**
- 📊 Dashboard KPIs temps réel
- ➕ Inscription express (formulaire simplifié)
- 💰 Encaissement mobile (scan QR élève)
- 📢 Envoi notifications urgentes
- 📋 Consultation rapide données élèves
- 📈 Rapports synthétiques

---

## 🏆 Avantages Compétitifs

**Par rapport aux solutions existantes (SchoolSoft, Edusys, etc.) :**

✅ **100% adapté au marché africain**
- Mobile Money natif (MTN, Moov, Orange)
- Mode offline robuste (connectivité limitée)
- SMS intégré (principal canal communication)
- Tarifs abordables (PME éducatives)

✅ **Multiplateforme Kotlin Compose**
- Desktop first (réalité terrain : PC fixes écoles)
- Code partagé (70-80%) → maintenance facilitée
- Performance native (pas Web wrapping)
- Une seule équipe dev (Kotlin everywhere)

✅ **Interface en français**
- UX pensée pour utilisateurs locaux
- Terminologie éducative locale
- Support dialectes (future)

✅ **Tout-en-un**
- Pas de modules séparés coûteux
- Pas d'intégrations complexes
- Formation simplifiée

✅ **Sécurité et conformité**
- RGPD et lois locales
- Sauvegarde automatique
- Hébergement sécurisé

✅ **Support local**
- WhatsApp support (+ phone)
- Formation en français
- Compréhension contexte local

✅ **Prix transparent**
- Pas de frais cachés
- Pas de commission élevée paiements
- Scalable (petites → grandes écoles)

---

## 🎯 Personas Utilisateurs

### 1. **Le Directeur** - M. Kouamé, 52 ans
**Besoins :**
- Vision globale établissement (KPIs)
- Décisions basées données (effectifs, finances)
- Gagner du temps (automatisation)

**Douleurs actuelles :**
- Registres papier illisibles
- Pas de vue consolidée
- Erreurs de calcul manuelles

**Utilisation app :**
- Dashboard statistiques (matin)
- Validation bulletins (fin période)
- Rapports financiers (mensuel)

### 2. **L'Enseignante** - Mme Diallo, 34 ans
**Besoins :**
- Saisie notes rapide et simple
- Suivi élèves (absences, comportement)
- Communication facile avec tuteurs

**Douleurs actuelles :**
- Calcul moyennes fastidieux
- Cahiers d'appel perdus
- Difficile joindre parents

**Utilisation app :**
- Saisie notes (après éval)
- Pointage absences (chaque cours)
- Consultation fiches élèves

### 3. **Le Caissier** - M. Traoré, 28 ans
**Besoins :**
- Encaissement rapide et sûr
- Génération reçus instantanée
- Rapport caisse fiable

**Douleurs actuelles :**
- Reçus manuels longs
- Erreurs de calcul monnaie
- Fermeture caisse compliquée

**Utilisation app :**
- Encaissement express (toute la journée)
- Rapport caisse (fin journée)
- Suivi impayés

### 4. **Le Tuteur** - Mme Koné, 41 ans
**Besoins :**
- Suivre scolarité enfant
- Être informée rapidement
- Payer facilement

**Douleurs actuelles :**
- Pas d'infos en temps réel
- Déplacements école fréquents
- Paiements file d'attente

**Utilisation app :**
- Consultation notes (hebdo)
- Réception SMS absences
- Paiement Mobile Money

---

## 📚 Annexes Techniques

### Stack Détaillée

**Backend :**
```kotlin
// Framework: Ktor
dependencies {
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-auth:2.3.7")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.7")
    
    // Database
    implementation("org.jetbrains.exposed:exposed-core:0.45.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.45.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.45.0")
    implementation("org.postgresql:postgresql:42.6.0")
    
    // Redis
    implementation("io.lettuce:lettuce-core:6.3.0")
    
    // S3/MinIO
    implementation("io.minio:minio:8.5.7")
}
```

**Frontend Desktop (Compose Multiplatform) :**
```kotlin
kotlin {
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("io.ktor:ktor-client-core:2.3.7")
                implementation("org.jetbrains.exposed:exposed-core:0.45.0")
            }
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.xerial:sqlite-jdbc:3.44.1.0") // Offline mode
            }
        }
    }
}
```

### Schéma Base de Données (Simplifié)

```sql
-- Table tenants (multi-tenant)
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom VARCHAR(255) NOT NULL,
    slug VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Table etablissements
CREATE TABLE etablissements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID REFERENCES tenants(id),
    nom VARCHAR(255) NOT NULL,
    type VARCHAR(50), -- primaire, college, lycee
    adresse TEXT,
    telephone VARCHAR(20),
    email VARCHAR(100),
    logo_url TEXT
);

-- Table annees_scolaires
CREATE TABLE annees_scolaires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    etablissement_id UUID REFERENCES etablissements(id),
    libelle VARCHAR(20), -- ex: 2024-2025
    date_debut DATE,
    date_fin DATE,
    est_active BOOLEAN DEFAULT FALSE
);

-- Table classes
CREATE TABLE classes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    etablissement_id UUID REFERENCES etablissements(id),
    code VARCHAR(20) UNIQUE NOT NULL, -- ex: 6emeA
    niveau VARCHAR(50), -- 6eme, 5eme, etc.
    filiere VARCHAR(50), -- L, S, ES, null si non applicable
    capacite_max INTEGER,
    enseignant_principal_id UUID,
    salle_id UUID
);

-- Table eleves
CREATE TABLE eleves (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    etablissement_id UUID REFERENCES etablissements(id),
    matricule VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenoms VARCHAR(100) NOT NULL,
    sexe CHAR(1), -- M/F
    date_naissance DATE,
    lieu_naissance VARCHAR(100),
    nationalite VARCHAR(50),
    adresse TEXT,
    photo_url TEXT,
    groupe_sanguin VARCHAR(5),
    statut VARCHAR(20) DEFAULT 'actif', -- actif, inactif, transfere, diplome
    created_at TIMESTAMP DEFAULT NOW()
);

-- Table tuteurs
CREATE TABLE tuteurs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID REFERENCES eleves(id),
    nom_complet VARCHAR(200) NOT NULL,
    lien_parente VARCHAR(50), -- pere, mere, tuteur
    telephone_principal VARCHAR(20),
    telephone_secondaire VARCHAR(20),
    email VARCHAR(100),
    profession VARCHAR(100),
    adresse TEXT,
    est_principal BOOLEAN DEFAULT FALSE
);

-- Table inscriptions
CREATE TABLE inscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID REFERENCES eleves(id),
    classe_id UUID REFERENCES classes(id),
    annee_scolaire_id UUID REFERENCES annees_scolaires(id),
    date_inscription DATE DEFAULT CURRENT_DATE,
    statut VARCHAR(20) DEFAULT 'inscrit', -- inscrit, admis, redoublant, transfere
    montant_frais_inscription DECIMAL(10,2),
    montant_scolarite_annuelle DECIMAL(10,2),
    UNIQUE(eleve_id, annee_scolaire_id)
);

-- Table matieres
CREATE TABLE matieres (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    etablissement_id UUID REFERENCES etablissements(id),
    libelle VARCHAR(100) NOT NULL,
    code VARCHAR(20),
    categorie VARCHAR(50) -- scientifique, litteraire, artistique
);

-- Table coefficients (par niveau/filiere)
CREATE TABLE coefficients (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    matiere_id UUID REFERENCES matieres(id),
    niveau VARCHAR(50),
    filiere VARCHAR(50),
    coefficient INTEGER DEFAULT 1
);

-- Table evaluations
CREATE TABLE evaluations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    classe_id UUID REFERENCES classes(id),
    matiere_id UUID REFERENCES matieres(id),
    annee_scolaire_id UUID REFERENCES annees_scolaires(id),
    periode VARCHAR(20), -- trimestre_1, trimestre_2, etc.
    type VARCHAR(50), -- devoir, examen, tp
    date_evaluation DATE,
    bareme DECIMAL(5,2) DEFAULT 20.00,
    ponderation INTEGER DEFAULT 100, -- % dans moyenne
    enseignant_id UUID,
    est_validee BOOLEAN DEFAULT FALSE
);

-- Table notes
CREATE TABLE notes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evaluation_id UUID REFERENCES evaluations(id),
    eleve_id UUID REFERENCES eleves(id),
    note DECIMAL(5,2),
    observation TEXT,
    UNIQUE(evaluation_id, eleve_id)
);

-- Table bulletins
CREATE TABLE bulletins (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID REFERENCES eleves(id),
    classe_id UUID REFERENCES classes(id),
    annee_scolaire_id UUID REFERENCES annees_scolaires(id),
    periode VARCHAR(20),
    moyenne_generale DECIMAL(5,2),
    rang INTEGER,
    total_eleves INTEGER,
    appreciation_generale TEXT,
    decision_conseil VARCHAR(100), -- encouragements, felicitations, avertissement
    date_generation TIMESTAMP DEFAULT NOW(),
    est_valide BOOLEAN DEFAULT FALSE,
    pdf_url TEXT
);

-- Table paiements
CREATE TABLE paiements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID REFERENCES eleves(id),
    annee_scolaire_id UUID REFERENCES annees_scolaires(id),
    type_frais VARCHAR(50), -- inscription, scolarite, cantine, etc.
    montant DECIMAL(10,2) NOT NULL,
    mode_paiement VARCHAR(50), -- especes, mobile_money, cheque, virement
    reference_paiement VARCHAR(100), -- pour mobile money, virement
    date_paiement TIMESTAMP DEFAULT NOW(),
    agent_encaisseur_id UUID,
    numero_recu VARCHAR(50) UNIQUE NOT NULL,
    periode_concernee VARCHAR(50), -- trimestre_1, mois_janvier, etc.
    observation TEXT
);

-- Table absences_eleves
CREATE TABLE absences_eleves (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID REFERENCES eleves(id),
    date_absence DATE NOT NULL,
    duree_heures DECIMAL(4,2) DEFAULT 1.0,
    motif VARCHAR(100),
    est_justifiee BOOLEAN DEFAULT FALSE,
    justificatif_url TEXT,
    date_enregistrement TIMESTAMP DEFAULT NOW(),
    enregistre_par_id UUID
);

-- Table retards
CREATE TABLE retards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID REFERENCES eleves(id),
    date_retard DATE NOT NULL,
    heure_arrivee TIME,
    duree_minutes INTEGER,
    motif VARCHAR(100),
    enregistre_par_id UUID
);

-- Table sanctions
CREATE TABLE sanctions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    eleve_id UUID REFERENCES eleves(id),
    date_sanction DATE NOT NULL,
    type_sanction VARCHAR(50), -- avertissement, blame, exclusion_temporaire, etc.
    motif TEXT NOT NULL,
    mesures_prises TEXT,
    autorite_id UUID, -- enseignant ou direction
    duree_jours INTEGER, -- pour exclusions
    tuteur_convoque BOOLEAN DEFAULT FALSE,
    date_fin_sanction DATE
);

-- Table emploi_du_temps_seances
CREATE TABLE emploi_du_temps_seances (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    classe_id UUID REFERENCES classes(id),
    matiere_id UUID REFERENCES matieres(id),
    enseignant_id UUID,
    salle_id UUID,
    jour_semaine INTEGER, -- 1=Lundi, 6=Samedi
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    annee_scolaire_id UUID REFERENCES annees_scolaires(id)
);

-- Table personnel
CREATE TABLE personnel (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    etablissement_id UUID REFERENCES etablissements(id),
    matricule VARCHAR(50) UNIQUE NOT NULL,
    nom VARCHAR(100) NOT NULL,
    prenoms VARCHAR(100) NOT NULL,
    sexe CHAR(1),
    date_naissance DATE,
    fonction VARCHAR(100), -- enseignant, directeur, secretaire, etc.
    specialite VARCHAR(100), -- pour enseignants
    type_contrat VARCHAR(50), -- CDI, CDD, vacataire
    date_embauche DATE,
    statut VARCHAR(20) DEFAULT 'actif',
    telephone VARCHAR(20),
    email VARCHAR(100),
    adresse TEXT,
    salaire_base DECIMAL(10,2),
    photo_url TEXT
);

-- Autres tables: bibliotheque_livres, bibliotheque_emprunts, materiel_scolaire, inventaire, logs_activites, etc.
```

---

## 🎬 Conclusion

Ce document constitue une architecture complète et détaillée pour une **application SaaS de gestion scolaire en Kotlin Compose Multiplatform (Desktop First)**, adaptée aux réalités du marché africain, notamment francophone (Côte d'Ivoire, Sénégal, Cameroun, etc.).

### Points clés :
✅ **16 modules complets** couvrant l'intégralité du cycle scolaire
✅ **Desktop first** avec Kotlin Compose pour performance native
✅ **Mode offline robuste** pour contextes à connectivité limitée
✅ **Mobile Money intégré** (MTN, Moov, Orange)
✅ **SMS/Email automatisés** pour communication efficace
✅ **Tarifs abordables** (15k - 75k FCFA/mois) pour PME éducatives
✅ **Roadmap réaliste** (MVP en 4 mois)
✅ **Multiplateforme** (Desktop, Web, Mobile) avec code partagé

### Prochaines étapes recommandées :
1. **Prototypage UI** (Figma ou directement Compose Desktop)
2. **Architecture backend** (Ktor + PostgreSQL)
3. **MVP Phase 1** (modules critiques : élèves, notes, paiements)
4. **Beta test** (5-10 écoles pilotes)
5. **Itération** basée feedback terrain
6. **Lancement commercial** (marketing ciblé)

**Document v1.0 - Architecture complète prête pour développement Kotlin Compose Multiplatform** 🚀


# 🎓 Propositions de Noms pour votre Projet

## 🏆 Mes Recommandations TOP 3

### 1. **EduFlow** ⭐⭐⭐⭐⭐
**Tagline :** *"La gestion scolaire qui coule de source"*

**Pourquoi c'est excellent :**
- ✅ Court, mémorable, international
- ✅ "Edu" = Éducation (universel)
- ✅ "Flow" = Fluidité, automatisation
- ✅ Domaine disponible : eduflow.app, eduflow.tech
- ✅ Fonctionne en français et anglais

**Package Kotlin :** `com.eduflow.desktop`

---

### 2. **SchoolSync** ⭐⭐⭐⭐
**Tagline :** *"Synchronisez votre école, simplifiez votre gestion"*

**Pourquoi c'est solide :**
- ✅ Évoque la synchronisation (online/offline)
- ✅ "School" immédiatement compréhensible
- ✅ Moderne, tech-friendly
- ✅ Domaine : schoolsync.io, schoolsync.app

**Package Kotlin :** `com.schoolsync.app`

---

### 3. **ÉcoliX** (ou **Ecolix**) ⭐⭐⭐⭐⭐
**Tagline :** *"L'expérience école réinventée"*

**Pourquoi c'est unique :**
- ✅ "Écoli" rappelle "École" en français
- ✅ "X" = Moderne, tech, expérience
- ✅ Court, brandable, original
- ✅ Forte identité africaine/francophone
- ✅ Domaine : ecolix.app, ecolix.io

**Package Kotlin :** `com.ecolix.app`

---

## 🌍 Variantes Afrique-Centrées

### 4. **AfroSchool** / **AfroEdu**
**Tagline :** *"Gestion scolaire pensée pour l'Afrique"*
- ✅ Identité africaine forte
- ✅ Positionnement marché clair
- ❌ Peut sembler limitant pour expansion

### 5. **BantuEdu**
**Tagline :** *"Ubuntu dans la gestion scolaire"* (Ubuntu = humanité en Bantu)
- ✅ Valeurs africaines
- ✅ Original et culturel
- ❌ Moins universel

### 6. **SahelSchool** / **SavanEdu**
**Tagline :** *"La tech éducative d'Afrique de l'Ouest"*
- ✅ Référence géographique
- ❌ Restrictif géographiquement

---

## 💡 Options Créatives/Modernes

### 7. **Klassi** (ou **Klassy**)
**Tagline :** *"Votre classe, digitalisée"*
- ✅ Phonétique "Class" + "i" moderne
- ✅ Court, catchy
- ✅ Féminin (empowerment éducation)

### 8. **Notio** (ou **Notéo**)
**Tagline :** *"Notes, notifications, tout en un"*
- ✅ Évoque "notes" et "notion"
- ✅ Court et moderne
- ✅ .io domain friendly

### 9. **Académix**
**Tagline :** *"Le mix parfait pour votre académie"*
- ✅ Académique + moderne (X)
- ✅ Sonorité professionnelle
- ❌ Peut sembler trop formel

### 10. **ÉcolePro** / **SchoolPro**
**Tagline :** *"La solution pro pour votre école"*
- ✅ Clair, direct, professionnel
- ❌ Peu original, générique

---

## 🔥 Noms avec Jeux de Mots

### 11. **SmartClass**
**Tagline :** *"Smart tools for smart schools"*
- ✅ Self-explanatory
- ❌ Très utilisé (concurrence SEO)

### 12. **DigiÉcole** / **DigiSchool**
**Tagline :** *"Digitalisez votre établissement"*
- ✅ Clair sur la valeur
- ❌ DigiSchool existe déjà (France)

### 13. **ÉcoleCloud**
**Tagline :** *"Votre école dans le cloud"*
- ✅ Évoque le SaaS
- ❌ Peu mémorable

---

## 🎯 Analyse Comparative

| Nom | Mémorabilité | Tech-feel | Afrique-friendly | Domaine dispo | Score Global |
|-----|--------------|-----------|------------------|---------------|--------------|
| **EduFlow** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ | **9.5/10** |
| **ÉcoliX** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ✅ | **9.5/10** |
| **SchoolSync** | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ | **9/10** |
| **AfroSchool** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ✅ | **8/10** |
| **Klassi** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ | **8.5/10** |

---

## 🏅 Ma Recommandation Finale

### **Je vote pour : ÉcoliX (Ecolix)** 🎯

**Raisons :**
1. **Unique et brandable** - Aucun concurrent majeur avec ce nom
2. **Mémorable** - Court, catchy, facile à prononcer
3. **Identité forte** - Ancré dans l'éducation avec une touche moderne
4. **Scalable** - Fonctionne en français, anglais, et autres langues
5. **Domaine disponible** - ecolix.app, ecolix.io, ecolix.com
6. **Déclinaisons marketing** :
   - Logo avec "X" stylisé (crayon, tableau, etc.)
   - Slogan : "L'X-périence école"
   - Couleurs : Bleu éducation + Orange énergie

**Alternatives si Ecolix pris :**
- **EcoliXe** (avec E final, plus français)
- **ÉcoliX+** (version premium)

---

## 📦 Structure Projet avec le Nom

```
ecolix/
├── ecolix-backend/          # Backend Ktor
│   └── src/main/kotlin/com/ecolix/api/
├── ecolix-desktop/          # Desktop Compose
│   └── src/main/kotlin/com/ecolix/desktop/
├── ecolix-web/              # Web Compose
│   └── src/main/kotlin/com/ecolix/web/
├── ecolix-mobile/           # Mobile Compose
│   └── src/main/kotlin/com/ecolix/mobile/
├── ecolix-shared/           # Code commun
│   └── src/commonMain/kotlin/com/ecolix/shared/
└── README.md
```

**Package principal :** `com.ecolix.app`

**Repo GitHub :** `github.com/votre-username/ecolix`

---

## 🎨 Idées Branding Rapide

### Logo concept pour ÉcoliX :
```
     ___
    |   |   É C O L I
    |_X_|            X
    
    (X = Crayon stylisé ou tableau digital)
```

**Palette couleurs :**
- Primaire : Bleu #2563EB (confiance, éducation)
- Secondaire : Orange #F97316 (énergie, innovation)
- Accent : Vert #10B981 (croissance, réussite)

---

## 🤔 Vérifications Avant Décision Finale

Avant de valider, vérifiez :

1. ✅ **Domaine disponible** : ecolix.app, ecolix.io, ecolix.com
   - Vérifier sur Namecheap, Google Domains
   
2. ✅ **Réseaux sociaux** : @ecolix sur Twitter, Instagram, LinkedIn
   
3. ✅ **Trademark** : Recherche OAPI (Afrique) et INPI (France)
   
4. ✅ **Google Search** : Pas de concurrent majeur
   
5. ✅ **Prononciation** : Facile dans plusieurs langues

---

## 🚀 Mes 3 Finalistes

Si vous deviez choisir maintenant :

1. **ÉcoliX** (Ecolix) - Unique, moderne, scalable ⭐⭐⭐⭐⭐
2. **EduFlow** - Professionnel, international ⭐⭐⭐⭐⭐
3. **SchoolSync** - Tech-friendly, clair ⭐⭐⭐⭐

**Mon coup de cœur personnel : ÉcoliX** 💙

Qu'en pensez-vous ? Quel nom résonne le plus avec votre vision ? Ou voulez-vous que je génère d'autres options dans un style particulier ?
# 🚀 Architecture Kotlin Compose Multiplatform pour ÉcoliX

## ✅ Configuration Projet Compose Multiplatform

### Structure de Projet Complète

```
ecolix/
├── composeApp/                          # App principale (Desktop/Mobile/Web)
│   ├── src/
│   │   ├── commonMain/                  # Code partagé (70-80%)
│   │   │   ├── kotlin/com/ecolix/
│   │   │   │   ├── App.kt              # Point d'entrée UI commun
│   │   │   │   ├── di/                 # Dependency Injection (Koin)
│   │   │   │   ├── data/
│   │   │   │   │   ├── models/         # Data classes
│   │   │   │   │   ├── repository/     # Repository pattern
│   │   │   │   │   ├── local/          # SQLite local
│   │   │   │   │   └── remote/         # Ktor Client API
│   │   │   │   ├── domain/             # Business logic
│   │   │   │   │   ├── usecases/
│   │   │   │   │   └── entities/
│   │   │   │   ├── presentation/       # UI Layer
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── dashboard/
│   │   │   │   │   │   ├── eleves/
│   │   │   │   │   │   ├── notes/
│   │   │   │   │   │   ├── paiements/
│   │   │   │   │   │   └── ...
│   │   │   │   │   ├── components/     # Composables réutilisables
│   │   │   │   │   ├── navigation/
│   │   │   │   │   ├── theme/
│   │   │   │   │   └── viewmodels/
│   │   │   │   └── utils/
│   │   │   └── resources/              # Images, strings, etc.
│   │   │
│   │   ├── desktopMain/                # Code spécifique Desktop
│   │   │   └── kotlin/com/ecolix/
│   │   │       ├── Main.kt             # Point d'entrée Desktop
│   │   │       └── platform/           # Platform-specific code
│   │   │
│   │   ├── androidMain/                # Code spécifique Android
│   │   │   └── kotlin/com/ecolix/
│   │   │       └── MainActivity.kt
│   │   │
│   │   ├── iosMain/                    # Code spécifique iOS
│   │   │   └── kotlin/com/ecolix/
│   │   │
│   │   └── wasmJsMain/                 # Code spécifique Web
│   │       └── kotlin/com/ecolix/
│   │
│   └── build.gradle.kts
│
├── server/                              # Backend Ktor (optionnel)
│   └── src/main/kotlin/com/ecolix/api/
│
├── gradle/
├── gradle.properties
├── settings.gradle.kts
└── build.gradle.kts
```

---

## 📦 Configuration `build.gradle.kts`

### Fichier Root `build.gradle.kts`

```kotlin
plugins {
    alias(libs.plugins.kotlin.multiplatform).apply(false)
    alias(libs.plugins.kotlin.compose).apply(false)
    alias(libs.plugins.android.application).apply(false)
    alias(libs.plugins.kotlinx.serialization).apply(false)
}
```

### `composeApp/build.gradle.kts`

```kotlin
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    // ========================================
    // DESKTOP (Priorité)
    // ========================================
    jvm("desktop") {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    // ========================================
    // ANDROID
    // ========================================
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    // ========================================
    // iOS
    // ========================================
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    // ========================================
    // WEB (WASM)
    // ========================================
    wasmJs {
        moduleName = "composeApp"
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }
    
    // ========================================
    // SOURCES SETS
    // ========================================
    sourceSets {
        // Common (Code partagé)
        val commonMain by getting {
            dependencies {
                // Compose Multiplatform
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                
                // Navigation
                implementation(libs.voyager.navigator)
                implementation(libs.voyager.screenModel)
                implementation(libs.voyager.transitions)
                
                // Ktor Client (API calls)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
                implementation(libs.ktor.client.auth)
                
                // Kotlinx
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                
                // Dependency Injection (Koin)
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                
                // Local Database
                implementation(libs.sqldelight.runtime)
                implementation(libs.sqldelight.coroutines)
                
                // Image Loading
                implementation(libs.kamel.image)
                
                // Settings (Preferences)
                implementation(libs.multiplatform.settings)
            }
        }
        
        // Desktop
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                
                // Ktor Client Engine
                implementation(libs.ktor.client.cio)
                
                // SQLite Driver
                implementation(libs.sqldelight.sqlite.driver)
                
                // File Picker
                implementation(libs.filekit.compose)
                
                // PDF Generation
                implementation(libs.itext.pdf)
            }
        }
        
        // Android
        val androidMain by getting {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
                
                // Ktor Client Engine
                implementation(libs.ktor.client.okhttp)
                
                // SQLite Driver
                implementation(libs.sqldelight.android.driver)
                
                // Android specific
                implementation(libs.koin.android)
                implementation(libs.androidx.core.ktx)
            }
        }
        
        // iOS
        val iosMain by creating {
            dependencies {
                // Ktor Client Engine
                implementation(libs.ktor.client.darwin)
                
                // SQLite Driver
                implementation(libs.sqldelight.native.driver)
            }
        }
        
        // Web
        val wasmJsMain by getting {
            dependencies {
                // Ktor Client Engine
                implementation(libs.ktor.client.js)
            }
        }
    }
}

// ========================================
// CONFIGURATION ANDROID
// ========================================
android {
    namespace = "com.ecolix.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    defaultConfig {
        applicationId = "com.ecolix.app"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    
    buildFeatures {
        compose = true
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// ========================================
// CONFIGURATION DESKTOP
// ========================================
compose.desktop {
    application {
        mainClass = "com.ecolix.MainKt"
        
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            
            packageName = "ÉcoliX"
            packageVersion = "1.0.0"
            description = "Solution de gestion scolaire moderne"
            vendor = "ÉcoliX Team"
            
            // Windows
            windows {
                iconFile.set(project.file("src/desktopMain/resources/icon.ico"))
                menuGroup = "ÉcoliX"
                perUserInstall = true
            }
            
            // macOS
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/icon.icns"))
                bundleID = "com.ecolix.app"
            }
            
            // Linux
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icon.png"))
            }
        }
    }
}
```

---

## 📝 Fichier `libs.versions.toml`

```toml
[versions]
kotlin = "2.0.21"
compose = "1.7.1"
agp = "8.2.2"
android-compileSdk = "34"
android-minSdk = "24"
android-targetSdk = "34"

ktor = "2.3.12"
kotlinx-coroutines = "1.8.1"
kotlinx-serialization = "1.7.1"
kotlinx-datetime = "0.6.1"
voyager = "1.1.0-beta02"
koin = "3.5.6"
sqldelight = "2.0.2"
kamel = "0.9.5"
multiplatform-settings = "1.1.1"
filekit = "0.8.2"
itext = "5.5.13.3"

[libraries]
# Ktor
ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-cio = { module = "io.ktor:ktor-client-cio", version.ref = "ktor" }
ktor-client-okhttp = { module = "io.ktor:ktor-client-okhttp", version.ref = "ktor" }
ktor-client-darwin = { module = "io.ktor:ktor-client-darwin", version.ref = "ktor" }
ktor-client-js = { module = "io.ktor:ktor-client-js", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }
ktor-client-logging = { module = "io.ktor:ktor-client-logging", version.ref = "ktor" }
ktor-client-auth = { module = "io.ktor:ktor-client-auth", version.ref = "ktor" }

# Kotlinx
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinx-coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization" }
kotlinx-datetime = { module = "org.jetbrains.kotlinx:kotlinx-datetime", version.ref = "kotlinx-datetime" }

# Navigation (Voyager)
voyager-navigator = { module = "cafe.adriel.voyager:voyager-navigator", version.ref = "voyager" }
voyager-screenModel = { module = "cafe.adriel.voyager:voyager-screenmodel", version.ref = "voyager" }
voyager-transitions = { module = "cafe.adriel.voyager:voyager-transitions", version.ref = "voyager" }

# DI (Koin)
koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose = { module = "io.insert-koin:koin-compose", version.ref = "koin" }
koin-android = { module = "io.insert-koin:koin-android", version.ref = "koin" }

# SQLDelight
sqldelight-runtime = { module = "app.cash.sqldelight:runtime", version.ref = "sqldelight" }
sqldelight-coroutines = { module = "app.cash.sqldelight:coroutines-extensions", version.ref = "sqldelight" }
sqldelight-sqlite-driver = { module = "app.cash.sqldelight:sqlite-driver", version.ref = "sqldelight" }
sqldelight-android-driver = { module = "app.cash.sqldelight:android-driver", version.ref = "sqldelight" }
sqldelight-native-driver = { module = "app.cash.sqldelight:native-driver", version.ref = "sqldelight" }

# Image Loading
kamel-image = { module = "media.kamel:kamel-image", version.ref = "kamel" }

# Settings
multiplatform-settings = { module = "com.russhwolf:multiplatform-settings", version.ref = "multiplatform-settings" }

# File Picker (Desktop)
filekit-compose = { module = "io.github.vinceglb:filekit-compose", version.ref = "filekit" }

# PDF Generation (Desktop)
itext-pdf = { module = "com.itextpdf:itextpdf", version.ref = "itext" }

# Android
androidx-core-ktx = { module = "androidx.core:core-ktx", version = "1.13.1" }
androidx-activity-compose = { module = "androidx.activity:activity-compose", version = "1.9.2" }

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
kotlin-compose = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
compose = { id = "org.jetbrains.compose", version.ref = "compose" }
compose-compiler = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
android-application = { id = "com.android.application", version.ref = "agp" }
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

---

## 🎨 Point d'Entrée Desktop

### `desktopMain/kotlin/com/ecolix/Main.kt`

```kotlin
package com.ecolix

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.ecolix.di.initKoin
import com.ecolix.presentation.App

fun main() = application {
    // Initialiser Koin
    initKoin()
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "ÉcoliX - Gestion Scolaire",
        state = rememberWindowState(
            size = DpSize(1400.dp, 900.dp)
        )
    ) {
        App()
    }
}
```

---

## 🎯 App Principal (Common)

### `commonMain/kotlin/com/ecolix/App.kt`

```kotlin
package com.ecolix

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.ecolix.presentation.screens.auth.LoginScreen
import com.ecolix.presentation.theme.EcolixTheme

@Composable
fun App() {
    EcolixTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Navigator(LoginScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
```

---

## 🎨 Theme Material 3

### `commonMain/kotlin/com/ecolix/presentation/theme/Theme.kt`

```kotlin
package com.ecolix.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Couleurs ÉcoliX
private val BluePrimary = Color(0xFF2563EB)      // Bleu confiance
private val OrangeSecondary = Color(0xFFF97316)  // Orange énergie
private val GreenAccent = Color(0xFF10B981)      // Vert réussite

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = OrangeSecondary,
    tertiary = GreenAccent,
    background = Color(0xFFF8FAFC),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1E293B),
    onSurface = Color(0xFF1E293B)
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    secondary = OrangeSecondary,
    tertiary = GreenAccent,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9)
)

@Composable
fun EcolixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 📊 Exemple Écran (Dashboard)

### `commonMain/kotlin/com/ecolix/presentation/screens/dashboard/DashboardScreen.kt`

```kotlin
package com.ecolix.presentation.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<DashboardScreenModel>()
        val state by screenModel.state.collectAsState()
        
        Scaffold(
            topBar = { DashboardTopBar() },
            bottomBar = { DashboardBottomNav() }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "Tableau de Bord",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
                
                item {
                    StatsRow(state)
                }
                
                item {
                    AlertsCard(state.alerts)
                }
                
                item {
                    QuickActionsCard()
                }
            }
        }
    }
}

@Composable
private fun StatsRow(state: DashboardState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Élèves",
            value = state.totalEleves.toString(),
            icon = Icons.Default.School,
            color = MaterialTheme.colorScheme.primary
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Présents",
            value = "${state.tauxPresence}%",
            icon = Icons.Default.CheckCircle,
            color = MaterialTheme.colorScheme.tertiary
        )
        
        StatCard(
            modifier = Modifier.weight(1f),
            title = "Paiements",
            value = "${state.tauxPaiement}%",
            icon = Icons.Default.Payment,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color
            )
        }
    }
}
```

---

**Voulez-vous que je continue avec :**
1. ScreenModel (ViewModel) pattern
2. Repository + API Client complet
3. SQLite local configuration
4. Navigation setup détaillé
5. Un module complet (ex: Gestion Élèves) ?
