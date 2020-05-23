package lightningtweaks.common.event;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.FireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
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
	private static final Map<IWorld, Difficulty> difficulties = new HashMap<>();

	/**
	 * Fired when an {@link Entity} is constructing.<br>
	 * <br>
	 * This method checks that the {@link Entity} is both an instance of
	 * {@link LightningBoltEntity} and that its {@link World#isRemote()} returns
	 * true. If this is the case, the current {@link Difficulty} is stored in
	 * {@link #difficulties} along with the {@link World} object. Then, the
	 * {@link World}'s {@link Difficulty} is set to {@link Difficulty#EASY}.<br>
	 * <br>
	 * The last thing a {@link LightningBoltEntity} does before finishing
	 * constructing is to call {@link LightningBoltEntity#igniteBlocks(int)}. This
	 * is before it is added to the {@link World}, but after this handler is
	 * alerted. Thus, changing the location of the {@link LightningBoltEntity} when
	 * it is added to the {@link World} does not prevent {@link FireBlock}s from
	 * being spawned around the initial strike position. Among a few other
	 * variables, the {@link Difficulty} of the {@link World} is checked before
	 * doing so. If it is {@link Difficulty#NORMAL} or {@link Difficulty#HARD},
	 * {@link FireBlock}s are spawned.<br>
	 * <br>
	 * The intention of this method is to set the {@link Difficulty} of the
	 * {@link World} to {@link Difficulty#EASY} before
	 * {@link LightningBoltEntity#igniteBlocks(int)} is called to prevent any
	 * {@link FireBlock} spawning. It also stores the current {@link Difficulty} to
	 * be restored before the {@link LightningBoltEntity} is added to the
	 * {@link World}. Since the {@link World} should not run any updates between
	 * {@link LightningBoltEntity} being constructed and being added, this shouldn't
	 * cause any changes to already spawned {@link Entity Entities}.
	 *
	 * @param event the {@link EntityConstructing} event
	 */
	@SubscribeEvent
	public static void onConstructing(EntityConstructing event) {
		Entity entity = event.getEntity();
		if (entity instanceof LightningBoltEntity) {
			@SuppressWarnings("resource")
			World world = entity.getEntityWorld();
			if (!world.isRemote()) {
				difficulties.put(world, world.getDifficulty());
				setDifficulty(world, Difficulty.EASY);
			}
		}
	}

	/**
	 * Sets the {@link Difficulty} of the given {@link IWorld} to the value stored
	 * in {@link #difficulties}. Then removes the matching {@link World} entry from
	 * {@link #difficulties}. It is assumed that all calls to this method pass in
	 * {@link World} objects that are present in {@link #difficulties}.
	 *
	 * @param world the {@link IWorld} to set the {@link Difficulty} of
	 */
	public static void revertDifficulty(IWorld world) {
		setDifficulty(world, difficulties.remove(world));
	}

	/**
	 * Sets the {@link Difficulty} of the given {@link IWorld} to the given
	 * {@link Difficulty}.
	 *
	 * @param world      the {@link IWorld} to change the {@link Difficulty} of
	 * @param difficulty the {@link Difficulty} to change the {@link IWorld}'s
	 *                   {@link Difficulty} to
	 */
	private static void setDifficulty(IWorld world, Difficulty difficulty) {
		world.getWorldInfo().setDifficulty(difficulty);
	}
}
