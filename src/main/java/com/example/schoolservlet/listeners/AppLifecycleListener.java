package com.example.schoolservlet.listeners;

import com.example.schoolservlet.utils.PostgreConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Listener for the application's lifecycle events in a servlet context.
 *
 * <p>This class implements ServletContextListener to handle initialization
 * and destruction events. During context destruction, it ensures that the
 * PostgreSQL connection pool or resources are properly shut down.</p>
 *
 * @see ServletContextListener
 * @see PostgreConnection
 */
@WebListener
public class AppLifecycleListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) { PostgreConnection.start(); }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        PostgreConnection.shutdown();
    }
}
