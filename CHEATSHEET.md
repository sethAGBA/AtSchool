# 🚀 ÉcoliX - Aide-mémoire (Cheat Sheet)

Ce document regroupe les commandes les plus utiles pour le développement quotidien.

## 🛠️ Lancement du Projet

### 1. Base de Données (Docker)
Lancer uniquement la base de données PostgreSQL en arrière-plan :
```bash
docker compose up -d db
```
Arrêter la base de données et nettoyer les réseaux :
```bash
docker compose down
```

### 2. Serveur (Backend)
Lancer le serveur Ktor (il se connectera automatiquement à la DB Docker sur le port 5432) :
```bash
./gradlew :server:run
```

### 3. Application Client (Desktop)
Lancer l'application Compose Multiplatform sur votre ordinateur :
```bash
./gradlew :composeApp:run
```

---

## 🔍 Débogage et Port 8080

Si vous recevez une erreur `Address already in use`, le port 8080 est probablement déjà occupé par une instance fantôme.

### Vérifier qui utilise le port 8080 :
```bash
lsof -i :8080
```

### Tuer le processus occupant le port 8080 :
```bash
# Remplacez <PID> par le numéro trouvé avec la commande précédente
kill -9 <PID>
```

**Commande magique (Tuer d'un coup) :**
```bash
kill -9 $(lsof -t -i:8080)
```

---

## 📊 Base de Données

### Connexion directe (si besoin)
- **Host**: `localhost`
- **Port**: `5432`
- **User**: `atschool`
- **Password**: `atschool_pass`
- **Database**: `atschool`

---

## 🔒 SuperAdmin par défaut
- **Email**: `seth@atschool.com`
- **Password**: `superadmin`
- **URL**: [http://localhost:8080](http://localhost:8080)
