package lightningtweaks.common.event;

import java.util.HashSet;
import java.util.Set;

import lightningtweaks.LightningTweaks;
import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.BooleanValue;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

/**
 * Handler for all {@link EntityEvent}s. Currently only monitors
 * {@link EntityConstructing}.
 */
@EventBusSubscriber
public class EntityHandler {
	public static Set<World> doFireTicks = new HashSet<>();

	/**
	 * Fired when an {@link Entity} is constructing.<br>
	 * <br>
	 * This method checks that the {@link Entity} is both an instance of
	 * {@link LightningBoltEntity} and that its {@link World#isRemote()} returns
	 * true. If this is the case, and the current value for
	 * {@link GameRules#DO_FIRE_TICK} is true, the {@link World} object is stored in
	 * {@link #doFireTicks}. Then, the value of {@link GameRules#DO_FIRE_TICK} is
	 * set to false.<br>
	 * <br>
	 * The last thing a {@link LightningBoltEntity} does before finishing
	 * constructing is to call {@link LightningBoltEntity#igniteBlocks(int)}. This
	 * is before it is added to the {@link World}, but after this handler is
	 * alerted. Thus, changing the location of the {@link LightningBoltEntity} when
	 * it is added to the {@link World} does not prevent {@link FireBlock}s from
	 * being spawned around the initial strike position. Among a few other
	 * variables, the value of {@link GameRules#DO_FIRE_TICK} is checked before
	 * doing so. If it is true, {@link FireBlock}s are spawned.<br>
	 * <br>
	 * The intention of this method is to set the value of
	 * {@link GameRules#DO_FIRE_TICK} to false before
	 * {@link LightningBoltEntity#igniteBlocks(int)} is called to prevent any
	 * {@link FireBlock} spawning. It also stores the information needed to restore
	 * the current value of {@link GameRules#DO_FIRE_TICK} before the
	 * {@link LightningBoltEntity} is added to the {@link World}. Since the
	 * {@link World} should not run any updates between {@link LightningBoltEntity}
	 * being constructed and being added, this shouldn't cause any changes to the
	 * world.
	 *
	 * @param event the {@link EntityConstructing} event
	 */
	@SubscribeEvent
	public static void onConstructing(EntityConstructing event) {
		Entity entity = event.getEntity();
		if (entity.getType() == EntityType.LIGHTNING_BOLT) {
			World world = entity.getEntityWorld();
			if (!world.isRemote()) {
				BooleanValue gamerule = world.getGameRules().get(GameRules.DO_FIRE_TICK);
				if (gamerule.get()) {
					doFireTicks.add(world);
					LightningTweaks.log("Setting " + GameRules.DO_FIRE_TICK + " to false", world);
					gamerule.set(false, world.getServer());
				}
			}
		}
	}

	/**
	 * TODO
	 *
	 * @param world TODO
	 * @return TODO
	 */
	public static boolean removeDoFireTick(World world) {
		return doFireTicks.remove(world);
	}

	/**
	 * TODO
	 *
	 * @param world TODO
	 */
	public static void revertDoFireTick(World world) {
		world.getGameRules().get(GameRules.DO_FIRE_TICK).set(true, world.getServer());
	}
}
