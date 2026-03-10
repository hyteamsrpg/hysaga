package com.smirk.hysaga.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.smirk.hysaga.config.SkillsConfig;
import com.smirk.hysaga.data.PlayerDataManager;
import com.smirk.hysaga.data.model.PlayerData;
import com.smirk.hysaga.ui.SkillsPage;

import java.awt.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkillsCommand extends AbstractPlayerCommand {

    private final PlayerDataManager playerDataManager;
    private final SkillsConfig skillsConfig;

    public SkillsCommand(String pluginName, String pluginVersion, PlayerDataManager playerDataManager, SkillsConfig skillsConfig) {
        super("skills", "Open skill allocation page from " + pluginName + " version " + pluginVersion);
        this.playerDataManager = playerDataManager;
        this.skillsConfig = skillsConfig;
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store, Ref<EntityStore> ref,
                           PlayerRef playerRef, World world) {
        UUID playerId = playerRef.getUuid();
        PlayerData data = playerDataManager.getCached(playerId);

        if (data == null) {
            data = playerDataManager.load(playerId);
        }

        if (data == null) {
            ctx.sendMessage(Message.raw("No profile found.").color(Color.RED));
            return;
        }

        Player player = ctx.senderAs(Player.class);
        PlayerData finalData = data;

        CompletableFuture.runAsync(() -> {
            player.getPageManager().openCustomPage(ref, store, new SkillsPage(playerRef, finalData, skillsConfig, playerDataManager));
        }, world);
    }
}
