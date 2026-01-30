Audit des Écrans et Fonctionnalités Manquantes
Ce document liste les modules, écrans et fonctionnalités techniques identifiés comme manquants ou à approfondir pour faire d'ÉcoliX une solution de gestion scolaire complète et robuste.

🖥️ Modules et Écrans Manquants
🩺 Santé et Infirmerie
Fiches Médicales Détaillées : Suivi des allergies, vaccinations, et antécédents.
Journal de l'Infirmerie : Registre des passages, soins administrés et alertes parents.
🚌 Logistique et Transport
Gestion des Lignes : Définition des trajets, arrêts et véhicules.
Suivi des Passagers : Liste des élèves par ligne et pointage à la montée/descente.
🍱 Cantine et Restauration
Gestion des Menus : Publication des menus hebdomadaires.
Paiements des Repas : Systèmes de tickets ou forfaits cantine.
🏘️ Internat / Dortoirs
Attribution des Chambres : Gestion des lits et des colocataires.
Suivi des Présences Nocturnes : Appels en soirées et gestion des sorties.
📅 Événements et Calendrier
Calendrier Scolaire Interactif : Vacances, jours fériés, réunions parents-profs.
Réservation de Salles : Planning d'utilisation des laboratoires, gymnase, etc.
👔 Ressources Humaines (Avancé)
Gestion de la Paie : Génération des bulletins de salaire (lié à la Comptabilité).
Suivi des Congés et Absences Profs : Workflow de demande et approbation.
Contrats et Documents RH : Stockage des contrats de travail et évaluations.
⚙️ Fonctionnalités Techniques Manquantes
🧱 Infrastructure de Données
Persistance Réelle (SQL) : Migration des ScreenModels (actuellement en Mock) vers une base de données réelle (Room/SQLite ou SQLDelight).
Synchronisation Cloud : Système de synchronisation pour le multi-dispositif.
Système de Sauvegarde : Exportation et restauration de la base de données.
📄 Moteurs de Génération
Générateur PDF Natif : Implémentation réelle des exports (actuellement des mocks) utilisant une bibliothèque comme OpenPDF (JVM) ou similaire pour Multiplatform.
Éditeur de Templates : Interface pour personnaliser le design des bulletins et cartes d'identité.
🔔 Système de Notifications
Centre de Notifications : Alertes pour paiements en retard, absences, ou messages urgents.
Push Notifications : Intégration Firebase pour les mobiles.
🌍 Internationalisation et Accessibilité
Multi-langue (I18n) : Support pour l'anglais, l'arabe, etc. (actuellement majoritairement en français).
Thèmes Avancés : Personnalisation des couleurs de marque par établissement.
🔐 Sécurité et Audit
Permissions Granulaires : Définir exactement ce que chaque rôle (Secrétaire, Comptable, Surveillant) peut voir ou modifier.
Journal d'Activités (Logs) : Tracer qui a modifié quelle donnée et quand (pour la traçabilité financière).
🤝 Portails Utilisateurs
Portail Parents/Élèves : Application dédiée pour consulter les notes et payer les frais.
Interface Enseignant Simplifiée : Saisie rapide des notes et absences sur mobile.

Comment
⌥⌘M
