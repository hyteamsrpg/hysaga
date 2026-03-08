package com.smirk.hysaga.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.smirk.hysaga.data.PlayerDataManager;
import com.smirk.hysaga.data.model.PlayerData;

import java.awt.*;
import java.util.UUID;

/**
 * Open Skills GUI
 */
public class SkillsCommand extends AbstractPlayerCommand {

    private final PlayerDataManager playerDataManager;

    public SkillsCommand(String pluginName, String pluginVersion, PlayerDataManager playerDataManager) {
        super("skills", "View your skills from " + pluginName + " version " + pluginVersion);
        this.playerDataManager = playerDataManager;
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

        ctx.sendMessage(Message.raw("--- Skills (Lv." + data.getLevel() + " | " + data.getExp() + " XP) ---").color(Color.YELLOW));
        ctx.sendMessage(Message.raw("  Strength:     " + data.getStrength()).color(Color.WHITE));
        ctx.sendMessage(Message.raw("  Dexterity:    " + data.getDexterity()).color(Color.WHITE));
        ctx.sendMessage(Message.raw("  Agility:      " + data.getAgility()).color(Color.WHITE));
        ctx.sendMessage(Message.raw("  Intelligence: " + data.getIntelligence()).color(Color.WHITE));

        if (data.getAbilities() != null && !data.getAbilities().isEmpty()) {
            ctx.sendMessage(Message.raw("  Abilities:    " + String.join(", ", data.getAbilities())).color(Color.CYAN));
        } else {
            ctx.sendMessage(Message.raw("  Abilities:    None").color(Color.GRAY));
        }
    }
}
