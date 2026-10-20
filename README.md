# TP14 — Client SOAP Android en Kotlin avec kSOAP2

Application Android du [TP14 du cours](https://mliaedu.toubkalit.com/student/courses/7/labs/167). Elle liste, crée, modifie et supprime des comptes via le service SOAP du TP13. Les appels réseau utilisent des coroutines hors du fil de l'interface.

## Préparer le serveur

Dans le projet TP13, exécuter `mvn spring-boot:run`. Le WSDL doit être visible à `http://localhost:8082/services/ws?wsdl`. L'émulateur Android utilise `10.0.2.2` pour atteindre le serveur de l'ordinateur. L'espace de noms du client correspond à celui du TP13 : `http://ws.demo.example.com/`.

## Compiler et lancer

Ouvrir ce dossier dans Android Studio, laisser Gradle synchroniser, puis lancer l'application sur un émulateur API 24 ou plus. En ligne de commande sous Windows :

```powershell
.\gradlew.bat :app:assembleDebug
```

Le bouton « Modifier » utilise l'opération `updateCompte` ajoutée au TP13 : l'énoncé du TP14 prévoit une action Modifier sans fournir cette opération côté serveur. L'accès HTTP en clair est limité à `10.0.2.2` pour cette démonstration locale.
