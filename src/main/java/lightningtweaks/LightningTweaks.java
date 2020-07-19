package lightningtweaks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lightningtweaks.common.Config;
import lightningtweaks.common.Resistivity;
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
	public static final String MODID = "lightningtweaks";
	public static final Logger LOGGER = LogManager.getLogger();

	public static void log(String message) {
		LOGGER.info('[' + MODID + "] " + message);
	}

	public static void log(String message, World world) {
		log(message + " in dimension " + DimensionType.getKey(world.getDimension().getType()) + '.');
	}

	@SubscribeEvent
	public static void onCommonSetup(@SuppressWarnings("unused") FMLCommonSetupEvent event) {
		Resistivity.setup();
	}

	/**
	 * The mod constructor for Lightning Tweaks. Registers the configuration file
	 * through {@link Config#register()}.
	 */
	public LightningTweaks() {
		Config.register();
	}
}