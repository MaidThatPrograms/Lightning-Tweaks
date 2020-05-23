package lightningtweaks.common;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.loading.FMLPaths;

/**
 * TODO
 */
@EventBusSubscriber
public class LTConfig {
	/**
	 * TODO
	 */
	private static final class Common {
		private final DoubleValue heightWeight, metallicityWeight;
		private final BooleanValue realisticLightning, spawnFire;

		public Common(Builder builder) {
			builder.comment("Common settings").push("Common");

			heightWeight = builder.comment("TODO").defineInRange("Height Weight", .5, 0, 1);
			metallicityWeight = builder.comment("TODO").defineInRange("Metallicity Weight", .5, 0, 1);
			realisticLightning = builder.comment("TODO").define("Realistic Lightning", true);
			spawnFire = builder.comment("TODO").define("Spawn Fire", true);

			builder.pop();
		}
	}

	private static final Pair<Common, ForgeConfigSpec> commonSpecPair = new Builder().configure(Common::new);

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static double getHeightWeight() {
		return commonSpecPair.getLeft().heightWeight.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static double getMetallicityWeight() {
		return commonSpecPair.getLeft().metallicityWeight.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean getRealisticLightning() {
		return commonSpecPair.getLeft().realisticLightning.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean getSpawnFire() {
		return commonSpecPair.getLeft().spawnFire.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static void register() {
		ModLoadingContext.get().registerConfig(Type.COMMON, commonSpecPair.getRight());
		ConfigTracker.INSTANCE.loadConfigs(Type.COMMON, FMLPaths.CONFIGDIR.get());
	}
}