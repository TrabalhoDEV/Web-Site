package com.example.schoolservlet.utils;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

/**
 * Provides methods for sending emails via the Brevo (formerly Sendinblue) API.
 *
 * <p>This service handles sending simple HTML emails or emails with file attachments.
 * The API URL, API key, and sender email are loaded from environment variables.
 *
 * <p>Static methods:
 * <ul>
 *   <li>{@link #sendEmail(String, String, String)} – sends an email without attachment</li>
 *   <li>{@link #sendEmail(String, String, String, String)} – sends an email with optional attachment</li>
 * </ul>
 *
 * <p>Throws exceptions if required environment variables are missing, the file does not exist,
 * or if the email fails to send.
 */
public class EmailService {
    private static final String BREVO_API_URL;
    private static final String BREVO_API_KEY;
    private static final String BREVO_EMAIL;

    /**
     * Static initializer block for the EmailService class.
     *
     * <p>Loads Brevo API configuration from environment variables using Dotenv.
     * Ensures that BREVO_API_URL, BREVO_API_KEY, and BREVO_EMAIL are all set.
     * If any required configuration is missing, throws a RuntimeException.
     */
    static{
        Dotenv dotenv = null;

        try{
            dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .load();
        } catch (Exception e){
            e.printStackTrace();
            dotenv = null;
        }

        BREVO_API_URL = ConfigService.getEnv("BREVO_API_URL", dotenv);
        BREVO_API_KEY = ConfigService.getEnv("BREVO_API_KEY", dotenv);
        BREVO_EMAIL = ConfigService.getEnv("BREVO_EMAIL", dotenv);

        if (BREVO_API_URL == null || BREVO_API_KEY == null ||BREVO_EMAIL == null){
            throw new RuntimeException("Brevo não está configurado");
        }
    }

    /**
     * Sends an HTML email to the specified destination without attachments.
     *
     * <p>This method delegates to {@link #sendEmail(String, String, String, String)} with a null filePath.
     *
     * @param destination the recipient's email address
     * @param topic       the subject of the email
     * @param messageHtml the HTML content of the email
     * @throws Exception if sending the email fails
     */
    public static void sendEmail(String destination, String topic, String messageHtml) throws Exception {
        sendEmail(destination, topic, messageHtml, null);
    }

    /**
     * Sends an HTML email to the specified destination with an optional attachment.
     *
     * <p>Constructs a JSON payload including sender, recipient, subject, HTML content,
     * and optional file attachment in Base64 format, then sends it via HTTP POST to Brevo API.
     *
     * @param destination the recipient's email address
     * @param topic       the subject of the email
     * @param messageHtml the HTML content of the email
     * @param filePath    the file path of an attachment, or null if no attachment
     * @throws Exception if the email cannot be sent, the file is missing, or an HTTP error occurs
     */
    public static void sendEmail(String destination, String topic, String messageHtml, String filePath) throws Exception {
        try {
            URL url = new URL(BREVO_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("api-key", BREVO_API_KEY);
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setDoOutput(true);

            // Create a json file:
            String fileJson = "";
            if (filePath != null && !filePath.isEmpty()) {
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new IOException("Arquivo não encontrado: " + filePath);
                }

                byte[] fileContent = Files.readAllBytes(file.toPath());
                String base64Content = Base64.getEncoder().encodeToString(fileContent);

                fileJson = String.format(
                        ",\"attachment\":[{\"name\":\"%s\",\"content\":\"%s\"}]",
                        file.getName(),
                        base64Content
                );
            }

            String html = messageHtml.replace("\"", "\\\"");

            //  Create a JSON with all information:
            String json =
                    "{"
                            + "\"sender\": {\"name\": \"Secreatária Vértice\", \"email\": \"" + BREVO_EMAIL + "\"},"
                            + "\"to\": [{\"email\": \"" + destination + "\"}],"
                            + "\"subject\": \"" + topic + "\","
                            + "\"htmlContent\": \"" + html + "\""
                            + fileJson
                            + "}";

            // Send JSON
            try (OutputStream os = connection.getOutputStream()) {
                os.write(json.getBytes("utf-8"));
            }

            // Read response:
            int status = connection.getResponseCode();
            if (!(status >= 200 && status < 300)) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) response.append(line);
                    throw new Exception("Erro ao enviar e-mail: " + response);
                }
            }
        } catch (Exception e) {
            throw new Exception("Não foi possível enviar e-mail para o aluno");
        }
    }
}
