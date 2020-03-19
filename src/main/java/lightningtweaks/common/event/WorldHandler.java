package lightningtweaks.common.event;

import java.util.List;

import lightningtweaks.common.GlobalEntitiesList;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Handler for all {@link WorldEvent}s. Currently only monitors {@link Load}.
 */
@EventBusSubscriber
public class WorldHandler {
    /**
     * Fired on an {@link IWorld} {@link Load} event. The name is pretty
     * self-explanatory: whenever the user or the server loads an
     * {@link IWorld}.<br>
     * <br>
     * This method checks {@link IWorld#isRemote()} before executing any code. This
     * guarantees that the {@link IWorld} object can be safely cast to
     * {@link ServerWorld}. If this is the case, {@link ServerWorld#globalEntities}
     * is replaced with an instance of {@link GlobalEntitiesList}.<br>
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
     * <hr>
     * <b>Mappings</b>
     * <ul>
     * <li><code>field_217497_w</code> -> {@link ServerWorld#globalEntities}</li>
     * </ul>
     *
     * @param event the {@link Load} event
     */
    @SubscribeEvent
    public static void onLoad(Load event) {
	IWorld world = event.getWorld();
	if (!world.isRemote())
	    ObfuscationReflectionHelper.setPrivateValue(ServerWorld.class, (ServerWorld) world,
		    new GlobalEntitiesList(world), "field_217497_w");
    }
}
