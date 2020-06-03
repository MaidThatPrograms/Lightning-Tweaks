package lightningtweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lightningtweaks.common.LTConfig;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.common.Mod;

/**
 * Entry point for Lightning Tweaks!
 */
@Mod(LightningTweaks.MODID)
public class LightningTweaks {
	public static Logger logger = LogManager.getLogger();
	public static final String MODID = "lightningtweaks";

	/**
	 * TODO
	 *
	 * @param message TODO
	 */
	public static void log(String message) {
		if (LTConfig.isVerbose())
			logger.info(message);
	}

	/**
	 * TODO
	 *
	 * @param message TODO
	 * @param world   TODO
	 */
	public static void log(String message, IWorld world) {
		log(message + " in dimension " + DimensionType.getKey(world.getDimension().getType()) + '.');
	}

	/**
	 * The mod constructor for Lightning Tweaks. Registers the configuration file
	 * through {@link LTConfig#register()}.
	 */
	public LightningTweaks() {
		LTConfig.register();
	}
}