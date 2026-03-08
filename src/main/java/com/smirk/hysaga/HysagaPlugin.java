package com.smirk.hysaga;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;
import java.util.logging.Level;

/**
 * Main plugin class.
 *
 * TODO: Implement your plugin logic here.
 *
 * @author smirk
 * @version 0.0.1
 */
public class HysagaPlugin extends JavaPlugin {

    private static HysagaPlugin instance;

    /**
     * Constructor - Called when plugin is loaded.
     */
    public HysagaPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().at(Level.INFO).log("[TemplatePlugin] Plugin loaded!");
    }

    /**
     * Get plugin instance.
     */
    public static HysagaPlugin getInstance() {
        return instance;
    }

    /**
     * Called when plugin is set up.
     */
    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("[TemplatePlugin] Plugin setup!");

        // TODO: Initialize your plugin here
        // - Load configuration
        // - Register event listeners
        // - Register commands
        // - Start services
        registerEvents();
        registerCommands();
    }

    /**
     * Called when plugin is enabled.
     */
    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("[TemplatePlugin] Plugin enabled!");
    }

    /**
     * Called when plugin is disabled.
     */
    @Override
    public void shutdown() {
        getLogger().at(Level.INFO).log("[TemplatePlugin] Plugin disabled!");

        // TODO: Cleanup your plugin here
        // - Save data
        // - Stop services
        // - Close connections
    }

    /**
     * Register your commands here.
     */
    private void registerEvents() {

    }

    /**
     * Register your commands here.
     */
    private void registerCommands() {

    }

}
