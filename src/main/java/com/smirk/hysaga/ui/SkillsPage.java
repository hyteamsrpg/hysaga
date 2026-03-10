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
        } else if (playerClass == null) {
            buildClassSelectTab(cmd);
        } else {
            buildClassTreeTab(cmd, playerClass);
        }

        bindTabEvents(events);

        if (activeTab == Tab.CLASS && playerClass == null) {
            bindClassSelectEvents(events);
        }
    }

    private void buildSkillsTab(UICommandBuilder cmd, PlayerClass playerClass) {
        cmd.append("SkillsPage.ui");

        int max = config.getMaxPointsPerSkill();

        cmd.set("#LevelInfo.TextSpans", Message.raw(
                "Level " + playerData.getLevel() + " | " + playerData.getExp() + " XP"));
        cmd.set("#AvailablePoints.TextSpans", Message.raw(
                "Available Points: " + playerData.getAvailablePoints()));

        int strBonus = playerClass != null ? playerClass.getStrengthBonus() : 0;
        int dexBonus = playerClass != null ? playerClass.getDexterityBonus() : 0;
        int agiBonus = playerClass != null ? playerClass.getAgilityBonus() : 0;
        int intBonus = playerClass != null ? playerClass.getIntelligenceBonus() : 0;
        int defBonus = playerClass != null ? playerClass.getDefenseBonus() : 0;

        injectBar(cmd, "#StrengthTrack", playerData.getStrength() + strBonus, max, "#e87461");
        cmd.set("#StrengthValue.TextSpans", Message.raw(formatStat(playerData.getStrength(), strBonus)));

        injectBar(cmd, "#DexterityTrack", playerData.getDexterity() + dexBonus, max, "#e8d44d");
        cmd.set("#DexterityValue.TextSpans", Message.raw(formatStat(playerData.getDexterity(), dexBonus)));

        injectBar(cmd, "#AgilityTrack", playerData.getAgility() + agiBonus, max, "#5cb85c");
        cmd.set("#AgilityValue.TextSpans", Message.raw(formatStat(playerData.getAgility(), agiBonus)));

        injectBar(cmd, "#IntelligenceTrack", playerData.getIntelligence() + intBonus, max, "#5b9bd5");
        cmd.set("#IntelligenceValue.TextSpans", Message.raw(formatStat(playerData.getIntelligence(), intBonus)));

        injectBar(cmd, "#DefenseTrack", playerData.getDefense() + defBonus, max, "#d4d4d4");
        cmd.set("#DefenseValue.TextSpans", Message.raw(formatStat(playerData.getDefense(), defBonus)));
    }

    private void buildClassSelectTab(UICommandBuilder cmd) {
        cmd.append("ClassSelectPage.ui");

        PlayerClass preview = CLASSES[selectedClassIndex];
        cmd.set("#ClassName.TextSpans", Message.raw(preview.getDisplayName()));
        cmd.set("#ClassDescription.TextSpans", Message.raw(preview.getDescription()));
        cmd.set("#ClassBaseStats.TextSpans", Message.raw(preview.getBaseStatsDisplay()));
        cmd.set("#ClassPassive.TextSpans", Message.raw(
                "+" + preview.getPassivePercent() + "% " + preview.getPassiveName()));
    }

    private void buildClassTreeTab(UICommandBuilder cmd, PlayerClass playerClass) {
        cmd.append("ClassTreePage.ui");
        cmd.set("#CurrentClassName.TextSpans", Message.raw(playerClass.getDisplayName()));
    }

    private void bindTabEvents(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.Pressed,
                "#SkillsTabBtn",
                EventData.of("@SkillsTab", "#SkillsTabBtnLabel.Text"),
                false
        );
        events.addEventBinding(
                CustomUIEventBindingType.Pressed,
                "#ClassTabBtn",
                EventData.of("@ClassTab", "#ClassTabBtnLabel.Text"),
                false
        );
    }

    private void bindClassSelectEvents(UIEventBuilder events) {
        events.addEventBinding(
                CustomUIEventBindingType.Pressed,
                "#PrevClassBtn",
                EventData.of("@PrevClass", "#PrevClassBtnLabel.Text"),
                false
        );
        events.addEventBinding(
                CustomUIEventBindingType.Pressed,
                "#NextClassBtn",
                EventData.of("@NextClass", "#NextClassBtnLabel.Text"),
                false
        );
        events.addEventBinding(
                CustomUIEventBindingType.Pressed,
                "#ConfirmClassBtn",
                EventData.of("@Confirm", "#ConfirmClassBtnLabel.Text"),
                false
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, EventInput data) {
        super.handleDataEvent(ref, store, data);

        if (data.skillsTab != null) {
            activeTab = Tab.SKILLS;
        } else if (data.classTab != null) {
            activeTab = Tab.CLASS;
        } else if (data.prevClass != null) {
            selectedClassIndex = (selectedClassIndex + CLASSES.length - 1) % CLASSES.length;
        } else if (data.nextClass != null) {
            selectedClassIndex = (selectedClassIndex + 1) % CLASSES.length;
        } else if (data.confirm != null && playerData.getPlayerClassEnum() == null) {
            PlayerClass selected = CLASSES[selectedClassIndex];
            playerData.setPlayerClass(selected.name());
            playerDataManager.save(playerData);
        }

        sendUpdate();
    }

    private String formatStat(int allocated, int bonus) {
        if (bonus > 0) {
            return allocated + " (+" + bonus + ")";
        }
        return String.valueOf(allocated);
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
                .append(new KeyedCodec<>("@SkillsTab", Codec.STRING), (d, v) -> d.skillsTab = v, d -> d.skillsTab).add()
                .append(new KeyedCodec<>("@ClassTab", Codec.STRING), (d, v) -> d.classTab = v, d -> d.classTab).add()
                .append(new KeyedCodec<>("@PrevClass", Codec.STRING), (d, v) -> d.prevClass = v, d -> d.prevClass).add()
                .append(new KeyedCodec<>("@NextClass", Codec.STRING), (d, v) -> d.nextClass = v, d -> d.nextClass).add()
                .append(new KeyedCodec<>("@Confirm", Codec.STRING), (d, v) -> d.confirm = v, d -> d.confirm).add()
                .build();

        private String skillsTab;
        private String classTab;
        private String prevClass;
        private String nextClass;
        private String confirm;
    }
}
