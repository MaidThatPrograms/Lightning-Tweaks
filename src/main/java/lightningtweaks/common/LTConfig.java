package lightningtweaks.common;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

import lightningtweaks.LightningTweaks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;

/**
 * TODO
 */
@EventBusSubscriber
public class LTConfig {
	private static final class Common {
		private final ConfigValue<List<String>> conductors, insulators;
		private final IntValue extraIgnitions;
		private final BooleanValue realisticLightning, spawnFire, verbose;

		/**
		 * TODO
		 *
		 * @param builder TODO
		 */
		private Common(Builder builder) {
			extraIgnitions = builder
					.comment("How many extra fires should be spawned around where the lightning strikes.",
							"The vanilla value is 4.")
					.defineInRange("Extra Ignitions", 4, 0, Integer.MAX_VALUE);
			realisticLightning = builder.comment(
					"Should lightning strike high or metal blocks more often? This is the main behavior of this mod.")
					.define("Realistic Lightning", true);
			spawnFire = builder
					.comment("Should lightning spawn fire where it strikes?",
							"Only changes the behavior of lightning that would have otherwise spawned fire.",
							"This also prevents entities from getting set on fire or damaged from lightning.")
					.define("Spawn Fire", true);
			verbose = builder.comment("Should log messages for this mod be posted?").define("Verbose", true);

			builder.push("Resistivity Settings");
			conductors = builder.define("Conductors", ImmutableList.of("gold", "iron"));
			insulators = builder.define("Insulators",
					ImmutableList.of("diamond", "glass", "leather", "quartz", "rubber", "wood"));
			builder.pop();
		}
	}

	private static final Pair<Common, ForgeConfigSpec> pair = new Builder().configure(Common::new);

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean doRealisticLightning() {
		return pair.getLeft().realisticLightning.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean doSpawnFire() {
		return pair.getLeft().spawnFire.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static List<String> getConductors() {
		return pair.getLeft().conductors.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static int getExtraIgnitions() {
		return pair.getLeft().extraIgnitions.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static List<String> getInsulators() {
		return pair.getLeft().insulators.get();
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static boolean isVerbose() {
		return pair.getLeft().verbose.get();
	}

	/**
	 * TODO
	 */
	public static void register() {
		LightningTweaks.log("Registering configuration file.");
		ModLoadingContext.get().registerConfig(Type.COMMON, pair.getRight());
	}
}