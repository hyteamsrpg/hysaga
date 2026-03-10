package com.smirk.hysaga.data.model;

public enum PlayerClass {

    WARRIOR("Warrior",
            "A mighty frontline combatant excelling in raw power and resilience.",
            5, 0, 0, 0, 0,
            "Max Health", 10),

    ROGUE("Rogue",
            "A swift shadow striker who thrives on agility and precision.",
            0, 0, 5, 0, 0,
            "Crit Chance", 10),

    MAGE("Mage",
            "A master of arcane arts wielding devastating magical forces.",
            0, 0, 0, 5, 0,
            "Mana Regen", 10),

    ARCHER("Archer",
            "A keen-eyed marksman with unmatched dexterity and range.",
            0, 5, 0, 0, 0,
            "Move Speed", 10);

    private final String displayName;
    private final String description;
    private final int strengthBonus;
    private final int dexterityBonus;
    private final int agilityBonus;
    private final int intelligenceBonus;
    private final int defenseBonus;
    private final String passiveName;
    private final int passivePercent;

    PlayerClass(String displayName, String description,
                int strengthBonus, int dexterityBonus, int agilityBonus,
                int intelligenceBonus, int defenseBonus,
                String passiveName, int passivePercent) {
        this.displayName = displayName;
        this.description = description;
        this.strengthBonus = strengthBonus;
        this.dexterityBonus = dexterityBonus;
        this.agilityBonus = agilityBonus;
        this.intelligenceBonus = intelligenceBonus;
        this.defenseBonus = defenseBonus;
        this.passiveName = passiveName;
        this.passivePercent = passivePercent;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public int getStrengthBonus() { return strengthBonus; }
    public int getDexterityBonus() { return dexterityBonus; }
    public int getAgilityBonus() { return agilityBonus; }
    public int getIntelligenceBonus() { return intelligenceBonus; }
    public int getDefenseBonus() { return defenseBonus; }
    public String getPassiveName() { return passiveName; }
    public int getPassivePercent() { return passivePercent; }

    public String getBaseStatsDisplay() {
        StringBuilder sb = new StringBuilder();
        if (strengthBonus > 0) sb.append("Strength: +").append(strengthBonus);
        if (dexterityBonus > 0) sb.append("Dexterity: +").append(dexterityBonus);
        if (agilityBonus > 0) sb.append("Agility: +").append(agilityBonus);
        if (intelligenceBonus > 0) sb.append("Intelligence: +").append(intelligenceBonus);
        if (defenseBonus > 0) sb.append("Defense: +").append(defenseBonus);
        return sb.toString();
    }
}
