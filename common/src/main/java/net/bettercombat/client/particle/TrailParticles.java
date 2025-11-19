package net.bettercombat.client.particle;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.api.trail.ParticleSettings;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.particle.ParticleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrailParticles {

    public record LayeredParticle(ParticleType<SlashParticleEffect> top, ParticleType<SlashParticleEffect> bottom) {
        public LayeredParticle of(ParticleType<SlashParticleEffect> top, ParticleType<SlashParticleEffect> bottom) {
            return new LayeredParticle(top, bottom);
        }
    }

    // TODO: Add field(s) to eliminate `stabPosition`
    public record Entry(List<LayeredParticle> particles, float rollOffset, boolean stabPosition) {
        public Entry(List<LayeredParticle> particles, float rollOffset) {
            this(particles, rollOffset, false);
        }
        public Entry(List<LayeredParticle> particles) {
            this(particles, 0F, false);
        }
    }

    private static final String NAMESPACE = BetterCombatMod.ID;
    public static Map<String, List<Entry>> ENTRIES = Map.of(
            "stab", List.of(
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topstab.particleType(),
                                    BetterCombatParticles.botstab.particleType()
                            )
                    ), -45F),
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topstab.particleType(),
                                    BetterCombatParticles.botstab.particleType()
                            )
                    ), 45F)
            ),
            "slash45", List.of(
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topslash45.particleType(),
                                    BetterCombatParticles.botslash45.particleType()
                            )
                    ))
            ),
            "slash90", List.of(
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topslash90.particleType(),
                                    BetterCombatParticles.botslash90.particleType()
                            )
                    ))
            ),
            "slash180", List.of(
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topslash180.particleType(),
                                    BetterCombatParticles.botslash180.particleType()
                            )
                    ))
            ),
            "slash270", List.of(
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topslash270.particleType(),
                                    BetterCombatParticles.botslash270.particleType()
                            )
                    ))
            ),
            "slash360", List.of(
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topslash360.particleType(),
                                    BetterCombatParticles.botslash360.particleType()
                            )
                    ))
            )
    );

    public static HashMap<String, List<ParticleSettings>> defaults() {
        HashMap<String, List<ParticleSettings>> map = new HashMap<>();

        // bettercombat:one_handed_slash_horizontal_right
        map.put(NAMESPACE + ":one_handed_slash_horizontal_right", List.of(
                new ParticleSettings(
                        "slash90",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_slash_horizontal_left
        map.put(NAMESPACE + ":one_handed_slash_horizontal_left", List.of(
                new ParticleSettings(
                        "slash90",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        180.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_uppercut_right
        map.put(NAMESPACE + ":one_handed_uppercut_right", List.of(
                new ParticleSettings(
                        "slash90",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        75.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_swipe_horizontal_right
        map.put(NAMESPACE + ":one_handed_swipe_horizontal_right", List.of(
                new ParticleSettings(
                        "slash90",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        -0.10F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_slam
        map.put(NAMESPACE + ":one_handed_slam", List.of(
                new ParticleSettings(
                        "slash180",
                        0.05F,
                        -0.1F,
                        0.0F,
                        45.0F,
                        -85.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_stab
        map.put(NAMESPACE + ":one_handed_stab", List.of(
                new ParticleSettings(
                        "stab",
                        0.0F,
                        -0.13F,
                        0.20F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_stab_mounted
        map.put(NAMESPACE + ":one_handed_stab_mounted", List.of(
                new ParticleSettings(
                        "stab",
                        0.0F,
                        0.15F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_punch
        map.put(NAMESPACE + ":one_handed_punch", List.of(
                new ParticleSettings(
                        "stab",
                        0.0F,
                        -0.1F,
                        0.15F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:dual_handed_slash_cross
        map.put(NAMESPACE + ":dual_handed_slash_cross", List.of(
                new ParticleSettings(
                        "slash180",
                        0.2F,
                        -0.15F,
                        0.0F,
                        0.0F,
                        -120.0F,
                        0.0F
                ),
                new ParticleSettings(
                        "slash180",
                        -0.2F,
                        -0.15F,
                        0.0F,
                        0.0F,
                        -60.0F,
                        0.0F
                )
        ));

        // bettercombat:dual_handed_slash_uncross
        map.put(NAMESPACE + ":dual_handed_slash_uncross", List.of(
                new ParticleSettings(
                        "slash180",
                        0.2F,
                        -0.15F,
                        0.0F,
                        0.0F,
                        240.0F,
                        0.0F
                ),
                new ParticleSettings(
                        "slash180",
                        -0.2F,
                        -0.15F,
                        0.0F,
                        0.0F,
                        300.0F,
                        0.0F
                )
        ));

        // bettercombat:dual_handed_stab
        map.put(NAMESPACE + ":dual_handed_stab", List.of(
                new ParticleSettings(
                        "stab",
                        0.4F,
                        -0.3F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                ),
                new ParticleSettings(
                        "stab",
                        -0.4F,
                        -0.3F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_stab_left
        map.put(NAMESPACE + ":two_handed_stab_left", List.of(
                new ParticleSettings(
                        "stab",
                        0.0F,
                        -0.15F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_stab_right
        map.put(NAMESPACE + ":two_handed_stab_right", List.of(
                new ParticleSettings(
                        "stab",
                        0.0F,
                        -0.15F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_slash_horizontal_right
        map.put(NAMESPACE + ":two_handed_slash_horizontal_right", List.of(
                new ParticleSettings(
                        "slash180",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        -5.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_slash_horizontal_left
        map.put(NAMESPACE + ":two_handed_slash_horizontal_left", List.of(
                new ParticleSettings(
                        "slash180",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        180.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_slash_switch_blade_right
        map.put(NAMESPACE + ":one_handed_slash_switch_blade_right", List.of(
                new ParticleSettings(
                        "slash180",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        0.0F,
                        0.0F
                )
        ));

        // bettercombat:one_handed_slash_switch_blade_left
        map.put(NAMESPACE + ":one_handed_slash_switch_blade_left", List.of(
                new ParticleSettings(
                        "slash180",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        180.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_spin
        map.put(NAMESPACE + ":two_handed_spin", List.of(
                new ParticleSettings(
                        "slash360",
                        0.0F,
                        -0.1F,
                        0.0F,
                        0.0F,
                        180.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_slash_vertical_right
        map.put(NAMESPACE + ":two_handed_slash_vertical_right", List.of(
                new ParticleSettings(
                        "slash90",
                        0.0F,
                        -0.1F,
                        0.0F,
                        45.0F,
                        -80.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_slash_vertical_left
        map.put(NAMESPACE + ":two_handed_slash_vertical_left", List.of(
                new ParticleSettings(
                        "slash90",
                        0.0F,
                        -0.1F,
                        0.0F,
                        45.0F,
                        -100.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_slam
        map.put(NAMESPACE + ":two_handed_slam", List.of(
                new ParticleSettings(
                        "slash180",
                        0.1F,
                        -0.1F,
                        0.0F,
                        45.0F,
                        -86.0F,
                        0.0F
                )
        ));

        // bettercombat:two_handed_slam_heavy
        map.put(NAMESPACE + ":two_handed_slam_heavy", List.of(
                new ParticleSettings(
                        "slash180",
                        0.1F,
                        -0.1F,
                        0.0F,
                        45.0F,
                        -86.0F,
                        0.0F
                )
        ));

        return map;
    }

    public static HashMap<String, List<ParticleSettings>> animationBasedParticles = defaults();
}
