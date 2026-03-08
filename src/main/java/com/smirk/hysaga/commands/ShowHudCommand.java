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
import com.smirk.hysaga.ui.TestHUD;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ShowHudCommand extends AbstractPlayerCommand {

    public ShowHudCommand() {
        super("testhud", "Shows a test HUD overlay");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Override
    protected void execute(@Nonnull CommandContext ctx, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = ctx.senderAs(Player.class);

        CompletableFuture.runAsync(() -> {
            player.getHudManager().setCustomHud(playerRef, new TestHUD(playerRef));
            playerRef.sendMessage(Message.raw("Test HUD shown."));
        }, world);
    }
}
