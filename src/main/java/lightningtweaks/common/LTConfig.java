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
		private final DoubleValue heightWeight, metallicityThreshold, metallicityWeight;
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

			builder.comment(
					"Weight settings. Used to score block positions for lightning strikes. The normal range for each value is between zero and one.",
					"Ignored if realistic lightning is disabled.").push("Weights");
			heightWeight = builder.comment("How important the height of a block is to getting struck by lightning.")
					.defineInRange("Height Weight", .5, 0, Double.MAX_VALUE);
			metallicityWeight = builder
					.comment("How important the metallicity of a block is to getting struck by lightning.")
					.defineInRange("Metallicity Weight", .5, 0, Double.MAX_VALUE);
			builder.pop();
		}
	}

	private static final Common common;
	private static final ForgeConfigSpec commonSpec;
	static {
		Pair<Common, ForgeConfigSpec> pair = new Builder().configure(Common::new);
		common = pair.getLeft();
		commonSpec = pair.getRight();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static double getHeightWeight() {
		return common.heightWeight.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static double getMetallicityThreshold() {
		return common.metallicityThreshold.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static double getMetallicityWeight() {
		return common.metallicityWeight.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean getRealisticLightning() {
		return common.realisticLightning.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean getSpawnFire() {
		return common.spawnFire.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static void register() {
		ModLoadingContext.get().registerConfig(Type.COMMON, commonSpec);
	}
}