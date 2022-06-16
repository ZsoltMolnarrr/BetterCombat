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
                            new WeaponAttributes.Sound("asd"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("asd"),
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
                            new WeaponAttributes.Sound("asd"),
                            null
                    ),
                    new WeaponAttributes.Attack(
                            WeaponAttributes.HitBoxShape.HORIZONTAL_PLANE,
                            1,
                            150,
                            0.5,
                            "bettercombat:placeholder",
                            new WeaponAttributes.Sound("asd"),
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


    public static void print() {
        var gson = new Gson();
        System.out.println("axe: " + gson.toJson(axe));
        System.out.println("dagger: " + gson.toJson(dagger));
        System.out.println("double_axe: " + gson.toJson(double_axe));
        System.out.println("fists: " + gson.toJson(fists));
    }
}
