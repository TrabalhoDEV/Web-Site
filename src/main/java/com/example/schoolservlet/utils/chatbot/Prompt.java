package com.example.schoolservlet.utils.chatbot;

import java.util.List;

public class Prompt {
    private SystemInstruction systemInstruction;
    private List<Content> contents;
    private GenerationConfig generationConfig;

    /**
     * Returns the system instruction associated with this prompt.
     *
     * @return the {@link SystemInstruction} object containing instructions for the AI system
     */
    public SystemInstruction getSystemInstruction() {
        return systemInstruction;
    }

    /**
     * Sets the system instruction for this prompt.
     *
     * @param systemInstruction the {@link SystemInstruction} object containing instructions for the AI system
     */
    public void setSystemInstruction(SystemInstruction systemInstruction) {
        this.systemInstruction = systemInstruction;
    }

    /**
     * Returns the list of content messages associated with this prompt.
     *
     * @return a {@link List} of {@link Content} objects representing the prompt's messages
     */
    public List<Content> getContents() {
        return contents;
    }

    /**
     * Sets the list of content messages for this prompt.
     *
     * @param contents a {@link List} of {@link Content} objects representing the prompt's messages
     */
    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    /**
     * Returns the generation configuration for this prompt.
     *
     * @return the {@link GenerationConfig} object containing parameters like temperature, top-p, and max output tokens
     */
    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }

    /**
     * Sets the generation configuration for this prompt.
     *
     * @param generationConfig the {@link GenerationConfig} object containing parameters like temperature, top-p, and max output tokens
     */
    public void setGenerationConfig(GenerationConfig generationConfig) {
        this.generationConfig = generationConfig;
    }

    /**
     * Represents system instructions for the prompt.
     *
     * <p>Holds multiple {@link Part} objects that make up the instruction text for the AI system.
     *
     * @see Part
     */
    public static class SystemInstruction {
        private List<Part> parts;

        /**
         * Returns the list of parts that make up this system instruction.
         *
         * @return a {@link List} of {@link Part} objects representing segments of the instruction
         */
        public List<Part> getParts() {
            return parts;
        }

        /**
         * Sets the list of parts for this system instruction.
         *
         * @param parts a {@link List} of {@link Part} objects representing segments of the instruction
         */
        public void setParts(List<Part> parts) {
            this.parts = parts;
        }

        /**
         * Returns a string representation of the system instruction.
         *
         * <p>Includes all {@link Part} objects contained in this instruction.
         *
         * @return a string representing the system instruction and its parts
         */
        @Override
        public String toString() {
            return "SystemInstruction{" +
                    "parts=" + parts +
                    '}';
        }
    }

    /**
     * Represents a content message within the prompt.
     *
     * <p>Contains a list of {@link Part} objects that make up the content's text segments.
     *
     * @see Part
     */
    public static class Content {
        private List<Part> parts;

        /**
         * Returns the list of parts for this content message.
         *
         * @return a {@link List} of {@link Part} objects representing the content segments
         */
        public List<Part> getParts() {
            return parts;
        }

        /**
         * Sets the list of parts for this content message.
         *
         * @param parts a {@link List} of {@link Part} objects representing the content segments
         */
        public void setParts(List<Part> parts) {
            this.parts = parts;
        }

        /**
         * Returns a string representation of the content object.
         *
         * <p>Includes all {@link Part} objects contained within this content.
         *
         * @return a string representing the content and its parts
         */
        @Override
        public String toString() {
            return "Content{" +
                    "parts=" + parts +
                    '}';
        }
    }

    /**
     * Configuration settings for text generation.
     *
     * <p>Includes parameters that influence the output of the AI model:
     * <ul>
     *   <li>temperature: controls randomness of the output</li>
     *   <li>topP: controls nucleus sampling probability</li>
     *   <li>maxOutputTokens: maximum number of tokens in the generated output</li>
     * </ul>
     */
    public static class GenerationConfig {
        private double temperature;
        private double topP;
        private int maxOutputTokens;

        /**
         * Returns the temperature setting for text generation.
         *
         * <p>The temperature controls the randomness of the AI's output. Higher values result in more diverse responses.
         *
         * @return the temperature as a double
         */
        public double getTemperature() {
            return temperature;
        }

        /**
         * Sets the temperature for text generation.
         *
         * <p>The temperature controls the randomness of the AI's output. Higher values produce more diverse responses.
         *
         * @param temperature the temperature value to set
         */
        public void setTemperature(double temperature) {
            this.temperature = temperature;
        }

        /**
         * Returns the top-p (nucleus sampling) value for text generation.
         *
         * <p>This parameter controls the cumulative probability threshold for token selection,
         * influencing the diversity of generated text.
         *
         * @return the top-p value as a double
         */
        public double getTopP() {
            return topP;
        }

        /**
         * Sets the top-p (nucleus sampling) value for text generation.
         *
         * <p>This parameter controls the cumulative probability threshold for token selection,
         * affecting the diversity and randomness of the generated output.
         *
         * @param topP the top-p value to set
         */
        public void setTopP(double topP) {
            this.topP = topP;
        }

        /**
         * Returns the maximum number of tokens allowed in the generated output.
         *
         * <p>This limits the length of the AI's response.
         *
         * @return the maximum output tokens as a double
         */
        public double getMaxOutputTokens() {
            return maxOutputTokens;
        }

        /**
         * Sets the maximum number of tokens for the generated output.
         *
         * <p>This limits the length of the AI's response.
         *
         * @param maxOutputTokens the maximum number of tokens to allow
         */
        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }
    }

    /**
     * Represents a single part of a prompt, system instruction, or content message.
     *
     * <p>Contains the text content for this segment.
     */
    public static class Part {
        private String text;

        /**
         * Returns the text of this part.
         *
         * @return the text content as a {@link String}
         */
        public String getText() {
            return text;
        }

        /**
         * Sets the text for this part.
         *
         * @param text the text content to set
         */
        public void setText(String text) {
            this.text = text;
        }

        /**
         * Returns a string representation of this part.
         *
         * <p>Includes the text content of the part.
         *
         * @return a string representing the part
         */
        @Override
        public String toString() {
            return "Part{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    /**
     * Returns a string representation of the prompt.
     *
     * <p>Includes the system instruction and the list of content messages.
     *
     * @return a string representing the prompt
     */
    @Override
    public String toString() {
        return "Prompt{" +
                "systemInstruction=" + systemInstruction +
                ", contents=" + contents +
                '}';
    }
}
