package com.smirk.hysaga.data.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private UUID playerId;
    private String username;

    // Skills
    private int strength;
    private int dexterity;
    private int agility;
    private int intelligence;
    private int defense;

    // Abilities (stored as enum name strings)
    private List<String> abilities;

    // Class (null = no class selected, stored as enum name)
    private String playerClass;

    // Skill points
    private int availablePoints;

    // Experience
    private int level;
    private int exp;

    // Metadata
    private Instant firstJoin;
    private Instant lastJoin;

    public PlayerData() {}

    public PlayerData(UUID playerId, String username) {
        this.playerId = playerId;
        this.username = username;

        this.strength = 1;
        this.dexterity = 1;
        this.agility = 1;
        this.intelligence = 1;
        this.defense = 1;

        this.availablePoints = 0;

        this.abilities = new ArrayList<>();
        this.level = 1;
        this.exp = 0;

        this.firstJoin = Instant.now();
        this.lastJoin = Instant.now();
    }

    public void updateOnJoin(String username) {
        this.username = username;
        this.lastJoin = Instant.now();

        // Migration: ensure fields added after initial release have valid defaults
        if (this.defense <= 0) this.defense = 1;
    }

    // --- Skills ---

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public int getDexterity() { return dexterity; }
    public void setDexterity(int dexterity) { this.dexterity = dexterity; }

    public int getAgility() { return agility; }
    public void setAgility(int agility) { this.agility = agility; }

    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }

    public int getDefense() { return defense; }
    public void setDefense(int defense) { this.defense = defense; }

    // --- Skill Points ---

    public int getAvailablePoints() { return availablePoints; }
    public void setAvailablePoints(int availablePoints) { this.availablePoints = availablePoints; }

    // --- Abilities ---

    public List<String> getAbilities() { return abilities; }

    public boolean hasAbility(Ability ability) {
        return abilities != null && abilities.contains(ability.name());
    }

    public void addAbility(Ability ability) {
        if (abilities == null) abilities = new ArrayList<>();
        String name = ability.name();
        if (!abilities.contains(name)) {
            abilities.add(name);
        }
    }

    public void removeAbility(Ability ability) {
        if (abilities != null) {
            abilities.remove(ability.name());
        }
    }

    // --- Experience ---

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }

    // --- Class ---

    public String getPlayerClass() { return playerClass; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }

    public PlayerClass getPlayerClassEnum() {
        if (playerClass == null) return null;
        try {
            return PlayerClass.valueOf(playerClass);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // --- Identity & Metadata ---

    public UUID getPlayerId() { return playerId; }
    public String getUsername() { return username; }

    public Instant getFirstJoin() { return firstJoin; }
    public Instant getLastJoin() { return lastJoin; }
}
