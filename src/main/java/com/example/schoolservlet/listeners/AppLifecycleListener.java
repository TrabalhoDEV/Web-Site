package com.example.schoolservlet.listeners;

import com.example.schoolservlet.utils.PostgreConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppLifecycleListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) { PostgreConnection.start(); }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        PostgreConnection.shutdown();
    }
}
