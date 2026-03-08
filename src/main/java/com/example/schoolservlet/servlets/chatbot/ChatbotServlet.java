package com.example.schoolservlet.servlets.chatbot;

import com.example.schoolservlet.utils.ErrorHandler;
import com.example.schoolservlet.utils.chatbot.Model;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ChatbotServlet handles HTTP requests for the chatbot functionality.
 * Processes user input, validates parameters, and communicates with the AI model.
 *
 * Maps to the "/chatbot" URL pattern and supports both GET and POST requests.
 * GET requests display the chatbot interface, while POST requests process user prompts.
 *
 * @author Eduardo
 * @version 1.0
 */
@WebServlet("/chatbot")
public class ChatbotServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ChatbotServlet.class.getName());

    // AI Model configuration constants
    private static final int MAX_OUTPUT_TOKENS = 1000;
    private static final double TEMPERATURE = 0.7;
    private static final double TOP_P = 0.9;
    private static final String SYSTEM_PROMPT_RESOURCE = "/WEB-INF/resources/systemPrompt.txt";
    private static final String CHATBOT_JSP_PATH = "/pages/chatbot/chatbot.jsp";
    private static final String USER_PROMPT_PARAM = "userPrompt";
    private static final String RESPONSE_ATTRIBUTE = "response";

    // Error messages
    private static final String EMPTY_PROMPT_ERROR = "User prompt cannot be empty.";
    private static final String MISSING_API_KEY_ERROR = "AI model API key is not configured.";
    private static final String SYSTEM_PROMPT_NOT_FOUND_ERROR = "System prompt file not found.";
    private static final String AI_REQUEST_ERROR = "An error occurred while processing your request.";

    private String apiKey;

    /**
     * Initializes the servlet by loading the API key from environment variables.
     *
     * @throws ServletException if initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        try {
            this.apiKey = Dotenv.configure()
                    .ignoreIfMissing()
                    .load()
                    .get("AI_MODEL_API_KEY");

            if (apiKey == null || apiKey.trim().isEmpty()) {
                logger.warning("AI_MODEL_API_KEY environment variable is not set");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to load environment variables", e);
        }
    }

    /**
     * Handles POST requests containing user prompts.
     * Validates input, builds the prompt, communicates with the AI model,
     * and forwards the response back to the chatbot interface.
     *
     * @param request the HTTP request containing the user prompt
     * @param response the HTTP response to send back to the client
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        String userPrompt = request.getParameter(USER_PROMPT_PARAM);

        // Validate user prompt
        if (!validateUserPrompt(userPrompt, request, response)) {
            return;
        }

        // Validate API key is configured
        if (!validateApiKey(request, response)) {
            return;
        }

        // Get system prompt file path
        String systemPromptPath = getServletContext().getRealPath(SYSTEM_PROMPT_RESOURCE);

        if (systemPromptPath == null || systemPromptPath.trim().isEmpty()) {
            logger.severe("System prompt file path cannot be resolved");
            ErrorHandler.forward(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    SYSTEM_PROMPT_NOT_FOUND_ERROR, CHATBOT_JSP_PATH);
            return;
        }

        try {
            // Initialize AI model with configuration
            Model model = new Model(
                    apiKey,
                    MAX_OUTPUT_TOKENS,
                    TEMPERATURE,
                    TOP_P,
                    systemPromptPath
            );

            // Build and send prompt to AI model
            String prompt = model.buildPrompt(userPrompt);
            String aiResponse = model.request(prompt);

            // Set response attributes and forward to JSP
            request.setAttribute(RESPONSE_ATTRIBUTE, aiResponse);
            request.setAttribute(USER_PROMPT_PARAM, userPrompt);
            request.getRequestDispatcher(CHATBOT_JSP_PATH).forward(request, response);

        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Invalid prompt: " + e.getMessage(), e);
            ErrorHandler.forward(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid prompt format: " + e.getMessage(), CHATBOT_JSP_PATH);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "I/O error while communicating with AI model", e);
            ErrorHandler.forward(request, response, HttpServletResponse.SC_BAD_GATEWAY,
                    AI_REQUEST_ERROR, CHATBOT_JSP_PATH);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error processing chatbot request", e);
            ErrorHandler.forward(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    AI_REQUEST_ERROR, CHATBOT_JSP_PATH);
        }
    }

    /**
     * Handles GET requests by displaying the chatbot interface.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher(CHATBOT_JSP_PATH).forward(request, response);
    }

    /**
     * Validates that the user prompt is not null or empty.
     *
     * @param userPrompt the user's input prompt
     * @param request the HTTP request
     * @param response the HTTP response
     * @return true if the prompt is valid, false otherwise
     * @throws IOException if an I/O error occurs during error forwarding
     */
    private boolean validateUserPrompt(String userPrompt, HttpServletRequest request,
            HttpServletResponse response) throws IOException, ServletException {
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            logger.warning("Empty user prompt received");
            ErrorHandler.forward(request, response, HttpServletResponse.SC_BAD_REQUEST,
                    EMPTY_PROMPT_ERROR, CHATBOT_JSP_PATH);
            return false;
        }
        return true;
    }

    /**
     * Validates that the API key is configured and available.
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @return true if the API key is valid, false otherwise
     * @throws IOException if an I/O error occurs during error forwarding
     */
    private boolean validateApiKey(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.severe("API key is not configured");
            ErrorHandler.forward(request, response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    MISSING_API_KEY_ERROR, CHATBOT_JSP_PATH);
            return false;
        }
        return true;
    }
}
