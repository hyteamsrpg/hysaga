package com.smirk.hysaga.ui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.smirk.hysaga.config.SkillsConfig;
import com.smirk.hysaga.data.PlayerDataManager;
import com.smirk.hysaga.data.model.PlayerClass;
import com.smirk.hysaga.data.model.PlayerData;

import javax.annotation.Nonnull;

public class SkillsPage extends InteractiveCustomUIPage<SkillsPage.EventInput> {

    private static final int BAR_WIDTH_PX = 200;
    private static final PlayerClass[] CLASSES = PlayerClass.values();

    private final PlayerData playerData;
    private final SkillsConfig config;
    private final PlayerDataManager playerDataManager;

    private enum Tab { SKILLS, CLASS }
    private Tab activeTab = Tab.SKILLS;
    private int selectedClassIndex = 0;

    public SkillsPage(@Nonnull PlayerRef playerRef, @Nonnull PlayerData playerData,
                       @Nonnull SkillsConfig config, @Nonnull PlayerDataManager playerDataManager) {
        super(playerRef, CustomPageLifetime.CanDismiss, EventInput.CODEC);
        this.playerData = playerData;
        this.config = config;
        this.playerDataManager = playerDataManager;
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder cmd,
                      @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        PlayerClass playerClass = playerData.getPlayerClassEnum();

        if (activeTab == Tab.SKILLS) {
            buildSkillsTab(cmd, playerClass);
            bindTabEvents(events);
        } else if (playerClass == null) {
            buildClassSelectTab(cmd);
            bindTabEvents(events);
            bindClassSelectEvents(events);
        } else {
            buildClassTreeTab(cmd, playerClass);
            bindTabEvents(events);
        }
    }

    private void buildSkillsTab(UICommandBuilder cmd, PlayerClass playerClass) {
        cmd.append("SkillsPage.ui");

        int max = config.getMaxPointsPerSkill();
        String ampStat = playerClass != null ? playerClass.getAmplifiedStat() : null;
        int ampPct = playerClass != null ? playerClass.getAmplifierPercent() : 0;

        cmd.set("#LevelInfo.TextSpans", Message.raw(
                "Level " + playerData.getLevel() + " | " + playerData.getExp() + " XP"));
        cmd.set("#AvailablePoints.TextSpans", Message.raw(
                "Available Points: " + playerData.getAvailablePoints()));

        int strBonus = playerClass != null ? playerClass.getStrengthBonus() : 0;
        int dexBonus = playerClass != null ? playerClass.getDexterityBonus() : 0;
        int agiBonus = playerClass != null ? playerClass.getAgilityBonus() : 0;
        int intBonus = playerClass != null ? playerClass.getIntelligenceBonus() : 0;
        int defBonus = playerClass != null ? playerClass.getDefenseBonus() : 0;

        boolean strAmp = "Strength".equals(ampStat);
        boolean dexAmp = "Dexterity".equals(ampStat);
        boolean agiAmp = "Agility".equals(ampStat);
        boolean intAmp = "Intelligence".equals(ampStat);
        boolean defAmp = "Defense".equals(ampStat);

        injectBar(cmd, "#StrengthTrack", playerData.getStrength() + strBonus, max, "#e87461");
        cmd.set("#StrengthValue.TextSpans", Message.raw(formatStat(playerData.getStrength(), strBonus, strAmp, ampPct)));

        injectBar(cmd, "#DexterityTrack", playerData.getDexterity() + dexBonus, max, "#e8d44d");
        cmd.set("#DexterityValue.TextSpans", Message.raw(formatStat(playerData.getDexterity(), dexBonus, dexAmp, ampPct)));

        injectBar(cmd, "#AgilityTrack", playerData.getAgility() + agiBonus, max, "#5cb85c");
        cmd.set("#AgilityValue.TextSpans", Message.raw(formatStat(playerData.getAgility(), agiBonus, agiAmp, ampPct)));

        injectBar(cmd, "#IntelligenceTrack", playerData.getIntelligence() + intBonus, max, "#5b9bd5");
        cmd.set("#IntelligenceValue.TextSpans", Message.raw(formatStat(playerData.getIntelligence(), intBonus, intAmp, ampPct)));

        injectBar(cmd, "#DefenseTrack", playerData.getDefense() + defBonus, max, "#d4d4d4");
        cmd.set("#DefenseValue.TextSpans", Message.raw(formatStat(playerData.getDefense(), defBonus, defAmp, ampPct)));

        if (playerClass != null) {
            cmd.set("#ClassBonus.TextSpans", Message.raw(
                    playerClass.getAmplifiedStat() + " is " + playerClass.getAmplifierPercent() + "% more effective (" + playerClass.getDisplayName() + ")"));
        }
    }

    private void buildClassSelectTab(UICommandBuilder cmd) {
        cmd.append("ClassSelectPage.ui");

        PlayerClass preview = CLASSES[selectedClassIndex];
        cmd.appendInline("#ClassIcon",
                "Group { Anchor: (Width: 160, Height: 160); Background: PatchStyle(TexturePath: \""
                + preview.getIconTexture() + "\"); }");
        cmd.set("#ClassName.TextSpans", Message.raw(preview.getDisplayName()));
        cmd.set("#ClassDescription.TextSpans", Message.raw(preview.getDescription()));
        cmd.set("#ClassBaseStats.TextSpans", Message.raw(preview.getBaseStatsDisplay()));
        cmd.set("#ClassPassive.TextSpans", Message.raw(
                "+" + preview.getPassivePercent() + "% " + preview.getPassiveName()));
        cmd.set("#ClassAmplifier.TextSpans", Message.raw(
                preview.getAmplifiedStat() + " is " + preview.getAmplifierPercent() + "% more effective"));
    }

    private void buildClassTreeTab(UICommandBuilder cmd, PlayerClass playerClass) {
        cmd.append("ClassTreePage.ui");
        cmd.set("#CurrentClassName.TextSpans", Message.raw(playerClass.getDisplayName()));
    }

    private void bindTabEvents(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SkillsTabBtn",
                EventData.of("Action", "skillsTab"),
                false
        );
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ClassTabBtn",
                EventData.of("Action", "classTab"),
                false
        );
    }

    private void bindClassSelectEvents(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#PrevClassBtn",
                EventData.of("Action", "prevClass"),
                false
        );
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#NextClassBtn",
                EventData.of("Action", "nextClass"),
                false
        );
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#ConfirmClassBtn",
                EventData.of("Action", "confirm"),
                false
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, EventInput data) {
        super.handleDataEvent(ref, store, data);

        if (data.action == null) return;

        switch (data.action) {
            case "skillsTab":
                activeTab = Tab.SKILLS;
                break;
            case "classTab":
                activeTab = Tab.CLASS;
                break;
            case "prevClass":
                selectedClassIndex = (selectedClassIndex + CLASSES.length - 1) % CLASSES.length;
                break;
            case "nextClass":
                selectedClassIndex = (selectedClassIndex + 1) % CLASSES.length;
                break;
            case "confirm":
                if (playerData.getPlayerClassEnum() == null) {
                    PlayerClass selected = CLASSES[selectedClassIndex];
                    playerData.setPlayerClass(selected.name());
                    playerDataManager.save(playerData);
                }
                break;
        }

        rebuild();
    }

    private String formatStat(int allocated, int bonus, boolean amplified, int ampPct) {
        StringBuilder sb = new StringBuilder();
        sb.append(allocated);
        if (bonus > 0) {
            sb.append(" (+").append(bonus).append(")");
        }
        if (amplified) {
            double multiplier = 1.0 + ampPct / 100.0;
            if (multiplier == (int) multiplier) {
                sb.append(" x").append((int) multiplier);
            } else {
                sb.append(" x").append(String.format("%.2g", multiplier));
            }
        }
        return sb.toString();
    }

    private void injectBar(UICommandBuilder cmd, String trackSelector, int total, int maxValue, String color) {
        int clamped = Math.max(0, Math.min(total, maxValue));
        int fillWidth = (int) (BAR_WIDTH_PX * (clamped / (double) maxValue));
        if (fillWidth < 1 && clamped > 0) fillWidth = 1;

        cmd.appendInline(trackSelector,
                "Group { Anchor: (Width: " + fillWidth + ", Height: 12, Left: 0); Background: " + color + "; }");
    }

    public static class EventInput {
        public static final BuilderCodec<EventInput> CODEC = BuilderCodec.builder(EventInput.class, EventInput::new)
                .append(new KeyedCodec<>("Action", Codec.STRING), (d, v) -> d.action = v, d -> d.action).add()
                .build();

        private String action;
    }
}
