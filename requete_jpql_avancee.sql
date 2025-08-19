-- Option 1: Requête JPQL avec échappement basique (si supporté par votre base de données)
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
ORDER BY self.name ASC;

-- Option 2: Requête JPQL avec échappement plus complet
SELECT
  self.importId AS IDdimport,
  self.name AS Nom,
  self.metaModel.name AS Modele,
  self.subject AS Sujet,
  CONCAT('"', 
    REPLACE(
      REPLACE(
        REPLACE(
          REPLACE(self.content, '"', '&quot;'), 
          'href =', 'href='
        ),
        ' =', '='
      ),
      '&', '&amp;'
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
ORDER BY self.name ASC;

-- Option 3: Requête JPQL avec gestion des cas spéciaux
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
ORDER BY self.name ASC;