package com.smirk.hysaga.data;

import com.smirk.hysaga.data.model.PlayerData;
import com.smirk.hysaga.storage.JsonStorageManager;
import com.smirk.hysaga.storage.StoragePaths;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerDataManager {

    private static final Logger LOGGER = Logger.getLogger(PlayerDataManager.class.getName());

    private final JsonStorageManager storage;
    private final StoragePaths paths;

    private final ConcurrentHashMap<UUID, PlayerData> cache = new ConcurrentHashMap<>();

    public PlayerDataManager(StoragePaths paths) {
        this.storage = JsonStorageManager.getInstance();
        this.paths = paths;
    }

    /**
     * Load existing data or create a new profile for the player.
     * Called on player join.
     */
    public PlayerData loadOrCreate(UUID playerId, String username) {
        Path path = paths.getPlayerDataFile(playerId);

        try {
            PlayerData data = storage.read(path, PlayerData.class);

            if (data == null) {
                data = new PlayerData(playerId, username);
                LOGGER.info("Created new player profile for " + username + " (" + playerId + ")");
            } else {
                data.updateOnJoin(username);
            }

            storage.writeAtomic(path, data);
            cache.put(playerId, data);
            return data;

        } catch (IOException e) {
            throw new RuntimeException("Failed loading player data for " + playerId, e);
        }
    }

    @Nullable
    public PlayerData load(UUID playerId) {
        PlayerData cached = cache.get(playerId);
        if (cached != null) return cached;

        Path path = paths.getPlayerDataFile(playerId);
        try {
            PlayerData data = storage.read(path, PlayerData.class);
            if (data != null) {
                cache.put(playerId, data);
            }
            return data;
        } catch (IOException e) {
            throw new RuntimeException("Failed loading player data for " + playerId, e);
        }
    }

    public void save(@Nonnull PlayerData data) {
        Path path = paths.getPlayerDataFile(data.getPlayerId());
        try {
            storage.writeAtomic(path, data);
            cache.put(data.getPlayerId(), data);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save player data for " + data.getPlayerId(), e);
            throw new RuntimeException("Failed saving player data for " + data.getPlayerId(), e);
        }
    }

    /**
     * Get cached data for an online player. Returns null if not loaded.
     */
    @Nullable
    public PlayerData getCached(UUID playerId) {
        return cache.get(playerId);
    }

    /**
     * Remove player from cache (e.g. on disconnect).
     */
    public void unload(UUID playerId) {
        cache.remove(playerId);
    }

    public boolean exists(@Nonnull UUID playerId) {
        return java.nio.file.Files.exists(paths.getPlayerDataFile(playerId));
    }

    public StoragePaths getPaths() {
        return paths;
    }
}
