package lightningtweaks;

import lightningtweaks.common.LTConfig;
import net.minecraftforge.fml.common.Mod;

/**
 * Entry point for Lightning Tweaks!
 */
@Mod(LightningTweaks.MODID)
public class LightningTweaks {
	public static final String MODID = "lightningtweaks";

	/**
	 * The mod constructor for Lightning Tweaks. Registers the configuration file
	 * through {@link LTConfig#register()}.
	 */
	public LightningTweaks() {
		LTConfig.register();
	}
}