package com.example.mail;

import com.axelor.text.Template;
import com.axelor.text.Templates;
import com.axelor.text.GroovyTemplates;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Exemple d'utilisation d'un template de mail avec échappement correct des guillemets
 * pour éviter les problèmes d'import de données.
 */
public class EmailTemplateExample {

    public static void main(String[] args) {
        EmailTemplateExample example = new EmailTemplateExample();
        example.sendConfirmationEmail();
    }

    /**
     * Envoie un email de confirmation avec un lien URL correctement échappé
     */
    public void sendConfirmationEmail() {
        try {
            // Lecture du template HTML
            String templateContent = loadTemplateFromFile("email_template_example.html");
            
            // Création du template avec GroovyTemplates (comme dans Axelor)
            Templates templates = new GroovyTemplates();
            Template template = templates.fromText(templateContent);
            
            // Préparation des données avec échappement approprié
            Map<String, Object> data = prepareEmailData();
            
            // Génération du contenu HTML
            String htmlContent = template.make(data).render();
            
            System.out.println("Contenu HTML généré :");
            System.out.println(htmlContent);
            
            // Ici vous pourriez envoyer l'email avec le contenu généré
            // sendEmail(htmlContent);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du template : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Prépare les données pour le template avec échappement approprié
     */
    private Map<String, Object> prepareEmailData() {
        Map<String, Object> data = new HashMap<>();
        
        // Données utilisateur de base
        data.put("firstName", "Jean");
        data.put("lastName", "Dupont");
        data.put("userId", "12345");
        data.put("userEmail", "jean.dupont@example.com");
        data.put("userName", "Jean Dupont");
        
        // Tokens et paramètres sécurisés
        data.put("token", "abc123def456ghi789");
        data.put("resetToken", "xyz789uvw456rst123");
        data.put("activationToken", "token123activation456");
        data.put("sessionId", "session789abc456def");
        
        // URLs et paramètres avec échappement approprié
        String baseUrl = "https://monapplication.com";
        data.put("baseUrl", baseUrl);
        
        // URL complète pour l'affichage en texte
        String fullUrl = baseUrl + "/confirm?token=" + data.get("token") + "&user=" + data.get("userId");
        data.put("fullUrl", fullUrl);
        
        // Paramètres additionnels
        data.put("expiryDate", "2024-12-31T23:59:59Z");
        data.put("redirectUrl", encodeUrl(baseUrl + "/dashboard"));
        data.put("language", "fr");
        
        return data;
    }

    /**
     * Encode une URL pour éviter les problèmes de caractères spéciaux
     */
    private String encodeUrl(String url) {
        try {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return url; // Retourne l'URL originale en cas d'erreur
        }
    }

    /**
     * Charge le contenu du template depuis un fichier
     * Dans un vrai projet, vous utiliseriez ResourceUtils.getResourceStream()
     */
    private String loadTemplateFromFile(String filename) {
        // Simulation du chargement du template
        // En réalité, vous utiliseriez :
        // InputStream stream = ResourceUtils.getResourceStream("templates/" + filename);
        // return IOUtils.toString(stream, StandardCharsets.UTF_8);
        
        return """
            <!DOCTYPE html>
            <html lang="fr">
            <head>
                <meta charset="UTF-8">
                <title>Confirmation</title>
            </head>
            <body style="font-family: Arial, sans-serif;">
                <h1>Bonjour ${firstName} ${lastName},</h1>
                <p>Voici votre lien de confirmation :</p>
                <a href="https://example.com/confirm?token=${token}&amp;user=${userId}" 
                   style="background-color: #3498db; color: white; padding: 12px 24px; text-decoration: none;">
                    Confirmer mon compte
                </a>
                <p>URL complète : ${fullUrl}</p>
            </body>
            </html>
            """;
    }

    /**
     * Exemple d'échappement manuel pour des cas spéciaux
     */
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

    /**
     * Exemple d'échappement pour les URLs avec paramètres
     */
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
}