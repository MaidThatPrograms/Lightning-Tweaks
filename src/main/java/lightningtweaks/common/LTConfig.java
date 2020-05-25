package lightningtweaks.common;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * TODO
 */
@EventBusSubscriber
public class LTConfig {
	/**
	 * TODO
	 */
	private static final class Common {
		private final DoubleValue metallicityThreshold;
		private final BooleanValue realisticLightning, spawnFire;

		/**
		 * TODO
		 *
		 * @param builder TODO
		 */
		public Common(Builder builder) {
			metallicityThreshold = builder.comment(
					"How metal a block needs to be for lightning to not spawn fire on striking it. Setting this to zero is effectively the same as disabling fire spawning.")
					.defineInRange("Metallicity Threshold", .5, 0, 1);
			realisticLightning = builder.comment(
					"Should lightning strike high or metal blocks more often? This is the main behavior of this mod.")
					.define("Realistic Lightning", true);
			spawnFire = builder
					.comment("Should lightning spawn fire where it strikes?",
							"Only changes the behavior of lightning that would have otherwise spawned fire.")
					.define("Spawn Fire", true);
		}
	}

	private static final Pair<Common, ForgeConfigSpec> commonPair = new Builder().configure(Common::new);

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static double getMetallicityThreshold() {
		return commonPair.getLeft().metallicityThreshold.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean getRealisticLightning() {
		return commonPair.getLeft().realisticLightning.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean getSpawnFire() {
		return commonPair.getLeft().spawnFire.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static void register() {
		ModLoadingContext.get().registerConfig(Type.COMMON, commonPair.getRight());
	}
}