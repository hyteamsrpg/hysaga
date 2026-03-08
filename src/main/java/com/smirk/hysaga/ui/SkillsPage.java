package com.smirk.hysaga.ui;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.smirk.hysaga.data.model.PlayerData;

import javax.annotation.Nonnull;

public class SkillsPage extends BasicCustomUIPage {

    private final PlayerData playerData;

    public SkillsPage(@Nonnull PlayerRef playerRef, @Nonnull PlayerData playerData) {
        super(playerRef, CustomPageLifetime.CanDismiss);
        this.playerData = playerData;
    }

    @Override
    public void build(@Nonnull UICommandBuilder cmd) {
        cmd.append("SkillsPage.ui");

        cmd.set("#Title.TextSpans", Message.raw("Skill Allocation"));
        cmd.set("#LevelInfo.TextSpans", Message.raw(
                "Level " + playerData.getLevel() + " | " + playerData.getExp() + " XP"));
        cmd.set("#AvailablePoints.TextSpans", Message.raw(
                "Available Points: " + playerData.getAvailablePoints()));

        cmd.set("#StrengthValue.TextSpans", Message.raw(String.valueOf(playerData.getStrength())));
        cmd.set("#DexterityValue.TextSpans", Message.raw(String.valueOf(playerData.getDexterity())));
        cmd.set("#AgilityValue.TextSpans", Message.raw(String.valueOf(playerData.getAgility())));
        cmd.set("#IntelligenceValue.TextSpans", Message.raw(String.valueOf(playerData.getIntelligence())));
        cmd.set("#DefenseValue.TextSpans", Message.raw(String.valueOf(playerData.getDefense())));

        String abilities = (playerData.getAbilities() != null && !playerData.getAbilities().isEmpty())
                ? String.join(", ", playerData.getAbilities())
                : "None";
        cmd.set("#AbilitiesList.TextSpans", Message.raw(abilities));
    }
}
