package lightningtweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lightningtweaks.common.LTConfig;
import lightningtweaks.common.ResistivityMap;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Entry point for Lightning Tweaks!
 */
@Mod(LightningTweaks.MODID)
@EventBusSubscriber(bus = Bus.MOD)
public class LightningTweaks {
	public static Logger logger = LogManager.getLogger();
	public static final String MODID = "lightningtweaks";

	/**
	 * TODO
	 *
	 * @param event TODO
	 */
	@SubscribeEvent
	public static void commonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		ResistivityMap.commonSetup();
	}

	/**
	 * TODO
	 *
	 * @param message TODO
	 */
	public static void log(String message) {
		if (LTConfig.isVerbose())
			logger.info('[' + MODID + "] " + message);
	}

	/**
	 * TODO
	 *
	 * @param message TODO
	 * @param world   TODO
	 */
	public static void log(String message, World world) {
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