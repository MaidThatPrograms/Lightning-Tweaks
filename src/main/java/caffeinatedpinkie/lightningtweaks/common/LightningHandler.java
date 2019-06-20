package caffeinatedpinkie.lightningtweaks.common;

import java.util.ArrayList;

import caffeinatedpinkie.lightningtweaks.LoggerLT;
import caffeinatedpinkie.lightningtweaks.compatibility.Compatibility;
import caffeinatedpinkie.lightningtweaks.compatibility.CompatibilityWeather2;
import net.minecraft.block.BlockFire;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Handler for when an {@link Entity} is added to the {@link World} in
 * {@link World#weatherEffects}.
 *
 * @author CaffeinatedPinkie
 */
@EventBusSubscriber
public class LightningHandler {
    /**
     * Whether the redirected lightning bolt should be considered only an effect.
     *
     * @param world the {@link World} the lightning bolt is in
     * @param pos   the {@link BlockPos} of the lightning bolt
     * @return true if the lightning bolt is only an effect, false if it spawns fire
     *         and hurts {@link Entity Entities}
     * @see EntityLightningBolt#effectOnly
     */
    public static boolean effectOnly(World world, BlockPos pos) {
	return AttributeMetallic.isMetallic(world.getBlockState(pos.down()));
    }

    /**
     * Whether the lightning bolt had effectOnlyIn passed in when constructed.
     *
     * @param lightning the lightning bolt to test
     * @return true if effectOnlyIn was set to true, false otherwise
     */
    public static boolean effectOnlyIn(Entity lightning) {
	return Compatibility.isVanillaLightning(lightning)
		&& (boolean) ObfuscationReflectionHelper.getPrivateValue(EntityLightningBolt.class,
			(EntityLightningBolt) lightning, "field_184529_d")
		|| Compatibility.weather2 && CompatibilityWeather2.isLightning(lightning)
			&& !CompatibilityWeather2.firesEnabled();
    }

    /**
     * More or less copy-and-pasted code from the {@link EntityLightningBolt}
     * constructor for whether {@link BlockFire} was created on construction plus
     * some code for mod compatibility.
     *
     * @param world        the {@link World} the {@link BlockFire} could be put in
     * @param lightning    the lightning bolt that the {@link BlockFire} would be
     *                     placed around
     * @param effectOnlyIn whether effectOnlyIn was true when passed into the
     *                     lightning constructor
     * @return true if {@link BlockFire} was created on construction, false if it
     *         was not
     * @see EntityLightningBolt#EntityLightningBolt(World, double, double, double,
     *      boolean)
     */
    public static boolean fireOnConstruction(World world, Entity lightning, boolean effectOnlyIn) {
	return !effectOnlyIn && world.getGameRules().getBoolean("doFireTick")
		&& (world.getDifficulty() == EnumDifficulty.NORMAL || world.getDifficulty() == EnumDifficulty.HARD)
		&& world.isAreaLoaded(lightning.getPosition(), 10);
    }

    /**
     * Whether the passed {@link Entity} is any type of lightning bolt, modded or
     * vanilla.
     *
     * @param entity {@link Entity} to test
     * @return true if the {@link Entity} is a type of lightning bolt, false if not
     */
    public static boolean isLightning(Entity entity) {
	return Compatibility.isVanillaLightning(entity)
		|| Compatibility.weather2 && CompatibilityWeather2.isLightning(entity);
    }

    /**
     * Sets the {@link BlockPos} of a lightning bolt.
     *
     * @param lightning the lightning bolt to move
     * @param pos       the {@link BlockPos} to move the lightning bolt to
     */
    public static void moveLightning(Entity lightning, BlockPos pos) {
	LoggerLT.log(() -> lightning.setPosition(pos.getX(), pos.getY(), pos.getZ()), "Lightning moved");
    }

    /**
     * Removes the {@link BlockFire} created when the lightning bolt is constructed.
     *
     * @param world              the {@link World} the {@link BlockFire} is spawned
     *                           in
     * @param lightning          the lightning bolt the {@link BlockFire} is spawned
     *                           around
     * @param fireOnConstruction whether {@link BlockFire} was created in the first
     *                           place
     * @see EntityLightningBolt#EntityLightningBolt(World, double, double, double,
     *      boolean)
     */
    public static void removeOnConstructionFire(World world, Entity lightning, boolean fireOnConstruction) {
	if (fireOnConstruction)
	    LoggerLT.log(() -> {
		BlockPos blockpos = new BlockPos(lightning);
		for (int i = -1; i <= 1; i++)
		    for (int j = -1; j <= 1; j++)
			for (int k = -1; k <= 1; k++) {
			    BlockPos posToCheck = blockpos.add(i, j, k);
			    if (world.getBlockState(posToCheck).getBlock() == Blocks.FIRE)
				world.setBlockToAir(posToCheck);
			}
	    }, "Fire created on lightning bolt construction removed");
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
     * @param fireOnConstruction whether {@link BlockFire} was created in the first
     *                           place
     * @param effectOnly         if {@link BlockFire} should be created
     */
    public static void respawnOnConstructionFire(World world, Entity lightning, BlockPos pos,
	    boolean fireOnConstruction, boolean effectOnly) {
	if (fireOnConstruction && !effectOnly)
	    LoggerLT.log(() -> {
		if (Compatibility.isVanillaLightning(lightning))
		    new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ(), false);
		else if (Compatibility.weather2)
		    CompatibilityWeather2.respawnOnConstructionFire(world, lightning, pos, effectOnly,
			    fireOnConstruction);
	    }, "Fire around new location created");
    }

    /**
     * Sets {@link EntityLightningBolt#effectOnly} and its analog in other mods'
     * lightning bolts.
     *
     * @param lightning          the lightning bolt to set the field in
     * @param fireOnConstruction whether {@link BlockFire} was created in the first
     *                           place
     * @param effectOnly         the {@link boolean} to set the field to
     * @param effectOnlyIn       if effectOnlyIn was true at the lightning's
     *                           construction
     */
    public static void setEffectOnly(Entity lightning, boolean fireOnConstruction, boolean effectOnly,
	    boolean effectOnlyIn) {
	LoggerLT.log(() -> {
	    if (!effectOnlyIn)
		if (Compatibility.isVanillaLightning(lightning))
		    ObfuscationReflectionHelper.setPrivateValue(EntityLightningBolt.class,
			    (EntityLightningBolt) lightning, effectOnly, "field_184529_d");
		else if (Compatibility.weather2)
		    CompatibilityWeather2.setEffectOnly(lightning, effectOnly);
	}, "Set whether fire should be created at new location");
    }

    /**
     * Creates an {@link ArrayList} that redirects {@link Entity Entities} when they
     * are added.
     *
     * @param world the {@link World} that the {@link Entity} is being added to
     * @return a {@link ArrayList} that replaces {@link World#weatherEffects}
     */
    @SuppressWarnings("serial")
    public static ArrayList<Entity> weatherEffectsArray(World world) {
	return new ArrayList<Entity>(world.weatherEffects) {
	    @Override
	    public boolean add(Entity entity) {
		LoggerLT.log(() -> {
		    if (isLightning(entity)) {
			Searcher searcher = new Searcher(world, entity);
			BlockPos pos = searcher.search();
			boolean effectOnlyIn = effectOnlyIn(entity);
			boolean fireOnConstruction = fireOnConstruction(world, entity, effectOnlyIn);
			boolean effectOnly = effectOnlyIn || effectOnly(world, pos);

			removeOnConstructionFire(world, entity, fireOnConstruction);
			setEffectOnly(entity, fireOnConstruction, effectOnly, effectOnlyIn);
			moveLightning(entity, pos);
			respawnOnConstructionFire(world, entity, pos, fireOnConstruction, effectOnly);
		    } else
			LoggerLT.log("EntityWeatherEffect skipped.");
		}, "EntityWeatherEffect caught and returned to the server");
		return super.add(entity);
	    }
	};
    }

    /**
     * Called when a {@link World} is first loaded. It changes
     * {@link World#weatherEffects} to the {@link ArrayList} returned from
     * {@link #weatherEffectsArray}. The new {@link ArrayList} catches {@link Entity
     * Entities} when {@link ArrayList#add(Object)} is called.
     *
     * @param event the {@link World} that was loaded
     */
    @SubscribeEvent
    public static void worldLoaded(Load event) {
	World world = event.getWorld();
	if (!world.isRemote)
	    LoggerLT.log(() -> ObfuscationReflectionHelper.setPrivateValue(World.class, world,
		    weatherEffectsArray(world), "weatherEffects"),
		    "Replaced weatherEffects in dimension " + world.provider.getDimension());
    }
}
