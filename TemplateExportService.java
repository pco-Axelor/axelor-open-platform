package com.axelor.message.service;

import com.axelor.db.JPA;
import com.axelor.db.Query;
import com.axelor.message.db.Template;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.persistence.Tuple;

/**
 * Service pour exporter les templates de mail avec échappement approprié des guillemets
 */
public class TemplateExportService {

    /**
     * Exporte les templates vers un fichier CSV avec échappement des guillemets
     */
    public void exportTemplatesToCSV(String outputFile) throws IOException {
        
        // Requête JPQL pour récupérer les données
        String jpql = """
            SELECT
              self.importId AS IDdimport,
              self.name AS Nom,
              self.metaModel.name AS Modele,
              self.subject AS Sujet,
              self.content AS Contenu,
              self.addSignature AS Ajouterunesignature,
              self.toRecipients AS A, 
              self.ccRecipients AS Cc, 
              self.bccRecipients AS Cci, 
              self.isJson AS Json, 
              self.isDefault AS Defaut
            FROM Template AS self
            ORDER BY self.name ASC
            """;
        
        Query<Tuple> query = JPA.em().createQuery(jpql, Tuple.class);
        List<Tuple> results = query.getResultList();
        
        // Écrire le fichier CSV
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            
            // En-tête
            writer.write("IDdimport;Nom;Modele;Sujet;Contenu;Ajouterunesignature;A;Cc;Cci;Json;Defaut");
            writer.newLine();
            
            // Données
            for (Tuple tuple : results) {
                String line = buildCSVLine(tuple);
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Construit une ligne CSV avec échappement approprié
     */
    private String buildCSVLine(Tuple tuple) {
        StringBuilder line = new StringBuilder();
        
        // IDdimport
        line.append(getStringValue(tuple.get("IDdimport"))).append(";");
        
        // Nom
        line.append(getStringValue(tuple.get("Nom"))).append(";");
        
        // Modele
        line.append(getStringValue(tuple.get("Modele"))).append(";");
        
        // Sujet
        line.append(getStringValue(tuple.get("Sujet"))).append(";");
        
        // Contenu (avec échappement spécial)
        String content = getStringValue(tuple.get("Contenu"));
        String escapedContent = escapeHTMLContent(content);
        line.append(escapedContent).append(";");
        
        // Ajouterunesignature
        line.append(getBooleanValue(tuple.get("Ajouterunesignature"))).append(";");
        
        // A
        line.append(getStringValue(tuple.get("A"))).append(";");
        
        // Cc
        line.append(getStringValue(tuple.get("Cc"))).append(";");
        
        // Cci
        line.append(getStringValue(tuple.get("Cci"))).append(";");
        
        // Json
        line.append(getBooleanValue(tuple.get("Json"))).append(";");
        
        // Defaut
        line.append(getBooleanValue(tuple.get("Defaut")));
        
        return line.toString();
    }

    /**
     * Échappe le contenu HTML pour le CSV
     */
    private String escapeHTMLContent(String content) {
        if (content == null || content.isEmpty()) {
            return "\"\"";
        }
        
        // Échapper les guillemets dans le HTML
        String escaped = content
            .replace("&quot;", "___QUOT_ENTITY___") // Sauvegarder les entités existantes
            .replace("\"", "&quot;") // Échapper les guillemets
            .replace("___QUOT_ENTITY___", "&quot;"); // Restaurer les entités
        
        // Corriger les espaces problématiques dans les attributs HTML
        escaped = escaped.replaceAll("(\\w+)\\s*=\\s*\"", "$1=\"");
        
        // Encapsuler dans des guillemets pour le CSV
        return "\"" + escaped + "\"";
    }

    /**
     * Obtient une valeur String avec gestion des nulls
     */
    private String getStringValue(Object value) {
        return value != null ? value.toString() : "";
    }

    /**
     * Obtient une valeur Boolean avec gestion des nulls
     */
    private String getBooleanValue(Object value) {
        if (value == null) {
            return "false";
        }
        return value.toString().toLowerCase();
    }

    /**
     * Exporte un template spécifique pour test
     */
    public void exportSingleTemplate(String templateName, String outputFile) throws IOException {
        String jpql = """
            SELECT
              self.importId AS IDdimport,
              self.name AS Nom,
              self.metaModel.name AS Modele,
              self.subject AS Sujet,
              self.content AS Contenu,
              self.addSignature AS Ajouterunesignature,
              self.toRecipients AS A, 
              self.ccRecipients AS Cc, 
              self.bccRecipients AS Cci, 
              self.isJson AS Json, 
              self.isDefault AS Defaut
            FROM Template AS self
            WHERE self.name = :templateName
            """;
        
        Query<Tuple> query = JPA.em().createQuery(jpql, Tuple.class);
        query.setParameter("templateName", templateName);
        Tuple result = query.getSingleResult();
        
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            
            // En-tête
            writer.write("IDdimport;Nom;Modele;Sujet;Contenu;Ajouterunesignature;A;Cc;Cci;Json;Defaut");
            writer.newLine();
            
            // Données
            String line = buildCSVLine(result);
            writer.write(line);
            writer.newLine();
        }
    }

    /**
     * Valide le contenu HTML d'un template
     */
    public boolean validateTemplateContent(String content) {
        if (content == null) {
            return true;
        }
        
        // Vérifier les guillemets non échappés
        if (content.contains("\"") && !content.contains("&quot;")) {
            return false;
        }
        
        // Vérifier les espaces problématiques dans les attributs
        if (content.matches(".*\\w+\\s*=\\s*\".*")) {
            return false;
        }
        
        return true;
    }

    /**
     * Génère un rapport de validation pour tous les templates
     */
    public void generateValidationReport() {
        String jpql = "SELECT self.name, self.content FROM Template AS self ORDER BY self.name ASC";
        Query<Tuple> query = JPA.em().createQuery(jpql, Tuple.class);
        List<Tuple> results = query.getResultList();
        
        System.out.println("=== RAPPORT DE VALIDATION DES TEMPLATES ===");
        
        for (Tuple tuple : results) {
            String name = getStringValue(tuple.get(0));
            String content = getStringValue(tuple.get(1));
            
            System.out.println("Template: " + name);
            
            if (validateTemplateContent(content)) {
                System.out.println("  ✅ Contenu valide");
            } else {
                System.out.println("  ❌ Contenu problématique détecté");
                System.out.println("     - Guillemets non échappés ou espaces dans attributs HTML");
            }
            
            System.out.println();
        }
    }
}