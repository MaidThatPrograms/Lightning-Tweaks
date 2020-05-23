package lightningtweaks;

import lightningtweaks.common.LTConfig;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Entry point for Lightning Tweaks!
 */
@Mod("lightningtweaks")
@EventBusSubscriber(bus = Bus.MOD)
public class LightningTweaks {
	/**
	 * TODO
	 *
	 * @param event TODO
	 */
	@SubscribeEvent
	public static void commonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		LTConfig.register();
	}
}