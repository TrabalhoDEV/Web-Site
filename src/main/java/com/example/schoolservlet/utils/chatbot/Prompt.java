package com.example.schoolservlet.utils.chatbot;

import java.util.List;

public class Prompt {
    private SystemInstruction systemInstruction;
    private List<Content> contents;
    private GenerationConfig generationConfig;

    public SystemInstruction getSystemInstruction() {
        return systemInstruction;
    }

    public void setSystemInstruction(SystemInstruction systemInstruction) {
        this.systemInstruction = systemInstruction;
    }

    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public GenerationConfig getGenerationConfig() {
        return generationConfig;
    }

    public void setGenerationConfig(GenerationConfig generationConfig) {
        this.generationConfig = generationConfig;
    }

    public static class SystemInstruction {
        private List<Part> parts;

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }

        @Override
        public String toString() {
            return "SystemInstruction{" +
                    "parts=" + parts +
                    '}';
        }
    }

    public static class Content {
        private List<Part> parts;

        public List<Part> getParts() {
            return parts;
        }

        public void setParts(List<Part> parts) {
            this.parts = parts;
        }

        @Override
        public String toString() {
            return "Content{" +
                    "parts=" + parts +
                    '}';
        }
    }

    public static class GenerationConfig {
        private double temperature;
        private double topP;
        private int maxOutputTokens;

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

        public double getMaxOutputTokens() {
            return maxOutputTokens;
        }

        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }
    }

    public static class Part {
        private String text;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return "Part{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Prompt{" +
                "systemInstruction=" + systemInstruction +
                ", contents=" + contents +
                '}';
    }
}
