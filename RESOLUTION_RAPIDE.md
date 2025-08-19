# Résolution rapide - Problème d'import CSV Axelor

## 🚨 Problème identifié

L'erreur `Invalid character between encapsulated token and delimiter at line: 5, position: 1,775` est causée par un espace entre `href` et `=` dans le HTML de la ligne 4.

## ✅ Solution immédiate

### 1. Corriger le fichier CSV

Dans votre fichier CSV, ligne 4, remplacer :
```html
<a href ="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

Par :
```html
<a href="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

### 2. Utiliser le fichier corrigé

Utilisez le fichier `mail_corrige.csv` que j'ai créé, qui contient :
- ✅ Espace supprimé entre `href` et `=`
- ✅ Guillemets échappés avec `&quot;`
- ✅ HTML valide

## 🔧 Solutions alternatives

### Option 1: Échappement avec entités HTML
```html
<a href=&quot;http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list&quot;>
```

### Option 2: Guillemets simples
```html
<a href='http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list'>
```

### Option 3: Utilisation de variables prétraitées
Dans votre binding XML, ajoutez une transformation :
```xml
<bind to="content" column="Contenu" eval="__config__(com.example.csv.CSVEscapeUtil).escapeHTMLContent(:Contenu)"/>
```

## 📋 Étapes de test

1. **Tester avec un seul enregistrement** d'abord
2. **Valider le HTML** avant l'import
3. **Vérifier l'encodage** du fichier (UTF-8)
4. **Utiliser l'utilitaire CSVEscapeUtil** pour automatiser l'échappement

## 🛠️ Utilisation de l'utilitaire

```java
CSVEscapeUtil util = new CSVEscapeUtil();

// Échapper un fichier complet
util.escapeCSVFile("mail_original.csv", "mail_escaped.csv");

// Générer un rapport de validation
util.generateValidationReport("mail_escaped.csv");
```

## ⚠️ Points d'attention

1. **Toujours échapper les guillemets** dans le contenu HTML
2. **Éviter les espaces** dans les attributs HTML
3. **Valider le HTML** avant l'import
4. **Tester avec des données réelles** contenant des caractères spéciaux

## 🎯 Résultat attendu

Après correction, votre import CSV devrait fonctionner sans erreur et vos templates de mail seront correctement importés dans Axelor.