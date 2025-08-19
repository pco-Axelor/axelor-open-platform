# Guide d'échappement des guillemets dans les templates de mail HTML

## Problème

Lors de la création de templates de mail HTML avec des liens URL, les guillemets peuvent poser des problèmes lors de l'import de données ou de l'analyse du HTML. Voici les solutions recommandées.

## Solutions recommandées

### 1. Utilisation d'entités HTML pour les caractères spéciaux

**✅ Recommandé :**
```html
<a href="https://example.com/confirm?token=${token}&amp;user=${userId}">
    Confirmer mon compte
</a>
```

**❌ À éviter :**
```html
<a href="https://example.com/confirm?token=${token}&user=${userId}">
    Confirmer mon compte
</a>
```

### 2. Utilisation de guillemets simples pour les attributs

**✅ Recommandé :**
```html
<a href='https://example.com/reset?email=${userEmail}&amp;token=${resetToken}'>
    Réinitialiser mon mot de passe
</a>
```

### 3. Échappement des paramètres d'URL

**✅ Recommandé :**
```html
<a href="https://example.com/profile?name=${userName}&amp;action=view&amp;id=${userId}">
    Voir mon profil
</a>
```

### 4. Utilisation de variables prétraitées

**✅ Recommandé :**
```java
// Dans votre code Java
String safeUrl = buildSafeUrl(baseUrl, parameters);
data.put("safeUrl", safeUrl);
```

```html
<!-- Dans votre template -->
<a href="${safeUrl}">Accéder au lien</a>
```

## Méthodes d'échappement en Java

### 1. Échappement HTML de base

```java
public String escapeForHtml(String text) {
    if (text == null) {
        return "";
    }
    
    return text
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;");
}
```

### 2. Construction d'URLs sécurisées

```java
public String buildSafeUrl(String baseUrl, Map<String, String> parameters) {
    StringBuilder url = new StringBuilder(baseUrl);
    
    if (parameters != null && !parameters.isEmpty()) {
        url.append("?");
        boolean first = true;
        
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            if (!first) {
                url.append("&amp;");
            }
            url.append(escapeForHtml(param.getKey()))
               .append("=")
               .append(escapeForHtml(param.getValue()));
            first = false;
        }
    }
    
    return url.toString();
}
```

### 3. Encodage URL avec URLEncoder

```java
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public String encodeUrl(String url) {
    try {
        return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
    } catch (Exception e) {
        return url;
    }
}
```

## Exemples pratiques avec Axelor

### Template Groovy avec échappement

```groovy
// Dans votre template Groovy
def safeUrl = baseUrl + "/confirm?token=" + token + "&amp;user=" + userId
```

```html
<a href="${safeUrl}">Confirmer mon compte</a>
```

### Utilisation avec GroovyTemplates

```java
Templates templates = new GroovyTemplates();
Template template = templates.fromText(templateContent);

Map<String, Object> data = new HashMap<>();
data.put("baseUrl", "https://example.com");
data.put("token", "abc123");
data.put("userId", "12345");

String htmlContent = template.make(data).render();
```

## Bonnes pratiques générales

### 1. Toujours échapper les caractères spéciaux dans les URLs

- `&` → `&amp;`
- `<` → `&lt;`
- `>` → `&gt;`
- `"` → `&quot;`
- `'` → `&#39;`

### 2. Utiliser des variables prétraitées

Plutôt que d'échapper dans le template, préparez les données dans votre code Java.

### 3. Tester avec des données réelles

Vérifiez que vos templates fonctionnent avec des données contenant des caractères spéciaux.

### 4. Utiliser des outils de validation HTML

Validez vos templates avec des outils comme W3C Validator.

## Exemples de cas problématiques

### ❌ Problématique : Données non échappées

```html
<a href="https://example.com/search?q=${searchTerm}">
    Rechercher
</a>
```

Si `searchTerm` contient `"test"`, cela génère :
```html
<a href="https://example.com/search?q="test"">
    Rechercher
</a>
```

### ✅ Solution : Données échappées

```html
<a href="https://example.com/search?q=${searchTermEncoded}">
    Rechercher
</a>
```

Avec `searchTermEncoded` = `&quot;test&quot;`

## Validation et tests

### Test avec des caractères spéciaux

```java
@Test
public void testTemplateWithSpecialCharacters() {
    Map<String, Object> data = new HashMap<>();
    data.put("searchTerm", "test\"with\"quotes");
    data.put("searchTermEncoded", escapeForHtml("test\"with\"quotes"));
    
    // Test du template
    String result = template.make(data).render();
    
    // Vérifier que le HTML est valide
    assertTrue(result.contains("&quot;"));
    assertFalse(result.contains("\""));
}
```

## Conclusion

L'échappement correct des guillemets dans les templates de mail HTML est essentiel pour :

1. **Éviter les erreurs d'import de données**
2. **Assurer la validité du HTML généré**
3. **Prévenir les problèmes de sécurité**
4. **Garantir la compatibilité avec les clients mail**

Utilisez toujours les entités HTML appropriées et préparez vos données dans le code Java plutôt que dans le template.