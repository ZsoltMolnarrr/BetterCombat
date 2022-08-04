package net.bettercombat.config;

public class FallbackConfig {
    public CompatibilitySpecifier[] fallback_compatibility;

    public static class CompatibilitySpecifier {
        public String item_id_regex;
        public String weapon_attributes;

        public CompatibilitySpecifier() { }

        public CompatibilitySpecifier(String item_id_regex, String weapon_attributes) {
            this.item_id_regex = item_id_regex;
            this.weapon_attributes = weapon_attributes;
        }
    }

    public static FallbackConfig createDefault() {
        var object = new FallbackConfig();
        object.fallback_compatibility = new CompatibilitySpecifier[] {
                new CompatibilitySpecifier(
                        "claymore|great_sword|greatsword",
                        "bettercombat:claymore"),
                new CompatibilitySpecifier(
                        "great_hammer|greathammer|war_hammer|warhammer",
                        "bettercombat:hammer"),
                new CompatibilitySpecifier(
                        "double_axe|doubleaxe|war_axe|waraxe|great_axe|greataxe",
                        "bettercombat:double_axe"),
                new CompatibilitySpecifier(
                        "scythe",
                        "bettercombat:scythe"),
                new CompatibilitySpecifier(
                        "halberd",
                        "bettercombat:halberd"),
                new CompatibilitySpecifier(
                        "glaive",
                        "bettercombat:glaive"),
                new CompatibilitySpecifier(
                        "spear|lance",
                        "bettercombat:spear"),
                new CompatibilitySpecifier(
                        "anchor",
                        "bettercombat:anchor"),
                new CompatibilitySpecifier(
                        "battlestaff|battle_staff",
                        "bettercombat:staff"),
                new CompatibilitySpecifier(
                        "fist|gauntlet",
                        "bettercombat:fist"),
                new CompatibilitySpecifier(
                        "trident|impaled",
                        "bettercombat:trident"),
                new CompatibilitySpecifier(
                        "katana",
                        "bettercombat:katana"),
                new CompatibilitySpecifier(
                        "rapier",
                        "bettercombat:rapier"),
                new CompatibilitySpecifier(
                        "sickle",
                        "bettercombat:sickle"),
                new CompatibilitySpecifier(
                        "dagger|knife",
                        "bettercombat:dagger"),
                new CompatibilitySpecifier(
                        "mace|hammer|flail",
                        "bettercombat:mace"),
                new CompatibilitySpecifier(
                        "axe",
                        "bettercombat:axe"),
                new CompatibilitySpecifier(
                        "sword|blade|cutlass|scimitar",
                        "bettercombat:sword")
        };
        return object;
    }
}
