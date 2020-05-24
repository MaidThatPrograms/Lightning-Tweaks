package lightningtweaks;

import lightningtweaks.common.LTConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * Entry point for Lightning Tweaks!
 */
@Mod("lightningtweaks")
@EventBusSubscriber(bus = Bus.MOD)
public class LightningTweaks {
	/**
	 * TODO
	 */
	public LightningTweaks() {
		LTConfig.register();
	}
}