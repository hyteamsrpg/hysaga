package com.smirk.hysaga.ui;

import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.BasicCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.smirk.hysaga.config.SkillsConfig;
import com.smirk.hysaga.data.model.PlayerData;

import javax.annotation.Nonnull;

public class SkillsPage extends BasicCustomUIPage {

    private static final int BAR_WIDTH_PX = 200;

    private final PlayerData playerData;
    private final SkillsConfig config;

    public SkillsPage(@Nonnull PlayerRef playerRef, @Nonnull PlayerData playerData, @Nonnull SkillsConfig config) {
        super(playerRef, CustomPageLifetime.CanDismiss);
        this.playerData = playerData;
        this.config = config;
    }

    @Override
    public void build(@Nonnull UICommandBuilder cmd) {
        cmd.append("SkillsPage.ui");

        int max = config.getMaxPointsPerSkill();

        cmd.set("#Title.TextSpans", Message.raw("Skill Allocation"));
        cmd.set("#LevelInfo.TextSpans", Message.raw(
                "Level " + playerData.getLevel() + " | " + playerData.getExp() + " XP"));
        cmd.set("#AvailablePoints.TextSpans", Message.raw(
                "Available Points: " + playerData.getAvailablePoints()));

        injectBar(cmd, "#StrengthTrack", playerData.getStrength(), max, "#e87461");
        cmd.set("#StrengthValue.TextSpans", Message.raw(String.valueOf(playerData.getStrength())));

        injectBar(cmd, "#DexterityTrack", playerData.getDexterity(), max, "#e8d44d");
        cmd.set("#DexterityValue.TextSpans", Message.raw(String.valueOf(playerData.getDexterity())));

        injectBar(cmd, "#AgilityTrack", playerData.getAgility(), max, "#5cb85c");
        cmd.set("#AgilityValue.TextSpans", Message.raw(String.valueOf(playerData.getAgility())));

        injectBar(cmd, "#IntelligenceTrack", playerData.getIntelligence(), max, "#5b9bd5");
        cmd.set("#IntelligenceValue.TextSpans", Message.raw(String.valueOf(playerData.getIntelligence())));

        injectBar(cmd, "#DefenseTrack", playerData.getDefense(), max, "#d4d4d4");
        cmd.set("#DefenseValue.TextSpans", Message.raw(String.valueOf(playerData.getDefense())));

        String abilities = (playerData.getAbilities() != null && !playerData.getAbilities().isEmpty())
                ? String.join(", ", playerData.getAbilities())
                : "None";
        cmd.set("#AbilitiesList.TextSpans", Message.raw(abilities));
    }

    private void injectBar(UICommandBuilder cmd, String trackSelector, int statValue, int maxValue, String color) {
        int clamped = Math.max(0, Math.min(statValue, maxValue));
        int fillWidth = (int) (BAR_WIDTH_PX * (clamped / (double) maxValue));
        if (fillWidth < 1 && clamped > 0) fillWidth = 1;

        cmd.appendInline(trackSelector,
                "Group { Anchor: (Width: " + fillWidth + ", Height: 12, Left: 0); Background: " + color + "; }");
    }
}
