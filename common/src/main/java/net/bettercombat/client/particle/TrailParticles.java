package net.bettercombat.client.particle;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.particle.BetterCombatParticles;
import net.bettercombat.particle.SlashParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;

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
    public Map<Identifier, List<Entry>> entries = Map.of(
            Identifier.of(NAMESPACE, "stab"), List.of(
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
            Identifier.of(NAMESPACE, "slash45"), List.of(
                    new Entry(List.of(
                            new LayeredParticle(
                                    BetterCombatParticles.topslash45.particleType(),
                                    BetterCombatParticles.botslash45.particleType()
                            )
                    ))
            )
    );
}
