package lightningtweaks.common;

import java.lang.reflect.Field;
import java.util.List;

import lightningtweaks.LightningTweaks;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

@EventBusSubscriber
public class EventHandler {
	public static final Field GLOBAL_ENTITIES = ObfuscationReflectionHelper.findField(ServerWorld.class,
			"field_217497_w");

	/**
	 * Fired on an {@link World} {@link Load} event. The name is pretty
	 * self-explanatory: whenever the user or the server loads an {@link World}.<br>
	 * <br>
	 * This method checks {@link World#isRemote()} before executing any code. This
	 * guarantees that the {@link World} object can be safely cast to
	 * {@link ServerWorld}. If this is the case, {@link ServerWorld#globalEntities}
	 * is replaced with an instance of {@link CustomGlobalEntitiesList}.<br>
	 * <br>
	 * All {@link LightningBoltEntity LightningBoltEntities} are added to the
	 * {@link ServerWorld} via
	 * {@link ServerWorld#addLightningBolt(LightningBoltEntity)} in which they are
	 * then added to {@link ServerWorld#globalEntities} via
	 * {@link List#add(Object)}.<br>
	 * <br>
	 * The intention of this method is to catch all calls to
	 * {@link List#add(Object)} in order to manipulate {@link LightningBoltEntity
	 * LightningBoltEntities} before they are added to the {@link ServerWorld}.
	 *
	 * @param event the {@link Load} event
	 * @throws IllegalAccessException never, unless something goes horribly wrong
	 */
	@SubscribeEvent
	public static void onLoad(Load event) throws IllegalAccessException {
		@SuppressWarnings("resource")
		World world = event.getWorld().getWorld();
		if (!world.isRemote()) {
			LightningTweaks.log("Replacing " + GLOBAL_ENTITIES, world);
			GLOBAL_ENTITIES.set(world, new CustomGlobalEntitiesList(world));
		}
	}
}