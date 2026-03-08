package com.smirk.hysaga;

import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.smirk.hysaga.commands.ShowHudCommand;
import com.smirk.hysaga.commands.ShowPageCommand;
import com.smirk.hysaga.commands.SkillsCommand;
import com.smirk.hysaga.config.SkillsConfig;
import com.smirk.hysaga.data.PlayerDataManager;
import com.smirk.hysaga.data.model.PlayerData;
import com.smirk.hysaga.storage.JsonStorageManager;
import com.smirk.hysaga.storage.StoragePaths;

import javax.annotation.Nonnull;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author smirk
 * @version 0.0.1
 */
public class Hysaga extends JavaPlugin {

    private static Hysaga instance;

    private StoragePaths storagePaths;
    private PlayerDataManager playerDataManager;
    private SkillsConfig skillsConfig;

    public Hysaga(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        getLogger().at(Level.INFO).log("[Hysaga] Plugin loaded!");
    }

    public static Hysaga getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        getLogger().at(Level.INFO).log("[Hysaga] Plugin setup!");

        Path dataDir = Path.of("plugins", "hysaga");
        this.storagePaths = new StoragePaths(dataDir);
        this.playerDataManager = new PlayerDataManager(storagePaths);
        this.skillsConfig = loadSkillsConfig();

        registerEvents();
        registerCommands();
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("[Hysaga] Plugin enabled!");
    }

    @Override
    public void shutdown() {
        getLogger().at(Level.INFO).log("[Hysaga] Plugin disabled!");
    }

    private void registerEvents() {
        this.getEventRegistry().register(PlayerConnectEvent.class, event -> {
            PlayerRef playerRef = event.getPlayerRef();
            UUID playerId = playerRef.getUuid();
            String username = playerRef.getUsername();

            boolean isNew = !playerDataManager.exists(playerId);
            PlayerData data = playerDataManager.loadOrCreate(playerId, username);

            if (isNew) {
                getLogger().at(Level.INFO).log("[Hysaga] New player profile created for " + username);
            } else {
                getLogger().at(Level.INFO).log("[Hysaga] Loaded profile for " + username
                        + " (Lv." + data.getLevel() + ")");
            }
        });

        this.getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
            PlayerRef playerRef = event.getPlayerRef();
            UUID playerId = playerRef.getUuid();

            PlayerData data = playerDataManager.getCached(playerId);
            if (data != null) {
                playerDataManager.save(data);
            }
            playerDataManager.unload(playerId);
        });
    }

    private void registerCommands() {
        this.getCommandRegistry().registerCommand(
                new SkillsCommand(this.getName(), this.getManifest().getVersion().toString(), playerDataManager, skillsConfig));
        this.getCommandRegistry().registerCommand(new ShowPageCommand());
        this.getCommandRegistry().registerCommand(new ShowHudCommand());
    }

    private SkillsConfig loadSkillsConfig() {
        JsonStorageManager storage = JsonStorageManager.getInstance();
        try {
            SkillsConfig config = storage.read(storagePaths.getSkillsConfigFile(), SkillsConfig.class);
            if (config == null) {
                config = new SkillsConfig();
                storage.writeAtomic(storagePaths.getSkillsConfigFile(), config);
                getLogger().at(Level.INFO).log("[Hysaga] Created default skills config");
            }
            return config;
        } catch (Exception e) {
            getLogger().at(Level.WARNING).log("[Hysaga] Failed to load skills config, using defaults");
            return new SkillsConfig();
        }
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public SkillsConfig getSkillsConfig() {
        return skillsConfig;
    }
}
