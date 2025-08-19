# Guide d'export des templates avec échappement des guillemets

## 🎯 Problème identifié

Votre requête JPQL actuelle :
```sql
CONCAT('""', self.content, '""') AS Contenu
```

Ne gère pas l'échappement des guillemets **à l'intérieur** du contenu HTML, ce qui cause l'erreur CSV.

## ✅ Solutions recommandées

### Option 1: Requête JPQL corrigée (Recommandée)

```sql
SELECT
  self.importId AS IDdimport,
  self.name AS Nom,
  self.metaModel.name AS Modele,
  self.subject AS Sujet,
  CONCAT('"', 
    REPLACE(
      REPLACE(
        REPLACE(self.content, '"', '&quot;'), 
        'href =', 'href='
      ),
      ' =', '='
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

### Option 2: Service Java (Solution complète)

Utilisez le `TemplateExportService.java` que j'ai créé :

```java
TemplateExportService service = new TemplateExportService();

// Export complet
service.exportTemplatesToCSV("templates_export.csv");

// Export d'un template spécifique pour test
service.exportSingleTemplate("Notification en attente pour ASD", "test_template.csv");

// Validation des templates
service.generateValidationReport();
```

### Option 3: Requête JPQL avec gestion conditionnelle

```sql
SELECT
  self.importId AS IDdimport,
  self.name AS Nom,
  self.metaModel.name AS Modele,
  self.subject AS Sujet,
  CASE 
    WHEN self.content LIKE '%href =%' THEN 
      CONCAT('"', REPLACE(REPLACE(self.content, '"', '&quot;'), 'href =', 'href='), '"')
    WHEN self.content LIKE '%"%' THEN 
      CONCAT('"', REPLACE(self.content, '"', '&quot;'), '"')
    ELSE 
      CONCAT('"', self.content, '"')
  END AS Contenu,
  self.addSignature AS Ajouterunesignature,
  self.toRecipients AS A, 
  self.ccRecipients AS Cc, 
  self.bccRecipients AS Cci, 
  self.isJson AS Json, 
  self.isDefault AS Defaut
FROM Template AS self
ORDER BY self.name ASC
```

## 🔧 Corrections appliquées

### 1. Échappement des guillemets
```sql
REPLACE(self.content, '"', '&quot;')
```

### 2. Correction des espaces dans les attributs HTML
```sql
REPLACE(self.content, 'href =', 'href=')
REPLACE(self.content, ' =', '=')
```

### 3. Encapsulation correcte pour CSV
```sql
CONCAT('"', contenu_echappe, '"')
```

## 📋 Étapes de mise en œuvre

### Étape 1: Test avec un template problématique
```sql
-- Test avec le template qui pose problème
SELECT
  self.name AS Nom,
  CONCAT('"', 
    REPLACE(
      REPLACE(self.content, '"', '&quot;'), 
      'href =', 'href='
    ), 
    '"'
  ) AS Contenu
FROM Template AS self
WHERE self.name = 'Notification en attente pour ASD';
```

### Étape 2: Validation du résultat
Vérifiez que le contenu généré est :
```html
<a href="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

Et non :
```html
<a href ="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

### Étape 3: Export complet
Utilisez la requête corrigée pour l'export complet.

## 🛠️ Utilisation avec Axelor

### Dans l'interface Axelor
1. Allez dans **Outils > Requêtes**
2. Collez la requête JPQL corrigée
3. Exécutez et exportez en CSV

### Via l'API Axelor
```java
// Dans votre contrôleur ou service Axelor
@Inject
private TemplateExportService templateExportService;

public void exportTemplates() {
    try {
        templateExportService.exportTemplatesToCSV("templates_export.csv");
    } catch (IOException e) {
        // Gestion d'erreur
    }
}
```

## ⚠️ Points d'attention

1. **Testez d'abord** avec un seul template problématique
2. **Vérifiez l'encodage** du fichier exporté (UTF-8)
3. **Validez le HTML** généré avant l'import
4. **Sauvegardez** vos templates avant l'export

## 🎯 Résultat attendu

Après correction, votre export CSV devrait :
- ✅ Ne plus contenir d'erreurs de parsing
- ✅ Avoir des guillemets correctement échappés
- ✅ Être prêt pour l'import dans Axelor
- ✅ Maintenir la validité du HTML

## 🔍 Validation

Utilisez le service de validation pour vérifier vos templates :

```java
TemplateExportService service = new TemplateExportService();
service.generateValidationReport();
```

Cela vous donnera un rapport détaillé des templates problématiques.