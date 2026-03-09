package com.example.schoolservlet.utils.chatbot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Model class responsible for managing AI chatbot interactions.
 * Handles prompt building and HTTP requests to the Gemini AI API.
 * 
 * @author Eduardo
 * @version 1.0
 */
public class Model {
    
    private static final Logger logger = Logger.getLogger(Model.class.getName());
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    private static final String PROMPT_DELIMITER = "-{20}";
    private static final String CHARSET = "utf-8";
    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    private String aiModelApiKey = null;
    private int maxOutputTokens;
    private double temperature;
    private double topP;
    private String systemPromptPath;

    public Model(String aiModelApiKey, int maxOutputTokens, double temperature, double topP, String systemPromptPath) {
        this.aiModelApiKey = aiModelApiKey;
        this.maxOutputTokens = maxOutputTokens;
        this.temperature = temperature;
        this.topP = topP;
        this.systemPromptPath = systemPromptPath;
    }

    public String getAiModelApiKey() {
        return aiModelApiKey;
    }

    public void setAiModelApiKey(String aiModelApiKey) {
        this.aiModelApiKey = aiModelApiKey;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(int maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTopP() {
        return topP;
    }

    public void setTopP(double topP) {
        this.topP = topP;
    }

    public String getSystemPromptPath() {
        return systemPromptPath;
    }

    public void setSystemPromptPath(String systemPromptPath) {
        this.systemPromptPath = systemPromptPath;
    }

    /**
     * Builds a complete prompt JSON structure from user input and system instructions.
     * Reads the system prompt from a file and combines it with the user's input.
     * 
     * @param userInput the input text provided by the user
     * @return a JSON string representing the complete prompt structure
     * @throws JsonProcessingException if there's an error converting the prompt to JSON
     */
    public String buildPrompt(String userInput) throws JsonProcessingException {
        Prompt prompt = new Prompt();
        
        // Build system instruction from file
        List<Prompt.Part> systemPromptParts = loadSystemPromptParts();
        Prompt.SystemInstruction systemInstruction = new Prompt.SystemInstruction();
        systemInstruction.setParts(systemPromptParts);
        prompt.setSystemInstruction(systemInstruction);

        // Build user content
        Prompt.Content content = createUserContent(userInput);
        prompt.setContents(new ArrayList<>(List.of(content)));

        // Build generation configuration
        prompt.setGenerationConfig(generateConfig());

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(prompt);
    }

    /**
     * Loads system prompt parts from the system prompt file.
     * The file is expected to contain sections separated by delimiters.
     * 
     * @return a list of prompt parts containing the system instructions
     */
    private List<Prompt.Part> loadSystemPromptParts() {
        List<Prompt.Part> systemPromptParts = new ArrayList<>();
        StringBuilder systemPromptPart = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(systemPromptPath))) {
            String line;
            
            while ((line = br.readLine()) != null) {
                if (line.matches(PROMPT_DELIMITER)) {
                    addPromptPart(systemPromptParts, systemPromptPart.toString());
                    systemPromptPart = new StringBuilder();
                } else {
                    systemPromptPart.append(line).append("\n");
                }
            }
            
            // Add the last part if exists
            if (systemPromptPart.length() > 0) {
                addPromptPart(systemPromptParts, systemPromptPart.toString());
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load system prompt from file: " + systemPromptPath, e);
        }
        
        return systemPromptParts;
    }

    /**
     * Creates a prompt part from text and adds it to the list.
     * 
     * @param parts the list to add the part to
     * @param text the text content of the part
     */
    private void addPromptPart(List<Prompt.Part> parts, String text) {
        if (text != null && !text.trim().isEmpty()) {
            Prompt.Part promptPart = new Prompt.Part();
            promptPart.setText(text);
            parts.add(promptPart);
        }
    }

    /**
     * Creates user content structure from user input text.
     * 
     * @param userInput the input text from the user
     * @return a Content object containing the user's input
     */
    private Prompt.Content createUserContent(String userInput) {
        Prompt.Content content = new Prompt.Content();
        Prompt.Part userPromptPart = new Prompt.Part();
        userPromptPart.setText(userInput);
        content.setParts(new ArrayList<>(List.of(userPromptPart)));
        return content;
    }

    /**
     * Generates a configuration object for the prompt generation parameters.
     * This includes settings like temperature, topP, and max output tokens.
     *
     * @return a GenerationConfig object with predefined settings
     */
    private Prompt.GenerationConfig generateConfig() {
        Prompt.GenerationConfig config = new Prompt.GenerationConfig();
        config.setTemperature(temperature);
        config.setTopP(topP);
        config.setMaxOutputTokens(maxOutputTokens);
        return config;
    }

    /**
     * Sends an HTTP POST request to the Gemini AI API with the provided JSON payload.
     * 
     * @param json the JSON payload to send in the request body
     * @return the response from the API as a string
     * @throws IOException if there's an error during the HTTP request
     */
    public String request(String json) throws IOException {
        HttpURLConnection conn = null;
        try {
            conn = createConnection();


            // Send request
            sendRequest(conn, json);

            // Read response
            int status = conn.getResponseCode();
            String response = extractText(readResponse(conn, status));

            handleRateLimiting(status);

            return response;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during API request", e);
            throw new IOException("Failed to communicate with Gemini API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    private String extractText(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        var root = mapper.readTree(json);

        return root
                .path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();
    }

    /**
     * Creates and configures an HTTP connection to the Gemini API.
     * 
     * @return a configured HttpURLConnection instance
     * @throws IOException if there's an error creating the connection
     */
    private HttpURLConnection createConnection() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("X-goog-api-key", aiModelApiKey);
        conn.setDoOutput(true);
        return conn;
    }

    /**
     * Sends the JSON payload through the HTTP connection.
     * 
     * @param conn the HTTP connection to use
     * @param json the JSON payload to send
     * @throws IOException if there's an error writing the request
     */
    private void sendRequest(HttpURLConnection conn, String json) throws IOException {
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = json.getBytes(CHARSET);
            os.write(input, 0, input.length);
        }
    }

    /**
     * Reads the response from the HTTP connection.
     * 
     * @param conn the HTTP connection to read from
     * @param status the HTTP status code
     * @return the response body as a string
     * @throws IOException if there's an error reading the response
     */
    private String readResponse(HttpURLConnection conn, int status) throws IOException {
        BufferedReader br = (status >= 200 && status < 300)
                ? new BufferedReader(new InputStreamReader(conn.getInputStream(), CHARSET))
                : new BufferedReader(new InputStreamReader(conn.getErrorStream(), CHARSET));

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = br.readLine()) != null) {
            response.append(line.trim());
        }
        
        br.close();
        return response.toString();
    }

    /**
     * Handles rate limiting responses from the API.
     * 
     * @param status the HTTP status code to check
     */
    private void handleRateLimiting(int status) {
        if (status == HTTP_TOO_MANY_REQUESTS) {
            logger.warning("Rate limit exceeded for Gemini API");
        }
    }
}
