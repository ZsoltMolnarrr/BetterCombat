package net.bettercombat.client.compat;

import net.bettercombat.BetterCombatMod;
import net.bettercombat.Platform;
import net.bettercombat.api.client.AttackRangeExtensions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class PehkuiHelper {
    public static Identifier scaleId = Identifier.fromNamespaceAndPath("pehkui", "entity_reach");
    private static final Method GET_SCALE_DATA;
    private static final Method GET_SCALE;
    private static final Map<Identifier, Object> SCALE_TYPES;

    public static void load() {}

    static
    {
        Method getScaleDataMethod = null;
        Method getScaleMethod = null;
        Map<Identifier, Object> scaleTypes = null;

        if (Platform.isModLoaded("pehkui"))
        {
            try
            {
                Class<?> scaleTypeClass = Class.forName("virtuoel.pehkui.api.ScaleType");
                Class<?> scaleDataClass = Class.forName("virtuoel.pehkui.api.ScaleData");
                Class<?> scaleRegistriesClass = Class.forName("virtuoel.pehkui.api.ScaleRegistries");
                Field scaleTypesField = scaleRegistriesClass.getField("SCALE_TYPES");

                getScaleDataMethod = scaleTypeClass.getMethod("getScaleData", Entity.class);
                getScaleMethod = scaleDataClass.getMethod("getScale", float.class);
                scaleTypes = (Map<Identifier, Object>) scaleTypesField.get(null);

                AttackRangeExtensions.register(context -> {
                    var multiplier = BetterCombatMod.config.getAttackRangeMultiplierForScale(PehkuiHelper.getScale(context.player()));
                    return new AttackRangeExtensions.Modifier(multiplier, AttackRangeExtensions.Operation.MULTIPLY);
                });
            }
            catch (ClassNotFoundException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException e)
            {
                getScaleDataMethod = null;
                getScaleMethod = null;
                scaleTypes = null;
            }
        }

        GET_SCALE_DATA = getScaleDataMethod;
        GET_SCALE = getScaleMethod;
        SCALE_TYPES = scaleTypes;
    }

    public static float getScale(Entity entity)
    {
        return getScale(entity, scaleId, 1.0F);
    }

    public static float getScale(Entity entity, Identifier scaleId, float tickDelta)
    {
        if (GET_SCALE_DATA != null && GET_SCALE != null && SCALE_TYPES != null)
        {
            try
            {
                return (float) GET_SCALE.invoke(GET_SCALE_DATA.invoke(SCALE_TYPES.get(scaleId), entity), tickDelta);
            }
            catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                return 1.0F;
            }
        }

        return 1.0F;
    }
}
