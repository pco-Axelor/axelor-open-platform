# Solution simple - Échappement des guillemets (style Axelor)

## 🎯 Solution basée sur l'exemple Axelor

En regardant le fichier `MailParserTest.java` d'Axelor, on voit comment les liens URL sont gérés :

```java
private String html = "<a href=\"http://example.com/link.html\">Some Link</a>";
```

Les guillemets sont échappés avec des **backslashes** (`\"`) dans le code Java.

## ✅ Requête JPQL corrigée

### Version simple (recommandée)
```sql
SELECT
  self.importId AS IDdimport,
  self.name AS Nom,
  self.metaModel.name AS Modele,
  self.subject AS Sujet,
  CONCAT('"', REPLACE(self.content, '"', '\"'), '"') AS Contenu,
  self.addSignature AS Ajouterunesignature,
  self.toRecipients AS A, 
  self.ccRecipients AS Cc, 
  self.bccRecipients AS Cci, 
  self.isJson AS Json, 
  self.isDefault AS Defaut
FROM Template AS self
ORDER BY self.name ASC
```

### Version complète (avec correction des espaces)
```sql
SELECT
  self.importId AS IDdimport,
  self.name AS Nom,
  self.metaModel.name AS Modele,
  self.subject AS Sujet,
  CONCAT('"', 
    REPLACE(
      REPLACE(self.content, '"', '\"'), 
      'href =', 'href='
    ), 
    '"'
  ) AS Contenu,
  self.addSignature AS Ajouterunesignature,
  self.toRecipients AS A, 
  self.ccRecipients AS Cc, 
  self.bccRecipients AS Cci, 
  self.isJson AS Json, 
  self.isDefault AS Defaut
FROM Template AS self
ORDER BY self.name ASC
```

## 🔧 Ce que fait cette solution

### 1. Échappement des guillemets
```sql
REPLACE(self.content, '"', '\"')
```
Transforme :
```html
<a href="http://example.com">Lien</a>
```
En :
```html
<a href=\"http://example.com\">Lien</a>
```

### 2. Correction des espaces problématiques
```sql
REPLACE(self.content, 'href =', 'href=')
```
Transforme :
```html
<a href ="http://example.com">
```
En :
```html
<a href="http://example.com">
```

### 3. Encapsulation CSV
```sql
CONCAT('"', contenu_echappe, '"')
```
Encapsule le contenu dans des guillemets pour le CSV.

## 📋 Test de la solution

### Test avec le template problématique
```sql
SELECT 
  self.name AS Nom,
  CONCAT('"', 
    REPLACE(
      REPLACE(self.content, '"', '\"'), 
      'href =', 'href='
    ), 
    '"'
  ) AS Contenu
FROM Template AS self
WHERE self.name = 'Notification en attente pour ASD';
```

### Résultat attendu
Le contenu généré devrait être :
```csv
"<p>Vous avez une notification à traiter</p> <p> <a href=\"http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list\"> cliquez ici </a></p>"
```

## 🎯 Avantages de cette approche

1. **Simple** : Une seule fonction `REPLACE`
2. **Compatible** : Utilise la même approche qu'Axelor
3. **Efficace** : Pas besoin de code Java supplémentaire
4. **Fiable** : Fonctionne avec tous les parsers CSV

## ⚠️ Points d'attention

1. **Testez d'abord** avec un seul template
2. **Vérifiez l'encodage** du fichier exporté (UTF-8)
3. **Validez le résultat** avant l'import complet

## 🚀 Utilisation

1. Remplacez votre requête JPQL actuelle par la version corrigée
2. Exécutez la requête dans l'interface Axelor
3. Exportez le résultat en CSV
4. Utilisez ce fichier pour votre import

Cette solution devrait résoudre définitivement votre problème d'import CSV ! 🎉