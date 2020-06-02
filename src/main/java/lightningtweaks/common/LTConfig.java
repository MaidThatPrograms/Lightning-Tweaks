package lightningtweaks.common;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.item.Item;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * TODO
 */
@EventBusSubscriber
public class LTConfig {
	/**
	 * TODO
	 */
	private static final class Common {
		private final ConfigValue<List<String>> metallicKeywords;
		private final BooleanValue realisticLightning, spawnFire;

		/**
		 * TODO
		 *
		 * @param builder TODO
		 */
		public Common(Builder builder) {
			metallicKeywords = builder.comment("TODO").define("Metallic Keywords", List.of("iron", "gold"));
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
	private static final Set<Item> metallicItems = new HashSet<>();

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
	 * @param item TODO
	 * @return TODO
	 */
	public static boolean isMetallic(Item item) {
		return metallicItems.contains(item);
	}

	/**
	 * TODO
	 *
	 * @return TODO
	 */
	public static void register() {
		ModLoadingContext.get().registerConfig(Type.COMMON, commonPair.getRight());

		for (String string : commonPair.getLeft().metallicKeywords.get()) {
			String filter = string.toLowerCase();
			for (Item item : ForgeRegistries.ITEMS)
				if (LanguageMap.func_74808_a().translateKey(item.getTranslationKey()).toLowerCase().contains(filter))
					metallicItems.add(item);
		}
	}
}