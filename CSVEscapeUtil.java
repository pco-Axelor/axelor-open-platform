package com.example.csv;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Utilitaire pour échapper les guillemets dans les fichiers CSV contenant du HTML
 * Spécialement conçu pour les templates de mail Axelor
 */
public class CSVEscapeUtil {

    public static void main(String[] args) {
        CSVEscapeUtil util = new CSVEscapeUtil();
        
        // Exemple d'utilisation
        String inputFile = "mail_original.csv";
        String outputFile = "mail_escaped.csv";
        
        try {
            util.escapeCSVFile(inputFile, outputFile);
            System.out.println("Fichier CSV échappé créé avec succès : " + outputFile);
        } catch (IOException e) {
            System.err.println("Erreur lors du traitement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Échappe un fichier CSV contenant du HTML
     */
    public void escapeCSVFile(String inputFile, String outputFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8));
             BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8))) {
            
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    // Écrire l'en-tête sans modification
                    writer.write(line);
                    isFirstLine = false;
                } else {
                    // Échapper le contenu HTML dans la ligne
                    String escapedLine = escapeCSVLine(line);
                    writer.write(escapedLine);
                }
                writer.newLine();
            }
        }
    }

    /**
     * Échappe une ligne CSV contenant du HTML
     */
    public String escapeCSVLine(String line) {
        // Diviser la ligne en colonnes
        String[] columns = line.split(";", -1); // -1 pour garder les colonnes vides
        
        // Échapper la colonne "Contenu" (index 4)
        if (columns.length > 4) {
            columns[4] = escapeHTMLContent(columns[4]);
        }
        
        // Rejoindre les colonnes
        return String.join(";", columns);
    }

    /**
     * Échappe le contenu HTML en remplaçant les guillemets par des entités HTML
     */
    public String escapeHTMLContent(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        
        // Supprimer les guillemets externes du CSV si présents
        String htmlContent = content;
        if (htmlContent.startsWith("\"") && htmlContent.endsWith("\"")) {
            htmlContent = htmlContent.substring(1, htmlContent.length() - 1);
        }
        
        // Échapper les guillemets dans le HTML
        htmlContent = escapeQuotesInHTML(htmlContent);
        
        // Corriger les espaces problématiques dans les attributs HTML
        htmlContent = fixHTMLAttributes(htmlContent);
        
        // Remettre les guillemets externes pour le CSV
        return "\"" + htmlContent + "\"";
    }

    /**
     * Échappe les guillemets dans le contenu HTML
     */
    private String escapeQuotesInHTML(String html) {
        // Remplacer les guillemets doubles par des entités HTML
        // Mais préserver ceux qui sont déjà dans des entités HTML
        return html
            .replace("&quot;", "___QUOT_ENTITY___") // Sauvegarder les entités existantes
            .replace("\"", "&quot;") // Échapper les guillemets
            .replace("___QUOT_ENTITY___", "&quot;"); // Restaurer les entités
    }

    /**
     * Corrige les attributs HTML problématiques
     */
    private String fixHTMLAttributes(String html) {
        // Corriger les espaces entre les attributs et les valeurs
        // Exemple: href ="..." devient href="..."
        return html.replaceAll("(\\w+)\\s*=\\s*\"", "$1=\"");
    }

    /**
     * Valide le HTML échappé
     */
    public boolean validateEscapedHTML(String html) {
        // Vérifications basiques
        if (html == null) {
            return false;
        }
        
        // Vérifier que les guillemets sont correctement échappés
        if (html.contains("\"") && !html.contains("&quot;")) {
            return false;
        }
        
        // Vérifier que les balises HTML sont valides
        if (html.contains("<") && html.contains(">")) {
            // Vérifications supplémentaires pour les balises
            return validateHTMLTags(html);
        }
        
        return true;
    }

    /**
     * Valide les balises HTML
     */
    private boolean validateHTMLTags(String html) {
        // Vérifications basiques des balises
        String[] validTags = {"p", "br", "a", "div", "span", "strong", "em", "ul", "li"};
        
        for (String tag : validTags) {
            // Compter les balises ouvrantes et fermantes
            Pattern openPattern = Pattern.compile("<" + tag + "[^>]*>", Pattern.CASE_INSENSITIVE);
            Pattern closePattern = Pattern.compile("</" + tag + ">", Pattern.CASE_INSENSITIVE);
            
            long openCount = openPattern.matcher(html).results().count();
            long closeCount = closePattern.matcher(html).results().count();
            
            // Pour les balises auto-fermantes comme <br>, <img>, etc.
            if (tag.equals("br") || tag.equals("img")) {
                continue;
            }
            
            // Vérifier l'équilibre des balises
            if (openCount != closeCount) {
                System.err.println("Balise " + tag + " non équilibrée: " + openCount + " ouvertes, " + closeCount + " fermées");
                return false;
            }
        }
        
        return true;
    }

    /**
     * Génère un rapport de validation pour un fichier CSV
     */
    public void generateValidationReport(String csvFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {
            
            String line;
            int lineNumber = 0;
            
            System.out.println("=== RAPPORT DE VALIDATION CSV ===");
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                
                if (lineNumber == 1) {
                    // En-tête
                    continue;
                }
                
                String[] columns = line.split(";", -1);
                if (columns.length > 4) {
                    String content = columns[4];
                    
                    // Supprimer les guillemets externes
                    if (content.startsWith("\"") && content.endsWith("\"")) {
                        content = content.substring(1, content.length() - 1);
                    }
                    
                    System.out.println("Ligne " + lineNumber + " (" + columns[1] + "):");
                    
                    // Vérifier les guillemets non échappés
                    if (content.contains("\"") && !content.contains("&quot;")) {
                        System.out.println("  ❌ Guillemets non échappés détectés");
                    } else {
                        System.out.println("  ✅ Guillemets correctement échappés");
                    }
                    
                    // Vérifier les attributs HTML problématiques
                    if (content.matches(".*\\w+\\s*=\\s*\".*")) {
                        System.out.println("  ⚠️  Espaces dans les attributs HTML détectés");
                    }
                    
                    // Valider le HTML
                    if (validateEscapedHTML(content)) {
                        System.out.println("  ✅ HTML valide");
                    } else {
                        System.out.println("  ❌ HTML invalide");
                    }
                    
                    System.out.println();
                }
            }
        }
    }
}