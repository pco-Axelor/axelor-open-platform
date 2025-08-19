# Analyse du problème CSV - Échappement des guillemets

## Problème identifié

L'erreur `Invalid character between encapsulated token and delimiter at line: 5, position: 1,775` indique que le parser CSV rencontre des guillemets non échappés dans le contenu HTML.

## Analyse ligne par ligne

### Ligne 1 (Changed password)
```csv
null;Changed password;User;Changed password;"<p>Dear $User.name$,<br> Your new password is: $transientPassword$<br> It was changed on $User.updatedOn$.</p> <p>Best regards<br>MTCR Team</p>";false;$User.email$;null;null;false;true
```
✅ **OK** - Pas de guillemets problématiques

### Ligne 2 (Confirmation coordonnées)
```csv
null;Confirmation coordonnées;Partner;Mise à jour de vos coordonnées;"<p>Madame, Monsieur,<br/> Nous sommes actuellement en phase de mise à jour de notre répertoire de contacts.<br/>  A ce jour voici les informations dont nous disposons : </p>   <p>Téléphone fixe : $Partner.fixedPhone$<br/> Mobile : $Partner.mobilePhone$<br/> Email : $Partner.emailAddress.address$<br/> Adresse : <br/> $Partner.mainInvoicingAddress.fullName$<br/></p> <p>Si parmi les coordonnées ci-dessus, certaines sont erronées ou manquantes, merci de nous le faire savoir en répondant directement à cet email.</p> <p>Cordialement<br/> L'équipe Axelor</p> ";false;$Partner.emailAddress.address$;null;null;false;true
```
✅ **OK** - Pas de guillemets problématiques

### Ligne 3 (Contact details confirmation)
```csv
null;Contact details confirmation;Partner;Contact details update;"<p>Dear $Partner.firstName$,<br/> We are currently proceeding to an update of  our contact database. Can you please confirm the following information about you are up to date :  </p>   <p>Fixed phone : $Partner.fixedPhone$<br/> Mobile : $Partner.mobilePhone$<br/> Email : $Partner.emailAddress.address$<br/> Address : <br/> $Partner.mainInvoicingAddress.fullName$</p> <p>If among those information upwards, some are biased, please do inform us by reply thie email.</p> <p>Best regards<br/> Axelor Team</p>";false;$Partner.emailAddress.address$;null;null;false;true
```
✅ **OK** - Pas de guillemets problématiques

### Ligne 4 (Notification en attente pour ASD) - PROBLÉMATIQUE
```csv
null;Notification en attente pour ASD;EpocMain;MTCR : nouvelle notification a traiter;"<p>Vous avez une notification à traiter</p> <p> <a href ="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list"> cliquez ici </a>";false;julien.leger@comptes.eval.mae;null;null;false;false
```

❌ **PROBLÈME** : Dans cette ligne, il y a un espace entre `href` et `=` dans :
```html
<a href ="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

## Solutions

### 1. Correction immédiate du HTML
Remplacer :
```html
<a href ="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```
Par :
```html
<a href="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

### 2. Échappement des guillemets dans le CSV
Pour éviter les problèmes futurs, échapper les guillemets dans le contenu HTML :

```html
<a href="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

### 3. Utilisation d'entités HTML
```html
<a href="http://10.104.17.239:8080/axelor/#/ds/action.epoc.main.view.validation.notif/list">
```

## Recommandations

1. **Corriger l'espace** entre `href` et `=`
2. **Valider le HTML** avant l'import
3. **Utiliser des outils d'échappement** pour le contenu HTML
4. **Tester l'import** avec un seul enregistrement d'abord