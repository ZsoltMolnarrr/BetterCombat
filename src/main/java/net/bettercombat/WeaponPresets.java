package net.bettercombat;

import com.google.gson.Gson;
import net.bettercombat.api.WeaponAttributes;

public class WeaponPresets {
    static WeaponAttributes axe = new WeaponAttributes(
            2.5F,
            null,
            false,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:axe-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:axe-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes dagger = new WeaponAttributes(
            2F,
            null,
            false,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:dagger-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:dagger-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes double_axe = new WeaponAttributes(
            3F,
            "bettercombat:double_axe_pose",
            true,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:double-axe-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            360,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:double-axe-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes fists = new WeaponAttributes(
            2F,
            null,
            false,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.FORWARD_BOX,
                            1,
                            0,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:fist-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.FORWARD_BOX,
                            1,
                            0,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:fist-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes glaive = new WeaponAttributes(
            3.5F,
            "bettercombat:2h_polearm_pose",
            true,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            0.8,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:glaive-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            0.8,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:glaive-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.VERTICAL_PLANE,
                            1.4,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:glaive-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes hammer = new WeaponAttributes(
            3F,
            "bettercombat:2h_hammer_pose",
            true,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.FORWARD_BOX,
                            1,
                            0,
                            0.5F,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:glaive-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes mace = new WeaponAttributes(
            2.5F,
            null,
            false,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            0.8,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:mace-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:mace-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.VERTICAL_PLANE,
                            1.2,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:mace-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes pickaxe = new WeaponAttributes(
            2.5F,
            null,
            false,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.VERTICAL_PLANE,
                            1,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:pickaxe-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes spear = new WeaponAttributes(
            3.5F,
            "bettercombat:2h_spear_pose",
            true,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.FORWARD_BOX,
                            1,
                            0,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:spear-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes staff = new WeaponAttributes(
            3.5F,
            "bettercombat:2h_polearm_pose",
            true,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            0.8,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:staff-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:staff-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.VERTICAL_PLANE,
                            1.2,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:staff-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.FORWARD_BOX,
                            1.4,
                            180,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:staff-swing"),
                            null
                    )
            }
    );

    static WeaponAttributes sword = new WeaponAttributes(
            2.5F,
            null,
            false,
            new WeaponAttributes.Attack[] {
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:sword-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:sword-swing"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.FORWARD_BOX,
                            1,
                            0,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("bettercombat:sword-swing"),
                            null
                    )
            }
    );

    public static void print() {
        var gson = new Gson();
        System.out.println("axe: " + gson.toJson(axe));
        System.out.println("dagger: " + gson.toJson(dagger));
        System.out.println("double_axe: " + gson.toJson(double_axe));
        System.out.println("fists: " + gson.toJson(fists));
        System.out.println("glaive: " + gson.toJson(glaive));
        System.out.println("hammer: " + gson.toJson(hammer));
        System.out.println("mace: " + gson.toJson(mace));
        System.out.println("pickaxe: " + gson.toJson(pickaxe));
        System.out.println("spear: " + gson.toJson(spear));
        System.out.println("staff: " + gson.toJson(staff));
        System.out.println("sword: " + gson.toJson(sword));
    }
}
