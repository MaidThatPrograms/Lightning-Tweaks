package caffeinatedpinkie.lightningtweaks.compatibility;

import caffeinatedpinkie.lightningtweaks.common.LightningHandler;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import weather2.config.ConfigStorm;
import weather2.entity.EntityLightningBolt;
import weather2.entity.EntityLightningBoltCustom;

/**
 * Various methods that allow this mod to interact with Weather2 for
 * compatibility.
 *
 * @author CaffeinatedPinkie
 */
public class CompatibilityWeather2 {
    /**
     * Just returns the value of {@link ConfigStorm#Lightning_StartsFires}.
     *
     * @return {@link ConfigStorm#Lightning_StartsFires}
     */
    public static boolean firesEnabled() {
	return ConfigStorm.Lightning_StartsFires;
    }

    /**
     * Determines whether an {@link Entity} is either an {@link EntityLightningBolt}
     * or {@link EntityLightningBoltCustom}.
     *
     * @param entity the {@link Entity} to test
     * @return true if the {@link Entity} is either an {@link EntityLightningBolt}
     *         or {@link EntityLightningBoltCustom}, false otherwise
     */
    public static boolean isLightning(Entity entity) {
	return entity instanceof EntityLightningBolt || entity instanceof EntityLightningBoltCustom;
    }

    /**
     * Creates {@link BlockFire} around where the lightning bolt has been moved to
     * by creating a new instance of a lightning bolt. The constructor places
     * {@link BlockFire} where it would normally go.
     *
     * @param world              the {@link World} the lightning bolt is in
     * @param lightning          the lightning bolt that was moved
     * @param pos                the {@link BlockPos} the lightning bolt was moved
     *                           to
     * @param effectOnly         if {@link BlockFire} should be created
     * @param fireOnConstruction whether {@link BlockFire} was created in the first
     *                           place
     * @see LightningHandler#respawnOnConstructionFire(World, Entity, BlockPos,
     *      boolean, boolean)
     */
    public static void respawnOnConstructionFire(World world, Entity lightning, BlockPos pos, boolean effectOnly,
	    boolean fireOnConstruction) {
	if (lightning instanceof EntityLightningBolt)
	    new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ());
	else
	    new EntityLightningBoltCustom(world, pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Sets the value of {@link EntityLightningBolt#fireChance} to
     * {@link Integer#MAX_VALUE}. This effectively makes the current lightning bolt
     * not create {@link BlockFire} as the chance of this happening is 1 in 2^31 -
     * 1.
     *
     * @param lightning  the lightning bolt to be affected
     * @param effectOnly if {@link BlockFire} should not be created
     */
    public static void setEffectOnly(Entity lightning, boolean effectOnly) {
	if (lightning instanceof EntityLightningBolt)
	    ((EntityLightningBolt) lightning).fireChance = Integer.MAX_VALUE;
	else
	    ((EntityLightningBoltCustom) lightning).fireChance = Integer.MAX_VALUE;
    }
}
