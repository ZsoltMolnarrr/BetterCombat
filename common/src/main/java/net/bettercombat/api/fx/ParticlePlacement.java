package net.bettercombat.api.fx;

public record ParticlePlacement(
        String particle_type,
        float x_addition,
        float y_addition,
        float z_addition,
        float local_yaw,
        float pitch_addition,
        float roll_set) {
    public static final ParticlePlacement DEFAULT = new ParticlePlacement("none", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

    public ParticlePlacement(String particle_type, float x_addition, float y_addition, float z_addition, float local_yaw, float pitch_addition, float roll_set) {
        this.particle_type = particle_type;
        this.x_addition = x_addition;
        this.y_addition = y_addition;
        this.z_addition = z_addition;
        this.local_yaw = local_yaw;
        this.pitch_addition = pitch_addition;
        this.roll_set = roll_set;
    }

    public String particle_type() {
        return this.particle_type;
    }

    public float z_addition() {
        return this.z_addition;
    }

    public float x_addition() {
        return this.x_addition;
    }

    public float y_addition() {
        return this.y_addition;
    }

    public float local_yaw() {
        return this.local_yaw;
    }

    public float pitch_addition() {
        return this.pitch_addition;
    }

    public float roll_set() {
        return this.roll_set;
    }
}
