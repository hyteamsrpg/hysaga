package com.smirk.hysaga.storage;

import java.nio.file.Path;
import java.util.UUID;

public record StoragePaths(Path pathDir) {

    public Path getBaseDir() {
        return this.pathDir;
    }

    public Path getPlayersDir() {
        return this.pathDir.resolve("players");
    }

    public Path getPlayerDataFile(UUID playerId) {
        return getPlayersDir().resolve(playerId.toString() + ".json");
    }
}
