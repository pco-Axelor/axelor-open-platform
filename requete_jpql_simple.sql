-- Requête JPQL simple avec échappement des guillemets (style Axelor)
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