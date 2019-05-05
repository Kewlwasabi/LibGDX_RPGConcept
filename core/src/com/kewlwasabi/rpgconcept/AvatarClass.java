package com.kewlwasabi.rpgconcept;

public enum AvatarClass { //stores stat values and classType of each class

    ROGUE("Rogue", 1, 150, 100, 10, 0, 15, 15, 15, 10, "Light"),
    ARCHER("Archer", 2, 130, 100, 10, 0, 12, 12, 12, 10, "Light"),
    WIZARD("Wizard", 3, 100, 100, 12, 0, 10, 15, 12, 12, "Robed"),
    PRIEST("Priest", 4, 100, 100, 10, 0, 12, 12, 10, 15, "Robed"),
    WARRIOR("Warrior", 5, 200, 100, 15, 0, 7, 10, 10, 10, "Melee"),
    KNIGHT("Knight", 6, 200, 100, 15, 0, 7, 10, 10, 10, "Melee"),
    PALADIN("Paladin", 7, 200, 100, 15, 0, 7, 10, 10, 10, "Melee"),
    ASSASIN("Assasin", 8, 150, 100, 12, 0, 15, 15, 15, 10, "Light"),
    NECRO("Necro", 9, 100, 100, 12, 0, 10, 15, 12, 12, "Robed"),
    HUNTRESS("Huntress", 10, 130, 100, 12, 0, 12, 12, 12, 10, "Light"),
    MYSTIC("Mystic", 11, 100, 100, 10, 0, 12, 10, 15, 15, "Robed"),
    TRICKSTER("Trickster", 12, 150, 100, 10, 0, 12, 15, 12, 12, "Light"),
    SORCERER("Sorcerer", 13, 100, 100, 10, 0, 12, 12, 10, 15, "Robed"),
    NINJA("Ninja", 14, 150, 100, 15, 0, 10, 12, 10, 12, "Medium"),
    SAMURAI("Samurai", 15, 150, 100, 15, 0, 10, 12, 10, 12, "Medium");


    private String name;
    private String classType;
    private int classOrder;
    private int[] stats;

    AvatarClass(String name, int classOrder, int hp, int mp, int atk,
                int def, int spd, int dex, int vit, int wis, String classType) {
        this.name = name;
        this.classOrder = classOrder;
        this.classType = classType;
        stats = new int[]{hp, mp, atk, def, spd, dex, vit, wis};
    }

    public int classOrder() {
        return classOrder;
    }

    public String className() {
        return name;
    }

    public int stat(int a) {
        return stats[a];
    }

    public String classType() {
        return classType;
    }

}
