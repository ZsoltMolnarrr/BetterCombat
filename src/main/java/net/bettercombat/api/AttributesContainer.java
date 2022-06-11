package net.bettercombat.api;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Represents the content of a weapon attributes json file.
 * If the name of the containing json file matches an existing item (for example: "data/minecraft/weapon_attributes/wooden_sword.json),
 * the attributes will be assigned to the item, after the attribute inheritance has been automatically resolved."
 */
public final class AttributesContainer {

    /**
     * Specifies the attributes container to inherit from.
     * If not specified (or null), no inheritance is looked up, make sure `attributes` has fully parsable value.
     * Value must be an identifier, formula: "namespace:resource".
     * Examples:
     *  "minecraft:wooden_sword"
     *  "my-mod-id:my-sword"
     *  "my-mod-id:my-abstract-weapon-attributes"
     */
    @Nullable
    private final String parent;

    /**
     * The actual attributes we want to assign to an existing item, or abstract attributes.
     * If not specified (or null), make sure `parent` has a valid value.
     * Check out the documentation of `WeaponAttributes` to see its structure and member wise explanation.
     */
    @Nullable
    private final WeaponAttributes attributes;

    public AttributesContainer(@Nullable String parent, @Nullable WeaponAttributes attributes) {
        this.parent = parent;
        this.attributes = attributes;
    }

    public String parent() {
        return parent;
    }

    public WeaponAttributes attributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (AttributesContainer) obj;
        return Objects.equals(this.parent, that.parent) &&
                Objects.equals(this.attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, attributes);
    }

    @Override
    public String toString() {
        return "AttributesContainer[" +
                "parent=" + parent + ", " +
                "attributes=" + attributes + ']';
    }

}
