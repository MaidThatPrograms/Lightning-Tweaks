package lightningtweaks.common.event;

import lightningtweaks.common.WeatherEffectsList;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber
public class WorldHandler {
    @SubscribeEvent
    public static void onLoad(Load event) {
	World world = event.getWorld();
	if (!world.isRemote) {
	    System.out.println("Replacing weatherEffects in dimension " + world.provider.getDimension() + '!');
	    ObfuscationReflectionHelper.setPrivateValue(World.class, world, new WeatherEffectsList(world),
		    "field_73007_j"); // weatherEffects
	}
    }
}