package caffeinatedpinkie.lightningtweaks.compatibility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraftforge.fml.common.Loader;

/**
 * A collection of constants and methods that are commonly used for mod
 * compatibility.
 *
 * @author CaffeinatedPinkie
 */
public class Compatibility {
    public static boolean weather2 = Loader.isModLoaded("weather2");

    /**
     * Returns whether the passed {@link Entity} is an {@link EntityLightningBolt}
     * or one of its subclasses.
     *
     * @param entity the {@link Entity} to test
     * @return true if the {@link Entity} is an {@link EntityLightningBolt}, false
     *         otherwise
     */
    public static boolean isVanillaLightning(Entity entity) {
	return entity instanceof EntityLightningBolt;
    }
}
